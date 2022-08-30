package starmeet.convergentmobile.com.starmeet.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import starmeet.convergentmobile.com.starmeet.Holders.ChatLeftMessageHolder;
import starmeet.convergentmobile.com.starmeet.Holders.ChatRightMessageHolder;
import starmeet.convergentmobile.com.starmeet.Models.ChatMessage;
import starmeet.convergentmobile.com.starmeet.R;

public class ChatArrayAdapter extends RecyclerView.Adapter {

    private int LeftMessage = 1;
    private int RightMessage = 2;
    private Context ctx;
    private List<ChatMessage> messageList = null;

    public ChatArrayAdapter(Context ctx, List<ChatMessage> messageList) {
        this.ctx = ctx;
        this.messageList = messageList;
    }

    @SuppressWarnings("ConstantConditions")
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        if (viewType == LeftMessage) {
            View view = layoutInflater.inflate(R.layout.left_message, parent, false);
            return new ChatLeftMessageHolder(view);
        } else if (viewType == RightMessage) {
            View view = layoutInflater.inflate(R.layout.right_message, parent, false);
            return new ChatRightMessageHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage msg = this.messageList.get(position);
        if (msg == null) return;

        if (!msg.isMy) {
            ChatLeftMessageHolder leftMessage = (ChatLeftMessageHolder) holder;
            if (leftMessage != null) {
                leftMessage.Bind(ctx, msg);
            }
        } else {
            ChatRightMessageHolder rightMessage = (ChatRightMessageHolder) holder;
            if (rightMessage != null) {
                rightMessage.Bind(msg);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.size() == 0) return RightMessage;

        ChatMessage msg = this.messageList.get(position);
        return msg.isMy ? RightMessage : LeftMessage;
    }

    @Override
    public int getItemCount() {
        if (messageList == null) messageList = new ArrayList<>();
        return messageList.size();
    }

    public void addNewMessage(ChatMessage message) {
        messageList.add(message);
        notifyDataSetChanged();
    }

    public int getCountMyMessage() {
        int myMessageCount = 0;

        for (int index = 0; index < messageList.size(); index++) {
            if (messageList.get(index).isMy)
                myMessageCount++;
        }

        return myMessageCount;
    }

    public String setMyMessageId(int refId, String id) {
        for (int index = 0; index < messageList.size(); index++) {
            ChatMessage message = messageList.get(index);

            if (message.isMy && message.refId == refId) {
                message.id = id;
                message.isDevilry = true;
                notifyDataSetChanged();
                return message.message;
            }
        }

        return null;
    }
}