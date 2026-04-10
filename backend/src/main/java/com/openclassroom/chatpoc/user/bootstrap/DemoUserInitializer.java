package com.openclassroom.chatpoc.user.bootstrap;

import com.openclassroom.chatpoc.user.entities.User;
import com.openclassroom.chatpoc.user.enums.UserRole;
import com.openclassroom.chatpoc.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
* Initialise des comptes de démonstration au démarrage de la PoC.
*
* Ces utilisateurs servent uniquement à tester rapidement les parcours
* de login et le chat temps réel entre client et agent.
*/
@Component
@RequiredArgsConstructor
@Order(1)
public class DemoUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.demo-users.client.username:client1}")
    private String demoClientUsername;

    @Value("${app.demo-users.client.password:Client123#}")
    private String demoClientPassword;

    @Value("${app.demo-users.agent.username:agent1}")
    private String demoAgentUsername;

    @Value("${app.demo-users.agent.password:Agent123#}")
    private String demoAgentPassword;

    @Value("${app.demo-users.client2.username:client2}")
    private String demoClient2Username;

    @Value("${app.demo-users.client2.password:Client234#}")
    private String demoClient2Password;

    @Value("${app.demo-users.agent2.username:agent2}")
    private String demoAgent2Username;

    @Value("${app.demo-users.agent2.password:Agent234#}")
    private String demoAgent2Password;

    /**
    * Crée les utilisateurs de démonstration uniquement s'ils n'existent pas déjà,
    * afin de garder le démarrage idempotent.
    */
    @Override
    public void run(String... args) {
        createUserIfNotExists(demoClientUsername, demoClientPassword, UserRole.CLIENT);
        createUserIfNotExists(demoAgentUsername, demoAgentPassword, UserRole.AGENT);
        createUserIfNotExists(demoClient2Username, demoClient2Password, UserRole.CLIENT);
        createUserIfNotExists(demoAgent2Username, demoAgent2Password, UserRole.AGENT);
    }

    /**
    * Crée un utilisateur de démo avec mot de passe hashé.
    *
    * Le hashage est réalisé ici exactement comme pour un utilisateur réel,
    * afin de conserver un comportement de sécurité cohérent.
    */
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
