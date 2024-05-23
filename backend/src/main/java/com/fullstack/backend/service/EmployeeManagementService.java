package com.fullstack.backend.service;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fullstack.backend.dto.ReqRes;
import com.fullstack.backend.entity.Employee;
import com.fullstack.backend.repository.EmployeeRepo;

@Service
public class EmployeeManagementService {

	@Autowired
	private EmployeeRepo employeeRepo;

	@Autowired
	private JWTUtils jwtUtils;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public ReqRes register(ReqRes registrationRequest
//			, String adminName
			) {

		ReqRes resp = new ReqRes();

		try {

			Employee employee = new Employee();
			employee.setEmail(registrationRequest.getEmail());
			employee.setRole(registrationRequest.getRole());
			employee.setFirstName(registrationRequest.getFirstName());
			employee.setMiddleName(registrationRequest.getMiddleName());
			employee.setEmpId(registrationRequest.getEmpId());

			employee.setLastname(registrationRequest.getLastname());
			employee.setFatherName(registrationRequest.getFatherName());
			employee.setMotherName(registrationRequest.getMotherName());
			employee.setQualification(registrationRequest.getQualification());

			employee.setDateOfBirth(registrationRequest.getDateOfBirth());
			employee.setPassword(registrationRequest.getPassword());

			employee.setJoiningDate(registrationRequest.getJoiningDate());
			employee.setReleavingDate(registrationRequest.getReleavingDate());
			employee.setDesignation(registrationRequest.getDesignation());

			employee.setSalary(registrationRequest.getSalary());
			employee.setAadhaarNumber(registrationRequest.getAadhaarNumber());
			employee.setAddress(registrationRequest.getAddress());

			employee.setPanNumber(registrationRequest.getPanNumber());

			employee.setRemark(registrationRequest.getRemark());

			employee.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
//			employee.setRegisteredBy(adminName);

			employee.setRegisterDate(new Date(System.currentTimeMillis()));
			Employee ourUsersResult = employeeRepo.save(employee);

			if (ourUsersResult.getId() > 0) {

				resp.setEmployee((ourUsersResult));
				resp.setMessage("User Saved Sucessfully");
				resp.setStatusCode(200);

			}

		} catch (Exception e) {

			resp.setStatusCode(500);
			resp.setError(e.getMessage());
		}

		return resp;

	}

	public ReqRes login(ReqRes loginRequest) {
		ReqRes response = new ReqRes();
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
			var user = employeeRepo.findByEmail(loginRequest.getEmail()).orElseThrow();
			var jwt = jwtUtils.generateToken(user);
			var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
			response.setStatusCode(200);
			response.setToken(jwt);
			response.setRole(user.getRole());
			response.setRefreshToken(refreshToken);
			response.setExpirationTime("24Hrs");
			response.setMessage("Successfully Logged In");

		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage(e.getMessage());
		}
		return response;
	}

	public ReqRes refreshToken(ReqRes refreshTokenReqiest) {
		ReqRes response = new ReqRes();
		try {
			String ourEmail = jwtUtils.extractUsername(refreshTokenReqiest.getToken());
			Employee users = employeeRepo.findByEmail(ourEmail).orElseThrow();
			if (jwtUtils.isTokenValid(refreshTokenReqiest.getToken(), users)) {
				var jwt = jwtUtils.generateToken(users);
				response.setStatusCode(200);
				response.setToken(jwt);
				response.setRefreshToken(refreshTokenReqiest.getToken());
				response.setExpirationTime("24Hr");
				response.setMessage("Successfully Refreshed Token");
			}
			response.setStatusCode(200);
			return response;

		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage(e.getMessage());
			return response;
		}
	}

	public ReqRes getAllUsers() {
		ReqRes reqRes = new ReqRes();

		try {
			List<Employee> result = employeeRepo.findAll();
			if (!result.isEmpty()) {
				reqRes.setEmployeeList(result);
				reqRes.setStatusCode(200);
				reqRes.setMessage("Successful");
			} else {
				reqRes.setStatusCode(404);
				reqRes.setMessage("No users found");
			}
			return reqRes;
		} catch (Exception e) {
			reqRes.setStatusCode(500);
			reqRes.setMessage("Error occurred: " + e.getMessage());
			return reqRes;
		}
	}

	public ReqRes getUsersById(Long id) {
		ReqRes reqRes = new ReqRes();
		try {
			Employee usersById = employeeRepo.findById(id).orElseThrow(() -> new RuntimeException("User Not found"));
			reqRes.setEmployee(usersById);
			reqRes.setStatusCode(200);
			reqRes.setMessage("Users with id '" + id + "' found successfully");
		} catch (Exception e) {
			reqRes.setStatusCode(500);
			reqRes.setMessage("Error occurred: " + e.getMessage());
		}
		return reqRes;
	}
	
	public ReqRes getUsersByEmpId(String empId) {
        ReqRes reqRes = new ReqRes();
        try {
        	Employee user = employeeRepo.findByEmpId(empId)
                    .orElseThrow(() -> new RuntimeException("User Not found"));
            reqRes.setEmployee(user);
            reqRes.setStatusCode(200);
            reqRes.setMessage("User with empId '" + empId + "' found successfully");
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
        }
        return reqRes;
    }

	public ReqRes deleteUser(Long userId) {
		ReqRes reqRes = new ReqRes();
		try {
			Optional<Employee> userOptional = employeeRepo.findById(userId);
			if (userOptional.isPresent()) {
				employeeRepo.deleteById(userId);
				reqRes.setStatusCode(200);
				reqRes.setMessage("User deleted successfully");
			} else {
				reqRes.setStatusCode(404);
				reqRes.setMessage("User not found for deletion");
			}
		} catch (Exception e) {
			reqRes.setStatusCode(500);
			reqRes.setMessage("Error occurred while deleting user: " + e.getMessage());
		}
		return reqRes;
	}

	public ReqRes updateUser(Long userId, Employee updatedUser) {
		ReqRes reqRes = new ReqRes();
		try {
			Optional<Employee> userOptional = employeeRepo.findById(userId);
			if (userOptional.isPresent()) {
				Employee existingUser = userOptional.get();
				existingUser.setEmail(updatedUser.getEmail());
				existingUser.setEmpId(updatedUser.getEmpId());
				existingUser.setFirstName(updatedUser.getFirstName());
				existingUser.setMiddleName(updatedUser.getFirstName());
				existingUser.setLastname(updatedUser.getLastname());
				existingUser.setFatherName(updatedUser.getFatherName());
				existingUser.setMotherName(updatedUser.getMotherName());
				existingUser.setDesignation(updatedUser.getDesignation());
				existingUser.setDateOfBirth(updatedUser.getDateOfBirth());
				existingUser.setAddress(updatedUser.getAddress());
				existingUser.setJoiningDate(updatedUser.getJoiningDate());
				existingUser.setSalary(updatedUser.getSalary());
				existingUser.setQualification(updatedUser.getQualification());
				existingUser.setReleavingDate(updatedUser.getReleavingDate());
				existingUser.setAadhaarNumber(updatedUser.getAadhaarNumber());
				existingUser.setPanNumber(updatedUser.getPanNumber());
				existingUser.setRemark(updatedUser.getRemark());
				existingUser.setRole(updatedUser.getRole());

				// Check if password is present in the request
				if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
					// Encode the password and update it
					existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
				}

				Employee savedUser = employeeRepo.save(existingUser);
				reqRes.setEmployee(savedUser);
				reqRes.setStatusCode(200);
				reqRes.setMessage("User updated successfully");
			} else {
				reqRes.setStatusCode(404);
				reqRes.setMessage("User not found for update");
			}
		} catch (Exception e) {
			reqRes.setStatusCode(500);
			reqRes.setMessage("Error occurred while updating user: " + e.getMessage());
		}
		return reqRes;
	}

	public ReqRes getMyInfo(String email) {
		ReqRes reqRes = new ReqRes();
		try {
			Optional<Employee> userOptional = employeeRepo.findByEmail(email);
			if (userOptional.isPresent()) {
				reqRes.setEmployee(userOptional.get());
				reqRes.setStatusCode(200);
				reqRes.setMessage("successful");
			} else {
				reqRes.setStatusCode(404);
				reqRes.setMessage("User not found for update");
			}

		} catch (Exception e) {
			reqRes.setStatusCode(500);
			reqRes.setMessage("Error occurred while getting user info: " + e.getMessage());
		}
		return reqRes;

	}

}
