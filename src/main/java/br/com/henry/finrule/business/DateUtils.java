package br.com.henry.finrule.business;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public final class DateUtils {

    private DateUtils() {
        // Static class
    }

    public static ZonedDateTime normalizeToDate(ZonedDateTime zonedDateTime) {
        return zonedDateTime.withZoneSameInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.DAYS);
    }

}
