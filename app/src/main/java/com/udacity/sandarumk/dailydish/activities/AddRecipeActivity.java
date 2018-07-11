package com.udacity.sandarumk.dailydish.activities;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.udacity.sandarumk.dailydish.R;
import com.udacity.sandarumk.dailydish.datamodel.QuantityUnit;

public class AddRecipeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);


        //add sample ingredient
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View ingredientView = layoutInflater.inflate(R.layout.ingredient_layout, (ViewGroup) findViewById(R.id.ingredients_layout));
        AppCompatSpinner unitSpinner = ingredientView.findViewById(R.id.item_unit);
        unitSpinner.setAdapter(new ArrayAdapter<QuantityUnit>(this,android.R.layout.simple_list_item_1,QuantityUnit.values()));

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
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.menu_ok:
                updateRecipe();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateRecipe() {
        //TODO save recipe as new one or update current
        finish();
    }
}
