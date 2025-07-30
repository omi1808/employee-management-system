package com.om.ems.service;

import com.om.ems.entity.Attendance;
import com.om.ems.entity.Employee;
import com.om.ems.repository.AttendanceRepository;
import com.om.ems.repository.EmployeeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public Attendance markCheckIn(Long employeeId) {
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        if (employeeOpt.isEmpty()) {
            throw new RuntimeException("Employee not found with ID: " + employeeId);
        }

        LocalDate today = LocalDate.now();
        Optional<Attendance> todayAttendanceOpt = attendanceRepository.findByEmployeeIdAndDate(employeeId, today);

        if (todayAttendanceOpt.isPresent()) {
            throw new RuntimeException("Already checked in today.");
        }

        Attendance attendance = new Attendance();
        attendance.setEmployee(employeeOpt.get());
        attendance.setDate(today);
        attendance.setCheckInTime(LocalTime.now());

        return attendanceRepository.save(attendance);
    }

    public Attendance markCheckOut(Long employeeId) {
        LocalDate today = LocalDate.now();
        Optional<Attendance> todayAttendanceOpt = attendanceRepository.findByEmployeeIdAndDate(employeeId, today);

        if (todayAttendanceOpt.isEmpty()) {
            throw new RuntimeException("Check-in not found for today.");
        }

        Attendance attendance = todayAttendanceOpt.get();

        if (attendance.getCheckOutTime() != null) {
            throw new RuntimeException("Already checked out today.");
        }

        attendance.setCheckOutTime(LocalTime.now());
        return attendanceRepository.save(attendance);
    }

    public List<Attendance> getAttendanceByEmployee(Long employeeId) {
        return attendanceRepository.findByEmployeeId(employeeId);
    }

    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }
}
