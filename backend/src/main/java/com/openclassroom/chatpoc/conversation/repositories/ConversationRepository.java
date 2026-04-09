package com.openclassroom.chatpoc.conversation.repositories;

import com.openclassroom.chatpoc.conversation.entities.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
}
