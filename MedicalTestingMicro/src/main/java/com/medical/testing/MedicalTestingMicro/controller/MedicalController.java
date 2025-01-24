package com.medical.testing.MedicalTestingMicro.controller;

import com.medical.testing.MedicalTestingMicro.dto.MedicalDataResponse;
import com.medical.testing.MedicalTestingMicro.models.MedicalFom;
import com.medical.testing.MedicalTestingMicro.services.MedicalServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medical")
public class MedicalController {

    @Autowired
    private MedicalServices services;

    @GetMapping("/medical-data/{medicalFormId}")
    @ResponseBody
    public MedicalDataResponse getMedicalData(@PathVariable int medicalFormId) {
        return services.getMedicalData(medicalFormId);
    }
    @GetMapping("")
    public List<MedicalFom> getAllMedicalData() {
        return services.getAllMedicalData();
    }
}
