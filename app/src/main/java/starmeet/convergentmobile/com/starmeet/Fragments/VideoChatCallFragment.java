package starmeet.convergentmobile.com.starmeet.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.skyfishjy.library.RippleBackground;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import starmeet.convergentmobile.com.starmeet.Communicators.ActivityCommunicator;
import starmeet.convergentmobile.com.starmeet.MyContext;
import starmeet.convergentmobile.com.starmeet.R;
import starmeet.convergentmobile.com.starmeet.Utils.LogUtil;
import starmeet.convergentmobile.com.starmeet.WebRtc.SignalingClient;
import starmeet.convergentmobile.com.starmeet.WebRtc.SignalingInitInterface;
import starmeet.convergentmobile.com.starmeet.WebRtc.SocketModel;

import static org.webrtc.ContextUtils.getApplicationContext;


public class VideoChatCallFragment extends Fragment implements View.OnClickListener, SignalingInitInterface {
    private AppCompatButton accept;
    private AppCompatButton decline;
    private CircleImageView avatar;
    private AppCompatTextView nickname;
    private ActivityCommunicator<String> activityCommunicator;
    private RippleBackground indicator;
    private View celebrityText;
    private MediaPlayer myMediaPlayer;
    private boolean isMediaPlayerRelease;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_videochat_call, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initListener();
        initDate();
        SignalingClient.getInstance().setInitSignalingClient(this);
        isMediaPlayerRelease = false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            activityCommunicator = (ActivityCommunicator<String>) context;
        } catch (Exception exp) {
            LogUtil.logE("VideoChatFragment", exp.toString());
        }
    }

    private void initViews() {
        accept = Objects.requireNonNull(getActivity()).findViewById(R.id.accept);
        decline = Objects.requireNonNull(getActivity()).findViewById(R.id.decline);
        nickname = Objects.requireNonNull(getActivity()).findViewById(R.id.nick_name);
        avatar = Objects.requireNonNull(getActivity()).findViewById(R.id.avatar_another_user);
        indicator = Objects.requireNonNull(getActivity()).findViewById(R.id.call_indicator);
        celebrityText = Objects.requireNonNull(getActivity()).findViewById(R.id.small_title_block_call);
    }

    private void initListener() {
        accept.setOnClickListener(this);
        decline.setOnClickListener(this);
    }

    @SuppressLint("CheckResult")
    private void initDate() {
        Bundle bundle = getArguments();
        assert bundle != null;
        String nick = bundle.getString("NickName", "");
        String avatarUrl = bundle.getString("Avatar");
        myMediaPlayer = MediaPlayer.create(getActivity(), R.raw.call_sound);
        myMediaPlayer.setVolume(100 * 0.1f, 100 * 0.1f);

        try {
            myMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    if (getActivity() == null || myMediaPlayer == null) return;

                    mp = MediaPlayer.create(getActivity(), R.raw.call_sound);
                    mp.start();
                    mp.setOnCompletionListener(this);
                }
            });
        } catch (Exception ex) {
            if (myMediaPlayer != null) {
                myMediaPlayer.pause();
                myMediaPlayer.release();
                myMediaPlayer = null;
                isMediaPlayerRelease = false;
            }
        }

        if (MyContext.getInstance().userRole == 2) {
            celebrityText.setVisibility(View.VISIBLE);
            indicator.setVisibility(View.GONE);
        } else {
            indicator.startRippleAnimation();
            myMediaPlayer.start();
            isMediaPlayerRelease = true;
        }

        nickname.setText(nick);

        if (MyContext.getInstance().userRole == 1) {
            RequestOptions options = new RequestOptions();
            options.centerCrop();
            options.diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(Objects.requireNonNull(getActivity())).load(avatarUrl).apply(options).into(avatar);
        } else {
            avatar.setVisibility(View.GONE);
        }

        if (MyContext.getInstance().userRole == 2) {
            accept.setText("Start");
            decline.setText("Disconnect");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.accept: {

                if (MyContext.getInstance().userRole == 2) {
                    SocketModel model1 = new SocketModel();
                    model1.type = SocketModel.TypeMessage.callUser;
                    SignalingClient.getInstance().sendMessage(model1);
                    accept.setEnabled(false);
                    celebrityText.setVisibility(View.GONE);
                    indicator.setVisibility(View.VISIBLE);
                    indicator.startRippleAnimation();
                    myMediaPlayer.start();
                    isMediaPlayerRelease = true;
                } else {
                    activityCommunicator.passDataToActivity(null, 2);
                    isMediaPlayerRelease = true;
                }
                break;
            }
            case R.id.decline: {
                SocketModel model = new SocketModel();
                model.type = MyContext.getInstance().userRole == 2 ? SocketModel.TypeMessage.callEnd : SocketModel.TypeMessage.callEnded;
                SignalingClient.getInstance().sendMessage(model);

                SignalingClient.getInstance().closeConnection();
                if (isMediaPlayerRelease && myMediaPlayer != null) {
                    isMediaPlayerRelease = false;
                    myMediaPlayer.pause();
                    myMediaPlayer.release();
                    myMediaPlayer = null;
                }
                activityCommunicator.passDataToActivity("0", -1);
                break;
            }
        }
    }

    @Override
    public void onAuth() {

    }

    @Override
    public void onClose() {
        // SignalingClient.getInstance().closeConnection();

        if (isMediaPlayerRelease && myMediaPlayer != null) {
            myMediaPlayer.pause();
            myMediaPlayer.release();
            myMediaPlayer = null;
            isMediaPlayerRelease = false;
        }
        activityCommunicator.passDataToActivity("0", -1);
    }

    @Override
    public void onInputCall(boolean isUser) {

    }

    @Override
    public void onInputCallReject() {
        if (MyContext.getInstance().userRole == 2) {
            SignalingClient.getInstance().closeConnection();
            activityCommunicator.passDataToActivity("0", -1);
        }
    }

    @Override
    public void onInputCallAccept(boolean isUser) {
        if (MyContext.getInstance().userRole == 2)
            activityCommunicator.passDataToActivity(null, 2);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (isMediaPlayerRelease && myMediaPlayer != null) {
            myMediaPlayer.pause();
            myMediaPlayer.release();
            myMediaPlayer = null;
            isMediaPlayerRelease = false;
        }

        SignalingClient.getInstance().setInitSignalingClient(null);
    }
}
