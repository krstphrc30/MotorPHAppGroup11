/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group11.cp2.motorphapp;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;


/**
 *
 * @author Pil,Kristopher,Janice,Clarinda
 */
public class AttendanceRecord {
    private LocalDate date;
    private LocalTime timeIn;
    private LocalTime timeOut;

    public AttendanceRecord(LocalDate date, LocalTime timeIn, LocalTime timeOut) {
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
    }

    public double computeWorkHours() {
        Duration duration = Duration.between(timeIn, timeOut);
        return duration.toMinutes() / 60.0;
    }
}
