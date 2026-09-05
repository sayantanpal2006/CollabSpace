package com.collabspace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public final class MessageDtos {
    private MessageDtos() {
    }

    public record SendMessageRequest(@NotBlank @Size(max = 4000) String content, UUID replyToId) {
    }

    public record MessageResponse(
            UUID id,
            UUID channelId,
            UUID senderId,
            String senderUsername,
            String content,
            UUID replyToId,
            Instant createdAt,
            boolean edited,
            boolean deleted,
            String readStatus,
            String moderationWarning
    ) {
    }

    public record SendDirectMessageRequest(@NotBlank @Size(max = 4000) String content) {
    }

    public record DirectMessageResponse(
            UUID id,
            UUID conversationId,
            UUID senderId,
            String senderUsername,
            String content,
            Instant createdAt,
            boolean edited,
            boolean deleted,
            String readStatus
    ) {
    }

    public record ReactionRequest(@NotBlank @Size(max = 16) String emoji) {
    }
}
