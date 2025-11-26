package com.ui.utilities;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static final String FORMAT_DATE_FROM = "yyyy/MM/dd";
    public static final String FORMAT_DATE_MMM = "MMM";
    public static final String FORMAT_DATE_YYYY_MM = "YYYY-MM";
    public static final String FORMAT_DD_MM_YY = "dd/MM/YY";
    public static final String FORMAT_DATE_YYYYMMDD = "yyyyMMdd";


    public static String getDateAsString(String getDate, String formatFrom, String formatTo) {
        return DateTimeFormatter
                .ofPattern(formatTo)
                .format(LocalDate.parse(getDate, DateTimeFormatter.ofPattern(formatFrom)));
    }

    /**
     * This method take input date as dd/MM/yyyy and return value as Month + 2
     * @param getDate
     * @param formatFrom
     * @param formatTo
     * @param plusMonth
     * @return
     */
    public static String getPlusMonthsFromDateAsString(String getDate, String formatFrom, String formatTo, int plusMonth) {
        return DateTimeFormatter
                .ofPattern(formatTo)
                .format(LocalDate.parse(getDate, DateTimeFormatter.ofPattern(formatFrom)).plusMonths(plusMonth));
    }

    /**
     * This method return month value in MMM format eg.Jun, Jul, Aug etc.
     * @param plusMonth which use to add the months eg. CurrentMonth + (Additional month)
     * @return month value in MMM format.
     */
    public static String getPlusMonthBasedOnCurrentMonth(int plusMonth) {
        return DateTimeFormatter
                .ofPattern(FORMAT_DATE_MMM)
                .format(LocalDate.now().plusMonths(plusMonth));
    }

    public static String getCurrentMonth() {
        return DateTimeFormatter
                .ofPattern(FORMAT_DATE_MMM)
                .format(LocalDate.now());
    }

    public static String getCurrentMonthYYYYMM() {
        return DateTimeFormatter
                .ofPattern(FORMAT_DATE_YYYY_MM)
                .format(LocalDate.now());
    }
}
