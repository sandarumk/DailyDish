package com.udacity.sandarumk.dailydish.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.udacity.sandarumk.dailydish.R;
import com.udacity.sandarumk.dailydish.activities.AddRecipeActivity;
import com.udacity.sandarumk.dailydish.adapters.RecipeListAdapter;
import com.udacity.sandarumk.dailydish.datamodel.Recipe;
import com.udacity.sandarumk.dailydish.datawrappers.RecipeWrapper;
import com.udacity.sandarumk.dailydish.util.DataProvider;
import com.udacity.sandarumk.dailydish.viewmodels.RecipeListViewModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class RecipeListFragment extends Fragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final int REQUEST_CODE = 1000;
    private static final String STATE_KEY_RECIPES = "allRecipes";
    private static final String STATE_KEY_LAST_SEARCH = "lastSearch";

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private TextView textMsg;

    private ArrayList<Recipe> allRecipes;
    private String lastSearch;

    private InterstitialAd mInterstitialAd;
    private RecipeListViewModel viewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RecipeListFragment newInstance(int columnCount) {
        RecipeListFragment fragment = new RecipeListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        setHasOptionsMenu(true);

        mInterstitialAd = new InterstitialAd(this.getContext());
//        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.setAdUnitId(getString(R.string.ad_interstitial_api_key));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private void selectRecipe(Recipe item) {
        new RecipeDetailLoadTask(this).execute(item.getRecipeId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        recyclerView = view.findViewById(R.id.recipe_list);
        textMsg = view.findViewById(R.id.text_msg);
        // Set the adapter
        Context context = view.getContext();
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRecipeDetailActivity(null);
            }
        });

        if (savedInstanceState != null) {
            lastSearch = savedInstanceState.getString(STATE_KEY_LAST_SEARCH);
            allRecipes = (ArrayList<Recipe>) savedInstanceState.getSerializable(STATE_KEY_RECIPES);
            showAllRecipes(allRecipes);
        } else {
            loadRecipes();
        }

        viewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);
        viewModel.getRecipeList().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                showAllRecipes(recipes);
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_KEY_RECIPES, allRecipes);
        outState.putSerializable(STATE_KEY_LAST_SEARCH, lastSearch);
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseAnalytics.getInstance(this.getContext()).setCurrentScreen(this.getActivity(), "RecipeList", RecipeListFragment.class.getSimpleName());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_recipe_list, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search");
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void startRecipeDetailActivity(RecipeWrapper recipeWrapper) {
        Intent intent = new Intent(getContext(), AddRecipeActivity.class);
        if (recipeWrapper != null) {
            intent.putExtra(AddRecipeActivity.INTENT_EXTRA_RECIPE, recipeWrapper);
        }
        if (lastSearch != null) {
            intent.putExtra(AddRecipeActivity.INTENT_EXTRA_RECIPE_NAME, lastSearch);
        }
        startActivityForResult(intent, REQUEST_CODE);
    }


    private void loadRecipes() {
//        new RecipeLoadTask(this).execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            loadRecipes();
        }
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RecipeListFragment.OnListFragmentInteractionListener) {
            mListener = (RecipeListFragment.OnListFragmentInteractionListener) context;
        } else {
            mListener = new OnListFragmentInteractionListener() {
                @Override
                public void onListFragmentInteraction(Recipe item) {
                    selectRecipe(item);
                }
            };
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onQueryTextSubmit(String newText) {
        if (newText == null || newText.trim().isEmpty()) {
            updateList(allRecipes, true);
            return false;
        }
        updateList(filterRecipes(newText), true);
        lastSearch = newText;
        return false;
    }

    @NonNull
    private List<Recipe> filterRecipes(String newText) {
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.SEARCH_TERM, newText);
        FirebaseAnalytics.getInstance(this.getContext()).logEvent("recipe_search", params);

        List<Recipe> filtered = new ArrayList<>();
        for (Recipe recipe : allRecipes) {
            if (recipe.getRecipeName().toLowerCase().contains(newText.toLowerCase())) {
                filtered.add(recipe);
            }
        }
        return filtered;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return this.onQueryTextSubmit(newText);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem menuItem) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
        return true;
    }

    private void updateList(List<Recipe> result, boolean searchResult) {
        this.recyclerView.setAdapter(new RecipeListAdapter(result, this.mListener));
        if (result == null || result.isEmpty()) {
            textMsg.setVisibility(View.VISIBLE);
            if (searchResult) {
                String string = getContext().getString(R.string.msg_add_named_recipe);
                textMsg.setText(string.replace("#", lastSearch));
            } else {
                textMsg.setText(R.string.msg_add_recipe);
            }
        } else {
            textMsg.setVisibility(View.GONE);
        }
    }

    private void showAllRecipes(List<Recipe> result) {
        this.allRecipes = new ArrayList<>(result);
        this.updateList(result, false);
    }


    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Recipe item);
    }

    static class RecipeDetailLoadTask extends AsyncTask<Long, Void, RecipeWrapper> {

        private WeakReference<RecipeListFragment> fragmentReference;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //show progress
        }

        public RecipeDetailLoadTask(RecipeListFragment fragment) {
            fragmentReference = new WeakReference<>(fragment);
        }

        @Override
        protected RecipeWrapper doInBackground(Long... params) {
            return DataProvider.loadRecipe(fragmentReference.get().getContext(), params[0]);
        }

        @Override
        protected void onPostExecute(RecipeWrapper result) {
            super.onPostExecute(result);
            //hide progress
            if (result != null && fragmentReference.get() != null) {
                fragmentReference.get().startRecipeDetailActivity(result);
            }
        }
    }
}
