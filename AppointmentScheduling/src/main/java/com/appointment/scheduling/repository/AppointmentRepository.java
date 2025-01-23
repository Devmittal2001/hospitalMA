package com.appointment.scheduling.repository;

import com.appointment.scheduling.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Optional<Appointment> findById(Long appointmentId);
    boolean existsByDoctor_DoctorIdAndDateAndTime(Long doctorId, LocalDate date, LocalTime time);
}
