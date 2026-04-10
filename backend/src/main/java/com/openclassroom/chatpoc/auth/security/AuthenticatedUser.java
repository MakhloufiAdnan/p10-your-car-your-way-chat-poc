package com.openclassroom.chatpoc.auth.security;

import com.openclassroom.chatpoc.user.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Représentation de l'utilisateur authentifié stockée dans le contexte Spring Security.
 *
 * Cette classe fait le lien entre l'utilisateur métier de la base et les besoins
 * de Spring Security pour l'autorisation et l'identification de la session.
 */
public record AuthenticatedUser(
        UUID userId,
        String username,
        String passwordHash,
        UserRole role
) implements UserDetails {

    /**
    * Transforme le rôle métier en autorité Spring Security (`ROLE_...`).
    */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
