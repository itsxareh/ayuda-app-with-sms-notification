package com.example.IT3A_PartialApps_grp11;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class SubsidyList extends AppCompatActivity {
    private static final int REQUEST_WRITE_STORAGE = 112;
    RecyclerView recViewSubsidy;
    ArrayList<UserSubsidy> subsidylist;
    ArrayList<UserSubsidy> filteredsubsidy;
    FirebaseFirestore fStore;
    myAdapterSubsidy adapter;
    EditText searchSubsidy;
    Spinner statusSpinner;
    ImageButton filter;
    String currentUserFullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subsidy_list);

        searchSubsidy = findViewById(R.id.searchSubsidy);
        recViewSubsidy = findViewById(R.id.recViewSubsidy);
        statusSpinner = findViewById(R.id.statusSpinner);
        recViewSubsidy.setLayoutManager(new LinearLayoutManager(this));
        subsidylist = new ArrayList<>();
        filteredsubsidy = new ArrayList<>();
        adapter = new myAdapterSubsidy(this, filteredsubsidy);
        recViewSubsidy.setAdapter(adapter);
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

        fStore.collection("Users").whereEqualTo("isResi", "2").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        UserSubsidy usersubsidy = document.toObject(UserSubsidy.class);
                        if (usersubsidy != null) {
                            usersubsidy.setUserId(document.getId());
                            usersubsidy.setEmail(document.getString("UserEmail"));
                            subsidylist.add(usersubsidy);
                        }
                    }
                    filteredsubsidy.addAll(subsidylist);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        searchSubsidy.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().toLowerCase(Locale.getDefault());
                filterAndSearchData(query, statusSpinner.getSelectedItem().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(this,
                R.array.status_array, android.R.layout.simple_spinner_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapterSpinner);

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedStatus = parent.getItemAtPosition(position).toString();
                filterData(selectedStatus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        Button btnDownload = findViewById(R.id.btn_download);
        btnDownload.setOnClickListener(v -> {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.R) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    createPdf(filteredsubsidy);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
                }
            } else {
                createPdf(filteredsubsidy);
            }
        });

        Button backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createPdf(filteredsubsidy);
            } else {
                Toast.makeText(this, "The app needs storage permissions to create PDF.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void createPdf(List<UserSubsidy> filteredSubsidyList) {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        paint.setTextSize(16);

        int pageHeight = 842;
        int pageWidth = 595;
        int yStartPosition = 50;
        int yCurrentPosition = yStartPosition;
        int xStartPosition = 20;
        int xCurrentPosition = xStartPosition;

        int yMarginHeader = yCurrentPosition + (2 * (int)(paint.descent() - paint.ascent()));

        Paint linePaint = new Paint();
        linePaint.setStrokeWidth(2f);

        int maxRowsPerPage = 35;

        int currentRow = 0;

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        yCurrentPosition = yStartPosition;

        Bitmap bitmapLeft = BitmapFactory.decodeResource(getResources(), R.drawable.quezoncitylogo);
        Bitmap scaledBitmapleft = Bitmap.createScaledBitmap(bitmapLeft, 100, 100,false);
        Bitmap bitmapRight = BitmapFactory.decodeResource(getResources(), R.drawable.images_removebg_preview);
        Bitmap scaledBitmapright = Bitmap.createScaledBitmap(bitmapRight, 100, 100,false);
        canvas.drawBitmap(scaledBitmapleft, 20, 20, paint);
        canvas.drawBitmap(scaledBitmapright, pageWidth-120, 20, paint);

        paint.setTextSize(12);
        paint.isFakeBoldText();
        canvas.drawText("Republika ng Pilipinas", (pageWidth - paint.measureText("Republika ng Pilipinas")) / 2, yCurrentPosition, paint);
        canvas.drawText("BARANGAY TATALON", (pageWidth - paint.measureText("BARANGAY TATALON")) / 2, yCurrentPosition + 15, paint);
        canvas.drawText("District IV, Lungsod Quezon", (pageWidth - paint.measureText("District IV, Lungsod Quezon")) / 2, yCurrentPosition  + 30, paint);
        canvas.drawText("TANGGAPAN NG PUNONG BARANGAY", (pageWidth - paint.measureText("TANGGAPAN NG PUNONG BARANGAY")) / 2, yCurrentPosition  + 45, paint);
        canvas.drawText("75275525 - brgytatalon@quezoncity.gov.ph", (pageWidth - paint.measureText("75275525 - brgytatalon@quezoncity.gov.ph")) / 2, yCurrentPosition  + 60, paint);

        yCurrentPosition += Math.max(scaledBitmapright.getHeight(), scaledBitmapright.getHeight());

        paint.setTextSize(10);

        drawTableHeader(canvas, xCurrentPosition, yCurrentPosition, paint, linePaint);
        yCurrentPosition += 20;

        for (UserSubsidy usersubsidy : filteredSubsidyList) {
            if (currentRow >= maxRowsPerPage) {
                pdfDocument.finishPage(page);
                pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pdfDocument.getPages().size() + 1).create();
                page = pdfDocument.startPage(pageInfo);
                canvas = page.getCanvas();
                yCurrentPosition = yMarginHeader + 20;

                drawTableHeader(canvas, xCurrentPosition, yCurrentPosition - 20, paint, linePaint);

                currentRow = 0;
            }
            drawSubsidyData(canvas, xCurrentPosition, yCurrentPosition, usersubsidy, paint, linePaint);
            yCurrentPosition += 20;
            currentRow++;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Manila"));
        String currentDate = dateFormat.format(new Date());

        String footer = "Printed by: " + currentUserFullName + " on " + currentDate;
        yCurrentPosition = pageHeight - 20;
        canvas.drawText(footer, xCurrentPosition, yCurrentPosition, paint);

        pdfDocument.finishPage(page);

        savePdfToStorage(pdfDocument);
        pdfDocument.close();
    }

    private void drawTableHeader(Canvas canvas, int x, int y, Paint paint, Paint line) {
        //horizontal lines
        canvas.drawLine(x, y - 13, 575, y - 13, line);
        canvas.drawLine(x, y + 6, 575, y + 6, line);
        //vertical lines
        canvas.drawLine(x, y - 12, x, y + 6, line);
        canvas.drawLine(x + 145, y - 12, x + 145, y + 6, line);
        canvas.drawLine(x + 375, y - 12, x + 375, y + 6, line);
        canvas.drawLine(x + 475, y - 12, x + 475, y + 6, line);
        canvas.drawLine(575, y - 12, 575, y + 6, line);

        canvas.drawText("Full Name", x + 5, y, paint);
        canvas.drawText("Email Address", x + 150, y, paint);
        canvas.drawText("Phone Number", x + 380, y, paint);
        canvas.drawText("Subsidy Status", x + 480, y, paint);
    }

    private void drawSubsidyData(Canvas canvas, int x, int y, UserSubsidy usersubsidy, Paint paint, Paint line) {
        canvas.drawText(usersubsidy.getFullName(), x + 5, y, paint);
        canvas.drawText(usersubsidy.getEmail(), x + 150, y, paint);
        canvas.drawText(usersubsidy.getPhoneNumber(), x + 380, y, paint);
        canvas.drawText(usersubsidy.getSubsidyStatus(), x + 480, y, paint);
        //horizontal lines
        canvas.drawLine(x, y + 6, 575, y + 6, line);
        //vertical lines
        canvas.drawLine(x, y - 14, x, y + 6, line);
        canvas.drawLine(x + 145, y - 14, x + 145, y + 6, line);
        canvas.drawLine(x + 375, y - 14, x + 375, y + 6, line);
        canvas.drawLine(x + 475, y - 14, x + 475, y + 6, line);
        canvas.drawLine(575, y - 14, 575, y + 6, line);
    }

    private void savePdfToStorage(PdfDocument pdfDocument) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, "BeneficiaryDetails.pdf");
        values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        Uri uri = getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);

        if (uri != null) {
            try (OutputStream fos = getContentResolver().openOutputStream(uri)) {
                pdfDocument.writeTo(fos);
                Toast.makeText(this, "PDF created successfully: " + uri.getPath(), Toast.LENGTH_LONG).show();
                Log.d("PDF Creation", "PDF created at: " + uri.getPath());
            } catch (IOException e) {
                Log.e("PDF Creation", "Error creating PDF", e);
                Toast.makeText(this, "Error creating PDF", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("PDF Creation", "Failed to create URI");
        }
    }

    private void searchData(String query) {
        filteredsubsidy.clear();
        if (query.isEmpty()) {
            filteredsubsidy.addAll(subsidylist);
        } else {
            filteredsubsidy.addAll(subsidylist.stream()
                    .filter(user -> user.getFullName().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList()));
        }
        adapter.notifyDataSetChanged();
    }

    private void filterData(String status) {
        filteredsubsidy.clear();
        if (status.equals("All")) {
            filteredsubsidy.addAll(subsidylist);
        } else {
            filteredsubsidy.addAll(subsidylist.stream()
                    .filter(user -> user.getSubsidyStatus().equalsIgnoreCase(status))
                    .collect(Collectors.toList()));
        }
        adapter.notifyDataSetChanged();
    }

    private void filterAndSearchData(String query, String status) {
        filteredsubsidy.clear();
        if (query.isEmpty() && status.equalsIgnoreCase("All")) {
            filteredsubsidy.addAll(subsidylist);
        } else if (query.isEmpty()) {
            filteredsubsidy.addAll(subsidylist.stream()
                    .filter(user -> user.getSubsidyStatus().equalsIgnoreCase(status))
                    .collect(Collectors.toList()));
        } else if (status.equalsIgnoreCase("All")) {
            filteredsubsidy.addAll(subsidylist.stream()
                    .filter(user -> user.getFullName().toLowerCase(Locale.getDefault()).contains(query))
                    .collect(Collectors.toList()));
        } else {
            filteredsubsidy.addAll(subsidylist.stream()
                    .filter(user -> user.getFullName().toLowerCase(Locale.getDefault()).contains(query)
                            && user.getSubsidyStatus().equalsIgnoreCase(status))
                    .collect(Collectors.toList()));
        }
        adapter.notifyDataSetChanged();
    }

}
