package com.udacity.sandarumk.dailydish.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.udacity.sandarumk.dailydish.datamodel.Ingredient;

@Dao
public interface IngredientDAO {

    @Query("SELECT * from Ingredient where ingredientID = :id")
    Ingredient findById(int id);
}
