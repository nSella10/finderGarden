
package com.example.project.Data;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.Managment.CustomScrollView;
import com.example.project.Managment.FacilitiesAdapter;
import com.example.project.Managment.Garden;
import com.example.project.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddGardenActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "AddGardenActivity";
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int REQUEST_CODE = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    private CustomScrollView mainScrollView;
    private EditText addGarden_EDT_name, addGarden_EDT_description;
    private RatingBar addGarden_RTG_rating;
    private ShapeableImageView addGarden_IMG_selected;
    private MaterialButton addPhoto_BTN_add, addGarden_BTN;
    private GoogleMap mMap;
    private LatLng selectedLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private Uri imageUri;
    private String currentPhotoPath;

    private RecyclerView recyclerFacilities;
    private FacilitiesAdapter facilitiesAdapter;
    private List<String> facilitiesList = Arrays.asList("carrousel", "fitness facilities", "kiosk", "Benches", "slide", "swings", "fountain", "lawn", "facilities for 0-3", "facilities 4-8");
    private List<String> selectedFacilities = new ArrayList<>();
    private ImageView transparentImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_garden);

        findViews();
        initViews();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.addGarden_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        setupTouchListenerForMap();
    }

    private void findViews() {
        addGarden_EDT_name = findViewById(R.id.addGarden_EDT_name);
        addGarden_EDT_description = findViewById(R.id.addGarden_EDT_description);
        addGarden_RTG_rating = findViewById(R.id.addGarden_RTG_rating);
        addGarden_IMG_selected = findViewById(R.id.addGarden_IMG_selected);
        addPhoto_BTN_add = findViewById(R.id.addPhoto_BTN_add);
        addGarden_BTN = findViewById(R.id.addGarden_BTN);
        recyclerFacilities = findViewById(R.id.addGarden_recycler_facilities);
        mainScrollView = findViewById(R.id.mainScrollView);
        transparentImageView = findViewById(R.id.imagetrans);
    }

    private void initViews() {
        addPhoto_BTN_add.setOnClickListener(v -> checkCameraPermissionAndOpenCamera());
        addGarden_BTN.setOnClickListener(v -> saveGardenToFirebase());

        facilitiesAdapter = new FacilitiesAdapter(this, facilitiesList, selectedFacilities);
        recyclerFacilities.setLayoutManager(new GridLayoutManager(this, 2));  // Grid layout with 2 columns
        recyclerFacilities.setAdapter(facilitiesAdapter);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupTouchListenerForMap() {
        transparentImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Disable scrolling of ScrollView when the map is touched
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;
                    case MotionEvent.ACTION_UP:
                        // Re-enable scrolling after interaction with the map
                        mainScrollView.requestDisallowInterceptTouchEvent(false);
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;
                    default:
                        return true;
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng defaultLocation = new LatLng(31.776, 35.234);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12));

        mMap.setOnMapClickListener(latLng -> {
            selectedLocation = latLng;
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Garden Location"));
        });

        getLastLocation();
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    selectedLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    moveCameraToLocation(selectedLocation.latitude, selectedLocation.longitude);
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
            mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Current Location"));
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    private void checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            openImagePicker();
        }
    }

    private void openImagePicker() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, "Error occurred while creating the file", ex);
            }
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(this, "com.example.project.provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(null);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            Bitmap resizedBitmap = resizeBitmap(bitmap, 600, 400);
            addGarden_IMG_selected.setImageBitmap(resizedBitmap);
        }
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    private void saveGardenToFirebase() {
        addGarden_BTN.setEnabled(false);

        if (selectedLocation == null) {
            Toast.makeText(this, "Please select a location on the map", Toast.LENGTH_SHORT).show();
            addGarden_BTN.setEnabled(true);
            return;
        }

        String name = addGarden_EDT_name.getText().toString().trim();
        String description = addGarden_EDT_description.getText().toString().trim();
        float rating = addGarden_RTG_rating.getRating();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please fill in the garden name", Toast.LENGTH_SHORT).show();
            addGarden_BTN.setEnabled(true);
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Garden").push();
        String gardenId = myRef.getKey();

        if (imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("images/" + gardenId + ".jpg");
            UploadTask uploadTask = storageRef.putFile(imageUri);

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) throw task.getException();
                return storageRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Garden garden = new Garden(gardenId, name, selectedLocation.latitude, selectedLocation.longitude, rating, downloadUri.toString(), description, selectedFacilities);
                    myRef.setValue(garden).addOnCompleteListener(saveTask -> {
                        if (saveTask.isSuccessful()) {
                            Toast.makeText(AddGardenActivity.this, "Garden added successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddGardenActivity.this, "Failed to add garden", Toast.LENGTH_SHORT).show();
                            addGarden_BTN.setEnabled(true);
                        }
                    });
                } else {
                    addGarden_BTN.setEnabled(true);
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                addGarden_BTN.setEnabled(true);
            });
        } else {
            Garden garden = new Garden(gardenId, name, selectedLocation.latitude, selectedLocation.longitude, rating, null, description, selectedFacilities);
            myRef.setValue(garden).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Garden added successfully without image", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to add garden", Toast.LENGTH_SHORT).show();
                    addGarden_BTN.setEnabled(true);
                }
            });
        }
    }
}
