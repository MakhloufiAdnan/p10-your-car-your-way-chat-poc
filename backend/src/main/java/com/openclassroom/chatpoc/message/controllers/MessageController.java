package com.openclassroom.chatpoc.message.controllers;

import com.openclassroom.chatpoc.auth.security.AuthenticatedUser;
import com.openclassroom.chatpoc.message.dtos.MessageResponse;
import com.openclassroom.chatpoc.message.dtos.SendMessageRequest;
import com.openclassroom.chatpoc.message.services.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/api/conversations/{conversationId}/messages")
    public List<MessageResponse> getConversationMessages(
            @PathVariable UUID conversationId,
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        AuthenticatedUser authenticatedUser = Objects.requireNonNull(
                currentUser,
                "Utilisateur authentifié requis."
        );

        return messageService.getConversationMessages(
                conversationId,
                authenticatedUser.getUsername()
        );
    }

    @PostMapping("/api/conversations/{conversationId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse sendMessage(
            @PathVariable UUID conversationId,
            @Valid @RequestBody SendMessageRequest request,
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        AuthenticatedUser authenticatedUser = Objects.requireNonNull(
                currentUser,
                "Utilisateur authentifié requis."
        );

        return messageService.sendMessage(
                conversationId,
                authenticatedUser.getUsername(),
                request
        );
    }
}
