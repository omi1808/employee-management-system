package com.om.ems.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.om.ems.dto.AuthRequest;
import com.om.ems.dto.AuthResponse;
import com.om.ems.entity.Employee;
import com.om.ems.entity.Role;
import com.om.ems.entity.Users;
import com.om.ems.repository.EmployeeRepository;
import com.om.ems.repository.UserRepository;
import com.om.ems.security.JwtUtil;
import com.om.ems.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private EmployeeRepository employeeRepository ;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(), authRequest.getPassword()
                )
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        final String token = jwtUtil.generateToken(userDetails);

        return new AuthResponse(token);
    }

    @PostMapping("/register")
    public Users registerUser(@RequestBody AuthRequest request) {
        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        // If the user role is EMPLOYEE, bind with Employee entity
        if (request.getRole() == Role.EMPLOYEE) {
            if (request.getEmployeeId() == null) {
                throw new IllegalArgumentException("Employee ID is required for EMPLOYEE role");
            }

            Employee emp = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + request.getEmployeeId()));

            user.setEmployee(emp);
        }

        return userRepository.save(user);
    }

}
