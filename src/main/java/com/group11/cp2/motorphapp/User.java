package com.group11.cp2.motorphapp;

import javax.swing.*;
import java.util.List;

public class User {
    private String username;
    private String password;
    private String email;
    private Employee employee;
    private String role;
    private String status;
    private static User loggedInUser;

    public User(String username, String password, String email, Employee employee, String role, String status) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.employee = employee;
        this.role = role;
        this.status = status;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public Employee getEmployee() { return employee; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
    public boolean isAdmin() { return "Admin".equalsIgnoreCase(role); }

    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email) { this.email = email; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public void setRole(String role) { this.role = role; }
    public void setStatus(String status) { this.status = status; }

    public static User getLoggedInUser() { return loggedInUser; }
    public static void setLoggedInUser(User user) { loggedInUser = user; }

    public static User login(List<User> users, String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password) && "Active".equals(user.getStatus())) {
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
                    "Login Successful! Welcome, " + loggedInUser.getUsername() + "!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loginFrame.dispose();
                MotorPHApp.createMainFrame();
            }
        });

        loginFrame.setVisible(true);
    }
}