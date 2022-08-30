package starmeet.convergentmobile.com.starmeet.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import starmeet.convergentmobile.com.starmeet.Activites.ProfileCelebrityActivity;
import starmeet.convergentmobile.com.starmeet.Activites.VideoInitChatActivity;
import starmeet.convergentmobile.com.starmeet.Base.BaseActivity;
import starmeet.convergentmobile.com.starmeet.Helpers.ApproachingEventHelper;
import starmeet.convergentmobile.com.starmeet.Helpers.DateHelper;
import starmeet.convergentmobile.com.starmeet.Models.Celebrity;
import starmeet.convergentmobile.com.starmeet.Models.Event;
import starmeet.convergentmobile.com.starmeet.R;
import starmeet.convergentmobile.com.starmeet.Response.ApproachingEventResponse;
import starmeet.convergentmobile.com.starmeet.StarMeetApp;
import starmeet.convergentmobile.com.starmeet.Utils.LogUtil;

/**
 * Created by alexeysidorov on 23.03.2018.
 */

public class CelebrityBioProfileFragment extends Fragment implements ApproachingEventHelper.ApproachingListener,
        View.OnClickListener {
    private AppCompatImageView avatar;
    private AppCompatTextView bio;
    private View approachingEventView;
    private AppCompatTextView titleEvent;
    private AppCompatTextView dateEvent;
    private AppCompatTextView typeEvent;
    private AppCompatTextView timeEvent;
    private AppCompatTextView titleSmallEvent;
    private AppCompatButton starChatButton;
    private AppCompatTextView titleStarChat;
    private AppCompatTextView nickName;
    private ApproachingEventResponse event;
    private View mainBackground;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.celebrity_bio_profile_fragment, null);
    }

    @SuppressWarnings("ConstantConditions")
    @SuppressLint({"SetTextI18n", "CheckResult"})
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        getCelebrityInfo();
        initNotification();
    }

    @SuppressWarnings("ConstantConditions")
    private void initViews() {
        avatar = getActivity().findViewById(R.id.avatar_celebrity_bio);
        bio = getActivity().findViewById(R.id.celebrity_bio);
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

        mainBackground = getActivity().findViewById(R.id.main_approaching_background);
        @SuppressLint("ResourceType")
        Drawable drawable = VectorDrawableCompat.create(getResources(),
                R.drawable.approaching_background, null);
        mainBackground.setBackground(drawable);

        ApproachingEventHelper.getInstance().ApproachingEventListener(this);
    }

    private void getCelebrityInfo() {
        final Call<Celebrity> request = StarMeetApp.getApi().getCelebrity();
        request.enqueue(new Callback<Celebrity>() {
            @Override
            public void onResponse(@NonNull Call<Celebrity> call, @NonNull Response<Celebrity> response) {
                if (response.code() == 200 && response.body() != null) {
                    setCelebrity(Objects.requireNonNull(response.body()));
                } else
                    LogUtil.logE("Faq error", response.code() + ". Faq is empty");
            }

            @Override
            public void onFailure(@NonNull Call<Celebrity> call, @NonNull Throwable t) {
                LogUtil.logE("Celebrity error", t.getMessage(), t);
            }
        });
    }

    @SuppressLint("CheckResult")
    private void setCelebrity(Celebrity celebrity) {
        bio.setText(celebrity.bio != null && !celebrity.bio.isEmpty()
                ? celebrity.bio : "Not specified");

        if(getActivity() == null) return;

        Display display = Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        avatar.getLayoutParams().width = width - 125;
        avatar.getLayoutParams().height = width - 125;
        avatar.requestLayout();

        RequestOptions options = new RequestOptions();
        options.centerCrop();
        options.skipMemoryCache(true);
        Glide.with(getActivity()).load(celebrity.photoUrl).apply(options).into(avatar);
    }

    @Override
    public void ApproachingEvent(boolean isVisibleNotification) {
        initNotification();
    }

    @Override
    public void getTimeStartChat(final String time) {
        try {
            if(getActivity() == null) return;

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
        if(getActivity() == null) return;

        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (timeEvent != null && !isVisible)
                    timeEvent.setText("");

                if(event != null && event.offerType != null)
                titleSmallEvent.setText(event.enableCountdown ? "Scheduled to start in:" :
                        event.offerType.id == 1 ? "Event start time has passed but you can still start chat" :
                                "Event start time has passed");
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
            titleSmallEvent.setText(event.enableCountdown ? "Scheduled to start in:" :
                    event.offerType.id == 1 ? "Event start time has passed but you can still start chat" :
                            "Event start time has passed");
            starChatButton.setVisibility(event.showStartButton ? View.VISIBLE : View.GONE);
            titleStarChat.setVisibility(event.showStartButton ? View.GONE : View.VISIBLE);

            String statusEvent = event.isOnlineType ? "online" : "offline";
            String lotteryTitle = "This is an " + statusEvent + " event. " +
                    "Please refer to conditions of your Event for place or venue";
            String starchatTitle = "This is an" + statusEvent + " event." +
                    " \"Star Chat\" button will apper in place of this " +
                    "text 5 minutes before scheduled Start Time";
            titleStarChat.setText(event.offerType.id == 1 ? starchatTitle : lotteryTitle);
            nickName.setText(event.offerType.id == 1 ? event.secondPartName : "Not specified");
        }

        if(getActivity() == null) return;

        ProfileCelebrityActivity activity = (ProfileCelebrityActivity) getActivity();
        if (activity != null)
            activity.setIconNotification(event != null && event.id > 0, null);
    }

    @Override
    public void onClick(View v) {
        ApproachingEventResponse eventAppr = ApproachingEventHelper.getInstance().getApproachingEvent();
        getEventInfo(eventAppr.id, new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (response.code() == 200 && response.body() != null) {
                    if(getActivity() == null) return;

                    Event event = response.body();
                    if (event != null) {
                        Gson gson = new Gson();
                        String json = gson.toJson(event);
                        Intent intent = new Intent(getActivity(), VideoInitChatActivity.class);
                        intent.putExtra("Event", json);
                        intent.putExtra("NickName", eventAppr.secondPartName);
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
               try {
                   callback.onResponse(call, response);
               }
               catch(Exception exp){

               }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }
}
