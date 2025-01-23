package com.authentication.AuthenticationService.services;

import com.authentication.AuthenticationService.dto.MedicalHistoryDTO;
import com.authentication.AuthenticationService.dto.PatientDTO;
import com.authentication.AuthenticationService.models.MedicalHistory;
import com.authentication.AuthenticationService.models.Patient;
import com.authentication.AuthenticationService.repositories.MedicalHistoryRepository;
import com.authentication.AuthenticationService.repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private MedicalHistoryRepository medicalHistoryRepository;

    // Register Patient
    public String registerPatient(PatientDTO patientDTO) {
        Patient patient = new Patient();
        patient.setName(patientDTO.getName());
        patient.setAge(patientDTO.getAge());
        patient.setGender(patientDTO.getGender());
        patient.setContactDetails(patientDTO.getContactDetails());
        patient.setAddress(patientDTO.getAddress());
        patientRepository.save(patient);

        // Ensure MedicalHistory is set correctly
        if (patientDTO.getMedicalHistory() != null && !patientDTO.getMedicalHistory().isEmpty()) {
            List<MedicalHistory> medicalHistories = patientDTO.getMedicalHistory().stream()
                    .map(dto -> {
                        MedicalHistory history = new MedicalHistory();
                        history.setHistoryDetails(dto.getHistoryDetails());
                        history.setRecordDate(dto.getRecordDate());
                        history.setPatient(patient); // Link medical history to patient
                        return history;
                    })
                    .collect(Collectors.toList());

            patient.setMedicalHistory(medicalHistories);
            medicalHistoryRepository.saveAll(medicalHistories);  // Save all medical histories
        }

        return "Patient registered successfully with ID: " + patient.getPatientId();
    }

    public PatientDTO getPatient(Long id) {
        // Fetch the patient from the repository, throwing an exception if not found
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        // Create a new PatientDTO and set the properties
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setId(patient.getPatientId());
        patientDTO.setName(patient.getName());
        patientDTO.setAge(patient.getAge());
        patientDTO.setGender(patient.getGender());
        patientDTO.setContactDetails(patient.getContactDetails());
        patientDTO.setAddress(patient.getAddress());

        // Convert the patient's medical history to DTOs
        List<MedicalHistoryDTO> medicalHistoryDTOs = patient.getMedicalHistory().stream()
                .map(history -> {
                    MedicalHistoryDTO historyDTO = new MedicalHistoryDTO();
                    historyDTO.setId(history.getId());
                    // You don't need to set patientId anymore, as it's already managed by JPA
                    historyDTO.setHistoryDetails(history.getHistoryDetails());
                    historyDTO.setRecordDate(history.getRecordDate());
                    return historyDTO;
                })
                .collect(Collectors.toList());

        // Set the medical history DTOs in the PatientDTO
        patientDTO.setMedicalHistory(medicalHistoryDTOs);

        // Return the PatientDTO
        return patientDTO;
    }


    // Update Patient
    public String updatePatient(Long id, PatientDTO patientDTO) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        patient.setName(patientDTO.getName());
        patient.setAge(patientDTO.getAge());
        patient.setGender(patientDTO.getGender());
        patient.setContactDetails(patientDTO.getContactDetails());
        patient.setAddress(patientDTO.getAddress());
        patientRepository.save(patient);

        // Update MedicalHistory, if applicable
        if (patientDTO.getMedicalHistory() != null && !patientDTO.getMedicalHistory().isEmpty()) {
            List<MedicalHistory> medicalHistories = patientDTO.getMedicalHistory().stream()
                    .map(dto -> {
                        MedicalHistory history = new MedicalHistory();
                        history.setHistoryDetails(dto.getHistoryDetails());
                        history.setRecordDate(dto.getRecordDate());
                        history.setPatient(patient); // Link medical history to patient
                        return history;
                    })
                    .collect(Collectors.toList());
            medicalHistoryRepository.saveAll(medicalHistories);
        }

        return "Patient updated successfully";
    }

    // Get Patient's Medical History
    public List<MedicalHistoryDTO> getPatientHistory(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        // Fetch medical history using the repository method
        List<MedicalHistory> historyList = medicalHistoryRepository.findByPatient(patient);


        // Convert the list of MedicalHistory entities to MedicalHistoryDTOs
        return historyList.stream()
                .map(history -> {
                    MedicalHistoryDTO historyDTO = new MedicalHistoryDTO();
                    historyDTO.setId(history.getId());
                    historyDTO.setHistoryDetails(history.getHistoryDetails());
                    historyDTO.setRecordDate(history.getRecordDate());
                    historyDTO.setPatientId(history.getPatient().getPatientId());
                    return historyDTO;
                })
                .collect(Collectors.toList());
    }

    public List<Patient> getPatients() {
        return patientRepository.findAll();
    }
}
