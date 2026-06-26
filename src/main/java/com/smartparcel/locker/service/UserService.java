package com.smartparcel.locker.service;

import com.smartparcel.locker.dto.LoginRequest;
import com.smartparcel.locker.dto.RegisterRequest;
import com.smartparcel.locker.entity.User;
import com.smartparcel.locker.exception.EmailAlreadyExistsException;
import com.smartparcel.locker.exception.InvalidCredentialsException;

/**
 * Business logic for user registration and login (FR-01). Sits between the
 * controller and DAO layers.
 */
public interface UserService {
    /**
     * Registers a new RESIDENT account (FR-01, A1).
     *
     * @throws EmailAlreadyExistsException if the email is already in use
     */
    User register(RegisterRequest request);

    /**
     * Validates credentials and returns the authenticated user, whose role
     * drives the destination dashboard (FR-01, main flow).
     *
     * @throws InvalidCredentialsException on unknown email or wrong password (E1)
     */
    User login(LoginRequest request);
}
