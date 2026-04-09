package com.openclassroom.chatpoc.conversation.repositories;

import com.openclassroom.chatpoc.conversation.entities.ConversationParticipant;
import com.openclassroom.chatpoc.conversation.entities.ConversationParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant, ConversationParticipantId> {

    @Query("""
        select cp
        from ConversationParticipant cp
        join fetch cp.conversation c
        where cp.user.username = :username
        order by c.createdAt desc
    """)
    List<ConversationParticipant> findAllByUsernameWithConversation(String username);
}
