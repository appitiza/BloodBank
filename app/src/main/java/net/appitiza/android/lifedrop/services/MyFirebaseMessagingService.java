package net.appitiza.android.lifedrop.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import net.appitiza.android.lifedrop.R;
import net.appitiza.android.lifedrop.ui.activities.HomeActivity;
import net.appitiza.android.lifedrop.ui.activities.MessageActivity;

import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // (developer): Handle FCM messages here.
        if (remoteMessage.getData().size() > 0) {
            System.out.println("data: " + remoteMessage.getData());
            Map<String, String> data = remoteMessage.getData();
            String title = data.get("title");
            String email = data.get("email_id");
            String blood = data.get("blood");
            String address = data.get("address");
            String lat = data.get("lat");
            String lon = data.get("lon");
            String required_date = data.get("required_date");
            String first_name = data.get("first_name");
            String last_name = data.get("last_name");
            String message = data.get("message");
            String number = data.get("number");


            sendNotification(title ,email,blood,address,lat,lon,required_date,first_name,last_name,message,number);
        }

        else if (remoteMessage.getNotification() != null) {
            Map<String, String> data = remoteMessage.getData();
            String type = data.get("type");
            sendNotification(remoteMessage.getNotification().getBody(), type);

        } else {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,"L_CHANNEL_ID")
                    .setSmallIcon(R.drawable.profile_icon)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.app_name))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void sendNotification(String messageBody, String type) {
        Intent intent = new Intent(this, MessageActivity.class);
        int nId = 0;
        if (type != null) {
            if (type.equalsIgnoreCase("blood")) {
                intent.putExtra("n_type", 1);
                nId = 1;
            } else {
                intent.putExtra("n_type", 2);
                nId = 2;
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,"L_CHANNEL_ID")
                .setSmallIcon(R.drawable.profile_icon)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(nId /* ID of notification */, notificationBuilder.build());

    }
    private void sendNotification(String title ,String email,String blood,String address,String lat,String lon,String required_date,String first_name,String last_name,String message,String number){
        Intent intent = new Intent(this, MessageActivity.class);
        int nId = 0;

        intent.putExtra("title",title);
        intent.putExtra("email_id",email);
        intent.putExtra("blood",blood);
        intent.putExtra("address",address);
        intent.putExtra("lat",lat);
        intent.putExtra("lon",lon);
        intent.putExtra("required_date",required_date);
        intent.putExtra("first_name",first_name);
        intent.putExtra("last_name",last_name);
        intent.putExtra("message",message);
        intent.putExtra("number",number);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,"L_CHANNEL_ID")
                .setSmallIcon(R.drawable.profile_icon)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(nId /* ID of notification */, notificationBuilder.build());

    }
}