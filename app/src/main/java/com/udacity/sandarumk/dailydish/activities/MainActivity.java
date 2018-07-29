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

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.udacity.sandarumk.dailydish.R;
import com.udacity.sandarumk.dailydish.fragments.GroceryListItemFragment;
import com.udacity.sandarumk.dailydish.fragments.RecipeListFragment;
import com.udacity.sandarumk.dailydish.fragments.SettingsFragment;
import com.udacity.sandarumk.dailydish.fragments.ThisWeekFragment;
import com.udacity.sandarumk.dailydish.fragments.TimeChangeFragment;
import com.udacity.sandarumk.dailydish.util.DateUtil;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements TimeChangeFragment.OnDateChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String STATE_KEY_FROM = "fromDate";
    private static final String STATE_KEY_TO = "toDate";
    private static final String STATE_KEY_SELECTION = "selection";
    private DrawerLayout mDrawerLayout;
    private FirebaseAnalytics mFirebaseAnalytics;

    private Date from;
    private Date to;
    private int selectedOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, getApplicationContext().getString(R.string.ad_app_api_key));
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

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
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        openSelectedOption(menuItem.getItemId());

                        return true;
                    }
                });

        if (savedInstanceState == null) {
            Pair<Date, Date> dateDatePair = DateUtil.geCurrentWeekStartEnd(this);
            from = dateDatePair.first;
            to = dateDatePair.second;
            selectedOption = R.id.this_week;
        } else {
            from = (Date) savedInstanceState.getSerializable(STATE_KEY_FROM);
            to = (Date) savedInstanceState.getSerializable(STATE_KEY_TO);
            selectedOption = savedInstanceState.getInt(STATE_KEY_SELECTION);
        }

        openSelectedOption(selectedOption);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_KEY_FROM, from);
        outState.putSerializable(STATE_KEY_TO, to);
        outState.putInt(STATE_KEY_SELECTION, selectedOption);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAnalytics.setCurrentScreen(this, TAG, TAG);
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
        selectedOption = itemID;
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
                setTitle(R.string.recipes);
                newFragment = RecipeListFragment.newInstance(1);
                break;
            case R.id.grocery_list:
                setTitle(R.string.grocery_list);
                newFragment = GroceryListItemFragment.newInstance(1, from, to);
                break;
            default:
                newFragment = new Fragment();
                break;
        }

        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.ITEM_NAME, newFragment.getClass().getSimpleName());
        FirebaseAnalytics.getInstance(this).logEvent("navigation_drawer_select", params);

        if (newFragment != null) {
            transaction.replace(R.id.content_frame, newFragment);
            String backStackname = getTitle().toString();
            transaction.addToBackStack(backStackname);
            transaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        }
    }

    @Override
    public void onDateChange(Date from, Date to) {
        this.from = from;
        this.to = to;
    }
}
