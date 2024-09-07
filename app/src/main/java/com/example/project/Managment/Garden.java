package com.example.project.Managment;

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

    public Garden setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public Garden setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public double getRating() {
        return rating;
    }

    public Garden setRating(double rating) {
        this.rating = rating;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Garden setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Garden setDescription(String description) {
        this.description = description;
        return this;
    }

    public List<String> getFacilities() {
        return facilities;
    }

    public Garden setFacilities(List<String> facilities) {
        this.facilities = facilities;
        return this;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

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
