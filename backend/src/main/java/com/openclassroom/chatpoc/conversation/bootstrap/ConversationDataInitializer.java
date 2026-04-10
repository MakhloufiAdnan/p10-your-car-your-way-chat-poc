package com.openclassroom.chatpoc.conversation.bootstrap;

import com.openclassroom.chatpoc.conversation.entities.Conversation;
import com.openclassroom.chatpoc.conversation.entities.ConversationParticipant;
import com.openclassroom.chatpoc.conversation.repositories.ConversationParticipantRepository;
import com.openclassroom.chatpoc.conversation.repositories.ConversationRepository;
import com.openclassroom.chatpoc.user.entities.User;
import com.openclassroom.chatpoc.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Order(2)
@RequiredArgsConstructor
public class ConversationDataInitializer implements CommandLineRunner {

    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository conversationParticipantRepository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (conversationRepository.count() > 0) {
            return;
        }

        User client = userRepository.findByUsername("client1").orElse(null);
        User agent = userRepository.findByUsername("agent1").orElse(null);

        if (client == null || agent == null) {
            return;
        }

        Conversation firstConversation = conversationRepository.save(
                Conversation.builder()
                        .id(UUID.randomUUID())
                        .title("Conversation de démonstration")
                        .build()
        );

        Conversation secondConversation = conversationRepository.save(
                Conversation.builder()
                        .id(UUID.randomUUID())
                        .title("Suivi de réservation")
                        .build()
        );

        saveParticipant(firstConversation, client);
        saveParticipant(firstConversation, agent);

        saveParticipant(secondConversation, client);
        saveParticipant(secondConversation, agent);
    }

    private void saveParticipant(Conversation conversation, User user) {
        ConversationParticipant participant = new ConversationParticipant();
        participant.setConversation(conversation);
        participant.setUser(user);

        conversationParticipantRepository.save(participant);
    }
}
