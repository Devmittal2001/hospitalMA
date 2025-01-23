package com.department.management.controller;

import com.department.management.model.Departments;
import com.department.management.model.Staff;
import com.department.management.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @PostMapping("/add")
    public String registerDepartment(@RequestBody Departments department) {
       return departmentService.add(department);
    }
    @GetMapping("/")
    public List<Departments> getDepartments() {
         return departmentService.getAll();
    }
    @PutMapping("/updateDepartment")
    public String updateDepartment(@RequestParam long id, @RequestBody HashMap<String, String> map) {
        return departmentService.updateDepartment(id,map.get("name"));
    }

    @GetMapping("/department/{id}/staff")
    public List<Staff> getStaff(@PathVariable long id) {
        return departmentService.getStaff(id);
    }


}
