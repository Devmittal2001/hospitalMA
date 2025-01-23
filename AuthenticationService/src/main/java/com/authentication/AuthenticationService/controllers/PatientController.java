package com.authentication.AuthenticationService.controllers;

import com.authentication.AuthenticationService.dto.MedicalHistoryDTO;
import com.authentication.AuthenticationService.dto.PatientDTO;
import com.authentication.AuthenticationService.models.Patient;
import com.authentication.AuthenticationService.response.ApiResponse;
import com.authentication.AuthenticationService.services.PatientService;
import com.authentication.AuthenticationService.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<ApiResponse> registerPatient(@RequestBody PatientDTO patientDTO) {
        String res = patientService.registerPatient(patientDTO);
        return new ResponseEntity<>(new ApiResponse(Constants.SUCCESS, null, res), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getPatient(@PathVariable Long id) {
        PatientDTO res = patientService.getPatient(id);
        return new ResponseEntity<>(new ApiResponse(Constants.SUCCESS, null, res), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<ApiResponse> updatePatient(@PathVariable Long id, @RequestBody PatientDTO patientDTO) {
        String res = patientService.updatePatient(id, patientDTO);
        return new ResponseEntity<>(new ApiResponse(Constants.SUCCESS, null, res), HttpStatus.OK);
    }

    @GetMapping("/{id}/history")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getPatientHistory(@PathVariable Long id) {
        List<MedicalHistoryDTO> res = patientService.getPatientHistory(id);
        return new ResponseEntity<>(new ApiResponse(Constants.SUCCESS, null, res), HttpStatus.OK);
    }

    @GetMapping("")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getPatients() {
        List<Patient> res = patientService.getPatients();
        return new ResponseEntity<>(new ApiResponse(Constants.SUCCESS, null, res), HttpStatus.OK);
    }
}