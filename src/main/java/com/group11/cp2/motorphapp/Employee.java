/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group11.cp2.motorphapp;

import java.time.LocalDate;


/**
 *
 * @author Pil,Kristopher,Janice,Clarinda
 */
public class Employee {
    private int employeeNumber;
    private String lastName;
    private String firstName;
    private LocalDate birthday;
    private String position;
    private double basicSalary;
    private double riceSubsidy;
    private double phoneAllowance;
    private double clothingAllowance;
    private double grossSemiMonthlyRate;
    private double hourlyRate;

     // Constructor added below, right after the class definition
    public Employee(int employeeNumber, String lastName, String firstName, LocalDate birthday, String position, 
                    double basicSalary, double riceSubsidy, double phoneAllowance, double clothingAllowance, 
                    double grossSemiMonthlyRate, double hourlyRate) {
        this.employeeNumber = employeeNumber;
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthday = birthday;
        this.position = position;
        this.basicSalary = basicSalary;
        this.riceSubsidy = riceSubsidy;
        this.phoneAllowance = phoneAllowance;
        this.clothingAllowance = clothingAllowance;
        this.grossSemiMonthlyRate = grossSemiMonthlyRate;
        this.hourlyRate = hourlyRate;
    }
    // Getters and Setters

    public int getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(int num) {
        this.employeeNumber = num;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String name) {
        this.lastName = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String name) {
        this.firstName = name;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate date) {
        this.birthday = date;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String pos) {
        this.position = pos;
    }

    public double getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(double salary) {
        this.basicSalary = salary;
    }

    public double getRiceSubsidy() {
        return riceSubsidy;
    }

    public void setRiceSubsidy(double subsidy) {
        this.riceSubsidy = subsidy;
    }

    public double getPhoneAllowance() {
        return phoneAllowance;
    }

    public void setPhoneAllowance(double allowance) {
        this.phoneAllowance = allowance;
    }

    public double getClothingAllowance() {
        return clothingAllowance;
    }

    public void setClothingAllowance(double allowance) {
        this.clothingAllowance = allowance;
    }

    public double getGrossSemiMonthlyRate() {
        return grossSemiMonthlyRate;
    }

    public void setGrossSemiMonthlyRate(double rate) {
        this.grossSemiMonthlyRate = rate;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double rate) {
        this.hourlyRate = rate;
    }
}
