package starmeet.convergentmobile.com.starmeet.Holders;


import android.graphics.Color;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import starmeet.convergentmobile.com.starmeet.R;

public class FaqGroupHolder extends GroupViewHolder {

    private AppCompatTextView titleFaq;
    private AppCompatImageView imgButton;

    public FaqGroupHolder(View itemView) {
        super(itemView);
        titleFaq = itemView.findViewById(R.id.faq_title_group);
        titleFaq.setTextColor(Color.parseColor("#000000"));
        imgButton = itemView.findViewById(R.id.img_button);
        imgButton.setImageResource(R.drawable.arrow_black);
    }

    public void setTitle(String title) {
        titleFaq.setText(title);
    }

    @Override
    public void expand() {
        imgButton.setImageResource(R.drawable.arrow_up);
        titleFaq.setTextColor(Color.parseColor("#DE009E"));
    }

    @Override
    public void collapse() {
        imgButton.setImageResource(R.drawable.arrow_black);
        titleFaq.setTextColor(Color.parseColor("#000000"));
    }
}
