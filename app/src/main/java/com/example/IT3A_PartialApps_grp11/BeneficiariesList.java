package com.example.IT3A_PartialApps_grp11;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class BeneficiariesList extends AppCompatActivity {

    RecyclerView recView;
    ArrayList<User> datalist;
    ArrayList<User> filteredList;
    FirebaseFirestore fStore;
    myAdapter adapter;
    FloatingActionButton addBeneBtn;
    EditText searchText;
    Button backBtn;
   // ImageButton searchButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beneficiaries_list);

        searchText = findViewById(R.id.searchText);
      //  searchButton = findViewById(R.id.search);

        recView = findViewById(R.id.recView);
        recView.setLayoutManager(new LinearLayoutManager(this));
        datalist = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new myAdapter(filteredList);
        recView.setAdapter(adapter);
        addBeneBtn = findViewById(R.id.addBeneBtn);

        backBtn = findViewById(R.id.backBtn);

        fStore = FirebaseFirestore.getInstance();

        // Fetch data from Firestore
        fStore.collection("Users").whereEqualTo("isResi", "2").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        User user = document.toObject(User.class);
                        if (user != null) {
                            datalist.add(user);
                        }
                    }
                    filteredList.addAll(datalist);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        // Set up ImageButton click listener
     /*   searchButton.setOnClickListener(v -> {
            String query = searchText.getText().toString().trim();
            filterData(query);
        });  */

        // Optional: Add a TextWatcher to the EditText to perform real-time search
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterData(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        addBeneBtn.setOnClickListener(v -> {
            Intent intent = new Intent(BeneficiariesList.this, Register.class);
            startActivity(intent);
        });

        backBtn.setOnClickListener(v -> {
           finish();
        });
    }

    private void filterData(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(datalist);
        } else {
            filteredList.addAll(datalist.stream()
                    .filter(user -> user.getFullName().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList()));
        }
        adapter.notifyDataSetChanged();
    }
}
