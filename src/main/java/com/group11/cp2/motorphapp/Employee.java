package com.group11.cp2.motorphapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class Employee {
    private int employeeNumber;
    private String lastName;
    private String firstName;
    private String position;
    private String employmentStatus;
    private LocalDate birthday;
    private CompensationDetails compensation;
    private GovernmentDetails governmentDetails;

    // GUI Components (static to share across Employee instances)
    private static JTextField employeeNumberField, lastNameField, firstNameField, birthdayField, positionField, statusField ;
    private static JTable employeeTable;
    private static DefaultTableModel tableModel;
    private static List<Employee> employees;

    public Employee(int employeeNumber, String lastName, String firstName,
                    LocalDate birthday, String position, String employmentStatus, 
                    CompensationDetails compensation, GovernmentDetails governmentDetails) {
        this.employeeNumber = employeeNumber;
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthday = birthday;
        this.position = position;
        this.employmentStatus = employmentStatus;
        this.compensation = compensation;
        this.governmentDetails = governmentDetails;
    }

    // Getters
    public int getEmployeeNumber() {
        return employeeNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }
    
    public LocalDate getBirthday() {
        return birthday;
    }

    public String getPosition() {
        return position;
    }

    public String getEmploymentStatus() {
        return employmentStatus;
    }

    public CompensationDetails getCompensation() {
        return compensation;
    }

    public GovernmentDetails getGovernmentDetails() {
        return governmentDetails;
    }

    // Static method to create GUI panel
    public static JPanel getGUIPanel(List<Employee> employeeList) {
        employees = employeeList;
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Employee Details"));

        // Employee Fields
        formPanel.add(new JLabel("Employee Number:"));
        employeeNumberField = new JTextField();
        formPanel.add(employeeNumberField);

        formPanel.add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        formPanel.add(lastNameField);

        formPanel.add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        formPanel.add(firstNameField);
        
        formPanel.add(new JLabel("Birthday (YYYY-MM-DD):"));
        birthdayField = new JTextField();
        formPanel.add(birthdayField);

        formPanel.add(new JLabel("Position:"));
        positionField = new JTextField();
        formPanel.add(positionField);

        formPanel.add(new JLabel("Employment Status:"));
        statusField = new JTextField();
        formPanel.add(statusField);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Employee");
        JButton clearButton = new JButton("Clear Form");
        buttonPanel.add(addButton);
        buttonPanel.add(clearButton);

        // Table Panel
        String[] columnNames = {"Emp No", "Last Name", "First Name", "Birthday", "Position", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        employeeTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(employeeTable);

        // Populate Table
        populateTable();

        // Add components to main panel
        panel.add(formPanel, BorderLayout.WEST);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Button Actions
        addButton.addActionListener(e -> addEmployee());
        clearButton.addActionListener(e -> clearForm());

        // Table Selection Listener
        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = employeeTable.getSelectedRow();
                if (selectedRow >= 0) {
                    displayEmployee(selectedRow);
                }
            }
        });

        return panel;
    }

    private static void populateTable() {
        tableModel.setRowCount(0); // Clear existing rows
        for (Employee emp : employees) {
            tableModel.addRow(new Object[]{
                    emp.getEmployeeNumber(),
                    emp.getLastName(),
                    emp.getFirstName(),
                    emp.getBirthday(),
                    emp.getPosition(),
                    emp.getEmploymentStatus()
            });
        }
    }

    private static void addEmployee() {
        try {
            // Validate and parse inputs
            int employeeNumber = Integer.parseInt(employeeNumberField.getText().trim());
            String lastName = lastNameField.getText().trim();
            String firstName = firstNameField.getText().trim();
            String position = positionField.getText().trim();
            String status = statusField.getText().trim();
            LocalDate birthday = LocalDate.parse(birthdayField.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);

            // Basic validation
            if (lastName.isEmpty() || firstName.isEmpty() || position.isEmpty() || status.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill in all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check for duplicate employee number
            if (employees.stream().anyMatch(emp -> emp.getEmployeeNumber() == employeeNumber)) {
                JOptionPane.showMessageDialog(null, "Employee number already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create new employee with null compensation and government details
            Employee newEmployee = new Employee(
                    employeeNumber, lastName, firstName, birthday, position, status, null, null);

            // Add to list and update table
            employees.add(newEmployee);
            tableModel.addRow(new Object[]{
                    newEmployee.getEmployeeNumber(),
                    newEmployee.getLastName(),
                    newEmployee.getFirstName(),
                    newEmployee.getBirthday(),
                    newEmployee.getPosition(),
                    newEmployee.getEmploymentStatus()
            });

            JOptionPane.showMessageDialog(null, "Employee added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter a valid number for Employee Number.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(null, "Please enter a valid date in YYYY-MM-DD format.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void clearForm() {
        employeeNumberField.setText("");
        lastNameField.setText("");
        firstNameField.setText("");
        positionField.setText("");
        statusField.setText("");
        birthdayField.setText("");
    }

    private static void displayEmployee(int rowIndex) {
        Employee emp = employees.get(rowIndex);
        employeeNumberField.setText(String.valueOf(emp.getEmployeeNumber()));
        lastNameField.setText(emp.getLastName());
        firstNameField.setText(emp.getFirstName());
        positionField.setText(emp.getPosition());
        statusField.setText(emp.getEmploymentStatus());
        birthdayField.setText(emp.getBirthday().toString());
    }
}