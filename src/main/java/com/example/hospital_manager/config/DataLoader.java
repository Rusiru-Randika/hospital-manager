package com.example.hospital_manager.config;

import com.example.hospital_manager.model.AppUser;
import com.example.hospital_manager.model.Patient;
import com.example.hospital_manager.model.Role;
import com.example.hospital_manager.repository.PatientRepository;
import com.example.hospital_manager.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(
            UserRepository userRepository,
            PatientRepository patientRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        seedPatients();
    }

    private void seedUsers() {
        if (userRepository.count() == 0) {
            AppUser admin = AppUser.builder()
                    .username("admin")
                    .fullName("System Administrator")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();

            AppUser doctor1 = AppUser.builder()
                    .username("dr_smith")
                    .fullName("Dr. John Smith")
                    .password(passwordEncoder.encode("doctor123"))
                    .role(Role.DOCTOR)
                    .build();

            AppUser doctor2 = AppUser.builder()
                    .username("dr_johnson")
                    .fullName("Dr. Sarah Johnson")
                    .password(passwordEncoder.encode("doctor123"))
                    .role(Role.DOCTOR)
                    .build();

            AppUser nurse = AppUser.builder()
                    .username("nurse_mary")
                    .fullName("Nurse Mary")
                    .password(passwordEncoder.encode("nurse123"))
                    .role(Role.NURSE)
                    .build();

            AppUser receptionist = AppUser.builder()
                    .username("receptionist_bob")
                    .fullName("Bob Miller")
                    .password(passwordEncoder.encode("receptionist123"))
                    .role(Role.RECEPTIONIST)
                    .build();

            userRepository.saveAll(List.of(admin, doctor1, doctor2, nurse, receptionist));
            System.out.println(">>> Seeded default staff accounts into the database.");
        }
    }

    private void seedPatients() {
        if (patientRepository.count() == 0) {
            Patient p1 = Patient.builder()
                    .name("Alice Walker")
                    .email("alice.walker@example.com")
                    .phone("+1234567890")
                    .dateOfBirth(LocalDate.of(1990, 5, 15))
                    .gender("Female")
                    .address("123 Maple Street, Springfield")
                    .medicalHistory("No known chronic illnesses. Penicillin allergy.")
                    .build();

            Patient p2 = Patient.builder()
                    .name("Bob Davis")
                    .email("bob.davis@example.com")
                    .phone("+1987654321")
                    .dateOfBirth(LocalDate.of(1982, 11, 23))
                    .gender("Male")
                    .address("456 Oak Avenue, Springfield")
                    .medicalHistory("Hypertension diagnosed 2021.")
                    .build();

            Patient p3 = Patient.builder()
                    .name("Charlie Evans")
                    .email("charlie.evans@example.com")
                    .phone("+1555123456")
                    .dateOfBirth(LocalDate.of(2001, 3, 10))
                    .gender("Male")
                    .address("789 Pine Road, Springfield")
                    .medicalHistory("Asthma.")
                    .build();

            patientRepository.saveAll(List.of(p1, p2, p3));
            System.out.println(">>> Seeded sample patients into the database.");
        }
    }
}
