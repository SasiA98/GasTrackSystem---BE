package com.teoresi.staff.libs.utils;

import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class Holiday {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Getter
    private LocalDate date;
    private String description;

    public Holiday(String date, String description, int year){
        String[] dayAndMonth = date.split("-");

        int day = Integer.parseInt(dayAndMonth[0]);
        int month = Integer.parseInt(dayAndMonth[1]);
        this.date = LocalDate.of(year, month, day);
        this.description = description;
    }

    public Holiday(LocalDate date, String description){
        this.date = date;
        this.description = description;
    }
}
