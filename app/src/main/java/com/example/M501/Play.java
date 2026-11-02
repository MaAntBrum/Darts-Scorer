package com.example.M501;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Play#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Play extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Play() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Play.
     */
    // TODO: Rename and change types and number of parameters
    public static Play newInstance(String param1, String param2) {
        Play fragment = new Play();
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

    String[] items1 = {"0"};//,"1","2","3","4","5","6"
    String[] items2 = {"1","2","3","4","5"};
    String[] itemsPunkte = {"101","201","301","401","501","601","701","801","901"};
    String[] itemsPlayer = {"zufall", "Spieler1", "Spieler2"};
    String[] checkOut = {"Single", "Double","Master"};
    String[] checkIn = {"Single"};//, "Double","Master"
    String eingabe;
    static TextView nameP1;
    static TextView nameP2;
    ArrayAdapter<String> adapterItems;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play, container, false);
        // Inflate the layout for this fragment
        Intent  intent = new Intent(getActivity(), GameActivity.class);
        nameP1 = view.findViewById(R.id.Player1);
        nameP2 = view.findViewById(R.id.Player2);

        choose_player choosePlayer = new choose_player();
        nameP1.setText(choosePlayer.name1);
        nameP2.setText(choosePlayer.name2);
        intent.putExtra("Spieler1Name", nameP1.getText());
        intent.putExtra("Spieler2Name", nameP2.getText());
        CardView cardView1 = view.findViewById(R.id.cardview1);
        CardView cardView2 = view.findViewById(R.id.cardview2);

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        MainActivity main = new MainActivity();

        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePlayer.text = "Wähle Spieler 1";
                main.replaceFragment(fragmentManager, new choose_player());

            }
        });



        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePlayer.text = "Wähle Spieler 2";
                main.replaceFragment(fragmentManager, new choose_player());
                //popUpNamen(2, intent);
            }
        });


        AutoCompleteTextView autoCompleteTextView1 = view.findViewById(R.id.auto_complete_txt);
        setupDropDownMenu(autoCompleteTextView1, items1,"Sets", intent);


        AutoCompleteTextView autoCompleteTextView2 = view.findViewById(R.id.auto_complete_txt2);
        setupDropDownMenu(autoCompleteTextView2, items2,"Legs", intent);

        AutoCompleteTextView autoCompleteTextView3 = view.findViewById(R.id.auto_complete_txt_startPunkte);
       setupDropDownMenu(autoCompleteTextView3, itemsPunkte,"Startpunkte", intent);


        AutoCompleteTextView autoCompleteTextView4 = view.findViewById(R.id.auto_complete_txt_startSpieler);
       setupDropDownMenu(autoCompleteTextView4, itemsPlayer,"Startspieler",intent);


        AutoCompleteTextView autoCompleteTextView5 = view.findViewById(R.id.auto_complete_txt_CheckIn);
       setupDropDownMenu(autoCompleteTextView5, checkIn,"Checkin",intent);


        AutoCompleteTextView autoCompleteTextView6 = view.findViewById(R.id.auto_complete_txt_CheckOut);
       setupDropDownMenu(autoCompleteTextView6, checkOut,"Checkout",intent);

        Button btnStart = view.findViewById(R.id.startBtn);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //MainActivity mainActivity = new MainActivity();
                //FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                //mainActivity.replaceFragment(fragmentManager, new GameFragment());
               // intent.putExtra("Sets1", "");
                //intent.putExtra("Legs1", "");
                //intent.putExtra("Startpunkte1", "");
                //intent.putExtra("Startspieler1","");
                //intent.putExtra("Checkin1", "");
                //intent.putExtra("Checkout", "");
                startActivity(intent);
            }
        });

        return view;
    }

    public void popUpNamen(int spieler, Intent intent){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_layout, null);
        // Finde das EditText-Feld im benutzerdefinierten Layout
        EditText editText = dialogView.findViewById(R.id.editText);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Bitte geben sie einen Namen ein")
                .setView(dialogView) // Setze das benutzerdefinierte Layout
                .setPositiveButton("Fertig", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                         eingabe = editText.getText().toString();
                        if(spieler==1){
                            nameP1.setText(eingabe);
                            intent.putExtra("Spieler1Name", eingabe);
                        }else{
                            nameP2.setText(eingabe);
                            intent.putExtra("Spieler2Name", eingabe);
                        }
                        dialogInterface.dismiss();
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
    public void setupDropDownMenu(AutoCompleteTextView autoCompleteTextView, String[] items, String name, Intent intent){
        Context context = getContext();
        adapterItems = new ArrayAdapter<>(context, R.layout.list_item1, items);
        autoCompleteTextView.setAdapter(adapterItems);
        final String item12;
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                intent.putExtra(name, item);
                System.out.println(item);
            }
        });
    }
}