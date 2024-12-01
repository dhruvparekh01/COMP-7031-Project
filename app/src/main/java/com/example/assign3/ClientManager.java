package com.example.assign3;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assign3.apiClient.model.Client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lombok.Getter;

public class ClientManager {

    private List<Client> clientListOriginal;
    private List<Client> clientList;
    @Getter
    private ClientAdapter adapter;

    public ClientManager(Context context, RecyclerView recyclerView, List<Client> clients) {
        this.clientListOriginal = clients;
        clientList = new ArrayList<>(clientListOriginal);
        adapter = new ClientAdapter(clientList, context);
        recyclerView.setAdapter(adapter);
    }

    public void filterClients(String text) {
        if (text == null || text.isEmpty()) {
            clientList.clear();
            clientList.addAll(clientListOriginal);
        } else {
            clientList.clear();
            for (Client client : clientListOriginal) {
                if (client.getFirstName().toLowerCase().contains(text.toLowerCase()) ||
                        client.getLastName().toLowerCase().contains(text.toLowerCase()) ||
                        client.getAddress().toLowerCase().contains(text.toLowerCase())) {
                    clientList.add(client);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void clearFilters() {
        clientList.clear();
        clientList.addAll(clientListOriginal);
        adapter.notifyDataSetChanged();
    }

    public void sortClients(int position) {
        Comparator<Client> comparator;

        switch (position) {
            case 0:
                comparator = Comparator.comparing(Client::getFirstName, Comparator.nullsLast(String::compareTo));
                break;
            case 1:
                comparator = Comparator.comparing(Client::getLastName, Comparator.nullsLast(String::compareTo));
                break;
            case 2:
                comparator = Comparator.comparing(Client::getAddress, Comparator.nullsLast(String::compareTo));
                break;
            default:
                return;
        }

        Collections.sort(clientList, comparator);
        adapter.notifyDataSetChanged();
    }

    public void filterClientsByAgeRangeAndStatus(int minAge, int maxAge, String status) {
        clientList.clear();
        for (Client client : clientListOriginal) {
            boolean matchesAge = client.getAge() >= minAge && client.getAge() <= maxAge;
            boolean matchesStatus = status == null || client.getStatus().equals(status);

            if (matchesAge && matchesStatus) {
                clientList.add(client);
            }
        }
        adapter.notifyDataSetChanged();
    }

    public List<Client> getClients() {
        return clientList;
    }
}