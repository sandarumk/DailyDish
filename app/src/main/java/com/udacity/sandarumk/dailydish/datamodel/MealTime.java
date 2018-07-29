package com.udacity.sandarumk.dailydish.datamodel;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum MealTime {
    BREAKFAST(1),
    LUNCH(2),
    DINNER(3);

    @Getter
    private final int mealTime;

    public static MealTime findMealTime(int mealTime) {
        for (MealTime time : values()) {
            if (mealTime == time.mealTime) {
                return time;
            }
        }
        return null;
    }
}
