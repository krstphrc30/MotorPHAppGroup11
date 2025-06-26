
package com.group11.cp2.motorphapp;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class User extends javax.swing.JFrame {
    private String username;
    private String password;
    private String role;
    private Employee employee;
    private String securityQuestion;
    private String securityAnswer;
    private static User loggedInUser;

    // GUI Components from UserLogin
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JPasswordField jTextField3; // Changed to JPasswordField for security

    public User(String username, String password, String role, Employee employee, String securityQuestion, String securityAnswer) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.employee = employee;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        initComponents();
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public Employee getEmployee() { return employee; }
    public String getSecurityQuestion() { return securityQuestion; }
    public String getSecurityAnswer() { return securityAnswer; }

    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public void setSecurityQuestion(String securityQuestion) { this.securityQuestion = securityQuestion; }
    public void setSecurityAnswer(String securityAnswer) { this.securityAnswer = securityAnswer; }

    // Logic
    public boolean authenticate(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    public boolean isAdmin() {
        return "Admin".equalsIgnoreCase(role);
    }

    public boolean verifySecurityAnswer(String inputAnswer) {
        return this.securityAnswer.equalsIgnoreCase(inputAnswer);
    }

    // 🔐 Static login method
    public static User login(java.util.List<User> users, String inputUsername, String inputPassword) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(inputUsername) && user.authenticate(inputPassword)) {
                loggedInUser = user;
                return user;
            }
        }
        return null; // Login failed
    }

    // Login UI
    public static void createLoginFrame(java.util.List<User> users) {
        User loginFrame = new User("", "", "", null, "", "");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(800, 500);
        loginFrame.setLocationRelativeTo(null);

        // Set up login action
        loginFrame.jButton1.addActionListener(e -> {
            String username = loginFrame.jTextField1.getText().trim();
            String password = new String(loginFrame.jTextField3.getPassword()).trim();
            loggedInUser = login(users, username, password);
            if (loggedInUser == null) {
                JOptionPane.showMessageDialog(loginFrame, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Login successful! Welcome, " + loggedInUser.getUsername() + "! It is currently " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a 'PST' 'on' EEEE, MMMM dd, yyyy")) + ".", "Success", JOptionPane.INFORMATION_MESSAGE);
                loginFrame.dispose();
                MotorPHApp.createMainFrame();
            }
        });

        // Set up forgot password action
        loginFrame.jButton3.addActionListener(e -> {
            createForgotPasswordFrame(users);
        });

        loginFrame.setVisible(true);
    }

    // Forgot Password UI
    private static void createForgotPasswordFrame(List<User> users) {
        JFrame forgotPasswordFrame = new JFrame("Password Recovery");
        forgotPasswordFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        forgotPasswordFrame.setSize(500, 350);
        forgotPasswordFrame.setLocationRelativeTo(null);
        forgotPasswordFrame.setLayout(new BorderLayout(10, 10));

        JPanel recoveryPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        recoveryPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(15);
        JLabel securityQuestionLabel = new JLabel("Security Question:");
        JTextField securityQuestionField = new JTextField(15);
        securityQuestionField.setEditable(false);
        JLabel answerLabel = new JLabel("Answer:");
        JTextField answerField = new JTextField(15);
        JLabel newPasswordLabel = new JLabel("New Password:");
        JPasswordField newPasswordField = new JPasswordField(15);
        JButton resetButton = new JButton("Reset Password");
        JLabel statusLabel = new JLabel("", SwingConstants.CENTER);

        recoveryPanel.add(usernameLabel);
        recoveryPanel.add(usernameField);
        recoveryPanel.add(securityQuestionLabel);
        recoveryPanel.add(securityQuestionField);
        recoveryPanel.add(answerLabel);
        recoveryPanel.add(answerField);
        recoveryPanel.add(newPasswordLabel);
        recoveryPanel.add(newPasswordField);
        recoveryPanel.add(new JLabel()); // Empty cell
        recoveryPanel.add(resetButton);

        forgotPasswordFrame.add(recoveryPanel, BorderLayout.CENTER);
        forgotPasswordFrame.add(statusLabel, BorderLayout.SOUTH);

        // Username field action to display security question
        usernameField.addActionListener(e -> {
            String username = usernameField.getText().trim();
            User user = users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
            if (user != null) {
                securityQuestionField.setText(user.getSecurityQuestion());
                statusLabel.setText("");
            } else {
                securityQuestionField.setText("");
                statusLabel.setText("User not found.");
                statusLabel.setForeground(Color.RED);
            }
        });

        // Reset password button action
        resetButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String answer = answerField.getText().trim();
            String newPassword = new String(newPasswordField.getPassword()).trim();

            if (username.isEmpty() || answer.isEmpty() || newPassword.isEmpty()) {
                statusLabel.setText("All fields are required.");
                statusLabel.setForeground(Color.RED);
                return;
            }

            User user = users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);

            if (user != null && user.verifySecurityAnswer(answer)) {
                user.setPassword(newPassword);
                statusLabel.setText("Password reset successful! Please login with new password.");
                statusLabel.setForeground(Color.GREEN);
                forgotPasswordFrame.dispose();
            } else {
                statusLabel.setText("Incorrect answer or user not found.");
                statusLabel.setForeground(Color.RED);
            }
        });

        forgotPasswordFrame.setVisible(true);
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JPasswordField(); // Changed to JPasswordField
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(102, 102, 102));
        setPreferredSize(new java.awt.Dimension(800, 500));

        jPanel1.setBackground(new java.awt.Color(102, 102, 102));
        jPanel1.setToolTipText("");
        jPanel1.setName("MotorPH Payroll System - User Login");
        jPanel1.setPreferredSize(new java.awt.Dimension(800, 500));
        jPanel1.setLayout(null);

        jPanel2.setBackground(new java.awt.Color(14, 49, 113));
        jPanel2.setMinimumSize(new java.awt.Dimension(400, 500));

        jLabel4.setForeground(new java.awt.Color(255, 0, 0));
        jLabel4.setIcon(new javax.swing.ImageIcon("C:\\Users\\Carlo\\Documents\\NetBeansProjects\\MotorPHAppGroup11\\src\\main\\java\\com\\group11\\cp2\\Icon\\Motorph (2).png")); // Adjust path as needed
        jLabel4.setText("jLabel4");

        jLabel6.setFont(new java.awt.Font("Verdana Pro Cond Black", 1, 24));
        jLabel6.setForeground(new java.awt.Color(221, 221, 221));
        jLabel6.setText("MOTOR");

        jLabel5.setFont(new java.awt.Font("Verdana Pro Cond Black", 1, 25));
        jLabel5.setForeground(new java.awt.Color(255, 51, 51));
        jLabel5.setText("PH");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(136, 136, 136)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(108, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(153, 153, 153)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addContainerGap(240, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel2);
        jPanel2.setBounds(0, 0, 400, 500);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setMinimumSize(new java.awt.Dimension(400, 579)); // Updated height

        jLabel1.setBackground(new java.awt.Color(14, 49, 113));
        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24));
        jLabel1.setForeground(new java.awt.Color(14, 49, 113));
        jLabel1.setText("MotorPH Payroll System");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14));
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Username");

        jTextField1.setBackground(new java.awt.Color(255, 255, 255));
        jTextField1.setForeground(new java.awt.Color(102, 102, 102));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14));
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Password");

        jTextField3.setBackground(new java.awt.Color(255, 255, 255));
        jTextField3.setForeground(new java.awt.Color(102, 102, 102));

        jButton1.setBackground(new java.awt.Color(14, 49, 113));
        jButton1.setFont(new java.awt.Font("Segoe UI Black", 0, 12));
        jButton1.setForeground(new java.awt.Color(221, 221, 221));
        jButton1.setText("LOGIN");

        jButton3.setBackground(new java.awt.Color(255, 255, 255));
        jButton3.setFont(new java.awt.Font("Segoe UI Black", 0, 10));
        jButton3.setForeground(new java.awt.Color(102, 102, 102));
        jButton3.setText("Forgot Password?");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(32, 32, 32)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton3))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(jLabel1)))
                .addContainerGap(37, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(74, 74, 74)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(175, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel3);
        jPanel3.setBounds(400, 0, 400, 579); // Updated height

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel1.getAccessibleContext().setAccessibleName("MotorPH Payroll System - User Login");
        jPanel1.getAccessibleContext().setAccessibleDescription("MotorPH Payroll System - User Login");

        pack();
    } // </editor-fold>
}
