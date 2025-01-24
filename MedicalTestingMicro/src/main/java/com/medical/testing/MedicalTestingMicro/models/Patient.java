package com.medical.testing.MedicalTestingMicro.models;


import com.medical.testing.MedicalTestingMicro.models.enums.GenderEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private Long patientId;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private int age;
    @Getter
    @Setter
    private GenderEnum gender;
    @Getter
    @Setter
    private String contactDetails;
    @Getter
    @Setter
    private String address;
    @Getter
    @Setter
    private String medicalHistory;

}
