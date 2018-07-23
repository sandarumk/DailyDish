package com.udacity.sandarumk.dailydish.datamodel;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class ScheduleRecipe {
    @Getter
    @Setter
    @Embedded
    MealSchedule mealSchedule;

    @Getter
    @Setter
    @Relation(parentColumn =  "recipe_id", entityColumn = "recipe_id")
    List<Recipe> recipe;

}