package com.udacity.sandarumk.dailydish.datawrappers;

import com.udacity.sandarumk.dailydish.datamodel.Ingredient;
import com.udacity.sandarumk.dailydish.datamodel.Recipe;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecipeWrapper {

    Recipe recipe;
    List<Ingredient> recipeIngridientList;
}
