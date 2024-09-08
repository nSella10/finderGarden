package com.example.project.Data;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project.Managment.Garden;
import com.example.project.Managment.GardenAdapter;
import com.example.project.Managment.GardenDetailActivity;
import com.example.project.R;
import com.example.project.interfaces.GardenCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private GardenAdapter gardenAdapter;
    private List<Garden> gardenList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        RecyclerView recyclerView = findViewById(R.id.garden_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        gardenList = new ArrayList<>();
        gardenAdapter = new GardenAdapter(gardenList);
        gardenAdapter.setGardenCallback(new GardenCallback() {
            @Override
            public void onGardenClick(Garden garden) {
                Intent intent = new Intent(ListActivity.this, GardenDetailActivity.class);

                intent.putExtra("garden_id", garden.getId());
                intent.putExtra("garden_name", garden.getName());
                intent.putExtra("garden_description", garden.getDescription());
                intent.putExtra("garden_distance", garden.getLatitude() + ", " + garden.getLongitude());
                intent.putExtra("garden_image_url", garden.getImageUrl());
                intent.putExtra("garden_rating",(float) garden.getRating());
                List<String> facilities = garden.getFacilities();
                if (facilities != null) {
                    String[] facilitiesArray = facilities.toArray(new String[0]); // Converting list to array
                    intent.putExtra("garden_facilities", facilitiesArray);
                } else {
                    intent.putExtra("garden_facilities", new String[0]); // Pass an empty array if null
                }
                startActivity(intent);
            }

            @Override
            public void favoriteButtonClicked(Garden garden, int position) {
                garden.setFavorite(!garden.isFavorite());
                gardenAdapter.notifyItemChanged(position);  // Update only the changed item

                // Optionally, update the favorite status in the database
                updateGardenFavoriteStatusInDatabase(garden);
            }
        });

        recyclerView.setAdapter(gardenAdapter);

        loadDataFromFirebase();
    }

    private void updateGardenFavoriteStatusInDatabase(Garden garden) {
        DatabaseReference gardenRef = FirebaseDatabase.getInstance().getReference("Garden").child(garden.getId());
        gardenRef.child("favorite").setValue(garden.isFavorite());
    }

    private void loadDataFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference gardenRef = database.getReference("Garden");

        gardenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gardenList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Garden garden = snapshot.getValue(Garden.class);
                    if (garden != null) {
                        gardenList.add(garden);
                    }
                }
                gardenAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }
}
