package com.example.IT3A_PartialApps_grp11;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.BreakIterator;
import java.util.ArrayList;

public class myAdapter extends RecyclerView.Adapter<myAdapter.myviewholder> {

    ArrayList<User> datalist;

    public myAdapter(ArrayList<User> datalist) {
        this.datalist = datalist;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerow, parent, false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        String fullName = datalist.get(position).getFullName();
        String email = datalist.get(position).getUserEmail();
        String phoneNumber = datalist.get(position).getPhoneNumber();

        String formattedFullName = fullName.length() > 50 ? fullName.substring(0, 50) : fullName;

        holder.t1.setText(formattedFullName);
        holder.t3.setText(phoneNumber);
        holder.t2.setText(email);
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    class myviewholder extends RecyclerView.ViewHolder {

        TextView t1, t2, t3;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            t1 = itemView.findViewById(R.id.t1);
            t2 = itemView.findViewById(R.id.t2);
            t3 = itemView.findViewById(R.id.t3);
        }
    }
}
