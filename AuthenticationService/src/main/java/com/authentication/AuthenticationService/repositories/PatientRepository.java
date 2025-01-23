package com.authentication.AuthenticationService.repositories;

import com.authentication.AuthenticationService.dto.PatientDTO;
import com.authentication.AuthenticationService.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<PatientDTO> getAllByPatientIdOrderByPatientId(Long patientId);
    List<Patient> findAll();
}
