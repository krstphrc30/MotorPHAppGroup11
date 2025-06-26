
package com.group11.cp2.motorphapp;

import java.util.ArrayList;

public class Deductions {
    private double sss;
    private double philHealth;
    private double pagIbig;
    private double withholdingTax;

    public Deductions(double monthlyGrossSalary, double basicSalary) {
        this.sss = calculateSSSContribution(monthlyGrossSalary);
        this.philHealth = calculatePhilHealthContribution(basicSalary); // Use basicSalary for PhilHealth
        this.pagIbig = calculatePagIbigContribution(monthlyGrossSalary);
        double taxableIncome = monthlyGrossSalary - (sss + philHealth + pagIbig);
        this.withholdingTax = calculateWithholdingTax(taxableIncome);
    }

    public double getSss() { return sss; }
    public double getPhilHealth() { return philHealth; }
    public double getPagIbig() { return pagIbig; }
    public double getWithholdingTax() { return withholdingTax; }

    public double getTotalDeductions() {
        return sss + philHealth + pagIbig + withholdingTax;
    }

    private static double calculateSSSContribution(double monthlyGrossSalary) {
        class SalaryContribution {
            double salaryLimit;
            double contribution;

            SalaryContribution(double salaryLimit, double contribution) {
                this.salaryLimit = salaryLimit;
                this.contribution = contribution;
            }
        }

        ArrayList<SalaryContribution> sssTable = new ArrayList<>();
        sssTable.add(new SalaryContribution(3250.0, 135.00));
        sssTable.add(new SalaryContribution(3750.0, 157.50));
        sssTable.add(new SalaryContribution(4250.0, 180.00));
        sssTable.add(new SalaryContribution(4750.0, 202.50));
        sssTable.add(new SalaryContribution(5250.0, 225.00));
        sssTable.add(new SalaryContribution(5750.0, 247.50));
        sssTable.add(new SalaryContribution(6250.0, 270.00));
        sssTable.add(new SalaryContribution(6750.0, 292.50));
        sssTable.add(new SalaryContribution(7250.0, 315.00));
        sssTable.add(new SalaryContribution(7750.0, 337.50));
        sssTable.add(new SalaryContribution(8250.0, 360.00));
        sssTable.add(new SalaryContribution(8750.0, 382.50));
        sssTable.add(new SalaryContribution(9250.0, 405.00));
        sssTable.add(new SalaryContribution(9750.0, 427.50));
        sssTable.add(new SalaryContribution(10250.0, 450.00));
        sssTable.add(new SalaryContribution(10750.0, 472.50));
        sssTable.add(new SalaryContribution(11250.0, 495.00));
        sssTable.add(new SalaryContribution(11750.0, 517.50));
        sssTable.add(new SalaryContribution(12250.0, 540.00));
        sssTable.add(new SalaryContribution(12750.0, 562.50));
        sssTable.add(new SalaryContribution(13250.0, 585.00));
        sssTable.add(new SalaryContribution(13750.0, 607.50));
        sssTable.add(new SalaryContribution(14250.0, 630.00));
        sssTable.add(new SalaryContribution(14750.0, 652.50));
        sssTable.add(new SalaryContribution(15250.0, 675.00));
        sssTable.add(new SalaryContribution(15750.0, 697.50));
        sssTable.add(new SalaryContribution(16250.0, 720.00));
        sssTable.add(new SalaryContribution(16750.0, 742.50));
        sssTable.add(new SalaryContribution(17250.0, 765.00));
        sssTable.add(new SalaryContribution(17750.0, 787.50));
        sssTable.add(new SalaryContribution(18250.0, 810.00));
        sssTable.add(new SalaryContribution(18750.0, 832.50));
        sssTable.add(new SalaryContribution(19250.0, 855.00));
        sssTable.add(new SalaryContribution(19750.0, 877.50));
        sssTable.add(new SalaryContribution(20250.0, 900.00));
        sssTable.add(new SalaryContribution(20750.0, 922.50));
        sssTable.add(new SalaryContribution(21250.0, 945.00));
        sssTable.add(new SalaryContribution(21750.0, 967.50));
        sssTable.add(new SalaryContribution(22250.0, 990.00));
        sssTable.add(new SalaryContribution(22750.0, 1012.50));
        sssTable.add(new SalaryContribution(23250.0, 1035.00));
        sssTable.add(new SalaryContribution(23750.0, 1057.50));
        sssTable.add(new SalaryContribution(24250.0, 1080.00));
        sssTable.add(new SalaryContribution(24750.0, 1102.50));

        for (SalaryContribution entry : sssTable) {
            if (monthlyGrossSalary < entry.salaryLimit) {
                return entry.contribution;
            }
        }

        return 1125.00; // Max contribution for salary >= 24,750
    }

    private static double calculatePhilHealthContribution(double basicSalary) {
        double monthlyPremium = basicSalary * 0.03;
        double employeeShare = monthlyPremium / 2; // Employee pays 50%

        if (basicSalary <= 10000.0) return 150.00; // Min: 300 / 2
        else if (basicSalary >= 60000.0) return 900.00; // Max: 1,800 / 2
        else return employeeShare; // 3% / 2
    }

    private static double calculatePagIbigContribution(double monthlyGrossSalary) {
        double contribution = monthlyGrossSalary <= 1500.0 ? monthlyGrossSalary * 0.01 : monthlyGrossSalary * 0.02;
        return Math.min(contribution, 100.00); // Max: 100
    }

    private static double calculateWithholdingTax(double taxableIncome) {
        if (taxableIncome <= 20832.0) return 0.0;
        else if (taxableIncome < 33333.0) return (taxableIncome - 20833.0) * 0.20;
        else if (taxableIncome < 66667.0) return 2500.0 + (taxableIncome - 33333.0) * 0.25;
        else if (taxableIncome < 166667.0) return 10833.0 + (taxableIncome - 66667.0) * 0.30;
        else if (taxableIncome < 666667.0) return 40833.33 + (taxableIncome - 166667.0) * 0.32;
        else return 200833.33 + (taxableIncome - 666667.0) * 0.35;
    }
}
