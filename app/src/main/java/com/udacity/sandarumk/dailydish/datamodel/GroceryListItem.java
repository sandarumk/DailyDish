package com.udacity.sandarumk.dailydish.datamodel;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Entity
public class GroceryListItem {

    @Getter
    @Setter
    @PrimaryKey(autoGenerate = true)
    private int groceryListItemID;

    @Getter
    @Setter
    @ColumnInfo(name = "grocery_list_item_name")
    private String groceryListItemName;

    @Getter
    @Setter
    @ColumnInfo(name = "date")
    private Date date;

    @Getter
    @Setter
    @ColumnInfo(name = "status")
    private boolean status;

    @Getter
    @Setter
    @ColumnInfo(name = "meal_schedule_id")
    private int mealScheduleID;

    @Getter
    @Setter
    @Embedded
    private Ingredient ingredient;

}
