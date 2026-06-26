package com.smartparcel.locker.service;

import com.smartparcel.locker.dao.UserDao;
import com.smartparcel.locker.dto.LoginRequest;
import com.smartparcel.locker.dto.RegisterRequest;
import com.smartparcel.locker.entity.User;
import com.smartparcel.locker.enums.Role;
import com.smartparcel.locker.exception.EmailAlreadyExistsException;
import com.smartparcel.locker.exception.InvalidCredentialsException;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link UserService} implementation. Owns the transaction boundaries
 * and delegates persistence to {@link UserDao}.
 */
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;
    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User register(RegisterRequest request) {
        String email = request.getEmail();
        if (userDao.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        User user = new User(
                email,
                passwordEncoder.encode(request.getPassword()),
                request.getFullName(),
                Role.RESIDENT);
        return userDao.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User login(LoginRequest request) {
        String email = request.getEmail();
        User user = userDao.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        return user;
    }
}
