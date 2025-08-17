package io.github.cheatbook.amzrevenuemanager.domain.constant;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class DateTimeFormats {

    private DateTimeFormats() {
    }

    public static final DateTimeFormatter ADVERTISEMENT_DATE_FORMAT = DateTimeFormatter.ofPattern("d-MMM-yy", Locale.ENGLISH);
    public static final DateTimeFormatter SETTLEMENT_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss z");

}