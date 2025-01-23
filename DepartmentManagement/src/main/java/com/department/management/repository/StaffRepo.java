package com.department.management.repository;

import com.department.management.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffRepo extends JpaRepository<Staff, Integer> {
    public Staff findByDepartmentid(long departmentid);
}
