package com.collabspace.controller;

import com.collabspace.dto.AiDtos.ChannelAiResponse;
import com.collabspace.dto.AiDtos.DraftReplyRequest;
import com.collabspace.service.AiAssistantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai/channels/{channelId}")
public class AiController {
    private final AiAssistantService service;

    @GetMapping("/summarize")
    public ChannelAiResponse summarize(@PathVariable UUID channelId) {
        return service.summarizeRecent(channelId);
    }

    @PostMapping("/draft-reply")
    public ChannelAiResponse draftReply(@PathVariable UUID channelId, @Valid @RequestBody DraftReplyRequest request) {
        return service.draftReply(channelId, request.messageId(), request.tone());
    }

    @GetMapping("/action-items")
    public ChannelAiResponse actionItems(@PathVariable UUID channelId) {
        return service.extractActionItems(channelId);
    }
}
