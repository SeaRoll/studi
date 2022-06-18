package com.yohan.studi.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DateUtils {
    /**
     * Converts date to localDate
     *
     * @param dateToConvert date to convert
     * @return localDate format date
     */
    public static LocalDate dateToLocalDate(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    /**
     * Converts localDate to date
     * @param dateToConvert localDate to convert
     * @return date
     */
    public static Date localDateToDate(LocalDate dateToConvert) {
        return java.util.Date.from(dateToConvert.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }
}
