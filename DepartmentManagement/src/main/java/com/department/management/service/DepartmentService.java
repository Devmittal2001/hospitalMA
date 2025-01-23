package com.department.management.service;

import com.department.management.model.Departments;
import com.department.management.model.Staff;
import com.department.management.repository.DepartmentRepo;
import com.department.management.repository.StaffRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentService.class);

    @Autowired
    private DepartmentRepo departmentRepo;
    @Autowired
    private StaffRepo staffRepo;

    public String add(Departments department) {
        departmentRepo.save(department);
        return "New department is created";
    }

    public List<Departments> getAll() {
        return departmentRepo.findAll();
    }

    public String updateDepartment(Long id , String name) {
        Departments departments =departmentRepo.findById(id).orElse(null);
        departments.setDepartmentHead(name);
        departmentRepo.save(departments);
        return departments.getDepartmentName();
    }

    public List<Staff> getStaff(long id) {
     return (List<Staff>) staffRepo.findByDepartmentid(id);
    }
}
