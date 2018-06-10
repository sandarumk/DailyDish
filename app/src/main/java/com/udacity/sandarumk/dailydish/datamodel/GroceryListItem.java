package com.udacity.sandarumk.dailydish.datamodel;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

import lombok.Getter;

@Entity
public class GroceryListItem {

    @Getter
    @PrimaryKey(autoGenerate = true)
    private int groceryListItemID;

    @Getter
    @ColumnInfo(name = "date")
    private Date date;

    @Getter
    @ColumnInfo(name = "status")
    private boolean status;

    @Getter
    @ColumnInfo(name = "meal_schedule_id")
    private int mealScheduleID;

    @Getter
    @ColumnInfo(name = "ingredient_id")
    private int ingredientID;
}
