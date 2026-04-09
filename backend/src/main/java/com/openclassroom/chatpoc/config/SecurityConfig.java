package com.openclassroom.chatpoc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF désactivé pour la PoC.
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        // Endpoint de connexion.
                        .requestMatchers("/api/auth/**").permitAll()

                        // Temporairement ouvert.
                        // La PoC ne porte pas encore de session ou de token backend.
                        .requestMatchers("/api/conversations/**").permitAll()

                        .anyRequest().authenticated()
                )

                // Pas de login Spring par défaut.
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
