package com.department.management.repository;


import com.department.management.model.Departments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DepartmentRepo extends JpaRepository<Departments, Long> {
    Optional<Departments> findByDepartmentName(String departmentName);

}
