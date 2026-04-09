package com.openclassroom.chatpoc.message.dtos;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class MessageResponse {

    private UUID messageId;
    private UUID conversationId;
    private String senderUsername;
    private String content;
    private LocalDateTime sentAt;
}
