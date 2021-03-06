package com.udacity.sandarumk.dailydish.adapters;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.udacity.sandarumk.dailydish.R;
import com.udacity.sandarumk.dailydish.datawrappers.GroceryItemWrapper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
                .inflate(R.layout.grocery_list_item_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        GroceryItemWrapper groceryItemWrapper = mValues.get(position);
        holder.mItem = groceryItemWrapper;
        holder.mIdView.setText(groceryItemWrapper.getIngredientName());
        holder.mContentView.setText(groceryItemWrapper.quantityText());
        holder.mCheckBox.setChecked(groceryItemWrapper.isChecked());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!holder.mCheckBox.isChecked()) {
                holder.mIdView.setTextAppearance(R.style.AppTheme_TextPrimary);
                holder.mContentView.setTextAppearance(R.style.AppTheme_TextPrimary);
            } else {
                holder.mIdView.setTextAppearance(R.style.AppTheme_TextSecondary);
                holder.mContentView.setTextAppearance(R.style.AppTheme_TextSecondary);
            }
        }

        if (null != mListener) {
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onListFragmentInteraction(holder.mItem);

                }
            });
//            holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    mListener.onListItemCheck(holder.mItem, isChecked);
//                }
//            });
            holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onListItemCheck(holder.mItem, ((CheckBox) v).isChecked());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        @BindView(R.id.text_grocery_item_name) public TextView mIdView;
        @BindView(R.id.text_grocery_item_quantity) public TextView mContentView;
        @BindView(R.id.check_grocery_item) public CheckBox mCheckBox;
        public GroceryItemWrapper mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(GroceryItemWrapper item);

        void onListItemCheck(GroceryItemWrapper item, boolean newStatus);
    }
}
