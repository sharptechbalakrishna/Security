package com.fullstack.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fullstack.backend.entity.Employee;

//	import com.fullstack.backend.entity.OurUsers;
//	UsersRepo

public interface EmployeeRepo extends JpaRepository<Employee, Long>{
	
	Optional<Employee> findByEmail(String email);
	Optional<Employee> findByEmpId(String empId);

}
