package com.example.hospital_manager.service;

import com.example.hospital_manager.dto.AuthResponse;
import com.example.hospital_manager.dto.LoginRequest;
import com.example.hospital_manager.dto.RegisterRequest;
import com.example.hospital_manager.dto.UserResponse;
import com.example.hospital_manager.exception.ApiException;
import com.example.hospital_manager.model.AppUser;
import com.example.hospital_manager.model.Patient;
import com.example.hospital_manager.model.Role;
import com.example.hospital_manager.repository.PatientRepository;
import com.example.hospital_manager.repository.UserRepository;
import com.example.hospital_manager.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserRepository userRepository,
            PatientRepository patientRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
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
                user.getUsername(),
                user.getFullName(),
                user.getRole()
        );
    }

    public UserResponse register(RegisterRequest request) {
        if (request.role() == Role.NURSE) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = authentication != null
                    && authentication.isAuthenticated()
                    && !(authentication instanceof AnonymousAuthenticationToken)
                    && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (!isAdmin) {
                throw new ApiException(HttpStatus.FORBIDDEN, "Nurses can only be added from an admin account");
            }
        }

        if (userRepository.existsByUsername(request.username())) {
            throw new ApiException(HttpStatus.CONFLICT, "Username already exists: " + request.username());
        }

        AppUser user = AppUser.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .role(request.role())
                .build();

        AppUser savedUser = userRepository.save(user);

        // If registered role is PATIENT, automatically create corresponding Patient profile
        if (request.role() == Role.PATIENT) {
            String patientName = (request.fullName() != null && !request.fullName().isBlank())
                    ? request.fullName().trim()
                    : request.username();

            Patient patient = Patient.builder()
                    .name(patientName)
                    .phone("+0000000000")
                    .gender("Unspecified")
                    .build();

            patientRepository.save(patient);
        }

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
