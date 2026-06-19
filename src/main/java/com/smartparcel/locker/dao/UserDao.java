package com.smartparcel.locker.dao;

import com.smartparcel.locker.entity.User;

import java.util.Optional;

/**
 * Persistence operations for {@link User}
 */
public interface UserDao {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
