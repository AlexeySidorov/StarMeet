package starmeet.convergentmobile.com.starmeet.Holders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import de.hdodenhof.circleimageview.CircleImageView;
import starmeet.convergentmobile.com.starmeet.Models.ChatMessage;
import starmeet.convergentmobile.com.starmeet.R;

public class ChatLeftMessageHolder extends RecyclerView.ViewHolder {
    private AppCompatTextView leftMessage;
    private CircleImageView avatarAnotherUser;

    public ChatLeftMessageHolder(View itemView) {
        super(itemView);

        leftMessage = itemView.findViewById(R.id.left_msgr);
        avatarAnotherUser = itemView.findViewById(R.id.avatar_another_user);
    }

    @SuppressLint("CheckResult")
    public void Bind(Context ctx, ChatMessage message) {
        leftMessage.setText(message.message);

        RequestOptions options = new RequestOptions();
        options.diskCacheStrategy(DiskCacheStrategy.ALL);
        options.centerCrop();
        Glide.with(ctx).load(message.avatarUrl).apply(options).into(avatarAnotherUser);
    }
}
