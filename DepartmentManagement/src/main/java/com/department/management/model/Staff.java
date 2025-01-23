package com.department.management.model;

import jakarta.persistence.*;

@Entity
@Table(name = "staff")
public class Staff {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name="StaffID")
   private int staffId;
   @Column(name="Name")
   private String staffName;
   @Column(name="Role")
   private String role;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Departments department;

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Departments getDepartment() {
        return department;
    }

    public void setDepartment(Departments department) {
        this.department = department;
    }

    public String getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }

    @Column(name ="AccessLevel")
   private String accessLevel;
}
