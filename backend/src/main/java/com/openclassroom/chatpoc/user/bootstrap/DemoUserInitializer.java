package com.openclassroom.chatpoc.user.bootstrap;

import com.openclassroom.chatpoc.user.entities.User;
import com.openclassroom.chatpoc.user.repositories.UserRepository;
import com.openclassroom.chatpoc.user.dtos.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Order(1)
@RequiredArgsConstructor
public class DemoUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.demo-users.client.username}")
    private String demoClientUsername;

    @Value("${app.demo-users.client.password}")
    private String demoClientPassword;

    @Value("${app.demo-users.agent.username}")
    private String demoAgentUsername;

    @Value("${app.demo-users.agent.password}")
    private String demoAgentPassword;

    @Override
    public void run(String... args) {
        createUserIfNotExists(demoClientUsername, demoClientPassword, UserRole.CLIENT);
        createUserIfNotExists(demoAgentUsername, demoAgentPassword, UserRole.AGENT);
    }

    private void createUserIfNotExists(String username, String rawPassword, UserRole role) {
        if (userRepository.existsByUsername(username)) {
            return;
        }

        User user = User.builder()
                .username(username)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .role(role)
                .build();

        userRepository.save(user);
    }
}
