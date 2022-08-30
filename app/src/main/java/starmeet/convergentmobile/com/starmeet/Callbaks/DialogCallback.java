package starmeet.convergentmobile.com.starmeet.Callbaks;

public interface DialogCallback<T> {
    void onResult(T result);

    void onClose();
}
