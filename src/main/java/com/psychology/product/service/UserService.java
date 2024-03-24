package com.psychology.product.service;

import com.psychology.product.controller.request.SignUpRequest;
import com.psychology.product.repository.dto.UserDTO;
import com.psychology.product.repository.model.UserDAO;
import jakarta.security.auth.message.AuthException;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface UserService {

    UserDTO getCurrentUser();

    void createNewUser(SignUpRequest signUpRequest);

    UserDAO findUserByEmail(String email);

    Authentication userAuthentication(UserDAO user);

    boolean deleteUser() throws AuthException;


}
