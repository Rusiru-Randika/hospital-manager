package com.example.hospital_manager.controller;

import com.example.hospital_manager.dto.AppointmentRequest;
import com.example.hospital_manager.dto.AppointmentResponse;
import com.example.hospital_manager.dto.StatusRequest;
import com.example.hospital_manager.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<AppointmentResponse> bookAppointment(@Valid @RequestBody AppointmentRequest request) {
        return new ResponseEntity<>(appointmentService.bookAppointment(request), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<List<AppointmentResponse>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByDoctorId(doctorId));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<AppointmentResponse>> getMyAppointments(Principal principal) {
        return ResponseEntity.ok(appointmentService.getAppointmentsForCurrentDoctor(principal.getName()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<AppointmentResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusRequest request,
            Authentication authentication
    ) {
        boolean isDoctor = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"));
        String username = authentication.getName();
        return ResponseEntity.ok(appointmentService.updateStatus(id, request, username, isDoctor));
    }
}
