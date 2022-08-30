package starmeet.convergentmobile.com.starmeet.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;



import starmeet.convergentmobile.com.starmeet.Models.Event;
import starmeet.convergentmobile.com.starmeet.R;


/**
 * Created by alexeysidorov on 23.03.2018.
 */

public class CauseCelebrityFragment extends Fragment implements View.OnClickListener {

    private Event event;
    private WebView web;
    private AppCompatButton webButton;
    private AppCompatImageView img;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_charity, null);
    }

    @SuppressWarnings("ConstantConditions")
    @SuppressLint("CheckResult")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();

        Bundle bundle = getArguments();
        event = new Event();

        if (bundle != null) {
            Gson mapper = new Gson();
            String json = bundle.getString("event_model");
            event = mapper.fromJson(json, Event.class);
        }

        if (event == null || event.charity == null ||
                event.charity.webSiteTitle == null || event.charity.webSiteTitle.isEmpty() ||
                event.charity.Description == null || event.charity.Description.isEmpty()) return;

        RequestOptions options = new RequestOptions();
        options.fitCenter();
        options.skipMemoryCache(true);
        Glide.with(getActivity()).load(event.charity.LogoUrl).apply(options).into(img);
        webButton.setText(event.charity.webSiteTitle);
        webButton.setOnClickListener(this);
        web.loadDataWithBaseURL(null, event.charity.Description, "text/html", "utf-8", null);
    }

    @SuppressWarnings("ConstantConditions")
    @SuppressLint("SetJavaScriptEnabled")
    private void initViews() {
        img = getActivity().findViewById(R.id.img);
        webButton = getActivity().findViewById(R.id.web_button);
        web = getActivity().findViewById(R.id.description);
        web.getSettings().setJavaScriptEnabled(true);
    }

    @Override
    public void onClick(View v) {

        if (event == null || event.charity == null ||
                event.charity.url == null || event.charity.url.isEmpty()) return;

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.charity.url));
        startActivity(browserIntent);
    }
}
