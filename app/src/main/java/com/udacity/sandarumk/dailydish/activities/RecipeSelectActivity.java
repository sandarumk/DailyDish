package com.udacity.sandarumk.dailydish.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.udacity.sandarumk.dailydish.R;
import com.udacity.sandarumk.dailydish.datamodel.Recipe;
import com.udacity.sandarumk.dailydish.fragments.RecipeListFragment;

public class RecipeSelectActivity extends AppCompatActivity implements RecipeListFragment.OnListFragmentInteractionListener {

    public static final String EXTRA_SELECTED_RECIPE = "EXTRA_SELECTED_RECIPE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_select);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, RecipeListFragment.newInstance(1))
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseAnalytics.getInstance(this).setCurrentScreen(this, "SelectRecipe", RecipeSelectActivity.class.getSimpleName());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListFragmentInteraction(Recipe item) {
        Intent data = new Intent();
        data.putExtra(EXTRA_SELECTED_RECIPE, item);
        this.setResult(RESULT_OK, data);
        this.finish();
    }
}
