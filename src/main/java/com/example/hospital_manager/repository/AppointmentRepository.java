package com.example.hospital_manager.repository;

import com.example.hospital_manager.model.AppUser;
import com.example.hospital_manager.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctor(AppUser doctor);
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByPatientId(Long patientId);
    boolean existsByPatientId(Long patientId);
}
