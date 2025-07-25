/**
 * Represents an employee in the MotorPH Payroll System.
 *
 * @author Kristopher Carlo, Clarinda, Pil, Janice (Group 11)
 */
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

    /**
     * Creates an Employee with the specified details.
     *
     * @param employeeNumber Unique employee ID.
     * @param lastName Employee's last name.
     * @param firstName Employee's first name.
     * @param birthday Employee's date of birth.
     * @param position Employee's job position.
     * @param status Employment status (e.g., Active, Inactive).
     * @param compensationDetails Employee's compensation details.
     * @param governmentDetails Employee's government-related details.
     */
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