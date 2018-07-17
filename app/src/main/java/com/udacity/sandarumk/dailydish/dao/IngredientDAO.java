package com.udacity.sandarumk.dailydish.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.udacity.sandarumk.dailydish.datamodel.Ingredient;

import java.util.List;

@Dao
public interface IngredientDAO {

    @Query("SELECT * from Ingredient where ingredient_id = :id")
    Ingredient findById(int id);

    @Insert
    void addIngredient(Ingredient ingredient);

    @Query("SELECT * FROM Ingredient")
    List<Ingredient> loadAllIngredients();

    @Query("SELECT * FROM Ingredient where recipe_id=:id ")
    List<Ingredient> loadRecipeIngredient(int id);

    @Update
    void updateIngredients(Ingredient... ingredients);
}
