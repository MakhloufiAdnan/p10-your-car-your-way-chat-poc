package com.openclassroom.chatpoc.conversation;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ConversationSummaryResponse {
    private UUID conversationId;
    private String title;
    private LocalDateTime createdAt;
}
