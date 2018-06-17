package com.udacity.sandarumk.dailydish.datamodel.typeconverters;

import android.arch.persistence.room.TypeConverter;

import com.udacity.sandarumk.dailydish.datamodel.MealTime;

public class MealTimeTypeConverter {

    @TypeConverter
    public static MealTime toMealTime(int value) {
        for(MealTime mealTime: MealTime.values()){
            if(mealTime.getMealTime()==value) {
                return mealTime;
            }
        }
        return null;
    }

    @TypeConverter
    public static int toMealTimeInt(MealTime mealTime) {
        return mealTime.getMealTime();
    }
}
