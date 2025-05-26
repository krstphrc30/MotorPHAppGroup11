package com.group11.cp2.motorphapp;

import java.time.LocalDate;

public class Employee {
    private int employeeNumber;
    private String lastName;
    private String firstName;
    private String position;
    private String employmentStatus;
    private LocalDate birthday;
    private CompensationDetails compensation;
    private GovernmentDetails governmentDetails; // Updated here

    public Employee(int employeeNumber, String lastName, String firstName,
                    String position, String employmentStatus, LocalDate birthday,
                    CompensationDetails compensation, GovernmentDetails governmentDetails) { // Updated parameter
        this.employeeNumber = employeeNumber;
        this.lastName = lastName;
        this.firstName = firstName;
        this.position = position;
        this.employmentStatus = employmentStatus;
        this.birthday = birthday;
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

    public String getPosition() {
        return position;
    }

    public String getEmploymentStatus() {
        return employmentStatus;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public CompensationDetails getCompensation() {
        return compensation;
    }

    public GovernmentDetails getGovernmentDetails() { // ✅ New getter
        return governmentDetails;
    }

    // Setters can be added as needed
}
