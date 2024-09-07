//
//package com.example.project.Managment;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.AppCompatRatingBar;
//import com.bumptech.glide.Glide;
//import com.example.project.R;
//import com.google.android.material.button.MaterialButton;
//import com.google.android.material.imageview.ShapeableImageView;
//import com.google.android.material.textview.MaterialTextView;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//public class GardenDetailActivity extends AppCompatActivity {
//
//    private ShapeableImageView garden_detail_image;
//    private MaterialTextView garden_detail_name;
//    private MaterialTextView garden_detail_facilities;
//    private MaterialTextView garden_detail_distance;
//    private MaterialTextView garden_detail_description;
//    private AppCompatRatingBar garden_detail_rating;
//    private MaterialButton edit_button;
//    private MaterialButton save_button;
//
//    private String gardenId; // Declare gardenId
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_garden_detail);
//
//        // Initialize views
//        garden_detail_image = findViewById(R.id.garden_detail_image);
//        garden_detail_name = findViewById(R.id.garden_detail_name);
//        garden_detail_facilities = findViewById(R.id.garden_detail_facilities);
//        garden_detail_distance = findViewById(R.id.garden_detail_distance);
//        garden_detail_description = findViewById(R.id.garden_detail_description);
//        garden_detail_rating = findViewById(R.id.garden_detail_rating);
//        edit_button = findViewById(R.id.edit_button);
//        save_button = findViewById(R.id.save_button);
//
//        garden_detail_rating.setIsIndicator(true);
//
//        // Get the garden details from the intent
//        Intent intent = getIntent();
//        if (intent != null) {
//            gardenId = intent.getStringExtra("garden_id");
//            String name = intent.getStringExtra("garden_name");
//            String description = intent.getStringExtra("garden_description");
//            String[] facilitiesArray = intent.getStringArrayExtra("garden_facilities");
//            String distance = intent.getStringExtra("garden_distance");
//            String imageUrl = intent.getStringExtra("garden_image_url");
//            float rating = intent.getFloatExtra("garden_rating", 0f);  // The default value is 0 if not found
//            garden_detail_rating.setRating(rating);
//
//
//            // Set the data to views
//            garden_detail_name.setText(name != null ? name : "No name available");
//            garden_detail_description.setText(description != null ? description : "No description available");
//            garden_detail_distance.setText(distance != null ? distance : "Location not available");
//
//            // Join facilities array into a single string (comma-separated)
//            if (facilitiesArray != null) {
//                String facilities = String.join(", ", facilitiesArray); // Join array elements
//                garden_detail_facilities.setText(facilities);
//            } else {
//                garden_detail_facilities.setText("No facilities listed");
//            }
//
//            garden_detail_rating.setRating(rating);
//
//            // Load the image using Glide
//            if (imageUrl != null && !imageUrl.isEmpty()) {
//                Glide.with(this).load(imageUrl).into(garden_detail_image);
//            } else {
//                garden_detail_image.setImageResource(R.drawable.unavailable_photo); // Placeholder image
//            }
//        }
//        edit_button.setOnClickListener(v -> {
//            enableEditing(true); // Enable editing
//        });
//
//    }
//
//    private void enableEditing(boolean enable) {
//        garden_detail_name.setEnabled(enable);
//        garden_detail_facilities.setEnabled(enable);
//        garden_detail_description.setEnabled(enable);
//        garden_detail_rating.setIsIndicator(!enable); // Rating bar should be interactive when editing
//
//        // Switch buttons visibility
//        if (enable) {
//            edit_button.setVisibility(Button.GONE);
//            edit_button.setVisibility(Button.VISIBLE);
//        } else {
//            edit_button.setVisibility(Button.VISIBLE);
//            edit_button.setVisibility(Button.GONE);
//        }
//
//        save_button.setOnClickListener(v->{
//            saveUpdatedGardenToFirebase();
//        });
//    }
//
//
//    private void saveUpdatedGardenToFirebase() {
//        // Get the updated values from the fields
//        String updatedName = garden_detail_name.getText().toString().trim();
//        String updatedFacilities = garden_detail_facilities.getText().toString().trim();
//        String updatedDescription = garden_detail_description.getText().toString().trim();
//        float updatedRating = garden_detail_rating.getRating();
//
//        if (updatedName.isEmpty()) {
//            Toast.makeText(this, "Please provide a name for the garden", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Get a reference to the specific garden in Firebase
//        DatabaseReference gardenRef = FirebaseDatabase.getInstance().getReference("Garden").child(gardenId);
//
//        // Update the fields in Firebase
//        gardenRef.child("name").setValue(updatedName);
//        gardenRef.child("facilities").setValue(updatedFacilities);
//        gardenRef.child("description").setValue(updatedDescription);
//        gardenRef.child("rating").setValue(updatedRating)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(GardenDetailActivity.this, "Garden updated successfully", Toast.LENGTH_SHORT).show();
//                        enableEditing(false); // Disable editing after saving
//                    } else {
//                        Toast.makeText(GardenDetailActivity.this, "Failed to update garden", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//}

package com.example.project.Managment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import com.bumptech.glide.Glide;
import com.example.project.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

public class GardenDetailActivity extends AppCompatActivity {

    private ShapeableImageView garden_detail_image;
    private MaterialTextView garden_detail_name;
    private MaterialTextView garden_detail_facilities;
    private MaterialTextView garden_detail_distance;
    private MaterialTextView garden_detail_description;
    private AppCompatRatingBar garden_detail_rating;
    private MaterialButton edit_button;
    private MaterialButton save_button;

    private String gardenId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garden_detail);

        // Initialize views
        garden_detail_image = findViewById(R.id.garden_detail_image);
        garden_detail_name = findViewById(R.id.garden_detail_name);
        garden_detail_facilities = findViewById(R.id.garden_detail_facilities);
        garden_detail_distance = findViewById(R.id.garden_detail_distance);
        garden_detail_description = findViewById(R.id.garden_detail_description);
        garden_detail_rating = findViewById(R.id.garden_detail_rating);
        edit_button = findViewById(R.id.edit_button);
        save_button = findViewById(R.id.save_button);

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
            float rating = intent.getFloatExtra("garden_rating", 0f);  // Default value is 0 if not found

            // Set the data to views
            garden_detail_name.setText(name != null ? name : "No name available");
            garden_detail_description.setText(description != null ? description : "No description available");
            garden_detail_distance.setText(distance != null ? distance : "Location not available");

            // Handle null facilities array safely
            if (facilitiesArray != null) {
                String facilities = String.join(", ", facilitiesArray); // Join array elements
                garden_detail_facilities.setText(facilities);
            } else {
                garden_detail_facilities.setText("No facilities listed");
            }

            garden_detail_rating.setRating(rating);

            // Load the image using Glide
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this).load(imageUrl).into(garden_detail_image);
            } else {
                garden_detail_image.setImageResource(R.drawable.unavailable_photo); // Placeholder image
            }
        }

        edit_button.setOnClickListener(v -> enableEditing(true));

        save_button.setOnClickListener(v -> saveUpdatedGardenToFirebase());
    }

    private void enableEditing(boolean enable) {
        garden_detail_name.setEnabled(enable);
        garden_detail_facilities.setEnabled(enable);
        garden_detail_description.setEnabled(enable);
        garden_detail_rating.setIsIndicator(!enable); // Rating bar should be interactive when editing

        // Switch buttons visibility
        if (enable) {
            edit_button.setVisibility(Button.GONE);
            save_button.setVisibility(Button.VISIBLE);
        } else {
            edit_button.setVisibility(Button.VISIBLE);
            save_button.setVisibility(Button.GONE);
        }
    }

    private void saveUpdatedGardenToFirebase() {
        // Get the updated values from the fields
        String updatedName = garden_detail_name.getText().toString().trim();
        String updatedFacilities = garden_detail_facilities.getText().toString().trim();
        String updatedDescription = garden_detail_description.getText().toString().trim();
        float updatedRating = garden_detail_rating.getRating();

        if (updatedName.isEmpty()) {
            Toast.makeText(this, "Please provide a name for the garden", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert updated facilities string into a List<String>
        List<String> facilitiesList = Arrays.asList(updatedFacilities.split(",\\s*"));

        // Get a reference to the specific garden in Firebase
        DatabaseReference gardenRef = FirebaseDatabase.getInstance().getReference("Garden").child(gardenId);

        // Update the fields in Firebase
        gardenRef.child("name").setValue(updatedName);
        gardenRef.child("facilities").setValue(facilitiesList); // Save facilities as a List
        gardenRef.child("description").setValue(updatedDescription);
        gardenRef.child("rating").setValue(updatedRating)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(GardenDetailActivity.this, "Garden updated successfully", Toast.LENGTH_SHORT).show();
                        enableEditing(false); // Disable editing after saving
                    } else {
                        Toast.makeText(GardenDetailActivity.this, "Failed to update garden", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
