/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group11.cp2.motorphapp;

/**
 *
 * @author Pil,Kristopher,Janice,Clarinda
 */
public class User {
    private String username;
    private String password;
    private String role;
    private Employee employee; // you must also create this class

    public User(String username, String password, String role, Employee employee) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.employee = employee;
    }

    public boolean login(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    public String getRole() {
        return role;
    }

    public boolean changePassword(String oldPass, String newPass) {
        if (this.password.equals(oldPass)) {
            this.password = newPass;
            return true;
        }
        return false;
    }

    public void forgotPassword(String email) {
        // Placeholder: In a real system, you'd send a reset link to the email
        System.out.println("Password reset link sent to: " + email);
    }

    // Getters and setters for username, password, role if needed
}
