package starmeet.convergentmobile.com.starmeet.WebRtc;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.google.gson.Gson;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import starmeet.convergentmobile.com.starmeet.Utils.LogUtil;

public class SignalingWebSocketListener extends WebSocketListener {
    private String token;
    private WebSocketCallback callback;
    private static final int NORMAL_CLOSURE_STATUS = 1000;

    public SignalingWebSocketListener(String token, WebSocketCallback callback) {
        this.callback = callback;
        this.token = token;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        output("WebSocket connected");

        SocketModel model = new SocketModel();
        model.type = SocketModel.TypeMessage.auth;
        model.token = token;
        Gson builder = new Gson();
        String json = builder.toJson(model);

        output("Output: " + json);
        webSocket.send(json);
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        output("Input: " + text);
        Gson builder = new Gson();
        final SocketModel model = builder.fromJson(text, SocketModel.class);

        if (model.type == SocketModel.TypeMessage.callEnded ||
                model.type == SocketModel.TypeMessage.callEnd ||
                model.type == SocketModel.TypeMessage.doubleConnection ||
                model.type == SocketModel.TypeMessage.authFail ||
                model.type == SocketModel.TypeMessage.interlocutorLost ||
                model.type == SocketModel.TypeMessage.none) {

            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            callback.onClose();
        } else
            callback.onMessage(model);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        output("Rx bytes: " + bytes.hex());
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        callback.onClose();
        output("Closing: " + code + " / " + reason);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        output("Closed: " + code + " / " + reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        output("Error: " + t.getMessage());
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        output("Error: " + t.getMessage());
        callback.onError();
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Push test message")
                .putCustomAttribute("Error", t.getMessage())
                .putContentType("Text"));
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
}
