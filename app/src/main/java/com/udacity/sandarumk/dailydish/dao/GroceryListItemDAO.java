package com.udacity.sandarumk.dailydish.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.udacity.sandarumk.dailydish.datamodel.GroceryListItem;

import java.util.Date;
import java.util.List;

@Dao
public interface GroceryListItemDAO {
    @Insert
    long addGroceryItem(GroceryListItem groceryListItem);

    @Query("SELECT * from GroceryListItem where groceryListItemID = :id")
    GroceryListItem findById(int id);

    @Query("SELECT * FROM GroceryListItem")
    List<GroceryListItem> loadAllGroceryLists();

    @Query("SELECT * FROM GroceryListItem WHERE date=:date")
    List<GroceryListItem> loadAllGroceryListItemsForGivenDate(Date date);

    @Query("SELECT * FROM GroceryListItem WHERE date BETWEEN :fromDate AND :toDate")
    List<GroceryListItem> loadAllGroceryListItemsdForGivenDateRange(Date fromDate, Date toDate);

    @Query("SELECT * FROM GroceryListItem WHERE is_manually_added = 'true' AND date BETWEEN :fromDate AND :toDate")
    List<GroceryListItem> loadManuallyAddedGroceryListItemsdForGivenDateRange(Date fromDate, Date toDate);

    @Query("SELECT * FROM GroceryListItem WHERE is_manually_added = 'false' AND date BETWEEN :fromDate AND :toDate")
    List<GroceryListItem> loadRecipeAddedGroceryListItemsdForGivenDateRange(Date fromDate, Date toDate);

    @Query("UPDATE GroceryListItem SET status=:status WHERE groceryListItemID = :id")
    void updateStatus(int id, boolean status);
}
