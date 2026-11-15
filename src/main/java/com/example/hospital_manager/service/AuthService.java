package com.example.hospital_manager.service;

import com.example.hospital_manager.dto.AuthResponse;
import com.example.hospital_manager.dto.LoginRequest;
import com.example.hospital_manager.dto.RegisterRequest;
import com.example.hospital_manager.dto.UserResponse;
import com.example.hospital_manager.exception.ApiException;
import com.example.hospital_manager.model.AppUser;
import com.example.hospital_manager.model.Role;
import com.example.hospital_manager.repository.UserRepository;
import com.example.hospital_manager.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        AppUser user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRole().name());

        String token = jwtService.generateToken(user.getUsername(), extraClaims);

        return new AuthResponse(
                token,
                "Bearer",
                jwtService.getExpirationTime(),
                user.getRole(),
                user.getUsername()
        );
    }

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ApiException(HttpStatus.CONFLICT, "Username already exists: " + request.username());
        }

        AppUser user = AppUser.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();

        AppUser savedUser = userRepository.save(user);
        return UserResponse.fromEntity(savedUser);
    }

    public UserResponse getCurrentUser(String username) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        return UserResponse.fromEntity(user);
    }

    public List<UserResponse> listUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::fromEntity)
                .toList();
    }

    public List<UserResponse> listDoctors() {
        return userRepository.findByRole(Role.DOCTOR)
                .stream()
                .map(UserResponse::fromEntity)
                .toList();
    }
}
