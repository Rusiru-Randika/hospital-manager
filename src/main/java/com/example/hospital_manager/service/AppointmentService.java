package com.example.hospital_manager.service;

import com.example.hospital_manager.dto.AppointmentRequest;
import com.example.hospital_manager.dto.AppointmentResponse;
import com.example.hospital_manager.dto.StatusRequest;
import com.example.hospital_manager.exception.ApiException;
import com.example.hospital_manager.model.AppUser;
import com.example.hospital_manager.model.Appointment;
import com.example.hospital_manager.model.AppointmentStatus;
import com.example.hospital_manager.model.Patient;
import com.example.hospital_manager.model.Role;
import com.example.hospital_manager.repository.AppointmentRepository;
import com.example.hospital_manager.repository.PatientRepository;
import com.example.hospital_manager.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            UserRepository userRepository
    ) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    public AppointmentResponse bookAppointment(AppointmentRequest request) {
        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Patient not found with id: " + request.patientId()));

        AppUser doctor = userRepository.findById(request.doctorId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Doctor not found with id: " + request.doctorId()));

        if (doctor.getRole() != Role.DOCTOR) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Assigned user is not a doctor");
        }

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .appointmentTime(request.appointmentTime())
                .status(AppointmentStatus.SCHEDULED)
                .reason(request.reason())
                .build();

        Appointment saved = appointmentRepository.save(appointment);
        return AppointmentResponse.fromEntity(saved);
    }

    public List<AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(AppointmentResponse::fromEntity)
                .toList();
    }

    public List<AppointmentResponse> getAppointmentsByDoctorId(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId)
                .stream()
                .map(AppointmentResponse::fromEntity)
                .toList();
    }

    public List<AppointmentResponse> getAppointmentsForCurrentDoctor(String username) {
        AppUser doctor = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Doctor not found with username: " + username));

        return appointmentRepository.findByDoctor(doctor)
                .stream()
                .map(AppointmentResponse::fromEntity)
                .toList();
    }

    public AppointmentResponse getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Appointment not found with id: " + id));
        return AppointmentResponse.fromEntity(appointment);
    }

    public AppointmentResponse updateStatus(Long id, StatusRequest request, String currentUsername, boolean isDoctor) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Appointment not found with id: " + id));

        // Ownership rule: If user has role DOCTOR, they can only modify their own appointments
        if (isDoctor && !appointment.getDoctor().getUsername().equalsIgnoreCase(currentUsername)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Doctors can only modify the status of their own appointments");
        }

        appointment.setStatus(request.status());
        Appointment updated = appointmentRepository.save(appointment);
        return AppointmentResponse.fromEntity(updated);
    }
}
