package com.example.M501;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    private ArrayList player_id, player_name, player_siege, player_spiele;
    int position;

    public CustomAdapter(Context context, ArrayList player_id, ArrayList player_name, ArrayList player_siege, ArrayList player_spiele) {
        this.context = context;
        this.player_id = player_id;
        this.player_name = player_name;
        this.player_siege = player_siege;
        this.player_spiele = player_spiele;
    }

    @NonNull
    @Override
    public CustomAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);

        return new MyViewHolder(view);
    }

    Player player;
    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.MyViewHolder holder, int position) {
        holder.player_id.setText(String.valueOf(player_id.get(position)) + "  ");
        holder.player_name.setText(String.valueOf(player_name.get(position)));
        //this.position = position;
       /* holder.recyclerviewEintrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Intent intent = new Intent(context, GameActivity.class);
                //player.popUpAddPlayer(intent);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return player_id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView player_id, player_name;
        LinearLayout recyclerviewEintrag;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            player_id = itemView.findViewById(R.id.player_id_text);
            player_name = itemView.findViewById(R.id.player_name_text);
            recyclerviewEintrag = itemView.findViewById(R.id.recycleViewEintrag) ;
        }
    }
}
