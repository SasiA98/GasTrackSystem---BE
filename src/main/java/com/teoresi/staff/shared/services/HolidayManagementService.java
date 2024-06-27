package com.teoresi.staff.shared.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.teoresi.staff.libs.utils.EasterCalculator;
import com.teoresi.staff.libs.utils.Holiday;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class HolidayManagementService {
    private final FileManagementService file = new FileManagementService();


    public Set<Holiday> retrieveHolidays(LocalDate startDate, LocalDate endDate) throws IOException{
        Set<Holiday> allHolidays = new HashSet<>();

        if(startDate.isAfter(endDate))
            return allHolidays;

        for(int year = startDate.getYear(); year <= endDate.getYear(); year ++)
            allHolidays.addAll(getAllHolidays(year));

        allHolidays.removeIf(holiday -> !holiday.getDate().isAfter(startDate) && !holiday.getDate().isBefore(endDate));

        return allHolidays;
    }


    public boolean isHoliday(Set<Holiday> holidays, LocalDate date){

        for(Holiday holiday : holidays){
            LocalDate holidayDate = holiday.getDate();
            if (holidayDate.equals(date))
                return true;
        }
        return false;
    }


    private Set<Holiday> getAllHolidays(int year) throws IOException {
        Set<Holiday> fixedHolidays = getFixedHolidays(year);
        Set<Holiday> easterHolidays = getEasterHolidays(year);

        Set<Holiday> allHolidays = new HashSet<>(fixedHolidays);
        allHolidays.addAll(easterHolidays);

        return allHolidays;
    }

    private Set<Holiday> getEasterHolidays(int year){
        Set<Holiday> easterHolidays = new HashSet<>();

        //Easter
        LocalDate easterDate = EasterCalculator.calculateEaster(year);
        easterHolidays.add(new Holiday(easterDate, "Pasqua"));

        //Easter Monday
        easterHolidays.add(new Holiday(easterDate.plusDays(1), "Lunedì dell'Angelo"));

        return easterHolidays;
    }

    private Set<Holiday> getFixedHolidays(int year) throws IOException {

        JsonArray fixedHolidays;
        Set<Holiday> holidays = new HashSet<>();
        fixedHolidays = file.retrieveFileInfoFromJsonArray("src/main/resources/fixed_italian_holidays.json");

        for(int i=0; i< fixedHolidays.size(); i++) {
            JsonObject holiday = fixedHolidays.get(i).getAsJsonObject();
            String date = holiday.get("date").getAsString();
            String description = holiday.get("description").getAsString();
            holidays.add(new Holiday(date, description, year));
        }

        return holidays;
    }

}
