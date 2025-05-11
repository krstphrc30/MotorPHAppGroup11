/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group11.cp2.motorphapp;

/**
 *
 * @author Pil,Kristopher,Janice,Clarinda
 */
public class Deductions {
    private double sss;
    private double philHealth;
    private double pagIbig;
    private double withholdingTax;

    public void setDeductions(double gross) {
        this.sss = gross * 0.045;
        this.philHealth = gross * 0.03;
        this.pagIbig = gross * 0.02;
        this.withholdingTax = gross * 0.10;
    }

    public double getTotal() {
        return sss + philHealth + pagIbig + withholdingTax;
    }
}
