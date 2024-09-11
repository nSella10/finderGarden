package com.example.project.Data;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project.Management.Garden;
import com.example.project.Management.GardenAdapter;
import com.example.project.Management.GardenDetailActivity;
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
import java.util.Comparator;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private GardenAdapter gardenAdapter;
    private List<Garden> gardenList;
    private FusedLocationProviderClient fusedLocationClient;
    private double userLatitude, userLongitude;
    private boolean userLocationAvailable = false;  // Add this flag to check if user location is available

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        RecyclerView recyclerView = findViewById(R.id.garden_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        gardenList = new ArrayList<>();
        gardenAdapter = new GardenAdapter(gardenList);

        // Initialize garden callback
        gardenAdapter.setGardenCallback(new GardenCallback() {
            @Override
            public void onGardenClick(Garden garden) {
                Intent intent = new Intent(ListActivity.this, GardenDetailActivity.class);
                intent.putExtra("garden_id", garden.getId());
                intent.putExtra("garden_name", garden.getName());
                intent.putExtra("garden_description", garden.getDescription());
                String distance = gardenAdapter.calculateDistance(garden.getLatitude(), garden.getLongitude());
                intent.putExtra("garden_distance", distance);
                intent.putExtra("garden_image_url", garden.getImageUrl());
                intent.putExtra("garden_rating", (float) garden.getRating());
                List<String> facilities = garden.getFacilities();
                if (facilities != null) {
                    String[] facilitiesArray = facilities.toArray(new String[0]);
                    intent.putExtra("garden_facilities", facilitiesArray);
                } else {
                    intent.putExtra("garden_facilities", new String[0]);
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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getUserLocation();

        loadDataFromFirebase();
    }

    private void getUserLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    userLatitude = location.getLatitude();
                    userLongitude = location.getLongitude();
                    userLocationAvailable = true;
                    gardenAdapter.setUserLocation(userLatitude, userLongitude);  // Pass the location to the adapter
                    loadDataFromFirebase();  // Reload data after getting location
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void updateGardenFavoriteStatusInDatabase(@NonNull Garden garden) {
        DatabaseReference gardenRef = FirebaseDatabase.getInstance().getReference("Garden").child(garden.getId());
        gardenRef.child("favorite").setValue(garden.isFavorite());
    }

    private void loadDataFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference gardenRef = database.getReference("Garden");

        gardenRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gardenList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Garden garden = snapshot.getValue(Garden.class);
                    if (garden != null) {
                        // If user location is available, calculate the distance
                        if (userLocationAvailable) {
                            double distance = calculateDistance(userLatitude, userLongitude, garden.getLatitude(), garden.getLongitude());
                            garden.setDistanceFromUser(distance);
                        }
                        gardenList.add(garden);
                    }
                }

                // Sort the garden list by distance
                if (userLocationAvailable) {
                    gardenList.sort(Comparator.comparingDouble(Garden::getDistanceFromUser));
                }

                gardenAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }



    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0] / 1000;  // Convert meters to kilometers
    }

}
