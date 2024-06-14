package com.example.skillnet.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillnet.Adapters.ChatBotAdapter;
import com.example.skillnet.Models.ChatModel;
import com.example.skillnet.R;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.BlockThreshold;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.ai.client.generativeai.type.HarmCategory;
import com.google.ai.client.generativeai.type.SafetySetting;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatBotFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatBotAdapter chatAdapter;
    private List<ChatModel> chatList;
    private EditText userMessageInput;
    private ImageView sendButton;
    private GenerativeModelFutures model;
    private ExecutorService executorService; // ExecutorService for managing threads

    // Replace with your API Key obtained from Google AI Studio
    private static final String API_KEY = "AIzaSyBn4IJBo-jtgV2MAjgPOZhYWe6VOjX6SIw";
    private String resultText = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_bot, container, false);
        recyclerView = view.findViewById(R.id.chat_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
        configBuilder.temperature = 0.15f;
        configBuilder.topK = 32;
        configBuilder.topP = 1f;
        configBuilder.maxOutputTokens = 4096;


        ArrayList<SafetySetting> safetySettings = new ArrayList<>();
        safetySettings.add(new SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE));
        safetySettings.add(new SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE));
        safetySettings.add(new SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE));
        safetySettings.add(new SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE));

        GenerativeModel gm = new GenerativeModel(
                "gemini-1.5-flash-001",
                API_KEY,
                configBuilder.build(),
                safetySettings
        );

        model = GenerativeModelFutures.from(gm);

        chatList = new ArrayList<>();
        chatAdapter = new ChatBotAdapter(chatList);
        recyclerView.setAdapter(chatAdapter);

        userMessageInput = view.findViewById(R.id.massage); // corrected from massage to message
        sendButton = view.findViewById(R.id.send);

        executorService = Executors.newSingleThreadExecutor(); // Initialize ExecutorService

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userMessage = userMessageInput.getText().toString().trim();
                if (!userMessage.isEmpty()) {
                    // Add user message to the chat list
                    chatList.add(new ChatModel("user", userMessage));
                    chatAdapter.notifyItemInserted(chatList.size() - 1);
                    userMessageInput.setText("");

                    // Initialize chat with message history
                    Content.Builder userContentBuilder = new Content.Builder();
                    userContentBuilder.setRole("user");
                    userContentBuilder.addText(userMessage);
                    Content userContent = userContentBuilder.build();

                    List<Content> history = Arrays.asList(userContent);
                    ChatFutures chat = model.startChat(history);

                    // Start chat with initial history
                    ListenableFuture<GenerateContentResponse> response = chat.sendMessage(userContent);

                    // Use executorService to handle callbacks
                    Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                        @Override
                        public void onSuccess(GenerateContentResponse result) {
                            String botResponse = result.getText();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    chatList.add(new ChatModel("bot", botResponse));
                                    chatAdapter.notifyItemInserted(chatList.size() - 1);
                                }
                            });
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            t.printStackTrace();
                            Toast.makeText(getContext(), "Failed to get response from AI model", Toast.LENGTH_SHORT).show();
                        }
                    }, executorService);
                }
            }
        });


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Shutdown executorService when the fragment is destroyed
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
