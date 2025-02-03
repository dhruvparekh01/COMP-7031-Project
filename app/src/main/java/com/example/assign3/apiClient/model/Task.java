package com.example.assign3.apiClient.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class Task {
    @NonNull
    private Integer id;
    @NonNull
    private Integer clientId;
    @NonNull
    private String reminderName;
    @NonNull
    private String taskType;
    @NonNull
    private String dateTime;
    private String repeatDays;
    @NonNull
    private String notes;
    private String filePath;
}