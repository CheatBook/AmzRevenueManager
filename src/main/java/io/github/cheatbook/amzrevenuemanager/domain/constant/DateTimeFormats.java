package io.github.cheatbook.amzrevenuemanager.domain.constant;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * 日時フォーマットを定義する定数クラスです。
 */
public final class DateTimeFormats {

    /**
     * プライベートコンストラクタ
     */
    private DateTimeFormats() {
    }

    /**
     * 広告レポートの日付フォーマット
     */
    public static final DateTimeFormatter ADVERTISEMENT_DATE_FORMAT = DateTimeFormatter.ofPattern("d-MMM-yy", Locale.ENGLISH);

    /**
     * 決済レポートの日時フォーマット
     */
    public static final DateTimeFormatter SETTLEMENT_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss z");

}