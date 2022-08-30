package starmeet.convergentmobile.com.starmeet.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Map;
import java.util.Objects;

import starmeet.convergentmobile.com.starmeet.Activites.MainActivity;
import starmeet.convergentmobile.com.starmeet.Models.PushMessage;
import starmeet.convergentmobile.com.starmeet.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Check if message contains a notification payload.

        if(remoteMessage.getFrom() != null && !remoteMessage.getFrom().isEmpty()){
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName("Push test message")
                    .putCustomAttribute("Test", remoteMessage.getFrom())
                    .putContentType("Text"));
        }

        if (remoteMessage.getData() != null) {
            try {
                Answers.getInstance().logContentView(new ContentViewEvent()
                        .putContentName("Push message")
                        .putCustomAttribute("Message", remoteMessage.getData().toString())
                        .putContentType("Text"));

                Map<String, String> params = remoteMessage.getData();

                if (params != null && params.containsKey("notification")) {
                    Gson gson = new Gson();
                    PushMessage message = gson.fromJson(params.get("notification"), PushMessage.class);
                    if (message != null && message.title != null && message.body != null) {
                        sendNotification(message.title, message.body);
                    }
                }
            } catch (Exception exp) {
                Answers.getInstance().logContentView(new ContentViewEvent()
                        .putContentName("Push error")
                        .putCustomAttribute("Error", exp.toString())
                        .putContentType("Text"));
            }
        }
    }

    private void sendNotification(String title, String bodyMessage) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code*/, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(bodyMessage)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }

        assert notificationManager != null;
        notificationManager.notify(0, notificationBuilder.build());
    }
}