package com.udacity.sandarumk.dailydish.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.sandarumk.dailydish.R;
import com.udacity.sandarumk.dailydish.TempObject;
import com.udacity.sandarumk.dailydish.adapters.DayAdapter;

public class ThisWeekFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

   // private OnFragmentInteractionListener mListener;

    public ThisWeekFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ThisWeekFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ThisWeekFragment newInstance(String param1, String param2) {
        ThisWeekFragment fragment = new ThisWeekFragment();
        Bundle args = new Bundle();
       // args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_this_week, container, false);

        mRecyclerView = view.findViewById(R.id.my_recycler_view);
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        //String[] myDataset = {"one", "two", "three", "four", "five", "six"};
        String date = "30th June, 2018 - Monday";
        String[] breakfast = {"bread", "jam"};
        String[] lunch = {"rice", "egg"};
        String[] dinner = {"pasta"};
        TempObject objectToPass = TempObject.builder()
                .date(date)
                .breakfast(breakfast)
                .lunch(lunch)
                .dinner(dinner)
                .build();

        TempObject[] myDataset = new TempObject[1];
        myDataset[0] = objectToPass;
        mAdapter = new DayAdapter(myDataset);

        mRecyclerView.setAdapter(mAdapter);
        return view;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
       // if (mListener != null) {
        //    mListener.onFragmentInteraction(uri);
       // }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
      //  mListener = null;
    }

}


