
package com.example.project.Data;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project.R;

public class FilterActivity extends AppCompatActivity {

    private SeekBar distanceSeekBar, ratingSeekBar;
    private TextView distanceValue, ratingValue;
    private CheckBox check_benches, check_kiosk, check_fitnessFacilities, check_carrousel, check_slide, check_swings, check_fountain, check_lawn, check_facilities_0_3, check_facilities_4_8;
    private Button applyFilterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        distanceSeekBar = findViewById(R.id.distance_seekbar);
        ratingSeekBar = findViewById(R.id.rating_seekbar);
        distanceValue = findViewById(R.id.distance_value);
        ratingValue = findViewById(R.id.rating_value);

        check_benches = findViewById(R.id.check_benches);
        check_kiosk = findViewById(R.id.check_kiosk);
        check_fitnessFacilities = findViewById(R.id.check_fitnessFacilities);
        check_carrousel = findViewById(R.id.check_carrousel);
        check_slide = findViewById(R.id.check_slide);
        check_swings = findViewById(R.id.check_swings);
        check_fountain = findViewById(R.id.check_fountain);
        check_lawn = findViewById(R.id.check_lawn);
        check_facilities_0_3 = findViewById(R.id.check_facilities_0_3);
        check_facilities_4_8 = findViewById(R.id.check_facilities_4_8);

        applyFilterButton = findViewById(R.id.apply_filter_button);

        // Set default values
        distanceSeekBar.setProgress(10);
        ratingSeekBar.setProgress(3);

        // Update the text values when the seekbars change
        distanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distanceValue.setText("Distance: " + progress + " km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        ratingSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ratingValue.setText("Rating: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Handle Apply button click
        applyFilterButton.setOnClickListener(v -> applyFilters());
    }

    private void applyFilters() {
        int selectedDistance = distanceSeekBar.getProgress();
        int selectedRating = ratingSeekBar.getProgress();

        boolean hasBenches = check_benches.isChecked();
        boolean hasKiosk = check_kiosk.isChecked();
        boolean hasFitnessFacilities = check_fitnessFacilities.isChecked();
        boolean hasCarrousel = check_carrousel.isChecked();
        boolean hasSlide = check_slide.isChecked();
        boolean hasSwings = check_swings.isChecked();
        boolean hasFountain = check_fountain.isChecked();
        boolean hasLawn = check_lawn.isChecked();
        boolean hasFacilities0To3 = check_facilities_0_3.isChecked();
        boolean hasFacilities4To8 = check_facilities_4_8.isChecked();

        // Create an Intent to send the selected filter values back to MainActivity
        Intent resultIntent = new Intent();

        resultIntent.putExtra("distance", selectedDistance);
        resultIntent.putExtra("rating", selectedRating);
        resultIntent.putExtra("kiosk", hasKiosk);
        resultIntent.putExtra("benches", hasBenches);
        resultIntent.putExtra("fitnessFacilities", hasFitnessFacilities);
        resultIntent.putExtra("carrousel", hasCarrousel);
        resultIntent.putExtra("slide", hasSlide);
        resultIntent.putExtra("swings", hasSwings);
        resultIntent.putExtra("fountain", hasFountain);
        resultIntent.putExtra("lawn", hasLawn);
        resultIntent.putExtra("facilities_0_3", hasFacilities0To3);
        resultIntent.putExtra("facilities_4_8", hasFacilities4To8);

        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
