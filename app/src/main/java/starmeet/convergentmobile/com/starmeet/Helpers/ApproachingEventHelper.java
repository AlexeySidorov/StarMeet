package starmeet.convergentmobile.com.starmeet.Helpers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import starmeet.convergentmobile.com.starmeet.Activites.AuthActivity;
import starmeet.convergentmobile.com.starmeet.Activites.ProfileCelebrityActivity;
import starmeet.convergentmobile.com.starmeet.Activites.ProfileUserActivity;
import starmeet.convergentmobile.com.starmeet.Base.BaseActivity;
import starmeet.convergentmobile.com.starmeet.Callbaks.CallbackEmptyFunc;
import starmeet.convergentmobile.com.starmeet.Callbaks.CallbackTimer;
import starmeet.convergentmobile.com.starmeet.Callbaks.CallbackTimerService;
import starmeet.convergentmobile.com.starmeet.Callbaks.CallbackTimerVisibleService;
import starmeet.convergentmobile.com.starmeet.Models.ActivityNextType;
import starmeet.convergentmobile.com.starmeet.MyContext;
import starmeet.convergentmobile.com.starmeet.Response.ApproachingCountResponse;
import starmeet.convergentmobile.com.starmeet.Response.ApproachingEventResponse;
import starmeet.convergentmobile.com.starmeet.StarMeetApp;
import starmeet.convergentmobile.com.starmeet.Utils.LogUtil;

import static android.content.Context.ACTIVITY_SERVICE;
import static starmeet.convergentmobile.com.starmeet.Models.NavigationModel.None;

public class ApproachingEventHelper {
    private ApproachingListener listener;
    private static ApproachingEventHelper sInstance;
    private Timer timer;
    private Timer secondTimer;
    private CallbackTimerVisibleService callback;
    private MyTimer myTimer;

    public static ApproachingEventHelper getInstance() {
        if (sInstance == null) {
            sInstance = new ApproachingEventHelper();
        }

        return sInstance;
    }

    public ApproachingEventHelper() {
        isVisibleNotification = false;
    }

    public void init() {
        startApproachingTimer();
    }

    private boolean isVisibleNotification;

    private void initCallback(boolean isVisibleNotification) {
        this.isVisibleNotification = isVisibleNotification;

        if (!isVisibleNotification && getApproachingEvent() != null)
            setApproachingEvent(null);

        if (listener != null) {
            listener.ApproachingEvent(isVisibleNotification);

            BaseActivity activity = StarMeetApp.getCurrentActivity();
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.setIconNotification(isVisibleNotification, new CallbackEmptyFunc() {
                            @Override
                            public void Click() {
                                if (MyContext.getInstance().isAuthUser) {
                                    Intent i;

                                    if (activity.getLocalClassName().equals("Activites." + ProfileUserActivity.class.getSimpleName()) ||
                                            activity.getLocalClassName().equals("Activites." + ProfileCelebrityActivity.class.getSimpleName()))
                                        return;

                                    if (MyContext.getInstance().userRole == 1)
                                        i = new Intent(activity, ProfileUserActivity.class);
                                    else
                                        i = new Intent(activity, ProfileCelebrityActivity.class);

                                    activity.startActivity(i);

                                } else {
                                    HashMap<ActivityNextType, Class> activities = new HashMap<>();
                                    activities.put(ActivityNextType.Activity1, ProfileUserActivity.class);
                                    activities.put(ActivityNextType.Activity2, ProfileCelebrityActivity.class);
                                    NavigationHelper.getInstance().setNextActivity(activities, None);
                                    activity.startActivity(new Intent(activity, AuthActivity.class));
                                }
                            }
                        });
                    }
                });

            }
        }
    }

    private void getFirstApproachingEvent() {
        final Call<ApproachingEventResponse> request = StarMeetApp.getApi().getFirstApproaching();
        request.enqueue(new Callback<ApproachingEventResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApproachingEventResponse> call,
                                   @NonNull Response<ApproachingEventResponse> response) {
                if (response.code() == 200 && response.body() != null) {
                    ApproachingEventResponse eventResponse = response.body();

                    if (eventResponse != null) {
                        if (eventResponse.enableCountdown) {

                            if (getApproachingEvent() != null
                                    && !eventResponse.startUtcTime.equals(getApproachingEvent().startUtcTime))
                                stopTimeEvent();

                            startTimeEvent(eventResponse.countdown, eventResponse.showStartButton);
                        } else
                            stopTimeEvent();

                        setApproachingEvent(eventResponse);
                        initCallback(true);
                    } else {
                        setApproachingEvent(null);
                        stopTimeEvent();
                        initCallback(false);
                    }
                } else {
                    setApproachingEvent(null);
                    stopTimeEvent();
                    LogUtil.logE("ApproachingEvent error", "" + response.code());
                    initCallback(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApproachingEventResponse> call, @NonNull Throwable t) {
                LogUtil.logE("ApproachingEvent error", t.getMessage(), t);
                initCallback(false);
                setApproachingEvent(null);
            }
        });
    }

    public void ApproachingEventListener(ApproachingListener listener) {
        this.listener = listener;
    }

    public interface ApproachingListener {
        void ApproachingEvent(boolean isVisibleNotification);

        void getTimeStartChat(String time);

        void visibleTimer(boolean isVisible);
    }

    public ApproachingEventResponse getApproachingEvent() {
        return approachingEvent;
    }

    private ApproachingEventResponse approachingEvent;

    public void setApproachingEvent(ApproachingEventResponse event) {
        approachingEvent = event;

        if (event == null && callback != null)
            callback.isVisibleTime(false);
    }

    private void startTimeEvent(String time, boolean isVisible) {
        if (secondTimer != null) return;

        callback = new CallbackTimerVisibleService() {
            @Override
            public void show(boolean isVisible) {
                approachingEvent.showStartButton = isVisible;

                if (listener != null && time != null && !time.isEmpty())
                    listener.getTimeStartChat(time);
            }

            @Override
            public void isVisibleTime(boolean isVisible) {
                if (listener != null && time != null && !time.isEmpty())
                    listener.visibleTimer(isVisible);
            }
        };

        secondTimer = new Timer();
        myTimer = new MyTimer(time, isVisible, new CallbackTimer() {
            @Override
            public void Value(String time) {
                if (listener != null && time != null && !time.isEmpty())
                    listener.getTimeStartChat(time);
            }
        }, new CallbackTimerService() {
            @Override
            public void Cancel() {
                stopTimeEvent();
            }
        }, callback);

        secondTimer.schedule(myTimer, 1000, 1000);
    }

    public void stopTimeEvent() {
        if (secondTimer == null) return;

        if (callback != null)
            callback.isVisibleTime(false);

        setApproachingEvent(null);

        secondTimer.cancel();
        secondTimer.purge();
        secondTimer = null;
    }

    private void startApproachingTimer() {
        if (timer != null) return;

        timer = new Timer();
        timer.schedule(new ApproachingEventTimer(), 1000, 60000);
    }

    public void StopApproachingTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }

        stopTimeEvent();
    }

    private class MyTimer extends TimerTask {

        private final SimpleDateFormat formatter;
        private Date date;
        private CallbackTimer callback;
        private CallbackTimerService timerService;
        private CallbackTimerVisibleService callbackVisibleButton;
        private boolean isVisible;
        private boolean isVisibleTime;

        @SuppressLint("SimpleDateFormat")
        MyTimer(String time, boolean isVisible, CallbackTimer callbackTimer,
                CallbackTimerService timerService, CallbackTimerVisibleService callbackVisibleButton) {
            formatter = new SimpleDateFormat("hh:mm:ss");

            try {
                date = formatter.parse(time);
                this.callback = callbackTimer;
                this.timerService = timerService;
                this.isVisible = isVisible;
                this.callbackVisibleButton = callbackVisibleButton;
                isVisibleTime = date.getTime() > 0;
                isRun = true;
            } catch (ParseException e) {
                e.printStackTrace();
                timerService.Cancel();
            }
        }

        private boolean isRun = false;

        @Override
        public void run() {
            if (!isRun) return;

            if (!isCheckTime(date)) {
                isRun = false;
                timerService.Cancel();

                if (callbackVisibleButton != null)
                    callbackVisibleButton.isVisibleTime(false);

                isVisibleTime = false;

                return;
            }

            date.setTime(date.getTime() - 1000);
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.setTime(date);
            int hour = calendar.get(Calendar.HOUR);
            int minutes = calendar.get(Calendar.MINUTE);

            String time = "";

            if (hour > 0)
                time = hour + " " + DeclensionHelper.getDeclensionWord(hour, "hr",
                        "hrs", "hrs");
            if (minutes > 0)
                time += " " + minutes + " min";

            time += " " + calendar.get(Calendar.SECOND) + " sec";

            if (minutes < 5 && !isVisible) {
                if (callbackVisibleButton != null)
                    callbackVisibleButton.show(true);

                isVisible = true;
            } else if (minutes > 5 && isVisible) {
                if (callbackVisibleButton != null)
                    callbackVisibleButton.show(false);

                isVisible = false;
            }

            if (callback != null)
                callback.Value(time);

            if (date.getTime() > 0 && !isVisibleTime) {
                if (callbackVisibleButton != null)
                    callbackVisibleButton.isVisibleTime(true);
                isVisibleTime = true;
            }
        }

        private boolean isCheckTime(Date date) {
            Date checkTime = new Date();
            checkTime.setTime(date.getTime());

            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.setTime(date);
            int hour = calendar.get(Calendar.HOUR);
            int minutes = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);

            return hour != 0 || minutes != 0 || second != 0;
        }
    }

    private class ApproachingEventTimer extends TimerTask {

        @Override
        public void run() {
            if (!MyContext.getInstance().isAuthUser) {
                if (approachingEvent != null)
                    setApproachingEvent(null);

                return;
            }

            final Call<ApproachingCountResponse> request = StarMeetApp.getApi().getEventApproachingCount();
            request.enqueue(new Callback<ApproachingCountResponse>() {
                @SuppressWarnings("ConstantConditions")
                @Override
                public void onResponse(@NonNull Call<ApproachingCountResponse> call,
                                       @NonNull Response<ApproachingCountResponse> response) {
                    if (response.code() == 200 && response.body() != null) {
                        ApproachingCountResponse approachCount = response.body();

                        if (approachCount.count != null && approachCount.count > 0) {
                            getFirstApproachingEvent();
                        } else {
                            initCallback(false);
                            stopTimeEvent();
                        }

                    } else {
                        stopTimeEvent();
                        LogUtil.logE("ApproachingEvent error", " " + response.code());
                        initCallback(false);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApproachingCountResponse> call, @NonNull Throwable t) {
                    stopTimeEvent();
                    LogUtil.logE("ApproachingEvent error", t.getMessage(), t);
                    initCallback(false);
                }
            });
        }
    }
}
