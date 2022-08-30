package starmeet.convergentmobile.com.starmeet.WebRtc;

public interface SignalingInterface {

    void onOfferReceived(SocketModel model);

    void onAnswerReceived(SocketModel model);

    void onIceCandidateReceived(SocketModel model);

    void onClose();
}