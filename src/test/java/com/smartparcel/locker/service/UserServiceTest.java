package com.smartparcel.locker.service;

import com.smartparcel.locker.dao.UserDao;
import com.smartparcel.locker.dto.LoginRequest;
import com.smartparcel.locker.dto.RegisterRequest;
import com.smartparcel.locker.entity.User;
import com.smartparcel.locker.enums.Role;
import com.smartparcel.locker.exception.EmailAlreadyExistsException;
import com.smartparcel.locker.exception.InvalidCredentialsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link UserServiceImpl} business logic
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserDao userDao;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;
    private RegisterRequest registerRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new.user@example.com");
        request.setPassword("password123");
        request.setFullName("New User");
        request.setRole(Role.RESIDENT);
        return request;
    }

    private LoginRequest loginRequest(String email, String password) {
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);
        return request;
    }
    @Test
    void registerNormalizesEmailHashesPasswordAndForcesResident() {
        when(userDao.existsByEmail("new.user@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("HASH");
        when(userDao.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User created = userService.register(registerRequest());

        assertThat(created.getEmail()).isEqualTo("new.user@example.com");
        assertThat(created.getPassword()).isEqualTo("HASH");
        assertThat(created.getRole()).isEqualTo(Role.RESIDENT);
    }
    @Test
    void registerRejectsDuplicateEmail() {
        when(userDao.existsByEmail("new.user@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(registerRequest()))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }
    @Test
    void loginReturnsUserWhenPasswordMatches() {
        User stored = new User("user@example.com", "HASH", "User", Role.RESIDENT);
        when(userDao.findByEmail("user@example.com")).thenReturn(Optional.of(stored));
        when(passwordEncoder.matches("password123", "HASH")).thenReturn(true);

        User result = userService.login(loginRequest("user@example.com", "password123"));

        assertThat(result.getEmail()).isEqualTo("user@example.com");
    }
    @Test
    void loginRejectsUnknownEmail() {
        when(userDao.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.login(loginRequest("ghost@example.com", "whatever")))
                .isInstanceOf(InvalidCredentialsException.class);
    }
    @Test
    void loginRejectsWrongPassword() {
        User stored = new User("user@example.com", "HASH", "User", Role.RESIDENT);
        when(userDao.findByEmail("user@example.com")).thenReturn(Optional.of(stored));
        when(passwordEncoder.matches("wrong", "HASH")).thenReturn(false);

        assertThatThrownBy(() -> userService.login(loginRequest("user@example.com", "wrong")))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
