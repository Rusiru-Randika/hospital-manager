package com.example.hospital_manager.service;

import com.example.hospital_manager.exception.ApiException;
import com.example.hospital_manager.model.Patient;
import com.example.hospital_manager.repository.AppointmentRepository;
import com.example.hospital_manager.repository.MedicalRecordRepository;
import com.example.hospital_manager.repository.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    public PatientService(
            PatientRepository patientRepository,
            AppointmentRepository appointmentRepository,
            MedicalRecordRepository medicalRecordRepository
    ) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    public Patient createPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    public List<Patient> getAllPatients(String search) {
        if (search != null && !search.trim().isEmpty()) {
            return patientRepository.findByNameContainingIgnoreCase(search.trim());
        }
        return patientRepository.findAll();
    }

    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Patient not found with id: " + id));
    }

    public Patient updatePatient(Long id, Patient updated) {
        Patient patient = getPatientById(id);

        patient.setName(updated.getName());
        patient.setEmail(updated.getEmail());
        patient.setPhone(updated.getPhone());
        patient.setDateOfBirth(updated.getDateOfBirth());
        patient.setGender(updated.getGender());
        patient.setAddress(updated.getAddress());
        patient.setMedicalHistory(updated.getMedicalHistory());

        return patientRepository.save(patient);
    }

    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Patient not found with id: " + id);
        }

        // Block on deleting a patient who has existing appointments or medical records
        if (appointmentRepository.existsByPatientId(id) || medicalRecordRepository.existsByPatientId(id)) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "Cannot delete patient: patient has associated appointments or medical records"
            );
        }

        patientRepository.deleteById(id);
    }
}
