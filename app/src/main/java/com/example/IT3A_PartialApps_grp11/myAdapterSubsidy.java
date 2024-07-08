package com.example.IT3A_PartialApps_grp11;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class myAdapterSubsidy extends RecyclerView.Adapter<myAdapterSubsidy.myviewholder> {
    ArrayList<UserSubsidy> subsidylist;
    Context context;

    public myAdapterSubsidy(Context context, ArrayList<UserSubsidy> subsidylist) {
        this.subsidylist = subsidylist;
        this.context = context;
    }


    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerowsubsidy, parent, false);
        context = parent.getContext();
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        UserSubsidy subsidy = subsidylist.get(position);

        String fullName = subsidy.getFullName();
        String subsidyStatus = subsidy.getSubsidyStatus();
        Timestamp timestamp = subsidy.getTimestamp();
        String timestampString = (timestamp != null) ? formatTimestamp(timestamp) : "";
        holder.t3.setText(fullName != null ? fullName : "N/A");
        holder.t4.setText("Status: " + subsidyStatus != null ? subsidyStatus : "N/A");
        holder.timestamp.setText(timestampString);

        holder.actionButton.setOnClickListener(v -> showSubsidyStatusDialog(position, fullName, subsidyStatus, subsidy.getUserId()));
    }
    private String formatTimestamp(Timestamp timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Manila")); // Set timezone to Philippines (UTC+8)
        return sdf.format(timestamp.toDate());
    }

    @Override
    public int getItemCount() {
        return subsidylist.size();
    }

    private void showSubsidyStatusDialog(int position, String fullName, String currentSubsidyStatus, String userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_subsidy_status, null);
        builder.setView(dialogView);

        Spinner subsidySpinner = dialogView.findViewById(R.id.subsidy_status_spinner);
        Button saveButton = dialogView.findViewById(R.id.save_button);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.subsidy_status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subsidySpinner.setAdapter(adapter);

        int positionInSpinner = adapter.getPosition(currentSubsidyStatus);
        subsidySpinner.setSelection(positionInSpinner);

        AlertDialog dialog = builder.create();

        saveButton.setOnClickListener(v -> {
            String newSubsidyStatus = (String) subsidySpinner.getSelectedItem();
            updateSubsidyStatus(userId, newSubsidyStatus, position);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updateSubsidyStatus(String userId, String newStatus, int positionToUpdate) {
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();

        Map<String, Object> updates = new HashMap<>();
        updates.put("SubsidyStatus", newStatus);

        if ("Claiming".equals(newStatus)) {
            updates.put("timestamp", new Timestamp(new Date()));
        }

        fStore.collection("Users").document(userId).update(updates)
                .addOnSuccessListener(aVoid -> {
                    subsidylist.get(positionToUpdate).setSubsidyStatus(newStatus);
                    if ("Claiming".equals(newStatus)) {
                        subsidylist.get(positionToUpdate).setTimestamp(new Timestamp(new Date()));
                    } else {
                        subsidylist.get(positionToUpdate).setTimestamp(null);
                    }
                    notifyItemChanged(positionToUpdate);
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                });
    }


    public class myviewholder extends RecyclerView.ViewHolder {
        TextView t3, t4, timestamp;
        TextView actionButton;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            t3 = itemView.findViewById(R.id.t3);
            t4 = itemView.findViewById(R.id.t4);
            timestamp = itemView.findViewById(R.id.timestamp);
            actionButton = itemView.findViewById(R.id.actionButton);
        }
    }
}
