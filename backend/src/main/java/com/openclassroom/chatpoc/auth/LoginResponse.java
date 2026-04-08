package com.openclassroom.chatpoc.auth;

import com.openclassroom.chatpoc.user.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class LoginResponse {

    private UUID userId;
    private String username;
    private UserRole role;
    private String message;
}
