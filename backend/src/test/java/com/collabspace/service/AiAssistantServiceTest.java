package com.collabspace.service;

import com.collabspace.dto.AiDtos.ChannelAiResponse;
import com.collabspace.entity.Channel;
import com.collabspace.entity.Message;
import com.collabspace.entity.User;
import com.collabspace.entity.Workspace;
import com.collabspace.repository.MessageRepository;
import com.collabspace.service.ai.AiGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiAssistantServiceTest {
    @Mock
    private ChannelService channelService;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private AiGateway aiGateway;

    @InjectMocks
    private AiAssistantService service;

    @Test
    void summarizeUsesFallbackWhenAiKeyMissing() {
        UUID channelId = UUID.randomUUID();
        when(aiGateway.isFallbackActive()).thenReturn(true);
        when(messageRepository.findByChannelIdAndDeletedFalseOrderByCreatedAtDesc(eq(channelId), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(message("alice", "Todo: prepare release notes"), message("bob", "Please review the PR"))));

        ChannelAiResponse response = service.summarizeRecent(channelId);

        verify(channelService).getMemberChannel(channelId);
        assertTrue(response.fallbackUsed());
        assertTrue(response.output().contains("Recent highlights"));
    }

    private Message message(String username, String content) {
        User user = new User();
        user.setUsername(username);

        Workspace workspace = new Workspace();
        workspace.setId(UUID.randomUUID());

        Channel channel = new Channel();
        channel.setId(UUID.randomUUID());
        channel.setWorkspace(workspace);

        Message message = new Message();
        message.setChannel(channel);
        message.setSender(user);
        message.setContent(content);
        return message;
    }
}
