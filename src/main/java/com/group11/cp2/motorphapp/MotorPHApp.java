package com.group11.cp2.motorphapp;

import javax.swing.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MotorPHApp {
    private static List<User> users = new ArrayList<>();
    private static List<Employee> employees = new ArrayList<>();
    private static List<AttendanceRecord> attendance = new ArrayList<>();

    public static void main(String[] args) {
        // Load data from CSV files
        try {
            employees = CSVHandler.readEmployeesFromCSV("src/main/resources/employeedata.csv");
            attendance = CSVHandler.readAttendanceCSV("src/main/resources/attendancerecord.csv");
            System.out.println("Loaded " + employees.size() + " employees from CSV");
            System.out.println("Loaded " + attendance.size() + " attendance records from CSV");
        } catch (Exception e) {
            System.err.println("Error loading CSV files: " + e.getMessage());
        }

        // Fallback: Add a hardcoded employee if CSV is empty
        if (employees.isEmpty()) {
            System.out.println("No employees loaded, adding test employee");
            GovernmentDetails gov = new GovernmentDetails("123456789", "987654321", "111222333", "444555666");
            CompensationDetails comp = new CompensationDetails(50000.0, 1000.0, 500.0, 300.0, 25000.0, 250.0);
            employees.add(new Employee(10001, "Doe", "John", LocalDate.of(1990, 1, 1), "Staff", "Active", comp, gov));
        }

        // Create users from employees
        for (Employee emp : employees) {
            users.add(new User(
                emp.getFirstName().toLowerCase() + emp.getEmployeeNumber(),
                "password123",
                emp.getFirstName().toLowerCase() + "@motorph.com",
                emp,
                emp.getEmployeeNumber() == 10001 ? "Admin" : "Employee",
                "Active"
            ));
        }
        users.add(new User("admin", "admin123", "admin@motorph.com", null, "Admin", "Active"));

        // Start with login frame
        java.awt.EventQueue.invokeLater(() -> {
            new UserLogin(users).setVisible(true);
        });
    }

    public static List<User> getUsers() {
        return users;
    }

    public static List<Employee> getEmployees() {
        return employees;
    }

    public static List<AttendanceRecord> getAttendance() {
        return attendance;
    }
}