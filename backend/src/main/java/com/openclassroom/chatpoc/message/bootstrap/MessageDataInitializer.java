package com.openclassroom.chatpoc.message.bootstrap;

import com.openclassroom.chatpoc.conversation.entities.Conversation;
import com.openclassroom.chatpoc.conversation.repositories.ConversationRepository;
import com.openclassroom.chatpoc.message.entities.Message;
import com.openclassroom.chatpoc.message.repositories.MessageRepository;
import com.openclassroom.chatpoc.user.entities.User;
import com.openclassroom.chatpoc.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageDataInitializer implements CommandLineRunner {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (messageRepository.count() > 0) {
            return;
        }

        Conversation conversation = conversationRepository.findAll()
                .stream()
                .findFirst()
                .orElse(null);

        User client = userRepository.findByUsername("client1").orElse(null);
        User agent = userRepository.findByUsername("agent1").orElse(null);

        if (conversation == null || client == null || agent == null) {
            return;
        }

        messageRepository.save(Message.builder()
                .conversation(conversation)
                .sender(client)
                .content("Bonjour, j’ai une question sur ma réservation.")
                .build());

        messageRepository.save(Message.builder()
                .conversation(conversation)
                .sender(agent)
                .content("Bonjour, je vous écoute. Que puis-je faire pour vous ?")
                .build());

        messageRepository.save(Message.builder()
                .conversation(conversation)
                .sender(client)
                .content("Je voudrais vérifier l’horaire de récupération du véhicule.")
                .build());
    }
}
