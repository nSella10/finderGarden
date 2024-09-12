package com.example.project.Management;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.project.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class GardenDetailActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final String TAG = "GardenDetailActivity";
    private ShapeableImageView garden_detail_image;
    private TextInputEditText garden_detail_name;
    private MaterialTextView garden_detail_facilities;
    private MaterialTextView garden_detail_distance;
    private TextInputEditText  garden_detail_description;
    private AppCompatRatingBar garden_detail_rating;
    private MaterialButton edit_button;
    private MaterialButton save_button;
    private MaterialButton update_image_button;
    private String gardenId;
    private Uri imageUri;
    private String currentPhotoPath;
    private boolean isImageChanged = false; // Flag to track if image is changed

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garden_detail);

        findView();

        garden_detail_rating.setIsIndicator(true);

        // Get the garden details from the intent
        Intent intent = getIntent();
        if (intent != null) {
            gardenId = intent.getStringExtra("garden_id");
            String name = intent.getStringExtra("garden_name");
            String description = intent.getStringExtra("garden_description");
            String[] facilitiesArray = intent.getStringArrayExtra("garden_facilities");
            String distance = intent.getStringExtra("garden_distance");
            String imageUrl = intent.getStringExtra("garden_image_url");
            float rating = intent.getFloatExtra("garden_rating", 0f);

            // Set the data to views
            garden_detail_name.setText(name != null ? name : "No name available");
            garden_detail_description.setText(description != null ? description : "No description available");
            garden_detail_distance.setText(distance != null ? distance : "Location not available");

            // Handle null facilities array safely
            if (facilitiesArray != null) {
                String facilities = String.join(", ", facilitiesArray);
                garden_detail_facilities.setText(facilities);
            } else {
                garden_detail_facilities.setText("No facilities listed");
            }

            garden_detail_rating.setRating(rating);

            // Load the image using Glide
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this).load(imageUrl).into(garden_detail_image);
            } else {
                garden_detail_image.setImageResource(R.drawable.unavailable_photo);
            }
        }

        // Button to capture or pick image
        update_image_button.setOnClickListener(v -> openCamera());

        edit_button.setOnClickListener(v -> enableEditing(true));

        save_button.setVisibility(View.INVISIBLE);
        save_button.setOnClickListener(v -> saveUpdatedGardenToFirebase());
    }

    private void findView() {
        // Initialize views
        garden_detail_image = findViewById(R.id.garden_detail_image);
        garden_detail_name = findViewById(R.id.garden_detail_name);
        garden_detail_facilities = findViewById(R.id.garden_detail_facilities);
        garden_detail_distance = findViewById(R.id.garden_detail_distance);
        garden_detail_description = findViewById(R.id.garden_detail_description);
        garden_detail_rating = findViewById(R.id.garden_detail_rating);
        edit_button = findViewById(R.id.edit_button);
        save_button = findViewById(R.id.save_button);
        update_image_button = findViewById(R.id.update_image_button);
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

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && currentPhotoPath != null) {
            File imageFile = new File(currentPhotoPath);
            if (imageFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                try {
                    bitmap = handleImageOrientation(imageFile, bitmap);
                    bitmap = resizeBitmap(bitmap);
                    garden_detail_image.setImageBitmap(bitmap);
                    isImageChanged = true; // Mark image as changed
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            imageUri = selectedImage;
            garden_detail_image.setImageURI(selectedImage);
            isImageChanged = true; // Mark image as changed
        }
    }

    private void enableEditing(boolean enable) {
        garden_detail_name.setEnabled(enable);
        garden_detail_description.setEnabled(enable);
        garden_detail_rating.setIsIndicator(!enable);
        update_image_button.setEnabled(enable);

        if (enable) {
            save_button.setVisibility(View.VISIBLE);
            edit_button.setVisibility(View.GONE);
        } else {
            edit_button.setVisibility(View.VISIBLE);
            save_button.setVisibility(View.GONE);
        }

        edit_button.setOnClickListener(v -> enableEditing(true));
        save_button.setOnClickListener(v -> {
            saveUpdatedGardenToFirebase();
        });
    }

    private void uploadImageAndSaveUri(DatabaseReference gardenRef) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("images/" + gardenId + ".jpg");
        UploadTask uploadTask = storageRef.putFile(imageUri);

        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) throw Objects.requireNonNull(task.getException());
            return storageRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                gardenRef.child("imageUrl").setValue(downloadUri.toString())
                        .addOnCompleteListener(saveTask -> {
                            if (saveTask.isSuccessful()) {
                                finishUpdate();
                            } else {
                                Toast.makeText(GardenDetailActivity.this, "Failed to update garden image", Toast.LENGTH_SHORT).show();
                                enableSaveButton(true); // Re-enable the save button on failure
                            }
                        });
            } else {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                enableSaveButton(true); // Re-enable the save button on failure
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
            enableSaveButton(true); // Re-enable the save button on failure
        });
    }

    private void saveUpdatedGardenToFirebase() {
        String updatedName = garden_detail_name.getText().toString().trim();
        String updatedDescription = garden_detail_description.getText().toString().trim();
        float updatedRating = garden_detail_rating.getRating();

        if (updatedName.isEmpty()) {
            Toast.makeText(this, "Please provide a name for the garden", Toast.LENGTH_SHORT).show();
            return;
        }

        enableSaveButton(false); // Disable the save button

        DatabaseReference gardenRef = FirebaseDatabase.getInstance().getReference("Garden").child(gardenId);

        gardenRef.child("name").setValue(updatedName);
        gardenRef.child("description").setValue(updatedDescription);
        gardenRef.child("rating").setValue(updatedRating);

        if (isImageChanged && imageUri != null) {
            uploadImageAndSaveUri(gardenRef);
        } else {
            finishUpdate();
        }
    }

    private void enableSaveButton(boolean enable) {
        save_button.setEnabled(enable);  // Completely disable or enable the button
    }

    private void finishUpdate() {
        Toast.makeText(GardenDetailActivity.this, "Garden updated successfully", Toast.LENGTH_SHORT).show();
        enableEditing(false);
        isImageChanged = false; // Reset the flag after saving
        enableSaveButton(true); // Re-enable the save button after the update completes
    }
}

