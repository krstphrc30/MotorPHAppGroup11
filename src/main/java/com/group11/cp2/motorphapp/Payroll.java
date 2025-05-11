/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group11.cp2.motorphapp;

/**
 *
 * @author Pil,Kristopher,Janice,Clarinda
 */
public class Payroll {
    private Employee employee;
    private double totalHours;
    private double grossPay;
    private Deductions deductions;
    private double netPay;

    // Constructor to initialize the Payroll object
    public Payroll(Employee employee, double totalHours, Deductions deductions) {
        this.employee = employee;
        this.totalHours = totalHours;
        this.deductions = deductions;
    }

     public double computeGrossPay() {
        this.grossPay = employee.getHourlyRate() * totalHours;
        return grossPay;
    }

    // Method to compute Net Pay (Gross Pay - Deductions)
    public void computeNetPay() {
        this.netPay = this.grossPay - this.deductions.getTotal();
    }

    // Getter methods to retrieve values
    public double getGrossPay() {
        return this.grossPay;
    }

    public double getNetPay() {
        return this.netPay;
    }

    public String getEmployeeName() {
        return employee.getFirstName() + " " + employee.getLastName();
    }

     public double getHoursWorked() {
        return totalHours;
    }
}
