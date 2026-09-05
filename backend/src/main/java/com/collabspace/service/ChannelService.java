package com.collabspace.service;

import com.collabspace.dto.ChannelDtos.*;
import com.collabspace.entity.*;
import com.collabspace.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChannelService {
    private final ChannelRepository channels;
    private final ChannelMemberRepository members;
    private final WorkspaceRepository workspaces;
    private final WorkspaceMemberRepository workspaceMembers;
    private final CurrentUserService current;
    private final ChannelCategoryService categories;

    public List<ChannelResponse> list(UUID workspaceId) {
        requireWorkspaceMember(workspaceId);
        return channels.findByWorkspaceIdOrderByNameAsc(workspaceId)
                .stream()
                .filter(c -> !c.isPrivateChannel() || members.existsByChannelIdAndUserId(c.getId(), current.get().getId()))
                .map(this::dto)
                .toList();
    }

    @Transactional
    public ChannelResponse create(UUID workspaceId, CreateChannelRequest request) {
        WorkspaceMember member = requireWorkspaceMember(workspaceId);
        if (member.getRole() == WorkspaceRole.MEMBER) {
            throw new SecurityException("Admin permission required");
        }
        Workspace workspace = workspaces.findById(workspaceId).orElseThrow();
        if (channels.findByWorkspaceIdAndNameIgnoreCase(workspaceId, request.name()).isPresent()) {
            throw new IllegalArgumentException("Channel name already exists");
        }

        ChannelCategory category = request.categoryId() == null ? null : categories.getForWorkspace(workspaceId, request.categoryId());
        Channel channel = channels.save(Channel.builder()
                .workspace(workspace)
                .category(category)
                .name(request.name().trim())
                .description(request.description())
                .privateChannel(request.privateChannel())
                .createdBy(current.get())
                .build());

        if (!channel.isPrivateChannel()) {
            workspaceMembers.findByWorkspaceId(workspaceId).forEach(m -> addMember(channel, m.getUser()));
        } else {
            addMember(channel, current.get());
        }
        return dto(channel);
    }

    @Transactional
    public ChannelResponse update(UUID id, UpdateChannelRequest request) {
        Channel channel = getMemberChannel(id);
        WorkspaceMember member = workspaceMembers.findByWorkspaceIdAndUserId(channel.getWorkspace().getId(), current.get().getId())
                .orElseThrow();
        if (member.getRole() == WorkspaceRole.MEMBER) {
            throw new SecurityException("Admin permission required");
        }

        channel.setName(request.name().trim());
        channel.setDescription(request.description());
        if (request.categoryId() == null) {
            channel.setCategory(null);
        } else {
            channel.setCategory(categories.getForWorkspace(channel.getWorkspace().getId(), request.categoryId()));
        }
        return dto(channel);
    }

    @Transactional
    public void delete(UUID id) {
        Channel channel = getMemberChannel(id);
        WorkspaceMember member = workspaceMembers.findByWorkspaceIdAndUserId(channel.getWorkspace().getId(), current.get().getId())
                .orElseThrow();
        if (member.getRole() == WorkspaceRole.MEMBER) {
            throw new SecurityException("Admin permission required");
        }
        channels.delete(channel);
    }

    @Transactional
    public void join(UUID id) {
        Channel channel = channels.findById(id).orElseThrow(() -> new NoSuchElementException("Channel not found"));
        requireWorkspaceMember(channel.getWorkspace().getId());
        if (channel.isPrivateChannel()) {
            throw new SecurityException("Private channel requires an invitation");
        }
        if (!members.existsByChannelIdAndUserId(id, current.get().getId())) {
            addMember(channel, current.get());
        }
    }

    @Transactional
    public void leave(UUID id) {
        Channel channel = getMemberChannel(id);
        if (members.findByChannelId(id).size() <= 1) {
            throw new IllegalArgumentException("A channel must keep at least one member");
        }
        members.deleteByChannelIdAndUserId(id, current.get().getId());
    }

    public Channel getMemberChannel(UUID id) {
        Channel channel = channels.findById(id).orElseThrow(() -> new NoSuchElementException("Channel not found"));
        if (!members.existsByChannelIdAndUserId(id, current.get().getId())) {
            throw new SecurityException("You are not a channel member");
        }
        return channel;
    }

    private WorkspaceMember requireWorkspaceMember(UUID workspaceId) {
        return workspaceMembers.findByWorkspaceIdAndUserId(workspaceId, current.get().getId())
                .orElseThrow(() -> new SecurityException("You are not a workspace member"));
    }

    private void addMember(Channel channel, User user) {
        if (!members.existsByChannelIdAndUserId(channel.getId(), user.getId())) {
            members.save(ChannelMember.builder().channel(channel).user(user).build());
        }
    }

    private ChannelResponse dto(Channel channel) {
        return new ChannelResponse(
                channel.getId(),
                channel.getWorkspace().getId(),
                channel.getCategory() == null ? null : channel.getCategory().getId(),
                channel.getCategory() == null ? null : channel.getCategory().getName(),
                channel.getName(),
                channel.getDescription(),
                channel.isPrivateChannel(),
                channel.getCreatedBy().getId(),
                channel.getCreatedAt()
        );
    }
}
