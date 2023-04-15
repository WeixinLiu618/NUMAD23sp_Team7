package edu.northeastern.numad23sp_team7.huskymarket.utils;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import edu.northeastern.numad23sp_team7.R;
import edu.northeastern.numad23sp_team7.huskymarket.activities.ChatActivity;
import edu.northeastern.numad23sp_team7.huskymarket.database.UserDao;

public class FCMService extends FirebaseMessagingService {

    private final static String TAG = "FCM";
    private final static UserDao userDao = new UserDao();
    private static final int MY_PERMISSIONS_REQUEST_NOTIFICATION = 123;
    private static final int NOTIFICATION_ID = 123123;

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "onNewToken: " +token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Map<String,String> data = message.getData();

        String userId = data.get(Constants.KEY_USER_ID);
        String username = data.get(Constants.KEY_USERNAME);
        String fcmToken = data.get(Constants.KEY_FCM_TOKEN);


        String channelId = "chat_message";

        Intent intent = new Intent(this, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        userDao.getUserById(userId, user -> {
            intent.putExtra(Constants.KEY_USER, user);
        });

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        builder.setSmallIcon(R.drawable.ic_launcher_husky_foreground)
                .setContentTitle(username)
                .setContentText(data.get(Constants.KEY_MESSAGE))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Chat Message";
            String channelDescription = "Chat Message Notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // show notification
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getApplicationContext(),
                    new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY},
                    MY_PERMISSIONS_REQUEST_NOTIFICATION);
        }
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());


    }


}
