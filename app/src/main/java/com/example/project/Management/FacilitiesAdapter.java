
package com.example.project.Management;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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

    private List<String> FACILITIES_LIST;
    private final List<String> SELECTED_FACILITIES;
    private final Context context;
    private boolean isEditable;

    public FacilitiesAdapter(Context context, List<String> facilityList, List<String> selectedFacilities, boolean isEditable) {
        this.context = context;
        this.FACILITIES_LIST = facilityList;
        this.SELECTED_FACILITIES = selectedFacilities;
        this.isEditable = isEditable;
    }

    @NonNull
    @Override
    public FacilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chip, parent, false);
        return new FacilityViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull FacilityViewHolder holder, int position) {
        String facility = FACILITIES_LIST.get(position);
        holder.chip.setText(facility);

        // Set chip checked based on whether it's selected
        holder.chip.setChecked(SELECTED_FACILITIES.contains(facility));

        // Enable or disable the chip based on the edit mode
        holder.chip.setEnabled(isEditable);

        // Handle chip click: if editable, toggle the facility in the selected list
        holder.chip.setOnClickListener(v -> {
            if (isEditable) {
                if (SELECTED_FACILITIES.contains(facility)) {
                    // Remove facility (deselect it)
                    SELECTED_FACILITIES.remove(facility);
                } else {
                    // Add facility (select it)
                    SELECTED_FACILITIES.add(facility);
                }
                // Notify adapter to refresh the view
                notifyDataSetChanged();
            }
        });
    }

    private void showDeleteConfirmation(String facility) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Facility")
                .setMessage("Are you sure you want to delete this facility?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    FACILITIES_LIST.remove(facility);
                    notifyDataSetChanged();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return FACILITIES_LIST.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setEditable(boolean isEditable) {
        this.isEditable = isEditable;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateFacilities(List<String> newFacilities) {
        this.FACILITIES_LIST = newFacilities;
        notifyDataSetChanged();
    }

    public List<String> getSelectedFacilities() {
        return SELECTED_FACILITIES;
    }

    public static class FacilityViewHolder extends RecyclerView.ViewHolder {
        Chip chip;

        public FacilityViewHolder(@NonNull View itemView) {
            super(itemView);
            chip = itemView.findViewById(R.id.chip_facility);
        }
    }
}
