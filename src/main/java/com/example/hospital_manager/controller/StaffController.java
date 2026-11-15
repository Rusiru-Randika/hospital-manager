package com.example.hospital_manager.controller;

import com.example.hospital_manager.dto.UserResponse;
import com.example.hospital_manager.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class StaffController {

    private final AuthService authService;

    public StaffController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> listUsers() {
        return ResponseEntity.ok(authService.listUsers());
    }

    @GetMapping("/doctors")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'RECEPTIONIST')")
    public ResponseEntity<List<UserResponse>> listDoctors() {
        return ResponseEntity.ok(authService.listDoctors());
    }
}
