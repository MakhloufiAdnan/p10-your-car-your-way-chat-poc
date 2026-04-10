package com.openclassroom.chatpoc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration STOMP/WebSocket de la fonctionnalité de chat temps réel.
 *
 * Le backend publie les messages sur des destinations utilisateur (`/user/...`)
 * afin que chaque participant reçoive uniquement les messages des conversations
 * auxquelles il appartient.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
    * Active un broker simple en mémoire pour la PoC.
    *
    * Le préfixe `/queue` est utilisé pour les destinations point-à-point.
    * Le préfixe `/user` permet ensuite à Spring de résoudre automatiquement
    * la file propre à chaque utilisateur connecté.
    */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue");
        registry.setUserDestinationPrefix("/user");
    }

    /**
    * Point d'entrée WebSocket consommé par le frontend Angular.
    *
    * Le frontend se connecte directement à `/ws-chat` via STOMP sur WebSocket natif.
    */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("http://localhost:4200", "http://127.0.0.1:4200");
    }
}
