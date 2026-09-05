package com.collabspace.service;

import com.collabspace.dto.AiDtos.ModerationResult;
import com.collabspace.dto.MessageDtos.MessageResponse;
import com.collabspace.dto.MessageDtos.SendMessageRequest;
import com.collabspace.entity.Channel;
import com.collabspace.entity.Message;
import com.collabspace.entity.MessageReaction;
import com.collabspace.repository.MessageReactionRepository;
import com.collabspace.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messages;
    private final ChannelService channels;
    private final MessageReactionRepository reactions;
    private final CurrentUserService current;
    private final NotificationDispatchService notifications;
    private final ModerationService moderation;

    public Page<MessageResponse> history(UUID channelId, int page, int size) {
        channels.getMemberChannel(channelId);
        return messages.findByChannelIdAndDeletedFalseOrderByCreatedAtDesc(
                        channelId,
                        PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100))
                )
                .map(this::dto);
    }

    @Transactional
    public MessageResponse send(UUID channelId, SendMessageRequest request) {
        Channel channel = channels.getMemberChannel(channelId);
        Message replyTarget = null;

        if (request.replyToId() != null) {
            replyTarget = messages.findById(request.replyToId())
                    .orElseThrow(() -> new NoSuchElementException("Reply target not found"));
            if (!replyTarget.getChannel().getId().equals(channelId)) {
                throw new IllegalArgumentException("Reply target is from another channel");
            }
        }

        Message saved = messages.save(Message.builder()
                .channel(channel)
                .sender(current.get())
                .content(request.content().trim())
                .replyTo(replyTarget)
                .build());

        notifications.notifyMentionsAndReplies(saved);
        ModerationResult moderationResult = moderation.analyze(saved.getContent());
        if (moderationResult.flagged()) {
            notifications.notifyModerators(saved, moderationResult.reason());
        }
        return dto(saved, moderationResult);
    }

    @Transactional
    public MessageResponse edit(UUID id, String content) {
        Message message = messages.findById(id).orElseThrow(() -> new NoSuchElementException("Message not found"));
        channels.getMemberChannel(message.getChannel().getId());
        if (!message.getSender().getId().equals(current.get().getId())) {
            throw new SecurityException("You can only edit your own messages");
        }
        if (content == null || content.isBlank() || content.length() > 4000) {
            throw new IllegalArgumentException("Invalid message content");
        }

        message.setContent(content.trim());
        message.setEdited(true);
        ModerationResult moderationResult = moderation.analyze(message.getContent());
        return dto(message, moderationResult);
    }

    @Transactional
    public void delete(UUID id) {
        Message message = messages.findById(id).orElseThrow(() -> new NoSuchElementException("Message not found"));
        Channel channel = channels.getMemberChannel(message.getChannel().getId());
        if (!message.getSender().getId().equals(current.get().getId())) {
            throw new SecurityException("You can only delete your own messages");
        }
        message.setDeleted(true);
        message.setContent("This message was deleted");
    }

    @Transactional
    public void react(UUID id, String emoji) {
        Message message = messages.findById(id).orElseThrow(() -> new NoSuchElementException("Message not found"));
        channels.getMemberChannel(message.getChannel().getId());

        var existing = reactions.findByMessageIdAndUserIdAndEmoji(id, current.get().getId(), emoji);
        if (existing.isPresent()) {
            reactions.delete(existing.get());
            return;
        }

        reactions.save(MessageReaction.builder()
                .message(message)
                .user(current.get())
                .emoji(emoji)
                .build());
    }

    public Page<MessageResponse> search(UUID workspaceId, String q, int page, int size) {
        return messages.findByChannel_Workspace_IdAndContentContainingIgnoreCaseAndDeletedFalse(
                        workspaceId,
                        q,
                        PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100))
                )
                .map(this::dto);
    }

    private MessageResponse dto(Message message) {
        return dto(message, moderation.analyze(message.getContent()));
    }

    private MessageResponse dto(Message message, ModerationResult moderationResult) {
        return new MessageResponse(
                message.getId(),
                message.getChannel().getId(),
                message.getSender().getId(),
                message.getSender().getUsername(),
                message.isDeleted() ? "This message was deleted" : message.getContent(),
                message.getReplyTo() == null ? null : message.getReplyTo().getId(),
                message.getCreatedAt(),
                message.isEdited(),
                message.isDeleted(),
                message.getReadStatus().name(),
                moderationResult.flagged() ? moderationResult.reason() : null
        );
    }
}
