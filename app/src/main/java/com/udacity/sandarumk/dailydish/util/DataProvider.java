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
import com.udacity.sandarumk.dailydish.datamodel.QuantityUnit;
import com.udacity.sandarumk.dailydish.datamodel.Recipe;
import com.udacity.sandarumk.dailydish.datawrappers.DayWrapper;
import com.udacity.sandarumk.dailydish.datawrappers.GroceryItemBreakdownWrapper;
import com.udacity.sandarumk.dailydish.datawrappers.GroceryItemWrapper;
import com.udacity.sandarumk.dailydish.datawrappers.RecipeWrapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DataProvider {

    public static final String JOINER = "~";
    public static final String MANUALLY_ADDED = "Manually Added";

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

    public RecipeWrapper loadRecipe(Context context, long recipeId) {
        RecipeDAO recipeDAO = getDatabase(context).recipeDAO();
        IngredientDAO ingredientDAO = getDatabase(context).ingredientDAO();
        return RecipeWrapper.builder()
                .recipe(recipeDAO.findById(recipeId))
                .recipeIngredientList(ingredientDAO.loadRecipeIngredient(recipeId))
                .build();

    }

    public List<Recipe> loadAllRecipes(Context context) {
        List<Recipe> recipes = getDatabase(context).recipeDAO().loadAllRecipes();
        Iterator<Recipe> iter = recipes.listIterator();
        while (iter.hasNext()) {
            if (iter.next().getRecipeName().equals(MANUALLY_ADDED)) {
                iter.remove();
            }
        }
        return recipes;
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
        MealSchedule mealSchedule = new MealSchedule();
        mealSchedule.setMealTime(mealTime);
        mealSchedule.setDate(date);
        mealSchedule.setRecipeId(recipe.getRecipeId());
        mealSchedule.setScheduleID(getDatabase(context).mealScheduleDAO().addMealSchedule(mealSchedule));

        updateRecipeMealTime(context, recipe);

        updateGroceryList(context, date, recipe);
    }

    private static void updateRecipeMealTime(Context context, Recipe recipe) {
        //update meal time for the recipe
        Recipe storedRecipe = getDatabase(context).recipeDAO().findById(recipe.getRecipeId());
        List<MealTime> distinctMealTimeForRecipe = getDatabase(context).mealScheduleDAO().findDistinctMealTimeForRecipe(recipe.getRecipeId());

        String mealTimeStr = "";
        mealTimeStr += distinctMealTimeForRecipe.contains(MealTime.BREAKFAST) ? "1" : "0";
        mealTimeStr += distinctMealTimeForRecipe.contains(MealTime.LUNCH) ? "1" : "0";
        mealTimeStr += distinctMealTimeForRecipe.contains(MealTime.DINNER) ? "1" : "0";
        storedRecipe.setMealTime(Integer.parseInt(mealTimeStr));
        getDatabase(context).recipeDAO().updateRecipe(storedRecipe);
    }

    private static void updateGroceryList(Context context, Date date, Recipe recipe) {
        for (Ingredient ingredient : getDatabase(context).ingredientDAO().loadRecipeIngredient(recipe.getRecipeId())) {
            GroceryListItem item = new GroceryListItem();
            item.setIngredientId(ingredient.getIngredientID());
            item.setManual(false);
            item.setStatus(false);
            item.setDate(date);
            item.setGroceryListItemName(ingredient.getIngredientName());
            getDatabase(context).groceryListItemDAO().addGroceryItem(item);
        }
    }

    public void deleteSchedule(Context context, Date date, MealTime mealTime, Recipe recipe) {
        //delete only one meal schedule
        List<MealSchedule> mealSchedules = getDatabase(context).mealScheduleDAO().findMealSchedules(date, mealTime.getMealTime(), recipe.getRecipeId());
        if (!mealSchedules.isEmpty()) {
            getDatabase(context).mealScheduleDAO().deleteMealSchedule(mealSchedules.get(0));
        }

        updateRecipeMealTime(context, recipe);

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
                    .checked(groceryListItem.isStatus())
                    .build();
            groceryItemMap.put(ingredient.getIngredientName() + JOINER + ingredient.getQuantityUnit() + JOINER + groceryListItem.isStatus(), groceryBreakdownItem);
        }
        return createGroceryListWrappers(groceryItemMap);
    }

    private static List<GroceryItemWrapper> createGroceryListWrappers(Multimap<String, GroceryItemBreakdownWrapper> groceryItemMap) {
        List<GroceryItemWrapper> groceryItemWrapperList = new ArrayList<>();
        for (String s : groceryItemMap.asMap().keySet()) {
            ArrayList<GroceryItemBreakdownWrapper> breakdownWrappers = new ArrayList<>(groceryItemMap.get(s));
            int totalQuantity = 0;
            for (GroceryItemBreakdownWrapper breakdownWrapper : breakdownWrappers) {
                totalQuantity += breakdownWrapper.getQuantity();
            }
            String[] split = s.split(JOINER);
            GroceryItemWrapper groceryItemWrapper = GroceryItemWrapper.builder()
                    .ingredientName(split[0])
                    .totalQuantity(totalQuantity)
                    .quantityUnit(QuantityUnit.findBySymbol(split[1]))
                    .checked(Boolean.parseBoolean(split[2]))
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
                totalQuantity += groceryItemBreakdownWrapper.getQuantity();
            }
            groceryItemWrapper.setTotalQuantity(totalQuantity);
        }
        return groceryItemWrapperList;
    }

    public static void checkGroceryListItem(Context context, GroceryItemWrapper itemWrapper, boolean isChecked) {
        for (GroceryItemBreakdownWrapper groceryItemBreakdownWrapper : itemWrapper.getBreakdownWrappers()) {
            getDatabase(context).groceryListItemDAO().updateStatus(groceryItemBreakdownWrapper.getId(), isChecked);
        }

    }

    public void addManualGroceryItem(Context context, Date date, String name, int quantity, QuantityUnit quantityUnit) {
        //save manual item from above details
        long recipeId = -1;
        if (getDatabase(context).recipeDAO().getRecipeGivenName(MANUALLY_ADDED) != null) {
            recipeId = getDatabase(context).recipeDAO().getRecipeGivenName(MANUALLY_ADDED).getRecipeId();
        } else {
            Recipe recipe = new Recipe();
            recipe.setRecipeName(MANUALLY_ADDED);
            recipeId = getDatabase(context).recipeDAO().addRecipe(recipe);
        }

        Ingredient ingredient = new Ingredient();
        ingredient.setIngredientName(name);
        ingredient.setQuantity(quantity);
        ingredient.setQuantityUnit(quantityUnit);
        ingredient.setRecipeId(recipeId);
        long ingredientId = getDatabase(context).ingredientDAO().addIngredient(ingredient);

        GroceryListItem groceryListItem = new GroceryListItem();
        groceryListItem.setGroceryListItemName(name);
        groceryListItem.setDate(date);
        groceryListItem.setManual(true);
        groceryListItem.setIngredientId(ingredientId);
        groceryListItem.setStatus(false);
        getDatabase(context).groceryListItemDAO().addGroceryItem(groceryListItem);


    }

    public List<String> loadAllIngredientNames(Context context) {
        List<Ingredient> ingredients = getDatabase(context).ingredientDAO().loadAllIngredients();
        Set<String> result = new HashSet<>();
        if (ingredients != null) {
            for (Ingredient ingredient : ingredients) {
                result.add(ingredient.getIngredientName());
            }
        }
        return new ArrayList<>(result);
    }


}
