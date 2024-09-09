package com.example.project;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.project.Data.AddGardenActivity;
import com.example.project.Data.FilterActivity;
import com.example.project.Data.ListActivity;
import com.example.project.Data.SavesActivity;
import com.example.project.Managment.Garden;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int FILTER_REQUEST_CODE = 1;
    private MaterialButton main_BTN_addGarden;
    private TextInputEditText main_SEARCH_garden;
    private MaterialButton main_BTN_findMyLocation;
    private MaterialButton main_IMG_saves;
    private MaterialButton main_IMG_filter;
    private MaterialButton main_IMG_list;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap mMap;
    private List<Garden> gardenList = new ArrayList<>();
    private double latitude;
    private double longitude;
    private final static int REQUEST_CODE = 100;

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
                    gardenList.clear();  // Clear garden list
                    for (DataSnapshot gardenSnapshot : dataSnapshot.getChildren()) {
                        Garden garden = gardenSnapshot.getValue(Garden.class);
                        if (garden != null) {
                            gardenList.add(garden);
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
                Toast.makeText(MainActivity.this, "Failed to load gardens", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        main_SEARCH_garden.setOnClickListener(v -> {
            String query = main_SEARCH_garden.getText().toString().trim();
            if (!query.isEmpty()) {
                searchGarden(query);  // Trigger search when search button is clicked
            } else {
                hideKeyboard();
                main_SEARCH_garden.clearFocus();
            }
        });

        main_SEARCH_garden.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                // Clear text and hide the keyboard when search loses focus
                main_SEARCH_garden.setText("");
                hideKeyboard();
            }
        });

        main_BTN_addGarden.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddGardenActivity.class);
            startActivity(intent);
        });

        main_BTN_findMyLocation.setOnClickListener(v -> getLastLocation());

        main_IMG_saves.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SavesActivity.class);
            startActivity(intent);
        });

        main_IMG_filter.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FilterActivity.class);
            startActivityForResult(intent, FILTER_REQUEST_CODE);
        });

        main_IMG_list.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ListActivity.class);
            startActivity(intent);
        });
    }

    private void searchGarden(String query) {
        for (Garden garden : gardenList) {
            if (garden.getName().equalsIgnoreCase(query)) {
                // Move the camera to the selected garden
                LatLng gardenLocation = new LatLng(garden.getLatitude(), garden.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gardenLocation, 15));
                mMap.addMarker(new MarkerOptions().position(gardenLocation).title(garden.getName()));

                Toast.makeText(MainActivity.this, "Found: " + garden.getName(), Toast.LENGTH_SHORT).show();

                // Hide the keyboard after search
                hideKeyboard();

                // Clear focus from the search bar to prevent keyboard from reappearing
                main_SEARCH_garden.clearFocus();

                return;
            }
        }

        Toast.makeText(MainActivity.this, "Garden not found", Toast.LENGTH_SHORT).show();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILTER_REQUEST_CODE && resultCode == RESULT_OK) {
            int selectedDistance = data.getIntExtra("distance", 10);  // Default distance
            int selectedRating = data.getIntExtra("rating", 3);       // Default rating
            boolean hasBenches = data.getBooleanExtra("benches",false);
            boolean hasKiosk = data.getBooleanExtra("kiosk",false);
            boolean hasFitnessFacilities = data.getBooleanExtra("fitnessFacilities", false);
            boolean hasCarrousel = data.getBooleanExtra("carrousel", false);
            boolean hasSlide = data.getBooleanExtra("slide", false);
            boolean hasSwings = data.getBooleanExtra("swings", false);
            boolean hasFountain = data.getBooleanExtra("fountain", false);
            boolean hasLawn = data.getBooleanExtra("lawn", false);
            boolean hasFacilities0To3 = data.getBooleanExtra("facilities_0_3", false);
            boolean hasFacilities4To8 = data.getBooleanExtra("facilities_4_8", false);

            // Apply the filters to the garden list
            applyFilters(selectedDistance, selectedRating,hasBenches,hasKiosk, hasFitnessFacilities, hasCarrousel, hasSlide, hasSwings, hasFountain, hasLawn, hasFacilities0To3, hasFacilities4To8);
        }
    }

    private void applyFilters(int distance, int rating, boolean hasBenches, boolean hasKiosk, boolean hasFitnessFacilities,
                              boolean hasCarrousel, boolean hasSlide, boolean hasSwings, boolean hasFountain,
                              boolean hasLawn, boolean hasFacilities0To3, boolean hasFacilities4To8) {

        List<Garden> filteredGardens = new ArrayList<>();

        Log.d("FilterDebug", "Starting filter process...");

        for (Garden garden : gardenList) {
            // Step 1: Calculate the distance between the user and the garden
            double gardenDistance = calculateDistance(garden.getLatitude(), garden.getLongitude());

            // Step 2: Check if the garden matches the distance and rating filters
            if (gardenDistance <= distance && garden.getRating() >= rating) {
                // Step 3: Get the garden's facilities safely, default to an empty list if null
                List<String> facilities = garden.getFacilities() != null ? garden.getFacilities() : new ArrayList<>();

                // Log the facilities for debugging
                Log.d("GardenFacilities", "Garden: " + garden.getName() + ", Facilities: " + facilities);

                // Step 4: Check if at least one selected facility matches the garden's facilities
                boolean matchFacilities = false;

                List<String> selectedFacilities = new ArrayList<>();
                if (hasBenches) selectedFacilities.add("benches");
                if (hasKiosk) selectedFacilities.add("kiosk");
                if (hasFitnessFacilities) selectedFacilities.add("fitness facilities");
                if (hasCarrousel) selectedFacilities.add("carrousel");
                if (hasSlide) selectedFacilities.add("slide");
                if (hasSwings) selectedFacilities.add("swings");
                if (hasFountain) selectedFacilities.add("fountain");
                if (hasLawn) selectedFacilities.add("lawn");
                if (hasFacilities0To3) selectedFacilities.add("facilities for 0-3");
                if (hasFacilities4To8) selectedFacilities.add("facilities for 4-8");

                for (String facility : selectedFacilities) {
                    if (facilities.contains(facility)) {
                        matchFacilities = true;
                        Log.d("FilterDebug", "Facility match found: " + facility + " in " + garden.getName());
                        break; // We only need one match
                    }
                }

                // Step 5: If a match is found, or if no specific facilities were selected
                if (matchFacilities || selectedFacilities.isEmpty()) {
                    filteredGardens.add(garden);
                    Log.d("FilterDebug", "Added garden: " + garden.getName());
                } else {
                    Log.d("FilterDebug", "Garden " + garden.getName() + " does not match any selected facilities.");
                }
            }
        }

        // Log the final filtered garden count
        Log.d("FilterDebug", "Total gardens after filter: " + filteredGardens.size());

        // Update the map with the filtered gardens
        updateMapWithFilteredGardens(filteredGardens);
    }

    private double calculateDistance(double gardenLatitude, double gardenLongitude) {
        float[] results = new float[1];
        Location.distanceBetween(latitude, longitude, gardenLatitude, gardenLongitude, results);  // Uses user’s latitude and longitude
        return results[0] / 1000;  // Return distance in kilometers
    }

    private void updateMapWithFilteredGardens(List<Garden> filteredGardens) {
        if (mMap != null) {
            mMap.clear();  // Clear existing markers

            for (Garden garden : filteredGardens) {
                LatLng gardenLocation = new LatLng(garden.getLatitude(), garden.getLongitude());
                mMap.addMarker(new MarkerOptions().position(gardenLocation).title(garden.getName()));
            }

            if (!filteredGardens.isEmpty()) {
                LatLng firstLocation = new LatLng(filteredGardens.get(0).getLatitude(), filteredGardens.get(0).getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 12));
            }
        }
    }
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == FILTER_REQUEST_CODE && resultCode == RESULT_OK) {
//            int selectedDistance = data.getIntExtra("distance", 10);  // Default distance
//            int selectedRating = data.getIntExtra("rating", 3);       // Default rating
//            boolean hasBenches = data.getBooleanExtra("benches", false);
//            boolean hasKiosk = data.getBooleanExtra("kiosk", false);
//            boolean hasFitnessFacilities = data.getBooleanExtra("fitnessFacilities", false);
//            boolean hasCarrousel = data.getBooleanExtra("carrousel", false);
//            boolean hasSlide = data.getBooleanExtra("slide", false);
//            boolean hasSwings = data.getBooleanExtra("swings", false);
//            boolean hasFountain = data.getBooleanExtra("fountain", false);
//            boolean hasLawn = data.getBooleanExtra("lawn", false);
//            boolean hasFacilities0To3 = data.getBooleanExtra("facilities_0_3", false);
//            boolean hasFacilities4To8 = data.getBooleanExtra("facilities_4_8", false);
//
//            applyFilters(selectedDistance, selectedRating, hasBenches, hasKiosk, hasFitnessFacilities, hasCarrousel, hasSlide, hasSwings, hasFountain, hasLawn, hasFacilities0To3, hasFacilities4To8);
//        }
//    }
//
//    private void applyFilters(int distance, int rating, boolean hasBenches, boolean hasKiosk, boolean hasFitnessFacilities,
//                              boolean hasCarrousel, boolean hasSlide, boolean hasSwings, boolean hasFountain,
//                              boolean hasLawn, boolean hasFacilities0To3, boolean hasFacilities4To8) {
//
//        List<Garden> filteredGardens = new ArrayList<>();
//
//        Log.d("FilterDebug", "Starting filter process...");
//
//        for (Garden garden : gardenList) {
//            // Step 1: Calculate the distance between the user and the garden
//            double gardenDistance = calculateDistance(garden.getLatitude(), garden.getLongitude());
//
//            // Step 2: Check if the garden matches the distance and rating filters
//            if (gardenDistance <= distance && garden.getRating() >= rating) {
//                List<String> facilities = garden.getFacilities() != null ? garden.getFacilities() : new ArrayList<>();
//
//                // Step 3: Check if at least one selected facility matches the garden's facilities
//                boolean matchFacilities = true; // Set to true by default, change based on selected filters
//
//                if (hasBenches && !facilities.contains("benches")) matchFacilities = false;
//                if (hasKiosk && !facilities.contains("kiosk")) matchFacilities = false;
//                if (hasFitnessFacilities && !facilities.contains("fitness facilities")) matchFacilities = false;
//                if (hasCarrousel && !facilities.contains("carrousel")) matchFacilities = false;
//                if (hasSlide && !facilities.contains("slide")) matchFacilities = false;
//                if (hasSwings && !facilities.contains("swings")) matchFacilities = false;
//                if (hasFountain && !facilities.contains("fountain")) matchFacilities = false;
//                if (hasLawn && !facilities.contains("lawn")) matchFacilities = false;
//                if (hasFacilities0To3 && !facilities.contains("facilities for 0-3")) matchFacilities = false;
//                if (hasFacilities4To8 && !facilities.contains("facilities for 4-8")) matchFacilities = false;
//
//                // Step 4: Add the garden to the filtered list if it matches the criteria
//                if (matchFacilities) {
//                    filteredGardens.add(garden);
//                    Log.d("FilterDebug", "Added garden: " + garden.getName());
//                } else {
//                    Log.d("FilterDebug", "Garden " + garden.getName() + " does not match selected facilities.");
//                }
//            }
//        }
//
//        Log.d("FilterDebug", "Total gardens after filter: " + filteredGardens.size());
//
//        // Update the map with the filtered gardens
//        updateMapWithFilteredGardens(filteredGardens);
//    }
//
//
////    private void applyFilters(int distance, int rating, boolean hasBenches, boolean hasKiosk, boolean hasFitnessFacilities,
////                              boolean hasCarrousel, boolean hasSlide, boolean hasSwings, boolean hasFountain,
////                              boolean hasLawn, boolean hasFacilities0To3, boolean hasFacilities4To8) {
////
////        List<Garden> filteredGardens = new ArrayList<>();
////
////        Log.d("FilterDebug", "Starting filter process...");
////
////        for (Garden garden : gardenList) {
////            double gardenDistance = calculateDistance(garden.getLatitude(), garden.getLongitude());
////
////            if (gardenDistance <= distance && garden.getRating() >= rating) {
////                List<String> facilities = garden.getFacilities() != null ? garden.getFacilities() : new ArrayList<>();
////
////                boolean matchFacilities = false;
////
////                List<String> selectedFacilities = new ArrayList<>();
////                if (hasBenches) selectedFacilities.add("benches");
////                if (hasKiosk) selectedFacilities.add("kiosk");
////                if (hasFitnessFacilities) selectedFacilities.add("fitness facilities");
////                if (hasCarrousel) selectedFacilities.add("carrousel");
////                if (hasSlide) selectedFacilities.add("slide");
////                if (hasSwings) selectedFacilities.add("swings");
////                if (hasFountain) selectedFacilities.add("fountain");
////                if (hasLawn) selectedFacilities.add("lawn");
////                if (hasFacilities0To3) selectedFacilities.add("facilities for 0-3");
////                if (hasFacilities4To8) selectedFacilities.add("facilities for 4-8");
////
////                for (String facility : selectedFacilities) {
////                    if (facilities.contains(facility)) {
////                        matchFacilities = true;
////                        Log.d("FilterDebug", "Facility match found: " + facility + " in " + garden.getName());
////                        break;
////                    }
////                }
////
////                if (matchFacilities || selectedFacilities.isEmpty()) {
////                    filteredGardens.add(garden);
////                    Log.d("FilterDebug", "Added garden: " + garden.getName());
////                } else {
////                    Log.d("FilterDebug", "Garden " + garden.getName() + " does not match any selected facilities.");
////                }
////            }
////        }
////
////        Log.d("FilterDebug", "Total gardens after filter: " + filteredGardens.size());
////
////        updateMapWithFilteredGardens(filteredGardens);
////    }
//
//    private double calculateDistance(double gardenLatitude, double gardenLongitude) {
//        float[] results = new float[1];
//        Location.distanceBetween(latitude, longitude, gardenLatitude, gardenLongitude, results);  // Uses user’s latitude and longitude
//        return results[0] / 1000;  // Return distance in kilometers
//    }
//
//    private void updateMapWithFilteredGardens(List<Garden> filteredGardens) {
//        if (mMap != null) {
//            mMap.clear();
//
//            for (Garden garden : filteredGardens) {
//                LatLng gardenLocation = new LatLng(garden.getLatitude(), garden.getLongitude());
//                mMap.addMarker(new MarkerOptions().position(gardenLocation).title(garden.getName()));
//            }
//
//            if (!filteredGardens.isEmpty()) {
//                LatLng firstLocation = new LatLng(filteredGardens.get(0).getLatitude(), filteredGardens.get(0).getLongitude());
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 12));
//            }
//        }
//    }

    private void findViews() {
        main_BTN_addGarden = findViewById(R.id.main_BTN_addGarden);
        main_SEARCH_garden = findViewById(R.id.main_SEARCH_garden);
        main_BTN_findMyLocation = findViewById(R.id.main_BTN_findMyLocation);
        main_IMG_saves = findViewById(R.id.main_IMG_saves);
        main_IMG_filter = findViewById(R.id.main_IMG_filter);
        main_IMG_list = findViewById(R.id.main_IMG_list);
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
