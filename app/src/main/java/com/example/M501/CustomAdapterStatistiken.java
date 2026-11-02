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

public class CustomAdapterStatistiken extends RecyclerView.Adapter<CustomAdapterStatistiken.MyViewHolder> {

    private Context context;
    private ArrayList game_id, name1, name2, legs1, legs2, avg1, avg2;
    int position;

    public CustomAdapterStatistiken(Context context, ArrayList game_id, ArrayList name1, ArrayList name2, ArrayList legs1, ArrayList legs2, ArrayList avg1,ArrayList avg2) {
        this.context = context;
        this.game_id = game_id;
        this.name1 = name1;
        this.name2 = name2;
        this.legs1 = legs1;
        this.legs2 = legs2;
        this.avg2 = avg2;
        this.avg1 = avg1;
    }

    @NonNull
    @Override
    public CustomAdapterStatistiken.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row_statistik, parent, false);

        return new MyViewHolder(view);
    }

    Player player;
    @Override
    public void onBindViewHolder(@NonNull CustomAdapterStatistiken.MyViewHolder holder, int position) {
       holder.game_id.setText(String.valueOf(game_id.get(position)));
       holder.name1.setText(String.valueOf(name1.get(position)));
       holder.name2.setText(String.valueOf(name2.get(position)));
       holder.legs1.setText(String.valueOf(legs1.get(position)));
       holder.legs2.setText(String.valueOf(legs2.get(position)));
       holder.avg1.setText(String.valueOf(avg1.get(position)));
       holder.avg2.setText(String.valueOf(avg2.get(position)));

       //       holder.avg1.setText(String.format("%.2f", String.valueOf(avg1.get(position))));
        //       holder.avg2.setText(String.format("%.2f", String.valueOf(avg2.get(position))));

    }

    @Override
    public int getItemCount() {
        return game_id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView game_id, name1, name2, legs1, legs2, avg1, avg2;
        LinearLayout recyclerviewEintrag;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            game_id = itemView.findViewById(R.id.game_id_text);
            name1 = itemView.findViewById(R.id.NamePlayer1);
            name2 = itemView.findViewById(R.id.NamePlayer2);
            legs1 = itemView.findViewById(R.id.LegsPlayer1);
            legs2 = itemView.findViewById(R.id.LegsPlayer2);
            avg1 = itemView.findViewById(R.id.textView18);
            avg2 = itemView.findViewById(R.id.textView19);
            recyclerviewEintrag = itemView.findViewById(R.id.recycleViewEintrag) ;
        }
    }
}
