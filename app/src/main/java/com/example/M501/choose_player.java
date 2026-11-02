package com.example.M501;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link choose_player#newInstance} factory method to
 * create an instance of this fragment.
 */
public class choose_player extends Fragment {

    // Argument Keys
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Argument Values
    private String mParam1;
    private String mParam2;

    // DB + Daten
    DatabaseHelper myDB;
    ArrayList<String> player_id, player_name, player_siege, player_spiele;

    // Spieler-Status
    static String text = "";
    static String name1 = "Player1";
    static String name2 = "Player2";
    static Boolean eins = false;
    static Boolean zwei = false;

    choosePlayerCustomAdapter customAdapter;

    public choose_player() {
        // Required empty public constructor
    }

    /**
     * Factory Methode um neues Fragment mit Parametern zu erzeugen
     */
    public static choose_player newInstance(String param1, String param2) {
        choose_player fragment = new choose_player();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_choose_player, container, false);

        // UI Referenzen
        TextView textView = view.findViewById(R.id.choose_player_text);
        RecyclerView playerchooseList = view.findViewById(R.id.recycleView_Player_choose);
        FloatingActionButton finish = view.findViewById(R.id.finishPlayer);

        textView.setText(text);

        // Button Listener
        finish.setOnClickListener(v -> {
            eins = false;
            zwei = false;
            changeFragment();
        });

        // Daten vorbereiten
        myDB = new DatabaseHelper(getContext());
        player_id = new ArrayList<>();
        player_name = new ArrayList<>();
        player_spiele = new ArrayList<>();
        player_siege = new ArrayList<>();

        storeDataInArray();

        // RecyclerView Adapter setzen
        customAdapter = new choosePlayerCustomAdapter(
                getContext(),
                player_id,
                player_name,
                player_spiele,
                player_siege
        );
        playerchooseList.setAdapter(customAdapter);
        playerchooseList.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    /**
     * Liest Spieler aus DB in ArrayLists
     */
    void storeDataInArray() {
        Cursor cursor = myDB.readAllData("player");
        if (cursor.getCount() == 0) {
            Toast.makeText(getContext(), "No data", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                player_id.add(cursor.getString(0));
                player_name.add(cursor.getString(1));
                player_spiele.add(cursor.getString(2));
                player_siege.add(cursor.getString(3));
            }
        }
    }

    /**
     * Wechselt zum Spielfragment
     */
    public void changeFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        MainActivity main = new MainActivity();
        main.replaceFragment(fragmentManager, new Play());
    }
}
