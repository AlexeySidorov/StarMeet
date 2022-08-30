package starmeet.convergentmobile.com.starmeet.Holders;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import starmeet.convergentmobile.com.starmeet.Models.ChatMessage;
import starmeet.convergentmobile.com.starmeet.R;

public class ChatRightMessageHolder extends RecyclerView.ViewHolder {
    private AppCompatTextView status;
    private AppCompatTextView rightMessage;

    public ChatRightMessageHolder(View itemView) {
        super(itemView);

        rightMessage = itemView.findViewById(R.id.right_msgr);
        status = itemView.findViewById(R.id.status);
    }

    public void Bind(ChatMessage message) {
        rightMessage.setText(message.message);
        status.setVisibility(message.isDevilry ? View.VISIBLE : View.GONE);
    }
}
