package com.medical.testing.MedicalTestingMicro.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import java.sql.Date;

@Entity
@Table(name="medicaltests")
public class MedicalFom {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    @Getter
    @Setter
    private int TestID;

    @Getter
    @Setter
    private String TestType;

    @Getter
    @Setter
    private String Result;

    @Getter
    @Setter
    @CreatedDate
    private Date date;

    @ManyToOne
    @JoinColumn(name = "docter_docter_id")
    private Docter docter;
    @ManyToOne
    @JoinColumn(name = "patient_patient_id")
    private Patient patient;

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Docter getDocter() {
        return docter;
    }

    public void setDocter(Docter docter) {
        this.docter = docter;
    }
}
