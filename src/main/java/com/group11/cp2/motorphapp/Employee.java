package com.group11.cp2.motorphapp;

import java.time.LocalDate;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class Employee {
    private int employeeNumber;
    private String lastName;
    private String firstName;
    private LocalDate birthday;
    private String position;
    private String status;
    private CompensationDetails compensationDetails;
    private GovernmentDetails governmentDetails;

    // Constructor
    public Employee(int employeeNumber, String lastName, String firstName, LocalDate birthday,
                    String position, String status, CompensationDetails compensationDetails,
                    GovernmentDetails governmentDetails) {
        this.employeeNumber = employeeNumber;
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthday = birthday;
        this.position = position;
        this.status = status;
        this.compensationDetails = compensationDetails;
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

    public String getStatus() {
        return status;
    }

    public CompensationDetails getCompensationDetails() {
        return compensationDetails;
    }

    public GovernmentDetails getGovernmentDetails() {
        return governmentDetails;
    }

    // Setters
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCompensationDetails(CompensationDetails compensationDetails) {
        this.compensationDetails = compensationDetails;
    }

    public void setGovernmentDetails(GovernmentDetails governmentDetails) {
        this.governmentDetails = governmentDetails;
    }

    // GUI Panel for Employee Management
    public static JPanel getGUIPanel(List<Employee> employees) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Employee Details"));

        JTextField empNumberField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField firstNameField = new JTextField();
        JTextField birthdayField = new JTextField();
        JTextField positionField = new JTextField();
        JTextField statusField = new JTextField();

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

        // Table Panel
        String[] columnNames = {"Emp No", "Last Name", "First Name", "Birthday", "Position", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);

        // Populate Table
        for (Employee emp : employees) {
            tableModel.addRow(new Object[]{
                    emp.getEmployeeNumber(),
                    emp.getLastName(),
                    emp.getFirstName(),
                    emp.getBirthday(),
                    emp.getPosition(),
                    emp.getStatus()
            });
        }

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton clearButton = new JButton("Clear Form");
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(clearButton);

        // Table Selection Listener
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    empNumberField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                    lastNameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    firstNameField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    birthdayField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                    positionField.setText(tableModel.getValueAt(selectedRow, 4).toString());
                    statusField.setText(tableModel.getValueAt(selectedRow, 5).toString());
                }
            }
        });

        // Button Actions
        addButton.addActionListener(e -> {
            try {
                int empNumber = Integer.parseInt(empNumberField.getText().trim());
                if (employees.stream().anyMatch(emp -> emp.getEmployeeNumber() == empNumber)) {
                    JOptionPane.showMessageDialog(null, "Employee number already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Employee newEmp = new Employee(
                        empNumber,
                        lastNameField.getText().trim(),
                        firstNameField.getText().trim(),
                        LocalDate.parse(birthdayField.getText().trim()),
                        positionField.getText().trim(),
                        statusField.getText().trim(),
                        new CompensationDetails(0, 0, 0, 0, 0, 0), // Default values
                        new GovernmentDetails("", "", "", "") // Default values
                );

                employees.add(newEmp);
                tableModel.addRow(new Object[]{
                        newEmp.getEmployeeNumber(),
                        newEmp.getLastName(),
                        newEmp.getFirstName(),
                        newEmp.getBirthday(),
                        newEmp.getPosition(),
                        newEmp.getStatus()
                });

                CSVHandler.writeEmployeesToCSV(employees, "src/main/resources/employeedata.csv");
                JOptionPane.showMessageDialog(null, "Employee added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearButton.doClick();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

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

                emp.setLastName(lastNameField.getText().trim());
                emp.setFirstName(firstNameField.getText().trim());
                emp.setBirthday(LocalDate.parse(birthdayField.getText().trim()));
                emp.setPosition(positionField.getText().trim());
                emp.setStatus(statusField.getText().trim());

                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if (Integer.parseInt(tableModel.getValueAt(i, 0).toString()) == empNumber) {
                        tableModel.setValueAt(emp.getLastName(), i, 1);
                        tableModel.setValueAt(emp.getFirstName(), i, 2);
                        tableModel.setValueAt(emp.getBirthday(), i, 3);
                        tableModel.setValueAt(emp.getPosition(), i, 4);
                        tableModel.setValueAt(emp.getStatus(), i, 5);
                        break;
                    }
                }

                CSVHandler.writeEmployeesToCSV(employees, "src/main/resources/employeedata.csv");
                JOptionPane.showMessageDialog(null, "Employee updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        clearButton.addActionListener(e -> {
            empNumberField.setText("");
            lastNameField.setText("");
            firstNameField.setText("");
            birthdayField.setText("");
            positionField.setText("");
            statusField.setText("");
        });

        panel.add(formPanel, BorderLayout.WEST);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }
}