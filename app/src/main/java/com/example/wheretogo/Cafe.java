package com.example.wheretogo;

import java.util.Objects;

public class Cafe {
    private String name;
    private String location;
    private String description;
    private String imageBase64;
    private boolean isFavorite;
    private String phonenumber; // New field
    private String email;

    public Cafe() {}

    public Cafe(String name, String location, String description, String imageBase64, String phonenumber, String email) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.imageBase64 = imageBase64;
        this.phonenumber = phonenumber;
        this.email = email;
        this.isFavorite = false;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phoneNumber) {
        this.phonenumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cafe cafe = (Cafe) o;
        return name.equals(cafe.name) && location.equals(cafe.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, location);
    }
}
