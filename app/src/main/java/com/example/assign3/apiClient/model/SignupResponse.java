package com.example.assign3.apiClient.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignupResponse {
    private String message;
    private String username;
    private int responseCode;
}
