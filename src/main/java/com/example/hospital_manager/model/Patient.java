package com.example.hospital_manager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Patient name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(nullable = false)
    private String name;

    @Email(message = "Email must be valid")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9+\\-\\s()]{7,20}$", message = "Phone number is invalid")
    @Column(nullable = false)
    private String phone;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Gender is required")
    @Column(nullable = false)
    private String gender;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;

    @Column(columnDefinition = "TEXT")
    private String medicalHistory;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
