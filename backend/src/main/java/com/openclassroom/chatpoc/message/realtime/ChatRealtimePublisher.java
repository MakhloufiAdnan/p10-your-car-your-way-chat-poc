package com.openclassroom.chatpoc.message.realtime;

import com.openclassroom.chatpoc.conversation.repositories.ConversationParticipantRepository;
import com.openclassroom.chatpoc.message.dtos.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChatRealtimePublisher {

    private final SimpMessagingTemplate messagingTemplate;
    private final ConversationParticipantRepository conversationParticipantRepository;

    public void publishMessageCreated(MessageResponse messageResponse) {
        UUID conversationId = messageResponse.getConversationId();

        List<String> participantUsernames =
                conversationParticipantRepository.findParticipantUsernamesByConversationId(conversationId);

        for (String username : participantUsernames) {
            messagingTemplate.convertAndSendToUser(
                    username,
                    "/queue/conversations/" + conversationId,
                    messageResponse
            );
        }
    }
}
