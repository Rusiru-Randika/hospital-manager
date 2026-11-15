package com.example.hospital_manager.dto;

import com.example.hospital_manager.model.MedicalRecord;
import java.time.LocalDateTime;

public record MedicalRecordResponse(
    Long id,
    Long patientId,
    String patientName,
    Long doctorId,
    String doctorName,
    String diagnosis,
    String prescription,
    String notes,
    LocalDateTime createdAt
) {
    public static MedicalRecordResponse fromEntity(MedicalRecord record) {
        if (record == null) return null;
        return new MedicalRecordResponse(
            record.getId(),
            record.getPatient() != null ? record.getPatient().getId() : null,
            record.getPatient() != null ? record.getPatient().getName() : null,
            record.getDoctor() != null ? record.getDoctor().getId() : null,
            record.getDoctor() != null ? record.getDoctor().getUsername() : null,
            record.getDiagnosis(),
            record.getPrescription(),
            record.getNotes(),
            record.getCreatedAt()
        );
    }
}
