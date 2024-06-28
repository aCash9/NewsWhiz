package com.example.technews;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class GetDate {
    public String convertDate(String date) {
        String dateString = "2024-05-27T14:21:36Z";

        // Parse the date string into LocalDateTime
        LocalDateTime dateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME);

        // Get the day name (e.g., "Sunday")
        String dayName = dateTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        // Get the day of the month (e.g., "27")
        int dayOfMonth = dateTime.getDayOfMonth();

        // Get the month name (e.g., "May")
        String monthName = dateTime.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        // Get the year (e.g., "2024")
        int year = dateTime.getYear();

        // Format the date in the desired format
        String formattedDate = String.format("%s, %d %s %d", dayName, dayOfMonth, monthName, year);

        return formattedDate;
    }

}
