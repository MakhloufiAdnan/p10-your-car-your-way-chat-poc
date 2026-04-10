package com.openclassroom.chatpoc.message.bootstrap;

import com.openclassroom.chatpoc.conversation.entities.Conversation;
import com.openclassroom.chatpoc.conversation.repositories.ConversationRepository;
import com.openclassroom.chatpoc.message.entities.Message;
import com.openclassroom.chatpoc.message.repositories.MessageRepository;
import com.openclassroom.chatpoc.user.entities.User;
import com.openclassroom.chatpoc.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(3)
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

        User client = userRepository.findByUsername("client1").orElse(null);
        User agent = userRepository.findByUsername("agent1").orElse(null);

        if (client == null || agent == null) {
            return;
        }

        List<Conversation> conversations = conversationRepository.findAll();

        if (conversations.size() < 2) {
            return;
        }

        Conversation firstConversation = conversations.get(0);
        Conversation secondConversation = conversations.get(1);

        seedFirstConversation(firstConversation, client, agent);
        seedSecondConversation(secondConversation, client, agent);
    }

    private void seedFirstConversation(Conversation conversation, User client, User agent) {
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

    private void seedSecondConversation(Conversation conversation, User client, User agent) {
        messageRepository.save(Message.builder()
                .conversation(conversation)
                .sender(client)
                .content("Bonjour, je souhaite savoir si ma réservation est bien confirmée.")
                .build());

        messageRepository.save(Message.builder()
                .conversation(conversation)
                .sender(agent)
                .content("Oui, votre réservation est confirmée. Vous pouvez venir récupérer le véhicule à l’agence prévue.")
                .build());

        messageRepository.save(Message.builder()
                .conversation(conversation)
                .sender(client)
                .content("Merci pour votre retour.")
                .build());
    }
}
