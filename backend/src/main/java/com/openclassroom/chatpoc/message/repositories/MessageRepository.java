package com.openclassroom.chatpoc.message.repositories;

import com.openclassroom.chatpoc.message.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Query("""
        select m
        from Message m
        join fetch m.sender s
        where m.conversation.id = :conversationId
        order by m.sentAt asc
    """)
    List<Message> findHistoryByConversationId(UUID conversationId);
}
