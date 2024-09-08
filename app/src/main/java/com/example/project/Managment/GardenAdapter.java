package com.example.project.Managment;

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

public class GardenAdapter extends RecyclerView.Adapter<GardenAdapter.GardenViewHolder> {

    private List<Garden> gardenList;
    private GardenCallback gardenCallback;



    public GardenAdapter(List<Garden> gardenList) {
        this.gardenList = gardenList;
    }

    public void setGardenCallback(GardenCallback gardenCallback) {
        this.gardenCallback = gardenCallback;
    }

    @NonNull
    @Override
    public GardenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_garden, parent, false);
        return new GardenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GardenViewHolder holder, int position) {
        Garden garden = gardenList.get(position);

        holder.gardenName.setText(garden.getName());
        holder.gardenDistance.setText("Lat: " + garden.getLatitude() + ", Lon: " + garden.getLongitude());
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

    @Override
    public int getItemCount() {
        return gardenList == null ? 0 : gardenList.size();
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
            return gardenList.get(position);
        }
    }
}
