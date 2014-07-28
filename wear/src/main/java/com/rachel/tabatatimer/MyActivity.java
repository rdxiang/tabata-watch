package com.rachel.tabatatimer;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MyActivity extends Activity {
    public final  String log = "TABATA";


    private TextView mTextView;
    private LinearLayout mLinearLayout;
    private int numIntervals;
    private int WorkSeconds;
    private int RestSeconds;
    private Vibrator v;

    private NotificationManagerCompat mNotificationManager;
    private NotificationCompat.Builder mNotificationBuilder;


    private long[] mDefaultVibrate = {0, 700, 200};
    private long[] mSuccessVibrate = {0, 1000, 200, 1000};
    private long[] mNoVibrate = {0};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                mLinearLayout = (LinearLayout) stub.findViewById(R.id.background);
                v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                int intervals = sharedPreferences.getInt("intervals", 1);
                Log.v(log, "" +intervals);


                // Set settings here?
                numIntervals = 8;
                WorkSeconds = 20 * 1000;
                RestSeconds = 10 * 1000;


                //Set Up notifications!

                // Create an intent to restart a timer.
                Intent restartIntent = new Intent("cancel", null, getApplicationContext(),
                        MyActivity.class);
                PendingIntent pendingIntentRestart = PendingIntent
                        .getService(getApplicationContext(), 0, restartIntent, PendingIntent.FLAG_CANCEL_CURRENT);


                mNotificationBuilder =
                        new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle("Get Ready!")
                                .setContentText("Why are you looking at me?")
                                .addAction(R.drawable.ic_cc_alarm, "Cancel",
                                    pendingIntentRestart);

                // Get an instance of the NotificationManager service
                mNotificationManager =
                        NotificationManagerCompat.from(getApplicationContext());


                workTimer(1);




            }
        });


    }


    private void workTimer (final int interval) {
        mNotificationManager.cancel(2);
        mNotificationBuilder
                .setVibrate(mDefaultVibrate)
                .setContentText("Interval " + interval + " of " + numIntervals);

        new CountDownTimer(WorkSeconds, 1000) {
            public void onTick(long millisUntilFinished) {
                millisUntilFinished -= 1000;
                //stuff inside app  no one looks at...
                mLinearLayout.setBackgroundColor(Color.GREEN);
                mTextView.setText("seconds remaining: " + millisUntilFinished / 1000);


               //notifications
               mNotificationBuilder
                       .setContentTitle("GO! " + millisUntilFinished/1000 + " sec.");
                mNotificationManager.notify(1, mNotificationBuilder.build());
                mNotificationBuilder.setVibrate(mNoVibrate);
            }
            public void onFinish() {
                mTextView.setText("Done!");
                if (interval < numIntervals)
                    restTimer(interval);
                else
                    endScreen();

            }
        }.start();
    }

    private void restTimer (final int interval) {
        mNotificationManager.cancel(1);
        mNotificationBuilder
                .setVibrate(mDefaultVibrate)
                .setContentText("Interval "+ interval + " of " + numIntervals);

        new CountDownTimer(RestSeconds, 1000) {
            public void onTick(long millisUntilFinished) {
                mLinearLayout.setBackgroundColor(Color.RED);
                mTextView.setText("seconds remaining: " + millisUntilFinished / 1000);


                mNotificationBuilder
                        .setContentTitle("Rest! " + millisUntilFinished/1000 + " sec.");
                mNotificationManager.notify(2, mNotificationBuilder.build());
                mNotificationBuilder.setVibrate(mNoVibrate);
            }

            public void onFinish() {
                workTimer(interval + 1);
            }

        }.start();
    }

    private void endScreen(){
        mNotificationManager.cancel(1);
        mLinearLayout.setBackgroundColor(Color.BLUE);
        mTextView.setText("GOOD JOB!");
        mNotificationBuilder
                .setContentTitle("Good Job!")
                .setContentText("Gold star for you.")
                .setVibrate(mSuccessVibrate);
        // Build the notification and issues it with notification manager.
        mNotificationManager.notify(3, mNotificationBuilder.build());

        finish();
    }





}
