package com.udacity.sandarumk.dailydish.datamodel;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

import lombok.Getter;

@Entity
public class MealSchedule {

    @Getter
    @PrimaryKey(autoGenerate = true)
    private int scheduleID;

    @Getter
    @ColumnInfo(name = "date")
    private Date date;

    @Getter
    @ColumnInfo(name = "recipe_id")
    private int recipleID;

    @Getter
    @ColumnInfo(name = "meal_time")
    private MealTime mealTime;
}
