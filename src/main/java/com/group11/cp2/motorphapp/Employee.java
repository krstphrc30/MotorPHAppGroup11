
package com.group11.cp2.motorphapp;

import java.time.LocalDate;

public class Employee {
    private int employeeNumber;
    private String lastName;
    private String firstName;
    private LocalDate birthday;
    private String position;
    private String status;
    private CompensationDetails compensationDetails;
    private GovernmentDetails governmentDetails;

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

    public int getEmployeeNumber() { return employeeNumber; }
    public String getLastName() { return lastName; }
    public String getFirstName() { return firstName; }
    public LocalDate getBirthday() { return birthday; }
    public String getPosition() { return position; }
    public String getStatus() { return status; }
    public CompensationDetails getCompensationDetails() { return compensationDetails; }
    public GovernmentDetails getGovernmentDetails() { return governmentDetails; }

    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }
    public void setPosition(String position) { this.position = position; }
    public void setStatus(String status) { this.status = status; }
    public void setCompensationDetails(CompensationDetails compensationDetails) { this.compensationDetails = compensationDetails; }
    public void setGovernmentDetails(GovernmentDetails governmentDetails) { this.governmentDetails = governmentDetails; }
}