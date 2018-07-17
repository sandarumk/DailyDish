package com.udacity.sandarumk.dailydish.util;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.squareup.haha.guava.collect.ArrayListMultimap;
import com.squareup.haha.guava.collect.Multimap;
import com.udacity.sandarumk.dailydish.dao.IngredientDAO;
import com.udacity.sandarumk.dailydish.dao.RecipeDAO;
import com.udacity.sandarumk.dailydish.datamodel.AppDatabase;
import com.udacity.sandarumk.dailydish.datamodel.GroceryListItem;
import com.udacity.sandarumk.dailydish.datamodel.Ingredient;
import com.udacity.sandarumk.dailydish.datamodel.MealSchedule;
import com.udacity.sandarumk.dailydish.datamodel.MealTime;
import com.udacity.sandarumk.dailydish.datamodel.Recipe;
import com.udacity.sandarumk.dailydish.datawrappers.DayWrapper;
import com.udacity.sandarumk.dailydish.datawrappers.GroceryItemBreakdownWrapper;
import com.udacity.sandarumk.dailydish.datawrappers.GroceryItemWrapper;
import com.udacity.sandarumk.dailydish.datawrappers.RecipeWrapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DataProvider {

    public static final String JOINER = "~";

    public AppDatabase getDatabase(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "dailydish").build();
    }

    public void saveRecipe(Context context, Recipe recipe, List<Ingredient> ingredientList) {
        RecipeDAO recipeDAO = getDatabase(context).recipeDAO();
        IngredientDAO ingredientDAO = getDatabase(context).ingredientDAO();
        if (recipe.getRecipeId() > 0) {
            recipeDAO.updateRecipe(recipe);
        } else {
            recipe.setRecipeId((int) recipeDAO.addRecipe(recipe));
        }

        for (Ingredient ingredient : ingredientList) {
            ingredient.setRecipeId(recipe.getRecipeId());
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
            Recipe recipe = getDatabase(context).recipeDAO().findById(mealSchedule.getRecipeId());
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
        MealSchedule mealSchedule =new MealSchedule();
        mealSchedule.setMealTime(mealTime);
        mealSchedule.setDate(date);
        mealSchedule.setRecipeId(recipe.getRecipeId());
        getDatabase(context).mealScheduleDAO().addMealSchedule(mealSchedule);
        updateGroceryList(context,recipe);
    }

    private static void updateGroceryList(Context context,Recipe recipe) {
        for (Ingredient ingredient : getDatabase(context).ingredientDAO().loadRecipeIngredient(recipe.getRecipeId())) {
            GroceryListItem item = new GroceryListItem();
            item.setIngredientId(ingredient.getIngredientID());
            item.setManual(false);
            item.setStatus(false);
            getDatabase(context).groceryListItemDAO().addGroceryItem(item);
        }
    }

    public void deleteSchedule(Context context, Date date, MealTime mealTime, Recipe recipe) {
        //TODO delete meal schedule
        getDatabase(context).mealScheduleDAO().deleteMealSchedule(date,mealTime.getMealTime(),recipe.getRecipeId());

        //TODO delete grocery list items with all recipe.ingredients
    }

    public List<GroceryItemWrapper> loadGroceryList(Context context, Date from, Date to) {
        Multimap<String, GroceryItemBreakdownWrapper> groceryItemMap = ArrayListMultimap.create();
        for (GroceryListItem groceryListItem : getDatabase(context).groceryListItemDAO().loadAllGroceryListItemsdForGivenDateRange(from, to)) {
            Ingredient ingredient = getDatabase(context).ingredientDAO().findById(groceryListItem.getIngredientId());
            Recipe recipe = getDatabase(context).recipeDAO().findById(ingredient.getRecipeId());
            GroceryItemBreakdownWrapper groceryBreakdownItem = GroceryItemBreakdownWrapper.builder()
                    .recipeName(recipe.getRecipeName())
                    .quantityUnit(ingredient.getQuantityUnit())
                    .quantity(ingredient.getQuantity())
                    .date(groceryListItem.getDate())
                    .manual(groceryListItem.isManual())
                    .id(groceryListItem.getGroceryListItemID())
                    .build();
            groceryItemMap.put(ingredient.getIngredientName()+JOINER+ ingredient.getQuantityUnit(),groceryBreakdownItem);
        }
        return createGroceryListWrappers(groceryItemMap);
    }

    private static List<GroceryItemWrapper> createGroceryListWrappers(Multimap<String, GroceryItemBreakdownWrapper> groceryItemMap) {
        List<GroceryItemWrapper> groceryItemWrapperList = new ArrayList<>();
        for (String s : groceryItemMap.asMap().keySet()) {
            ArrayList<GroceryItemBreakdownWrapper> breakdownWrappers = new ArrayList<>(groceryItemMap.get(s));
            GroceryItemWrapper groceryItemWrapper = GroceryItemWrapper.builder()
                    .ingredientName(s.split(JOINER)[0])
                    .breakdownWrappers(breakdownWrappers)
                    .build();
            groceryItemWrapperList.add(groceryItemWrapper);
        }
        return aggregate(groceryItemWrapperList);
    }

    private static List<GroceryItemWrapper> aggregate(List<GroceryItemWrapper> groceryItemWrapperList) {
        for (GroceryItemWrapper groceryItemWrapper : groceryItemWrapperList) {
            int totalQuantity = 0;
            for (GroceryItemBreakdownWrapper groceryItemBreakdownWrapper : groceryItemWrapper.getBreakdownWrappers()) {
                totalQuantity+= groceryItemBreakdownWrapper.getQuantity();
            }
            groceryItemWrapper.setTotalQuantity(totalQuantity);
        }
        return groceryItemWrapperList;
    }

    public static void checkGroceryListItem(Context context,GroceryItemWrapper itemWrapper, boolean isChecked){
        for (GroceryItemBreakdownWrapper groceryItemBreakdownWrapper : itemWrapper.getBreakdownWrappers()) {
            getDatabase(context).groceryListItemDAO().updateStatus(groceryItemBreakdownWrapper.getId(),isChecked);
        }

    }


}
