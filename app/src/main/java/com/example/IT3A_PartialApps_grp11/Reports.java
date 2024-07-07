package com.example.IT3A_PartialApps_grp11;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Reports extends AppCompatActivity {

    Button backHome;
    CardView graphBene, graphSub;
    BarChart chartBeneficiaries;
    PieChart chartSubsidy;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        db = FirebaseFirestore.getInstance();

        graphBene = findViewById(R.id.graphBene);
        graphSub = findViewById(R.id.graphSub);
        backHome = findViewById(R.id.backHome);
        chartBeneficiaries = findViewById(R.id.chartBeneficiaries);
        chartSubsidy = findViewById(R.id.chartSubsidy);

        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Generate data and populate charts
        generateBeneficiariesGraph();
        generateSubsidyGraph();
    }

    private void generateBeneficiariesGraph() {
        db.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Initialize a map to count occurrences of each 'isResi' value
                            Map<String, Integer> resiCounts = new HashMap<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String resi = document.getString("isResi");

                                // Increment count for this 'isResi' value
                                if (resi != null && !resi.isEmpty()) {
                                    if (resiCounts.containsKey(resi)) {
                                        resiCounts.put(resi, resiCounts.get(resi) + 1);
                                    } else {
                                        resiCounts.put(resi, 1);
                                    }
                                }
                            }

                            // Prepare entries for the bar chart
                            List<BarEntry> entries = new ArrayList<>();
                            int index = 1;

                            // Convert map entries to BarEntry objects
                            for (Map.Entry<String, Integer> entry : resiCounts.entrySet()) {
                                entries.add(new BarEntry(index++, entry.getValue()));
                            }

                            BarDataSet dataSet = new BarDataSet(entries, "Registered Residences");

                            BarData data = new BarData(dataSet);
                            chartBeneficiaries.setData(data);
                            chartBeneficiaries.invalidate(); // refresh

                            // Customize chart appearance
                            dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                            dataSet.setValueTextColor(android.graphics.Color.BLACK);
                            dataSet.setValueTextSize(10f);

                            chartBeneficiaries.getDescription().setEnabled(false);
                            chartBeneficiaries.setDrawGridBackground(false);
                            chartBeneficiaries.setDrawBarShadow(false);

                            chartBeneficiaries.getXAxis().setDrawGridLines(false);
                            chartBeneficiaries.getXAxis().setDrawAxisLine(true);
                            chartBeneficiaries.getXAxis().setGranularity(1f);

                            chartBeneficiaries.getAxisRight().setDrawGridLines(false);
                            chartBeneficiaries.getAxisRight().setDrawAxisLine(true);
                            chartBeneficiaries.getAxisLeft().setDrawGridLines(false);
                            chartBeneficiaries.getAxisLeft().setDrawAxisLine(true);

                            chartBeneficiaries.animateY(2000);

                            // Customize legend
                            Legend legend = chartBeneficiaries.getLegend();
                            legend.setEnabled(true);
                            legend.setTextSize(12f);
                            legend.setWordWrapEnabled(true); // Enable word wrap if necessary
                            legend.setFormSize(10f); // Set form size
                            legend.setForm(Legend.LegendForm.SQUARE); // Set legend form to square
                            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM); // Align legend vertically
                            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER); // Align legend horizontally
                            legend.setOrientation(Legend.LegendOrientation.HORIZONTAL); // Set legend orientation
                            legend.setDrawInside(false); // Draw legend outside chart
                            legend.setXEntrySpace(7f); // Set space between legend entries on x-axis
                            legend.setYEntrySpace(0f); // Set space between legend entries on y-axis
                            legend.setYOffset(5f); // Set offset from chart
                        } else {
                            // Handle errors
                        }
                    }
                });
    }

    private void generateSubsidyGraph() {
        db.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int subsidizedCount = 0;
                            int pendingCount = 0;
                            int notSubsidizedCount = 0;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String status = document.getString("SubsidyStatus");
                                if ("Claimed".equals(status)) {
                                    subsidizedCount++;
                                } else if ("Pending".equals(status)) {
                                    pendingCount++;
                                } else {
                                    notSubsidizedCount++;
                                }
                            }

                            ArrayList<PieEntry> entries = new ArrayList<>();
                            entries.add(new PieEntry(subsidizedCount, "Claimed"));
                            entries.add(new PieEntry(pendingCount, "Pending"));
                            entries.add(new PieEntry(notSubsidizedCount, "For Claiming"));

                            PieDataSet dataSet = new PieDataSet(entries, " ");

                            // Customize pie data set
                            dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                            dataSet.setValueTextColor(android.graphics.Color.BLACK);
                            dataSet.setValueTextSize(10f);
                            dataSet.setValueFormatter(new PercentFormatter(chartSubsidy));

                            // Create pie data object
                            PieData data = new PieData(dataSet);
                            chartSubsidy.setData(data);
                            chartSubsidy.invalidate(); // refresh

                            // Customize pie chart
                            chartSubsidy.getDescription().setEnabled(false);
                            chartSubsidy.setDrawEntryLabels(false);
                            chartSubsidy.setUsePercentValues(true);

                            chartSubsidy.animateY(2000);

                            // Customize legend
                            Legend legend = chartSubsidy.getLegend();
                            legend.setEnabled(true);
                            legend.setTextSize(10f);
                            legend.setWordWrapEnabled(true);
                            legend.setFormSize(10f);
                            legend.setForm(Legend.LegendForm.CIRCLE);
                            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
                            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                            legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                            legend.setDrawInside(false);
                            legend.setXEntrySpace(7f);
                            legend.setYEntrySpace(0f);
                            legend.setYOffset(5f);

                            // Set value formatter for data set
                            data.setValueFormatter(new PercentFormatter(chartSubsidy));
                            data.setValueTextColor(android.graphics.Color.BLACK);
                            data.setValueTextSize(10f);
                        } else {
                            // Handle errors
                        }
                    }
                });
    }



}
