package com.collabspace.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public final class ChannelDtos {
    private ChannelDtos() {
    }

    public record CreateChannelRequest(
            @NotBlank @Size(max = 60) String name,
            @Size(max = 300) String description,
            boolean privateChannel,
            UUID categoryId
    ) {
    }

    public record UpdateChannelRequest(
            @NotBlank @Size(max = 60) String name,
            @Size(max = 300) String description,
            UUID categoryId
    ) {
    }

    public record ChannelResponse(
            UUID id,
            UUID workspaceId,
            UUID categoryId,
            String categoryName,
            String name,
            String description,
            boolean privateChannel,
            UUID createdBy,
            Instant createdAt
    ) {
    }

    public record CreateCategoryRequest(
            @NotBlank @Size(max = 80) String name,
            @Min(0) @Max(999) int position
    ) {
    }

    public record UpdateCategoryRequest(
            @NotBlank @Size(max = 80) String name,
            @Min(0) @Max(999) int position
    ) {
    }

    public record CategoryResponse(
            UUID id,
            UUID workspaceId,
            String name,
            int position,
            Instant createdAt
    ) {
    }
}
