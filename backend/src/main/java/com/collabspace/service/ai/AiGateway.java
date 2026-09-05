package com.collabspace.service.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AiGateway {
    private final OpenAiLlmProvider openAi;
    private final FallbackLlmProvider fallback;

    public String complete(String systemPrompt, String userPrompt) {
        if (openAi.isAvailable()) {
            try {
                return openAi.complete(systemPrompt, userPrompt);
            } catch (Exception ignored) {
                return fallback.complete(systemPrompt, userPrompt);
            }
        }
        return fallback.complete(systemPrompt, userPrompt);
    }

    public boolean isFallbackActive() {
        return !openAi.isAvailable();
    }
}
