package com.openclassroom.chatpoc.conversation.services;

import com.openclassroom.chatpoc.conversation.dtos.ConversationSummaryResponse;
import com.openclassroom.chatpoc.conversation.repositories.ConversationParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationParticipantRepository conversationParticipantRepository;

    public List<ConversationSummaryResponse> getConversationsForUser(String username) {
        return conversationParticipantRepository.findAllByUsernameWithConversation(username)
                .stream()
                // Retourne une vue simple pour la liste des conversations.
                .map(cp -> ConversationSummaryResponse.builder()
                        .conversationId(cp.getConversation().getId())
                        .title(cp.getConversation().getTitle())
                        .createdAt(cp.getConversation().getCreatedAt())
                        .build())
                .toList();
    }
}