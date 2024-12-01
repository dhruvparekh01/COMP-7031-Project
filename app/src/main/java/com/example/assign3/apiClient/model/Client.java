package com.example.assign3.apiClient.model;

import android.graphics.Bitmap;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class Client {
    private Bitmap photo;
    @NonNull
    private Integer clientId;
    @NonNull
    private String firstName;
    @NonNull
    private String lastName;
    @NonNull
    private String address;
    @NonNull
    private Integer age;
    @NonNull
    private String status;
}