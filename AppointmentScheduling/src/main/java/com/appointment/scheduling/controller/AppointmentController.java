package com.appointment.scheduling.controller;

import com.appointment.scheduling.dto.AppointmentDTO;
import com.appointment.scheduling.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentDTO bookAppointment(@RequestBody AppointmentDTO appointmentDTO) {
        return appointmentService.bookAppointment(appointmentDTO);
    }

    @GetMapping("/{id}")
    public AppointmentDTO getAppointmentDetails(@PathVariable Long id) {
        return appointmentService.getAppointmentDetails(id);
    }

    @PutMapping("/{id}")
    public AppointmentDTO rescheduleAppointment(@PathVariable Long id, @RequestBody AppointmentDTO updatedAppointmentDTO) {
        return appointmentService.rescheduleAppointment(id, updatedAppointmentDTO);
    }

    @DeleteMapping("/{id}")
    public void cancelAppointment(@PathVariable Long id) {
        appointmentService.cancelAppointment(id);
    }

    @GetMapping("/doctors/{doctorId}/availability")
    public boolean checkDoctorAvailability(@PathVariable Long doctorId, @RequestParam LocalDate date, @RequestParam LocalTime time) {
        return appointmentService.checkDoctorAvailability(doctorId, date, time);
    }
}
