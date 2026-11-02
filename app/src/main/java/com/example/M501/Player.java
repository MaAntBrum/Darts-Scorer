package com.example.M501;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Player#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Player extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Player() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Player.
     */
    // TODO: Rename and change types and number of parameters
    public static Player newInstance(String param1, String param2) {
        Player fragment = new Player();
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
    String eingabe;
    DatabaseHelper myDB;
    ArrayList <String> player_id, player_name, player_siege, player_spiele;

    CustomAdapter customAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        // Inflate the layout for this fragment
        Intent intent = new Intent(getActivity(), GameActivity.class);
        // Inflate the layout for this fragment


        FloatingActionButton add = view.findViewById(R.id.addPlayer);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpAddPlayer(intent);
            }
        });

        RecyclerView playerList = view.findViewById(R.id.listview_Player);


        myDB= new DatabaseHelper(getContext());
        player_id = new ArrayList<>();
        player_name = new ArrayList<>();
        player_spiele = new ArrayList<>();
        player_siege = new ArrayList<>();

        storeDataInArray();

        customAdapter = new CustomAdapter(getContext(), player_id, player_name, player_spiele, player_siege);
        playerList.setAdapter(customAdapter);
        playerList.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    void storeDataInArray(){
        Cursor cursor = myDB.readAllData("player");
        if(cursor.getCount() == 0){
            Toast.makeText(getContext(), "No data", Toast.LENGTH_SHORT).show();
        } else{
            while (cursor.moveToNext() ){
                player_id.add(cursor.getString(0));
                player_name.add(cursor.getString(1));
                player_spiele.add(cursor.getString(2));
                player_siege.add(cursor.getString(3));
            }
        }
    }


    public void popUpAddPlayer(Intent intent){
        MainActivity main = new MainActivity();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_layout, null);
        // Finde das EditText-Feld im benutzerdefinierten Layout
        EditText editText = dialogView.findViewById(R.id.editText);
        DatabaseHelper dbh = new DatabaseHelper(getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Bitte geben Sie den Namen des Spielers ein, den Sie hinzufügen möchten.")
                .setView(dialogView) // Setze das benutzerdefinierte Layout
                .setPositiveButton("Fertig", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        eingabe = editText.getText().toString();

                        dbh.addPlayer(eingabe);

                        dialogInterface.dismiss();
                        main.replaceFragment(fragmentManager, new Player());
                    }
                })
                .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .show();
    }
}