package com.example.assign3.apiClient.model;

import android.graphics.Bitmap;

import lombok.Data;

@Data
public class ClientDetails {
    int id;
    int age;
    String firstName, lastName, address, email, phone;
    String status;

    Bitmap decodedBitmap, thumbnail;


    public ClientDetails(Bitmap image, Bitmap thumbnailBitmap, String firstName, String lastName, String address,
                         String status, int age, String email, String phone) {
        decodedBitmap = image;
        this.thumbnail = thumbnailBitmap;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.status = status;

        this.age = age;
        this.email = email;
        this.phone = phone;
    }
}
