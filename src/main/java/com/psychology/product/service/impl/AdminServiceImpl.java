package com.psychology.product.service.impl;

import com.psychology.product.repository.AdminRepository;
import com.psychology.product.repository.dto.UserDTO;
import com.psychology.product.repository.model.UserDAO;
import com.psychology.product.service.AdminService;
import com.psychology.product.service.UserMapper;
import com.psychology.product.service.UserService;
import com.psychology.product.util.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    public UserDTO getCurrentClient(UUID clientId) {
        Optional<UserDAO> userOptional = adminRepository.findById(clientId);
        if (userOptional.isPresent()) {
            UserDAO userDAO = userOptional.get();
            return userMapper.toDTO(userDAO);
        }
        throw new NotFoundException("User not found");
    }
}