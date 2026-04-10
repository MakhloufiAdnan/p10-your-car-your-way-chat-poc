package com.openclassroom.chatpoc.conversation.repositories;

import com.openclassroom.chatpoc.conversation.entities.ConversationParticipant;
import com.openclassroom.chatpoc.conversation.entities.ConversationParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant, ConversationParticipantId> {

    @Query("""
        select cp
        from ConversationParticipant cp
        join fetch cp.conversation c
        where cp.user.username = :username
        order by c.createdAt desc
    """)
    List<ConversationParticipant> findAllByUsernameWithConversation(String username);

    @Query("""
        select
            case
                when count(cp) > 0 then true
                else false
            end
        from ConversationParticipant cp
        where cp.conversation.id = :conversationId
          and cp.user.username = :username
    """)
    boolean existsByConversationIdAndUsername(UUID conversationId, String username);

    @Query("""
        select cp.user.username
        from ConversationParticipant cp
        where cp.conversation.id = :conversationId
    """)
    List<String> findParticipantUsernamesByConversationId(UUID conversationId);
}
