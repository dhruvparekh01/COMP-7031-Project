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

import com.example.assign3.apiClient.model.Client;

import java.util.List;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ClientViewHolder> {

    private List<Client> clientList;
    private Context context;

    public ClientAdapter(List<Client> clientList, Context context) {
        this.clientList = clientList;
        this.context = context;
    }

    @NonNull
    @Override
    public ClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.client_item, parent, false);
        return new ClientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientViewHolder holder, int position) {
        Client client = clientList.get(position);
        if(client.getPhoto() != null) {
            holder.photo.setImageBitmap(client.getPhoto());
        } else {
            holder.photo.setImageResource(R.drawable.person_icon_placeholder);
        }
        holder.name.setText(client.getFirstName() + " " + client.getLastName());
        holder.address.setText(client.getAddress());

        holder.statusButton.setOnClickListener(v -> {
            boolean visible = holder.statusCheckboxes.getVisibility() == View.VISIBLE;
            holder.statusCheckboxes.setVisibility(visible ? View.GONE : View.VISIBLE);
        });

        // Populate checkboxes with status options
        holder.checkbox1.setText("Active");
        holder.checkbox2.setText("Inactive");
        holder.checkbox3.setText("Pending");

        // Set OnClickListener for the "Tasks" button
        holder.tasksButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, TaskActivity.class);
            SharedPreferences sharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
            String authToken = sharedPreferences.getString("token", null);
            intent.putExtra("clientId", client.getClientId()); // Pass the client ID
            intent.putExtra("clientName", client.getFirstName() + " " + client.getLastName()); // Pass the client name
            intent.putExtra("authTok", authToken);
            context.startActivity(intent);
        });

        // Set OnClickListener to navigate to DetailActivity on item click
        holder.itemView.setOnClickListener(v -> {
            // Retrieve the auth token from SharedPreferences
            SharedPreferences sharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
            String authToken = sharedPreferences.getString("token", null);

            // Start DetailActivity with clientId and authTok as extras
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("clientId", client.getClientId()); // Assuming client.getId() returns the client ID
            intent.putExtra("authTok", authToken);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return clientList.size();
    }

    public static class ClientViewHolder extends RecyclerView.ViewHolder {
        Button tasksButton;
        ImageView photo;
        TextView name, address;
        Button statusButton;
        LinearLayout statusCheckboxes;
        CheckBox checkbox1, checkbox2, checkbox3;

        public ClientViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.clientPhoto);
            name = itemView.findViewById(R.id.clientName);
            address = itemView.findViewById(R.id.clientAddress);
            statusButton = itemView.findViewById(R.id.statusButton);
            statusCheckboxes = itemView.findViewById(R.id.statusCheckboxes);
            tasksButton = itemView.findViewById(R.id.tasksButton);
            checkbox1 = itemView.findViewById(R.id.statusCheckbox1);
            checkbox2 = itemView.findViewById(R.id.statusCheckbox2);
            checkbox3 = itemView.findViewById(R.id.statusCheckbox3);
        }
    }

    // Add this method to the existing ClientAdapter class
    public void updateList(List<Client> newList) {
        clientList.clear();
        clientList.addAll(newList);
        notifyDataSetChanged();
    }
}