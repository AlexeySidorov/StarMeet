package starmeet.convergentmobile.com.starmeet.Holders;

import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import starmeet.convergentmobile.com.starmeet.R;

public class FaqChildHolder extends ChildViewHolder {

    private AppCompatTextView faqText;

    public FaqChildHolder(View itemView) {
        super(itemView);

        faqText = itemView.findViewById(R.id.faq_txt);
    }

    public void setFaqText(String text) {
        faqText.setText(text);
    }
}
