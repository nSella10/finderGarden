package com.example.project;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.example.project.Data.AddGardenActivity;
import com.example.project.Data.SavesActivity;
import com.example.project.Managment.Garden;
import com.example.project.Data.ListActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
//    private MaterialButton main_BTN_addGarden;
//    private MaterialButton main_BTN_search;
//    private MaterialButton main_BTN_findMyLocation;
//    private MaterialButton main_IMG_saves;
//    private MaterialButton main_IMG_filter;
//    private MaterialButton main_IMG_list;
//    private double latitude;
//    private double longitude;
//    private final static int REQUEST_CODE = 100;
//    FusedLocationProviderClient fusedLocationProviderClient;
//    private GoogleMap mMap;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        findViews();
//        initView();
//
//        updateTitleFromDB();
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//
//        // Initialize map fragment and set the callback
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        if (mapFragment != null) {
//            mapFragment.getMapAsync(this);
//        }
//    }
//
//    private void updateTitleFromDB() {
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference gardenRef = database.getReference("Garden");
//
//        gardenRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(mMap!= null){
//                    mMap.clear();
//                }
//                for(DataSnapshot gardenSnapshot : dataSnapshot.getChildren()){
//                    Garden garden = gardenSnapshot.getValue(Garden.class);
//                    if(garden != null){
//                        LatLng gardenLocation = new LatLng(garden.getLatitude(), garden.getLongitude());
//                        mMap.addMarker(new MarkerOptions().position(gardenLocation).title(garden.getName()));
//                    }
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                //pass
//            }
//        });
//
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        fetchGardensFromFirebase();
//    }
//
//    private void fetchGardensFromFirebase() {
//    }
//
//
//    private void initView() {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        main_BTN_search.setOnClickListener(v -> {
//            // Implement search functionality here
//        });
//
//        main_BTN_addGarden.setOnClickListener(v -> {
//            // Navigate to AddGardenActivity to add a new garden
//            Intent intent = new Intent(this, AddGardenActivity.class);
//            startActivity(intent);
//        });
//
//        main_BTN_findMyLocation.setOnClickListener(v -> {
//            // Get the user's last known location and move the map's camera to that location
//            getLastLocation();
//        });
//
//        main_IMG_saves.setOnClickListener(v -> {
//            Intent intent = new Intent(this, SavesActivity.class);
//            startActivity(intent);
//
//        });
//
//        main_IMG_filter.setOnClickListener(v -> {
//            // Implement filter functionality here
//        });
//
//        main_IMG_list.setOnClickListener(v -> {
//            // Navigate to ListActivity to show a list of all gardens
//            Intent intent = new Intent(this, ListActivity.class);
//            startActivity(intent);
//        });
//    }
//
//
//    private void savaDataToFirebase() {
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("Garden");
//
//        myRef.setValue(DataManager.createGarden());
//    }
//
//    private void findViews() {
//        main_BTN_search = findViewById(R.id.main_BTN_search);
//        main_BTN_findMyLocation = findViewById(R.id.main_BTN_findMyLocation);
//        main_IMG_saves = findViewById(R.id.main_IMG_saves);
//        main_IMG_filter = findViewById(R.id.main_IMG_filter);
//        main_IMG_list = findViewById(R.id.main_IMG_list);
//        main_BTN_addGarden = findViewById(R.id.main_BTN_addGarden);
//    }
//
//    private void getLastLocation() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            fusedLocationProviderClient.getLastLocation()
//                    .addOnSuccessListener(new OnSuccessListener<Location>() {
//                        @Override
//                        public void onSuccess(Location location) {
//                            if (location != null) {
//                                latitude = location.getLatitude();
//                                longitude = location.getLongitude();
//                                moveCameraToLocation(latitude, longitude);
//                            }
//                        }
//                    });
//        } else {
//            askPermission();
//        }
//    }
//
//    private void moveCameraToLocation(double latitude, double longitude) {
//        if (mMap != null) {
//            LatLng userLocation = new LatLng(latitude, longitude);
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
//            mMap.addMarker(new MarkerOptions().position(userLocation).title("You are here"));
//        }
//    }
//
//    private void askPermission() {
//        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode == REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getLastLocation();
//            } else {
//                Toast.makeText(MainActivity.this, "Please provide the required permission", Toast.LENGTH_SHORT).show();
//            }
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//}

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MaterialButton main_BTN_addGarden;
    private MaterialButton main_BTN_search;
    private MaterialButton main_BTN_findMyLocation;
    private MaterialButton main_IMG_saves;
    private MaterialButton main_IMG_filter;
    private MaterialButton main_IMG_list;
    private double latitude;
    private double longitude;
    private final static int REQUEST_CODE = 100;
    FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        initView();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize map fragment and set the callback
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        fetchGardensFromFirebase();
    }

    private void fetchGardensFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference gardenRef = database.getReference("Garden");

        gardenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mMap != null) {
                    mMap.clear();  // Clear any existing markers

                    LatLng firstGardenLocation = null;
                    for (DataSnapshot gardenSnapshot : dataSnapshot.getChildren()) {
                        Garden garden = gardenSnapshot.getValue(Garden.class);
                        if (garden != null) {
                            LatLng gardenLocation = new LatLng(garden.getLatitude(), garden.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(gardenLocation).title(garden.getName()));

                            if (firstGardenLocation == null) {
                                firstGardenLocation = gardenLocation;
                            }
                        }
                    }

                    // Move camera to the first garden found
                    if (firstGardenLocation != null) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstGardenLocation, 12));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Toast.makeText(MainActivity.this, "Failed to load gardens", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        main_BTN_search.setOnClickListener(v -> {
            // Implement search functionality here
        });

        main_BTN_addGarden.setOnClickListener(v -> {
            // Navigate to AddGardenActivity to add a new garden
            Intent intent = new Intent(MainActivity.this, AddGardenActivity.class);
            startActivity(intent);
        });

        main_BTN_findMyLocation.setOnClickListener(v -> {
            // Get the user's last known location and move the map's camera to that location
            getLastLocation();
        });

        main_IMG_saves.setOnClickListener(v -> {
            Intent intent = new Intent(this, SavesActivity.class);
            startActivity(intent);
        });

        main_IMG_filter.setOnClickListener(v -> {
            // Implement filter functionality here
        });

        main_IMG_list.setOnClickListener(v -> {
            // Navigate to ListActivity to show a list of all gardens
            Intent intent = new Intent(this, ListActivity.class);
            startActivity(intent);
        });
    }

    private void findViews() {
        main_BTN_search = findViewById(R.id.main_BTN_search);
        main_BTN_findMyLocation = findViewById(R.id.main_BTN_findMyLocation);
        main_IMG_saves = findViewById(R.id.main_IMG_saves);
        main_IMG_filter = findViewById(R.id.main_IMG_filter);
        main_IMG_list = findViewById(R.id.main_IMG_list);
        main_BTN_addGarden = findViewById(R.id.main_BTN_addGarden);
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            moveCameraToLocation(latitude, longitude);
                        }
                    });
        } else {
            askPermission();
        }
    }

    private void moveCameraToLocation(double latitude, double longitude) {
        if (mMap != null) {
            LatLng userLocation = new LatLng(latitude, longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
            mMap.addMarker(new MarkerOptions().position(userLocation).title("You are here"));
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(MainActivity.this, "Please provide the required permission", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
