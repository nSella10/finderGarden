package com.example.project.Data;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import com.airbnb.lottie.LottieAnimationView;
import com.example.project.Management.CustomScrollView;
import com.example.project.Management.FacilitiesAdapter;
import com.example.project.Management.Garden;
import com.example.project.R;
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
import java.util.Objects;

public class AddGardenActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "AddGardenActivity";
    private static final int REQUEST_CODE = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private MaterialButton main_BTN_findMyLocation;

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

    private double latitude;
    private double longitude;

    private RecyclerView recyclerFacilities;
    private final List<String> FACILITIES_LIST = Arrays.asList("carrousel", "fitness facilities", "kiosk", "benches", "slide", "swings", "fountain", "lawn", "facilities for 0-3", "facilities 4-8");
    private final List<String> SELECTED_FACILITIES = new ArrayList<>();
    private ImageView transparentImageView;
    private LottieAnimationView lottie_LOTTIE_saving;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_garden);

        findViews();
        initViews();

        FacilitiesAdapter facilitiesAdapter = new FacilitiesAdapter(this, FACILITIES_LIST, SELECTED_FACILITIES,true);
        recyclerFacilities.setLayoutManager(new GridLayoutManager(this, 2));  // Grid layout with 2 columns
        recyclerFacilities.setAdapter(facilitiesAdapter);

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
        lottie_LOTTIE_saving = findViewById(R.id.lottie_LOTTIE_saving);
        main_BTN_findMyLocation = findViewById(R.id.main_BTN_findMyLocation);
    }

    private void initViews() {
        main_BTN_findMyLocation.setOnClickListener(v-> getLastLocation());
        addPhoto_BTN_add.setOnClickListener(v -> openCamera());
        addGarden_BTN.setOnClickListener(v -> saveGardenToFirebase());

    }


    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create a file to save the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, "Error occurred while creating the file", ex);
            }

            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(this, "com.example.project.Provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);  // This stores it in /files/Pictures
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save the file path for later use
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }



    private Bitmap resizeBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float bitmapRatio = (float) width / (float) height;

        if (bitmapRatio > 1) {
            width = 600;
            height = (int) (width / bitmapRatio);
        } else {
            height = 400;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }



    private Bitmap handleImageOrientation(File imageFile, Bitmap bitmap) throws IOException {
        ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        return rotateBitmap(bitmap, orientation);
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            default:
                return bitmap;
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    @SuppressLint("ClickableViewAccessibility")
    private void setupTouchListenerForMap() {
        transparentImageView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    mainScrollView.requestDisallowInterceptTouchEvent(true);
                    return false;
                case MotionEvent.ACTION_UP:
                    mainScrollView.requestDisallowInterceptTouchEvent(false);
                    return true;
                default:
                    return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if currentPhotoPath is not null before proceeding
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && currentPhotoPath != null) {
            File imageFile = new File(currentPhotoPath);
            if (imageFile.exists()) {  // Make sure the file exists before trying to process it
                Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                try {
                    // Check and fix image orientation before displaying it
                    bitmap = handleImageOrientation(imageFile, bitmap);
                    bitmap = resizeBitmap(bitmap);  // Resize the bitmap if necessary
                    addGarden_IMG_selected.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            // For image picked from gallery
            Uri selectedImage = data.getData();
            imageUri = selectedImage;
            addGarden_IMG_selected.setImageURI(selectedImage);
        }
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
            lottie_LOTTIE_saving.setVisibility(View.VISIBLE);
            lottie_LOTTIE_saving.playAnimation();
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("images/" + gardenId + ".jpg");
            UploadTask uploadTask = storageRef.putFile(imageUri);

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) throw Objects.requireNonNull(task.getException());
                return storageRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Garden garden = new Garden(gardenId, name, selectedLocation.latitude, selectedLocation.longitude, rating, downloadUri.toString(), description, SELECTED_FACILITIES);
                    myRef.setValue(garden).addOnCompleteListener(saveTask -> {
                        if (saveTask.isSuccessful()) {
                            lottie_LOTTIE_saving.cancelAnimation();
                            lottie_LOTTIE_saving.setVisibility(View.GONE);
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

            //image is null
        } else {
            Garden garden = new Garden(gardenId, name, selectedLocation.latitude, selectedLocation.longitude, rating, null, description, SELECTED_FACILITIES);
            myRef.setValue(garden).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    lottie_LOTTIE_saving.cancelAnimation();
                    lottie_LOTTIE_saving.setVisibility(View.GONE);

                    Toast.makeText(this, "Garden added successfully without image", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to add garden", Toast.LENGTH_SHORT).show();
                    addGarden_BTN.setEnabled(true);
                }
            });
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng defaultLocation = new LatLng(31.776, 35.234);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12));

        mMap.setOnMapClickListener(latLng -> {
            selectedLocation = latLng;
            mMap.clear();
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Selected Garden Location")
                    .icon(BitmapFromVector
                            (getApplicationContext(), R.drawable.pin)));
        });

        getLastLocation();
    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId)
    {
        Drawable vectorDrawable = ContextCompat.getDrawable(
                context, vectorResId);

        assert vectorDrawable != null;
        vectorDrawable.setBounds(
                0, 0, vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(
                vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    private void askPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
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

}


