package com.example.skillnet.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillnet.Models.ChatModel;
import com.example.skillnet.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatBotAdapter extends RecyclerView.Adapter<ChatBotAdapter.ChatViewHolder> {

    private List<ChatModel> chatList;

    public ChatBotAdapter(List<ChatModel> chatList) {
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatModel chat = chatList.get(position);
        if(chat.getCode().equals("user")){
            holder.botCard.setVisibility(View.GONE);
            holder.userCard.setVisibility(View.VISIBLE);
            holder.userMessage.setText(chat.getMassage());
            if(chat.getUser().getImageUrl() != null)
            {
                Picasso.get().load(chat.getUser().getImageUrl()).into(holder.profileImage);
            }
            else {
                holder.profileImage.setBackgroundResource(R.drawable.person);
            }
        } else if (chat.getCode().equals("otherUser")) {
            holder.botCard.setVisibility(View.VISIBLE);
            holder.userCard.setVisibility(View.GONE);
            holder.botMassage.setText(chat.getMassage());
            if(chat.getOtherUser().getImageUrl() != null) {
                Picasso.get().load(chat.getOtherUser().getImageUrl()).into(holder.botImage);
            }
            else {
                holder.botImage.setBackgroundResource(R.drawable.logo);
            }
        } else{
            holder.botCard.setVisibility(View.VISIBLE);
            holder.userCard.setVisibility(View.GONE);
            holder.botMassage.setText(chat.getMassage());
            holder.botImage.setBackgroundResource(R.drawable.logo);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView userMessage, botMassage;
        de.hdodenhof.circleimageview.CircleImageView profileImage, botImage;
        LinearLayout userCard, botCard;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            userCard     = itemView.findViewById(R.id.user_card);
            botCard      = itemView.findViewById(R.id.robot_card);
            userMessage  = itemView.findViewById(R.id.usertext);
            botMassage   = itemView.findViewById(R.id.robotext);
            profileImage = itemView.findViewById(R.id.user);
            botImage     = itemView.findViewById(R.id.robo);
        }
    }
}
