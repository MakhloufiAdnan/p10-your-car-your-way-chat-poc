package com.openclassroom.chatpoc.message.realtime;

import com.openclassroom.chatpoc.conversation.repositories.ConversationParticipantRepository;
import com.openclassroom.chatpoc.message.dtos.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
* Publie les nouveaux messages vers les participants de la conversation.
*
* La persistance reste la source de vérité : le message est d'abord enregistré
* en base, puis diffusé en temps réel pour mettre à jour l'interface sans refresh.
*/
@Component
@RequiredArgsConstructor
public class ChatRealtimePublisher {

    private final SimpMessagingTemplate messagingTemplate;
    private final ConversationParticipantRepository conversationParticipantRepository;

    /**
    * Envoie le message à chaque participant via une destination utilisateur.
    *
    * Publie explicitement pour chaque username afin de s'appuyer sur
    * `convertAndSendToUser(...)` et sur la résolution Spring des destinations `/user/queue/...`.
    */
    public void publishMessageCreated(MessageResponse messageResponse) {
        UUID conversationId = messageResponse.getConversationId();

        List<String> participantUsernames =
                conversationParticipantRepository.findParticipantUsernamesByConversationId(conversationId);

        // Chaque participant reçoit le même MessageResponse, mais sur sa propre file utilisateur.
        for (String username : participantUsernames) {
            messagingTemplate.convertAndSendToUser(
                    username,
                    "/queue/conversations/" + conversationId,
                    messageResponse
            );
        }
    }
}
