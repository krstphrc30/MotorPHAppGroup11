package com.group11.cp2.motorphapp;

import java.time.*;
import java.time.temporal.WeekFields;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AttendanceRecord {
    private int employeeNumber;
    private LocalDate date;
    private LocalTime timeIn;
    private LocalTime timeOut;
    private Duration regularDuration;
    private Duration overtimeDuration;

    private static final LocalTime WORK_START = LocalTime.of(8, 0);
    private static final LocalTime GRACE_END = WORK_START.plusMinutes(10);
    private static final LocalTime WORK_END = LocalTime.of(17, 0);
    private static final Duration BREAK_TIME = Duration.ofHours(1);

    // GUI Components (static to share across AttendanceRecord instances)
    private static JTextField employeeNumberField, dateField, timeInField, timeOutField;
    private static JTable attendanceTable;
    private static DefaultTableModel tableModel;
    private static List<AttendanceRecord> attendanceRecords;
    private static List<Employee> employees;

    public AttendanceRecord(int employeeNumber, LocalDate date, LocalTime timeIn, LocalTime timeOut) {
        this.employeeNumber = employeeNumber;
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        computeWorkDurations();
    }

    private void computeWorkDurations() {
        if (timeIn == null || timeOut == null || timeOut.isBefore(timeIn)) {
            regularDuration = Duration.ZERO;
            overtimeDuration = Duration.ZERO;
            return;
        }

        boolean isLate = timeIn.isAfter(GRACE_END);
        LocalTime adjustedTimeIn = timeIn.isAfter(WORK_START) && timeIn.isBefore(GRACE_END) ? WORK_START : timeIn;

        LocalTime adjustedLogOut = timeOut.isAfter(WORK_END) ? WORK_END : timeOut;
        Duration workDuration = Duration.between(adjustedTimeIn, adjustedLogOut).minus(BREAK_TIME);
        workDuration = workDuration.isNegative() ? Duration.ZERO : workDuration;

        Duration otDuration = (!isLate && timeOut.isAfter(WORK_END)) ? Duration.between(WORK_END, timeOut) : Duration.ZERO;

        this.regularDuration = workDuration;
        this.overtimeDuration = otDuration;
    }

    public int getEmployeeNumber() {
        return employeeNumber;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTimeIn() {
        return timeIn;
    }

    public LocalTime getTimeOut() {
        return timeOut;
    }

    public Duration getRegularDuration() {
        return regularDuration;
    }

    public Duration getOvertimeDuration() {
        return overtimeDuration;
    }

    public Duration getTotalWorkedDuration() {
        return regularDuration.plus(overtimeDuration);
    }

    private static String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.minusHours(hours).toMinutes();
        return String.format("%02dh %02dm", hours, minutes);
    }

    private static int getWeekOfYear(LocalDate date) {
        return date.get(WeekFields.ISO.weekOfWeekBasedYear());
    }

    public static void printWeeklySummary(List<AttendanceRecord> records) {
        System.out.println(getWeeklySummary(records));
    }

    public static String getWeeklySummary(List<AttendanceRecord> records) {
        StringBuilder sb = new StringBuilder();
        Map<Integer, List<AttendanceRecord>> recordsByEmployee = records.stream()
                .collect(Collectors.groupingBy(AttendanceRecord::getEmployeeNumber));

        for (Map.Entry<Integer, List<AttendanceRecord>> empEntry : recordsByEmployee.entrySet()) {
            int empId = empEntry.getKey();
            List<AttendanceRecord> empRecords = empEntry.getValue();

            Map<Integer, List<AttendanceRecord>> recordsByWeek = empRecords.stream()
                    .collect(Collectors.groupingBy(r -> getWeekOfYear(r.getDate())));

            sb.append("\nEmployee #").append(empId).append(" Weekly Summary:\n");

            for (Map.Entry<Integer, List<AttendanceRecord>> weekEntry : recordsByWeek.entrySet()) {
                int weekNum = weekEntry.getKey();
                List<AttendanceRecord> weekRecords = weekEntry.getValue();

                Duration totalRegular = weekRecords.stream()
                        .map(AttendanceRecord::getRegularDuration)
                        .reduce(Duration.ZERO, Duration::plus);

                Duration totalOT = weekRecords.stream()
                        .map(AttendanceRecord::getOvertimeDuration)
                        .reduce(Duration.ZERO, Duration::plus);

                Duration totalWorked = totalRegular.plus(totalOT);

                sb.append(String.format("Week %d - Regular: %s | OT: %s | Total: %s\n",
                        weekNum,
                        formatDuration(totalRegular),
                        formatDuration(totalOT),
                        formatDuration(totalWorked)));
            }
        }

        return sb.toString();
    }

    public static String generateWeeklySalarySummary(List<AttendanceRecord> records, List<Employee> employees) {
        Map<Integer, Employee> employeeMap = employees.stream()
                .collect(Collectors.toMap(Employee::getEmployeeNumber, e -> e));

        Map<Integer, List<AttendanceRecord>> recordsByEmployee = records.stream()
                .collect(Collectors.groupingBy(AttendanceRecord::getEmployeeNumber));

        StringBuilder sb = new StringBuilder();
        sb.append("=== WEEKLY SALARY SUMMARY ===\n");

        for (Map.Entry<Integer, List<AttendanceRecord>> entry : recordsByEmployee.entrySet()) {
            int empId = entry.getKey();
            Employee employee = employeeMap.get(empId);
            if (employee == null) continue;

            String fullName = employee.getLastName() + ", " + employee.getFirstName();
            List<AttendanceRecord> empRecords = entry.getValue();

            Map<Integer, List<AttendanceRecord>> recordsByWeek = empRecords.stream()
                    .collect(Collectors.groupingBy(r -> getWeekOfYear(r.getDate())));

            List<Map.Entry<Integer, List<AttendanceRecord>>> sortedWeeks = new ArrayList<>(recordsByWeek.entrySet());
            sortedWeeks.sort(Comparator.comparing(e -> e.getValue().get(0).getDate()));

            for (Map.Entry<Integer, List<AttendanceRecord>> weekEntry : sortedWeeks) {
                List<AttendanceRecord> weekRecords = weekEntry.getValue();
                LocalDate anyDate = weekRecords.get(0).getDate();
                LocalDate weekStart = anyDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                LocalDate weekEnd = anyDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));

                Duration totalRegular = weekRecords.stream()
                        .map(AttendanceRecord::getRegularDuration)
                        .reduce(Duration.ZERO, Duration::plus);

                Duration totalOT = weekRecords.stream()
                        .map(AttendanceRecord::getOvertimeDuration)
                        .reduce(Duration.ZERO, Duration::plus);

                sb.append("Weekly Salary Summary for ").append(fullName).append(":\n")
                        .append("-------------------------------------------------\n")
                        .append("Week Period             : ").append(weekStart).append(" - ").append(weekEnd).append("\n")
                        .append("Total Hours Worked      : ").append(formatDuration(totalRegular)).append("\n")
                        .append("Total Overtime          : ").append(formatDuration(totalOT)).append("\n\n");
            }
        }

        return sb.toString();
    }

    public static JPanel createAttendancePanel(List<Employee> employees, User loggedInUser) {
        AttendanceRecord.employees = employees;
        attendanceRecords = MotorPHApp.getAttendance();
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Attendance Record"));

        // Attendance Fields
        formPanel.add(new JLabel("Employee Number:"));
        employeeNumberField = new JTextField();
        formPanel.add(employeeNumberField);

        formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField();
        formPanel.add(dateField);

        formPanel.add(new JLabel("Time In (HH:MM):"));
        timeInField = new JTextField();
        formPanel.add(timeInField);

        formPanel.add(new JLabel("Time Out (HH:MM):"));
        timeOutField = new JTextField();
        formPanel.add(timeOutField);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Attendance");
        JButton clearButton = new JButton("Clear Form");
        buttonPanel.add(addButton);
        buttonPanel.add(clearButton);

        // Table Panel
        String[] columnNames = {"Emp No", "Date", "Time In", "Time Out", "Regular", "Overtime", "Total"};
        tableModel = new DefaultTableModel(columnNames, 0);
        attendanceTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(attendanceTable);

        // Populate Table
        populateTable(loggedInUser);

        // Add components to main panel
        panel.add(formPanel, BorderLayout.WEST);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Button Actions
        addButton.addActionListener(e -> addAttendance(loggedInUser));
        clearButton.addActionListener(e -> clearForm());

        // Table Selection Listener
        attendanceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = attendanceTable.getSelectedRow();
                if (selectedRow >= 0) {
                    displayAttendance(selectedRow);
                }
            }
        });

        return panel;
    }

    private static void populateTable(User loggedInUser) {
        tableModel.setRowCount(0); // Clear existing rows
        for (AttendanceRecord record : attendanceRecords) {
            if (!loggedInUser.isAdmin() && record.getEmployeeNumber() != loggedInUser.getEmployee().getEmployeeNumber()) {
                continue;
            }
            tableModel.addRow(new Object[]{
                    record.getEmployeeNumber(),
                    record.getDate(),
                    record.getTimeIn() != null ? record.getTimeIn() : "N/A",
                    record.getTimeOut() != null ? record.getTimeOut() : "N/A",
                    formatDuration(record.getRegularDuration()),
                    formatDuration(record.getOvertimeDuration()),
                    formatDuration(record.getTotalWorkedDuration())
            });
        }
    }

    private static void addAttendance(User loggedInUser) {
        try {
            // Validate and parse inputs
            int employeeNumber = Integer.parseInt(employeeNumberField.getText().trim());
            LocalDate date = LocalDate.parse(dateField.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            LocalTime timeIn = LocalTime.parse(timeInField.getText().trim(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime timeOut = LocalTime.parse(timeOutField.getText().trim(), DateTimeFormatter.ofPattern("HH:mm"));

            // Validate employee number
            boolean employeeExists = employees.stream()
                    .anyMatch(emp -> emp.getEmployeeNumber() == employeeNumber);
            if (!employeeExists) {
                JOptionPane.showMessageDialog(null, "Employee number does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Restrict non-admin users to their own records
            if (!loggedInUser.isAdmin() && employeeNumber != loggedInUser.getEmployee().getEmployeeNumber()) {
                JOptionPane.showMessageDialog(null, "You can only add attendance for yourself.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check for duplicate attendance record (same employee and date)
            if (attendanceRecords.stream()
                    .anyMatch(record -> record.getEmployeeNumber() == employeeNumber && record.getDate().equals(date))) {
                JOptionPane.showMessageDialog(null, "Attendance record for this employee and date already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create new attendance record
            AttendanceRecord newRecord = new AttendanceRecord(employeeNumber, date, timeIn, timeOut);

            // Add to list and update table
            attendanceRecords.add(newRecord);
            if (loggedInUser.isAdmin() || employeeNumber == loggedInUser.getEmployee().getEmployeeNumber()) {
                tableModel.addRow(new Object[]{
                        newRecord.getEmployeeNumber(),
                        newRecord.getDate(),
                        newRecord.getTimeIn() != null ? newRecord.getTimeIn() : "N/A",
                        newRecord.getTimeOut() != null ? newRecord.getTimeOut() : "N/A",
                        formatDuration(newRecord.getRegularDuration()),
                        formatDuration(newRecord.getOvertimeDuration()),
                        formatDuration(newRecord.getTotalWorkedDuration())
                });
            }

            JOptionPane.showMessageDialog(null, "Attendance record added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter a valid number for Employee Number.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(null, "Please enter valid formats: Date (YYYY-MM-DD), Time (HH:MM).", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void clearForm() {
        employeeNumberField.setText("");
        dateField.setText("");
        timeInField.setText("");
        timeOutField.setText("");
    }

    private static void displayAttendance(int rowIndex) {
        // Find the corresponding record in attendanceRecords
        int tableEmpNo = (Integer) tableModel.getValueAt(rowIndex, 0);
        LocalDate tableDate = LocalDate.parse(tableModel.getValueAt(rowIndex, 1).toString(), DateTimeFormatter.ISO_LOCAL_DATE);
        AttendanceRecord record = attendanceRecords.stream()
                .filter(r -> r.getEmployeeNumber() == tableEmpNo && r.getDate().equals(tableDate))
                .findFirst()
                .orElse(null);

        if (record != null) {
            employeeNumberField.setText(String.valueOf(record.getEmployeeNumber()));
            dateField.setText(record.getDate().toString());
            timeInField.setText(record.getTimeIn() != null ? record.getTimeIn().format(DateTimeFormatter.ofPattern("HH:mm")) : "");
            timeOutField.setText(record.getTimeOut() != null ? record.getTimeOut().format(DateTimeFormatter.ofPattern("HH:mm")) : "");
        }
    }

    @Override
    public String toString() {
        return "Employee #" + employeeNumber +
                " | Date: " + date +
                " | Time In: " + timeIn +
                " | Time Out: " + timeOut +
                " | Regular: " + formatDuration(regularDuration) +
                " | Overtime: " + formatDuration(overtimeDuration) +
                " | Total: " + formatDuration(getTotalWorkedDuration());
    }
}