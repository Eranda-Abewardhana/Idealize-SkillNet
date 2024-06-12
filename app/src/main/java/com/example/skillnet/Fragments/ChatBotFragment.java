package com.example.skillnet.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.skillnet.Adapters.ChatBotAdapter;
import com.example.skillnet.Models.ChatModel;
import com.example.skillnet.R;

import java.util.ArrayList;
import java.util.List;

public class ChatBotFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatBotAdapter chatAdapter;
    private List<ChatModel> chatList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_bot, container, false);
        recyclerView = view.findViewById(R.id.chat_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        chatList = new ArrayList<>();
        // Add sample chat data
        chatList.add(new ChatModel("user", "Hello!"));
        chatList.add(new ChatModel("bot", "Hi there! How can I assist you today?"));
        chatList.add(new ChatModel("user", "I need help with my account."));
        chatList.add(new ChatModel("bot", "Sure, I'd be happy to help with that."));
        chatList.add(new ChatModel("user", "Hello!"));
        chatList.add(new ChatModel("bot", "Hi there! How can I assist you today?"));
        chatList.add(new ChatModel("user", "I need help with my account."));
        chatList.add(new ChatModel("bot", "Sure, I'd be happy to help with that."));
        chatList.add(new ChatModel("user", "Hello!"));
        chatList.add(new ChatModel("bot", "Hi there! How can I assist you today?"));
        chatList.add(new ChatModel("user", "I need help with my account."));
        chatList.add(new ChatModel("bot", "Sure, I'd be happy to help with that."));


        chatAdapter = new ChatBotAdapter(chatList);
        recyclerView.setAdapter(chatAdapter);
        // Inflate the layout for this fragment
        return view;
    }
}