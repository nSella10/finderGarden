package com.example.project.Management;

import androidx.annotation.NonNull;

import java.util.List;

public class Garden {
    private String id;
    private String name;
    private double latitude;
    private double longitude;
    private double rating;
    private String imageUrl;
    private String description;
    private List<String> facilities;
    private boolean isFavorite = false ;
    private double distanceFromUser;  // Add this field



    public Garden() {
    }

    public Garden(String id,String name, double latitude, double longitude, double rating, String imageUrl, String description, List<String> facilities) {
         this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.description = description;
        this.facilities = facilities;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public Garden setName(String name) {
        this.name = name;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }


    public double getLongitude() {
        return longitude;
    }



    public double getRating() {
        return rating;
    }



    public String getImageUrl() {
        return imageUrl;
    }



    public String getDescription() {
        return description;
    }



    public List<String> getFacilities() {
        return facilities;
    }


    public double getDistanceFromUser() {
        return distanceFromUser;
    }

    public void setDistanceFromUser(double distanceFromUser) {
        this.distanceFromUser = distanceFromUser;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    @NonNull
    @Override
    public String toString() {
        return "Garden{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", rating=" + rating +
                ", imageUrl='" + imageUrl + '\'' +
                ", description='" + description + '\'' +
                ", facilities=" + facilities +
                '}';
    }
}
