package com.udacity.sandarumk.dailydish.datamodel;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import lombok.Getter;

@Entity
public class Ingredient {

    @Getter
    @PrimaryKey(autoGenerate = true)
    private int ingredientID;

    @Getter
    @ColumnInfo(name = "name")
    private String ingredientName;

    @Getter
    @ColumnInfo(name = "quantity")
    private int quantity;

    @Getter
    @ColumnInfo(name = "quantity_unit")
    private QuantityUnit quantityUnit;

    @Getter
    @ColumnInfo(name = "recipe_id")
    private int recipeID;
}
