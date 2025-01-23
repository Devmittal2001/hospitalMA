package com.appointment.scheduling.service;

import com.appointment.scheduling.dto.AppointmentDTO;
import com.appointment.scheduling.mapper.AppointmentMapper;
import com.appointment.scheduling.model.Appointment;
import com.appointment.scheduling.repository.AppointmentRepository;
import com.appointment.scheduling.repository.DoctorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class AppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Transactional
    public AppointmentDTO bookAppointment(AppointmentDTO appointmentDTO) {
        logger.info("Attempting to book an appointment for doctor ID {} on {} at {}",
                appointmentDTO.getDoctorId(), appointmentDTO.getDate(), appointmentDTO.getTime());

        // Convert the DTO to entity
        Appointment appointment = appointmentMapper.toEntity(appointmentDTO);

        if (appointmentRepository.existsByDoctor_DoctorIdAndDateAndTime(appointment.getDoctor().getDoctorId(),
                appointment.getDate(), appointment.getTime())) {
            logger.error("Doctor with ID {} is not available at this time.", appointmentDTO.getDoctorId());
            throw new IllegalStateException("Doctor is not available at this time.");
        }

        Appointment savedAppointment = appointmentRepository.save(appointment);
        logger.info("Appointment successfully booked with ID {}", savedAppointment.getAppointmentId());
        return appointmentMapper.toDTO(savedAppointment);
    }

    public AppointmentDTO getAppointmentDetails(Long appointmentId) {
        logger.info("Fetching details for appointment ID {}", appointmentId);
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> {
                    logger.error("Appointment with ID {} not found.", appointmentId);
                    return new IllegalArgumentException("Appointment not found");
                });
        return appointmentMapper.toDTO(appointment);
    }

    public AppointmentDTO rescheduleAppointment(Long appointmentId, AppointmentDTO updatedAppointmentDTO) {
        logger.info("Attempting to reschedule appointment ID {}", appointmentId);

        Appointment existingAppointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> {
                    logger.error("Appointment with ID {} not found.", appointmentId);
                    return new IllegalArgumentException("Appointment not found");
                });

        // Convert the DTO to entity for rescheduling
        Appointment updatedAppointment = appointmentMapper.toEntity(updatedAppointmentDTO);

        if (appointmentRepository.existsByDoctor_DoctorIdAndDateAndTime(updatedAppointment.getDoctor().getDoctorId(),
                updatedAppointment.getDate(), updatedAppointment.getTime())) {
            logger.error("Doctor with ID {} is not available at the new time.", updatedAppointmentDTO.getDoctorId());
            throw new IllegalStateException("Doctor is not available at this time.");
        }

        existingAppointment.setDate(updatedAppointment.getDate());
        existingAppointment.setTime(updatedAppointment.getTime());

        Appointment savedAppointment = appointmentRepository.save(existingAppointment);
        logger.info("Appointment with ID {} successfully rescheduled", savedAppointment.getAppointmentId());
        return appointmentMapper.toDTO(savedAppointment);
    }

    public void cancelAppointment(Long appointmentId) {
        logger.info("Attempting to cancel appointment ID {}", appointmentId);
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> {
                    logger.error("Appointment with ID {} not found.", appointmentId);
                    return new IllegalArgumentException("Appointment not found");
                });

        appointmentRepository.delete(appointment);
        logger.info("Appointment with ID {} successfully cancelled", appointmentId);
    }

    public boolean checkDoctorAvailability(Long doctorId, LocalDate date, LocalTime time) {
        logger.info("Checking availability for doctor ID {} on {} at {}", doctorId, date, time);
        boolean isAvailable = !appointmentRepository.existsByDoctor_DoctorIdAndDateAndTime(doctorId, date, time);
        if (isAvailable) {
            logger.info("Doctor ID {} is available on {} at {}", doctorId, date, time);
        } else {
            logger.error("Doctor ID {} is not available on {} at {}", doctorId, date, time);
        }
        return isAvailable;
    }
}
