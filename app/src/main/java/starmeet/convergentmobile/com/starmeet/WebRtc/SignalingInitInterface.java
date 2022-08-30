package starmeet.convergentmobile.com.starmeet.WebRtc;

public interface SignalingInitInterface {
    void onAuth();

    void onClose();

    void onInputCall(boolean isUser);

    void onInputCallReject();

    void onInputCallAccept(boolean isUser);
}