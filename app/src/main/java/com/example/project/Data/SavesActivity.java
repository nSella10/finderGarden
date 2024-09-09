package com.example.project.Data;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.Managment.Garden;
import com.example.project.Managment.GardenAdapter;
import com.example.project.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
    private FusedLocationProviderClient fusedLocationClient;
    private double userLatitude,userLongitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saves);

        RecyclerView recyclerView = findViewById(R.id.garden_favorite_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        favoriteGardenList = new ArrayList<>();
        gardenAdapter = new GardenAdapter(favoriteGardenList);
        recyclerView.setAdapter(gardenAdapter);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getUserLocation();

        loadFavoriteGardenFromFirebase();


    }

    private void getUserLocation() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    userLatitude = location.getLatitude();
                    userLongitude = location.getLongitude();
                    gardenAdapter.setUserLocation(userLatitude, userLongitude);
                }
            });
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
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