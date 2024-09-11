package com.example.project.Data;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.Management.Garden;
import com.example.project.Management.GardenAdapter;
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

        gardenAdapter.setGardenCallback(new GardenCallback() {
                                            @Override
                                            public void onGardenClick(Garden garden) {
                                            //pass
                                            }

                                            @Override
                                            public void favoriteButtonClicked(Garden garden, int position) {
                                                garden.setFavorite(!garden.isFavorite());

                                                // Update the favorite status in Firebase
                                                updateFavoriteStatusInFirebase(garden);

                                                // Notify the adapter that the item has changed
                                                gardenAdapter.notifyItemChanged(position);
                                            }
                                        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getUserLocation();

        loadFavoriteGardenFromFirebase();


    }

    private void updateFavoriteStatusInFirebase(Garden garden) {
            DatabaseReference gardenRef = FirebaseDatabase.getInstance().getReference("Garden").child(garden.getId());
            gardenRef.child("favorite").setValue(garden.isFavorite());
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
            @SuppressLint("NotifyDataSetChanged")
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