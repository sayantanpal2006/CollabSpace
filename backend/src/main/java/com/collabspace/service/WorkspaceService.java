package com.collabspace.service;

import com.collabspace.dto.WorkspaceDtos.*;
import com.collabspace.entity.*;
import com.collabspace.repository.UserRepository;
import com.collabspace.repository.WorkspaceMemberRepository;
import com.collabspace.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkspaceService {
    private final WorkspaceRepository workspaces;
    private final WorkspaceMemberRepository members;
    private final UserRepository users;
    private final CurrentUserService current;

    public List<WorkspaceResponse> list() {
        User user = current.get();
        return members.findByUserId(user.getId()).stream().map(m -> toDto(m.getWorkspace())).toList();
    }

    @Transactional
    public WorkspaceResponse create(CreateWorkspaceRequest request) {
        User user = current.get();
        Workspace workspace = workspaces.save(Workspace.builder()
                .name(request.name().trim())
                .description(request.description())
                .owner(user)
                .build());
        members.save(WorkspaceMember.builder()
                .workspace(workspace)
                .user(user)
                .role(WorkspaceRole.OWNER)
                .build());
        return toDto(workspace);
    }

    public WorkspaceResponse get(UUID id) {
        Workspace workspace = getEntity(id);
        requireMember(id);
        return toDto(workspace);
    }

    public Workspace getEntity(UUID id) {
        return workspaces.findById(id).orElseThrow(() -> new NoSuchElementException("Workspace not found"));
    }

    @Transactional
    public WorkspaceResponse update(UUID id, UpdateWorkspaceRequest request) {
        Workspace workspace = getEntity(id);
        requireAdmin(id);
        workspace.setName(request.name().trim());
        workspace.setDescription(request.description());
        return toDto(workspace);
    }

    @Transactional
    public void delete(UUID id) {
        Workspace workspace = getEntity(id);
        WorkspaceMember member = member(id);
        if (member.getRole() != WorkspaceRole.OWNER) {
            throw new SecurityException("Only the owner can delete a workspace");
        }
        workspaces.delete(workspace);
    }

    @Transactional
    public void addMember(UUID id, AddMemberRequest request) {
        requireAdmin(id);
        Workspace workspace = getEntity(id);
        User user = users.findById(request.userId()).orElseThrow(() -> new NoSuchElementException("User not found"));
        if (members.existsByWorkspaceIdAndUserId(id, user.getId())) {
            throw new IllegalArgumentException("User is already a member");
        }
        members.save(WorkspaceMember.builder().workspace(workspace).user(user).role(WorkspaceRole.MEMBER).build());
    }

    @Transactional
    public void removeMember(UUID id, UUID userId) {
        requireAdmin(id);
        if (current.get().getId().equals(userId)) {
            throw new IllegalArgumentException("You cannot remove yourself");
        }
        members.deleteByWorkspaceIdAndUserId(id, userId);
    }

    public WorkspaceMember member(UUID id) {
        return members.findByWorkspaceIdAndUserId(id, current.get().getId())
                .orElseThrow(() -> new SecurityException("You are not a workspace member"));
    }

    public void requireMember(UUID id) {
        member(id);
    }

    public void requireAdmin(UUID id) {
        WorkspaceRole role = member(id).getRole();
        if (role == WorkspaceRole.MEMBER) {
            throw new SecurityException("Admin permission required");
        }
    }

    public WorkspaceResponse toDto(Workspace workspace) {
        return new WorkspaceResponse(
                workspace.getId(),
                workspace.getName(),
                workspace.getDescription(),
                workspace.getOwner().getId(),
                workspace.getOwner().getUsername(),
                workspace.getCreatedAt()
        );
    }
}
