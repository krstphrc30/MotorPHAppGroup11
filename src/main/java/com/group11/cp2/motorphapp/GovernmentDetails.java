package com.group11.cp2.motorphapp;

/**
 * Stores mandatory government identification numbers for an employee.
 * Author: Carlo
 */
public class GovernmentDetails {
    private String sssNumber;
    private String tinNumber;
    private String philHealthNumber;
    private String pagIbigNumber;

    // Constructor
    public GovernmentDetails(String sssNumber, String tinNumber, String philHealthNumber, String pagIbigNumber) {
        this.sssNumber = sssNumber;
        this.tinNumber = tinNumber;
        this.philHealthNumber = philHealthNumber;
        this.pagIbigNumber = pagIbigNumber;
    }

    // Getters
    public String getSssNumber() {
        return sssNumber;
    }
    public String getPhilHealthNumber() {
        return philHealthNumber;
    }
    public String getTinNumber() {
        return tinNumber;
    }
    public String getPagIbigNumber() {
        return pagIbigNumber;
    }

    // Setters
    public void setSssNumber(String sssNumber) {
        this.sssNumber = sssNumber;
    }

     public void setPhilHealthNumber(String philHealthNumber) {
        this.philHealthNumber = philHealthNumber;
    }
     
    public void setTinNumber(String tinNumber) {
        this.tinNumber = tinNumber;
    }

    public void setPagIbigNumber(String pagIbigNumber) {
        this.pagIbigNumber = pagIbigNumber;
    }
    

}