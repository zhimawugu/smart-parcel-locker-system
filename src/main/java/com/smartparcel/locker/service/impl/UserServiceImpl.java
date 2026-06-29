package com.smartparcel.locker.service.impl;

import com.smartparcel.locker.dao.UserDao;
import com.smartparcel.locker.dto.LoginRequest;
import com.smartparcel.locker.dto.RegisterRequest;
import com.smartparcel.locker.entity.User;
import com.smartparcel.locker.enums.Role;
import com.smartparcel.locker.exception.BizException;
import com.smartparcel.locker.service.UserService;
import com.smartparcel.locker.vo.ResultCode;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new BizException(ResultCode.EMAIL_EXISTS, "Email already registered: " + email);
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
                .orElseThrow(() -> new BizException(ResultCode.UNAUTHORIZED));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BizException(ResultCode.UNAUTHORIZED);
        }
        return user;
    }
}
