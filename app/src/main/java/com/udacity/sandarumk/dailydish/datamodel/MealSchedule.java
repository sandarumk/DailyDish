package com.udacity.sandarumk.dailydish.datamodel;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Builder
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
    @Embedded
    @ColumnInfo(name = "recipe")
    private Recipe recipe;

    @Getter
    @Setter
    @ColumnInfo(name = "meal_time")
    private MealTime mealTime;
}
