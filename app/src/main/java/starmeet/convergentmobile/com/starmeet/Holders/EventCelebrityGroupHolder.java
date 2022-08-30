package starmeet.convergentmobile.com.starmeet.Holders;


import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

import starmeet.convergentmobile.com.starmeet.R;

public class EventCelebrityGroupHolder extends RecyclerView.ViewHolder {
    private AppCompatTextView titleGroup;
    private AppCompatTextView statusGroup;
    private AppCompatImageView imgButton;
    private Context cxt;

    public EventCelebrityGroupHolder(View itemView) {
        super(itemView);

        titleGroup = itemView.findViewById(R.id.group_title);
        statusGroup = itemView.findViewById(R.id.status_title);
        imgButton = itemView.findViewById(R.id.img_button);
        imgButton.setImageResource(R.drawable.arrow_green_small);
    }

    public void resizeView() {
        DisplayMetrics metrics = cxt.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int widthDefault = imgButton.getLayoutParams().width;
        titleGroup.getLayoutParams().width = width - widthDefault - 85;
        titleGroup.invalidate();
    }

    public void setContext(Context cxt) {
        this.cxt = cxt;
    }

    public void setTitle(String title) {
        titleGroup.setText(title);
    }

    public void setStatus(String status) {
        statusGroup.setText(status);
    }

    public void expand() {
        imgButton.setImageResource(R.drawable.arrow_up);
    }

    public void collapse() {
        imgButton.setImageResource(R.drawable.arrow_green_small);
    }
}
