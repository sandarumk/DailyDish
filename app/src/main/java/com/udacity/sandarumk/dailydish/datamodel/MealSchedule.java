package com.udacity.sandarumk.dailydish.datamodel;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Entity(foreignKeys = @ForeignKey(entity = Recipe.class,
        parentColumns = "recipe_id",
        childColumns = "recipe_id"),
        indices = @Index("recipe_id"))
public class MealSchedule {

    @Getter
    @Setter
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "schedule_id")
    private long scheduleID;

    @Getter
    @Setter
    @ColumnInfo(name = "date")
    private Date date;

    @Getter
    @Setter
    @ColumnInfo(name = "recipe_id")
    private int recipeId;

    @Getter
    @Setter
    @ColumnInfo(name = "meal_time")
    private MealTime mealTime;
}
