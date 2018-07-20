package com.udacity.sandarumk.dailydish.datamodel;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Entity(foreignKeys = @ForeignKey(entity = Recipe.class,
        parentColumns = "recipe_id",
        childColumns = "recipe_id"),
        indices = @Index("recipe_id"))
public class Ingredient implements Serializable {

    @Getter
    @Setter
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ingredient_id")
    private int ingredientID;

    @Getter
    @Setter
    @ColumnInfo(name = "name")
    private String ingredientName;

    @Getter
    @Setter
    @ColumnInfo(name = "quantity")
    private int quantity;

    @Getter
    @Setter
    @ColumnInfo(name = "quantity_unit")
    private QuantityUnit quantityUnit;

    @Getter
    @Setter
    @ColumnInfo(name = "recipe_id")
    private long recipeId;
}
