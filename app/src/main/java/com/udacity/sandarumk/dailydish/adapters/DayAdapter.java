package com.udacity.sandarumk.dailydish.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.udacity.sandarumk.dailydish.R;
import com.udacity.sandarumk.dailydish.TempObject;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder> {

    private TempObject[] mDataset;
    private LayoutInflater inflater;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView mCardView;
        public TextView mDateTextView;
        public FlexboxLayout flexboxBreakfast;
        public FlexboxLayout flexboxLunch;
        public FlexboxLayout flexboxDinner;

        public ViewHolder(CardView v) {
            super(v);
            mCardView = v;
            mDateTextView = mCardView.findViewById(R.id.text_date);
            flexboxBreakfast = mCardView.findViewById(R.id.flexbox_breakfast);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DayAdapter(TempObject[] myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                    int viewType) {
        inflater = LayoutInflater.from(parent.getContext());
        CardView cardView = (CardView) inflater
                .inflate(R.layout.this_week_card_layout, parent, false);
        ViewHolder vh = new ViewHolder(cardView);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //holder.mTextView.setText("text");
        TempObject tempObject = mDataset[position];
        holder.mDateTextView.setText(tempObject.getDate());
        holder.flexboxBreakfast.removeAllViews();
        for (String s : tempObject.getBreakfast()) {
            TextView chip = createChip(s);
            holder.flexboxBreakfast.addView(chip);
            int dp = 2;
            ((FlexboxLayout.LayoutParams)chip.getLayoutParams()).setMargins(0, 0, 10 * dp, 10 * dp);
        }


    }

    private TextView createChip(String text) {
        TextView textView = (TextView) inflater.inflate(R.layout.chip_layout, null);
        textView.setText(text);
        return textView;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
