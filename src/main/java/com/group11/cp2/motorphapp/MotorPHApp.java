package com.group11.cp2.motorphapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.YearMonth;
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
        User.createLoginFrame(users);
    }

    public static void createMainFrame() {
        System.setProperty("sun.java2d.uiScale", "1.0"); // Fix DPI scaling
        User loggedInUser = User.getLoggedInUser();
        if (loggedInUser == null) {
            JOptionPane.showMessageDialog(null, "No user logged in.", "Error", JOptionPane.ERROR_MESSAGE);
            User.createLoginFrame(users);
            return;
        }
        System.out.println("Logged in: " + loggedInUser.getUsername());

        // Instantiate JForm-generated Dashboard
        Dashboard dashboard = new Dashboard();
        dashboard.setTitle("MotorPH Payroll System");
        dashboard.setLocationRelativeTo(null);

        try {
            // Access protected fields directly
            JTable jTable4 = dashboard.jTable4;
            JScrollPane jScrollPane4 = dashboard.jScrollPane4;
            JButton jButton6 = dashboard.jButton6; // Add Employee
            JButton jButton7 = dashboard.jButton7; // View Employee
            JButton jButton8 = dashboard.jButton8; // Update
            JButton jButton26 = dashboard.jButton26; // Delete
            JButton jButton9 = dashboard.jButton9; // Logout
            JButton jButton12 = dashboard.jButton12; // Save
            JTextField jTextField7 = dashboard.jTextField7; // PhilHealth
            JTextField jTextField8 = dashboard.jTextField8; // Employee Number
            JTextField jTextField9 = dashboard.jTextField9; // Last Name
            JTextField jTextField10 = dashboard.jTextField10; // First Name
            JTextField jTextField11 = dashboard.jTextField11; // SSS
            JTextField jTextField12 = dashboard.jTextField12; // Pag-IBIG
            JTextField jTextField13 = dashboard.jTextField13; // TIN

            // Adjust button sizes to ensure full text is visible
            jButton7.setBounds(680, 950, 150, 30); // View Employee: width 150
            jButton6.setBounds(840, 950, 150, 30); // Add Employee: width 150
            jButton8.setBounds(1000, 950, 100, 30); // Update: width 100
            jButton26.setBounds(1110, 950, 100, 30); // Delete: width 100
            jButton9.setBounds(1680, 950, 100, 30); // LOGOUT: width 100

            // Configure jTable4
            jTable4.setRowSelectionAllowed(true);
            jTable4.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            jTable4.getColumnModel().getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            if (jTable4.getColumnModel().getColumnCount() >= 7) {
                jTable4.getColumnModel().getColumn(0).setPreferredWidth(120);
                jTable4.getColumnModel().getColumn(1).setPreferredWidth(200);
                jTable4.getColumnModel().getColumn(2).setPreferredWidth(200);
                jTable4.getColumnModel().getColumn(3).setPreferredWidth(120);
                jTable4.getColumnModel().getColumn(4).setPreferredWidth(120);
                jTable4.getColumnModel().getColumn(5).setPreferredWidth(120);
                jTable4.getColumnModel().getColumn(6).setPreferredWidth(120);
            }

            // Populate jTable4
            DefaultTableModel tableModel = (DefaultTableModel) jTable4.getModel();
            tableModel.setRowCount(0);
            for (Employee emp : employees) {
                GovernmentDetails gov = emp.getGovernmentDetails();
                tableModel.addRow(new Object[]{
                    emp.getEmployeeNumber(),
                    emp.getLastName(),
                    emp.getFirstName(),
                    gov != null ? gov.getSssNumber() : "N/A",
                    gov != null ? gov.getPhilHealthNumber() : "N/A",
                    gov != null ? gov.getTinNumber() : "N/A",
                    gov != null ? gov.getPagIbigNumber() : "N/A"
                });
            }

            // Enable buttons and set text field properties
            jButton6.setEnabled(true);
            jButton7.setEnabled(true);
            jButton8.setEnabled(true);
            jButton26.setEnabled(true);
            jButton12.setEnabled(true);
            jTextField8.setEditable(false);

            // Remove existing action listeners
            for (ActionListener al : jButton6.getActionListeners()) {
                jButton6.removeActionListener(al);
            }
            for (ActionListener al : jButton7.getActionListeners()) {
                jButton7.removeActionListener(al);
            }
            for (ActionListener al : jButton8.getActionListeners()) {
                jButton8.removeActionListener(al);
            }
            for (ActionListener al : jButton26.getActionListeners()) {
                jButton26.removeActionListener(al);
            }
            for (ActionListener al : jButton9.getActionListeners()) {
                jButton9.removeActionListener(al);
            }
            for (ActionListener al : jButton12.getActionListeners()) {
                jButton12.removeActionListener(al);
            }

            // Row selection listener
            jTable4.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = jTable4.getSelectedRow();
                    if (selectedRow >= 0) {
                        jTextField8.setText(String.valueOf(tableModel.getValueAt(selectedRow, 0)));
                        jTextField9.setText(String.valueOf(tableModel.getValueAt(selectedRow, 1)));
                        jTextField10.setText(String.valueOf(tableModel.getValueAt(selectedRow, 2)));
                        jTextField11.setText(String.valueOf(tableModel.getValueAt(selectedRow, 3)));
                        jTextField7.setText(String.valueOf(tableModel.getValueAt(selectedRow, 4)));
                        jTextField13.setText(String.valueOf(tableModel.getValueAt(selectedRow, 5)));
                        jTextField12.setText(String.valueOf(tableModel.getValueAt(selectedRow, 6)));
                    } else {
                        jTextField8.setText("");
                        jTextField9.setText("");
                        jTextField10.setText("");
                        jTextField11.setText("");
                        jTextField7.setText("");
                        jTextField13.setText("");
                        jTextField12.setText("");
                    }
                }
            });

            // Add Employee
            jButton6.addActionListener(e -> {
                try {
                    NewEmployeeRecord addFrame = new NewEmployeeRecord(employees, tableModel);
                    addFrame.setLocationRelativeTo(dashboard);
                    addFrame.setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dashboard, "Error opening add form: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            // View Employee
            jButton7.addActionListener(e -> {
                int selectedRow = jTable4.getSelectedRow();
                if (selectedRow >= 0) {
                    int empNumber = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                    Employee emp = employees.stream()
                            .filter(employee -> employee.getEmployeeNumber() == empNumber)
                            .findFirst()
                            .orElse(null);
                    if (emp != null) {
                        // Debug: Log PayrollFrame parameters
                        System.out.println("Calling PayrollFrame for Employee: " + emp.getEmployeeNumber() + ", " + emp.getLastName());
                        System.out.println("Attendance records: " + (attendance != null ? attendance.size() : "null"));
                        PayrollFrame detailsFrame = new PayrollFrame(dashboard, emp, attendance);
                        detailsFrame.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(dashboard, "Employee not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(dashboard, "Please select an employee.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            // Update Employee
            jButton8.addActionListener(e -> {
                int selectedRow = jTable4.getSelectedRow();
                if (selectedRow >= 0) {
                    try {
                        int empNumber = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                        Employee emp = employees.stream()
                                .filter(employee -> employee.getEmployeeNumber() == empNumber)
                                .findFirst()
                                .orElse(null);
                        if (emp != null) {
                            UpdateEmployeeRecord updateFrame = new UpdateEmployeeRecord(
                                dashboard, emp, tableModel, selectedRow,
                                jTextField8, jTextField9, jTextField10, jTextField11,
                                jTextField7, jTextField13, jTextField12
                            );
                            updateFrame.setLocationRelativeTo(dashboard);
                            updateFrame.setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(dashboard, "Employee not found.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dashboard, "Error opening update form: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(dashboard, "Please select an employee.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            // Delete Employee
            jButton26.addActionListener(e -> {
                int selectedRow = jTable4.getSelectedRow();
                if (selectedRow >= 0) {
                    try {
                        int empNumber = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                        int confirm = JOptionPane.showConfirmDialog(dashboard, "Delete employee #" + empNumber + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            Employee emp = employees.stream()
                                    .filter(employee -> employee.getEmployeeNumber() == empNumber)
                                    .findFirst()
                                    .orElse(null);
                            if (emp != null) {
                                employees.remove(emp);
                                users.removeIf(user -> user.getEmployee() != null && user.getEmployee().getEmployeeNumber() == empNumber);
                                attendance.removeIf(record -> record.getEmployeeNumber() == empNumber);
                                tableModel.removeRow(selectedRow);
                                CSVHandler.writeEmployeesToCSV(employees, "src/main/resources/employeedata.csv");
                                CSVHandler.writeAttendanceToCSV(attendance, "src/main/resources/attendancerecord.csv");
                                jTextField8.setText("");
                                jTextField9.setText("");
                                jTextField10.setText("");
                                jTextField11.setText("");
                                jTextField7.setText("");
                                jTextField13.setText("");
                                jTextField12.setText("");
                                JOptionPane.showMessageDialog(dashboard, "Employee deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(dashboard, "Employee not found.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dashboard, "Error deleting employee: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(dashboard, "Please select an employee.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            // Save
            jButton12.addActionListener(e -> {
                int selectedRow = jTable4.getSelectedRow();
                if (selectedRow >= 0) {
                    try {
                        int empNumber = Integer.parseInt(jTextField8.getText().trim());
                        Employee emp = employees.stream()
                                .filter(employee -> employee.getEmployeeNumber() == empNumber)
                                .findFirst()
                                .orElse(null);
                        if (emp == null) {
                            JOptionPane.showMessageDialog(dashboard, "Employee not found.", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        String lastName = jTextField9.getText().trim();
                        String firstName = jTextField10.getText().trim();
                        String sssNumber = jTextField11.getText().trim();
                        String philHealthNumber = jTextField7.getText().trim();
                        String tinNumber = jTextField13.getText().trim();
                        String pagIbigNumber = jTextField12.getText().trim();

                        if (lastName.isEmpty() || firstName.isEmpty()) {
                            JOptionPane.showMessageDialog(dashboard, "Name fields cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        if (!sssNumber.matches("\\d{9,12}") || !philHealthNumber.matches("\\d{9,12}") ||
                            !tinNumber.matches("\\d{9,12}") || !pagIbigNumber.matches("\\d{9,12}")) {
                            JOptionPane.showMessageDialog(dashboard, "Invalid ID numbers (9–12 digits).", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        emp.setLastName(lastName);
                        emp.setFirstName(firstName);
                        GovernmentDetails gov = new GovernmentDetails(sssNumber, philHealthNumber, tinNumber, pagIbigNumber);
                        emp.setGovernmentDetails(gov);

                        User user = users.stream()
                                .filter(u -> u.getEmployee() != null && u.getEmployee().getEmployeeNumber() == empNumber)
                                .findFirst()
                                .orElse(null);
                        if (user != null) {
                            user.setEmployee(emp);
                            user.setUsername(firstName.toLowerCase() + empNumber);
                        }

                        tableModel.setValueAt(lastName, selectedRow, 1);
                        tableModel.setValueAt(firstName, selectedRow, 2);
                        tableModel.setValueAt(sssNumber, selectedRow, 3);
                        tableModel.setValueAt(philHealthNumber, selectedRow, 4);
                        tableModel.setValueAt(tinNumber, selectedRow, 5);
                        tableModel.setValueAt(pagIbigNumber, selectedRow, 6);

                        CSVHandler.writeEmployeesToCSV(employees, "src/main/resources/employeedata.csv");
                        JOptionPane.showMessageDialog(dashboard, "Employee updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dashboard, "Error saving employee: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(dashboard, "Please select an employee.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            // Logout
            jButton9.addActionListener(e -> {
                User.setLoggedInUser(null);
                dashboard.dispose();
                User.createLoginFrame(users);
            });

            // Refresh UI
            jTable4.revalidate();
            jTable4.repaint();
            dashboard.revalidate();
            dashboard.repaint();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(dashboard, "Error initializing Dashboard: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        dashboard.setVisible(true);
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