package starmeet.convergentmobile.com.starmeet.Fragments;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.io.IOException;

import starmeet.convergentmobile.com.starmeet.Models.Event;
import starmeet.convergentmobile.com.starmeet.R;

/**
 * Created by alexeysidorov on 23.03.2018.
 */

public class CelebrityBioFragment extends Fragment {
    private Event event;
    private AppCompatImageView avatar;
    private AppCompatTextView bio;
    private AppCompatTextView name;
    private RelativeLayout layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.celebrity_bio_fragment, null);
    }

    @SuppressWarnings("ConstantConditions")
    @SuppressLint({"SetTextI18n", "CheckResult"})
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        Bundle bundle = getArguments();
        event = new Event();

        if (bundle != null) {
            Gson mapper = new Gson();
            String json = bundle.getString("event_model");

            event = mapper.fromJson(json, Event.class);
        }

        if (event == null || event.celebrity == null) return;

        name.setText(event.celebrity.firstName != null && !event.celebrity.firstName.isEmpty()
                || event.celebrity.lastName != null && !event.celebrity.lastName.isEmpty()
                ? event.celebrity.firstName + " " + event.celebrity.lastName
                : "Not specified");
        bio.setText(event.celebrity.bio != null && !event.celebrity.bio.isEmpty()
                ? event.celebrity.bio : "Not specified");

        if(getActivity() == null) return;

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        avatar.getLayoutParams().width = width / 2 + 50;
        avatar.getLayoutParams().height = width / 2 + 50;
        avatar.requestLayout();

        layout.getLayoutParams().width = width / 2 - 50;
        layout.getLayoutParams().height = width / 2 + 80;
        layout.requestLayout();

        RequestOptions options = new RequestOptions();
        options.centerCrop();
        options.skipMemoryCache(true);
        Glide.with(getActivity()).load(event.celebrity.photoUrl).apply(options).into(avatar);
    }

    @SuppressWarnings("ConstantConditions")
    private void initViews() {
        avatar = getActivity().findViewById(R.id.avatar_celebrity_bio);
        bio = getActivity().findViewById(R.id.celebrity_bio);
        name = getActivity().findViewById(R.id.celebrity_name_bio);
        layout = getActivity().findViewById(R.id.name_block);
    }
}
