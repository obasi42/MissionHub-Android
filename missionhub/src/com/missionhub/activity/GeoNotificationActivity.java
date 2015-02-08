package com.missionhub.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Window;

import com.missionhub.R;
import com.missionhub.application.Application;
import com.missionhub.fragment.dialog.EditContactDialogFragment;
import com.missionhub.fragment.dialog.InteractionDialogFragment;

// Written by Obasi Shaw on 02/08/2015
// Notification to be called when the phone-bearer leaves a campus.
// To run, call GeoNotificationActivity.createNotification(context, "CAMPUS NAME"); from anywhere.
public class GeoNotificationActivity extends BaseActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        Log.d("Testing", "Is anybody out there?");
        if (!Application.getSession().isOpen())
            Application.getSession().open();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Application.getSession().isOpen()) {
                    Log.d("Testing", "Waiting");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                String type = getIntent().getStringExtra("com.missionhub.geo_notif_type");
                if (type.equals("interact")) {
                    InteractionDialogFragment.showForResult(getSupportFragmentManager(), R.id.action_interaction);
                    Log.d("Testing", "Interaction");
                } else if (type.equals("contact")) {
                    EditContactDialogFragment.showForResult(getSupportFragmentManager(), R.id.action_add_contact);
                    Log.d("Testing", "Contact");
                }
            }
        }).start();
    }

    public static void createNotification(Context context, String campus)
    {
        PendingIntent contactIntent = newPendingIntent("contact", context, 1);
        PendingIntent interactIntent = newPendingIntent("interact", context, 2);
        Notification n  = new NotificationCompat.Builder(context)
                .setContentTitle("Just left " + campus + "?")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentText("Pull to expand for options")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("I noticed you just left " + campus + ". Do you want to log an action?"))
                .setAutoCancel(true)
                .addAction(R.drawable.ic_action_add_contact_white, "New Contact", contactIntent)
                .addAction(R.drawable.ic_action_interaction_white, "Interaction", interactIntent).build();
        n.flags |= Notification.FLAG_AUTO_CANCEL;
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(0, n);
    }

    private static PendingIntent newPendingIntent(String type, Context context, int id) {
        Intent i = new Intent(context, GeoNotificationActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("com.missionhub.geo_notif_type", type);
        return PendingIntent.getActivity(context, id, i, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void cancelNotification()
    {
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
    }

    @Override
    public void finish() {
        super.finish();
        cancelNotification();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        this.finish();
    }
}