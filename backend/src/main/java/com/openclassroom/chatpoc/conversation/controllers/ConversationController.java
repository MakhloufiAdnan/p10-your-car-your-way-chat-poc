package com.openclassroom.chatpoc.conversation.controllers;

import com.openclassroom.chatpoc.conversation.dtos.ConversationSummaryResponse;
import com.openclassroom.chatpoc.conversation.services.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ConversationController {

    private final ConversationService conversationService;

    @GetMapping
    public List<ConversationSummaryResponse> getConversations(
            @RequestParam("username") String username
    ) {
        return conversationService.getConversationsForUser(username);
    }
}
