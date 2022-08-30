package starmeet.convergentmobile.com.starmeet.WebRtc;

import android.annotation.SuppressLint;

import com.google.gson.Gson;

import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import starmeet.convergentmobile.com.starmeet.Rest.UnsafeOkHttpClientBuilder;
import starmeet.convergentmobile.com.starmeet.Utils.LogUtil;


public class SignalingClient implements WebSocketCallback {
    private String token;
    private SignalingInterface callback;
    private SignalingChatInterface chatCallback;
    private SignalingInitInterface initCallback;
    @SuppressLint("StaticFieldLeak")
    private static SignalingClient instance;
    private WebSocket webSocket;
    public boolean isStart = false;

    private SignalingClient() {
    }

    public static SignalingClient getInstance() {
        if (instance == null) {
            instance = new SignalingClient();
        }

        return instance;
    }

    public void setSettings(String token) {
        this.token = token;
    }

    public void setCallbackChat(SignalingChatInterface chatCallBack) {
        this.chatCallback = chatCallBack;
    }

    public void setInitSignalingClient(SignalingInitInterface callback) {
        this.initCallback = callback;
    }

    public void setCallbackVideoChat(SignalingInterface signalingInterface) {
        this.callback = signalingInterface;
    }

    public void init() {
        if (token == null || token.isEmpty()) return;

        String mSocketAddress = "wss://starmeet.eleview.com/handlers/videochat.ashx";
        Request request = new Request.Builder().url(mSocketAddress).build();

        OkHttpClient.Builder builder = UnsafeOkHttpClientBuilder.getUnsafeOkHttpClientBuilder();
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(20, TimeUnit.SECONDS);
        builder.writeTimeout(15, TimeUnit.SECONDS);

        OkHttpClient okHttpClient = builder.build();

        webSocket = okHttpClient.newWebSocket(request,
                new SignalingWebSocketListener(token, this));
        okHttpClient.dispatcher().executorService().shutdown();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void onMessage(SocketModel model) {
        if (model.type == SocketModel.TypeMessage.authSuccess) {
            isStart = true;
            if (initCallback != null)
                initCallback.onAuth();
        } else if (model.type == SocketModel.TypeMessage.readyToStart) {
            if (initCallback != null)
                initCallback.onInputCall(false);
        } else if (model.type == SocketModel.TypeMessage.callCelebrity) {
            if (initCallback != null)
                initCallback.onInputCall(false);
        } else if (model.type == SocketModel.TypeMessage.callUser) {
            if (initCallback != null)
                initCallback.onInputCall(true);
        } else if (model.type == SocketModel.TypeMessage.callAcceptCelebrity) {
            if (initCallback != null)
                initCallback.onInputCallAccept(false);
        } else if (model.type == SocketModel.TypeMessage.callAcceptUser) {
            if (initCallback != null)
                initCallback.onInputCallAccept(true);
        } else if (model.type == SocketModel.TypeMessage.callRejectCelebrity ||
                model.type == SocketModel.TypeMessage.callRejectUser) {
            if (initCallback != null)
                initCallback.onInputCallReject();
        } else if (model.type == SocketModel.TypeMessage.offer) {
            if (callback != null)
                callback.onOfferReceived(model);
        } else if (model.type == SocketModel.TypeMessage.answer) {
            if (callback != null)
                callback.onAnswerReceived(model);
        } else if (model.type == SocketModel.TypeMessage.candidate) {
            if (callback != null)
                callback.onIceCandidateReceived(model);
        } else if (model.type == SocketModel.TypeMessage.newMessageUser ||
                model.type == SocketModel.TypeMessage.newMessageCelebrity) {
            if (chatCallback != null)
                chatCallback.onNewMessage(model);
        } else if (model.type == SocketModel.TypeMessage.confirmMessageCelebrity ||
                model.type == SocketModel.TypeMessage.confirmMessageUser) {
            if (chatCallback != null)
                chatCallback.onConfirm(model);
        }
    }

    @Override
    public void onError() {
        if (callback != null)
            callback.onClose();

        if (initCallback != null)
            initCallback.onClose();

        if (chatCallback != null)
            chatCallback.onClose();
    }

    @Override
    public void onClose() {
        if (callback != null)
            callback.onClose();

        if (initCallback != null)
            initCallback.onClose();

        if (chatCallback != null)
            chatCallback.onClose();

        isStart = false;
    }

    public void sendMessage(SocketModel model) {
        Gson builder = new Gson();
        String json = builder.toJson(model);
        output("OUTPUT: " + json);
        webSocket.send(json);
    }

    private void output(final String txt) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.logE("websocketchat", txt);
            }
        });
        t.start();
    }

    public void closeConnection() {
        isStart = false;
        webSocket.close(1000, null);
    }
}
