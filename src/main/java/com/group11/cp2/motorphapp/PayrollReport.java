package com.group11.cp2.motorphapp;

import java.time.Duration;
import java.time.LocalDate;

/**
 * Computes weekly payroll:
 * - Gross Pay = Weekly Hours * Hourly Rate
 * - Deductions = SSS, PhilHealth, Pag-IBIG, Tax
 * - Allowances = Rice, Phone, Clothing (divided by 4 weeks)
 * - Net Pay = Gross - Deductions + Allowances
 */
public class PayrollReport {

    private Employee employee;
    private LocalDate payPeriodStart;
    private LocalDate payPeriodEnd;
    private double weeklyHoursWorked;
    private double grossPay;
    private Deductions deductions;
    private double totalAllowances;
    private double netPay;

    public PayrollReport(Employee employee, LocalDate start, LocalDate end, Duration weeklyWorkDuration) {
        this.employee = employee;
        this.payPeriodStart = start;
        this.payPeriodEnd = end;
        this.weeklyHoursWorked = weeklyWorkDuration.toMinutes() / 60.0;

        double hourlyRate = employee.getCompensation().getHourlyRate();
        this.grossPay = hourlyRate * weeklyHoursWorked;

        this.deductions = new Deductions(grossPay);
        this.totalAllowances = computeWeeklyAllowances(employee.getCompensation());
        this.netPay = computeNetPay();
    }

    private double computeWeeklyAllowances(CompensationDetails comp) {
        final int WEEKS_IN_MONTH = 4;
        return (comp.getRiceSubsidy() + comp.getPhoneAllowance() + comp.getClothingAllowance()) / WEEKS_IN_MONTH;
    }

    private double computeNetPay() {
        return grossPay - deductions.getTotalDeductions() + totalAllowances;
    }

    // Getters
    public Employee getEmployee() { return employee; }
    public LocalDate getPayPeriodStart() { return payPeriodStart; }
    public LocalDate getPayPeriodEnd() { return payPeriodEnd; }
    public double getWeeklyHoursWorked() { return weeklyHoursWorked; }
    public double getGrossPay() { return grossPay; }
    public Deductions getDeductions() { return deductions; }
    public double getTotalAllowances() { return totalAllowances; }
    public double getNetPay() { return netPay; }

    public void printPayrollSummary() {
        System.out.println("===== Payroll Summary =====");
        System.out.println("Employee: " + employee.getLastName() + ", " + employee.getFirstName());
        System.out.println("Pay Period: " + payPeriodStart + " to " + payPeriodEnd);
        System.out.printf("Weekly Hours Worked: %.2f hours%n", weeklyHoursWorked);
        System.out.printf("Gross Pay: ₱%.2f%n", grossPay);
        System.out.printf("  - SSS: ₱%.2f%n", deductions.getSss());
        System.out.printf("  - Pag-IBIG: ₱%.2f%n", deductions.getPagIbig());
        System.out.printf("  - PhilHealth: ₱%.2f%n", deductions.getPhilHealth());
        System.out.printf("  - Withholding Tax: ₱%.2f%n", deductions.getWithholdingTax());
        System.out.printf("Total Deductions: ₱%.2f%n", deductions.getTotalDeductions());
        System.out.printf("Weekly Allowances: ₱%.2f%n", totalAllowances);
        System.out.printf("Net Pay: ₱%.2f%n", netPay);
        System.out.println("===========================\n");
    }
}
