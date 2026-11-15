package com.example.hospital_manager.dto;

import com.example.hospital_manager.model.Appointment;
import com.example.hospital_manager.model.AppointmentStatus;
import java.time.LocalDateTime;

public record AppointmentResponse(
    Long id,
    Long patientId,
    String patientName,
    Long doctorId,
    String doctorName,
    LocalDateTime appointmentTime,
    AppointmentStatus status,
    String reason,
    LocalDateTime createdAt
) {
    public static AppointmentResponse fromEntity(Appointment appointment) {
        if (appointment == null) return null;
        return new AppointmentResponse(
            appointment.getId(),
            appointment.getPatient() != null ? appointment.getPatient().getId() : null,
            appointment.getPatient() != null ? appointment.getPatient().getName() : null,
            appointment.getDoctor() != null ? appointment.getDoctor().getId() : null,
            appointment.getDoctor() != null ? appointment.getDoctor().getUsername() : null,
            appointment.getAppointmentTime(),
            appointment.getStatus(),
            appointment.getReason(),
            appointment.getCreatedAt()
        );
    }
}
