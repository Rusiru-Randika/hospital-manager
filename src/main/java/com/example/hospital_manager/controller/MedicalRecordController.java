package com.example.hospital_manager.controller;

import com.example.hospital_manager.dto.MedicalRecordRequest;
import com.example.hospital_manager.dto.MedicalRecordResponse;
import com.example.hospital_manager.service.MedicalRecordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<MedicalRecordResponse> createRecord(
            @Valid @RequestBody MedicalRecordRequest request,
            Principal principal
    ) {
        return new ResponseEntity<>(medicalRecordService.createRecord(request, principal.getName()), HttpStatus.CREATED);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<List<MedicalRecordResponse>> getRecordsByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(medicalRecordService.getRecordsByPatientId(patientId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<MedicalRecordResponse> getRecordById(@PathVariable Long id) {
        return ResponseEntity.ok(medicalRecordService.getRecordById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<MedicalRecordResponse>> getAllRecords() {
        return ResponseEntity.ok(medicalRecordService.getAllRecords());
    }
}
