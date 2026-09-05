package com.collabspace.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public final class WorkspaceDtos {
    private WorkspaceDtos() {
    }

    public record CreateWorkspaceRequest(
            @NotBlank @Size(max = 80) String name,
            @Size(max = 500) String description
    ) {
    }

    public record UpdateWorkspaceRequest(
            @NotBlank @Size(max = 80) String name,
            @Size(max = 500) String description
    ) {
    }

    public record AddMemberRequest(@NotNull UUID userId) {
    }

    public record CreateInviteRequest(
            @NotBlank @Email String email,
            @Min(1) @Max(168) Integer expiresInHours
    ) {
    }

    public record AcceptInviteRequest(@NotBlank String token) {
    }

    public record WorkspaceInviteResponse(
            UUID id,
            UUID workspaceId,
            String workspaceName,
            String email,
            String token,
            String status,
            Instant expiresAt,
            Instant createdAt
    ) {
    }

    public record WorkspaceResponse(
            UUID id,
            String name,
            String description,
            UUID ownerId,
            String ownerUsername,
            Instant createdAt
    ) {
    }
}
