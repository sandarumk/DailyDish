package com.udacity.sandarumk.dailydish.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.sandarumk.dailydish.R;
import com.udacity.sandarumk.dailydish.adapters.GroceryListItemRecyclerViewAdapter;
import com.udacity.sandarumk.dailydish.datamodel.QuantityUnit;
import com.udacity.sandarumk.dailydish.datawrappers.GroceryItemBreakdownWrapper;
import com.udacity.sandarumk.dailydish.datawrappers.GroceryItemWrapper;
import com.udacity.sandarumk.dailydish.util.DataProvider;
import com.udacity.sandarumk.dailydish.util.DateUtil;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class GroceryListItemFragment extends Fragment implements GroceryListItemRecyclerViewAdapter.OnListFragmentInteractionListener {

    private static final String TAG = GroceryListItemFragment.class.getSimpleName();

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";

    private static final String ARG_FROM = "fromDate";
    private static final String ARG_TO = "toDate";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView recyclerView;

    private Date from;
    private Date to;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GroceryListItemFragment() {
    }

    @SuppressWarnings("unused")
    public static GroceryListItemFragment newInstance(int columnCount, Date from, Date to) {
        GroceryListItemFragment fragment = new GroceryListItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putSerializable(ARG_FROM, from);
        args.putSerializable(ARG_TO, to);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            from = (Date) getArguments().getSerializable(ARG_FROM);
            to = (Date) getArguments().getSerializable(ARG_TO);
        }
        if (from == null || to == null) {
            Pair<Date, Date> startEnd = DateUtil.getWeekStartEnd();
            from = startEnd.first;
            to = startEnd.second;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_grocerylistitem_list, container, false);

        recyclerView = view.findViewById(R.id.list);

        // Set the adapter
        if (recyclerView != null) {
            Context context = recyclerView.getContext();
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
        }


        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddItem();
            }
        });

        startLoad();
        return view;
    }

    private void startLoad() {
        new GroceryLoadTask(this).execute(from, to);
    }

    private void updateGroceryList(List<GroceryItemWrapper> list) {
        recyclerView.setAdapter(new GroceryListItemRecyclerViewAdapter(list, this));
    }

    private void refreshGroceryList() {
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    private void addNewItem(String itemName, int quantity, QuantityUnit quantityUnit) {
        Date date = new Date();
        if (!(from.before(date) && to.after(date))) {
            date = from;
        }
        new AddManualGroceryItemTask(this).execute(date, itemName, quantity, quantityUnit);
    }

    private void showAddItem() {
        LayoutInflater layoutInflater = LayoutInflater.from(this.getContext());
        final View parent = layoutInflater.inflate(R.layout.grocery_add_layout, null);
        final EditText editItemName = parent.findViewById(R.id.edit_grocery_item_name);
        final EditText editItemQuantity = parent.findViewById(R.id.edit_grocery_item_quantity);
        final Spinner spinnerUnit = parent.findViewById(R.id.spinner_grocery_item_unit);
        spinnerUnit.setAdapter(new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_1, QuantityUnit.values()));
        spinnerUnit.setSelection(0);


        new AlertDialog.Builder(this.getContext())
                .setTitle("Add New Item")
                .setView(parent)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean hasError = false;
                        if (editItemName.getText().toString().isEmpty()) {
                            editItemName.setError("Invalid item name");
                            hasError = true;
                        } else if (editItemQuantity.toString().isEmpty()) {
                            editItemName.setError("Invalid quantity");
                            hasError = true;
                        }

                        QuantityUnit quantityUnit = QuantityUnit.findBySymbol(spinnerUnit.getSelectedItem().toString());

                        if (!hasError) {
                            addNewItem(editItemName.getText().toString(), Integer.parseInt(editItemQuantity.getText().toString()), quantityUnit);
                            dialog.dismiss();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            //  throw new RuntimeException(context.toString()
            //         + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListFragmentInteraction(GroceryItemWrapper item) {
        //show item breakdown
        List<GroceryItemBreakdownWrapper> breakdownWrappers = item.getBreakdownWrappers();
        if (breakdownWrappers != null && !breakdownWrappers.isEmpty()) {
            LayoutInflater layoutInflater = LayoutInflater.from(this.getContext());
            View parent = layoutInflater.inflate(R.layout.grocery_breakdown_layout, null);
            ViewGroup containerBreakdown = parent.findViewById(R.id.container_breakdown);
            ViewGroup containerTotal = parent.findViewById(R.id.container_total);
            ((TextView) containerTotal.findViewById(R.id.text_quantity)).setText(item.quantityText());

            Collections.sort(breakdownWrappers, new Comparator<GroceryItemBreakdownWrapper>() {
                @Override
                public int compare(GroceryItemBreakdownWrapper o1, GroceryItemBreakdownWrapper o2) {
                    return (int) (o1.getDate().getTime() - o2.getDate().getTime());
                }
            });

            //TODO append 21st, or 30th to the formatter
            SimpleDateFormat sdf = new SimpleDateFormat("dd");

            for (GroceryItemBreakdownWrapper breakdownWrapper : breakdownWrappers) {
                View row = layoutInflater.inflate(R.layout.grocery_breakdown_layout_row, containerBreakdown, false);
                ((TextView) row.findViewById(R.id.text_date)).setText(sdf.format(breakdownWrapper.getDate()));
                ((TextView) row.findViewById(R.id.text_recipe_name)).setText(breakdownWrapper.getRecipeName());
                ((TextView) row.findViewById(R.id.text_quantity)).setText(breakdownWrapper.quantityText());
                containerBreakdown.addView(row);
            }

            new AlertDialog.Builder(this.getContext())
                    .setTitle(item.getIngredientName())
                    .setView(parent)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onListItemCheck(GroceryItemWrapper item, boolean newStatus) {
        //update new status
        item.setChecked(newStatus);
        new GroceryItemChangeStatusTask(this).execute(item, newStatus);
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
        void onListFragmentInteraction(GroceryItemWrapper item);
    }

    static class GroceryLoadTask extends AsyncTask<Date, Void, List<GroceryItemWrapper>> {

        private WeakReference<GroceryListItemFragment> fragmentReference;

        public GroceryLoadTask(GroceryListItemFragment fragment) {
            fragmentReference = new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //show progress
        }

        @Override
        protected List<GroceryItemWrapper> doInBackground(Date... params) {
            Date from = params[0];
            Date to = params[1];
            List<GroceryItemWrapper> groceryItemWrapperList = DataProvider.loadGroceryList(fragmentReference.get().getContext(), from, to);
            if (groceryItemWrapperList != null) {
                Collections.sort(groceryItemWrapperList, new Comparator<GroceryItemWrapper>() {
                    @Override
                    public int compare(GroceryItemWrapper o1, GroceryItemWrapper o2) {
                        if (o1.isChecked() == o2.isChecked()) {
                            return o1.getIngredientName().compareTo(o2.getIngredientName());
                        } else if (o1.isChecked()) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                });
            }
            return groceryItemWrapperList;
        }

        @Override
        protected void onPostExecute(List<GroceryItemWrapper> result) {
            super.onPostExecute(result);
            //hide progress
            if (result != null && fragmentReference.get() != null) {
                fragmentReference.get().updateGroceryList(result);
            }
        }
    }

    static class GroceryItemChangeStatusTask extends AsyncTask<Object, Void, Boolean> {

        private WeakReference<GroceryListItemFragment> fragmentReference;

        public GroceryItemChangeStatusTask(GroceryListItemFragment fragment) {
            fragmentReference = new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //show progress
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            try {
                DataProvider.checkGroceryListItem(fragmentReference.get().getContext(), (GroceryItemWrapper) params[0], (boolean) params[1]);
            } catch (Exception e) {
                Log.e(TAG, "Error in updating grocery item status", e);
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            //hide progress
            if (fragmentReference.get() != null) {
                if (result != null) {
                    fragmentReference.get().refreshGroceryList();
                } else {
                    Toast.makeText(fragmentReference.get().getContext(), "Error updating grocery item status", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    static class AddManualGroceryItemTask extends AsyncTask<Object, Void, Boolean> {

        private WeakReference<GroceryListItemFragment> fragmentReference;

        public AddManualGroceryItemTask(GroceryListItemFragment fragment) {
            fragmentReference = new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //show progress
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            try {
                DataProvider.addManualGroceryItem(fragmentReference.get().getContext(), (Date) params[0], (String) params[1], (int) params[2], (QuantityUnit) params[3]);
            } catch (Exception e) {
                Log.e(TAG, "Error in adding manual grocery item", e);
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            //hide progress
            if (fragmentReference.get() != null) {
                if (result != null) {
                    fragmentReference.get().startLoad();
                } else {
                    Toast.makeText(fragmentReference.get().getContext(), "Error adding manual grocery item", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
