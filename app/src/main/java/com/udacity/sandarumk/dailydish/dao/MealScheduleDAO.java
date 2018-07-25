package com.udacity.sandarumk.dailydish.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

import com.udacity.sandarumk.dailydish.datamodel.MealSchedule;
import com.udacity.sandarumk.dailydish.datamodel.MealTime;

import java.util.Date;
import java.util.List;

@Dao
public interface MealScheduleDAO {

    @Insert
    long addMealSchedule(MealSchedule mealSchedule);

    @Query("SELECT * from MealSchedule where schedule_id = :id")
    Cursor findById(long id);

    @Query("SELECT * FROM MealSchedule")
    List<MealSchedule> loadAllMealSchedules();

    @Query("SELECT * FROM MealSchedule WHERE date=:date")
    List<MealSchedule> loadAllMealSchedulesForGivenDate(Date date);

    @Query("SELECT * FROM MealSchedule WHERE date=:date AND meal_time=:mealTime")
    List<MealSchedule> loadAllMealSchedulesForGivenDateGivenMealTime(Date date, MealTime mealTime);

    @Query("SELECT * FROM MealSchedule WHERE date BETWEEN :fromDate AND :toDate")
    List<MealSchedule> loadAllMealSchedulesForGivenDateRange(Date fromDate, Date toDate);

    @Query("SELECT * FROM MealSchedule WHERE date=:date AND meal_time=:mealTimeId AND recipe_id=:recipeId")
    List<MealSchedule> findMealSchedules(Date date, int mealTimeId, long recipeId);


    @Query("SELECT * FROM MealSchedule WHERE date BETWEEN :fromDate AND :toDate ORDER BY date ASC")
    Cursor loadForGivenDateRange(Date fromDate, Date toDate);

    @Query("SELECT * FROM MealSchedule ")
    Cursor loadAll();

    @Update
    void updateMealSchedule(MealSchedule... mealSchedules);

    @Delete
    void deleteMealSchedule(MealSchedule mealSchedule);

    @Query("DELETE FROM MealSchedule WHERE date=:date AND meal_time=:mealTimeId AND recipe_id=:recipeId")
    void deleteMealSchedule(Date date, int mealTimeId, long recipeId);

    @Query("SELECT DISTINCT meal_time FROM MealSchedule WHERE recipe_id=:recipeId ORDER BY meal_time ASC")
    List<MealTime> findDistinctMealTimeForRecipe(long recipeId);
}
