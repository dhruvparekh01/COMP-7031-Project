package com.example.assign3.apiClient.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class PostClientResponse {
    private ClientDetails clientDetails;
    @NonNull
    private Integer responseCode;
}
