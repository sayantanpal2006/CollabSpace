package com.collabspace.service;

import com.collabspace.dto.AiDtos.ChannelAiResponse;
import com.collabspace.entity.Message;
import com.collabspace.repository.MessageRepository;
import com.collabspace.service.ai.AiGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiAssistantService {
    private final ChannelService channels;
    private final MessageRepository messages;
    private final AiGateway aiGateway;

    public ChannelAiResponse summarizeRecent(UUID channelId) {
        List<Message> recent = getRecentMessages(channelId, 20);
        String output = aiGateway.isFallbackActive()
                ? fallbackSummary(recent)
                : aiGateway.complete(
                "You are an assistant for a Discord-like collaboration app. Summarize concise and actionable.",
                "Summarize these channel messages:\n" + toTranscript(recent)
        );
        return new ChannelAiResponse(output, aiGateway.isFallbackActive());
    }

    public ChannelAiResponse draftReply(UUID channelId, UUID messageId, String tone) {
        List<Message> recent = getRecentMessages(channelId, 15);
        Message target = messages.findById(messageId).orElseThrow(() -> new NoSuchElementException("Message not found"));
        if (!target.getChannel().getId().equals(channelId)) {
            throw new IllegalArgumentException("Message is not from this channel");
        }

        String output = aiGateway.isFallbackActive()
                ? "Suggested " + tone + " reply: Thanks for the update — I'll follow up on this thread shortly."
                : aiGateway.complete(
                "You draft helpful Discord-style replies.",
                "Thread context:\n" + toTranscript(recent) + "\n\nTarget message:\n" + target.getContent() + "\n\nTone:" + tone
        );
        return new ChannelAiResponse(output, aiGateway.isFallbackActive());
    }

    public ChannelAiResponse extractActionItems(UUID channelId) {
        List<Message> recent = getRecentMessages(channelId, 30);
        String output = aiGateway.isFallbackActive()
                ? fallbackActionItems(recent)
                : aiGateway.complete(
                "Extract action items from collaboration chat. Return a concise bullet list.",
                "Extract action items from these messages:\n" + toTranscript(recent)
        );
        return new ChannelAiResponse(output, aiGateway.isFallbackActive());
    }

    private List<Message> getRecentMessages(UUID channelId, int limit) {
        channels.getMemberChannel(channelId);
        List<Message> content = messages
                .findByChannelIdAndDeletedFalseOrderByCreatedAtDesc(channelId, PageRequest.of(0, limit))
                .getContent();
        Collections.reverse(content);
        return content;
    }

    private String toTranscript(List<Message> recent) {
        return recent.stream()
                .map(m -> m.getSender().getUsername() + ": " + m.getContent())
                .collect(Collectors.joining("\n"));
    }

    private String fallbackSummary(List<Message> recent) {
        if (recent.isEmpty()) {
            return "No recent messages to summarize.";
        }
        List<String> snippets = recent.stream()
                .skip(Math.max(0, recent.size() - 5L))
                .map(Message::getContent)
                .map(s -> s.length() > 90 ? s.substring(0, 90) + "..." : s)
                .toList();
        return "Recent highlights:\n- " + String.join("\n- ", snippets);
    }

    private String fallbackActionItems(List<Message> recent) {
        List<String> items = recent.stream()
                .map(Message::getContent)
                .filter(c -> {
                    String lower = c.toLowerCase(Locale.ROOT);
                    return lower.contains("todo") || lower.contains("action") || lower.contains("please")
                            || lower.contains("need to") || lower.contains("follow up");
                })
                .map(c -> c.length() > 120 ? c.substring(0, 120) + "..." : c)
                .distinct()
                .limit(6)
                .toList();
        if (items.isEmpty()) {
            return "No explicit action items found. Consider assigning owners in your next messages.";
        }
        return "Action items:\n- " + String.join("\n- ", items);
    }
}
