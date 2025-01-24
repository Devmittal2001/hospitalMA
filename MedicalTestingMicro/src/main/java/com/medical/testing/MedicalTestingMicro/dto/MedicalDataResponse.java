package com.medical.testing.MedicalTestingMicro.dto;

import com.medical.testing.MedicalTestingMicro.models.*;

public class MedicalDataResponse {
    private MedicalFom medicalForm;
    private Patient patient;
    private Docter doctor;

    // Getters and Setters
    public MedicalFom getMedicalForm() {
        return medicalForm;
    }

    public void setMedicalForm(MedicalFom medicalForm) {
        this.medicalForm = medicalForm;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Docter getDoctor() {
        return doctor;
    }

    public void setDoctor(Docter doctor) {
        this.doctor = doctor;
    }
}
