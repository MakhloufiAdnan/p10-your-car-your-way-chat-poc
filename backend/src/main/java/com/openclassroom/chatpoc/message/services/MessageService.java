package com.openclassroom.chatpoc.message.services;

import com.openclassroom.chatpoc.conversation.repositories.ConversationParticipantRepository;
import com.openclassroom.chatpoc.message.dtos.MessageResponse;
import com.openclassroom.chatpoc.message.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationParticipantRepository conversationParticipantRepository;

    public List<MessageResponse> getConversationMessages(UUID conversationId, String username) {
        boolean hasAccess = conversationParticipantRepository.existsByConversationIdAndUsername(
                conversationId,
                username
        );

        if (!hasAccess) {
            throw new ResponseStatusException(
                    FORBIDDEN,
                    "Accès refusé à cette conversation."
            );
        }

        return messageRepository.findHistoryByConversationId(conversationId)
                .stream()
                .map(message -> MessageResponse.builder()
                        .messageId(message.getId())
                        .conversationId(message.getConversation().getId())
                        .senderUsername(message.getSender().getUsername())
                        .content(message.getContent())
                        .sentAt(message.getSentAt())
                        .build())
                .toList();
    }
}
