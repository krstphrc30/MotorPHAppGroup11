/**
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group11.cp2.motorphapp;

import java.util.*;

public class User {
    private String username;
    private String password;
    private String role;
    private Employee employee;

    public User(String username, String password, String role, Employee employee) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.employee = employee;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public Employee getEmployee() { return employee; }

    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    // Logic
    public boolean authenticate(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    public boolean isAdmin() {
        return "Admin".equalsIgnoreCase(role);
    }

    // 🔐 Static login method
    public static User login(List<User> users, String inputUsername, String inputPassword) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(inputUsername) && user.authenticate(inputPassword)) {
                return user;
            }
        }
        return null; // Login failed
    }
}
