package starmeet.convergentmobile.com.starmeet.Holders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.daimajia.swipe.SwipeLayout;

import starmeet.convergentmobile.com.starmeet.Helpers.DateHelper;
import starmeet.convergentmobile.com.starmeet.Models.PurchasedModel;
import starmeet.convergentmobile.com.starmeet.R;

/**
 * Created by alexeysidorov on 20.03.2018.
 */

public class EventsPurchaseHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final AppCompatTextView date;
    private final AppCompatTextView name;
    private final AppCompatImageView avatar;
    private final AppCompatTextView description;
    private final AppCompatImageView swipeLeft;
    private final AppCompatImageView swipeRight;
    private final LinearLayout infoBlock;
    private final SwipeLayout swipeLayout;
    private final AppCompatTextView eventType;
    private final AppCompatTextView eventTime;
    private final AppCompatTextView category;
    private final AppCompatTextView status;
    private Context context;

    @SuppressLint("CutPasteId")
    public EventsPurchaseHolder(View itemView) {
        super(itemView);

        date = itemView.findViewById(R.id.date_celebrity);
        name = itemView.findViewById(R.id.name_celebrity);
        avatar = itemView.findViewById(R.id.avatar_celebrity);
        description = itemView.findViewById(R.id.description_celebrity);
        swipeLeft = itemView.findViewById(R.id.swipe_left);
        swipeRight = itemView.findViewById(R.id.swipe_right);
        infoBlock = itemView.findViewById(R.id.info_block);
        swipeLayout = itemView.findViewById(R.id.swipe_block);
        eventType = itemView.findViewById(R.id.purchase_event);
        eventTime = itemView.findViewById(R.id.event_time);
        category = itemView.findViewById(R.id.category);
        status = itemView.findViewById(R.id.event_status);
    }

    @SuppressLint({"CheckResult", "SetTextI18n"})
    public void Bind(Context context, PurchasedModel model) {
        this.context = context;

        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        name.setText(model.celebrity.firstName + " " + model.celebrity.lastName);
        description.setText(model.shortDescription);
        date.setText(DateHelper.UtcToDate(model.startUtcTime, "EEE, d MMM yyyy hh:mm a"));

        RequestOptions options = new RequestOptions();
        options.diskCacheStrategy(DiskCacheStrategy.ALL);
        options.centerCrop();
        Glide.with(context).load(model.celebrity.photoUrl).apply(options).into(avatar);

        swipeRight.setOnClickListener(this);
        swipeLeft.setOnClickListener(this);

        eventType.setText(model.type.title);
        eventTime.setText(DateHelper.UtcToDate(model.startUtcTime, "EEE, d MMM yyyy hh:mm a"));
        category.setText(model.category.title);

        if (model.raffleStatus == null && model.directPurchaseStatus == null)
            return;

        status.setText(model.raffleStatus != null ? model.raffleStatus.title : model.directPurchaseStatus);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.swipe_left: {
                swipeLayout.open();
                break;
            }
            case R.id.swipe_right: {
                swipeLayout.close();
                break;
            }
        }
    }

    public void resizeView() {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int widthDefault = avatar.getLayoutParams().width + swipeLeft.getLayoutParams().width;
        infoBlock.getLayoutParams().width = width - widthDefault - 85;
        infoBlock.invalidate();
    }
}
