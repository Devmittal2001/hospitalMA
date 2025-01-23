package com.appointment.scheduling.repository;

import com.appointment.scheduling.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}