/**
 * Generates a payroll report for an employee in the MotorPH Payroll System.
 *
 * @author Kristopher Carlo, Clarinda, Pil, Janice (Group 11)
 */
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

    /**
     * Creates a payroll report for the specified employee and month.
     *
     * @param employee Employee for the report.
     * @param yearMonth Year and month for the report.
     * @param attendanceRecords List of attendance records.
     * @throws IllegalArgumentException If hourly rate or hours are invalid.
     */
    public PayrollReport(Employee employee, YearMonth yearMonth, List<AttendanceRecord> attendanceRecords) {
        this.employee = employee;
        this.yearMonth = yearMonth;

        List<AttendanceRecord> monthlyRecords = attendanceRecords.stream()
                .filter(record -> record.getEmployeeNumber() == employee.getEmployeeNumber()
                        && YearMonth.from(record.getDate()).equals(yearMonth))
                .collect(Collectors.toList());

        if (monthlyRecords.isEmpty()) {
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

        double hourlyRate = employee.getCompensationDetails().getHourlyRate();
        if (hourlyRate <= 0) {
            throw new IllegalArgumentException("Invalid hourly rate for employee #" + employee.getEmployeeNumber());
        }

        if (monthlyRegularHours < 0 || monthlyOvertimeHours < 0) {
            throw new IllegalArgumentException("Negative hours detected for employee #" + employee.getEmployeeNumber());
        }

        this.baseSalary = hourlyRate * monthlyRegularHours;
        this.overtimePay = hourlyRate * 1.25 * monthlyOvertimeHours;
        this.grossSalary = baseSalary + overtimePay;
        this.totalAllowances = computeMonthlyAllowances(employee.getCompensationDetails());
        this.deductions = new Deductions(grossSalary, employee.getCompensationDetails().getBasicSalary());
    }

    /**
     * Computes total monthly allowances.
     *
     * @param comp Employee's compensation details.
     * @return Total allowances.
     * @throws IllegalArgumentException If allowances are invalid.
     */
    private double computeMonthlyAllowances(CompensationDetails comp) {
        double allowances = comp.getRiceSubsidy() + comp.getPhoneAllowance() + comp.getClothingAllowance();
        if (allowances < 0) {
            throw new IllegalArgumentException("Invalid allowances for employee #" + employee.getEmployeeNumber());
        }
        return allowances;
    }

    /**
     * Calculates net salary after deductions and adding allowances.
     *
     * @return Net salary.
     */
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

    /**
     * Formats duration as hours and minutes.
     *
     * @param duration Duration to format.
     * @return Formatted string (e.g., "08h 30m").
     */
    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.minusHours(hours).toMinutes();
        return String.format("%02dh %02dm", hours, minutes);
    }

    /**
     * Returns a string representation of the payroll report.
     *
     * @return Formatted payroll summary.
     */
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