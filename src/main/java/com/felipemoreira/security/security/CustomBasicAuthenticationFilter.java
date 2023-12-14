package com.felipemoreira.security.security;

import com.felipemoreira.security.user.entities.User;
import com.felipemoreira.security.user.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class CustomBasicAuthenticationFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    private static final String AUTHORIZATION = "Authorization";
    private static final String BASIC = "Basic ";
    private static final String USER_DOES_NOT_EXISTS = "User does not exists !";

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        if (isBasicAuthentication(request)) {
            String[] credentials = decodeBase64(getHeader(request)).split(":");

            String username = credentials[0];
            String password = credentials[1];

            Optional<User> user = userRepository.findByUsername(username);

            if (!user.isPresent()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(USER_DOES_NOT_EXISTS);
                return;
            }

            Boolean valid = checkPassword(user.get().getPassword(), password);

            if (!valid) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Password not match");
            }

            setAuthentication(user);
        }
    }

    private void setAuthentication(Optional<User> user) {
        Authentication authentication = createAuthenticationToken(user);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private Authentication createAuthenticationToken(Optional<User> user) {
        UserPrincipal userPrincipal = UserPrincipal.create(user);

        return new UsernamePasswordAuthenticationToken(userPrincipal, null,
            userPrincipal.getAuthorities());
    }

    private Boolean checkPassword(String userPassword, String loginPassword) {
        return passwordEncoder().matches(loginPassword, userPassword);
    }

    private String decodeBase64(String base64) {
        byte[] decodeBytes = Base64.getDecoder().decode(base64);
        return new String(decodeBytes);
    }

    private boolean isBasicAuthentication(HttpServletRequest request) {
        String header = getHeader(request);

        return header != null && header.startsWith(BASIC);
    }

    private String getHeader(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION);
    }
}
