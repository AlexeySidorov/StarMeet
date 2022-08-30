package starmeet.convergentmobile.com.starmeet.Holders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import starmeet.convergentmobile.com.starmeet.Helpers.DpPxHelper;
import starmeet.convergentmobile.com.starmeet.Models.MainSlider;
import starmeet.convergentmobile.com.starmeet.R;

/**
 * Created by alexeysidorov on 20.03.2018.
 */

public class MainSliderHolder extends RecyclerView.ViewHolder {

    private final AppCompatTextView title;
    private final AppCompatTextView smallTitle;
    private final AppCompatImageView img;
    private final CardView cardView;

    public MainSliderHolder(View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.title);
        smallTitle = itemView.findViewById(R.id.small_title);
        img = itemView.findViewById(R.id.img_url);
        cardView = itemView.findViewById(R.id.slider_car_view);
    }

    @SuppressLint("CheckResult")
    public void Bind(Context context, MainSlider model, boolean isLastItem) {
        title.setText(model.name);
        smallTitle.setText(model.title);

        if (isLastItem) {
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
            layoutParams.setMargins((int)DpPxHelper.dpToPx(context, 12), 0,
                    (int)DpPxHelper.dpToPx(context, 10), 0);
            cardView.requestLayout();
        }

        RequestOptions options = new RequestOptions();
        options.centerCrop();
        options.diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(context).load(model.url).apply(options).into(img);
    }
}
