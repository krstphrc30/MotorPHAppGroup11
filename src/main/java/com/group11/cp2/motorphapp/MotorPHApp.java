/**
 * Main application class for the MotorPH Payroll System.
 *
 * @author Kristopher Carlo, Clarinda, Pil, Janice (Group 11)
 */
package com.group11.cp2.motorphapp;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MotorPHApp {
    private static List<User> users = new ArrayList<>();
    private static List<Employee> employees = new ArrayList<>();
    private static List<AttendanceRecord> attendance = new ArrayList<>();

    /**
     * Main entry point for the application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        try {
            employees = CSVHandler.readEmployeesFromCSV("src/main/resources/employeedata.csv");
            attendance = CSVHandler.readAttendanceCSV("src/main/resources/attendancerecord.csv");
            System.out.println("Loaded " + employees.size() + " employees from CSV");
            System.out.println("Loaded " + attendance.size() + " attendance records from CSV");
        } catch (Exception e) {
            System.err.println("Error loading CSV files: " + e.getMessage());
        }

        if (employees.isEmpty()) {
            System.out.println("No employees loaded, adding test employee");
            GovernmentDetails gov = new GovernmentDetails("123456789", "987654321", "111222333", "444555666");
            CompensationDetails comp = new CompensationDetails(50000.0, 1000.0, 500.0, 300.0, 25000.0, 250.0);
            employees.add(new Employee(10001, "Doe", "John", LocalDate.of(1990, 1, 1), "Staff", "Active", comp, gov));
        }

        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/users.csv"))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    String[] header = parseCSVLine(line).toArray(new String[0]);
                    if (header.length != 2) {
                        throw new IOException("Invalid header in users.csv: expected exactly 2 columns (username,password)");
                    }
                    String[] expectedHeader = {"username", "password"};
                    for (int i = 0; i < expectedHeader.length; i++) {
                        if (!header[i].trim().equalsIgnoreCase(expectedHeader[i])) {
                            throw new IOException("Invalid header in users.csv: expected " + expectedHeader[i] + ", found " + header[i]);
                        }
                    }
                    isFirstLine = false;
                    continue;
                }

                List<String> values = parseCSVLine(line);

                if (values.size() == 2) {
                    try {
                        String username = values.get(0).trim();
                        String password = values.get(1).trim();

                        if (username.isEmpty()) {
                            System.err.println("Empty username in line: " + line);
                            continue;
                        }
                        if (password.isEmpty()) {
                            System.err.println("Empty password in line: " + line);
                            continue;
                        }

                        String email = username.equals("admin") ? "admin@motorph.com" : username + "@motorph.com";
                        String role = username.equals("admin") ? "Admin" : "Employee";
                        String status = "Active";

                        users.add(new User(username, password, email, null, role, status));
                    } catch (Exception e) {
                        System.err.println("Error parsing user line: " + line + ", Error: " + e.getMessage());
                    }
                } else {
                    System.err.println("Invalid user row (expected 2 columns, found " + values.size() + "): " + line);
                }
            }
            System.out.println("Loaded " + users.size() + " users from CSV");
        } catch (IOException e) {
            System.err.println("Error reading users CSV: " + e.getMessage());
            System.out.println("No users loaded, adding default admin user");
            users.add(new User("admin", "admin123", "admin@motorph.com", null, "Admin", "Active"));
            JOptionPane.showMessageDialog(null, 
                "Failed to load user data: " + e.getMessage() + ". Using default admin user.", 
                "CSV Error", JOptionPane.ERROR_MESSAGE);
        }

        java.awt.EventQueue.invokeLater(() -> {
            new UserLogin(users).setVisible(true);
        });
    }

    /**
     * Parses a CSV line into a list of values.
     *
     * @param line The CSV line to parse.
     * @return List of parsed values.
     */
    private static List<String> parseCSVLine(String line) {
        List<String> tokens = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString());
        return tokens;
    }

    /**
     * Gets the list of users.
     *
     * @return List of users.
     */
    public static List<User> getUsers() {
        return users;
    }

    /**
     * Gets the list of employees.
     *
     * @return List of employees.
     */
    public static List<Employee> getEmployees() {
        return employees;
    }

    /**
     * Gets the list of attendance records.
     *
     * @return List of attendance records.
     */
    public static List<AttendanceRecord> getAttendance() {
        return attendance;
    }
}