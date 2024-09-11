package com.example.project.interfaces;

import com.example.project.Management.Garden;

public interface GardenCallback {
    void onGardenClick(Garden garden);
    void favoriteButtonClicked(Garden garden, int position);
}
