package com.om.ems.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.om.ems.entity.Employee;
import com.om.ems.entity.Role;
import com.om.ems.entity.Users;
import com.om.ems.repository.EmployeeRepository;
import com.om.ems.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public Users registerUser(String username, String password, Role role, Long employeeId) {
        Employee emp = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Users user = new Users();
        user.setUsername(username);
        user.setPassword(password); // You should encode it
        user.setRole(role);
        user.setEmployee(emp);

        return userRepository.save(user);
    }
}

