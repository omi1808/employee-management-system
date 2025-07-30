package com.om.ems.controller;

import com.om.ems.entity.Attendance;
import com.om.ems.entity.Employee;
import com.om.ems.service.AttendanceService;
import com.om.ems.service.EmployeeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private EmployeeService employeeService;

    // Employee can only check-in themselves
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/checkin")
    @ResponseStatus(HttpStatus.CREATED)
    public Attendance checkIn(Authentication authentication) {
        String username = authentication.getName();
        Employee employee = employeeService.getByUsername(username);
        return attendanceService.markCheckIn(employee.getId());
    }

    // Employee can only check-out themselves
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    public Attendance checkOut(Authentication authentication) {
        String username = authentication.getName();
        Employee employee = employeeService.getByUsername(username);
        return attendanceService.markCheckOut(employee.getId());
    }

    // Admin and Manager can get attendance by employee ID
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/employee/{employeeId}")
    public List<Attendance> getEmployeeAttendance(@PathVariable Long employeeId) {
        return attendanceService.getAttendanceByEmployee(employeeId);
    }

    // Admin can get all attendance records
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Attendance> getAllAttendance() {
        return attendanceService.getAllAttendance();
    }
}
