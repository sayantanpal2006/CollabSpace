package com.collabspace.service;

import com.collabspace.dto.AiDtos.ModerationResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModerationServiceTest {
    private final ModerationService moderationService = new ModerationService();

    @Test
    void flagsToxicLanguage() {
        ModerationResult result = moderationService.analyze("You are an idiot");
        assertTrue(result.flagged());
        assertEquals("high", result.severity());
    }

    @Test
    void ignoresCleanMessage() {
        ModerationResult result = moderationService.analyze("Let's sync after standup");
        assertFalse(result.flagged());
        assertEquals("none", result.severity());
    }
}
