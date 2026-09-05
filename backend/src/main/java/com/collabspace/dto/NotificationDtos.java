package com.collabspace.dto;

import java.time.Instant;
import java.util.UUID;

public final class NotificationDtos {
    private NotificationDtos() {
    }

    public record NotificationResponse(UUID id, String type, String message, boolean read, Instant createdAt) {
    }
}
