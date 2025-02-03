package com.example.assign3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assign3.apiClient.ApiClient;
import com.example.assign3.apiClient.model.Client;
import com.example.assign3.apiClient.model.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private Context context;

    public TaskAdapter(List<Task> taskList, Context context) {
        this.taskList = taskList;
        this.context = context;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        // Set the task name and date time
        holder.name.setText(task.getReminderName());
        holder.datetime.setText(task.getDateTime());

        // Set OnClickListener to navigate to DetailActivity on item click
        holder.detailsButton.setOnClickListener(v -> {
            // Retrieve the auth token from SharedPreferences
            SharedPreferences sharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
            String authToken = sharedPreferences.getString("token", null);

            // Start DetailActivity with clientId and authTok as extras
            Intent intent = new Intent(context, TaskDetailActivity.class);
            intent.putExtra("taskId", task.getId());
            intent.putExtra("authTok", authToken);
            context.startActivity(intent);
        });

        // Set OnClickListener to delete the task
        holder.deleteButton.setOnClickListener(v -> {
            // Retrieve the auth token from SharedPreferences
            SharedPreferences sharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
            String authToken = sharedPreferences.getString("token", null);

            // Delete the task
            ApiClient apiClient = ApiClient.getInstance();
            apiClient.deleteTask(authToken, task.getId());

            // Remove the item from the list
            taskList.remove(position);

            // Notify adapter about item removal
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, taskList.size());
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView name, datetime;
        Button detailsButton, deleteButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.taskName);
            datetime = itemView.findViewById(R.id.taskDateTime);
            detailsButton = itemView.findViewById(R.id.taskDetailsButton);
            deleteButton = itemView.findViewById(R.id.taskDeleteButton);

        }
    }

    public void updateList(List<Task> newList) {
        taskList.clear();
        taskList.addAll(newList);
    }
}