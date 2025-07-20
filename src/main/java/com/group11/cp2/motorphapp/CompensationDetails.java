/**
 * Stores employee compensation details for the MotorPH Payroll System.
 *
 * @author Kristopher Carlo, Clarinda, Pil, Janice (Group 11)
 */
package com.group11.cp2.motorphapp;

public class CompensationDetails {

    private double basicSalary;
    private double riceSubsidy;
    private double phoneAllowance;
    private double clothingAllowance;
    private double grossSemiMonthlyRate;
    private double hourlyRate;

    /**
     * Creates a CompensationDetails instance with the specified values.
     *
     * @param basicSalary Monthly basic salary.
     * @param riceSubsidy Monthly rice subsidy.
     * @param phoneAllowance Monthly phone allowance.
     * @param clothingAllowance Monthly clothing allowance.
     * @param grossSemiMonthlyRate Gross semi-monthly rate.
     * @param hourlyRate Hourly pay rate.
     */
    public CompensationDetails(double basicSalary, double riceSubsidy, double phoneAllowance, double clothingAllowance,
                               double grossSemiMonthlyRate, double hourlyRate) {
        this.basicSalary = basicSalary;
        this.riceSubsidy = riceSubsidy;
        this.phoneAllowance = phoneAllowance;
        this.clothingAllowance = clothingAllowance;
        this.grossSemiMonthlyRate = grossSemiMonthlyRate;
        this.hourlyRate = hourlyRate;
    }

    public double getBasicSalary() { return basicSalary; }
    public double getRiceSubsidy() { return riceSubsidy; }
    public double getPhoneAllowance() { return phoneAllowance; }
    public double getClothingAllowance() { return clothingAllowance; }
    public double getGrossSemiMonthlyRate() { return grossSemiMonthlyRate; }
    public double getHourlyRate() { return hourlyRate; }

    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }
    public void setRiceSubsidy(double riceSubsidy) { this.riceSubsidy = riceSubsidy; }
    public void setPhoneAllowance(double phoneAllowance) { this.phoneAllowance = phoneAllowance; }
    public void setClothingAllowance(double clothingAllowance) { this.clothingAllowance = clothingAllowance; }
    public void setGrossSemiMonthlyRate(double grossSemiMonthlyRate) { this.grossSemiMonthlyRate = grossSemiMonthlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }
}