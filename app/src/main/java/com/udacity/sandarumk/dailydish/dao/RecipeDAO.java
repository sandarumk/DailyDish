package com.udacity.sandarumk.dailydish.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.udacity.sandarumk.dailydish.datamodel.Recipe;

import java.util.List;

@Dao
public interface RecipeDAO {

    @Insert
    long addRecipe(Recipe recipe);

    @Query("SELECT * from Recipe where recipe_id = :id")
    Recipe findById(long id);

    @Query("SELECT * FROM Recipe")
    List<Recipe> loadAllRecipes();

    @Query("SELECT * FROM Recipe WHERE name != 'Manually Added' ORDER BY name")
    LiveData<List<Recipe>> loadLiveRecipes();

    @Update
    void updateRecipe(Recipe... recipes);

    @Query("SELECT name FROM Recipe WHERE recipe_id = :recipeID")
    String getRecipeNameGivenId(int recipeID);

    @Query("SELECT * FROM Recipe WHERE name = :name")
    Recipe getRecipeGivenName(String name);
}
