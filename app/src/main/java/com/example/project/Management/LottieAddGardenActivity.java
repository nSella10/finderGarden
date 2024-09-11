package com.example.project.Management;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.project.MainActivity;
import com.example.project.R;

public class LottieAddGardenActivity extends AppCompatActivity {
    private LottieAnimationView lottie_LOTTIE_lottie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottie_add_garden);

        findViews();
        lottie_LOTTIE_lottie.playAnimation(); // Start the animation

        lottie_LOTTIE_lottie.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {
                // pass
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                // After the animation ends, move to MainActivity
                transactToMainActivity();
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {
                // pass
            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {
                // pass
            }
        });
    }

    private void transactToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish(); // Close the LottieAddGardenActivity
    }

    private void findViews() {
        lottie_LOTTIE_lottie = findViewById(R.id.lottie_LOTTIE_lottie);
    }
}
