package com.example.project;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.project.Data.AddGardenActivity;
import com.example.project.Data.FilterActivity;
import com.example.project.Data.ListActivity;
import com.example.project.Data.SavesActivity;
import com.example.project.Management.Garden;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int FILTER_REQUEST_CODE = 1;
    private final static int REQUEST_CODE = 100;

    private MaterialButton main_BTN_addGarden;
    private TextInputEditText main_SEARCH_garden;
    private MaterialButton main_BTN_findMyLocation;
    private MaterialButton main_IMG_saves;
    private MaterialButton main_IMG_filter;
    private MaterialButton main_IMG_list;
    private MaterialButton header_action;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap mMap;
    private final List<Garden> GARDEN_LIST = new ArrayList<>();
    private double latitude;
    private double longitude;



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
    public void onMapReady(@NonNull GoogleMap googleMap) {
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
                    GARDEN_LIST.clear();  // Clear garden list
                    for (DataSnapshot gardenSnapshot : dataSnapshot.getChildren()) {
                        Garden garden = gardenSnapshot.getValue(Garden.class);
                        if (garden != null) {
                            GARDEN_LIST.add(garden);
                            LatLng gardenLocation = new LatLng(garden.getLatitude(), garden.getLongitude());

                            mMap.addMarker(new MarkerOptions()
                                    .position(gardenLocation)
                                    .title(garden.getName())
                                    .icon(BitmapFromVector
                                            (getApplicationContext(), R.drawable.pin)));

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

    private BitmapDescriptor
    BitmapFromVector(Context context, int vectorResId)
    {

        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        assert vectorDrawable != null;
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap
                (vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }




    private void initView() {
        main_SEARCH_garden.setOnClickListener(v -> {
            String query = Objects.requireNonNull(main_SEARCH_garden.getText()).toString().trim();
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

        //click to add garden
        main_BTN_addGarden.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddGardenActivity.class);
            startActivity(intent);
        });


        //click our location
        main_BTN_findMyLocation.setOnClickListener(v -> getLastLocation());

        main_IMG_saves.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SavesActivity.class);
            startActivity(intent);
        });

        //click to the filter
        main_IMG_filter.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FilterActivity.class);
            startActivityForResult(intent, FILTER_REQUEST_CODE);
        });

        //click to the garden list
        main_IMG_list.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ListActivity.class);
            startActivity(intent);
        });

        //click for sign out
        header_action.setOnClickListener(v -> showSignOutPopup());
    }

    private void showSignOutPopup() {
        // Create an AlertDialog for confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sign Out");
        builder.setMessage("Are you sure you want to sign out?");

        // Sign Out button
        builder.setPositiveButton("Sign Out", (dialog, which) -> signOutUser());
        // Cancel button
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });
        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }




    private void signOutUser() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(task -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();  // Close MainActivity after sign-out
        });
    }


    private void searchGarden(String query) {
        for (Garden garden : GARDEN_LIST) {
            if (garden.getName().equalsIgnoreCase(query)) {
                // Move the camera to the selected garden
                LatLng gardenLocation = new LatLng(garden.getLatitude(), garden.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gardenLocation, 15));
                mMap.addMarker(new MarkerOptions()
                        .position(gardenLocation).
                        title(garden.getName())
                        .icon(BitmapFromVector
                                (getApplicationContext(), R.drawable.pin)));

                Toast.makeText(MainActivity.this, "Found: " + garden.getName(), Toast.LENGTH_SHORT).show();

                // Hide the keyboard after search
                hideKeyboard();

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

        for (Garden garden : GARDEN_LIST) {
            double gardenDistance = calculateDistance(garden.getLatitude(), garden.getLongitude());

            if (gardenDistance <= distance && garden.getRating() >= rating) {
                List<String> facilities = garden.getFacilities() != null ? garden.getFacilities() : new ArrayList<>();


                // Log the facilities for debugging
                Log.d("GardenFacilities", "Garden: " + garden.getName() + ", Facilities: " + facilities);

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
                mMap.addMarker(new MarkerOptions()
                        .position(gardenLocation)
                        .title(garden.getName())
                        .icon(BitmapFromVector(getApplicationContext(), R.drawable.pin)));
            }

            if (!filteredGardens.isEmpty()) {
                LatLng firstLocation = new LatLng(filteredGardens.get(0).getLatitude(), filteredGardens.get(0).getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 12));
            }
        }
    }

    private void findViews() {
        main_BTN_addGarden = findViewById(R.id.main_BTN_addGarden);
        main_SEARCH_garden = findViewById(R.id.main_SEARCH_garden);
        main_BTN_findMyLocation = findViewById(R.id.main_BTN_findMyLocation);
        main_IMG_saves = findViewById(R.id.main_IMG_saves);
        main_IMG_filter = findViewById(R.id.main_IMG_filter);
        main_IMG_list = findViewById(R.id.main_IMG_list);
        header_action = findViewById(R.id.header_action);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
