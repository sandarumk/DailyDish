package com.udacity.sandarumk.dailydish.datamodel;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Entity
public class MealSchedule {

    @Getter
    @Setter
    @PrimaryKey(autoGenerate = true)
    private int scheduleID;

    @Getter
    @Setter
    @ColumnInfo(name = "date")
    private Date date;

    @Getter
    @Setter
    @ColumnInfo(name = "recipe_id")
    private int recipleID;

    @Getter
    @Setter
    @ColumnInfo(name = "meal_time")
    private MealTime mealTime;
}
