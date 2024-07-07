package com.example.IT3A_PartialApps_grp11;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.github.mikephil.charting.components.YAxis;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class Reports extends AppCompatActivity {

    private static final int REQUEST_WRITE_STORAGE = 112;
    Button backHome, download;
    CardView graphBene, graphSub;
    FirebaseFirestore fStore;
    BarChart chartBeneficiaries;
    PieChart chartSubsidy;
    FirebaseFirestore db;
    String currentUserFullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        db = FirebaseFirestore.getInstance();
        download = findViewById(R.id.btn_download);
        graphBene = findViewById(R.id.graphBene);
        graphSub = findViewById(R.id.graphSub);
        backHome = findViewById(R.id.backHome);
        chartBeneficiaries = findViewById(R.id.chartBeneficiaries);
        chartSubsidy = findViewById(R.id.chartSubsidy);

        fStore = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            fStore.collection("Users").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot document = task.getResult();
                    currentUserFullName = document.getString("FullName");
                }
            });
        }

        backHome.setOnClickListener(v -> finish());

        download.setOnClickListener(v -> {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.R) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    downloadContent();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
                }
            } else {
                downloadContent();
            }
        });

        generateBeneficiariesGraph();
        generateSubsidyGraph();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            downloadContent();
        } else {
            Toast.makeText(this, "The app needs storage permissions to create PDF.", Toast.LENGTH_LONG).show();
        }
    }

    private void generateBeneficiariesGraph() {
        db.collection("Users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Map<String, Integer> resiCounts = new HashMap<>();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String resi = document.getString("isResi");

                    if (resi != null && !resi.isEmpty()) {
                        resiCounts.put(resi, resiCounts.getOrDefault(resi, 0) + 1);
                    }
                }

                List<BarEntry> entries = new ArrayList<>();
                int index = 1;

                for (Map.Entry<String, Integer> entry : resiCounts.entrySet()) {
                    entries.add(new BarEntry(index++, entry.getValue()));
                }

                BarDataSet dataSet = new BarDataSet(entries, "Registered Residences");

                BarData data = new BarData(dataSet);
                chartBeneficiaries.setData(data);
                chartBeneficiaries.invalidate();

                dataSet.setColors(Color.CYAN);
                dataSet.setValueTextColor(Color.BLACK);
                dataSet.setValueTextSize(16f);

                chartBeneficiaries.getDescription().setEnabled(false);
                chartBeneficiaries.setDrawGridBackground(false);
                chartBeneficiaries.setDrawBarShadow(false);

                chartBeneficiaries.getXAxis().setDrawGridLines(false);
                chartBeneficiaries.getXAxis().setDrawAxisLine(true);
                chartBeneficiaries.getXAxis().setGranularity(1f);

                YAxis leftAxis = chartBeneficiaries.getAxisLeft();
                leftAxis.setDrawGridLines(false);
                leftAxis.setDrawAxisLine(true);
                leftAxis.setTextSize(14f);

                YAxis rightAxis = chartBeneficiaries.getAxisRight();
                rightAxis.setDrawGridLines(false);
                rightAxis.setDrawAxisLine(true);
                rightAxis.setTextSize(14f);

                chartBeneficiaries.animateY(1000);

                Legend legend = chartBeneficiaries.getLegend();
                legend.setEnabled(true);
                legend.setTextSize(14f);
                legend.setWordWrapEnabled(true);
                legend.setFormSize(12f);
                legend.setForm(Legend.LegendForm.SQUARE);
                legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
                legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                legend.setDrawInside(false);
                legend.setXEntrySpace(7f);
                legend.setYEntrySpace(0f);
                legend.setYOffset(5f);
            } else {
                // Handle errors
            }
        });
    }

    private void generateSubsidyGraph() {
        db.collection("Users").get().addOnCompleteListener(task -> {
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

                dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                dataSet.setValueTextColor(Color.BLACK);
                dataSet.setValueTextSize(16f);
                dataSet.setValueFormatter(new PercentFormatter(chartSubsidy));

                PieData data = new PieData(dataSet);
                chartSubsidy.setData(data);
                chartSubsidy.invalidate();

                chartSubsidy.getDescription().setEnabled(false);
                chartSubsidy.setDrawEntryLabels(false);
                chartSubsidy.setUsePercentValues(true);

                chartSubsidy.animateY(1000);

                Legend legend = chartSubsidy.getLegend();
                legend.setEnabled(true);
                legend.setTextSize(12f);
                legend.setWordWrapEnabled(true);
                legend.setFormSize(10f);
                legend.setForm(Legend.LegendForm.CIRCLE);
                legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
                legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                legend.setDrawInside(false);
                legend.setXEntrySpace(14f);
                legend.setYEntrySpace(0f);
                legend.setYOffset(5f);

                data.setValueFormatter(new PercentFormatter(chartSubsidy));
                data.setValueTextColor(Color.BLACK);
                data.setValueTextSize(16f);
            } else {
                // Handle errors
            }
        });
    }

    private void downloadContent() {
        PdfDocument pdfDocument = new PdfDocument();
        int pageHeight = 842;
        int pageWidth = 595;
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();

        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(12);

        int y = 25;
        Bitmap bitmapLeft = BitmapFactory.decodeResource(getResources(), R.drawable.quezoncitylogo);
        Bitmap scaledBitmapLeft = Bitmap.createScaledBitmap(bitmapLeft, 100, 100, false);
        Bitmap bitmapRight = BitmapFactory.decodeResource(getResources(), R.drawable.images_removebg_preview);
        Bitmap scaledBitmapRight = Bitmap.createScaledBitmap(bitmapRight, 100, 100, false);
        canvas.drawBitmap(scaledBitmapLeft, 20, 20, paint);
        canvas.drawBitmap(scaledBitmapRight, pageWidth - 120, 20, paint);

        int yCurrentPosition = 50;

        paint.setTextSize(12);
        paint.setFakeBoldText(true);
        canvas.drawText("Republika ng Pilipinas", (pageWidth - paint.measureText("Republika ng Pilipinas")) / 2, yCurrentPosition, paint);
        yCurrentPosition += 15;
        canvas.drawText("BARANGAY TATALON", (pageWidth - paint.measureText("BARANGAY TATALON")) / 2, yCurrentPosition, paint);
        yCurrentPosition += 15;
        canvas.drawText("District IV, Lungsod Quezon", (pageWidth - paint.measureText("District IV, Lungsod Quezon")) / 2, yCurrentPosition, paint);
        yCurrentPosition += 15;
        canvas.drawText("TANGGAPAN NG PUNONG BARANGAY", (pageWidth - paint.measureText("TANGGAPAN NG PUNONG BARANGAY")) / 2, yCurrentPosition, paint);
        yCurrentPosition += 15;
        canvas.drawText("75275525 - brgytatalon@quezoncity.gov.ph", (pageWidth - paint.measureText("75275525 - brgytatalon@quezoncity.gov.ph")) / 2, yCurrentPosition, paint);
        yCurrentPosition += 40;

        paint.setFakeBoldText(false);
        canvas.drawText("Beneficiaries Data", (pageWidth - paint.measureText("Beneficiaries Data")) / 2, yCurrentPosition, paint);
        yCurrentPosition += 20;
        int highResWidth = 1100;
        int highResHeight = 1100;
        Bitmap highResBeneficiariesBitmap = Bitmap.createBitmap(highResWidth, highResHeight, Bitmap.Config.ARGB_8888);
        Canvas highResBeneficiariesCanvas = new Canvas(highResBeneficiariesBitmap);
        chartBeneficiaries.draw(highResBeneficiariesCanvas);
        Bitmap scaledBitmapBeneficiaries = Bitmap.createScaledBitmap(highResBeneficiariesBitmap, 270, 270, false);
        canvas.drawBitmap(scaledBitmapBeneficiaries, (pageWidth - scaledBitmapBeneficiaries.getWidth()) / 2, yCurrentPosition, paint);
        yCurrentPosition += scaledBitmapBeneficiaries.getHeight();

        canvas.drawText("Subsidy Data", (pageWidth - paint.measureText("Subsidy Data")) / 2, yCurrentPosition, paint);
        yCurrentPosition += 20;

        Bitmap highResSubsidyBitmap = Bitmap.createBitmap(highResWidth, highResHeight, Bitmap.Config.ARGB_8888);
        Canvas highResSubsidyCanvas = new Canvas(highResSubsidyBitmap);
        chartSubsidy.draw(highResSubsidyCanvas);
        Bitmap scaledBitmapSubsidy = Bitmap.createScaledBitmap(highResSubsidyBitmap, 270, 270, false);
        canvas.drawBitmap(scaledBitmapSubsidy, (pageWidth - scaledBitmapSubsidy.getWidth()) / 2, yCurrentPosition, paint);
        yCurrentPosition += scaledBitmapSubsidy.getHeight() + 20;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Manila"));
        String currentDate = dateFormat.format(new Date());

        String footer = "Printed by: " + currentUserFullName + " on " + currentDate;
        canvas.drawText(footer, 20, pageHeight - 20, paint);

        pdfDocument.finishPage(page);

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "Subsidy Report.pdf");
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        Uri uri = getContentResolver().insert(MediaStore.Files.getContentUri("external"), contentValues);

        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            pdfDocument.writeTo(outputStream);
            outputStream.close();
            Toast.makeText(this, "PDF saved successfully.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save PDF.", Toast.LENGTH_SHORT).show();
        }

        pdfDocument.close();
    }
}
