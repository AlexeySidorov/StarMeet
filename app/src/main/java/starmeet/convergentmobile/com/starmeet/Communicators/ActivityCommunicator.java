package starmeet.convergentmobile.com.starmeet.Communicators;

/**
 * Created by alexeysidorov on 15.03.2018.
 */

public interface ActivityCommunicator<T> {
        void passDataToActivity(T value, int code);
}
