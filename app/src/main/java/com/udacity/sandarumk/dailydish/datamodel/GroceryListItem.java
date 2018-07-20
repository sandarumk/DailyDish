package com.udacity.sandarumk.dailydish.datamodel;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Entity(foreignKeys = @ForeignKey(entity = Ingredient.class,
        parentColumns = "ingredient_id",
        childColumns = "ingredient_id"),
        indices = @Index("ingredient_id"))
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
    @ColumnInfo(name = "ingredient_id")
    private long ingredientId;

    @Getter
    @Setter
    @ColumnInfo(name = "is_manually_added")
    private boolean isManual;


}
