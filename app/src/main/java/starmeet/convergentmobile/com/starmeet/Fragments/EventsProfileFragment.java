package starmeet.convergentmobile.com.starmeet.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import starmeet.convergentmobile.com.starmeet.Activites.VideoInitChatActivity;
import starmeet.convergentmobile.com.starmeet.Adapters.CustomViewPagerItemAdapter;
import starmeet.convergentmobile.com.starmeet.Adapters.ProfileViewPagerItemAdapter;
import starmeet.convergentmobile.com.starmeet.Base.BaseActivity;
import starmeet.convergentmobile.com.starmeet.Helpers.ApproachingEventHelper;
import starmeet.convergentmobile.com.starmeet.Helpers.DateHelper;
import starmeet.convergentmobile.com.starmeet.Models.Event;
import starmeet.convergentmobile.com.starmeet.R;
import starmeet.convergentmobile.com.starmeet.Response.ApproachingEventResponse;
import starmeet.convergentmobile.com.starmeet.StarMeetApp;
import starmeet.convergentmobile.com.starmeet.Utils.LogUtil;

/**
 * Created by alexeysidorov on 28.03.2018.
 */

public class EventsProfileFragment extends Fragment implements ApproachingEventHelper.ApproachingListener,
        View.OnClickListener {
    private ViewPager viewPager;
    private SmartTabLayout viewPagerTab;
    private View approachingEventView;
    private AppCompatTextView titleEvent;
    private AppCompatTextView dateEvent;
    private AppCompatTextView typeEvent;
    private AppCompatTextView timeEvent;
    private AppCompatTextView titleSmallEvent;
    private AppCompatButton starChatButton;
    private AppCompatTextView titleStarChat;
    private CustomViewPagerItemAdapter adapter;
    private AppCompatTextView nickName;
    private ApproachingEventResponse event;
    private FragmentPagerItems items;
    private View nested;
    private View mainBackground;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.events_profile_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initPager();
        initNotification();
    }

    private void initViews() {
        viewPager = Objects.requireNonNull(getActivity()).findViewById(R.id.viewpager);
        viewPagerTab = getActivity().findViewById(R.id.view_pager_tab);
        approachingEventView = getActivity().findViewById(R.id.approaching_event);
        titleEvent = getActivity().findViewById(R.id.title_approaching_event);
        dateEvent = getActivity().findViewById(R.id.date_time_event);
        typeEvent = getActivity().findViewById(R.id.event_type_value);
        timeEvent = getActivity().findViewById(R.id.time_approaching_event);
        titleSmallEvent = getActivity().findViewById(R.id.title_small);
        titleStarChat = getActivity().findViewById(R.id.title_star_chat);
        starChatButton = getActivity().findViewById(R.id.star_chat_button);
        starChatButton.setOnClickListener(this);
        nickName = getActivity().findViewById(R.id.nick_name);
        nested = getActivity().findViewById(R.id.nested);
        mainBackground = getActivity().findViewById(R.id.main_approaching_background);

        @SuppressLint("ResourceType")
        Drawable drawable = VectorDrawableCompat.create(getActivity().getResources(),
                R.drawable.approaching_background, null);
        mainBackground.setBackground(drawable);

        ApproachingEventHelper.getInstance().ApproachingEventListener(this);
    }

    private void initPager() {
        ProfileViewPagerItemAdapter adapter = new ProfileViewPagerItemAdapter(
                Objects.requireNonNull(getChildFragmentManager()),
                FragmentPagerItems.with(getContext())
                        .add(R.string.profile_upcoming, UpcomingProfileEventFragment.class)
                        .add(R.string.profile_past, PastProfileEventFragment.class)
                        .create());
        viewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(viewPager);

        final LinearLayout lyTabs = (LinearLayout) viewPagerTab.getChildAt(0);
        changeTabsTitleTypeFace(lyTabs, 0);

        viewPagerTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                changeTabsTitleTypeFace(lyTabs, i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public void ApproachingEvent(boolean isVisibleNotification) {
        initNotification();
    }

    @Override
    public void getTimeStartChat(final String time) {
        try {
            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    timeEvent.setText(time);
                }
            });
        } catch (Exception exp) {

        }
    }

    @Override
    public void visibleTimer(boolean isVisible) {
        if (getActivity() == null) return;

        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (timeEvent != null && !isVisible)
                    timeEvent.setText("");

                if (event != null && event.id > 0)
                    titleSmallEvent.setText(isVisible ? "Scheduled to start in:" : "" +
                            "Event start time has passed but you can still start chat");
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void initNotification() {
        event = ApproachingEventHelper.getInstance().getApproachingEvent();
        approachingEventView.setVisibility(event != null && event.id > 0 ? View.VISIBLE : View.GONE);

        if (event != null && event.id > 0) {
            dateEvent.setText(DateHelper.UtcToDate(event.startUtcTime, "EEE, d MMM yyyy hh:mm a"));
            typeEvent.setText(event.offerType.title);
            titleEvent.setText(event.enableCountdown ? "Event is Approaching!" : "Event is ON now!");
            titleSmallEvent.setText(event.enableCountdown ? "Scheduled to start in:" : "" +
                    "Event start time has passed but you can still start chat");
            starChatButton.setVisibility(event.showStartButton ? View.VISIBLE : View.GONE);
            titleStarChat.setVisibility(event.showStartButton ? View.GONE : View.VISIBLE);
            String statusEvent = event.isOnlineType ? "online" : "offline";
            titleStarChat.setText("This is " + statusEvent + " event. \"Star Chat\" button will apper in place of this " +
                    "text 5 minutes before scheduled Start Time");
            nickName.setText(event.secondPartName);
        }

        BaseActivity activity = (BaseActivity) getActivity();
        if (activity != null)
            activity.setIconNotification(event != null && event.id > 0, null);
    }

    @Override
    public void onClick(View v) {
        ApproachingEventResponse event = ApproachingEventHelper.getInstance().getApproachingEvent();
        getEventInfo(event.id, new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (response.code() == 200 && response.body() != null) {
                    Event event = response.body();
                    if (event != null) {
                        Gson gson = new Gson();
                        String json = gson.toJson(event);
                        Intent intent = new Intent(getActivity(), VideoInitChatActivity.class);
                        intent.putExtra("Event", json);
                        startActivity(intent);
                    }

                } else
                    LogUtil.logE("Events error", response.code() + ". Event is empty");
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                LogUtil.logE("Event error", t.getMessage(), t);
            }
        });
    }

    private void getEventInfo(int eventId, Callback<Event> callback) {
        Call<Event> response = StarMeetApp.getApi().getEventById(eventId);
        response.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    public void changeTabsTitleTypeFace(LinearLayout ly, int position) {
        for (int j = 0; j < ly.getChildCount(); j++) {
            TextView tvTabTitle = (TextView) ly.getChildAt(j);
            tvTabTitle.setTypeface(null, Typeface.NORMAL);
            if (j == position) tvTabTitle.setTypeface(null, Typeface.BOLD);
        }
    }
}
