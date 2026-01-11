package com.mcm.api.services;

import com.mcm.api.dto.LoginRequest;
import com.mcm.api.dto.RegisterRequest;
import com.mcm.api.dto.UserResponse;
import com.mcm.api.entities.UserAccount;
import com.mcm.api.repository.UserAccountRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AuthService {

    private final UserAccountRepository users;
    private final BCryptPasswordEncoder encoder;

    public AuthService(UserAccountRepository users) {
        this.users = users;
        this.encoder = new BCryptPasswordEncoder();
    }

    public UserResponse register(RegisterRequest req) {
        String username = req.username() == null ? "" : req.username().trim();
        if (username.isEmpty()) throw new IllegalArgumentException("username required");
        if (req.password() == null || req.password().trim().isEmpty()) throw new IllegalArgumentException("password required");

        if (users.existsByUsernameIgnoreCase(username)) {
            throw new IllegalStateException("username already exists");
        }

        UserAccount user = new UserAccount();
        user.setUsername(username);
        user.setPasswordHash(encoder.encode(req.password()));
        user.setCredits(new BigDecimal("500.00"));
        UserAccount saved = users.save(user);
        return new UserResponse(saved.getId(), saved.getUsername(), saved.getCredits());
    }

    public UserResponse login(LoginRequest req) {
        String username = req.username() == null ? "" : req.username().trim();
        String password = req.password() == null ? "" : req.password();

        UserAccount user = users.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new IllegalStateException("invalid credentials"));

        if (!encoder.matches(password, user.getPasswordHash())) {
            throw new IllegalStateException("invalid credentials");
        }

        return new UserResponse(user.getId(), user.getUsername(), user.getCredits());
    }
}
