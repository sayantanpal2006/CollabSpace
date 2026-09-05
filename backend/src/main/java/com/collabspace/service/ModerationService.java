package com.collabspace.service;

import com.collabspace.dto.AiDtos.ModerationResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class ModerationService {
    private static final List<String> TOXIC_TERMS = List.of("idiot", "stupid", "hate", "kill", "trash", "moron");

    public ModerationResult analyze(String content) {
        if (content == null || content.isBlank()) {
            return new ModerationResult(false, "none", "clean");
        }
        String normalized = content.toLowerCase(Locale.ROOT);

        boolean toxic = TOXIC_TERMS.stream().anyMatch(normalized::contains);
        boolean repeatedSpam = normalized.matches(".*(.)\\1{6,}.*");
        boolean promoSpam = normalized.contains("http://") || normalized.contains("https://") || normalized.contains("free nitro");

        if (toxic) {
            return new ModerationResult(true, "high", "Potentially toxic language");
        }
        if (repeatedSpam || promoSpam) {
            return new ModerationResult(true, "medium", "Potential spam-like content");
        }
        return new ModerationResult(false, "none", "clean");
    }
}
