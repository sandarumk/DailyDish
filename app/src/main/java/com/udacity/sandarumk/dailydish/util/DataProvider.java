package com.udacity.sandarumk.dailydish.util;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.udacity.sandarumk.dailydish.dao.IngredientDAO;
import com.udacity.sandarumk.dailydish.dao.RecipeDAO;
import com.udacity.sandarumk.dailydish.datamodel.AppDatabase;
import com.udacity.sandarumk.dailydish.datamodel.Ingredient;
import com.udacity.sandarumk.dailydish.datamodel.Recipe;
import com.udacity.sandarumk.dailydish.datawrappers.RecipeWrapper;

import java.util.List;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DataProvider {

    public AppDatabase getDatabase(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "dailydish").build();
    }

    public void saveRecipe(Context context, Recipe recipe, List<Ingredient> ingredientList) {
        RecipeDAO recipeDAO = getDatabase(context).recipeDAO();
        IngredientDAO ingredientDAO = getDatabase(context).ingredientDAO();
        if (recipe.getId() > 0) {
            recipeDAO.updateRecipe(recipe);
        } else {
            recipeDAO.addRecipe(recipe);
            //TODO set newly generated ID to recipe.id
        }

        for (Ingredient ingredient : ingredientList) {
            ingredient.setRecipeID(recipe.getId());
            if (ingredient.getIngredientID() > 0) {
                ingredientDAO.updateIngredients(ingredient);
            } else {
                ingredientDAO.addIngredient(ingredient);
            }
        }
    }

    public RecipeWrapper loadRecipe(Context context, int recipeId) {
        RecipeDAO recipeDAO = getDatabase(context).recipeDAO();
        IngredientDAO ingredientDAO = getDatabase(context).ingredientDAO();
        return RecipeWrapper.builder()
                .recipe(recipeDAO.findById(recipeId))
                .recipeIngredientList(ingredientDAO.loadRecipeIngredient(recipeId))
                .build();

    }

    public List<Recipe> loadAllRecipes(Context context) {
        return getDatabase(context).recipeDAO().loadAllRecipes();
    }


}
