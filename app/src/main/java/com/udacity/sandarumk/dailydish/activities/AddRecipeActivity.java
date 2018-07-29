package com.udacity.sandarumk.dailydish.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.udacity.sandarumk.dailydish.R;
import com.udacity.sandarumk.dailydish.datamodel.Ingredient;
import com.udacity.sandarumk.dailydish.datamodel.QuantityUnit;
import com.udacity.sandarumk.dailydish.datamodel.Recipe;
import com.udacity.sandarumk.dailydish.datawrappers.RecipeWrapper;
import com.udacity.sandarumk.dailydish.util.DataProvider;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddRecipeActivity extends AppCompatActivity {

    public static final String INTENT_EXTRA_RECIPE = "recipeWrapper";
    public static final String INTENT_EXTRA_RECIPE_NAME = "newRecipeName";
    public static final String INTENT_EXTRA_RECIPE_ID = "recipeWrapperId";
    private static final String STATE_KEY_RECIPE = "recipe";

    private RecipeWrapper recipeWrapper;
    private List<String> ingredients = new ArrayList<>();

    @BindView(R.id.edit_text_recipe_name)
    EditText editTextRecipeName;
    @BindView(R.id.edit_text_steps)
    EditText editTextSteps;
    @BindView(R.id.edit_text_notes)
    EditText editTextNotes;
    @BindView(R.id.text_ingredients)
    TextView textIngredients;
    @BindView(R.id.ingredients_layout)
    ViewGroup containerIngredients;

    private ArrayAdapter<QuantityUnit> quantityUnitArrayAdapter;
    private ArrayAdapter<String> ingredientAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            recipeWrapper = (RecipeWrapper) savedInstanceState.getSerializable(STATE_KEY_RECIPE);
        } else {
            if (getIntent().hasExtra(INTENT_EXTRA_RECIPE)) {
                recipeWrapper = (RecipeWrapper) getIntent().getSerializableExtra(INTENT_EXTRA_RECIPE);
            } else if (getIntent().hasExtra(INTENT_EXTRA_RECIPE_ID)) {
                new RecipeDetailLoadTask(this).execute(getIntent().getLongExtra(INTENT_EXTRA_RECIPE_ID, 0));
            } else {
                String newRecipeName = "";
                if (getIntent().hasExtra(INTENT_EXTRA_RECIPE_NAME)) {
                    newRecipeName = getIntent().getStringExtra(INTENT_EXTRA_RECIPE_NAME);
                }
                Recipe recipe = new Recipe();
                recipe.setRecipeName(newRecipeName);
                recipeWrapper = RecipeWrapper.builder()
                        .recipe(recipe)
                        .recipeIngredientList(new ArrayList<Ingredient>())
                        .build();
            }
        }

        setContentView(R.layout.activity_add_recipe);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        if (ingredientAdapter == null) {
            ingredientAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, ingredients);
        }
        if (recipeWrapper != null) {
            updateUI();
        }
        new IngredientLoadTask(this).execute();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        updateRecipeWrapper();
        outState.putSerializable(STATE_KEY_RECIPE, recipeWrapper);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_recipe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_ok:
                updateRecipeWrapper();
                startRecipeUpdate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseAnalytics.getInstance(this).setCurrentScreen(this, "Recipe", AddRecipeActivity.class.getSimpleName());
    }

    private void updateRecipeWrapper() {
        Recipe recipe = recipeWrapper.getRecipe();
        recipe.setRecipeName(editTextRecipeName.getText().toString());
        recipe.setRecipeSteps(editTextSteps.getText().toString());
        recipe.setRecipeNotes(editTextNotes.getText().toString());

        List<Ingredient> recipeIngredientList = recipeWrapper.getRecipeIngredientList();

        int childCount = containerIngredients.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View ingredientView = containerIngredients.getChildAt(i);
            Ingredient ingredient;
            if (i < recipeIngredientList.size()) {
                ingredient = recipeIngredientList.get(i);
            } else {
                ingredient = new Ingredient();
                recipeIngredientList.add(ingredient);
            }

            TextView ingredientNameView = ingredientView.findViewById(R.id.item_name);
            TextView ingredientQuantityView = ingredientView.findViewById(R.id.item_size);
            Spinner ingredientUnitView = ingredientView.findViewById(R.id.item_unit);

            String ingredientName = ingredientNameView.getText().toString();
            String ingredientQuantity = ingredientQuantityView.getText().toString();
            String unitSymbol = ingredientUnitView.getSelectedItem().toString();

            if (ingredientName.isEmpty()) {
                ingredientNameView.setError(getApplicationContext().getString(R.string.invalid_ingredient_error));
                return;
            }
            if (ingredientQuantity.isEmpty()) {
                ingredientQuantityView.setError(getApplicationContext().getString(R.string.invalid_ingredient_quantity_error));
                return;
            }
            if (unitSymbol.isEmpty()) {
                Toast.makeText(this, getApplicationContext().getString(R.string.invalid_unit_selected) + ingredientName, Toast.LENGTH_LONG).show();
                return;
            }

            ingredient.setIngredientName(ingredientName);
            ingredient.setQuantity(Integer.parseInt(ingredientQuantity));
            ingredient.setQuantityUnit(QuantityUnit.findBySymbol(unitSymbol));
        }
    }

    private void startRecipeUpdate() {
        {
            Bundle params = new Bundle();
            params.putString(FirebaseAnalytics.Param.ITEM_NAME, recipeWrapper.getRecipe().getRecipeName());
            FirebaseAnalytics.getInstance(this).logEvent("save_recipe", params);
        }

        if (recipeWrapper.getRecipe().getRecipeNotes() != null && !recipeWrapper.getRecipe().getRecipeNotes().isEmpty()) {
            FirebaseAnalytics.getInstance(this).logEvent("save_recipe_note", null);
        }

        RecipeSaveTask asyncTask = new RecipeSaveTask(this);
        asyncTask.execute(recipeWrapper);
    }

    private void updateUI() {
        Recipe recipe = recipeWrapper.getRecipe();
        editTextRecipeName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setTitle(s.toString());
            }
        });
        editTextRecipeName.setText(recipe.getRecipeName());
        editTextNotes.setText(recipe.getRecipeNotes());
        editTextSteps.setText(recipe.getRecipeSteps());

        List<Ingredient> recipeIngridientList = recipeWrapper.getRecipeIngredientList();
        if (recipeIngridientList == null) {
            recipeIngridientList = new ArrayList<>();
            recipeWrapper.setRecipeIngredientList(recipeIngridientList);
        }

        textIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIngredient();
            }
        });

        for (final Ingredient ingredient : recipeIngridientList) {
            final View ingredientView = createIngredientView(containerIngredients, ingredient);
            AutoCompleteTextView editTextIngredientName = ingredientView.findViewById(R.id.item_name);
            editTextIngredientName.setText(ingredient.getIngredientName());

            EditText editTextIngredientSize = ingredientView.findViewById(R.id.item_size);
            editTextIngredientSize.setText(String.valueOf(ingredient.getQuantity()));

            Spinner spinnerIngredientUnit = ingredientView.findViewById(R.id.item_unit);
            int selection = 0;
            QuantityUnit[] values = QuantityUnit.values();
            for (int i = 0; i < values.length; i++) {
                if (values[i] == ingredient.getQuantityUnit()) {
                    selection = i;
                }
            }
            spinnerIngredientUnit.setSelection(selection);
        }

    }

    private void removeIngredient(View ingredientView, Ingredient ingredient) {
        recipeWrapper.getRecipeIngredientList().remove(ingredient);
        containerIngredients.removeView(ingredientView);
    }

    private void addIngredient() {
        Ingredient ingredient = new Ingredient();
        recipeWrapper.getRecipeIngredientList().add(ingredient);
        createIngredientView(containerIngredients, ingredient);
    }

    private View createIngredientView(ViewGroup parentView, final Ingredient ingredient) {
        if (quantityUnitArrayAdapter == null) {
            quantityUnitArrayAdapter = new ArrayAdapter<>(this, R.layout.simple_list_item, QuantityUnit.values());
        }
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View ingredientView = layoutInflater.inflate(R.layout.ingredient_layout, null);
        Spinner unitSpinner = ingredientView.findViewById(R.id.item_unit);
        unitSpinner.setAdapter(quantityUnitArrayAdapter);
        unitSpinner.setSelection(0);

        AutoCompleteTextView editTextIngredientName = ingredientView.findViewById(R.id.item_name);
        editTextIngredientName.setThreshold(1);
        editTextIngredientName.setAdapter(ingredientAdapter);

        parentView.addView(ingredientView);
        ingredientView.findViewById(R.id.item_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeIngredient(ingredientView, ingredient);
            }
        });
        return ingredientView;
    }

    static class RecipeSaveTask extends AsyncTask<RecipeWrapper, Void, Void> {

        private WeakReference<AddRecipeActivity> activityReference;

        public RecipeSaveTask(AddRecipeActivity activity) {
            activityReference = new WeakReference<>(activity);
        }

        @Override
        protected Void doInBackground(RecipeWrapper... wrappers) {
            DataProvider.saveRecipe(activityReference.get(), wrappers[0].getRecipe(), wrappers[0].getRecipeIngredientList());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            AddRecipeActivity addRecipeActivity = activityReference.get();
            if (addRecipeActivity != null) {
                Intent data = new Intent();
                addRecipeActivity.setResult(RESULT_OK, data);
                addRecipeActivity.finish();
            }
        }
    }

    static class RecipeDetailLoadTask extends AsyncTask<Long, Void, RecipeWrapper> {

        private WeakReference<AddRecipeActivity> activityReference;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        public RecipeDetailLoadTask(AddRecipeActivity activity) {
            activityReference = new WeakReference<>(activity);
        }

        @Override
        protected RecipeWrapper doInBackground(Long... params) {
            return DataProvider.loadRecipe(activityReference.get(), params[0]);
        }

        @Override
        protected void onPostExecute(RecipeWrapper result) {
            super.onPostExecute(result);
            if (result != null && activityReference.get() != null) {
                activityReference.get().recipeWrapper = result;
                activityReference.get().updateUI();
            }
        }
    }

    static class IngredientLoadTask extends AsyncTask<Void, Void, List<String>> {

        private WeakReference<AddRecipeActivity> activityReference;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        public IngredientLoadTask(AddRecipeActivity activity) {
            activityReference = new WeakReference<>(activity);
        }

        @Override
        protected List<String> doInBackground(Void... params) {
            return DataProvider.loadAllIngredientNames(activityReference.get());
        }

        @Override
        protected void onPostExecute(List<String> result) {
            super.onPostExecute(result);
            if (result != null && activityReference.get() != null) {
                activityReference.get().ingredientAdapter.clear();
                activityReference.get().ingredientAdapter.addAll(result);
            }
        }
    }
}
