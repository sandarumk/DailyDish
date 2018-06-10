package com.udacity.sandarumk.dailydish.datamodel;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import lombok.Getter;

@Entity
public class Recipe {

    @Getter
    @PrimaryKey(autoGenerate = true)
    private int id;

    @Getter
    @ColumnInfo(name = "name")
    private String recipeName;

    @Getter
    @ColumnInfo(name = "steps")
    private String recipeSteps;

    @Getter
    @ColumnInfo(name = "notes")
    private String recipeNotes;

    // mealTime will be represented as following
    // 100 - breakfast only - 4
    // 010 - lunch only - 2
    // 001 - dinner only - 1
    // 011 - lunch and dinner only - 3
    // 101 - breakfast and dinner only - 5
    // 110 - breakfast and lunch only - 6
    // 111 - breakfast, lunch and dinner - 7
    @Getter
    @ColumnInfo(name = "meal_time")
    private int mealTime;
}
