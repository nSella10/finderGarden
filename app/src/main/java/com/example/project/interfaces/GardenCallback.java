package com.example.project.interfaces;

import com.example.project.Managment.Garden;

public interface GardenCallback {
    void onGardenClick(Garden garden);
    void favoriteButtonClicked(Garden garden, int position);
}
