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
import com.udacity.sandarumk.dailydish.util.ExtendedDateFormat;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder> {

    private SimpleDateFormat sdfName = new SimpleDateFormat("EEEE");
    private SimpleDateFormat sdf = new ExtendedDateFormat("dG\nMMMM");

    private List<DayWrapper> mDataset;
    private LayoutInflater inflater;

    private ScheduleEventListener scheduleEventListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView mCardView;
        @BindView(R.id.text_date) public TextView mDateTextView;
        @BindView(R.id.text_date_name) public TextView mDateTextViewName;
        @BindView(R.id.flexbox_breakfast)public FlexboxLayout flexboxBreakfast;
        @BindView(R.id.flexbox_lunch)public FlexboxLayout flexboxLunch;
        @BindView(R.id.flexbox_dinner)public FlexboxLayout flexboxDinner;
        @BindView(R.id.text_breakfast)public View buttonAddBreakfast;
        @BindView(R.id.text_lunch)public View buttonAddLunch;
        @BindView(R.id.text_dinner)public View buttonAddDinner;

        public ViewHolder(CardView v) {
            super(v);
            ButterKnife.bind(this, v);
            mCardView = v;
        }
    }

    public DayAdapter(List<DayWrapper> myDataset, ScheduleEventListener scheduleEventListener) {
        mDataset = myDataset;
        this.scheduleEventListener = scheduleEventListener;
    }

    @Override
    public DayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                    int viewType) {
        inflater = LayoutInflater.from(parent.getContext());
        CardView cardView = (CardView) inflater
                .inflate(R.layout.this_week_card_layout, parent, false);
        ViewHolder vh = new ViewHolder(cardView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final DayWrapper dayWrapper = mDataset.get(position);
        holder.mDateTextView.setText(sdf.format(dayWrapper.getDate()));
        holder.mDateTextViewName.setText(sdfName.format(dayWrapper.getDate()));
        holder.flexboxBreakfast.removeAllViews();
        final MealTime[] mealTimes = {MealTime.BREAKFAST, MealTime.LUNCH, MealTime.DINNER};
        ViewGroup[] dayViewContainers = {holder.flexboxBreakfast, holder.flexboxLunch, holder.flexboxDinner};
        for (int i = 0; i < mealTimes.length; i++) {
            dayViewContainers[i].removeAllViews();
            final MealTime mealTime = mealTimes[i];
            List<Recipe> recipeList = dayWrapper.getSchedule().get(mealTime);
            if (recipeList != null) {
                for (final Recipe recipe : recipeList) {
                    ViewGroup chip = createChip(recipe.getRecipeName());
                    if (scheduleEventListener != null) {
                        chip.findViewById(R.id.btn_remove).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                scheduleEventListener.onDeleteSchedule(position, dayWrapper, mealTime, recipe);
                            }
                        });
                        chip.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                scheduleEventListener.onSelectSchedule(position, dayWrapper, mealTime, recipe);
                            }
                        });
                    }
                    dayViewContainers[i].addView(chip);
                    int dp = 2;
                    ((FlexboxLayout.LayoutParams) chip.getLayoutParams()).setMargins(0, 0, 10 * dp, 10 * dp);
                }
            }
        }
        if (scheduleEventListener != null) {
            View[] buttonContainers = {holder.buttonAddBreakfast, holder.buttonAddLunch, holder.buttonAddDinner};
            for (int i = 0; i < mealTimes.length; i++) {
                final int finalI = i;
                buttonContainers[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        scheduleEventListener.onAddSchedule(position, dayWrapper, mealTimes[finalI]);
                    }
                });
            }
        }
    }

    private ViewGroup createChip(String text) {
        ViewGroup chip = (ViewGroup) inflater.inflate(R.layout.chip_layout, null);
        ((TextView) chip.findViewById(R.id.text_name)).setText(text);
        return chip;
    }

    public void setDataset(List<DayWrapper> dataset) {
        this.mDataset = dataset;
    }

    public List<DayWrapper> getDataset() {
        return mDataset;
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface ScheduleEventListener {
        void onAddSchedule(int position, DayWrapper dayWrapper, MealTime mealTime);

        void onDeleteSchedule(int position, DayWrapper dayWrapper, MealTime mealTime, Recipe recipe);

        void onSelectSchedule(int position, DayWrapper dayWrapper, MealTime mealTime, Recipe recipe);
    }
}
