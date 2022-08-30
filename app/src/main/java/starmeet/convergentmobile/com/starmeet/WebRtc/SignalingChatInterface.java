package starmeet.convergentmobile.com.starmeet.WebRtc;

public interface SignalingChatInterface {

    void onNewMessage(SocketModel model);

    void onConfirm(SocketModel model);

    void onClose();
}