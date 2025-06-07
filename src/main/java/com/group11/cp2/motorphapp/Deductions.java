package com.group11.cp2.motorphapp;

import java.util.ArrayList;

public class Deductions {
    private static final int WEEKS_PER_MONTH = 4;

    private double sss;
    private double philHealth;
    private double pagIbig;
    private double withholdingTax;

    public Deductions(double grossWeeklySalary) {
        this.sss = calculateSSSContribution(grossWeeklySalary);
        this.philHealth = calculatePhilHealthContribution(grossWeeklySalary);
        this.pagIbig = calculatePagIbigContribution(grossWeeklySalary);
        this.withholdingTax = calculateWithholdingTax(grossWeeklySalary);
    }

    public double getSss() { return sss; }
    public double getPhilHealth() { return philHealth; }
    public double getPagIbig() { return pagIbig; }
    public double getWithholdingTax() { return withholdingTax; }

    public double getTotalDeductions() {
        return sss + philHealth + pagIbig;
    }

    private static double calculateSSSContribution(double grossWeeklySalary) {
        class SalaryContribution {
            double salaryLimit;
            double contribution;

            SalaryContribution(double salaryLimit, double contribution) {
                this.salaryLimit = salaryLimit;
                this.contribution = contribution;
            }
        }

        ArrayList<SalaryContribution> sssTable = new ArrayList<>();
        sssTable.add(new SalaryContribution(3250.0 / 4, 135.00));
        sssTable.add(new SalaryContribution(3750.0 / 4, 157.50));
        sssTable.add(new SalaryContribution(4250.0 / 4, 180.00));
        sssTable.add(new SalaryContribution(4750.0 / 4, 202.50));
        sssTable.add(new SalaryContribution(5250.0 / 4, 225.00));
        sssTable.add(new SalaryContribution(5750.0 / 4, 247.50));
        sssTable.add(new SalaryContribution(6250.0 / 4, 270.00));
        sssTable.add(new SalaryContribution(6750.0 / 4, 292.50));
        sssTable.add(new SalaryContribution(7250.0 / 4, 315.00));
        sssTable.add(new SalaryContribution(7750.0 / 4, 337.50));
        sssTable.add(new SalaryContribution(8250.0 / 4, 360.00));
        sssTable.add(new SalaryContribution(8750.0 / 4, 382.50));
        sssTable.add(new SalaryContribution(9250.0 / 4, 405.00));
        sssTable.add(new SalaryContribution(9750.0 / 4, 427.50));
        sssTable.add(new SalaryContribution(10250.0 / 4, 450.00));
        sssTable.add(new SalaryContribution(10750.0 / 4, 472.50));
        sssTable.add(new SalaryContribution(11250.0 / 4, 495.00));
        sssTable.add(new SalaryContribution(11750.0 / 4, 517.50));
        sssTable.add(new SalaryContribution(12250.0 / 4, 540.00));
        sssTable.add(new SalaryContribution(12750.0 / 4, 562.50));
        sssTable.add(new SalaryContribution(13250.0 / 4, 585.00));
        sssTable.add(new SalaryContribution(13750.0 / 4, 607.50));
        sssTable.add(new SalaryContribution(14250.0 / 4, 630.00));
        sssTable.add(new SalaryContribution(14750.0 / 4, 652.50));
        sssTable.add(new SalaryContribution(15250.0 / 4, 675.00));
        sssTable.add(new SalaryContribution(15750.0 / 4, 697.50));
        sssTable.add(new SalaryContribution(16250.0 / 4, 720.00));
        sssTable.add(new SalaryContribution(16750.0 / 4, 742.50));
        sssTable.add(new SalaryContribution(17250.0 / 4, 765.00));
        sssTable.add(new SalaryContribution(17750.0 / 4, 787.50));
        sssTable.add(new SalaryContribution(18250.0 / 4, 810.00));
        sssTable.add(new SalaryContribution(18750.0 / 4, 832.50));
        sssTable.add(new SalaryContribution(19250.0 / 4, 855.00));
        sssTable.add(new SalaryContribution(19750.0 / 4, 877.50));
        sssTable.add(new SalaryContribution(20250.0 / 4, 900.00));
        sssTable.add(new SalaryContribution(20750.0 / 4, 922.50));
        sssTable.add(new SalaryContribution(21250.0 / 4, 945.00));
        sssTable.add(new SalaryContribution(21750.0 / 4, 967.50));
        sssTable.add(new SalaryContribution(22250.0 / 4, 990.00));
        sssTable.add(new SalaryContribution(22750.0 / 4, 1012.50));
        sssTable.add(new SalaryContribution(23250.0 / 4, 1035.00));
        sssTable.add(new SalaryContribution(23750.0 / 4, 1057.50));
        sssTable.add(new SalaryContribution(24250.0 / 4, 1080.00));
        sssTable.add(new SalaryContribution(24750.0 / 4, 1102.50));

        for (SalaryContribution entry : sssTable) {
            if (grossWeeklySalary <= entry.salaryLimit) {
                return entry.contribution / WEEKS_PER_MONTH;
            }
        }

        return 1125.00 / WEEKS_PER_MONTH; // Max contribution
    }

    private static double calculatePhilHealthContribution(double grossWeeklySalary) {
        double monthlyEquivalent = grossWeeklySalary * WEEKS_PER_MONTH;
        double monthlyPremium = monthlyEquivalent * 0.03;
        double contribution = monthlyPremium / 2 / WEEKS_PER_MONTH;

        if (monthlyEquivalent <= 10000) return 150.00 / WEEKS_PER_MONTH;
        else if (monthlyEquivalent >= 60000) return 900.00 / WEEKS_PER_MONTH;
        else return contribution;
    }

    private static double calculatePagIbigContribution(double grossWeeklySalary) {
        double contribution = grossWeeklySalary <= 1500 ? grossWeeklySalary * 0.01 : grossWeeklySalary * 0.02;
        return Math.min(contribution, 100.00 / WEEKS_PER_MONTH);
    }

    private static double calculateWithholdingTax(double grossWeeklySalary) {
        double monthly = grossWeeklySalary * WEEKS_PER_MONTH;

        if (monthly <= 20833) return 0.0;
        else if (monthly <= 33333) return ((monthly - 20833) * 0.20) / WEEKS_PER_MONTH;
        else if (monthly <= 66667) return (2500 + (monthly - 33333) * 0.25) / WEEKS_PER_MONTH;
        else if (monthly <= 166667) return (10833 + (monthly - 66667) * 0.30) / WEEKS_PER_MONTH;
        else if (monthly <= 666667) return (40833.33 + (monthly - 166667) * 0.32) / WEEKS_PER_MONTH;
        else return (200833.33 + (monthly - 666667) * 0.35) / WEEKS_PER_MONTH;
    }
}
