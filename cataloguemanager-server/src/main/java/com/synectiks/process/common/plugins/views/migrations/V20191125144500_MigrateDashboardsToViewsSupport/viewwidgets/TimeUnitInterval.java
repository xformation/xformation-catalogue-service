/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.viewwidgets;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.BucketInterval;

import java.util.Locale;

@AutoValue
public abstract class TimeUnitInterval implements Interval {
    public static final String type = "timeunit";

    public enum IntervalUnit {
        SECONDS("seconds"),
        MINUTES("minutes"),
        HOURS("hours"),
        DAYS("days"),
        WEEKS("weeks"),
        MONTHS("months"),
        YEARS("years"),

        AUTO("auto");

        private final String name;

        IntervalUnit(String name) {
            this.name = name;
        }

        @Override
        @JsonValue
        public String toString() {
            return this.name;
        }
    }

    static final String FIELD_TYPE = "type";
    static final String FIELD_VALUE = "value";
    static final String FIELD_UNIT = "unit";

    @JsonProperty
    public abstract String type();

    @JsonProperty(FIELD_VALUE)
    public abstract int value();

    @JsonProperty(FIELD_UNIT)
    public abstract IntervalUnit unit();

    @Override
    public BucketInterval toBucketInterval() {
        final String esUnit = mapUnit(unit());
        return com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.TimeUnitInterval.create(value() + esUnit);
    }

    private String mapUnit(TimeUnitInterval.IntervalUnit unit) {
        switch (unit) {
            case SECONDS: return "s";
            case MINUTES: return "m";
            case HOURS: return "h";
            case DAYS: return "d";
            case WEEKS: return "w";
            case MONTHS: return "M";
            case YEARS: return "y";
        }

        throw new RuntimeException("Unable to map interval unit: " + unit);
    }

    private static Builder builder() {
        return new AutoValue_TimeUnitInterval.Builder().type(type);
    }

    public static TimeUnitInterval create(IntervalUnit unit, int value) {
        return builder()
                .unit(unit)
                .value(value)
                .build();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder type(String type);
        public abstract Builder value(int value);
        public abstract Builder unit(IntervalUnit unit);
        public Builder unit(String unit) {
            return unit(IntervalUnit.valueOf(unit.toUpperCase(Locale.ENGLISH)));
        }

        public abstract TimeUnitInterval build();

    }
}
