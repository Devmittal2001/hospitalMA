package com.medical.testing.MedicalTestingMicro.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Docter {
       @Id
       @Getter
       @Setter
       private int docterID;
       @Getter
       @Setter
       private String docterName;
       @Getter
       @Setter
       private String contactDetails;
       @Setter
       @Getter
       private String Specialization;
       @Getter
       @Setter
       private int departmentID;

}
