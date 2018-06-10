package com.udacity.sandarumk.dailydish.datamodel;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Ingredient.class, Recipe.class, MealSchedule.class, GroceryListItem.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

}
