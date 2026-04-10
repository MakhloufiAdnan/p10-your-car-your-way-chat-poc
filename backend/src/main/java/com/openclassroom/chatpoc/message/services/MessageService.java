package com.openclassroom.chatpoc.message.services;

import com.openclassroom.chatpoc.conversation.entities.Conversation;
import com.openclassroom.chatpoc.conversation.repositories.ConversationParticipantRepository;
import com.openclassroom.chatpoc.conversation.repositories.ConversationRepository;
import com.openclassroom.chatpoc.message.dtos.MessageResponse;
import com.openclassroom.chatpoc.message.dtos.SendMessageRequest;
import com.openclassroom.chatpoc.message.entities.Message;
import com.openclassroom.chatpoc.message.realtime.ChatRealtimePublisher;
import com.openclassroom.chatpoc.message.repositories.MessageRepository;
import com.openclassroom.chatpoc.user.entities.User;
import com.openclassroom.chatpoc.user.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationParticipantRepository conversationParticipantRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final ChatRealtimePublisher chatRealtimePublisher;

    public List<MessageResponse> getConversationMessages(UUID conversationId, String username) {
        ensureUserHasAccessToConversation(conversationId, username);

        return messageRepository.findHistoryByConversationId(conversationId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public MessageResponse sendMessage(UUID conversationId, String username, SendMessageRequest request) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND,
                        "Conversation introuvable."
                ));

        ensureUserHasAccessToConversation(conversationId, username);

        User sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND,
                        "Utilisateur introuvable."
                ));

        Message message = Message.builder()
                .conversation(conversation)
                .sender(sender)
                .content(request.getContent().trim())
                .build();

        Message savedMessage = messageRepository.save(message);
        MessageResponse response = toResponse(savedMessage);

        chatRealtimePublisher.publishMessageCreated(response);

        return response;
    }

    private void ensureUserHasAccessToConversation(UUID conversationId, String username) {
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
    }

    private MessageResponse toResponse(Message message) {
        return MessageResponse.builder()
                .messageId(message.getId())
                .conversationId(message.getConversation().getId())
                .senderUsername(message.getSender().getUsername())
                .content(message.getContent())
                .sentAt(message.getSentAt())
                .build();
    }
}
