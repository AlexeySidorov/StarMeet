package starmeet.convergentmobile.com.starmeet.Activites;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import java.util.Objects;

import starmeet.convergentmobile.com.starmeet.Base.BaseActivity;
import starmeet.convergentmobile.com.starmeet.Callbaks.DialogCallback;
import starmeet.convergentmobile.com.starmeet.Communicators.ActivityCommunicator;
import starmeet.convergentmobile.com.starmeet.Fragments.ChatFragment;
import starmeet.convergentmobile.com.starmeet.Fragments.VideoChatFragment;
import starmeet.convergentmobile.com.starmeet.R;
import starmeet.convergentmobile.com.starmeet.Services.DialogService;
import starmeet.convergentmobile.com.starmeet.WebRtc.SignalingClient;

public class VideoChatActivity extends BaseActivity implements ActivityCommunicator<Integer> {

    private Bundle bundle;
    private FragmentManager fr;
    private ChatFragment chatFragment;
    private VideoChatFragment videoChatFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLayout(R.layout.activity_base);
        setOnlyMyViews(false);
        setToolBarMenuButton(false);
        setColorToolbarMainButton(R.color.white);
        super.onCreate(savedInstanceState);
        setColorToolbarTitle(R.color.colorAccent);
        removeToolbarBottomLine();
        setTypeScreen(0);
        setVisibleNavigationButton();
        setTitle("");
    }

    @Override
    protected void initViews2() {
        initData();
        fr = getSupportFragmentManager();
        videoChatFragment = new VideoChatFragment();
        videoChatFragment.setArguments(bundle);
        fr.beginTransaction().add(R.id.container, videoChatFragment, "VideoChatFragment")
                .addToBackStack("VideoChatFragment").commit();
        chatFragmentInit();
    }

    private void chatFragmentInit() {
        chatFragment = new ChatFragment();
        chatFragment.setArguments(bundle);
    }

    private void initData() {
        Intent intent = getIntent();
        bundle = intent.getBundleExtra("video_chat_model");
    }

    @Override
    public void passDataToActivity(Integer value, int code) {
        if (code == 1) {
            fr.beginTransaction().replace(R.id.container, videoChatFragment, "VideoChatFragment")
                    .addToBackStack("VideoChatFragment").commit();

        } else if (code == 2) {
            fr.beginTransaction().replace(R.id.container, chatFragment, "ChatFragment")
                    .addToBackStack("ChatFragment").commit();
        }
    }

    @Override
    public void onBackPressed() {
        DialogService.MessageDialog(Objects.requireNonNull(this),
                "Call", "Do you want to end the call?", "Yes, end the call",
                new DialogCallback<DialogService.ButtonType>() {
                    @Override
                    public void onResult(DialogService.ButtonType result) {
                        SignalingClient.getInstance().closeConnection();
                    }

                    @Override
                    public void onClose() {

                    }
                });
    }
}
