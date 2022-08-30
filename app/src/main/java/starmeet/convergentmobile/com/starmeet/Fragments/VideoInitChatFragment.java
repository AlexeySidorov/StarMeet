package starmeet.convergentmobile.com.starmeet.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import starmeet.convergentmobile.com.starmeet.Callbaks.CallbackTimer;
import starmeet.convergentmobile.com.starmeet.Communicators.ActivityCommunicator;
import starmeet.convergentmobile.com.starmeet.Helpers.CallWebSocketHelper;
import starmeet.convergentmobile.com.starmeet.Models.IceServer;
import starmeet.convergentmobile.com.starmeet.Models.VideoChat;
import starmeet.convergentmobile.com.starmeet.MyContext;
import starmeet.convergentmobile.com.starmeet.R;
import starmeet.convergentmobile.com.starmeet.StarMeetApp;
import starmeet.convergentmobile.com.starmeet.Utils.LogUtil;
import starmeet.convergentmobile.com.starmeet.WebRtc.SignalingClient;
import starmeet.convergentmobile.com.starmeet.WebRtc.SignalingInitInterface;
import starmeet.convergentmobile.com.starmeet.WebRtc.SocketModel;


public class VideoInitChatFragment extends Fragment implements View.OnClickListener, SignalingInitInterface,
        CallbackTimer {
    private VideoChat chatModel;
    private AppCompatButton reconnect;
    private AppCompatButton cancel;
    private AppCompatTextView nickName;
    private AppCompatTextView smallTitle;
    private AppCompatTextView mainTitle;
    private AVLoadingIndicatorView progress;
    private CircleImageView avatar;
    private int eventId;
    private ActivityCommunicator<String> activityCommunicator;
    private String urls;
    private boolean isConnection;
    private RelativeLayout avatarBlock;
    private String nick;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_init_videochat, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initPermission();
        initData();
        initClickListener();
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

    @SuppressLint("CutPasteId")
    private void initViews() {
        progress = Objects.requireNonNull(getActivity()).findViewById(R.id.progress_call);
        nickName = Objects.requireNonNull(getActivity()).findViewById(R.id.nick_name);
        cancel = Objects.requireNonNull(getActivity()).findViewById(R.id.cancel_button);
        reconnect = Objects.requireNonNull(getActivity()).findViewById(R.id.reconnect_button);
        avatar = Objects.requireNonNull(getActivity()).findViewById(R.id.avatar_another_user);
        avatarBlock = Objects.requireNonNull(getActivity()).findViewById(R.id.avatar_block);
        smallTitle = Objects.requireNonNull(getActivity()).findViewById(R.id.small_title);
        mainTitle = Objects.requireNonNull(getActivity()).findViewById(R.id.main_title);
    }

    private void initClickListener() {
        reconnect.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    private void initPermission() {
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 1);
        } else if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, 2);
        } else if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, 3);
        }
    }

    @SuppressLint({"SetTextI18n", "CheckResult"})
    private void initData() {
        Bundle bundle = getArguments();
        assert bundle != null;
        nick = bundle.getString("NickName", "");
        String avatarUrl = bundle.getString("Avatar");
        eventId = bundle.getInt("EventId", 0);
        isConnection = bundle.getBoolean("Stop", false);
        nickName.setText(nick);
        progress.setVisibility(!isConnection ? View.VISIBLE : View.GONE);
        reconnect.setVisibility(!isConnection ? View.GONE : View.VISIBLE);
        RequestOptions options = new RequestOptions();
        options.centerCrop();
        options.diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(Objects.requireNonNull(getActivity())).load(avatarUrl).apply(options).into(avatar);

        setUISettings(isConnection);
        CallWebSocketHelper.getInstance().startConnect();
        CallWebSocketHelper.getInstance().setListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        getTokenForSocket();
    }

    @Override
    public void onAuth() {
        SocketModel model = new SocketModel();
        model.type = SocketModel.TypeMessage.mediaReady;
        SignalingClient.getInstance().sendMessage(model);
    }

    @Override
    public void onClose() {
        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (progress != null)
                    progress.setVisibility(View.GONE);

                if (reconnect != null)
                    reconnect.setVisibility(View.VISIBLE);

                setUISettings(true);
            }
        });
    }

    @Override
    public void onInputCall(boolean isUser) {
        if (MyContext.getInstance().userRole == 2) {
            SocketModel model1 = new SocketModel();
            model1.type = SocketModel.TypeMessage.readyWaitStart;
            SignalingClient.getInstance().sendMessage(model1);
        }

        activityCommunicator.passDataToActivity(urls, 1);
        CallWebSocketHelper.getInstance().stopConnect();
    }

    @Override
    public void onInputCallReject() {

    }

    @Override
    public void onInputCallAccept(boolean isUser) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reconnect_button: {
                isConnection = false;
                reconnect.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                CallWebSocketHelper.getInstance().startConnect();

                setUISettings(false);

                if (chatModel != null && !chatModel.accessToken.isEmpty())
                    connection(chatModel);
                break;
            }
            case R.id.cancel_button: {
                Objects.requireNonNull(getActivity()).onBackPressed();
                SignalingClient.getInstance().onClose();
                SignalingClient.getInstance().closeConnection();
                CallWebSocketHelper.getInstance().stopConnect();
                break;
            }
        }
    }

    private void getTokenForSocket() {
        if (eventId == 0)
            activityCommunicator.passDataToActivity(null, -1);

        Call<VideoChat> request = StarMeetApp.getApi().videoChatInit(eventId);
        request.enqueue(new Callback<VideoChat>() {
            @Override
            public void onResponse(@NonNull Call<VideoChat> call,
                                   @NonNull retrofit2.Response<VideoChat> response) {

                if (response.code() == 200 || response.body() != null) {
                    chatModel = response.body();
                    assert chatModel != null;
                    setChatModel(chatModel.iceServers);
                    connection(chatModel);
                } else
                    LogUtil.logE("Chat init error: ", response.code() + "");
            }

            @Override
            public void onFailure(@NonNull Call<VideoChat> call, @NonNull Throwable t) {
                LogUtil.logE("Chat init error: ", t.getMessage(), t);
            }
        });
    }

    private void setChatModel(ArrayList<IceServer> serverUrls) {
        if (serverUrls == null || serverUrls.size() == 0) urls = null;

        Gson gson = new Gson();
        urls = gson.toJson(serverUrls);
    }

    private void connection(VideoChat model) {
        if (SignalingClient.getInstance().isStart) return;

        SignalingClient.getInstance().setSettings(model.accessToken);

        if (!isConnection)
            SignalingClient.getInstance().init();

        SignalingClient.getInstance().setInitSignalingClient(this);
    }

    private void setUISettings(boolean isReconnect) {

        if (MyContext.getInstance().userRole == 1) {
            RelativeLayout.LayoutParams layoutParams =
                    (RelativeLayout.LayoutParams) avatarBlock.getLayoutParams();
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, !isReconnect ? RelativeLayout.TRUE : 0);
            avatarBlock.setLayoutParams(layoutParams);
        }

        nickName.setText(!isReconnect ? nick : MyContext.getInstance().userRole == 1 ? "The other party just end..." : "You ended the call!");
        mainTitle.setVisibility(!isReconnect ? View.VISIBLE : View.GONE);

        if (MyContext.getInstance().userRole == 1)
            smallTitle.setVisibility(!isReconnect ? View.VISIBLE : View.GONE);

        if (MyContext.getInstance().userRole == 2) {
            mainTitle.setText("Connected");
            smallTitle.setText(!isReconnect ? "Waiting for user to connect..." : nick);
            nickName.setVisibility(!isReconnect ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CallWebSocketHelper.getInstance().stopConnect();
    }

    @Override
    public void Value(String time) {
        CallWebSocketHelper.getInstance().stopConnect();
        SignalingClient.getInstance().closeConnection();
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
