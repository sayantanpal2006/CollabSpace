package com.collabspace.service.ai;

import org.springframework.stereotype.Component;

@Component
public class FallbackLlmProvider implements LlmProvider {
    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String complete(String systemPrompt, String userPrompt) {
        String compact = userPrompt.replaceAll("\\s+", " ").trim();
        if (compact.length() > 280) {
            compact = compact.substring(0, 280) + "...";
        }
        return "AI fallback mode: " + compact;
    }
}
