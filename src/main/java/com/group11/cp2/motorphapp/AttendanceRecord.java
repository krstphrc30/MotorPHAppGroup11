package com.group11.cp2.motorphapp;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public AttendanceRecord(int employeeNumber, LocalDate date, LocalTime timeIn, LocalTime timeOut) {
        this.employeeNumber = employeeNumber;
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        computeWorkDurations();
    }

    private void computeWorkDurations() {
        if (timeIn == null || timeOut == null) {
            System.err.println("Warning: Null timeIn or timeOut for employee #" + employeeNumber + " on " + date);
            regularDuration = Duration.ZERO;
            overtimeDuration = Duration.ZERO;
            return;
        }
        if (timeOut.isBefore(timeIn)) {
            System.err.println("Warning: Invalid time range for employee #" + employeeNumber + " on " + date + ": timeOut before timeIn");
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

    public static String generatePaySummary(List<AttendanceRecord> records, List<Employee> employees) {
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
                double otPay = totalOT.toHours() * (comp.getHourlyRate() * 1.5);
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