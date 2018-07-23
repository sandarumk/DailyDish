package com.udacity.sandarumk.dailydish.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import com.udacity.sandarumk.dailydish.datamodel.ScheduleRecipe;

import java.util.Date;
import java.util.List;

@Dao
public interface ScheduleRecipeDAO {
    @Query("SELECT MealSchedule.*, Recipe.* FROM MealSchedule INNER JOIN Recipe ON MealSchedule.recipe_id = Recipe.recipe_id WHERE MealSchedule.schedule_id = :scheduleId ORDER BY MealSchedule.date")
    List<ScheduleRecipe> findByMealScheduleId(long scheduleId);

    @Query("SELECT MealSchedule.*, Recipe.* FROM MealSchedule INNER JOIN Recipe ON MealSchedule.recipe_id = Recipe.recipe_id WHERE MealSchedule.schedule_id = :scheduleId ORDER BY MealSchedule.date")
    Cursor loadByMealScheduleId(long scheduleId);


    @Query("SELECT MealSchedule.*, Recipe.* FROM MealSchedule INNER JOIN Recipe ON MealSchedule.recipe_id = Recipe.recipe_id  WHERE date BETWEEN :fromDate AND :toDate  ORDER BY MealSchedule.date")
    List<ScheduleRecipe> findByMealScheduleId(Date fromDate, Date toDate);

    @Query("SELECT MealSchedule.*, Recipe.* FROM MealSchedule INNER JOIN Recipe ON MealSchedule.recipe_id = Recipe.recipe_id  ORDER BY MealSchedule.date ")
    Cursor loadAll();

    @Query("SELECT MealSchedule.*, Recipe.* FROM MealSchedule INNER JOIN Recipe ON MealSchedule.recipe_id = Recipe.recipe_id  WHERE date BETWEEN :fromDate AND :toDate  ORDER BY MealSchedule.date")
    Cursor loadByMealScheduleId(Date fromDate, Date toDate);
}
