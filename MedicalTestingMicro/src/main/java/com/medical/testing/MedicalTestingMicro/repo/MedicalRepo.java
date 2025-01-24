package com.medical.testing.MedicalTestingMicro.repo;

import com.medical.testing.MedicalTestingMicro.models.MedicalFom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalRepo extends JpaRepository<MedicalFom, Integer> {
    // Your query methods
}
