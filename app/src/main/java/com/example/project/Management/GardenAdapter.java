package com.example.project.Management;

import android.annotation.SuppressLint;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.project.R;
import com.example.project.interfaces.GardenCallback;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import java.util.List;
import java.util.Locale;

public class GardenAdapter extends RecyclerView.Adapter<GardenAdapter.GardenViewHolder> {

    private final List<Garden> GARDEN_LIST;
    private GardenCallback gardenCallback;
    private double userLatitude, userLongitude;



    public GardenAdapter(List<Garden> gardenList) {
        this.GARDEN_LIST = gardenList;
    }

    public void setGardenCallback(GardenCallback gardenCallback) {
        this.gardenCallback = gardenCallback;
    }


    @SuppressLint("NotifyDataSetChanged")
    public void setUserLocation(double userLatitude, double userLongitude) {
        this.userLatitude = userLatitude;
        this.userLongitude = userLongitude;
        notifyDataSetChanged();  // Update the list with distances
    }
    @NonNull
    @Override
    public GardenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_garden, parent, false);
        return new GardenViewHolder(view);
    }
    

    @SuppressLint("SetTextI18n")
    public void onBindViewHolder(@NonNull GardenViewHolder holder, int position) {
        Garden garden = GARDEN_LIST.get(position);

        holder.gardenName.setText(garden.getName());

        double gardenLatitude = garden.getLatitude();
        double gardenLongitude = garden.getLongitude();

        // Calculate the distance and display it
        String distance = calculateDistance(gardenLatitude, gardenLongitude);
        holder.gardenDistance.setText("Distance: " + distance);

        holder.gardenRatingBar.setRating((float) garden.getRating());

        // Load the garden image using Glide
        if (garden.getImageUrl() != null && !garden.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(garden.getImageUrl())
                    .into(holder.gardenImage);
        } else {
            holder.gardenImage.setImageResource(R.drawable.unavailable_photo);
        }

        if (garden.isFavorite()) {
            holder.gardenFavorite.setImageResource(R.drawable.heart);
        } else {
            holder.gardenFavorite.setImageResource(R.drawable.empty_heart);
        }

        // Handle click events for garden item
        holder.itemView.setOnClickListener(v -> {
            if (gardenCallback != null) {
                gardenCallback.onGardenClick(garden);
            }
        });
    }

    public String calculateDistance(double gardenLatitude, double gardenLongitude) {
        float[] results = new float[1];
        Location.distanceBetween(userLatitude, userLongitude, gardenLatitude, gardenLongitude, results);
        float distanceInMeters = results[0];
        float distanceInKm = distanceInMeters / 1000;
        return String.format(Locale.getDefault(), "%.2f km", distanceInKm);
    }


    @Override
    public int getItemCount() {
        return GARDEN_LIST == null ? 0 : GARDEN_LIST.size();
    }


    public class GardenViewHolder extends RecyclerView.ViewHolder {
        public MaterialTextView gardenName;
        public MaterialTextView gardenDistance;
        public AppCompatRatingBar gardenRatingBar;
        public ShapeableImageView gardenImage;
        public ShapeableImageView gardenFavorite;

        public GardenViewHolder(View view) {
            super(view);
            gardenName = view.findViewById(R.id.garden_TXT_name);
            gardenDistance = view.findViewById(R.id.garden_TXT_distance);
            gardenRatingBar = view.findViewById(R.id.garden_RTNG_rating);
            gardenImage = view.findViewById(R.id.garden_IMG_poster);
            gardenFavorite = view.findViewById(R.id.garden_IMG_favorite);

            // Click listener for favorite button
            gardenFavorite.setOnClickListener(v -> {
                if (gardenCallback != null)
                    gardenCallback.favoriteButtonClicked(getItem(getAdapterPosition()), getAdapterPosition());
            });
        }

        private Garden getItem(int position) {
            return GARDEN_LIST.get(position);
        }
    }
}
