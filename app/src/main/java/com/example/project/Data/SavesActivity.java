package com.example.project.Data;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.Managment.Garden;
import com.example.project.Managment.GardenAdapter;
import com.example.project.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SavesActivity extends AppCompatActivity {
    private GardenAdapter gardenAdapter;
    private List<Garden> favoriteGardenList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saves);

        RecyclerView recyclerView = findViewById(R.id.garden_favorite_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        favoriteGardenList = new ArrayList<>();
        gardenAdapter = new GardenAdapter(favoriteGardenList);
        recyclerView.setAdapter(gardenAdapter);

        loadFavoriteGardenFromFirebase();


    }

    private void loadFavoriteGardenFromFirebase() {
        DatabaseReference gardenRef = FirebaseDatabase.getInstance().getReference("Garden");

        gardenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favoriteGardenList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Garden garden = snapshot.getValue(Garden.class);
                    if (garden != null && garden.isFavorite()) {
                        favoriteGardenList.add(garden);
                        }
                    }
                    gardenAdapter.notifyDataSetChanged();
                }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}