package com.udacity.sandarumk.dailydish.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.udacity.sandarumk.dailydish.R;
import com.udacity.sandarumk.dailydish.fragments.GroceryListItemFragment;
import com.udacity.sandarumk.dailydish.fragments.RecipeListFragment;
import com.udacity.sandarumk.dailydish.fragments.SettingsFragment;
import com.udacity.sandarumk.dailydish.fragments.ThisWeekFragment;
import com.udacity.sandarumk.dailydish.util.DateUtil;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    private Date from;
    private Date to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_nav_drawer);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        openSelectedOption(menuItem.getItemId());

                        return true;
                    }
                });

        Pair<Date, Date> dateDatePair = DateUtil.getWeekStartEnd();
        from = dateDatePair.first;
        to = dateDatePair.second;

        openSelectedOption(R.id.this_week);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openSelectedOption(int itemID) {
        Fragment newFragment = null;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (itemID) {
            case R.id.settings:
                setTitle(R.string.settings);
                newFragment = new SettingsFragment();
                break;
            case R.id.this_week:
                setTitle(R.string.this_week);
                newFragment = ThisWeekFragment.newInstance(from, to);
                break;
            case R.id.recipes:
                setTitle("Recipes");
                newFragment = RecipeListFragment.newInstance(1);
                break;
            case R.id.grocery_list:
                setTitle("Grocery List");
                newFragment = GroceryListItemFragment.newInstance(1, from, to);
                break;
            default:
                newFragment = new Fragment();
                break;
        }
        if (newFragment != null) {
            transaction.replace(R.id.content_frame, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}
