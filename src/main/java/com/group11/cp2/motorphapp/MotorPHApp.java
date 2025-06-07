package com.group11.cp2.motorphapp;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;

public class MotorPHApp {
    private static java.util.List<User> users;
    private static java.util.List<Employee> employees;
    private static java.util.List<AttendanceRecord> attendance;

    public static void main(String[] args) {
        // Initialize sample users with security questions and answers
        users = new ArrayList<>();
        Employee sampleEmployee = new Employee(10001, "Garcia", "Manuel III", LocalDate.of(1983, 10, 11),
                "Manager", "Regular", new CompensationDetails(30000, 1500, 1000, 2000, 15000, 535.73),
                new GovernmentDetails("123456789", "987654321", "111222333", "444555666"));
        users.add(new User("admin", "admin123", "Admin", sampleEmployee, 
                "What is your mother's maiden name?", "Smith"));
        users.add(new User("user", "user123", "Employee", sampleEmployee, 
                "What is your favorite color?", "Blue"));

        // Load data
        employees = CSVHandler.readEmployeesFromCSV("src/main/resources/employeedata.csv");
        CSVHandler csvHandler = new CSVHandler();
        attendance = csvHandler.readAttendanceCSV("src/main/resources/attendancerecord.csv");

        // Start with login screen
        SwingUtilities.invokeLater(() -> User.createLoginFrame(users));
    }

    public static List<AttendanceRecord> getAttendance() {
        return attendance;
    }

    public static void createMainFrame() {
        JFrame mainFrame = new JFrame("MotorPH Payroll System - Dashboard");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 600);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setLayout(new BorderLayout());

        // Tabs for employee management, payroll reports, and attendance
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Employee Management", Employee.getGUIPanel(employees));
        tabbedPane.addTab("Attendance Records", AttendanceRecord.createAttendancePanel(employees, User.getLoggedInUser()));
        tabbedPane.addTab("Payroll Reports", createPayrollPanel());
        
        

        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            mainFrame.dispose();
            User.createLoginFrame(users);
        });

        mainFrame.add(tabbedPane, BorderLayout.CENTER);
        mainFrame.add(logoutButton, BorderLayout.SOUTH);
        mainFrame.setVisible(true);
    }

    private static JPanel createPayrollPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        StringBuilder sb = new StringBuilder();
        Map<Integer, java.util.List<AttendanceRecord>> recordsByEmployee = attendance.stream()
                .collect(Collectors.groupingBy(AttendanceRecord::getEmployeeNumber));

        for (Employee emp : employees) {
            User loggedInUser = User.getLoggedInUser();
            if (!loggedInUser.isAdmin() && emp.getEmployeeNumber() != loggedInUser.getEmployee().getEmployeeNumber()) {
                continue;
            }

            java.util.List<AttendanceRecord> empAttendance = recordsByEmployee.getOrDefault(emp.getEmployeeNumber(), java.util.Collections.emptyList());
            if (empAttendance.isEmpty()) {
                sb.append("No attendance records found for ").append(emp.getFirstName())
                        .append(" ").append(emp.getLastName()).append("\n");
                continue;
            }

            Map<Integer, java.util.List<AttendanceRecord>> recordsByWeek = empAttendance.stream()
                    .collect(Collectors.groupingBy(record -> record.getDate().get(WeekFields.ISO.weekOfWeekBasedYear())));

            java.util.List<Map.Entry<Integer, java.util.List<AttendanceRecord>>> sortedWeeks = recordsByWeek.entrySet().stream()
                    .sorted(Comparator.comparing(e -> {
                        LocalDate date = e.getValue().stream()
                                .map(AttendanceRecord::getDate)
                                .min(LocalDate::compareTo)
                                .orElse(LocalDate.now());
                        return date;
                    }))
                    .collect(Collectors.toList());

            for (Map.Entry<Integer, java.util.List<AttendanceRecord>> weekEntry : sortedWeeks) {
                java.util.List<AttendanceRecord> weekRecords = weekEntry.getValue();
                LocalDate firstDate = weekRecords.stream()
                        .map(AttendanceRecord::getDate)
                        .min(LocalDate::compareTo)
                        .orElse(LocalDate.now());
                LocalDate weekStart = firstDate.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
                LocalDate weekEnd = firstDate.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.FRIDAY));

                Duration totalRegular = weekRecords.stream()
                        .map(AttendanceRecord::getRegularDuration)
                        .reduce(Duration.ZERO, Duration::plus);
                Duration totalOvertime = weekRecords.stream()
                        .map(AttendanceRecord::getOvertimeDuration)
                        .reduce(Duration.ZERO, Duration::plus);
                Duration totalWorked = totalRegular.plus(totalOvertime);

                try {
                    PayrollReport payroll = new PayrollReport(emp, weekStart, weekEnd, totalWorked);
                    sb.append(payroll.toString()).append("\n");
                } catch (IllegalArgumentException e) {
                    sb.append("Error generating payroll for ").append(emp.getFirstName())
                            .append(" ").append(emp.getLastName())
                            .append(" (Week ").append(weekEntry.getKey()).append("): ")
                            .append(e.getMessage()).append("\n");
                }
            }
        }

        textArea.setText(sb.toString());
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        return panel;
    }

    private static void appendEmployeeDetails(StringBuilder sb, Employee emp) {
        sb.append("Employee Number: ").append(emp.getEmployeeNumber()).append("\n");
        sb.append("Name: ").append(emp.getLastName()).append(", ").append(emp.getFirstName()).append("\n");
        sb.append("Birthday: ").append(emp.getBirthday()).append("\n");
        sb.append("Status: ").append(emp.getEmploymentStatus()).append("\n");
        sb.append("Position: ").append(emp.getPosition()).append("\n");

        GovernmentDetails gov = emp.getGovernmentDetails();
        if (gov != null) {
            sb.append("SSS Number: ").append(gov.getSssNumber()).append("\n");
            sb.append("PhilHealth Number: ").append(gov.getPhilHealthNumber()).append("\n");
            sb.append("TIN: ").append(gov.getTinNumber()).append("\n");
            sb.append("Pag-IBIG Number: ").append(gov.getPagIbigNumber()).append("\n");
        }

        CompensationDetails comp = emp.getCompensation();
        if (comp != null) {
            sb.append("Basic Salary: ").append(comp.getBasicSalary()).append("\n");
            sb.append("Rice Subsidy: ").append(comp.getRiceSubsidy()).append("\n");
            sb.append("Phone Allowance: ").append(comp.getPhoneAllowance()).append("\n");
            sb.append("Clothing Allowance: ").append(comp.getClothingAllowance()).append("\n");
            sb.append("Gross Semi-Monthly Rate: ").append(comp.getGrossSemiMonthlyRate()).append("\n");
            sb.append("Hourly Rate: ").append(comp.getHourlyRate()).append("\n");
        }
    }
}