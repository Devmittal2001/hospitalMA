package com.appointment.scheduling.mapper;

import com.appointment.scheduling.dao.Patient;
import com.appointment.scheduling.dto.AppointmentDTO;
import com.appointment.scheduling.model.Appointment;
import com.appointment.scheduling.model.Doctor;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public AppointmentDTO toDTO(Appointment appointment) {
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setAppointmentId(appointment.getAppointmentId());
        appointmentDTO.setDate(appointment.getDate());
        appointmentDTO.setTime(appointment.getTime());
        appointmentDTO.setPatientId(appointment.getPatient().getPatientId());
        appointmentDTO.setDoctorId(appointment.getDoctor().getDoctorId());
        return appointmentDTO;
    }

    public Appointment toEntity(AppointmentDTO appointmentDTO) {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(appointmentDTO.getAppointmentId());
        appointment.setDate(appointmentDTO.getDate());
        appointment.setTime(appointmentDTO.getTime());
        // Assuming we have methods to get Patient and Doctor entities from IDs
        appointment.setPatient(new Patient(appointmentDTO.getPatientId()));
        appointment.setDoctor(new Doctor(appointmentDTO.getDoctorId()));
        return appointment;
    }
}

