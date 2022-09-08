package com.ulalalab.snipe.common.util;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;


public class LocalDateUtil {

    public final static String DATE_MONTH_FORMAT = "yyyyMM";
    public final static String DATE_FORMAT = "yyyyMMdd";
    public final static String DATE_TIME_FORMAT = "yyyyMMddHHmmss";

    public final static String DATE_MONTH_FORMAT_STATS ="yyyy/MM";

    /**
     * @return 현재 UTC LocalDateTime
     */
    public static LocalDateTime getNowUTCLocalDateTime() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    /**
     * @return 현재 UTC LocalDate
     */
    public static LocalDate getNowUTCLocalDate() {
        return LocalDate.now(ZoneOffset.UTC);
    }

    /**
     * UTC Timestamp
     * @param localDateTime
     * @return timestamp Long 타입
     */
    public static Long getTimestmp(LocalDateTime localDateTime) {
        if(localDateTime!=null) {
            Timestamp timestamp = Timestamp.valueOf(localDateTime);
            return timestamp.getTime();
        } else {
            return 0L;
        }
    }

    /**
     * 현재 UTC Timestamp
     * @return timestamp Long 타입
     */
    public static Long getUTCTimestmp() {
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        return timestamp.getTime();
    }


    /**
     * String -> LocalDate
     */
    public static LocalDate getLocalDate(String date, String pattern) {
        return LocalDate.parse(date, getDateTimeFormatter(pattern));
    }

    /**
     * LocalDate -> String
     */
    public static String getLocalDateString(LocalDate localDate, String pattern) {
        return getDateTimeFormatter(pattern).format(localDate);
    }

    /**
     * String -> LocalDateTime
     */
    public static LocalDateTime getLocalDateTime(String date, String pattern) {
        return LocalDateTime.parse(date, getDateTimeFormatter(pattern));
    }

    /**
     * LocalDate -> String
     */
    public static String getLocalDateTimeString(LocalDateTime localDateTime, String pattern) {
        return getDateTimeFormatter(pattern).format(localDateTime);
    }

    /**
     * DateTime 패턴 포맷터
     * @param pattern String
     */
    private static DateTimeFormatter getDateTimeFormatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern);
    }

    /**
     * DateTime 패턴 포맷터
     * @param pattern String
     */
    public static String plusMonth(LocalDate localDate, String DATE_MONTH_FORMAT, int plus) {
        return DateTimeFormatter.ofPattern(DATE_MONTH_FORMAT).format(localDate.plusMonths(plus));
    }

    /**
     * Date 패턴 포맷터
     * @param pattern String
     */
}