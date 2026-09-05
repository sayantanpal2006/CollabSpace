package com.collabspace.service.ai;

public interface LlmProvider {
    boolean isAvailable();

    String complete(String systemPrompt, String userPrompt);
}
