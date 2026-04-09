package com.openclassroom.chatpoc.conversation.controllers;

import com.openclassroom.chatpoc.auth.security.AuthenticatedUser;
import com.openclassroom.chatpoc.conversation.dtos.ConversationSummaryResponse;
import com.openclassroom.chatpoc.conversation.services.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;

    @GetMapping
    public List<ConversationSummaryResponse> getConversations(
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        return conversationService.getConversationsForUser(currentUser.getUsername());
    }
}
