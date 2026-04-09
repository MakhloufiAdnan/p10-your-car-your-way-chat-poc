package com.openclassroom.chatpoc.auth.controllers;

import com.openclassroom.chatpoc.auth.services.AuthService;
import com.openclassroom.chatpoc.auth.dtos.LoginRequest;
import com.openclassroom.chatpoc.auth.dtos.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
