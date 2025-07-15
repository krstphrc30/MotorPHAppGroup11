package com.group11.cp2.motorphapp;

import javax.swing.*;
import java.util.List;

public class User {
    private String username;
    private String password;
    private String role;
    private Employee employee;
    private String securityQuestion;
    private String securityAnswer;
    private static User loggedInUser;

    public User(String username, String password, String role, Employee employee, String securityQuestion, String securityAnswer) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.employee = employee;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
    }

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

    public boolean authenticate(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    public boolean isAdmin() {
        return "Admin".equalsIgnoreCase(role);
    }

    public static User login(List<User> users, String inputUsername, String inputPassword) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(inputUsername) && user.authenticate(inputPassword)) {
                loggedInUser = user;
                return user;
            }
        }
        return null;
    }

    public static void createLoginFrame(List<User> users) {
        UserLogin loginFrame = new UserLogin();
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(800, 500);
        loginFrame.setLocationRelativeTo(null);

        loginFrame.jButton1.addActionListener(e -> {
            String username = loginFrame.jTextField1.getText().trim();
            String password = new String(loginFrame.jPasswordField1.getPassword()).trim();
            loggedInUser = login(users, username, password);
            if (loggedInUser == null) {
                JOptionPane.showMessageDialog(loginFrame, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(loginFrame, 
                    "Login successful! Welcome, " + loggedInUser.getUsername() + "! It is currently " + 
                    java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a 'PST' 'on' EEEE, MMMM dd, yyyy")) + ".", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loginFrame.dispose();
                MotorPHApp.createMainFrame();
            }
        });

        loginFrame.setVisible(true);
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static void setLoggedInUser(User user) {
        loggedInUser = user;
    }
}