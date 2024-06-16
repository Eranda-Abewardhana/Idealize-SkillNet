package com.example.skillnet.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.skillnet.Adapters.ChatBotAdapter;
import com.example.skillnet.Adapters.ChatListAdapter;
import com.example.skillnet.FirebaseHelper.Firebase;
import com.example.skillnet.FirebaseHelper.FirebaseCallback;
import com.example.skillnet.Global_Variables.GlobalVariables;
import com.example.skillnet.Models.ChatModel;
import com.example.skillnet.Models.PersonData;
import com.example.skillnet.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatFragment extends Fragment implements ChatListAdapter.OnItemClickListener {

    private List<PersonData> personDataList = new ArrayList<>();
    private RecyclerView recyclerView,chatRecycle;
    private ChatListAdapter chatListAdapter;
    private List<QueryDocumentSnapshot> chatDataList;
    private List<String> otherUserCodes = new ArrayList<>();
    private LinearLayout chatLayout;
    private List<ChatModel> chatList;
    public static ChatBotAdapter chatAdapter;
    private  String code = "";
    private String otherCode = "";
    private EditText massage;
    private ImageView send;
    private String documentId = "";
    private  Firebase firebase;
    private PersonData currentUser = new PersonData();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.recycle);
        chatRecycle = view.findViewById(R.id.chat_list);
        chatLayout = view.findViewById(R.id.chatdatalist);
        massage = view.findViewById(R.id.massage);
        send = view.findViewById(R.id.sendto);
        CircleImageView backButton = view.findViewById(R.id.circleImageView2);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String email = auth.getCurrentUser().getEmail();
        firebase = new Firebase();
        chatList = new ArrayList<>();
        chatRecycle.setLayoutManager(new LinearLayoutManager(getContext()));
        chatAdapter = new ChatBotAdapter(chatList);
        chatRecycle.setAdapter(chatAdapter);

        // Create a SimpleDateFormat instance with the desired format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd[HH:mm:ss]", Locale.getDefault());

        // Get the current date and time
        Date now = new Date();

        // Format the current time as a string
        String formattedTime = sdf.format(now);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chatLayout.getVisibility() == View.VISIBLE){
                    chatLayout.setVisibility(View.GONE) ;
                }
                else {
                    getParentFragmentManager().popBackStack();
                }
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userMessage = massage.getText().toString().trim();
                if (!userMessage.isEmpty()) {
                    documentId = otherCode + "-" + code; // Construct the document ID
                    String fieldName = formattedTime + " " + code; // Construct the field name

                    // Optionally, clear the message field after sending
                    massage.setText("");

                    // Or, if you are in an Android environment, use Log.d() to log the values
                    Log.d("Firestore", "Document ID: " + documentId);
                    Log.d("Firestore", "Field Name: " + fieldName);
                    firebase.addChatData(documentId, fieldName, userMessage, new FirebaseCallback<Void>() {
                        @Override
                        public void onCallback(List<Void> list) {
                        }

                        @Override
                        public void onDocumentSnapshotCallback(DocumentSnapshot snapshot) {
                        }

                        @Override
                        public void onSingleCallback(Void result) {
                            // Handle the result of the update operation
                            if (result == null) {
                                Log.d("Firestore", "Field added successfully");
                                chatAdapter.notifyDataSetChanged();
                                chatRecycle.scrollToPosition(chatList.size() - 1);
                                
                            } else {
                                Log.w("Firestore", "Error adding field");
                            }
                        }
                    });
                }
            }
        });


        // Fetch user by email
        firebase.getUserByEmail(email, new FirebaseCallback<QueryDocumentSnapshot>() {
            @Override
            public void onCallback(List<QueryDocumentSnapshot> users) {
                if (!users.isEmpty()) {
                    QueryDocumentSnapshot user = users.get(0);
                    code = user.getString("user");

                    firebase.getAllUsers(new FirebaseCallback<PersonData>() {
                        @Override
                        public void onCallback(List<PersonData> users) {
                            for (PersonData personData : users) {
                                if (otherUserCodes.contains(personData.getpCode()) && (personData.isIswoker() == !GlobalVariables.isWorker)) {
                                    personDataList.add(personData);
                                }
                                if (personData.getpCode().equals(code)) {
                                    currentUser = personData;
                                }
                            }

                            // Set adapter with fetched data
                            chatListAdapter = new ChatListAdapter(personDataList, ChatFragment.this);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(chatListAdapter);
                        }

                        @Override
                        public void onDocumentSnapshotCallback(DocumentSnapshot snapshot) {

                        }

                        @Override
                        public void onSingleCallback(PersonData item) {

                        }
                    });
                    firebase.getChatsByCode(code, new FirebaseCallback<QueryDocumentSnapshot>() {

                        @Override
                        public void onCallback(List<QueryDocumentSnapshot> chatList) {
                            chatDataList = chatList;
                            for (QueryDocumentSnapshot chat : chatList) {
                                String[] user2 = chat.getId().split(code);
                                String otherUserCode = (!user2[0].equals(code)) ? user2[0].split("-")[0] : user2[1].split("-")[0];
                                otherUserCodes.add(otherUserCode);
                            }

                            // Fetch all users and filter by chat codes

                        }

                        @Override
                        public void onDocumentSnapshotCallback(DocumentSnapshot snapshot){

                        }

                        @Override
                        public void onSingleCallback(QueryDocumentSnapshot item) {

                        }
                    });
                }
            }

            @Override
            public void onDocumentSnapshotCallback(DocumentSnapshot snapshot) {

            }

            @Override
            public void onSingleCallback(QueryDocumentSnapshot item) {

            }
        });

        return view;
    }

    // Handle item click from ChatListAdapter
    @Override
    public void onItemClicked(PersonData person, int position) {

        chatLayout.setVisibility(View.VISIBLE);

        // Ensure position is within bounds
        if (position >= 0 && position < chatDataList.size()) {
            QueryDocumentSnapshot chatDocument = chatDataList.get(position);
            Map<String, Object> documentData = chatDocument.getData();
            UpdateAdapter(documentData, person);
            // Set the document ID dynamically
            String documentId = chatDocument.getId();

            RefreshAndLoadChat(documentId, person);
        }
    }

    private void UpdateAdapter(Map<String, Object> documentData, PersonData person) {
        // Use TreeMap to sort by keys (timestamps)
        TreeMap<String, Object> sortedDocumentData = new TreeMap<>(documentData);

        // Process the sorted document data
        for (Map.Entry<String, Object> entry : sortedDocumentData.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            Log.d("Firestore", "Key: " + key + ", Value: " + value);
            if (key.contains(code)) {
                chatList.add(new ChatModel("user", value.toString(), currentUser, person));
            } else {
                otherCode = key.split(" ")[1];
                chatList.add(new ChatModel("otherUser", value.toString(), currentUser, person));
            }
        }
        // Notify the adapter that the data set has changed
        chatAdapter.notifyDataSetChanged();
        chatRecycle.scrollToPosition(chatList.size() - 1);

    }

    private void RefreshAndLoadChat(String documentId, PersonData person) {
        firebase.listenToDocumentUpdates(documentId, new FirebaseCallback<DocumentSnapshot>() {
            @Override
            public void onCallback(List<DocumentSnapshot> list) {
            }

            @Override
            public void onDocumentSnapshotCallback(DocumentSnapshot snapshot) {
                if (snapshot != null) {
                    // Handle the updated document snapshot
                    Map<String, Object> documentData = snapshot.getData();
                    UpdateAdapter(documentData, person);
                    // Notify the adapter that the data set has changed
                    chatAdapter.notifyDataSetChanged();
                    chatRecycle.scrollToPosition(chatList.size() - 1);
                    Log.d("Firestore", "Updated Document data: " + documentData);
                    // Do something with the data
                } else {
                    Log.d("Firestore", "Document data: null");
                }
            }

            @Override
            public void onSingleCallback(DocumentSnapshot item) {
                // Handle single document snapshot (alternative usage)
            }
        });

    }
}
