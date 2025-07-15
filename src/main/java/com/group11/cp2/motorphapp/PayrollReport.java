package com.group11.cp2.motorphapp;

import java.time.Duration;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

public class PayrollReport {
    private Employee employee;
    private YearMonth yearMonth;
    private double monthlyRegularHours;
    private double monthlyOvertimeHours;
    private double baseSalary;
    private double overtimePay;
    private double grossSalary;
    private Deductions deductions;
    private double totalAllowances;

    public PayrollReport(Employee employee, YearMonth yearMonth, List<AttendanceRecord> attendanceRecords) {
        this.employee = employee;
        this.yearMonth = yearMonth;

        // Filter attendance records for the specific employee and month
        List<AttendanceRecord> monthlyRecords = attendanceRecords.stream()
                .filter(record -> record.getEmployeeNumber() == employee.getEmployeeNumber()
                        && YearMonth.from(record.getDate()).equals(yearMonth))
                .collect(Collectors.toList());

        // Handle empty records
        if (monthlyRecords.isEmpty()) {
            System.err.println("Warning: No attendance records found for " + employee.getLastName() + ", " + employee.getFirstName() + " in " + yearMonth);
            this.monthlyRegularHours = 0.0;
            this.monthlyOvertimeHours = 0.0;
        } else {
            this.monthlyRegularHours = monthlyRecords.stream()
                    .map(AttendanceRecord::getRegularDuration)
                    .reduce(Duration.ZERO, Duration::plus)
                    .toMinutes() / 60.0;
            this.monthlyOvertimeHours = monthlyRecords.stream()
                    .map(AttendanceRecord::getOvertimeDuration)
                    .reduce(Duration.ZERO, Duration::plus)
                    .toMinutes() / 60.0;
        }

        // Validate hourly rate
        double hourlyRate = employee.getCompensationDetails().getHourlyRate();
        if (hourlyRate <= 0) {
            throw new IllegalArgumentException("Invalid hourly rate (" + hourlyRate + ") for employee #" + employee.getEmployeeNumber());
        }

        // Validate hours
        if (monthlyRegularHours < 0 || monthlyOvertimeHours < 0) {
            throw new IllegalArgumentException("Negative hours detected for employee #" + employee.getEmployeeNumber());
        }

        // Debug output to diagnose issues
        System.out.println("Payroll Report for Employee #" + employee.getEmployeeNumber() + ", " + yearMonth);
        System.out.println("Employee: " + employee.getLastName() + ", " + employee.getFirstName());
        System.out.println("Filtered Records: " + monthlyRecords.size());
        monthlyRecords.forEach(record -> System.out.println(record.toString()));
        System.out.println("Monthly Regular Hours: " + monthlyRegularHours);
        System.out.println("Monthly Overtime Hours: " + monthlyOvertimeHours);
        System.out.println("Hourly Rate: " + hourlyRate);

        // Calculate salaries
        this.baseSalary = hourlyRate * monthlyRegularHours;
        this.overtimePay = hourlyRate * 1.25 * monthlyOvertimeHours;
        this.grossSalary = baseSalary + overtimePay;
        this.totalAllowances = computeMonthlyAllowances(employee.getCompensationDetails());
        this.deductions = new Deductions(grossSalary, employee.getCompensationDetails().getBasicSalary());

        // Log computed values
        System.out.println("Base Salary: PHP " + String.format("%.2f", baseSalary));
        System.out.println("Overtime Pay: PHP " + String.format("%.2f", overtimePay));
        System.out.println("Gross Salary: PHP " + String.format("%.2f", grossSalary));
        System.out.println("Total Allowances: PHP " + String.format("%.2f", totalAllowances));
        System.out.println("Net Salary: PHP " + String.format("%.2f", getNetSalary()));
    }

    private double computeMonthlyAllowances(CompensationDetails comp) {
        double allowances = comp.getRiceSubsidy() + comp.getPhoneAllowance() + comp.getClothingAllowance();
        if (allowances < 0) {
            throw new IllegalArgumentException("Invalid allowances for employee #" + employee.getEmployeeNumber());
        }
        return allowances;
    }

    public double getNetSalary() {
        double netSalary = grossSalary - deductions.getTotalDeductions() + totalAllowances;
        if (netSalary < 0) {
            System.err.println("Warning: Negative net salary calculated for employee #" + employee.getEmployeeNumber());
        }
        return netSalary;
    }

    public Employee getEmployee() { return employee; }
    public YearMonth getYearMonth() { return yearMonth; }
    public double getMonthlyRegularHours() { return monthlyRegularHours; }
    public double getMonthlyOvertimeHours() { return monthlyOvertimeHours; }
    public double getBaseSalary() { return baseSalary; }
    public double getOvertimePay() { return overtimePay; }
    public double getGrossSalary() { return grossSalary; }
    public Deductions getDeductions() { return deductions; }
    public double getTotalAllowances() { return totalAllowances; }

    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.minusHours(hours).toMinutes();
        return String.format("%02dh %02dm", hours, minutes);
    }

    @Override
    public String toString() {
        if (monthlyRegularHours == 0.0 && monthlyOvertimeHours == 0.0) {
            return "No attendance records found for " + employee.getLastName() + ", " + employee.getFirstName() + " in " + yearMonth + ".\nSalary will be calculated with zero hours.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Monthly Salary Summary for ").append(employee.getLastName()).append(", ").append(employee.getFirstName()).append(":\n");
        sb.append("---------------------------------------------\n");
        sb.append("Month                   : ").append(yearMonth).append("\n");
        sb.append("Total Regular Hours     : ").append(formatDuration(Duration.ofMinutes((long)(monthlyRegularHours * 60)))).append("\n");
        sb.append("Total Overtime Hours    : ").append(formatDuration(Duration.ofMinutes((long)(monthlyOvertimeHours * 60)))).append("\n");
        sb.append(String.format("Base Salary             : PHP %.2f%n", baseSalary));
        sb.append(String.format("Overtime Pay            : PHP %.2f%n", overtimePay));
        sb.append(String.format("Gross Salary            : PHP %.2f%n", grossSalary));
        sb.append(String.format("SSS Contribution        : PHP -%.2f%n", deductions.getSss()));
        sb.append(String.format("PhilHealth Contribution : PHP -%.2f%n", deductions.getPhilHealth()));
        sb.append(String.format("Pag-Ibig Contribution   : PHP -%.2f%n", deductions.getPagIbig()));
        sb.append(String.format("Total Deductions        : PHP -%.2f%n", deductions.getTotalDeductions()));
        sb.append(String.format("Withholding Tax         : PHP -%.2f%n", deductions.getWithholdingTax()));
        sb.append(String.format("Allowances              : PHP %.2f%n", totalAllowances));
        sb.append(String.format("Net Salary              : PHP %.2f%n", getNetSalary()));
        sb.append("---------------------------------------------\n");
        return sb.toString();
    }
}