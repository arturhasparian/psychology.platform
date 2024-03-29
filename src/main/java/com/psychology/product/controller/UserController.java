package com.psychology.product.controller;

import com.psychology.product.repository.dto.UserDTO;
import com.psychology.product.service.UserService;
import com.psychology.product.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.security.auth.message.AuthException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
@AllArgsConstructor
@Tag(name = "User Controller", description = "Endpoints for working with user")
public class UserController {

    private final UserService userService;

    @GetMapping("/")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get current user", tags = {"User Library"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> getUser() {
        UserDTO user = userService.getCurrentUser();
        return ResponseEntity.ok(user);
    }

    @PutMapping("/")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> updateUser(@RequestBody UserDTO updated) {
        UserDTO user = userService.updateUser(updated);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Disable user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Deleted"),
            @ApiResponse(responseCode = "403", description = "Haven't permission to disable user"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> disableUser() throws AuthException {
        userService.disableUser();
        return ResponseUtil.generateResponse("User was disabled.", HttpStatus.OK);
    }


}
