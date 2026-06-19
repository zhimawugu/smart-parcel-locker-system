package com.smartparcel.locker.dao;

import com.smartparcel.locker.entity.User;
import com.smartparcel.locker.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Persistence-layer tests for {@link UserDaoImpl} running against in-memory H2.
 */
@DataJpaTest
@Import(UserDaoImpl.class)
class UserDaoImplTest {

    @Autowired
    private UserDao userDao;

    private User newUser(String email) {
        return new User(email, "hashed", "Test User", Role.RESIDENT);
    }

    @Test
    void savePersistsAndAssignsId() {
        User saved = userDao.save(newUser("alice@example.com"));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void findByEmailReturnsMatch() {
        userDao.save(newUser("bob@example.com"));
        Optional<User> found = userDao.findByEmail("bob@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getRole()).isEqualTo(Role.RESIDENT);
    }

    @Test
    void findByEmailReturnsEmptyWhenMissing() {
        assertThat(userDao.findByEmail("nobody@example.com")).isEmpty();
    }

    @Test
    void existsByEmailReflectsPersistence() {
        assertThat(userDao.existsByEmail("carol@example.com")).isFalse();
        userDao.save(newUser("carol@example.com"));
        assertThat(userDao.existsByEmail("carol@example.com")).isTrue();
    }
}
