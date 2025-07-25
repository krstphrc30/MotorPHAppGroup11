/**
 * User class for MotorPH application.
 *
 * @author Kristopher Carlo, Clarinda, Pil, Janice (Group 11)
 */
package com.group11.cp2.motorphapp;

import java.util.List;

public class User {
    private String username;
    private String password;
    private String email;
    private Employee employee;
    private String role;
    private String status;
    private static User loggedInUser;

    /**
     * Creates a User with given details.
     *
     * @param username Username.
     * @param password Password.
     * @param email Email address.
     * @param employee Associated Employee.
     * @param role User role.
     * @param status Account status.
     */
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

    /**
     * Checks if user is Admin.
     *
     * @return true if Admin, false otherwise.
     */
    public boolean isAdmin() { return "Admin".equalsIgnoreCase(role); }

    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email) { this.email = email; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public void setRole(String role) { this.role = role; }
    public void setStatus(String status) { this.status = status; }

    /**
     * Gets logged-in user.
     *
     * @return Current User or null.
     */
    public static User getLoggedInUser() { return loggedInUser; }

    /**
     * Sets logged-in user.
     *
     * @param user User to set.
     */
    public static void setLoggedInUser(User user) { loggedInUser = user; }

    /**
     * Authenticates user.
     *
     * @param users List of users.
     * @param username Username to check.
     * @param password Password to check.
     * @return Authenticated User or null.
     */
    public static User login(List<User> users, String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password) && "Active".equals(user.getStatus())) {
                return user;
            }
        }
        return null;
    }
}