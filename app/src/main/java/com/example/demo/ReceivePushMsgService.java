package com.example.demo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;


public class ReceivePushMsgService extends FirebaseMessagingService {


    private static final String TAG = "ReceivePushMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e(TAG, "收到推送 From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "收到推送 Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "收到通知 Message Notification Body: " + remoteMessage.getNotification().getBody()+ "Title = "+remoteMessage.getNotification().getTitle()+"Sound = "+remoteMessage.getNotification().getSound() );
            String sound = remoteMessage.getNotification().getSound();
            if (!TextUtils.isEmpty(sound)){
                EventBus.getDefault().post(new MessageEvent(sound));
            }
        }
    }
    /**
     * 如果更新了InstanceID令牌，则调用此方法。
     * 当先前令牌的安全性受到损害，则可能更新令牌。
     * 最初生成InstanceID令牌时也会调用此方法，因此您可以在此处检索令牌。
     * 该回调方法可以代替Demo工程中的的MyFirebaseInstanceIDService。Demo工程中FirebaseInstanceIdService这个类也已经被废弃了。
     */
    @Override
    public void onNewToken(String token) {
       Log.e(TAG, "Refreshed token: " + token);
        // 可以在这里将用户的FCM InstanceID令牌与应用程序维护的任何服务器端帐户关联起来。
         sendRegistrationToServer(token);
    }
    // [END on_new_token]


    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.e(TAG, "Short lived task is done.");
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }


    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
        Log.e(TAG,"onDeletedMessages is start");
    }


//    private void sendNotification(RemoteMessage remoteMessage) {
//
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);

//        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//// this is a my insertion looking for a solution
//        int icon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? R.drawable.myicon: R.mipmap.myicon;
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(icon)
//                .setContentTitle(remoteMessage.getFrom())
//                .setContentText(remoteMessage.getNotification().getBody())
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
//    }
}
