package com.group11.cp2.motorphapp;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class User {
    private String username;
    private String password;
    private String role;
    private Employee employee;
    private String securityQuestion;
    private String securityAnswer;
    private static JFrame loginFrame;
    private static User loggedInUser;

    public User(String username, String password, String role, Employee employee, String securityQuestion, String securityAnswer) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.employee = employee;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
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
        loginFrame = new JFrame("MotorPH Payroll System - User Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(500, 300);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setLayout(new BorderLayout(10, 10));

        // Login panel
        JPanel loginPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(15);
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(15);
        JButton loginButton = new JButton("Login");
        JButton forgotPasswordButton = new JButton("Forgot Password?");
        JLabel statusLabel = new JLabel("", SwingConstants.CENTER);

        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(new JLabel()); // Empty cell
        loginPanel.add(loginButton);
        loginPanel.add(new JLabel()); // Empty cell
        loginPanel.add(forgotPasswordButton);

        loginFrame.add(loginPanel, BorderLayout.CENTER);
        loginFrame.add(statusLabel, BorderLayout.SOUTH);

        // Login button action
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            loggedInUser = login(users, username, password);
            if (loggedInUser == null) {
                statusLabel.setText("Invalid username or password.");
                statusLabel.setForeground(Color.RED);
            } else {
                statusLabel.setText("Login successful! Welcome, " + loggedInUser.getUsername());
                statusLabel.setForeground(Color.GREEN);
                loginFrame.dispose();
                MotorPHApp.createMainFrame();
            }
        });

        // Forgot password button action
        forgotPasswordButton.addActionListener(e -> {
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
}