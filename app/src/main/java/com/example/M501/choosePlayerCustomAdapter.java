package com.example.M501;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class choosePlayerCustomAdapter extends RecyclerView.Adapter<choosePlayerCustomAdapter.MyViewHolder1> {

    private Context context;
    private ArrayList player_id, player_name, player_siege, player_spiele;

    public choosePlayerCustomAdapter(Context context,
                                     ArrayList player_id,
                                     ArrayList player_name,
                                     ArrayList player_siege,
                                     ArrayList player_spiele) {
        this.context = context;
        this.player_id = player_id;
        this.player_name = player_name;
        this.player_siege = player_siege;
        this.player_spiele = player_spiele;
    }

    @NonNull
    @Override
    public MyViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder1(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder1 holder, int position) {
        // Setze die Player-Daten
        holder.player_id.setText(String.valueOf(player_id.get(position)) + " ");
        holder.player_name.setText(String.valueOf(player_name.get(position)));

        // Klicklistener für jeden Eintrag
        holder.recyclerviewEintrag.setOnClickListener(view -> {
            if (choose_player.eins == false) {
                holder.player_name.setTextColor(context.getResources().getColor(R.color.blue));
                choose_player.name1 = holder.player_name.getText().toString();
                choose_player.text = "Wähle Spieler 2";
                choose_player.eins = true;
            } else if (choose_player.zwei == false) {
                holder.player_name.setTextColor(context.getResources().getColor(R.color.blue));
                choose_player.name2 = holder.player_name.getText().toString();
                choose_player.text = "Du hast 2 Spieler gewählt";
                choose_player.zwei = true;
            } else {
                Toast.makeText(context, "Sie können maximal 2 Spieler wählen", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return player_id.size();
    }

    public class MyViewHolder1 extends RecyclerView.ViewHolder {

        TextView player_id, player_name;
        LinearLayout recyclerviewEintrag;

        public MyViewHolder1(@NonNull View itemView) {
            super(itemView);
            player_id = itemView.findViewById(R.id.player_id_text);
            player_name = itemView.findViewById(R.id.player_name_text);
            recyclerviewEintrag = itemView.findViewById(R.id.recycleViewEintrag);
        }
    }
}
