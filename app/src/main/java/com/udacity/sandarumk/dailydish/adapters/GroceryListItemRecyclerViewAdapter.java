package com.udacity.sandarumk.dailydish.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.udacity.sandarumk.dailydish.R;
import com.udacity.sandarumk.dailydish.datawrappers.GroceryItemWrapper;
import com.udacity.sandarumk.dailydish.fragments.GroceryListItemFragment.OnListFragmentInteractionListener;
import com.udacity.sandarumk.dailydish.fragments.dummy.RecipeContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class GroceryListItemRecyclerViewAdapter extends RecyclerView.Adapter<GroceryListItemRecyclerViewAdapter.ViewHolder> {

    private final List<GroceryItemWrapper> mValues;
    private final OnListFragmentInteractionListener mListener;

    public GroceryListItemRecyclerViewAdapter(List<GroceryItemWrapper> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_grocerylistitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        GroceryItemWrapper groceryItemWrapper = mValues.get(position);
        holder.mItem = groceryItemWrapper;
        holder.mIdView.setText(groceryItemWrapper.getIngredientName());
        holder.mContentView.setText(groceryItemWrapper.getTotalQuantity() + " " + groceryItemWrapper.getQuantityUnit().getSymbol());
        holder.mCheckBox.setChecked(groceryItemWrapper.isChecked());

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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final CheckBox mCheckBox;
        public GroceryItemWrapper mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.text_grocery_item_name);
            mContentView = (TextView) view.findViewById(R.id.text_grocery_item_quantity);
            mCheckBox = view.findViewById(R.id.check_grocery_item);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
