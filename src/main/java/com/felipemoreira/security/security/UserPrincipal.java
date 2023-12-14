package com.felipemoreira.security.security;

import com.felipemoreira.security.user.entities.User;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
public class UserPrincipal {

    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    private UserPrincipal(Optional<User> user) {
        this.username = user.get().getUsername();
        this.password = user.get().getPassword();
        this.authorities = user.get().getRoles().stream().map(role -> {
            return new SimpleGrantedAuthority("ROLE_".concat(role.getName()));
        }).collect(Collectors.toList());
    }

    public static UserPrincipal create(Optional<User> user) {
        return new UserPrincipal(user);
    }
}
