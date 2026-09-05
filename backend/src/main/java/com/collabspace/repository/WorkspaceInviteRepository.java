package com.collabspace.repository;

import com.collabspace.entity.InviteStatus;
import com.collabspace.entity.WorkspaceInvite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkspaceInviteRepository extends JpaRepository<WorkspaceInvite, UUID> {
    Optional<WorkspaceInvite> findByToken(String token);

    List<WorkspaceInvite> findByWorkspaceIdOrderByCreatedAtDesc(UUID workspaceId);

    List<WorkspaceInvite> findByStatusAndExpiresAtBefore(InviteStatus status, Instant expiresAt);
}
