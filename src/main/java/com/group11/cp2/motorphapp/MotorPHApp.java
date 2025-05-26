package com.group11.cp2.motorphapp;

import java.util.List;

public class MotorPHApp {
    public static void main(String[] args) {
        // Step 1: Read Employee CSV
        List<Employee> employees = CSVHandler.readEmployeesFromCSV("src/main/resources/employeedata.csv");

        // Step 2: Read Attendance CSV
        CSVHandler csvHandler = new CSVHandler();  // Instance needed for non-static method
        List<AttendanceRecord> attendance = csvHandler.readAttendanceCSV("src/main/resources/attendancerecord.csv");

        // Step 3: Display Employee Information
        System.out.println("=== EMPLOYEE INFORMATION ===");
        for (Employee emp : employees) {
            displayEmployeeDetails(emp);
            System.out.println("----------------------------");
        }

        // Step 4: Weekly Summary Report
        System.out.println("\n=== WEEKLY SALARY SUMMARY ===");
        String summary = AttendanceRecord.generateWeeklySalarySummary(attendance, employees);
        System.out.println(summary);
    }

    // Utility method to organize employee display
    private static void displayEmployeeDetails(Employee emp) {
        System.out.println("Employee Number: " + emp.getEmployeeNumber());
        System.out.println("Name: " + emp.getFirstName() + " " + emp.getLastName());
        System.out.println("Birthday: " + emp.getBirthday());
        System.out.println("Status: " + emp.getEmploymentStatus());
        System.out.println("Position: " + emp.getPosition());

        GovernmentDetails gov = emp.getGovernmentDetails();
        if (gov != null) {
            System.out.println("SSS Number: " + gov.getSssNumber());
            System.out.println("PhilHealth Number: " + gov.getPhilHealthNumber());
            System.out.println("TIN: " + gov.getTinNumber());
            System.out.println("Pag-IBIG Number: " + gov.getPagIbigNumber());
        }

        CompensationDetails comp = emp.getCompensation();
        if (comp != null) {
            System.out.println("Basic Salary: " + comp.getBasicSalary());
            System.out.println("Rice Subsidy: " + comp.getRiceSubsidy());
            System.out.println("Phone Allowance: " + comp.getPhoneAllowance());
            System.out.println("Clothing Allowance: " + comp.getClothingAllowance());
            System.out.println("Gross Semi-Monthly Rate: " + comp.getGrossSemiMonthlyRate());
            System.out.println("Hourly Rate: " + comp.getHourlyRate());
        }
    }
}
