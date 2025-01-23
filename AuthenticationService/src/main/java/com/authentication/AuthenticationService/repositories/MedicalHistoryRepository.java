package com.authentication.AuthenticationService.repositories;

import com.authentication.AuthenticationService.models.MedicalHistory;
import com.authentication.AuthenticationService.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, Long> {
    List<MedicalHistory> findByPatient(Patient patient);
}
