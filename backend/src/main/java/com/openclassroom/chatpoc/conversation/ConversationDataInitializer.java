package com.openclassroom.chatpoc.conversation;

import com.openclassroom.chatpoc.user.User;
import com.openclassroom.chatpoc.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ConversationDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository conversationParticipantRepository;

    @Override
    public void run(String... args) {
        User client = userRepository.findByUsername("client1").orElse(null);
        User agent = userRepository.findByUsername("agent1").orElse(null);

        if (client == null || agent == null) {
            return;
        }

        if (conversationRepository.count() > 0) {
            return;
        }

        Conversation conversation = Conversation.builder()
                .title("Conversation de démonstration")
                .build();

        conversationRepository.save(conversation);

        conversationParticipantRepository.save(new ConversationParticipant(conversation, client, null));
        conversationParticipantRepository.save(new ConversationParticipant(conversation, agent, null));
    }
}
