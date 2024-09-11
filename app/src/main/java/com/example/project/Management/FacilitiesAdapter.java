package com.example.project.Management;

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

    private final List<String> FACILITIES_LIST;
    private final List<String> SELECTED_FACILITIES;
    private final Context context;

    public FacilitiesAdapter(Context context, List<String> facilityList, List<String> selectedFacilities) {
        this.context = context;
        this.FACILITIES_LIST = facilityList;
        this.SELECTED_FACILITIES = selectedFacilities;
    }

    @NonNull
    @Override
    public FacilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chip, parent, false);
        return new FacilityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacilityViewHolder holder, int position) {
        String facility = FACILITIES_LIST.get(position);
        holder.chip.setText(facility);

        holder.chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                SELECTED_FACILITIES.add(facility);
            } else {
                SELECTED_FACILITIES.remove(facility);
            }
        });
    }

    @Override
    public int getItemCount() {
        return FACILITIES_LIST.size();
    }

    public static class FacilityViewHolder extends RecyclerView.ViewHolder {
        Chip chip;

        public FacilityViewHolder(@NonNull View itemView) {
            super(itemView);
            chip = itemView.findViewById(R.id.chip_facility);
        }
    }
}
