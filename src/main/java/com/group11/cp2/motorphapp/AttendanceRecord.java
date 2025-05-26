package com.group11.cp2.motorphapp;

import java.time.*;
import java.time.temporal.WeekFields;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
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
        if (timeIn == null || timeOut == null || timeOut.isBefore(timeIn)) {
            regularDuration = Duration.ZERO;
            overtimeDuration = Duration.ZERO;
            return;
        }

        boolean isLate = timeIn.isAfter(GRACE_END);
        if (timeIn.isAfter(WORK_START) && timeIn.isBefore(GRACE_END)) {
            timeIn = WORK_START;
        }

        LocalTime adjustedLogOut = timeOut.isAfter(WORK_END) ? WORK_END : timeOut;
        Duration workDuration = Duration.between(timeIn, adjustedLogOut).minus(BREAK_TIME);
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

            // Sort records by date to ensure proper week order
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
