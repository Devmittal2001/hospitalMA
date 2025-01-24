package com.medical.testing.MedicalTestingMicro.services;

import com.medical.testing.MedicalTestingMicro.models.*;
import com.medical.testing.MedicalTestingMicro.dto.*;
import com.medical.testing.MedicalTestingMicro.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class MedicalServices {

    @Autowired
    private MedicalRepo medicalFormRepository;
    
    public MedicalDataResponse getMedicalData(int medicalFormId) {
        MedicalDataResponse response = new MedicalDataResponse();
        Optional<MedicalFom> medicalFormOptional = medicalFormRepository.findById(medicalFormId);
        if (medicalFormOptional.isPresent()) {
            MedicalFom medicalForm = medicalFormOptional.get();
            response.setMedicalForm(medicalForm);
            response.setPatient(medicalForm.getPatient());
            response.setDoctor(medicalForm.getDocter());
        }
        return response;
    }
    public List<MedicalFom> getAllMedicalData(){
        return medicalFormRepository.findAll();
    }
}