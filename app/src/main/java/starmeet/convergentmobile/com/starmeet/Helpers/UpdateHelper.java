package starmeet.convergentmobile.com.starmeet.Helpers;

import starmeet.convergentmobile.com.starmeet.Listners.UpdateData;

public class UpdateHelper {
    private UpdateData.UpdateListener listener = null;
    private static UpdateHelper sInstance;

    public static UpdateHelper getInstance() {
        if (sInstance == null) {
            sInstance = new UpdateHelper();
        }

        return sInstance;
    }

    public void updateListener(UpdateData.UpdateListener listener) {
        this.listener = listener;
    }

    public void update() {
        if (listener != null)
            listener.Update();
    }
}
