package com.udacity.sandarumk.dailydish.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.udacity.sandarumk.dailydish.datamodel.Recipe;

import java.util.List;

@Dao
public interface RecipeDAO {

    @Insert
    void addRecipe(Recipe recipe);

    @Query("SELECT * from Recipe where id = :id")
    Recipe findById(int id);

    @Query("SELECT * FROM Recipe")
    List<Recipe> loadAllRecipes();

    @Update
    void updateRecipe(Recipe... recipes);
}
