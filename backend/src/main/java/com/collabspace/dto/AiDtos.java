package com.collabspace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public final class AiDtos {
    private AiDtos() {
    }

    public record ChannelAiResponse(String output, boolean fallbackUsed) {
    }

    public record DraftReplyRequest(
            @NotNull UUID messageId,
            @NotBlank @Size(max = 300) String tone
    ) {
    }

    public record ModerationResult(boolean flagged, String severity, String reason) {
    }
}
