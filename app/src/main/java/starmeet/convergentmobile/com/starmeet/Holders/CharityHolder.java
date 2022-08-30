package starmeet.convergentmobile.com.starmeet.Holders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import starmeet.convergentmobile.com.starmeet.Listners.HolderClickListener;
import starmeet.convergentmobile.com.starmeet.Models.Charity;
import starmeet.convergentmobile.com.starmeet.R;

/**
 * Created by alexeysidorov on 20.03.2018.
 */

public class CharityHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final AppCompatImageView avatar;
    private HolderClickListener<Charity> listener;
    private View itemView;

    public CharityHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        avatar = itemView.findViewById(R.id.avatar_charity);
    }

    @SuppressLint("CheckResult")
    public void Bind(Context context, Charity model) {
        RequestOptions options = new RequestOptions();
        options.centerCrop();
        options.skipMemoryCache(true);
        Glide.with(context).load(model.LogoUrl).apply(options).into(avatar);

        avatar.setOnClickListener(this);
        itemView.setTag(model);
    }

    public void setClickListener(HolderClickListener<Charity> listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.clickElement((Charity) itemView.getTag(), -1);
    }
}
