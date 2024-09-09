package com.example.project.Managment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.google.android.material.chip.Chip;

import java.util.List;

public class FacilitiesAdapter extends RecyclerView.Adapter<FacilitiesAdapter.FacilityViewHolder> {

    private final List<String> facilityList;
    private final List<String> selectedFacilities;
    private final Context context;

    public FacilitiesAdapter(Context context, List<String> facilityList, List<String> selectedFacilities) {
        this.context = context;
        this.facilityList = facilityList;
        this.selectedFacilities = selectedFacilities;
    }

    @NonNull
    @Override
    public FacilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chip, parent, false);
        return new FacilityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacilityViewHolder holder, int position) {
        String facility = facilityList.get(position);
        holder.chip.setText(facility);

        holder.chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedFacilities.add(facility);
            } else {
                selectedFacilities.remove(facility);
            }
        });
    }

    @Override
    public int getItemCount() {
        return facilityList.size();
    }

    public static class FacilityViewHolder extends RecyclerView.ViewHolder {
        Chip chip;

        public FacilityViewHolder(@NonNull View itemView) {
            super(itemView);
            chip = itemView.findViewById(R.id.chip_facility);
        }
    }
}
