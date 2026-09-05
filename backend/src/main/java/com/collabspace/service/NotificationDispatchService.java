package com.collabspace.service;

import com.collabspace.entity.*;
import com.collabspace.repository.NotificationRepository;
import com.collabspace.repository.WorkspaceMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class NotificationDispatchService {
    private static final Pattern MENTION_PATTERN = Pattern.compile("@([A-Za-z0-9_\\-\\.]{3,40})");

    private final NotificationRepository notifications;
    private final WorkspaceMemberRepository workspaceMembers;

    public void notifyMentionsAndReplies(Message message) {
        UUID senderId = message.getSender().getId();
        Set<UUID> notified = new HashSet<>();

        if (message.getReplyTo() != null) {
            User target = message.getReplyTo().getSender();
            if (!target.getId().equals(senderId)) {
                notifications.save(Notification.builder()
                        .user(target)
                        .type(NotificationType.CHANNEL_ACTIVITY)
                        .message(message.getSender().getUsername() + " replied to your message in #" + message.getChannel().getName())
                        .build());
                notified.add(target.getId());
            }
        }

        Map<String, User> membersByName = new HashMap<>();
        for (WorkspaceMember member : workspaceMembers.findByWorkspaceId(message.getChannel().getWorkspace().getId())) {
            membersByName.put(member.getUser().getUsername().toLowerCase(Locale.ROOT), member.getUser());
        }

        Matcher matcher = MENTION_PATTERN.matcher(message.getContent());
        while (matcher.find()) {
            String username = matcher.group(1).toLowerCase(Locale.ROOT);
            User mentioned = membersByName.get(username);
            if (mentioned == null || mentioned.getId().equals(senderId) || notified.contains(mentioned.getId())) {
                continue;
            }
            notifications.save(Notification.builder()
                    .user(mentioned)
                    .type(NotificationType.MENTION)
                    .message(message.getSender().getUsername() + " mentioned you in #" + message.getChannel().getName())
                    .build());
            notified.add(mentioned.getId());
        }
    }

    public void notifyModerators(Message message, String warningText) {
        UUID workspaceId = message.getChannel().getWorkspace().getId();
        UUID senderId = message.getSender().getId();
        for (WorkspaceMember member : workspaceMembers.findByWorkspaceId(workspaceId)) {
            if (member.getUser().getId().equals(senderId)) {
                continue;
            }
            if (member.getRole() == WorkspaceRole.OWNER || member.getRole() == WorkspaceRole.ADMIN) {
                notifications.save(Notification.builder()
                        .user(member.getUser())
                        .type(NotificationType.CHANNEL_ACTIVITY)
                        .message("Moderation alert in #" + message.getChannel().getName() + ": " + warningText)
                        .build());
            }
        }
    }
}
