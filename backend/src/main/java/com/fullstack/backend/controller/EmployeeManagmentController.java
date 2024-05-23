package com.fullstack.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.fullstack.backend.dto.ReqRes;
import com.fullstack.backend.entity.Employee;
import com.fullstack.backend.service.EmployeeManagementService;
//	import com.fullstack.backend.service.UsersManagementService;

@RestController
public class EmployeeManagmentController {
	
	@Autowired
	private EmployeeManagementService employeeManagementService;

	@PostMapping("/auth/register")
	public ResponseEntity<ReqRes> regeister(@RequestBody ReqRes reg
//			, @AuthenticationPrincipal UserDetails adminDetails
			) {
//		String adminName = adminDetails.getUsername();
		return ResponseEntity.ok(employeeManagementService.register(reg
//				, adminName
				));
	}

	@PostMapping("/auth/login")
	public ResponseEntity<ReqRes> login(@RequestBody ReqRes req) {
		return ResponseEntity.ok(employeeManagementService.login(req));
	}

	@PostMapping("/auth/refresh")
	public ResponseEntity<ReqRes> refreshToken(@RequestBody ReqRes req) {
		return ResponseEntity.ok(employeeManagementService.refreshToken(req));
	}

	@GetMapping("/admin/get-all-users")
	public ResponseEntity<ReqRes> getAllUsers() {
		return ResponseEntity.ok(employeeManagementService.getAllUsers());

	}

	@GetMapping("/admin/get-users/{userId}")
	public ResponseEntity<ReqRes> getUSerByID(@PathVariable Long userId) {
		return ResponseEntity.ok(employeeManagementService.getUsersById(userId));

	}
	
	@GetMapping("/admin/get-users-by-empid/{empId}")
    public ResponseEntity<ReqRes> getUserByEmpId(@PathVariable String empId) {
        return ResponseEntity.ok(employeeManagementService.getUsersByEmpId(empId));
    }

	@PutMapping("/admin/update/{userId}")
	public ResponseEntity<ReqRes> updateUser(@PathVariable Long userId, @RequestBody Employee reqres) {
		return ResponseEntity.ok(employeeManagementService.updateUser(userId, reqres));
	}

	@GetMapping("/adminuser/get-profile")
	public ResponseEntity<ReqRes> getMyProfile() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();
		ReqRes response = employeeManagementService.getMyInfo(email);
		return ResponseEntity.status(response.getStatusCode()).body(response);
	}

	@DeleteMapping("/admin/delete/{userId}")
	public ResponseEntity<ReqRes> deleteUSer(@PathVariable Long userId) {
		return ResponseEntity.ok(employeeManagementService.deleteUser(userId));
	}
	
	

}
