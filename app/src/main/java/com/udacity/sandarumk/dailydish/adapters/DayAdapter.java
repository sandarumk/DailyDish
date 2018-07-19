package com.udacity.sandarumk.dailydish.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.udacity.sandarumk.dailydish.R;
import com.udacity.sandarumk.dailydish.datamodel.MealTime;
import com.udacity.sandarumk.dailydish.datamodel.Recipe;
import com.udacity.sandarumk.dailydish.datawrappers.DayWrapper;

import java.text.SimpleDateFormat;
import java.util.List;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder> {

    private SimpleDateFormat sdf = new SimpleDateFormat("d\nMMMM\nEEEE");

    private List<DayWrapper> mDataset;
    private LayoutInflater inflater;

    private AddScheduleListener addScheduleListener;

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
        public View buttonAddBreakfast;
        public View buttonAddLunch;
        public View buttonAddDinner;

        public ViewHolder(CardView v) {
            super(v);
            mCardView = v;
            mDateTextView = mCardView.findViewById(R.id.text_date);
            flexboxBreakfast = mCardView.findViewById(R.id.flexbox_breakfast);
            flexboxLunch = mCardView.findViewById(R.id.flexbox_lunch);
            flexboxDinner = mCardView.findViewById(R.id.flexbox_dinner);
            buttonAddBreakfast = mCardView.findViewById(R.id.text_breakfast);
            buttonAddLunch = mCardView.findViewById(R.id.text_lunch);
            buttonAddDinner = mCardView.findViewById(R.id.text_dinner);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DayAdapter(List<DayWrapper> myDataset, AddScheduleListener addScheduleListener) {
        mDataset = myDataset;
        this.addScheduleListener = addScheduleListener;
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
        final DayWrapper dayWrapper = mDataset.get(position);
        holder.mDateTextView.setText(sdf.format(dayWrapper.getDate()));
        holder.flexboxBreakfast.removeAllViews();
        final MealTime[] mealTimes = {MealTime.BREAKFAST, MealTime.LUNCH, MealTime.DINNER};
        ViewGroup[] dayViewContainers = {holder.flexboxBreakfast, holder.flexboxLunch, holder.flexboxDinner};
        for (int i = 0; i < mealTimes.length; i++) {
            dayViewContainers[i].removeAllViews();
            List<Recipe> recipeList = dayWrapper.getSchedule().get(mealTimes[i]);
            if (recipeList != null) {
                for (Recipe recipes : recipeList) {
                    TextView chip = createChip(recipes.getRecipeName());
                    dayViewContainers[i].addView(chip);
                    int dp = 2;
                    ((FlexboxLayout.LayoutParams) chip.getLayoutParams()).setMargins(0, 0, 10 * dp, 10 * dp);
                }
            }
        }
        if (addScheduleListener != null) {
            View[] buttonContainers = {holder.buttonAddBreakfast, holder.buttonAddLunch, holder.buttonAddDinner};
            for (int i = 0; i < mealTimes.length; i++) {
                final int finalI = i;
                buttonContainers[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addScheduleListener.onAddSchedule(dayWrapper, mealTimes[finalI]);
                    }
                });
            }
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
        return mDataset.size();
    }

    public interface AddScheduleListener {
        void onAddSchedule(DayWrapper dayWrapper, MealTime mealTime);
    }
}
