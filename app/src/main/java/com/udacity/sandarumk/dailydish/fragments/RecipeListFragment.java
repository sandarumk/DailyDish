package com.udacity.sandarumk.dailydish.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.sandarumk.dailydish.R;
import com.udacity.sandarumk.dailydish.activities.AddRecipeActivity;
import com.udacity.sandarumk.dailydish.adapters.RecipeListAdapter;
import com.udacity.sandarumk.dailydish.datamodel.Recipe;
import com.udacity.sandarumk.dailydish.datawrappers.RecipeWrapper;
import com.udacity.sandarumk.dailydish.util.DataProvider;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class RecipeListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final int REQUEST_CODE = 1000;
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView recyclerView;


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

        mListener = new OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(Recipe item) {
                selectRecipe(item);
            }
        };
    }

    private void selectRecipe(Recipe item) {
        new RecipeDetailLoadTask(this).execute(item.getRecipeId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        recyclerView = view.findViewById(R.id.recipe_list);
        // Set the adapter
        Context context = view.getContext();
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
//        recyclerView.setAdapter(new RecipeListAdapter(RecipeContent.ITEMS, mListener));
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

        loadRecipes();

        return view;
    }

    private void startRecipeDetailActivity(RecipeWrapper recipeWrapper) {
        Intent intent = new Intent(getContext(), AddRecipeActivity.class);
        if (recipeWrapper != null) {
            intent.putExtra(AddRecipeActivity.INTENT_EXTRA_RECIPE, recipeWrapper);
        }
        startActivityForResult(intent, REQUEST_CODE);
    }


    private void loadRecipes() {
        new RecipeLoadTask(this).execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            loadRecipes();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Recipe item);
    }

    static class RecipeLoadTask extends AsyncTask<Void, Void, List<Recipe>> {

        private WeakReference<RecipeListFragment> fragmentReference;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //show progress
        }

        public RecipeLoadTask(RecipeListFragment fragment) {
            fragmentReference = new WeakReference<>(fragment);
        }

        @Override
        protected List<Recipe> doInBackground(Void... params) {
            return DataProvider.loadAllRecipes(fragmentReference.get().getContext());
        }

        @Override
        protected void onPostExecute(List<Recipe> result) {
            super.onPostExecute(result);
            //hide progress
            if (result != null) {
                RecipeListFragment recipeListFragment = fragmentReference.get();
                if (recipeListFragment != null) {
                    recipeListFragment.recyclerView.setAdapter(new RecipeListAdapter(result, recipeListFragment.mListener));
                }
            }
        }
    }

    static class RecipeDetailLoadTask extends AsyncTask<Integer, Void, RecipeWrapper> {

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
        protected RecipeWrapper doInBackground(Integer... params) {
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
