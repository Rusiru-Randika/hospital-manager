package com.example.hospital_manager.service;

import com.example.hospital_manager.dto.MedicalRecordRequest;
import com.example.hospital_manager.dto.MedicalRecordResponse;
import com.example.hospital_manager.exception.ApiException;
import com.example.hospital_manager.model.AppUser;
import com.example.hospital_manager.model.MedicalRecord;
import com.example.hospital_manager.model.Patient;
import com.example.hospital_manager.model.Role;
import com.example.hospital_manager.repository.MedicalRecordRepository;
import com.example.hospital_manager.repository.PatientRepository;
import com.example.hospital_manager.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public MedicalRecordService(
            MedicalRecordRepository medicalRecordRepository,
            PatientRepository patientRepository,
            UserRepository userRepository
    ) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    public MedicalRecordResponse createRecord(MedicalRecordRequest request, String doctorUsername) {
        AppUser doctor = userRepository.findByUsername(doctorUsername)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Doctor not found: " + doctorUsername));

        if (doctor.getRole() != Role.DOCTOR) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Only doctors can write medical records");
        }

        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Patient not found with id: " + request.patientId()));

        MedicalRecord record = MedicalRecord.builder()
                .patient(patient)
                .doctor(doctor)
                .diagnosis(request.diagnosis())
                .prescription(request.prescription())
                .notes(request.notes())
                .build();

        MedicalRecord saved = medicalRecordRepository.save(record);
        return MedicalRecordResponse.fromEntity(saved);
    }

    public List<MedicalRecordResponse> getRecordsByPatientId(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Patient not found with id: " + patientId);
        }

        return medicalRecordRepository.findByPatientId(patientId)
                .stream()
                .map(MedicalRecordResponse::fromEntity)
                .toList();
    }

    public List<MedicalRecordResponse> getAllRecords() {
        return medicalRecordRepository.findAll()
                .stream()
                .map(MedicalRecordResponse::fromEntity)
                .toList();
    }

    public MedicalRecordResponse getRecordById(Long id) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Medical record not found with id: " + id));
        return MedicalRecordResponse.fromEntity(record);
    }
}
