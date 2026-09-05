package com.collabspace.service;

import com.collabspace.dto.WorkspaceDtos.AcceptInviteRequest;
import com.collabspace.dto.WorkspaceDtos.CreateInviteRequest;
import com.collabspace.dto.WorkspaceDtos.WorkspaceInviteResponse;
import com.collabspace.entity.*;
import com.collabspace.repository.NotificationRepository;
import com.collabspace.repository.UserRepository;
import com.collabspace.repository.WorkspaceInviteRepository;
import com.collabspace.repository.WorkspaceMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkspaceInviteService {
    private final WorkspaceInviteRepository invites;
    private final WorkspaceMemberRepository members;
    private final WorkspaceService workspaces;
    private final CurrentUserService currentUserService;
    private final UserRepository users;
    private final NotificationRepository notifications;

    @Transactional
    public WorkspaceInviteResponse create(UUID workspaceId, CreateInviteRequest request) {
        workspaces.requireAdmin(workspaceId);
        Workspace workspace = workspaces.getEntity(workspaceId);

        WorkspaceInvite invite = new WorkspaceInvite();
        invite.setWorkspace(workspace);
        invite.setInvitedBy(currentUserService.get());
        invite.setEmail(request.email().trim().toLowerCase());
        invite.setToken(UUID.randomUUID() + "-" + UUID.randomUUID());
        invite.setExpiresAt(Instant.now().plusSeconds(3600L * (request.expiresInHours() == null ? 72 : request.expiresInHours())));

        WorkspaceInvite saved = invites.save(invite);
        users.findByEmail(saved.getEmail()).ifPresent(target -> notifications.save(Notification.builder()
                .user(target)
                .type(NotificationType.WORKSPACE_INVITE)
                .message("You were invited to join workspace " + workspace.getName())
                .build()));
        return toDto(saved);
    }

    public List<WorkspaceInviteResponse> list(UUID workspaceId) {
        workspaces.requireAdmin(workspaceId);
        expirePendingInvites();
        return invites.findByWorkspaceIdOrderByCreatedAtDesc(workspaceId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public void revoke(UUID workspaceId, UUID inviteId) {
        workspaces.requireAdmin(workspaceId);
        WorkspaceInvite invite = invites.findById(inviteId).orElseThrow(() -> new NoSuchElementException("Invite not found"));
        if (!invite.getWorkspace().getId().equals(workspaceId)) {
            throw new SecurityException("Invite does not belong to this workspace");
        }
        invite.setStatus(InviteStatus.REVOKED);
    }

    @Transactional
    public WorkspaceInviteResponse accept(AcceptInviteRequest request) {
        WorkspaceInvite invite = invites.findByToken(request.token())
                .orElseThrow(() -> new NoSuchElementException("Invite not found"));

        if (invite.getStatus() != InviteStatus.PENDING) {
            throw new IllegalArgumentException("Invite is no longer valid");
        }
        if (invite.getExpiresAt().isBefore(Instant.now())) {
            invite.setStatus(InviteStatus.EXPIRED);
            throw new IllegalArgumentException("Invite has expired");
        }

        User current = currentUserService.get();
        if (!current.getEmail().equalsIgnoreCase(invite.getEmail())) {
            throw new SecurityException("Invite email does not match your account");
        }
        if (!members.existsByWorkspaceIdAndUserId(invite.getWorkspace().getId(), current.getId())) {
            WorkspaceMember member = new WorkspaceMember();
            member.setWorkspace(invite.getWorkspace());
            member.setUser(current);
            member.setRole(WorkspaceRole.MEMBER);
            members.save(member);
        }
        invite.setStatus(InviteStatus.ACCEPTED);
        invite.setAcceptedAt(Instant.now());
        return toDto(invite);
    }

    @Transactional
    public void expirePendingInvites() {
        Instant now = Instant.now();
        invites.findByStatusAndExpiresAtBefore(InviteStatus.PENDING, now)
                .forEach(invite -> invite.setStatus(InviteStatus.EXPIRED));
    }

    private WorkspaceInviteResponse toDto(WorkspaceInvite invite) {
        return new WorkspaceInviteResponse(
                invite.getId(),
                invite.getWorkspace().getId(),
                invite.getWorkspace().getName(),
                invite.getEmail(),
                invite.getToken(),
                invite.getStatus().name(),
                invite.getExpiresAt(),
                invite.getCreatedAt()
        );
    }
}
