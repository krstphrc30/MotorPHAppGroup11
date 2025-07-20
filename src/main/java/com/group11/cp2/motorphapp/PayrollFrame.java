package com.group11.cp2.motorphapp;
/**
 * Displays employee details and payroll computation in the MotorPH Payroll System.
 *
 * @author Kristopher Carlo, Clarinda, Pil, Janice (Group 11)
 */
import javax.swing.*;
import java.awt.*;
import java.time.YearMonth;
import java.util.List;

/**
 * JForm-generated PayrollFrame to display employee details and payroll computation.
 */
public class PayrollFrame extends javax.swing.JFrame {
    private final Dashboard dashboard; // Nullable to allow standalone use
    private Employee employee; // Non-final to allow dynamic updates
    private final List<AttendanceRecord> attendance;

    /**
     * Creates new form PayrollFrame with employee details and payroll computation.
     * @param dashboard Optional Dashboard instance for navigation (can be null)
     * @param employee Initial employee (can be null)
     * @param attendance List of attendance records
     */
    public PayrollFrame(Dashboard dashboard, Employee employee, List<AttendanceRecord> attendance) {
        this.dashboard = dashboard; // Allow null
        this.employee = employee;
        this.attendance = attendance;
        initComponents();
        setTitle("MotorPH Payroll System - Employee Payroll");
        setLocationRelativeTo(null); // Center the window
        System.out.println("PayrollFrame: Initialized with dashboard=" + (dashboard != null ? "present" : "null") +
                ", employee=" + (employee != null ? employee.getEmployeeNumber() + ", " + employee.getLastName() : "null") +
                ", attendance records=" + (attendance != null ? attendance.size() : "null"));
        // Populate month and year choices
        String[] months = {"January", "February", "March", "April", "May", "June",
                           "July", "August", "September", "October", "November", "December"};
        for (String month : months) {
            choice2.add(month);
        }
        choice2.select(YearMonth.now().getMonthValue() - 1);
        for (int year = YearMonth.now().getYear() - 5; year <= YearMonth.now().getYear() + 5; year++) {
            choice1.add(String.valueOf(year));
        }
        choice1.select(String.valueOf(YearMonth.now().getYear()));
        // Update UI with initial employee details
        updateEmployeeDetails();
    }

    /**
     * Updates employee detail fields based on the current employee.
     */
    private void updateEmployeeDetails() {
        System.out.println("updateEmployeeDetails: Employee is " + (employee != null ? employee.getEmployeeNumber() + ", " + employee.getLastName() : "null"));
        if (employee != null) {
            jTextField1.setText(String.valueOf(employee.getEmployeeNumber()));
            System.out.println("Setting jTextField1: " + employee.getEmployeeNumber());
            jLabel17.setText(employee.getLastName() != null ? employee.getLastName() : "N/A");
            System.out.println("Setting jLabel17 (Last Name): " + jLabel17.getText());
            jLabel21.setText(employee.getFirstName() != null ? employee.getFirstName() : "N/A");
            System.out.println("Setting jLabel21 (First Name): " + jLabel21.getText());
            jLabel20.setText(employee.getBirthday() != null ? employee.getBirthday().toString() : "N/A");
            System.out.println("Setting jLabel20 (Birthday): " + jLabel20.getText());
            jLabel26.setText(employee.getPosition() != null ? employee.getPosition() : "N/A");
            System.out.println("Setting jLabel26 (Position): " + jLabel26.getText());
            jLabel29.setText(employee.getStatus() != null ? employee.getStatus() : "N/A");
            System.out.println("Setting jLabel29 (Status): " + jLabel29.getText());
            GovernmentDetails gov = employee.getGovernmentDetails();
            if (gov != null) {
                jLabel24.setText(gov.getSssNumber() != null && gov.getSssNumber().matches("\\d{2}-\\d{7}-\\d{1}") ? 
                    gov.getSssNumber() : "Invalid SSS (XX-XXXXXXX-X)");
                System.out.println("Setting jLabel24 (SSS Number): " + jLabel24.getText());
                jLabel22.setText(gov.getPhilHealthNumber() != null && gov.getPhilHealthNumber().matches("\\d{9,12}") ? 
                    gov.getPhilHealthNumber() : "Invalid PhilHealth (9-12 digits)");
                System.out.println("Setting jLabel22 (PhilHealth Number): " + jLabel22.getText());
                jLabel25.setText(gov.getTinNumber() != null && gov.getTinNumber().matches("\\d{3}-\\d{3}-\\d{3}-\\d{3}") ? 
                    gov.getTinNumber() : "Invalid TIN (XXX-XXX-XXX-XXX)");
                System.out.println("Setting jLabel25 (TIN Number): " + jLabel25.getText());
                jLabel23.setText(gov.getPagIbigNumber() != null && gov.getPagIbigNumber().matches("\\d{9,12}") ? 
                    gov.getPagIbigNumber() : "Invalid Pag-IBIG (9-12 digits)");
                System.out.println("Setting jLabel23 (Pag-IBIG Number): " + jLabel23.getText());
            } else {
                jLabel24.setText("N/A");
                jLabel22.setText("N/A");
                jLabel25.setText("N/A");
                jLabel23.setText("N/A");
                System.out.println("Setting government details to N/A (gov is null)");
            }
            CompensationDetails comp = employee.getCompensationDetails();
            if (comp != null) {
                jLabel27.setText(String.format("%.2f", comp.getBasicSalary()));
                jLabel28.setText(String.format("%.2f", comp.getHourlyRate()));
            } else {
                jLabel27.setText("N/A");
                jLabel28.setText("N/A");
                System.out.println("Warning: CompensationDetails is null for employee #" + employee.getEmployeeNumber());
            }
            System.out.println("Setting jLabel27 (Basic Salary): " + jLabel27.getText());
            System.out.println("Setting jLabel28 (Hourly Rate): " + jLabel28.getText());
        } else {
            jTextField1.setText("");
            jLabel17.setText("");
            jLabel21.setText("");
            jLabel20.setText("");
            jLabel26.setText("");
            jLabel29.setText("");
            jLabel24.setText("");
            jLabel22.setText("");
            jLabel25.setText("");
            jLabel23.setText("");
            jLabel27.setText("");
            jLabel28.setText("");
            System.out.println("Setting all employee fields to N/A (employee is null)");
        }
        // Clear payroll summary fields
        clearPayrollSummary();
        // Force UI repaint
        jPanel5.revalidate();
        jPanel5.repaint();
        jPanel4.revalidate();
        jPanel4.repaint();
    }

    /**
     * Clears payroll summary fields to prevent stale data.
     */
    private void clearPayrollSummary() {
        jLabel45.setText(""); // Month
        jLabel53.setText(""); // Total Regular Hours
        jLabel52.setText(""); // Total Overtime Hours
        jLabel51.setText(""); // Gross Salary
        jLabel54.setText(""); // SSS Contribution
        jLabel44.setText(""); // PhilHealth Contribution
        jLabel46.setText(""); // Pag-IBIG Contribution
        jLabel47.setText(""); // Total Deductions
        jLabel48.setText(""); // Withholding Tax
        jLabel49.setText(""); // Allowances
        jLabel50.setText(""); // Base Salary
        jLabel55.setText(""); // Net Salary
        System.out.println("clearPayrollSummary: Cleared all payroll summary fields");
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        etchedBorder1 = (javax.swing.border.EtchedBorder)javax.swing.BorderFactory.createEtchedBorder();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        choice1 = new java.awt.Choice();
        choice2 = new java.awt.Choice();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jPanel1.setBackground(new java.awt.Color(14, 39, 94));
        jPanel1.setBorder(new javax.swing.border.MatteBorder(null));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel10.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(14, 49, 113));
        jLabel10.setText("Employee Details");

        jPanel5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(14, 39, 94), 3, true));
        jPanel5.setForeground(new java.awt.Color(153, 153, 153));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(14, 49, 113));
        jLabel2.setText("Employee Number:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(14, 49, 113));
        jLabel3.setText("Last Name:");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(14, 49, 113));
        jLabel4.setText("First Name:");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(14, 49, 113));
        jLabel5.setText("Birthday:");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(14, 49, 113));
        jLabel11.setText("SSS Number:");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(14, 49, 113));
        jLabel12.setText("Philhealth Number:");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(14, 49, 113));
        jLabel13.setText("Pag Ibig Number:");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(14, 49, 113));
        jLabel14.setText("TIN Number:");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(14, 49, 113));
        jLabel6.setText("Position:");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(14, 49, 113));
        jLabel7.setText("Status:");

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(14, 49, 113));
        jLabel15.setText("Basic Salary:");

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(14, 49, 113));
        jLabel16.setText("Hourly Rate:");

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel17.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jLabel17.setOpaque(true);

        jTextField1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel20.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jLabel20.setOpaque(true);

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel21.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jLabel21.setOpaque(true);

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel22.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jLabel22.setOpaque(true);

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel23.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jLabel23.setOpaque(true);

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel24.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jLabel24.setOpaque(true);

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel25.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jLabel25.setOpaque(true);

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel26.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jLabel26.setOpaque(true);

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel27.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jLabel27.setOpaque(true);

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel28.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jLabel28.setOpaque(true);

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel29.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jLabel29.setOpaque(true);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 144, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(59, 59, 59))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addGap(34, 34, 34)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addGap(30, 30, 30)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(30, 30, 30)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addGap(40, 40, 40)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16)
                            .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(50, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(40, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(202, 202, 202))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(58, 58, 58)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(249, 249, 249));
        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        choice1.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N

        choice2.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N

        jButton7.setBackground(new java.awt.Color(0, 102, 204));
        jButton7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton7.setForeground(new java.awt.Color(255, 255, 255));
        jButton7.setText("Compute");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setBackground(new java.awt.Color(0, 102, 204));
        jButton8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton8.setForeground(new java.awt.Color(255, 255, 255));
        jButton8.setText("Cancel");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(14, 49, 113));
        jLabel8.setText("Payroll Report");

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(14, 49, 113));
        jLabel18.setText("Month:");

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(14, 49, 113));
        jLabel19.setText("Year:");

        jPanel4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(14, 39, 94), 3, true));

        jLabel31.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(14, 49, 113));
        jLabel31.setText("Monthly Salary Summary");

        jLabel30.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(14, 49, 113));
        jLabel30.setText("Month:");

        jLabel32.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(14, 49, 113));
        jLabel32.setText("Total Regular Hours:");

        jLabel33.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(14, 49, 113));
        jLabel33.setText("Total Overtime Hours:");

        jLabel34.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(14, 49, 113));
        jLabel34.setText("Base Salary:");

        jLabel35.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(14, 49, 113));
        jLabel35.setText("Gross Salary:");

        jLabel36.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(14, 49, 113));
        jLabel36.setText("SSS Contribution:");

        jLabel37.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(14, 49, 113));
        jLabel37.setText("PhilHealth Contribution:");

        jLabel38.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(14, 49, 113));
        jLabel38.setText("Pag-IBIG Contribution:");

        jLabel39.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(14, 49, 113));
        jLabel39.setText("Total Deductions:");

        jLabel40.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(14, 49, 113));
        jLabel40.setText("Withholding Tax:");

        jLabel41.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel41.setForeground(new java.awt.Color(14, 49, 113));
        jLabel41.setText("Allowances");

        jLabel42.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(14, 49, 113));
        jLabel42.setText("Net Salary:");

        jLabel44.setBackground(new java.awt.Color(255, 255, 255));
        jLabel44.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel44.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jLabel44.setOpaque(true);

        jLabel45.setBackground(new java.awt.Color(255, 255, 255));
        jLabel45.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel45.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jLabel45.setOpaque(true);

        jLabel46.setBackground(new java.awt.Color(255, 255, 255));
        jLabel46.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel46.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jLabel46.setOpaque(true);

        jLabel47.setBackground(new java.awt.Color(255, 255, 255));
        jLabel47.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel47.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jLabel47.setOpaque(true);

        jLabel48.setBackground(new java.awt.Color(255, 255, 255));
        jLabel48.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel48.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jLabel48.setOpaque(true);

        jLabel49.setBackground(new java.awt.Color(255, 255, 255));
        jLabel49.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel49.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jLabel49.setOpaque(true);

        jLabel50.setBackground(new java.awt.Color(255, 255, 255));
        jLabel50.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel50.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jLabel50.setOpaque(true);

        jLabel51.setBackground(new java.awt.Color(255, 255, 255));
        jLabel51.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel51.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jLabel51.setOpaque(true);

        jLabel52.setBackground(new java.awt.Color(255, 255, 255));
        jLabel52.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel52.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jLabel52.setOpaque(true);

        jLabel53.setBackground(new java.awt.Color(255, 255, 255));
        jLabel53.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel53.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jLabel53.setOpaque(true);

        jLabel54.setBackground(new java.awt.Color(255, 255, 255));
        jLabel54.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel54.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jLabel54.setOpaque(true);

        jLabel55.setBackground(new java.awt.Color(255, 255, 255));
        jLabel55.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel55.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jLabel55.setOpaque(true);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(86, 86, 86)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel36)
                            .addComponent(jLabel35)
                            .addComponent(jLabel30)
                            .addComponent(jLabel32)
                            .addComponent(jLabel37)
                            .addComponent(jLabel39)
                            .addComponent(jLabel38)
                            .addComponent(jLabel40)
                            .addComponent(jLabel41)
                            .addComponent(jLabel33)
                            .addComponent(jLabel34))
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel54, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel44, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel46, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel47, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel48, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(101, 101, 101)
                                .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel42)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(51, 51, 51))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(198, 198, 198)
                .addComponent(jLabel31)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel31)
                .addGap(37, 37, 37)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30))
                .addGap(28, 28, 28)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel32)
                    .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel33)
                    .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35))
                .addGap(28, 28, 28)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36))
                .addGap(28, 28, 28)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37))
                .addGap(28, 28, 28)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38))
                .addGap(28, 28, 28)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39))
                .addGap(28, 28, 28)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel40)
                        .addGap(38, 38, 38)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41))
                .addGap(28, 28, 28)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel34)
                    .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel42))
                .addGap(23, 23, 23))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton7)
                        .addGap(55, 55, 55)
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(64, 64, 64))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(145, 145, 145)
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(choice2, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 125, Short.MAX_VALUE)
                        .addComponent(jLabel19)
                        .addGap(19, 19, 19)
                        .addComponent(choice1, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(164, 164, 164))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(214, 214, 214))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(choice2, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                    .addComponent(choice1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton8)
                    .addComponent(jButton7))
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        if (employee == null) {
            clearPayrollSummary();
            JOptionPane.showMessageDialog(this, "Error: No employee selected. Please enter a valid employee number.", "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("Payroll computation error: No employee selected");
            return;
        }
        if (employee.getCompensationDetails() == null) {
            clearPayrollSummary();
            JOptionPane.showMessageDialog(this, "Error: Compensation details missing for employee #" + employee.getEmployeeNumber(), "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("Payroll computation error: CompensationDetails is null for employee #" + employee.getEmployeeNumber());
            return;
        }
        try {
            int year = Integer.parseInt(choice1.getSelectedItem());
            int month = choice2.getSelectedIndex() + 1;
            YearMonth yearMonth = YearMonth.of(year, month);
            PayrollReport report = new PayrollReport(employee, yearMonth, attendance);
            // Check for empty attendance records
            if (report.getMonthlyRegularHours() == 0.0 && report.getMonthlyOvertimeHours() == 0.0) {
                clearPayrollSummary();
                JOptionPane.showMessageDialog(this, "No attendance records found for " + employee.getLastName() + ", " + employee.getFirstName() + " in " + yearMonth, "Warning", JOptionPane.WARNING_MESSAGE);
                System.out.println("jButton7ActionPerformed: Warning - No attendance records for " + employee.getEmployeeNumber() + " in " + yearMonth);
            }
            // Update payroll summary fields
            jLabel45.setText(yearMonth.getMonth().toString() + " " + year);
            System.out.println("Setting jLabel45 (Month): " + jLabel45.getText());
            jLabel53.setText(String.format("%.2f", report.getMonthlyRegularHours()));
            System.out.println("Setting jLabel53 (Total Regular Hours): " + jLabel53.getText());
            jLabel52.setText(String.format("%.2f", report.getMonthlyOvertimeHours()));
            System.out.println("Setting jLabel52 (Total Overtime Hours): " + jLabel52.getText());
            jLabel51.setText(String.format("%.2f", report.getGrossSalary()));
            System.out.println("Setting jLabel51 (Gross Salary): " + jLabel51.getText());
            jLabel54.setText(String.format("%.2f", report.getDeductions().getSss()));
            System.out.println("Setting jLabel54 (SSS Contribution): " + jLabel54.getText());
            jLabel44.setText(String.format("%.2f", report.getDeductions().getPhilHealth()));
            System.out.println("Setting jLabel44 (PhilHealth Contribution): " + jLabel44.getText());
            jLabel46.setText(String.format("%.2f", report.getDeductions().getPagIbig()));
            System.out.println("Setting jLabel46 (Pag-IBIG Contribution): " + jLabel46.getText());
            jLabel47.setText(String.format("%.2f", report.getDeductions().getTotalDeductions()));
            System.out.println("Setting jLabel47 (Total Deductions): " + jLabel47.getText());
            jLabel48.setText(String.format("%.2f", report.getDeductions().getWithholdingTax()));
            System.out.println("Setting jLabel48 (Withholding Tax): " + jLabel48.getText());
            jLabel49.setText(String.format("%.2f", report.getTotalAllowances()));
            System.out.println("Setting jLabel49 (Allowances): " + jLabel49.getText());
            jLabel50.setText(String.format("%.2f", report.getBaseSalary()));
            System.out.println("Setting jLabel50 (Base Salary): " + jLabel50.getText());
            jLabel55.setText(String.format("%.2f", report.getNetSalary()));
            System.out.println("Setting jLabel55 (Net Salary): " + jLabel55.getText());
            System.out.println("jButton7ActionPerformed: Payroll summary updated for employee #" + employee.getEmployeeNumber() + " in " + yearMonth);
            System.out.println("Attendance records passed: " + (attendance != null ? attendance.size() : "null"));
            // Force UI repaint
            jPanel4.revalidate();
            jPanel4.repaint();
            jPanel5.revalidate();
            jPanel5.repaint();
        } catch (NumberFormatException ex) {
            clearPayrollSummary();
            JOptionPane.showMessageDialog(this, "Error: Please select a valid year.", "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("Payroll computation error: Invalid year format - " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            clearPayrollSummary();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("Payroll computation error: " + ex.getMessage());
        } catch (Exception ex) {
            clearPayrollSummary();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("Payroll computation error: " + ex.getMessage());
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        dispose();       
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
         String empNumberText = jTextField1.getText().trim();
        System.out.println("jTextField1ActionPerformed: Entered employee number: " + empNumberText);
        if (empNumberText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Employee number cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("jTextField1ActionPerformed: Error - Employee number is empty");
            return;
        }
        try {
            int empNumber = Integer.parseInt(empNumberText);
            System.out.println("jTextField1ActionPerformed: Parsed employee number: " + empNumber);
            List<Employee> employees = MotorPHApp.getEmployees();
            System.out.println("jTextField1ActionPerformed: Total employees available: " + (employees != null ? employees.size() : "null"));
            if (employees == null || employees.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No employees available.", "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("jTextField1ActionPerformed: Error - Employee list is null or empty");
                jTextField1.setText("");
                employee = null;
                updateEmployeeDetails();
                return;
            }
            employee = employees.stream()
                    .filter(e -> e.getEmployeeNumber() == empNumber)
                    .findFirst()
                    .orElse(null);
            if (employee == null) {
                JOptionPane.showMessageDialog(this, "Employee not found.", "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("jTextField1ActionPerformed: Error - Employee not found for number: " + empNumber);
                jTextField1.setText("");
            } else {
                System.out.println("jTextField1ActionPerformed: Loaded employee: " + employee.getEmployeeNumber() + ", " + employee.getLastName());
                System.out.println("jTextField1ActionPerformed: Employee details - " +
                    "FirstName: " + employee.getFirstName() +
                    ", Birthday: " + (employee.getBirthday() != null ? employee.getBirthday().toString() : "null") +
                    ", Position: " + employee.getPosition() +
                    ", Status: " + employee.getStatus() +
                    ", GovernmentDetails: " + (employee.getGovernmentDetails() != null ? 
                        "SSS=" + employee.getGovernmentDetails().getSssNumber() + 
                        ", TIN=" + employee.getGovernmentDetails().getTinNumber() + 
                        ", PhilHealth=" + employee.getGovernmentDetails().getPhilHealthNumber() + 
                        ", PagIbig=" + employee.getGovernmentDetails().getPagIbigNumber() : "null") +
                    ", CompensationDetails: " + (employee.getCompensationDetails() != null ? 
                        "Salary=" + employee.getCompensationDetails().getBasicSalary() + 
                        ", Hourly=" + employee.getCompensationDetails().getHourlyRate() +
                        ", Rice=" + employee.getCompensationDetails().getRiceSubsidy() +
                        ", Phone=" + employee.getCompensationDetails().getPhoneAllowance() +
                        ", Clothing=" + employee.getCompensationDetails().getClothingAllowance() : "null"));
            }
            updateEmployeeDetails();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid employee number format.", "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("jTextField1ActionPerformed: Error - Invalid employee number format: " + ex.getMessage());
            jTextField1.setText("");
            employee = null;
            updateEmployeeDetails();
        }
    }//GEN-LAST:event_jTextField1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PayrollFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new PayrollFrame(null, null, null).setVisible(true));
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Choice choice1;
    private java.awt.Choice choice2;
    private javax.swing.border.EtchedBorder etchedBorder1;
    protected javax.swing.JButton jButton7;
    protected javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
