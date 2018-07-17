package com.udacity.sandarumk.dailydish.util;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.udacity.sandarumk.dailydish.dao.IngredientDAO;
import com.udacity.sandarumk.dailydish.dao.RecipeDAO;
import com.udacity.sandarumk.dailydish.datamodel.AppDatabase;
import com.udacity.sandarumk.dailydish.datamodel.GroceryListItem;
import com.udacity.sandarumk.dailydish.datamodel.Ingredient;
import com.udacity.sandarumk.dailydish.datamodel.MealSchedule;
import com.udacity.sandarumk.dailydish.datamodel.MealTime;
import com.udacity.sandarumk.dailydish.datamodel.Recipe;
import com.udacity.sandarumk.dailydish.datawrappers.DayWrapper;
import com.udacity.sandarumk.dailydish.datawrappers.GroceryItemWrapper;
import com.udacity.sandarumk.dailydish.datawrappers.RecipeGroceryItemWrapper;
import com.udacity.sandarumk.dailydish.datawrappers.RecipeWrapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            recipe.setId((int) recipeDAO.addRecipe(recipe));
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

    public List<DayWrapper> loadSchedule(Context context, Date from, Date to) {
        //load data and create day wrapper list for the date range from to to
        Map<Date, DayWrapper> dayWrapperMap = new HashMap<>();
        for (MealSchedule mealSchedule : getDatabase(context).mealScheduleDAO().loadAllMealSchedulesForGivenDateRange(from, to)) {
            MealTime mealTime = mealSchedule.getMealTime();
            Recipe recipe = mealSchedule.getRecipe();
            Date mealScheduleDate = mealSchedule.getDate();
            if (dayWrapperMap.containsKey(mealSchedule.getDate())) {
                DayWrapper dayWrapper = dayWrapperMap.get(mealScheduleDate);
                Map<MealTime, List<Recipe>> dailyMealMap = dayWrapper.getSchedule();
                List<Recipe> recipeList = dailyMealMap.get(mealTime);
                if (recipeList == null) {
                    recipeList = new ArrayList<>();
                    dailyMealMap.put(mealTime, recipeList);
                }
                recipeList.add(recipe);
            } else {
                List<Recipe> recipeList = new ArrayList<>();
                recipeList.add(recipe);
                Map<MealTime, List<Recipe>> dailyMealMap = new HashMap<>();
                dailyMealMap.put(mealTime, recipeList);
                DayWrapper dayWrapper = DayWrapper.builder()
                        .date(mealScheduleDate)
                        .schedule(dailyMealMap)
                        .build();
                dayWrapperMap.put(mealScheduleDate, dayWrapper);
            }
        }
        return new ArrayList<>(dayWrapperMap.values());
    }

    public void saveSchedule(Context context, Date date, MealTime mealTime, Recipe recipe) {
        MealSchedule mealSchedule = MealSchedule.builder()
                .mealTime(mealTime)
                .recipe(recipe)
                .date(date)
                .build();
        getDatabase(context).mealScheduleDAO().addMealSchedule(mealSchedule);
    }

    public List<GroceryItemWrapper> loadManualGroceryList(Context context, Date from, Date to) {
        //TODO load manually added grocery items for the given date range
        List<GroceryItemWrapper> groceryItemWrapperList = new ArrayList<>();
        for (GroceryListItem groceryListItem : getDatabase(context).groceryListItemDAO().loadAllGroceryListItemsdForGivenDateRange(from, to)) {
            GroceryItemWrapper groceryItemWrapper = GroceryItemWrapper.builder()
                    .ingredientName(groceryListItem.getIngredient().getIngredientName())
                    .quantityUnit(groceryListItem.getIngredient().getQuantityUnit())
                    .checked(groceryListItem.isStatus())
        }
    }

    public List<RecipeGroceryItemWrapper> loadRecipeGroceryList(Context context, Date from, Date to) {
        //TODO load grocery items populated from the meal schedules for the given date range
    }


}
