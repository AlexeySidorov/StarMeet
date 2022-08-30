package starmeet.convergentmobile.com.starmeet.WebRtc;

public interface WebSocketCallback {
    void onMessage(SocketModel model);

    void onError();

    void onClose();
}