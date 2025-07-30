package com.om.ems.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.om.ems.entity.Employee;
import com.om.ems.entity.Users;
import com.om.ems.repository.EmployeeRepository;
import com.om.ems.repository.UserRepository;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private UserRepository userRepository;  // Inject UserRepository here
    
    public Employee addEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }
    
    public List<Employee> getAllEmployees(){
        return employeeRepository.findAll();
    }
    
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }
    
    public Employee updateEmployee(Long id, Employee updatedEmployee) {
        Optional<Employee> optionalEmp = employeeRepository.findById(id);
        if(optionalEmp.isPresent()) {
            Employee emp = optionalEmp.get();
            emp.setName(updatedEmployee.getName());
            emp.setDepartment(updatedEmployee.getDepartment());
            emp.setSalary(updatedEmployee.getSalary());
            return employeeRepository.save(emp);
        } else {
            throw new RuntimeException("Employee Not found with id : " + id);
        }
    }

    public Employee getByUsername(String username) {
        Users user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Employee employee = user.getEmployee();
        if (employee == null) {
            throw new RuntimeException("Employee not linked to user");
        }
        return employee;
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }
}
