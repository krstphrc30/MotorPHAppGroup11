package com.group11.cp2.motorphapp;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

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
    private static JTextArea summaryArea;

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

    public int getEmployeeNumber() { return employeeNumber; }
    public LocalDate getDate() { return date; }
    public LocalTime getTimeIn() { return timeIn; }
    public LocalTime getTimeOut() { return timeOut; }
    public Duration getRegularDuration() { return regularDuration; }
    public Duration getOvertimeDuration() { return overtimeDuration; }
    public Duration getTotalWorkedDuration() { return regularDuration.plus(overtimeDuration); }

    private static String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.minusHours(hours).toMinutes();
        return String.format("%02dh %02dm", hours, minutes);
    }

    private static YearMonth getYearMonth(LocalDate date) { return YearMonth.from(date); }

    public static void printMonthlySummary(List<AttendanceRecord> records) {
        System.out.println(getMonthlySummary(records));
    }

    public static String getMonthlySummary(List<AttendanceRecord> records) {
        StringBuilder sb = new StringBuilder();
        Map<Integer, List<AttendanceRecord>> recordsByEmployee = records.stream()
                .collect(Collectors.groupingBy(AttendanceRecord::getEmployeeNumber));
        for (Map.Entry<Integer, List<AttendanceRecord>> empEntry : recordsByEmployee.entrySet()) {
            int empId = empEntry.getKey();
            List<AttendanceRecord> empRecords = empEntry.getValue();
            Map<YearMonth, List<AttendanceRecord>> recordsByMonth = empRecords.stream()
                    .collect(Collectors.groupingBy(r -> YearMonth.from(r.getDate())));
            sb.append("\nEmployee #").append(empId).append(" Monthly Summary:\n");
            for (Map.Entry<YearMonth, List<AttendanceRecord>> monthEntry : recordsByMonth.entrySet()) {
                YearMonth yearMonth = monthEntry.getKey();
                List<AttendanceRecord> monthRecords = monthEntry.getValue();
                Duration totalRegular = monthRecords.stream().map(AttendanceRecord::getRegularDuration).reduce(Duration.ZERO, Duration::plus);
                Duration totalOT = monthRecords.stream().map(AttendanceRecord::getOvertimeDuration).reduce(Duration.ZERO, Duration::plus);
                Duration totalWorked = totalRegular.plus(totalOT);
                sb.append(String.format("Month %s - Regular Hours: %s | OT: %s | Total: %s\n",
                        yearMonth, formatDuration(totalRegular), formatDuration(totalOT), formatDuration(totalWorked)));
            }
        }
        return sb.toString();
    }

    public static String generateMonthlySalarySummary(List<AttendanceRecord> records, List<Employee> employees) {
        Map<Integer, Employee> employeeMap = employees.stream()
                .collect(Collectors.toMap(Employee::getEmployeeNumber, e -> e));
        Map<Integer, List<AttendanceRecord>> recordsByEmployee = records.stream()
                .collect(Collectors.groupingBy(AttendanceRecord::getEmployeeNumber));
        StringBuilder sb = new StringBuilder();
        sb.append("=== MONTHLY HOURS SUMMARY ===\n");
        for (Map.Entry<Integer, List<AttendanceRecord>> empEntry : recordsByEmployee.entrySet()) {
            int empId = empEntry.getKey();
            Employee employee = employeeMap.get(empId);
            if (employee == null) continue;
            String fullName = employee.getLastName() + ", " + employee.getFirstName();
            List<AttendanceRecord> empRecords = empEntry.getValue();
            Map<YearMonth, List<AttendanceRecord>> recordsByMonth = empRecords.stream()
                    .collect(Collectors.groupingBy(r -> YearMonth.from(r.getDate())));
            List<Map.Entry<YearMonth, List<AttendanceRecord>>> sortedMonths = new ArrayList<>(recordsByMonth.entrySet());
            sortedMonths.sort(Comparator.comparing(Map.Entry::getKey));
            for (Map.Entry<YearMonth, List<AttendanceRecord>> monthEntry : sortedMonths) {
                YearMonth yearMonth = monthEntry.getKey();
                List<AttendanceRecord> monthRecords = monthEntry.getValue();
                Duration totalRegular = monthRecords.stream().map(AttendanceRecord::getRegularDuration).reduce(Duration.ZERO, Duration::plus);
                Duration totalOT = monthRecords.stream().map(AttendanceRecord::getOvertimeDuration).reduce(Duration.ZERO, Duration::plus);
                sb.append("Monthly Hours Summary for ").append(fullName).append(":\n")
                        .append("-------------------------------------------------\n")
                        .append("Month                   : ").append(yearMonth).append("\n")
                        .append("Total Regular Hours     : ").append(formatDuration(totalRegular)).append("\n")
                        .append("Total Overtime Hours    : ").append(formatDuration(totalOT)).append("\n\n");
            }
        }
        return sb.toString();
    }

    public static JPanel createAttendancePanel(List<Employee> employees, User loggedInUser) {
        AttendanceRecord.employees = employees;
        // Fix: Explicit cast to resolve type mismatch between MotorPHApp.AttendanceRecord and this AttendanceRecord
        attendanceRecords = new ArrayList<>();
        for (Object record : MotorPHApp.getAttendance()) {
            if (record instanceof AttendanceRecord) {
                attendanceRecords.add((AttendanceRecord) record);
            } else {
                System.err.println("Skipping incompatible record: " + record);
            }
        }
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Attendance Record"));

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

        // Summary and Pay Panel
        JPanel summaryPayPanel = new JPanel(new BorderLayout(5, 5));
        summaryPayPanel.setBorder(BorderFactory.createTitledBorder("Summary & Pay"));

        JPanel selectorPanel = new JPanel(new FlowLayout());
        String[] months = {"January", "February", "March", "April", "May", "June",
                          "July", "August", "September", "October", "November", "December"};
        JComboBox<String> monthCombo = new JComboBox<>(months);
        monthCombo.setSelectedIndex(YearMonth.now().getMonthValue() - 1);
        JTextField yearField = new JTextField(String.valueOf(YearMonth.now().getYear()), 4);
        JButton generateSummaryButton = new JButton("Generate Hours Summary");
        JButton computePayButton = new JButton("Compute Pay");
        selectorPanel.add(new JLabel("Month:"));
        selectorPanel.add(monthCombo);
        selectorPanel.add(new JLabel("Year:"));
        selectorPanel.add(yearField);
        selectorPanel.add(generateSummaryButton);
        selectorPanel.add(computePayButton);

        summaryArea = new JTextArea();
        summaryArea.setEditable(false);
        summaryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane summaryScrollPane = new JScrollPane(summaryArea);
        summaryPayPanel.add(selectorPanel, BorderLayout.NORTH);
        summaryPayPanel.add(summaryScrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Attendance");
        JButton clearButton = new JButton("Clear Form");
        JButton viewDetailsButton = new JButton("View Details");
        buttonPanel.add(addButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(viewDetailsButton);

        // Table Panel
        String[] columnNames = {"Emp No", "Date", "Time In", "Time Out", "Regular", "Overtime", "Total"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        attendanceTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(attendanceTable);

        // Populate Table
        populateTable(loggedInUser, null);

        // Main Panel Layout
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        centerPanel.add(tableScrollPane);
        centerPanel.add(summaryPayPanel);

        panel.add(formPanel, BorderLayout.WEST);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Button Actions
        addButton.addActionListener(e -> addAttendance(loggedInUser));
        clearButton.addActionListener(e -> clearForm());
        viewDetailsButton.addActionListener(e -> viewAttendanceDetails());

        generateSummaryButton.addActionListener(e -> {
            try {
                int year = Integer.parseInt(yearField.getText().trim());
                int month = monthCombo.getSelectedIndex() + 1;
                YearMonth yearMonth = YearMonth.of(year, month);
                List<AttendanceRecord> filteredRecords = attendanceRecords.stream()
                        .filter(r -> getYearMonth(r.getDate()).equals(yearMonth))
                        .filter(r -> loggedInUser.isAdmin() || r.getEmployeeNumber() == loggedInUser.getEmployee().getEmployeeNumber())
                        .collect(Collectors.toList());
                summaryArea.setText(generateMonthlySalarySummary(filteredRecords, employees));
                populateTable(loggedInUser, yearMonth);
            } catch (NumberFormatException ex) {
                summaryArea.setText("Error: Please enter a valid year.");
            } catch (Exception ex) {
                summaryArea.setText("Error: " + ex.getMessage());
            }
        });

        computePayButton.addActionListener(e -> {
            try {
                int year = Integer.parseInt(yearField.getText().trim());
                int month = monthCombo.getSelectedIndex() + 1;
                YearMonth yearMonth = YearMonth.of(year, month);
                List<AttendanceRecord> filteredRecords = attendanceRecords.stream()
                        .filter(r -> getYearMonth(r.getDate()).equals(yearMonth))
                        .filter(r -> loggedInUser.isAdmin() || r.getEmployeeNumber() == loggedInUser.getEmployee().getEmployeeNumber())
                        .collect(Collectors.toList());
                String paySummary = generatePaySummary(filteredRecords, employees);
                summaryArea.setText(paySummary);
                populateTable(loggedInUser, yearMonth);
            } catch (NumberFormatException ex) {
                summaryArea.setText("Error: Please enter a valid year.");
            } catch (Exception ex) {
                summaryArea.setText("Error: " + ex.getMessage());
            }
        });

        attendanceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = attendanceTable.getSelectedRow();
                if (selectedRow >= 0) displayAttendance(selectedRow);
            }
        });

        generateSummaryButton.doClick();

        return panel;
    }

    private static void populateTable(User loggedInUser, YearMonth yearMonth) {
        tableModel.setRowCount(0);
        for (AttendanceRecord record : attendanceRecords) {
            if (!loggedInUser.isAdmin() && record.getEmployeeNumber() != loggedInUser.getEmployee().getEmployeeNumber()) continue;
            if (yearMonth != null && !getYearMonth(record.getDate()).equals(yearMonth)) continue;
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
            int employeeNumber = Integer.parseInt(employeeNumberField.getText().trim());
            LocalDate date = LocalDate.parse(dateField.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            LocalTime timeIn = LocalTime.parse(timeInField.getText().trim(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime timeOut = LocalTime.parse(timeOutField.getText().trim(), DateTimeFormatter.ofPattern("HH:mm"));

            boolean employeeExists = employees.stream().anyMatch(emp -> emp.getEmployeeNumber() == employeeNumber);
            if (!employeeExists) {
                JOptionPane.showMessageDialog(null, "Employee number does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!loggedInUser.isAdmin() && employeeNumber != loggedInUser.getEmployee().getEmployeeNumber()) {
                JOptionPane.showMessageDialog(null, "You can only add attendance for yourself.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (attendanceRecords.stream().anyMatch(record -> record.getEmployeeNumber() == employeeNumber && record.getDate().equals(date))) {
                JOptionPane.showMessageDialog(null, "Attendance record for this employee and date already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            AttendanceRecord newRecord = new AttendanceRecord(employeeNumber, date, timeIn, timeOut);
            attendanceRecords.add(newRecord);
            CSVHandler.writeAttendanceToCSV(attendanceRecords, "src/main/resources/attendancerecord.csv");

            YearMonth currentFilter = null;
            if (summaryArea.getText().contains("Month")) {
                String[] parts = summaryArea.getText().split("Month ");
                if (parts.length > 1) {
                    String monthStr = parts[1].split(" -")[0].trim();
                    try { currentFilter = YearMonth.parse(monthStr); } catch (DateTimeParseException e) {}
                }
            }
            if (currentFilter == null || getYearMonth(newRecord.getDate()).equals(currentFilter)) {
                if (loggedInUser.isAdmin() || employeeNumber == loggedInUser.getEmployee().getEmployeeNumber()) {
                    tableModel.addRow(new Object[]{
                            newRecord.getEmployeeNumber(),
                            newRecord.getDate(),
                            newRecord.getTimeIn(),
                            newRecord.getTimeOut(),
                            formatDuration(newRecord.getRegularDuration()),
                            formatDuration(newRecord.getOvertimeDuration()),
                            formatDuration(newRecord.getTotalWorkedDuration())
                    });
                }
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
        int tableEmpNo = (Integer) tableModel.getValueAt(rowIndex, 0);
        LocalDate tableDate = LocalDate.parse(tableModel.getValueAt(rowIndex, 1).toString(), DateTimeFormatter.ISO_LOCAL_DATE);
        AttendanceRecord record = attendanceRecords.stream()
                .filter(r -> r.getEmployeeNumber() == tableEmpNo && r.getDate().equals(tableDate))
                .findFirst().orElse(null);
        if (record != null) {
            employeeNumberField.setText(String.valueOf(record.getEmployeeNumber()));
            dateField.setText(record.getDate().toString());
            timeInField.setText(record.getTimeIn().format(DateTimeFormatter.ofPattern("HH:mm")));
            timeOutField.setText(record.getTimeOut().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
    }

    private static void viewAttendanceDetails() {
        int selectedRow = attendanceTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "Please select an attendance record.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int empNo = (Integer) tableModel.getValueAt(selectedRow, 0);
        LocalDate date = LocalDate.parse(tableModel.getValueAt(selectedRow, 1).toString(), DateTimeFormatter.ISO_LOCAL_DATE);
        AttendanceRecord record = attendanceRecords.stream()
                .filter(r -> r.getEmployeeNumber() == empNo && r.getDate().equals(date))
                .findFirst().orElse(null);
        if (record != null) {
            JFrame detailsFrame = new JFrame("Attendance Details - Emp #" + empNo + ", " + date);
            detailsFrame.setSize(400, 300);
            detailsFrame.setLocationRelativeTo(null);
            detailsFrame.setLayout(new GridLayout(7, 2, 10, 5));

            detailsFrame.add(new JLabel("Employee Number:"));
            detailsFrame.add(new JLabel(String.valueOf(record.getEmployeeNumber())));
            detailsFrame.add(new JLabel("Date:"));
            detailsFrame.add(new JLabel(record.getDate().toString()));
            detailsFrame.add(new JLabel("Time In:"));
            detailsFrame.add(new JLabel(record.getTimeIn() != null ? record.getTimeIn().toString() : "N/A"));
            detailsFrame.add(new JLabel("Time Out:"));
            detailsFrame.add(new JLabel(record.getTimeOut() != null ? record.getTimeOut().toString() : "N/A"));
            detailsFrame.add(new JLabel("Regular Hours:"));
            detailsFrame.add(new JLabel(formatDuration(record.getRegularDuration())));
            detailsFrame.add(new JLabel("Overtime Hours:"));
            detailsFrame.add(new JLabel(formatDuration(record.getOvertimeDuration())));
            detailsFrame.add(new JLabel("Total Hours:"));
            detailsFrame.add(new JLabel(formatDuration(record.getTotalWorkedDuration())));

            detailsFrame.setVisible(true);
        }
    }

    private static String generatePaySummary(List<AttendanceRecord> records, List<Employee> employees) {
        Map<Integer, Employee> employeeMap = employees.stream().collect(Collectors.toMap(Employee::getEmployeeNumber, e -> e));
        Map<Integer, List<AttendanceRecord>> recordsByEmployee = records.stream().collect(Collectors.groupingBy(AttendanceRecord::getEmployeeNumber));
        StringBuilder sb = new StringBuilder();
        sb.append("=== MONTHLY PAY SUMMARY ===\n");
        for (Map.Entry<Integer, List<AttendanceRecord>> empEntry : recordsByEmployee.entrySet()) {
            int empId = empEntry.getKey();
            Employee employee = employeeMap.get(empId);
            if (employee == null) continue;
            String fullName = employee.getLastName() + ", " + employee.getFirstName();
            List<AttendanceRecord> empRecords = empEntry.getValue();
            Map<YearMonth, List<AttendanceRecord>> recordsByMonth = empRecords.stream().collect(Collectors.groupingBy(r -> YearMonth.from(r.getDate())));
            List<Map.Entry<YearMonth, List<AttendanceRecord>>> sortedMonths = new ArrayList<>(recordsByMonth.entrySet());
            sortedMonths.sort(Comparator.comparing(Map.Entry::getKey));
            for (Map.Entry<YearMonth, List<AttendanceRecord>> monthEntry : sortedMonths) {
                YearMonth yearMonth = monthEntry.getKey();
                List<AttendanceRecord> monthRecords = monthEntry.getValue();
                Duration totalRegular = monthRecords.stream().map(AttendanceRecord::getRegularDuration).reduce(Duration.ZERO, Duration::plus);
                Duration totalOT = monthRecords.stream().map(AttendanceRecord::getOvertimeDuration).reduce(Duration.ZERO, Duration::plus);
                CompensationDetails comp = employee.getCompensationDetails();
                double regularPay = totalRegular.toHours() * comp.getHourlyRate();
                double otPay = totalOT.toHours() * (comp.getHourlyRate() * 1.5); // 1.5x OT rate (example)
                double grossPay = regularPay + otPay + comp.getRiceSubsidy() + comp.getPhoneAllowance() + comp.getClothingAllowance();
                sb.append("Monthly Pay Summary for ").append(fullName).append(":\n")
                        .append("-------------------------------------------------\n")
                        .append("Month                   : ").append(yearMonth).append("\n")
                        .append("Total Regular Hours     : ").append(formatDuration(totalRegular)).append("\n")
                        .append("Total Overtime Hours    : ").append(formatDuration(totalOT)).append("\n")
                        .append("Regular Pay             : ").append(String.format("%.2f", regularPay)).append("\n")
                        .append("Overtime Pay            : ").append(String.format("%.2f", otPay)).append("\n")
                        .append("Allowances              : ").append(String.format("%.2f", comp.getRiceSubsidy() + comp.getPhoneAllowance() + comp.getClothingAllowance())).append("\n")
                        .append("Gross Pay               : ").append(String.format("%.2f", grossPay)).append("\n\n");
            }
        }
        return sb.toString();
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
