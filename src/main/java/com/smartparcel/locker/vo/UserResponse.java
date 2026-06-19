package com.smartparcel.locker.vo;

import com.smartparcel.locker.entity.User;
import com.smartparcel.locker.enums.Role;

/**
 * View object returned by the controller layer. Deliberately excludes the
 * password hash (NFR-05).
 */
public record UserResponse(Long id, String email, String fullName, Role role) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole());
    }
}
