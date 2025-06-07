package com.group11.cp2.motorphapp;

import java.time.Duration;
import java.time.LocalDate;

public class PayrollReport {
    private static final int WEEKS_IN_MONTH = 4;

    private Employee employee;
    private LocalDate payPeriodStart;
    private LocalDate payPeriodEnd;
    private double weeklyHoursWorked;
    private double baseSalary;
    private double overtimePay;
    private double grossSalary;
    private Deductions deductions;
    private double totalAllowances;

    public PayrollReport(Employee employee, LocalDate start, LocalDate end, Duration weeklyWorkDuration) {
        this.employee = employee;
        this.payPeriodStart = start;
        this.payPeriodEnd = end;

        this.weeklyHoursWorked = weeklyWorkDuration.toMinutes() / 60.0;
        double hourlyRate = employee.getCompensation().getHourlyRate();

        // Validate inputs
        if (hourlyRate < 0 || weeklyHoursWorked < 0) {
            throw new IllegalArgumentException("Hourly rate must be non-negative.");
        }

        // Calculate regular and overtime hours
        double regularHours = Math.min(weeklyHoursWorked, 40); // Assume 40 hours/week max
        double overtimeHours = Math.max(0, weeklyHoursWorked - 40);
        this.baseSalary = hourlyRate * regularHours;
        this.overtimePay = hourlyRate * 1.25 * overtimeHours; // 1.25x rate for overtime
        this.grossSalary = baseSalary + overtimePay;
        this.deductions = new Deductions(grossSalary);
        this.totalAllowances = computeWeeklyAllowances(employee.getCompensation());
    }

    private double computeWeeklyAllowances(CompensationDetails comp) {
        return (comp.getRiceSubsidy() + comp.getPhoneAllowance() + comp.getClothingAllowance()) / WEEKS_IN_MONTH;
    }

    // Corrected method to compute net salary
    public double getNetSalary() {
        return (grossSalary - deductions.getTotalDeductions() - deductions.getWithholdingTax()) + totalAllowances;
    }

    // Getters
    public Employee getEmployee() { return employee; }
    public LocalDate getPayPeriodStart() { return payPeriodStart; }
    public LocalDate getPayPeriodEnd() { return payPeriodEnd; }
    public double getWeeklyHoursWorked() { return weeklyHoursWorked; }
    public double getBaseSalary() { return baseSalary; }
    public double getOvertimePay() { return overtimePay; }
    public double getGrossSalary() { return grossSalary; }
    public Deductions getDeductions() { return deductions; }
    public double getTotalAllowances() { return totalAllowances; }

    public void printPayrollSummary() {
        System.out.println("-------------------------------------------------");
        System.out.println("Weekly Salary Summary for " + employee.getLastName() + ", " + employee.getFirstName() + ":");
        System.out.println("-------------------------------------------------");
        System.out.println("Week Period             : " + payPeriodStart + " - " + payPeriodEnd);
        System.out.printf("Total Hours Worked      : %s%n", formatDuration(Duration.ofMinutes((long)(weeklyHoursWorked * 60))));
        System.out.printf("Total Overtime          : %s%n", formatDuration(Duration.ofMinutes((long)(overtimeHours() * 60))));
        System.out.printf("Base Salary             : PHP %.2f%n", baseSalary);
        System.out.printf("Overtime Pay            : PHP %.2f%n", overtimePay);
        System.out.printf("Gross Salary            : PHP %.2f%n", grossSalary);
        System.out.printf("SSS Contribution        : PHP -%.2f%n", deductions.getSss());
        System.out.printf("PhilHealth Contribution : PHP -%.2f%n", deductions.getPhilHealth());
        System.out.printf("Pag-Ibig Contribution   : PHP -%.2f%n", deductions.getPagIbig());
        System.out.printf("Total Deductions        : PHP -%.2f%n", deductions.getTotalDeductions());
        System.out.printf("Withholding Tax         : PHP -%.2f%n", deductions.getWithholdingTax());
        System.out.printf("Allowances              : PHP %.2f%n", totalAllowances);
        System.out.printf("Net Salary              : PHP %.2f%n", getNetSalary());
        System.out.println("-------------------------------------------------\n");
    }

    private double overtimeHours() {
        return Math.max(0, weeklyHoursWorked - 40);
    }

    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.minusHours(hours).toMinutes();
        return String.format("%dh %dm", hours, minutes);
    }

    @Override
    public String toString() {
        return String.format(
            "Weekly Salary Summary for %s, %s%n" +
            "-------------------------------------------------%n" +
            "Week Period             : %s - %s%n" +
            "Total Hours Worked      : %s%n" +
            "Total Overtime          : %s%n" +
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
            payPeriodStart, payPeriodEnd,
            formatDuration(Duration.ofMinutes((long)(weeklyHoursWorked * 60))),
            formatDuration(Duration.ofMinutes((long)(overtimeHours() * 60))),
            baseSalary, overtimePay, grossSalary,
            deductions.getSss(), deductions.getPhilHealth(), deductions.getPagIbig(),
            deductions.getTotalDeductions(), deductions.getWithholdingTax(), totalAllowances,
            getNetSalary()
        );
    }
}