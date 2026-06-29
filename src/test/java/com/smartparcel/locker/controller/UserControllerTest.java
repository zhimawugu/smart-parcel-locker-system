package com.smartparcel.locker.controller;

import com.smartparcel.locker.entity.User;
import com.smartparcel.locker.enums.Role;
import com.smartparcel.locker.exception.BizException;
import com.smartparcel.locker.exception.GlobalExceptionHandler;
import com.smartparcel.locker.service.UserService;
import com.smartparcel.locker.vo.ResultCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web-layer tests for {@link UserController}. Every response is HTTP 200 with a
 * unified {@code {code, msg, data}} body; the business outcome is in {@code code}.
 */
@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {
    private static final String REGISTER_BODY =
            "{\"email\":\"resident@example.com\",\"password\":\"password123\",\"fullName\":\"Resident One\"}";
    private static final String LOGIN_BODY =
            "{\"email\":\"resident@example.com\",\"password\":\"password123\"}";
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    private final User user =
            new User("resident@example.com", "HASH", "Resident One", Role.RESIDENT);
    @Test
    void registerSucceedsWithCode0AndNoPassword() throws Exception {
        when(userService.register(any())).thenReturn(user);

        mockMvc.perform(post("/api/auth/register").contentType(APPLICATION_JSON).content(REGISTER_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.email").value("resident@example.com"))
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }
    @Test
    void registerInvalidEmailReturnsParamErrorCode() throws Exception {
        String invalid = "{\"email\":\"not-an-email\",\"password\":\"password123\",\"fullName\":\"X\"}";

        mockMvc.perform(post("/api/auth/register").contentType(APPLICATION_JSON).content(invalid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(40000));
    }
    @Test
    void registerDuplicateEmailReturnsEmailExistsCode() throws Exception {
        when(userService.register(any())).thenThrow(new BizException(ResultCode.EMAIL_EXISTS));

        mockMvc.perform(post("/api/auth/register").contentType(APPLICATION_JSON).content(REGISTER_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(40900));
    }
    @Test
    void loginSucceedsWithCode0() throws Exception {
        when(userService.login(any())).thenReturn(user);

        mockMvc.perform(post("/api/auth/login").contentType(APPLICATION_JSON).content(LOGIN_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.email").value("resident@example.com"));
    }
    @Test
    void loginBadCredentialsReturnsUnauthorizedCode() throws Exception {
        when(userService.login(any())).thenThrow(new BizException(ResultCode.UNAUTHORIZED));

        mockMvc.perform(post("/api/auth/login").contentType(APPLICATION_JSON).content(LOGIN_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(40100));
    }
}
