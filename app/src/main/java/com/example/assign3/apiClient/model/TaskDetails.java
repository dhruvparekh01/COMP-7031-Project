package com.example.assign3.apiClient.model;

import lombok.Data;

@Data
public class TaskDetails {
    int id;
    int clientId;
    String reminderName;
    String taskType;
    String dateTime;
    String repeatDays;
    String notes;
    String filePath;

    public TaskDetails(int clientId, String reminderName, String taskType, String dateTime, String repeatDays, String notes, String filePath) {
        this.clientId = clientId;
        this.reminderName = reminderName;
        this.taskType = taskType;
        this.dateTime = dateTime;
        this.repeatDays = repeatDays;
        this.notes = notes;
        this.filePath = filePath;
    }

    public int getId() {
        return id;
    }
}
