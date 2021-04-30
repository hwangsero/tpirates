package com.example.tpirates.store;

import java.util.Arrays;

public enum Day {
    Monday(1),Tuesday(2),Wednesday(3),Thursday(4),Friday(5),Saturday(6),Sunday(7);

    final int day;
    Day(int day) {
        this.day = day;
    }

    public static Day intToDay(int day) {
        return Arrays.stream(Day.values())
                .filter(d -> d.day == day)
                .findAny()
                .orElse(null);
    }

}
