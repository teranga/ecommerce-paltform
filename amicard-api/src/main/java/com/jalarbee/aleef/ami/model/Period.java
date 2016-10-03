package com.jalarbee.aleef.ami.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;

/**
 * @author Abdoulaye Diallo
 */

public final class Period {

    public final Month month;
    public final Year year;

    private Period(Month month, Year year) {
        this.month = month;
        this.year = year;
    }

    @JsonCreator
    public static Period of(Month month, Year year) {
        return new Period(month, year);
    }

    @JsonCreator
    public static Period of(LocalDateTime dateTime) {
        return Period.of(dateTime.getMonth(), Year.of(dateTime.getYear()));
    }

    @JsonCreator
    public static Period now() {
        return Period.of(LocalDateTime.now());
    }

    @Override
    public String toString() {
        return String.format("%4d-%2d", year.getValue(), month.getValue());
    }
}
