package starmeet.convergentmobile.com.starmeet.Helpers;

import android.annotation.SuppressLint;

import java.util.Timer;
import java.util.TimerTask;

import starmeet.convergentmobile.com.starmeet.Callbaks.CallbackTimer;

public class CallWebSocketHelper {
    private static CallWebSocketHelper sInstance;
    private Timer timer;
    private DelayTimer delayTimer;
    private CallbackTimer callback;

    public static CallWebSocketHelper getInstance() {
        if (sInstance == null) {
            sInstance = new CallWebSocketHelper();
        }

        return sInstance;
    }

    public void setListener(CallbackTimer callback){
        this.callback = callback;
    }

    public void startConnect() {
        startTimeEvent();
    }

    public void stopConnect() {

        if (delayTimer != null) {
            delayTimer.cancel();
            delayTimer = null;
        }

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void startTimeEvent() {
        if (timer != null) return;

        timer = new Timer();
        delayTimer = new DelayTimer(new CallbackTimer() {
            @Override
            public void Value(String time) {
                if (callback != null)
                    callback.Value(null);
            }
        });

        timer.schedule(delayTimer, 0, 1000);
    }

    private class DelayTimer extends TimerTask {
        private final CallbackTimer callback;

        @SuppressLint("SimpleDateFormat")
        public DelayTimer(CallbackTimer callbackTimer) {
            this.callback = callbackTimer;
        }

        private int count = 0;

        @Override
        public void run() {
            count++;

            if (count == 120) {
                count = 0;
                callback.Value(null);
            }
        }
    }
}


