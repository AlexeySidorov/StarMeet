package starmeet.convergentmobile.com.starmeet.Activites;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.google.gson.Gson;


import starmeet.convergentmobile.com.starmeet.Base.BaseActivity;
import starmeet.convergentmobile.com.starmeet.Callbaks.DialogCallback;
import starmeet.convergentmobile.com.starmeet.Communicators.ActivityCommunicator;
import starmeet.convergentmobile.com.starmeet.Fragments.VideoChatCallFragment;
import starmeet.convergentmobile.com.starmeet.Fragments.VideoInitChatFragment;
import starmeet.convergentmobile.com.starmeet.Models.Event;
import starmeet.convergentmobile.com.starmeet.MyContext;
import starmeet.convergentmobile.com.starmeet.R;
import starmeet.convergentmobile.com.starmeet.Services.DialogService;

public class VideoInitChatActivity extends BaseActivity implements ActivityCommunicator<String> {

    private FragmentManager fr;
    private Bundle bundle;
    private Event event;
    private VideoInitChatFragment chatInitFragment;
    private VideoChatCallFragment chatCallFragment;
    private int code;
    private String nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLayout(R.layout.activity_base);
        setOnlyMyViews(false);
        setToolBarMenuButton(false);
        setColorToolbarMainButton(R.color.white);
        setSelfMenuItem(R.id.star_chat);
        super.onCreate(savedInstanceState);
        setTitle("");
        setHideMainButtonInToolbar();
        setTypeScreen(0);
    }

    @Override
    protected void initViews2() {
        initData();

        if (event == null || event.id == 0) {
            onBackPressed();
            return;
        }

        fr = getSupportFragmentManager();
        chatInitFragment = new VideoInitChatFragment();
        bundle = new Bundle();

        bundle.putString("NickName", MyContext.getInstance().userRole == 1 ? event.celebrity.firstName + " " + event.celebrity.lastName : nickname);

        if (MyContext.getInstance().userRole == 1)
            bundle.putString("Avatar", event.celebrity.photoUrl);

        bundle.putInt("EventId", event.id);
        chatInitFragment.setArguments(bundle);
        fr.beginTransaction().add(R.id.container, chatInitFragment, " VideoInitChatFragment").commit();

        chatCallFragment = new VideoChatCallFragment();
    }

    private void initData() {
        Intent intent = getIntent();
        String json = intent.getStringExtra("Event");

        if (json == null || json.isEmpty()) {
            onBackPressed();
            return;
        }

        nickname = intent.getStringExtra("NickName");

        Gson gson = new Gson();
        event = gson.fromJson(json, Event.class);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults[0] == -1) {
            DialogService.MessageDialog(this, "Camera",
                    "Access to the camera is prohibited. Establishing a video chat is not possible.",
                    "OK", new DialogCallback<DialogService.ButtonType>() {
                        @Override
                        public void onResult(DialogService.ButtonType result) {
                            onBackPressed();
                        }

                        @Override
                        public void onClose() {
                            onBackPressed();
                        }
                    });
        } else if (requestCode == 2 && grantResults[0] == -1) {
            DialogService.MessageDialog(this, "Microphone",
                    "Access to the record audio is prohibited. Establishing a video chat is not possible.",
                    "OK", new DialogCallback<DialogService.ButtonType>() {
                        @Override
                        public void onResult(DialogService.ButtonType result) {
                            onBackPressed();
                        }

                        @Override
                        public void onClose() {
                            onBackPressed();
                        }
                    });
        } else if (requestCode == 3 && grantResults[0] == -1) {
            DialogService.MessageDialog(this, "Camera and Microphone",
                    "Access to the camera and record audio is prohibited. Establishing a video chat is not possible.",
                    "OK", new DialogCallback<DialogService.ButtonType>() {
                        @Override
                        public void onResult(DialogService.ButtonType result) {
                            onBackPressed();
                        }

                        @Override
                        public void onClose() {
                            onBackPressed();
                        }
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (code == 2 || code == 0) {
            bundle.putBoolean("Stop", true);
            chatInitFragment.setArguments(bundle);
            fr.beginTransaction().replace(R.id.container, chatInitFragment, " VideoInitChatFragment").commit();
        }
    }

    @Override
    public void passDataToActivity(String value, int code) {
        this.code = code;
        if (value != null)
            bundle.putString("ice_server_urls", value);

        if (code == 1) {
            chatCallFragment.setArguments(bundle);
            fr.beginTransaction().replace(R.id.container, chatCallFragment, "VideoChatCallFragment").commit();
        } else if (code == 0) {
            chatInitFragment.setArguments(bundle);
            fr.beginTransaction().replace(R.id.container, chatInitFragment, " VideoInitChatFragment").commit();
        } else if (code == -1) {
            bundle.putBoolean("Stop", true);
            chatInitFragment.setArguments(bundle);
            fr.beginTransaction().replace(R.id.container, chatInitFragment, " VideoInitChatFragment").commit();
        } else if (code == 2) {
            Intent intent = new Intent(this, VideoChatActivity.class);
            intent.putExtra("video_chat_model", bundle);
            startActivity(intent);
        }
    }
}
