package starmeet.convergentmobile.com.starmeet.Helpers;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import starmeet.convergentmobile.com.starmeet.Callbaks.CallbackTimer;
import starmeet.convergentmobile.com.starmeet.WebRtc.SocketModel;

public class ChatHelper {
    private static ChatHelper sInstance;
    private Timer timer;
    private VideoChatTimer videoChatTimer;
    private CallbackTimer callback;
    private ArrayList<SocketModel> historyMessage;

    public static ChatHelper getInstance() {
        if (sInstance == null) {
            sInstance = new ChatHelper();
        }

        return sInstance;
    }

    public ChatHelper() {
        historyMessage = new ArrayList<>();
    }

    public void startChat() {
        startTimeEvent();
    }

    public void stopChat() {
        clearHistory();

        if (videoChatTimer != null) {
            videoChatTimer.cancel();
            videoChatTimer = null;
        }

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void setMessage(SocketModel model) {
        historyMessage.add(model);
    }

    public ArrayList<SocketModel> getHistory() {
        return historyMessage;
    }

    private void clearHistory() {
        historyMessage.clear();
    }

    public void setListener(CallbackTimer callback) {
        this.callback = callback;
    }

    private void startTimeEvent() {
        if (timer != null) return;

        timer = new Timer();
        videoChatTimer = new VideoChatTimer(new CallbackTimer() {
            @Override
            public void Value(String time) {
                if (callback != null)
                    callback.Value(time);
            }
        });

        timer.schedule(videoChatTimer, 0, 1000);
    }

    private class VideoChatTimer extends TimerTask {
        private final CallbackTimer callback;
        private final Calendar calendar;
        private final SimpleDateFormat simpleDateFormat;

        @SuppressLint("SimpleDateFormat")
        public VideoChatTimer(CallbackTimer callbackTimer) {
            this.callback = callbackTimer;
            calendar = Calendar.getInstance();
            simpleDateFormat = new SimpleDateFormat(
                    "HH:mm:ss", Locale.getDefault());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
        }

        private int count = 0;

        @Override
        public void run() {
            count++;
            calendar.set(Calendar.SECOND, count);

            if (count == 60) count = 0;

            final String strTime = simpleDateFormat.format(calendar.getTime());

            if (callback != null)
                callback.Value(strTime);
        }
    }
}


