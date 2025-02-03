package com.example.assign3.apiClient.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class PostTaskResponse {
    private TaskDetails taskDetails;
    @NonNull
    private Integer responseCode;
}
