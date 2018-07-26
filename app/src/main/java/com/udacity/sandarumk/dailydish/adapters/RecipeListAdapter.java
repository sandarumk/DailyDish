package com.udacity.sandarumk.dailydish.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.sandarumk.dailydish.R;
import com.udacity.sandarumk.dailydish.datamodel.Recipe;
import com.udacity.sandarumk.dailydish.fragments.RecipeListFragment.OnListFragmentInteractionListener;

import java.util.List;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.ViewHolder> {

    private final List<Recipe> mValues;
    private final OnListFragmentInteractionListener mListener;

    public RecipeListAdapter(List<Recipe> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getRecipeName());
        holder.mContentView.setText(mValues.get(position).getRecipeNotes());
        String mealTimeText = "";
        int mealTime = mValues.get(position).getMealTime();
        if ((mealTime / 100) == 1) {
            mealTimeText += "B|";
        }
        if ((mealTime / 10) % 10 == 1) {
            mealTimeText += "L|";
        }
        if (mealTime % 2 == 1) {
            mealTimeText += "D|";
        }
        if (mealTimeText.isEmpty()) {
            mealTimeText = "--";
        }
        if (mealTimeText.endsWith("|")) {
            mealTimeText = mealTimeText.substring(0, mealTimeText.length() - 1);
        }
        holder.mIconView.setText(mealTimeText);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public List<Recipe> getValues() {
        return mValues;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mIconView;
        public Recipe mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.recipe_name);
            mContentView = view.findViewById(R.id.recipe_ingredients);
            mIconView = view.findViewById(R.id.meal_time);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
