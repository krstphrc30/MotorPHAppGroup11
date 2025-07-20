/**
 * Stores government identification numbers for an employee in the MotorPH Payroll System.
 *
 * @author Kristopher Carlo, Clarinda, Pil, Janice (Group 11)
 */
package com.group11.cp2.motorphapp;

public class GovernmentDetails {
    private String sssNumber;
    private String philHealthNumber;
    private String tinNumber;
    private String pagIbigNumber;

    /**
     * Creates a GovernmentDetails instance with the specified ID numbers.
     *
     * @param sssNumber Social Security System number.
     * @param philHealthNumber PhilHealth number.
     * @param tinNumber Tax Identification Number.
     * @param pagIbigNumber Pag-IBIG Fund number.
     */
    public GovernmentDetails(String sssNumber, String philHealthNumber, String tinNumber, String pagIbigNumber) {
        this.sssNumber = sssNumber;
        this.philHealthNumber = philHealthNumber;
        this.tinNumber = tinNumber;
        this.pagIbigNumber = pagIbigNumber;
    }

    public String getSssNumber() { return sssNumber; }
    public String getPhilHealthNumber() { return philHealthNumber; }
    public String getTinNumber() { return tinNumber; }
    public String getPagIbigNumber() { return pagIbigNumber; }

    public void setSssNumber(String sssNumber) { this.sssNumber = sssNumber; }
    public void setPhilHealthNumber(String philHealthNumber) { this.philHealthNumber = philHealthNumber; }
    public void setTinNumber(String tinNumber) { this.tinNumber = tinNumber; }
    public void setPagIbigNumber(String pagIbigNumber) { this.pagIbigNumber = pagIbigNumber; }
}