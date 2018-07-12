package com.udacity.sandarumk.dailydish.datamodel;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.udacity.sandarumk.dailydish.dao.GroceryListItemDAO;
import com.udacity.sandarumk.dailydish.dao.IngredientDAO;
import com.udacity.sandarumk.dailydish.dao.MealScheduleDAO;
import com.udacity.sandarumk.dailydish.dao.RecipeDAO;
import com.udacity.sandarumk.dailydish.datamodel.typeconverters.DateTypeConverter;
import com.udacity.sandarumk.dailydish.datamodel.typeconverters.MealTimeTypeConverter;
import com.udacity.sandarumk.dailydish.datamodel.typeconverters.QuantityUnitTypeConverter;

@Database(entities = {Ingredient.class, Recipe.class, MealSchedule.class, GroceryListItem.class}, version = 1)
@TypeConverters({QuantityUnitTypeConverter.class, DateTypeConverter.class, MealTimeTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract RecipeDAO recipeDAO();
    public abstract IngredientDAO ingredientDAO();
    public abstract MealScheduleDAO mealScheduleDAO();
    public abstract GroceryListItemDAO groceryListItemDAO();

}
