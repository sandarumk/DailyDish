package com.udacity.sandarumk.dailydish.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.udacity.sandarumk.dailydish.datamodel.Recipe;
import com.udacity.sandarumk.dailydish.util.DataProvider;

import java.util.List;

public class RecipeListViewModel extends AndroidViewModel {

    private final LiveData<List<Recipe>> recipeList;

    public RecipeListViewModel(@NonNull Application application) {
        super(application);
        recipeList = DataProvider.loadLiveRecipes(application.getApplicationContext());
    }

    public LiveData<List<Recipe>> getRecipeList() {
        return recipeList;
    }
}
