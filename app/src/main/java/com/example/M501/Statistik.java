package com.example.M501;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Statistik#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Statistik extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Statistik() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Statistik.
     */
    // TODO: Rename and change types and number of parameters
    public static Statistik newInstance(String param1, String param2) {
        Statistik fragment = new Statistik();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    DatabaseHelper myDB;
    private ArrayList <String> game_id, name1, name2, legs1, legs2, avg1, avg2;
    CustomAdapterStatistiken customAdapterStatistiken;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistik, container, false);
        // Inflate the layout for this fragment
        Intent intent = new Intent(getActivity(), GameActivity.class);
        // Inflate the layout for this fragment


        RecyclerView playerList = view.findViewById(R.id.recycleView_games);
        myDB= new DatabaseHelper(getContext());
        game_id = new ArrayList<>();
        name1 = new ArrayList<>();
        name2 = new ArrayList<>();
        legs1 = new ArrayList<>();
        legs2 = new ArrayList<>();
        avg1 = new ArrayList<>();
        avg2 = new ArrayList<>();

        storeDataInArray();

        customAdapterStatistiken = new CustomAdapterStatistiken(getContext(), game_id, name1, name2, legs1, legs2, avg1, avg2);
        playerList.setAdapter(customAdapterStatistiken);
        playerList.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    void storeDataInArray(){
        Cursor cursor = myDB.readAllData("games");
        if(cursor.getCount() == 0){
            Toast.makeText(getContext(), "No data", Toast.LENGTH_SHORT).show();
        } else{
            while (cursor.moveToNext() ){
                game_id.add(cursor.getString(0));
                name1.add(cursor.getString(1));
                name2.add(cursor.getString(2));
                legs1.add(cursor.getString(3));
                legs2.add(cursor.getString(4));
                avg1.add(cursor.getString(5));
                avg2.add(cursor.getString(6));
            }
        }
    }
}