package br.com.henry.finrule.business;

import java.time.Duration;
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

    public static Duration calculateNormalizedGap(ZonedDateTime past, ZonedDateTime future) {
        return Duration.between(DateUtils.normalizeToDate(past), DateUtils.normalizeToDate(future));
    }

}
