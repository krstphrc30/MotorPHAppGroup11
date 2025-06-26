
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

        // Filter attendance records for the specified month and employee
        List<AttendanceRecord> monthlyRecords = attendanceRecords.stream()
                .filter(record -> record.getEmployeeNumber() == employee.getEmployeeNumber()
                        && YearMonth.from(record.getDate()).equals(yearMonth))
                .collect(Collectors.toList());

        // Compute monthly hours
        this.monthlyRegularHours = monthlyRecords.stream()
                .map(AttendanceRecord::getRegularDuration)
                .reduce(Duration.ZERO, Duration::plus)
                .toMinutes() / 60.0;

        this.monthlyOvertimeHours = monthlyRecords.stream()
                .map(AttendanceRecord::getOvertimeDuration)
                .reduce(Duration.ZERO, Duration::plus)
                .toMinutes() / 60.0;

        double hourlyRate = employee.getCompensationDetails().getHourlyRate();

        // Validate inputs
        if (hourlyRate < 0 || monthlyRegularHours < 0 || monthlyOvertimeHours < 0) {
            throw new IllegalArgumentException("Hourly rate and hours must be non-negative.");
        }

        // Calculate salary components
        this.baseSalary = hourlyRate * monthlyRegularHours;
        this.overtimePay = hourlyRate * 1.25 * monthlyOvertimeHours;
        this.grossSalary = baseSalary + overtimePay;
        this.totalAllowances = computeMonthlyAllowances(employee.getCompensationDetails());
        // Deductions based on gross salary and CSV basic salary
        this.deductions = new Deductions(grossSalary, employee.getCompensationDetails().getBasicSalary());
    }

    private double computeMonthlyAllowances(CompensationDetails comp) {
        return comp.getRiceSubsidy() + comp.getPhoneAllowance() + comp.getClothingAllowance();
    }

    public double getNetSalary() {
        return grossSalary - deductions.getTotalDeductions() + totalAllowances;
    }

    // Getters
    public Employee getEmployee() { return employee; }
    public YearMonth getYearMonth() { return yearMonth; }
    public double getMonthlyRegularHours() { return monthlyRegularHours; }
    public double getMonthlyOvertimeHours() { return monthlyOvertimeHours; }
    public double getBaseSalary() { return baseSalary; }
    public double getOvertimePay() { return overtimePay; }
    public double getGrossSalary() { return grossSalary; }
    public Deductions getDeductions() { return deductions; }
    public double getTotalAllowances() { return totalAllowances; }

    public void printPayrollSummary() {
        System.out.println("-------------------------------------------------");
        System.out.println("Monthly Salary Summary for " + employee.getLastName() + ", " + employee.getFirstName() + ":");
        System.out.println("-------------------------------------------------");
        System.out.println("Month                   : " + yearMonth);
        System.out.printf("Total Regular Hours     : %s%n", formatDuration(Duration.ofMinutes((long)(monthlyRegularHours * 60))));
        System.out.printf("Total Overtime Hours    : %s%n", formatDuration(Duration.ofMinutes((long)(monthlyOvertimeHours * 60))));
        System.out.printf("Basic Salary            : PHP %.2f%n", employee.getCompensationDetails().getBasicSalary());
        System.out.printf("SSS Contribution        : PHP -%.2f%n", deductions.getSss());
        System.out.printf("PhilHealth Contribution : PHP -%.2f%n", deductions.getPhilHealth());
        System.out.printf("Pag-Ibig Contribution   : PHP -%.2f%n", deductions.getPagIbig());
        System.out.printf("Total Deductions        : PHP -%.2f%n", deductions.getTotalDeductions());
        System.out.printf("Withholding Tax         : PHP -%.2f%n", deductions.getWithholdingTax());
        System.out.printf("Allowances              : PHP %.2f%n", totalAllowances);
        System.out.printf("Net Salary              : PHP %.2f%n", getNetSalary());
        System.out.println("-------------------------------------------------\n");
    }

    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.minusHours(hours).toMinutes();
        return String.format("%dh %dm", hours, minutes);
    }

    @Override
    public String toString() {
        return String.format(
            "Monthly Salary Summary for %s, %s%n" +
            "-------------------------------------------------%n" +
            "Month                   : %s%n" +
            "Total Regular Hours     : %s%n" +
            "Total Overtime Hours    : %s%n" +
            "Base Salary             : PHP %.2f%n" +
            "Overtime Pay            : PHP %.2f%n" +
            "Gross Salary            : PHP %.2f%n" +
            "SSS Contribution        : PHP -%.2f%n" +
            "PhilHealth Contribution : PHP -%.2f%n" +
            "Pag-Ibig Contribution   : PHP -%.2f%n" +
            "Total Deductions        : PHP -%.2f%n" +
            "Withholding Tax         : PHP -%.2f%n" +
            "Allowances              : PHP %.2f%n" +
            "Net Salary              : PHP %.2f%n",
            employee.getLastName(), employee.getFirstName(),
            yearMonth,
            formatDuration(Duration.ofMinutes((long)(monthlyRegularHours * 60))),
            formatDuration(Duration.ofMinutes((long)(monthlyOvertimeHours * 60))),
            baseSalary, overtimePay, grossSalary,
            deductions.getSss(),
            deductions.getPhilHealth(),
            deductions.getPagIbig(),
            deductions.getTotalDeductions(),
            deductions.getWithholdingTax(),
            totalAllowances,
            getNetSalary()
        );
    }
}
