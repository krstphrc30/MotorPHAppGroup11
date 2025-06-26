package com.group11.cp2.motorphapp;

import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

public class MotorPHApp {
    private static List<User> users;
    private static List<Employee> employees;
    private static List<AttendanceRecord> attendance;

    public static void main(String[] args) {
        users = new ArrayList<>();
        employees = CSVHandler.readEmployeesFromCSV("src/main/resources/employeedata.csv");
        System.out.println("Loaded " + employees.size() + " employees from CSV");

        if (employees.isEmpty()) {
            System.err.println("No employees loaded from CSV. Creating default admin user with sample employee.");
            Employee sampleEmployee = new Employee(10001, "Garcia", "Manuel III", LocalDate.of(1983, 10, 11),
                    "Manager", "Regular", new CompensationDetails(30000, 1500, 1000, 2000, 15000, 535.73),
                    new GovernmentDetails("123456789", "444555666", "987654321", "111222333"));
            employees.add(sampleEmployee);
            users.add(new User("admin", "admin123", "Admin", sampleEmployee,
                    "What is your mother's maiden name?", "Smith"));
        } else {
            for (Employee emp : employees) {
                String username = emp.getFirstName().toLowerCase() + emp.getEmployeeNumber();
                String password = "password123";
                String role = emp.getPosition().contains("Manager") || emp.getPosition().contains("Chief") ? "Admin" : "Employee";
                String securityQuestion = "What is your favorite color?";
                String securityAnswer = "Blue";
                users.add(new User(username, password, role, emp, securityQuestion, securityAnswer));
            }
            Employee adminEmployee = employees.get(0);
            users.add(new User("admin", "admin123", "Admin", adminEmployee,
                    "What is your mother's maiden name?", "Smith"));
        }

        attendance = CSVHandler.readAttendanceCSV("src/main/resources/attendancerecord.csv");
        SwingUtilities.invokeLater(() -> User.createLoginFrame(users));
    }

    public static List<AttendanceRecord> getAttendance() {
        return attendance;
    }

    public static void createMainFrame() {
        JFrame mainFrame = new JFrame("MotorPH Payroll System - Dashboard");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainFrame.setLocationRelativeTo(null); // Optional, can be removed
        mainFrame.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Employee Records", createEmployeeRecordsPanel());
        tabbedPane.addTab("Employee Management", Employee.getGUIPanel(employees));
        tabbedPane.addTab("Attendance Records", AttendanceRecord.createAttendancePanel(employees, User.getLoggedInUser()));
        tabbedPane.addTab("Compensation Details", createCompensationPanel());
        tabbedPane.addTab("Government Details", createGovernmentPanel());
        tabbedPane.addTab("Payroll Reports", createPayrollPanel());

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            mainFrame.dispose();
            User.createLoginFrame(users);
        });

        mainFrame.add(tabbedPane, BorderLayout.CENTER);
        mainFrame.add(logoutButton, BorderLayout.SOUTH);
        mainFrame.setVisible(true);
    }

    private static JPanel createEmployeeRecordsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table Panel
        String[] columnNames = {"Emp No", "Last Name", "First Name", "SSS Number", "PhilHealth Number", "TIN", "Pag-IBIG Number"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        JTable table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);

        // Populate Table
        User loggedInUser = User.getLoggedInUser();
        for (Employee emp : employees) {
            if (!loggedInUser.isAdmin() && emp.getEmployeeNumber() != loggedInUser.getEmployee().getEmployeeNumber()) {
                continue;
            }
            GovernmentDetails gov = emp.getGovernmentDetails();
            tableModel.addRow(new Object[]{
                    emp.getEmployeeNumber(),
                    emp.getLastName(),
                    emp.getFirstName(),
                    gov.getSssNumber(),
                    gov.getPhilHealthNumber(),
                    gov.getTinNumber(),
                    gov.getPagIbigNumber()
            });
        }

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton viewButton = new JButton("View Employee");
        JButton newEmployeeButton = new JButton("New Employee");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        buttonPanel.add(viewButton);
        buttonPanel.add(newEmployeeButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        // Enable/Disable Update and Delete buttons based on selection
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = table.getSelectedRow() >= 0;
                updateButton.setEnabled(hasSelection);
                deleteButton.setEnabled(hasSelection);
            }
        });

        // View Employee Action
        viewButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int empNumber = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                Employee emp = employees.stream()
                        .filter(employee -> employee.getEmployeeNumber() == empNumber)
                        .findFirst()
                        .orElse(null);
                if (emp != null) {
                    createEmployeeDetailsFrame(emp);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select an employee.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // New Employee Action
        newEmployeeButton.addActionListener(e -> createNewEmployeeFrame(tableModel));

        // Update Employee Action
        updateButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int empNumber = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                Employee emp = employees.stream()
                        .filter(employee -> employee.getEmployeeNumber() == empNumber)
                        .findFirst()
                        .orElse(null);
                if (emp != null) {
                    createUpdateEmployeeFrame(emp, tableModel, selectedRow);
                }
            }
        });

        // Delete Employee Action
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int empNumber = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete employee #" + empNumber + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    Employee emp = employees.stream()
                            .filter(employee -> employee.getEmployeeNumber() == empNumber)
                            .findFirst()
                            .orElse(null);
                    if (emp != null) {
                        employees.remove(emp);
                        users.removeIf(user -> user.getEmployee().getEmployeeNumber() == empNumber);
                        attendance.removeIf(record -> record.getEmployeeNumber() == empNumber);
                        tableModel.removeRow(selectedRow);
                        CSVHandler.writeEmployeesToCSV(employees, "src/main/resources/employeedata.csv");
                        CSVHandler.writeAttendanceToCSV(attendance, "src/main/resources/attendancerecord.csv");
                        JOptionPane.showMessageDialog(null, "Employee deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });

        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private static void createEmployeeDetailsFrame(Employee emp) {
        JFrame detailsFrame = new JFrame("Employee Details - " + emp.getLastName() + ", " + emp.getFirstName());
        detailsFrame.setSize(600, 300);
        detailsFrame.setLocationRelativeTo(null);
        detailsFrame.setLayout(new BorderLayout(10, 10));

        // Employee Details Panel
        JPanel detailsPanel = new JPanel(new GridLayout(2, 4, 10, 5));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Employee Information"));

        // Row 1: Basic Info
        detailsPanel.add(new JLabel("Emp No:"));
        detailsPanel.add(new JLabel(String.valueOf(emp.getEmployeeNumber())));
        detailsPanel.add(new JLabel("Name:"));
        detailsPanel.add(new JLabel(emp.getLastName() + ", " + emp.getFirstName()));

        // Row 2: Government Details
        GovernmentDetails gov = emp.getGovernmentDetails();
        detailsPanel.add(new JLabel("SSS No:"));
        detailsPanel.add(new JLabel(gov.getSssNumber()));
        detailsPanel.add(new JLabel("TIN:"));
        detailsPanel.add(new JLabel(gov.getTinNumber()));

        detailsFrame.add(detailsPanel, BorderLayout.CENTER);
        detailsFrame.setVisible(true);
    }

    private static void createUpdateEmployeeFrame(Employee emp, DefaultTableModel tableModel, int selectedRow) {
        JFrame updateFrame = new JFrame("Update Employee - " + emp.getLastName() + ", " + emp.getFirstName());
        updateFrame.setSize(500, 600);
        updateFrame.setLocationRelativeTo(null);
        updateFrame.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(14, 2, 10, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Update Employee Details"));

        JTextField empNumberField = new JTextField(String.valueOf(emp.getEmployeeNumber()));
        empNumberField.setEditable(false);
        JTextField lastNameField = new JTextField(emp.getLastName());
        JTextField firstNameField = new JTextField(emp.getFirstName());
        JTextField birthdayField = new JTextField(emp.getBirthday().toString());
        JTextField positionField = new JTextField(emp.getPosition());
        JTextField statusField = new JTextField(emp.getStatus());
        JTextField basicSalaryField = new JTextField(String.valueOf(emp.getCompensationDetails().getBasicSalary()));
        JTextField riceSubsidyField = new JTextField(String.valueOf(emp.getCompensationDetails().getRiceSubsidy()));
        JTextField phoneAllowanceField = new JTextField(String.valueOf(emp.getCompensationDetails().getPhoneAllowance()));
        JTextField clothingAllowanceField = new JTextField(String.valueOf(emp.getCompensationDetails().getClothingAllowance()));
        JTextField hourlyRateField = new JTextField(String.valueOf(emp.getCompensationDetails().getHourlyRate()));
        JTextField sssField = new JTextField(emp.getGovernmentDetails().getSssNumber());
        JTextField philHealthField = new JTextField(emp.getGovernmentDetails().getPhilHealthNumber());
        JTextField tinField = new JTextField(emp.getGovernmentDetails().getTinNumber());
        JTextField pagIbigField = new JTextField(emp.getGovernmentDetails().getPagIbigNumber());

        formPanel.add(new JLabel("Employee Number:"));
        formPanel.add(empNumberField);
        formPanel.add(new JLabel("Last Name:"));
        formPanel.add(lastNameField);
        formPanel.add(new JLabel("First Name:"));
        formPanel.add(firstNameField);
        formPanel.add(new JLabel("Birthday (YYYY-MM-DD):"));
        formPanel.add(birthdayField);
        formPanel.add(new JLabel("Position:"));
        formPanel.add(positionField);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusField);
        formPanel.add(new JLabel("Basic Salary:"));
        formPanel.add(basicSalaryField);
        formPanel.add(new JLabel("Rice Subsidy:"));
        formPanel.add(riceSubsidyField);
        formPanel.add(new JLabel("Phone Allowance:"));
        formPanel.add(phoneAllowanceField);
        formPanel.add(new JLabel("Clothing Allowance:"));
        formPanel.add(clothingAllowanceField);
        formPanel.add(new JLabel("Hourly Rate:"));
        formPanel.add(hourlyRateField);
        formPanel.add(new JLabel("SSS Number:"));
        formPanel.add(sssField);
        formPanel.add(new JLabel("PhilHealth Number:"));
        formPanel.add(philHealthField);
        formPanel.add(new JLabel("TIN:"));
        formPanel.add(tinField);
        formPanel.add(new JLabel("Pag-IBIG Number:"));
        formPanel.add(pagIbigField);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        saveButton.addActionListener(e -> {
            try {
                int empNumber = Integer.parseInt(empNumberField.getText().trim());
                String lastName = lastNameField.getText().trim();
                String firstName = firstNameField.getText().trim();
                LocalDate birthday = LocalDate.parse(birthdayField.getText().trim());
                String position = positionField.getText().trim();
                String status = statusField.getText().trim();
                double basicSalary = Double.parseDouble(basicSalaryField.getText().trim());
                double riceSubsidy = Double.parseDouble(riceSubsidyField.getText().trim());
                double phoneAllowance = Double.parseDouble(phoneAllowanceField.getText().trim());
                double clothingAllowance = Double.parseDouble(clothingAllowanceField.getText().trim());
                double hourlyRate = Double.parseDouble(hourlyRateField.getText().trim());
                double grossSemiMonthlyRate = basicSalary / 2;
                String sssNumber = sssField.getText().trim();
                String philHealthNumber = philHealthField.getText().trim();
                String tinNumber = tinField.getText().trim();
                String pagIbigNumber = pagIbigField.getText().trim();

                if (!sssNumber.matches("\\d{9,12}") || !philHealthNumber.matches("\\d{9,12}") ||
                        !pagIbigNumber.matches("\\d{9,12}") || !tinNumber.matches("\\d{9,12}")) {
                    JOptionPane.showMessageDialog(null, "Please enter valid ID numbers (9–12 digits).", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                CompensationDetails comp = new CompensationDetails(basicSalary, riceSubsidy, phoneAllowance, clothingAllowance, grossSemiMonthlyRate, hourlyRate);
                GovernmentDetails gov = new GovernmentDetails(sssNumber, tinNumber, philHealthNumber, pagIbigNumber);
                emp.setLastName(lastName);
                emp.setFirstName(firstName);
                emp.setBirthday(birthday);
                emp.setPosition(position);
                emp.setStatus(status);
                emp.setCompensationDetails(comp);
                emp.setGovernmentDetails(gov);

                tableModel.setValueAt(lastName, selectedRow, 1);
                tableModel.setValueAt(firstName, selectedRow, 2);
                tableModel.setValueAt(sssNumber, selectedRow, 3);
                tableModel.setValueAt(philHealthNumber, selectedRow, 4);
                tableModel.setValueAt(tinNumber, selectedRow, 5);
                tableModel.setValueAt(pagIbigNumber, selectedRow, 6);

                CSVHandler.writeEmployeesToCSV(employees, "src/main/resources/employeedata.csv");
                JOptionPane.showMessageDialog(null, "Employee updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                updateFrame.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> updateFrame.dispose());

        updateFrame.add(formPanel, BorderLayout.CENTER);
        updateFrame.add(buttonPanel, BorderLayout.SOUTH);
        updateFrame.setVisible(true);
    }

    private static void createNewEmployeeFrame(DefaultTableModel tableModel) {
        JFrame newEmployeeFrame = new JFrame("Add New Employee");
        newEmployeeFrame.setSize(800, 400);
        newEmployeeFrame.setLocationRelativeTo(null);
        newEmployeeFrame.setLayout(new BorderLayout(10, 10));

        // Main Panel with 2 rows
        JPanel mainPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createTitledBorder("New Employee Details"));

        // First Row: Personal Information
        JPanel personalPanel = new JPanel(new GridLayout(3, 4, 5, 5));
        JTextField empNumberField = new JTextField(10);
        JTextField lastNameField = new JTextField(10);
        JTextField firstNameField = new JTextField(10);
        JTextField birthdayField = new JTextField(10);
        personalPanel.add(new JLabel("Employee Number:"));
        personalPanel.add(empNumberField);
        personalPanel.add(new JLabel("Last Name:"));
        personalPanel.add(lastNameField);
        personalPanel.add(new JLabel("First Name:"));
        personalPanel.add(firstNameField);
        personalPanel.add(new JLabel("Birthday (YYYY-MM-DD):"));
        personalPanel.add(birthdayField);
        personalPanel.add(new JLabel("Position:"));
        JTextField positionField = new JTextField(10);
        personalPanel.add(positionField);
        personalPanel.add(new JLabel("Status:"));
        JTextField statusField = new JTextField(10);
        personalPanel.add(statusField);

        // Second Row: Compensation and Government Details
        JPanel compGovPanel = new JPanel(new GridLayout(3, 6, 5, 5));
        JTextField basicSalaryField = new JTextField(10);
        JTextField riceSubsidyField = new JTextField(10);
        JTextField phoneAllowanceField = new JTextField(10);
        JTextField clothingAllowanceField = new JTextField(10);
        JTextField hourlyRateField = new JTextField(10);
        JTextField sssField = new JTextField(10);
        JTextField philHealthField = new JTextField(10);
        JTextField tinField = new JTextField(10);
        JTextField pagIbigField = new JTextField(10);
        compGovPanel.add(new JLabel("Basic Salary:"));
        compGovPanel.add(basicSalaryField);
        compGovPanel.add(new JLabel("Rice Subsidy:"));
        compGovPanel.add(riceSubsidyField);
        compGovPanel.add(new JLabel("Phone Allowance:"));
        compGovPanel.add(phoneAllowanceField);
        compGovPanel.add(new JLabel("Clothing Allowance:"));
        compGovPanel.add(clothingAllowanceField);
        compGovPanel.add(new JLabel("Hourly Rate:"));
        compGovPanel.add(hourlyRateField);
        compGovPanel.add(new JLabel("SSS Number:"));
        compGovPanel.add(sssField);
        compGovPanel.add(new JLabel("PhilHealth Number:"));
        compGovPanel.add(philHealthField);
        compGovPanel.add(new JLabel("TIN:"));
        compGovPanel.add(tinField);
        compGovPanel.add(new JLabel("Pag-IBIG Number:"));
        compGovPanel.add(pagIbigField);

        mainPanel.add(personalPanel);
        mainPanel.add(compGovPanel);

        // Button Panel
        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        // Add panels to frame
        newEmployeeFrame.add(mainPanel, BorderLayout.CENTER);
        newEmployeeFrame.add(buttonPanel, BorderLayout.SOUTH);

        submitButton.addActionListener(e -> {
            try {
                int empNumber = Integer.parseInt(empNumberField.getText().trim());
                if (employees.stream().anyMatch(emp -> emp.getEmployeeNumber() == empNumber)) {
                    JOptionPane.showMessageDialog(null, "Employee number already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String lastName = lastNameField.getText().trim();
                String firstName = firstNameField.getText().trim();
                LocalDate birthday = LocalDate.parse(birthdayField.getText().trim());
                String position = positionField.getText().trim();
                String status = statusField.getText().trim();
                double basicSalary = Double.parseDouble(basicSalaryField.getText().trim());
                double riceSubsidy = Double.parseDouble(riceSubsidyField.getText().trim());
                double phoneAllowance = Double.parseDouble(phoneAllowanceField.getText().trim());
                double clothingAllowance = Double.parseDouble(clothingAllowanceField.getText().trim());
                double hourlyRate = Double.parseDouble(hourlyRateField.getText().trim());
                double grossSemiMonthlyRate = basicSalary / 2;
                String sssNumber = sssField.getText().trim();
                String philHealthNumber = philHealthField.getText().trim();
                String tinNumber = tinField.getText().trim();
                String pagIbigNumber = pagIbigField.getText().trim();

                if (!sssNumber.matches("\\d{9,12}") || !philHealthNumber.matches("\\d{9,12}") ||
                        !pagIbigNumber.matches("\\d{9,12}") || !tinNumber.matches("\\d{9,12}")) {
                    JOptionPane.showMessageDialog(null, "Please enter valid ID numbers (9–12 digits).", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                CompensationDetails comp = new CompensationDetails(basicSalary, riceSubsidy, phoneAllowance, clothingAllowance, grossSemiMonthlyRate, hourlyRate);
                GovernmentDetails gov = new GovernmentDetails(sssNumber, tinNumber, philHealthNumber, pagIbigNumber);
                Employee newEmp = new Employee(empNumber, lastName, firstName, birthday, position, status, comp, gov);

                employees.add(newEmp);
                CSVHandler.writeEmployeesToCSV(employees, "src/main/resources/employeedata.csv");

                tableModel.addRow(new Object[]{
                        empNumber, lastName, firstName, sssNumber, philHealthNumber, tinNumber, pagIbigNumber
                });

                JOptionPane.showMessageDialog(null, "Employee added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                newEmployeeFrame.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> newEmployeeFrame.dispose());

        newEmployeeFrame.setVisible(true);
    }

    private static JPanel createPayrollPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Month/Year Selector
        JPanel selectorPanel = new JPanel(new FlowLayout());
        String[] months = {"January", "February", "March", "April", "May", "June",
                          "July", "August", "September", "October", "November", "December"};
        JComboBox<String> monthCombo = new JComboBox<>(months);
        monthCombo.setSelectedIndex(YearMonth.now().getMonthValue() - 1);
        JTextField yearField = new JTextField(String.valueOf(YearMonth.now().getYear()), 4);
        JButton generateButton = new JButton("Generate Monthly Report");
        selectorPanel.add(new JLabel("Month:"));
        selectorPanel.add(monthCombo);
        selectorPanel.add(new JLabel("Year:"));
        selectorPanel.add(yearField);
        selectorPanel.add(generateButton);

        // Output Area
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);

        panel.add(selectorPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Generate Report Action
        generateButton.addActionListener(e -> {
            try {
                int year = Integer.parseInt(yearField.getText().trim());
                int month = monthCombo.getSelectedIndex() + 1;
                YearMonth yearMonth = YearMonth.of(year, month);

                StringBuilder sb = new StringBuilder();
                User loggedInUser = User.getLoggedInUser();

                for (Employee emp : employees) {
                    if (!loggedInUser.isAdmin() && emp.getEmployeeNumber() != loggedInUser.getEmployee().getEmployeeNumber()) {
                        continue;
                    }
                    PayrollReport monthlyReport = new PayrollReport(emp, yearMonth, attendance);
                    sb.append(monthlyReport.toString()).append("\n");
                }

                textArea.setText(sb.toString());
            } catch (NumberFormatException ex) {
                textArea.setText("Error: Please enter a valid year.");
            } catch (Exception ex) {
                textArea.setText("Error: " + ex.getMessage());
            }
        });

        // Display current month's report by default
        generateButton.doClick();

        return panel;
    }

    private static JPanel createCompensationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Compensation Details"));

        JTextField empNumberField = new JTextField();
        empNumberField.setEditable(false);
        JTextField basicSalaryField = new JTextField();
        JTextField riceSubsidyField = new JTextField();
        JTextField phoneAllowanceField = new JTextField();
        JTextField clothingAllowanceField = new JTextField();
        JTextField hourlyRateField = new JTextField();

        formPanel.add(new JLabel("Employee Number:"));
        formPanel.add(empNumberField);
        formPanel.add(new JLabel("Basic Salary:"));
        formPanel.add(basicSalaryField);
        formPanel.add(new JLabel("Rice Subsidy:"));
        formPanel.add(riceSubsidyField);
        formPanel.add(new JLabel("Phone Allowance:"));
        formPanel.add(phoneAllowanceField);
        formPanel.add(new JLabel("Clothing Allowance:"));
        formPanel.add(clothingAllowanceField);
        formPanel.add(new JLabel("Hourly Rate:"));
        formPanel.add(hourlyRateField);

        // Disable form for non-admins
        User loggedInUser = User.getLoggedInUser();
        boolean isEditable = loggedInUser.isAdmin();
        basicSalaryField.setEditable(isEditable);
        riceSubsidyField.setEditable(isEditable);
        phoneAllowanceField.setEditable(isEditable);
        clothingAllowanceField.setEditable(isEditable);
        hourlyRateField.setEditable(isEditable);

        // Table Panel
        String[] columnNames = {"Emp No", "Name", "Basic Salary", "Rice Subsidy", "Phone Allowance", "Clothing Allowance", "Hourly Rate"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);

        // Populate Table
        for (Employee emp : employees) {
            if (!loggedInUser.isAdmin() && emp.getEmployeeNumber() != loggedInUser.getEmployee().getEmployeeNumber()) {
                continue;
            }
            CompensationDetails comp = emp.getCompensationDetails();
            tableModel.addRow(new Object[]{
                    emp.getEmployeeNumber(),
                    emp.getLastName() + ", " + emp.getFirstName(),
                    comp.getBasicSalary(),
                    comp.getRiceSubsidy(),
                    comp.getPhoneAllowance(),
                    comp.getClothingAllowance(),
                    comp.getHourlyRate()
            });
        }

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton updateButton = new JButton("Update");
        updateButton.setEnabled(isEditable);
        JButton clearButton = new JButton("Clear Form");
        buttonPanel.add(updateButton);
        buttonPanel.add(clearButton);

        // Table Selection Listener
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    empNumberField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                    basicSalaryField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    riceSubsidyField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                    phoneAllowanceField.setText(tableModel.getValueAt(selectedRow, 4).toString());
                    clothingAllowanceField.setText(tableModel.getValueAt(selectedRow, 5).toString());
                    hourlyRateField.setText(tableModel.getValueAt(selectedRow, 6).toString());
                }
            }
        });

        // Button Actions
        updateButton.addActionListener(e -> {
            try {
                int empNumber = Integer.parseInt(empNumberField.getText().trim());
                Employee emp = employees.stream()
                        .filter(employee -> employee.getEmployeeNumber() == empNumber)
                        .findFirst()
                        .orElse(null);
                if (emp == null) {
                    JOptionPane.showMessageDialog(null, "Employee not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double basicSalary = Double.parseDouble(basicSalaryField.getText().trim());
                double riceSubsidy = Double.parseDouble(riceSubsidyField.getText().trim());
                double phoneAllowance = Double.parseDouble(phoneAllowanceField.getText().trim());
                double clothingAllowance = Double.parseDouble(clothingAllowanceField.getText().trim());
                double hourlyRate = Double.parseDouble(hourlyRateField.getText().trim());
                double grossSemiMonthlyRate = basicSalary / 2;

                CompensationDetails comp = emp.getCompensationDetails();
                comp.setBasicSalary(basicSalary);
                comp.setRiceSubsidy(riceSubsidy);
                comp.setPhoneAllowance(phoneAllowance);
                comp.setClothingAllowance(clothingAllowance);
                comp.setGrossSemiMonthlyRate(grossSemiMonthlyRate);
                comp.setHourlyRate(hourlyRate);

                // Update Table
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if (Integer.parseInt(tableModel.getValueAt(i, 0).toString()) == empNumber) {
                        tableModel.setValueAt(basicSalary, i, 2);
                        tableModel.setValueAt(riceSubsidy, i, 3);
                        tableModel.setValueAt(phoneAllowance, i, 4);
                        tableModel.setValueAt(clothingAllowance, i, 5);
                        tableModel.setValueAt(hourlyRate, i, 6);
                        break;
                    }
                }

                // Save to CSV
                CSVHandler.writeEmployeesToCSV(employees, "src/main/resources/employeedata.csv");
                JOptionPane.showMessageDialog(null, "Compensation details updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        clearButton.addActionListener(e -> {
            empNumberField.setText("");
            basicSalaryField.setText("");
            riceSubsidyField.setText("");
            phoneAllowanceField.setText("");
            clothingAllowanceField.setText("");
            hourlyRateField.setText("");
        });

        // Layout
        panel.add(formPanel, BorderLayout.WEST);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private static JPanel createGovernmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Government Details"));

        JTextField empNumberField = new JTextField();
        empNumberField.setEditable(false);
        JTextField sssField = new JTextField();
        JTextField philHealthField = new JTextField();
        JTextField pagIbigField = new JTextField();
        JTextField tinField = new JTextField();

        formPanel.add(new JLabel("Employee Number:"));
        formPanel.add(empNumberField);
        formPanel.add(new JLabel("SSS Number:"));
        formPanel.add(sssField);
        formPanel.add(new JLabel("PhilHealth Number:"));
        formPanel.add(philHealthField);
        formPanel.add(new JLabel("Pag-IBIG Number:"));
        formPanel.add(pagIbigField);
        formPanel.add(new JLabel("TIN:"));
        formPanel.add(tinField);

        // Disable form for non-admins
        User loggedInUser = User.getLoggedInUser();
        boolean isEditable = loggedInUser.isAdmin();
        sssField.setEditable(isEditable);
        philHealthField.setEditable(isEditable);
        pagIbigField.setEditable(isEditable);
        tinField.setEditable(isEditable);

        // Table Panel
        String[] columnNames = {"Emp No", "Name", "SSS Number", "PhilHealth Number", "Pag-IBIG Number", "TIN"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);

        // Populate Table
        for (Employee emp : employees) {
            if (!loggedInUser.isAdmin() && emp.getEmployeeNumber() != loggedInUser.getEmployee().getEmployeeNumber()) {
                continue;
            }
            GovernmentDetails gov = emp.getGovernmentDetails();
            tableModel.addRow(new Object[]{
                    emp.getEmployeeNumber(),
                    emp.getLastName() + ", " + emp.getFirstName(),
                    gov.getSssNumber(),
                    gov.getPhilHealthNumber(),
                    gov.getPagIbigNumber(),
                    gov.getTinNumber()
            });
        }

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton updateButton = new JButton("Update");
        updateButton.setEnabled(isEditable);
        JButton clearButton = new JButton("Clear Form");
        buttonPanel.add(updateButton);
        buttonPanel.add(clearButton);

        // Table Selection Listener
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    empNumberField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                    sssField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    philHealthField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                    pagIbigField.setText(tableModel.getValueAt(selectedRow, 4).toString());
                    tinField.setText(tableModel.getValueAt(selectedRow, 5).toString());
                }
            }
        });

        // Button Actions
        updateButton.addActionListener(e -> {
            try {
                int empNumber = Integer.parseInt(empNumberField.getText().trim());
                Employee emp = employees.stream()
                        .filter(employee -> employee.getEmployeeNumber() == empNumber)
                        .findFirst()
                        .orElse(null);
                if (emp == null) {
                    JOptionPane.showMessageDialog(null, "Employee not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String sssNumber = sssField.getText().trim();
                String philHealthNumber = philHealthField.getText().trim();
                String pagIbigNumber = pagIbigField.getText().trim();
                String tinNumber = tinField.getText().trim();

                if (!sssNumber.matches("\\d{9,12}") || !philHealthNumber.matches("\\d{9,12}") ||
                        !pagIbigNumber.matches("\\d{9,12}") || !tinNumber.matches("\\d{9,12}")) {
                    JOptionPane.showMessageDialog(null, "Please enter valid ID numbers (9–12 digits).", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                GovernmentDetails gov = emp.getGovernmentDetails();
                gov.setSssNumber(sssNumber);
                gov.setPhilHealthNumber(philHealthNumber);
                gov.setPagIbigNumber(pagIbigNumber);
                gov.setTinNumber(tinNumber);

                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if (Integer.parseInt(tableModel.getValueAt(i, 0).toString()) == empNumber) {
                        tableModel.setValueAt(sssNumber, i, 2);
                        tableModel.setValueAt(philHealthNumber, i, 3);
                        tableModel.setValueAt(pagIbigNumber, i, 4);
                        tableModel.setValueAt(tinNumber, i, 5);
                        break;
                    }
                }

                CSVHandler.writeEmployeesToCSV(employees, "src/main/resources/employeedata.csv");
                JOptionPane.showMessageDialog(null, "Government details updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid employee number.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        clearButton.addActionListener(e -> {
            empNumberField.setText("");
            sssField.setText("");
            philHealthField.setText("");
            pagIbigField.setText("");
            tinField.setText("");
        });

        // Layout
        panel.add(formPanel, BorderLayout.WEST);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }
}