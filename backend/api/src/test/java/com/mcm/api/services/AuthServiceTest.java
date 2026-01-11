package com.mcm.api.services;

import com.mcm.api.dto.LoginRequest;
import com.mcm.api.dto.RegisterRequest;
import com.mcm.api.dto.UserResponse;
import com.mcm.api.entities.UserAccount;
import com.mcm.api.repository.UserAccountRepository;
import com.mcm.api.services.AuthService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserAccountRepository users;

    @InjectMocks private AuthService service;

    @Test
    void register_fails_when_username_missing() {
        // Arrange
        RegisterRequest req = new RegisterRequest(null, "pass");

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> service.register(req));
        verifyNoInteractions(users);
    }

    @Test
    void register_fails_when_password_missing() {
        // Arrange
        RegisterRequest req = new RegisterRequest("alice", "   ");

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> service.register(req));
        verifyNoInteractions(users);
    }

    @Test
    void register_fails_when_username_already_exists() {
        // Arrange
        RegisterRequest req = new RegisterRequest("alice", "pass");
        when(users.existsByUsernameIgnoreCase("alice")).thenReturn(true);

        // Act + Assert
        assertThrows(IllegalStateException.class, () -> service.register(req));
        verify(users, never()).save(any());
    }

    @Test
    void register_success_trims_username_hashes_password_sets_credits() {
        // Arrange
        RegisterRequest req = new RegisterRequest("  Alice  ", "secret");
        when(users.existsByUsernameIgnoreCase("Alice")).thenReturn(false);

        when(users.save(any(UserAccount.class))).thenAnswer(inv -> {
            UserAccount u = inv.getArgument(0);
            u.setId(123L);
            return u;
        });

        // Act
        UserResponse res = service.register(req);

        // Assert
        assertEquals(123L, res.id());
        assertEquals("Alice", res.username());
        assertEquals(new BigDecimal("500.00"), res.credits());

        ArgumentCaptor<UserAccount> captor = ArgumentCaptor.forClass(UserAccount.class);
        verify(users).save(captor.capture());

        UserAccount saved = captor.getValue();
        assertEquals("Alice", saved.getUsername());
        assertNotNull(saved.getPasswordHash());
        assertNotEquals("secret", saved.getPasswordHash()); // hashed
        assertEquals(new BigDecimal("500.00"), saved.getCredits());
    }

    @Test
    void login_fails_when_user_not_found() {
        // Arrange
        LoginRequest req = new LoginRequest("alice", "secret");
        when(users.findByUsernameIgnoreCase("alice")).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(IllegalStateException.class, () -> service.login(req));
    }

    @Test
    void login_fails_when_password_wrong() {
        // Arrange
        LoginRequest req = new LoginRequest("alice", "wrong");

        // create a real bcrypt hash for a different password
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder enc =
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();

        UserAccount user = new UserAccount();
        user.setId(1L);
        user.setUsername("alice");
        user.setPasswordHash(enc.encode("secret"));
        user.setCredits(new BigDecimal("10.00"));

        when(users.findByUsernameIgnoreCase("alice")).thenReturn(Optional.of(user));

        // Act + Assert
        assertThrows(IllegalStateException.class, () -> service.login(req));
    }

    @Test
    void login_success_returns_user_response() {
        // Arrange
        LoginRequest req = new LoginRequest(" alice ", "secret");

        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder enc =
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();

        UserAccount user = new UserAccount();
        user.setId(1L);
        user.setUsername("alice");
        user.setPasswordHash(enc.encode("secret"));
        user.setCredits(new BigDecimal("25.00"));

        when(users.findByUsernameIgnoreCase("alice")).thenReturn(Optional.of(user));

        // Act
        UserResponse res = service.login(req);

        // Assert
        assertEquals(1L, res.id());
        assertEquals("alice", res.username());
        assertEquals(new BigDecimal("25.00"), res.credits());
    }
}