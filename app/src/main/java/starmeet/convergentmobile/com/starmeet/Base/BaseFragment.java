package starmeet.convergentmobile.com.starmeet.Base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Objects;

import starmeet.convergentmobile.com.starmeet.Activites.AuthActivity;
import starmeet.convergentmobile.com.starmeet.Activites.MainActivity;
import starmeet.convergentmobile.com.starmeet.Activites.ProfileCelebrityActivity;
import starmeet.convergentmobile.com.starmeet.Activites.ProfileUserActivity;
import starmeet.convergentmobile.com.starmeet.Adapters.EventsAdapter;
import starmeet.convergentmobile.com.starmeet.Callbaks.CallbackEmptyFunc;
import starmeet.convergentmobile.com.starmeet.Helpers.NavigationHelper;
import starmeet.convergentmobile.com.starmeet.Models.ActivityNextType;
import starmeet.convergentmobile.com.starmeet.MyContext;
import starmeet.convergentmobile.com.starmeet.R;

import static starmeet.convergentmobile.com.starmeet.Models.NavigationModel.None;

public class BaseFragment extends Fragment {
    private AppCompatActivity activity;
    private Toolbar toolbar;

    @SuppressLint("ResourceAsColor")
    public void setBackgroundToolbar(int color) {
        activity = ((AppCompatActivity) Objects.requireNonNull(getActivity()));
        toolbar = activity.findViewById(R.id.toolbar);
        Objects.requireNonNull(activity.getSupportActionBar()).setBackgroundDrawable(
                new ColorDrawable(ContextCompat.getColor(activity, color)));
    }

    @SuppressLint("ResourceAsColor")
    public void setTitleToolbar(String title) {
        if (activity == null || toolbar == null) return;

        AppCompatTextView textView = new AppCompatTextView(activity);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);

        textView.setMaxLines(1);
        textView.setText(title);
        textView.setLayoutParams(params);
        textView.setTextSize(21);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }

        toolbar.addView(textView);
    }

    public void setIconChat(int iconId, final CallbackEmptyFunc callback) {
        if (activity == null || toolbar == null) return;

        ActionBar actionBar = activity.getSupportActionBar();
        Objects.requireNonNull(actionBar).setIcon(iconId);

        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        if (toolbar == null) return;

        for (int index = 0; index < toolbar.getChildCount(); index++) {
            View view = toolbar.getChildAt(index);

            if (view == null || callback == null) return;
            if (view instanceof AppCompatImageView) {
                AppCompatImageView logoNotif = (AppCompatImageView) view;

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.Click();
                    }
                });
            }
        }
    }

    public void searchEvents(String searchLine) {
        Intent i = new Intent(getActivity(), MainActivity.class);
        i.putExtra("SearchLine", searchLine);
        Objects.requireNonNull(getActivity()).startActivity(i);
    }

    public void searchEvents(String searchLine, EventsAdapter eventAdapter) {
        if (eventAdapter != null)
            eventAdapter.searchEvent(searchLine);
    }

    public void navigationProfile() {
        if (MyContext.getInstance().isAuthUser) {
            Intent i;

            if (MyContext.getInstance().userRole == 1)
                i = new Intent(getActivity(), ProfileUserActivity.class);
            else
                i = new Intent(getActivity(), ProfileCelebrityActivity.class);

            Objects.requireNonNull(getActivity()).startActivity(i);

        } else {
            HashMap<ActivityNextType, Class> activities = new HashMap<>();
            activities.put(ActivityNextType.Activity1, ProfileUserActivity.class);
            activities.put(ActivityNextType.Activity2, ProfileCelebrityActivity.class);
            NavigationHelper.getInstance().setNextActivity(activities, None);

            Intent i = new Intent(getContext(), AuthActivity.class);
            startActivity(i);
        }
    }
}
