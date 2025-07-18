package com.group11.cp2.motorphapp;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class CSVHandler {

    private static final DateTimeFormatter employeeDateFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    private static final DateTimeFormatter attendanceDateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");

    public static List<Employee> readEmployeesFromCSV(String filePath) {
        List<Employee> employees = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                List<String> values = parseCSVLine(line);

                if (values.size() >= 18) { // Ensure enough columns
                    try {
                        int employeeNumber = Integer.parseInt(values.get(0).trim());
                        String lastName = values.get(1).trim();
                        String firstName = values.get(2).trim();
                        LocalDate birthday = LocalDate.parse(values.get(3).trim(), employeeDateFormatter);
                        String employmentStatus = values.get(10).trim();
                        String position = values.get(11).trim();
                        double basicSalary = parseDouble(values.get(13).trim());
                        double riceSubsidy = parseDouble(values.get(14).trim());
                        double phoneAllowance = parseDouble(values.get(15).trim());
                        double clothingAllowance = parseDouble(values.get(16).trim());
                        double grossSemiMonthlyRate = parseDouble(values.get(17).trim());
                        double hourlyRate = parseDouble(values.get(18).trim());

                        // Parse Government Details
                        String sssNumber = values.get(6).trim();
                        String philHealthNumber = values.get(7).trim();
                        String tinNumber = values.get(8).trim();
                        String pagIbigNumber = values.get(9).trim();

                        GovernmentDetails govDetails = new GovernmentDetails(
                                sssNumber,
                                philHealthNumber,
                                tinNumber,
                                pagIbigNumber
                        );

                        CompensationDetails compensation = new CompensationDetails(
                                basicSalary,
                                riceSubsidy,
                                phoneAllowance,
                                clothingAllowance,
                                grossSemiMonthlyRate,
                                hourlyRate
                        );

                        Employee employee = new Employee(
                                employeeNumber,
                                lastName,
                                firstName,
                                birthday,
                                position,
                                employmentStatus,
                                compensation,
                                govDetails
                        );

                        employees.add(employee);
                    } catch (DateTimeParseException e) {
                        System.err.println("Invalid date format in line: " + line);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid number format in employee line: " + line);
                    } catch (Exception e) {
                        System.err.println("Error parsing employee line: " + line);
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("Invalid employee row (too few columns): " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading employee CSV: " + e.getMessage());
        }

        return employees;
    }

    public static List<AttendanceRecord> readAttendanceCSV(String filePath) {
        List<AttendanceRecord> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                List<String> values = parseCSVLine(line);

                if (values.size() >= 4) { // Expect exactly 4 columns: EmployeeNumber,Date,TimeIn,TimeOut
                    try {
                        int employeeNumber = Integer.parseInt(values.get(0).trim());
                        LocalDate date = LocalDate.parse(values.get(1).trim(), attendanceDateFormatter);

                        // Handle TimeIn and TimeOut, allowing for empty or null values
                        LocalTime timeIn = null;
                        String timeInStr = values.get(2).trim();
                        if (!timeInStr.isEmpty()) {
                            timeIn = LocalTime.parse(timeInStr, timeFormatter);
                        }

                        LocalTime timeOut = null;
                        String timeOutStr = values.get(3).trim();
                        if (!timeOutStr.isEmpty()) {
                            timeOut = LocalTime.parse(timeOutStr, timeFormatter);
                        }

                        AttendanceRecord record = new AttendanceRecord(employeeNumber, date, timeIn, timeOut);
                        records.add(record);
                    } catch (DateTimeParseException e) {
                        System.err.println("Invalid date or time format in attendance line: " + line + ", Error: " + e.getMessage());
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid employee number format in attendance line: " + line + ", Error: " + e.getMessage());
                    } catch (Exception e) {
                        System.err.println("Error parsing attendance line: " + line + ", Error: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("Invalid attendance row (expected 4 columns, found " + values.size() + "): " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading attendance CSV: " + e.getMessage());
        }

        return records;
    }

    public static void writeAttendanceToCSV(List<AttendanceRecord> records, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("EmployeeNumber,Date,TimeIn,TimeOut");
            writer.newLine();
            for (AttendanceRecord record : records) {
                writer.write(String.format("%d,%s,%s,%s",
                        record.getEmployeeNumber(),
                        record.getDate().format(attendanceDateFormatter),
                        record.getTimeIn() != null ? record.getTimeIn().format(timeFormatter) : "",
                        record.getTimeOut() != null ? record.getTimeOut().format(timeFormatter) : ""));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing attendance CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void writeEmployeesToCSV(List<Employee> employees, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Employee Number,Last Name,First Name,Birthday,Address,Phone Number,SSS Number,Philhealth Number,TIN Number,Pag-ibig Number,Status,Position,Immediate Supervisor,Basic Salary,Rice Subsidy,Phone Allowance,Clothing Allowance,Semi-monthly Rate,Hourly Rate");
            writer.newLine();

            for (Employee emp : employees) {
                CompensationDetails comp = emp.getCompensationDetails();
                GovernmentDetails gov = emp.getGovernmentDetails();
                writer.write(String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f",
                        emp.getEmployeeNumber(),
                        emp.getLastName(),
                        emp.getFirstName(),
                        emp.getBirthday().format(employeeDateFormatter),
                        "", // Address
                        "", // Phone Number
                        gov.getSssNumber(),
                        gov.getPhilHealthNumber(),
                        gov.getTinNumber(),
                        gov.getPagIbigNumber(),
                        emp.getStatus(),
                        emp.getPosition(),
                        "", // Immediate Supervisor
                        comp.getBasicSalary(),
                        comp.getRiceSubsidy(),
                        comp.getPhoneAllowance(),
                        comp.getClothingAllowance(),
                        comp.getGrossSemiMonthlyRate(),
                        comp.getHourlyRate()));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing employee CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static List<String> parseCSVLine(String line) {
        List<String> tokens = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString());
        return tokens;
    }

    private static double parseDouble(String input) throws NumberFormatException {
        return Double.parseDouble(input.replace(",", ""));
    }
}