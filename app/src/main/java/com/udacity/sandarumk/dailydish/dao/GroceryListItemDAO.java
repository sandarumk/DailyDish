package com.udacity.sandarumk.dailydish.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.udacity.sandarumk.dailydish.datamodel.GroceryListItem;
import com.udacity.sandarumk.dailydish.datamodel.MealSchedule;

import java.util.Date;
import java.util.List;

@Dao
public interface GroceryListItemDAO {
    @Insert
    long addMealSchedule(GroceryListItem groceryListItem);

    @Query("SELECT * from GroceryListItem where groceryListItemID = :id")
    GroceryListItem findById(int id);

    @Query("SELECT * FROM GroceryListItem")
    List<GroceryListItem> loadAllGroceryLists();

    @Query("SELECT * FROM MealSchedule WHERE date=:date")
    List<GroceryListItem> loadAllGroceryListItemsForGivenDate(Date date);

    @Query("SELECT * FROM MealSchedule WHERE date BETWEEN :fromDate AND :toDate")
    List<GroceryListItem> loadAllGroceryListItemsdForGivenDateRange(Date fromDate, Date toDate);

    @Update
    void updateMealSchedule(MealSchedule... mealSchedules);
}
