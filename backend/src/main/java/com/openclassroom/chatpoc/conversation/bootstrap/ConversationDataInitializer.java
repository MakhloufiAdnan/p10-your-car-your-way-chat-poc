package com.openclassroom.chatpoc.conversation.bootstrap;

import com.openclassroom.chatpoc.conversation.entities.Conversation;
import com.openclassroom.chatpoc.conversation.entities.ConversationParticipant;
import com.openclassroom.chatpoc.conversation.enums.ConversationCategory;
import com.openclassroom.chatpoc.conversation.repositories.ConversationParticipantRepository;
import com.openclassroom.chatpoc.conversation.repositories.ConversationRepository;
import com.openclassroom.chatpoc.user.entities.User;
import com.openclassroom.chatpoc.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
* Prépare des conversations de démonstration pour visualiser rapidement le chat.
*
* L'objectif est de rendre la PoC immédiatement exploitable après le démarrage,
* sans manipulation manuelle préalable en base de données.
*/
@Component
@RequiredArgsConstructor
@Order(2)
public class ConversationDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository conversationParticipantRepository;

    /**
    * Ne sème les conversations que si aucune conversation n'existe encore,
    * afin d'éviter les doublons à chaque redémarrage.
    */
    @Override
    public void run(String... args) {
        if (conversationRepository.count() > 0) {
            return;
        }

        User client1 = userRepository.findByUsername("client1").orElse(null);
        User agent1 = userRepository.findByUsername("agent1").orElse(null);
        User client2 = userRepository.findByUsername("client2").orElse(null);
        User agent2 = userRepository.findByUsername("agent2").orElse(null);

        if (client1 == null || agent1 == null || client2 == null || agent2 == null) {
            return;
        }

        seedConversationsForPair(client1, agent1);
        seedConversationsForPair(client2, agent2);
    }

    /**
    * Génère plusieurs catégories de conversation pour un couple client/agent.
    *
    * Cela permet de démontrer que le tchat s'intègre à plusieurs contextes métier :
    * réservation, paiement, modification, annulation et support général.
    */
    private void seedConversationsForPair(User client, User agent) {
        createConversationWithParticipants(ConversationCategory.BOOKING, client, agent);
        createConversationWithParticipants(ConversationCategory.PAYMENT, client, agent);
        createConversationWithParticipants(ConversationCategory.MODIFICATION, client, agent);
        createConversationWithParticipants(ConversationCategory.CANCELLATION, client, agent);
        createConversationWithParticipants(ConversationCategory.GENERAL_SUPPORT, client, agent);
    }

    private void createConversationWithParticipants(
            ConversationCategory category,
            User client,
            User agent
    ) {
        Conversation conversation = Conversation.builder()
                .category(category)
                .title(category.getLabel())
                .build();

        conversationRepository.save(conversation);

        saveParticipant(conversation, client);
        saveParticipant(conversation, agent);
    }

    private void saveParticipant(Conversation conversation, User user) {
        ConversationParticipant participant = new ConversationParticipant();
        participant.setConversation(conversation);
        participant.setUser(user);

        conversationParticipantRepository.save(participant);
    }
}
