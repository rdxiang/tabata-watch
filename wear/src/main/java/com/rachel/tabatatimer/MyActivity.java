package com.rachel.tabatatimer;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
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

    private AltCountDownTimer mWorkTimer;
    private AltCountDownTimer mRestTimer;
    private boolean mCancelSelected = false;
    private int mInterval = 0;

    private NotificationManagerCompat mNotificationManager;
    private NotificationCompat.Builder mNotificationBuilder;


    private long[] mDefaultVibrate = {0, 700, 200};
    private long[] mSuccessVibrate = {0, 1000, 200, 1000};
    private long[] mNoVibrate = {0};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
       mCancelSelected = intent.getBooleanExtra("cancel", false);
        Log.v("TABATA", "wtf" + mCancelSelected);


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
                Intent restartIntent = new Intent(getApplicationContext(),
                        MyActivity.class);

                restartIntent.putExtra("cancel", true);
                PendingIntent pendingIntentRestart = PendingIntent
                        .getActivity(getApplicationContext(), 0, restartIntent, PendingIntent.FLAG_NO_CREATE);

                Resources res = getResources();
                mNotificationBuilder =
                        new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.ic_logo)
                                .setContentTitle("Get Ready!")
                                .setContentText("Why are you looking at me?")
                                .addAction(R.drawable.ic_cc_alarm, "Cancel",
                                    pendingIntentRestart);

                // Get an instance of the NotificationManager service
                mNotificationManager =
                        NotificationManagerCompat.from(getApplicationContext());


                mWorkTimer =  new AltCountDownTimer(WorkSeconds, 1000) {

                    public void onTick(long millisUntilFinished) {
                        //stuff inside app  no one looks at...
                        mLinearLayout.setBackgroundColor(Color.parseColor("#27ae60"));
                        mTextView.setText("work for:  " + millisUntilFinished / 1000);
                    }
                    public void onFinish() {
                            mTextView.setText("Done!");
                                restTimer();
                    }
                };

                mRestTimer =   new AltCountDownTimer(RestSeconds, 1000) {

                    public void onTick(long millisUntilFinished) {
                        mLinearLayout.setBackgroundColor(Color.parseColor("#8e44ad"));
                        mTextView.setText("seconds remaining: " + millisUntilFinished / 1000);

                    }

                    public void onFinish() {
                            workTimer();
                    }

                };



                if (mCancelSelected) {
                    Log.v("TABATA", "Cancel is TRUE");

                    NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();
                    if (mWorkTimer != null) {

                        mWorkTimer.cancel();
                    }
                    else
                        Log.v("TABATA", "wtf work nill" );

                    if (mRestTimer != null) {

                        mRestTimer.cancel();
                    }else
                        Log.v("TABATA", "wtf rest null" );


                    finish();

                    Log.v("TABATA", "omg how are you runningg????");

                    android.os.Process.killProcess(android.os.Process.myPid());

                    return;

                }
                Log.v("TABATA", "Cancel is FALSE");




                workTimer();
            }
        });


    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.d("TABATA", "NEW INTENT");


    }

    private void workTimer () {
        mInterval++;
        mNotificationManager.cancel(2);


        Log.v("TABATA", "omg how are you runningg???? " + mCancelSelected);

        Resources res = getResources();
        mNotificationBuilder
                .setVibrate(mDefaultVibrate)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.green))
                .setContentTitle("WORK. " + mInterval + " of " + numIntervals)
                .setUsesChronometer(true)
                .setWhen(System.currentTimeMillis() + WorkSeconds);
        mNotificationManager.notify(1, mNotificationBuilder.build());


        mWorkTimer.start();
    }

    private void restTimer () {
        mNotificationManager.cancel(1);
        if (mInterval == numIntervals)
        { endScreen();
            return;}

        if (mCancelSelected) return;
        Log.v("TABATA", "omg how are you runningg???? " + mCancelSelected);


        mNotificationBuilder
                .setVibrate(mDefaultVibrate)
                .setUsesChronometer(true)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.purple))

                .setContentTitle("REST. " + mInterval + " of " + numIntervals)
                .setWhen(System.currentTimeMillis() + RestSeconds);
        mNotificationManager.notify(2, mNotificationBuilder.build());


        mRestTimer.start();
    }

    private void endScreen(){
        mNotificationManager.cancelAll();
        mLinearLayout.setBackgroundColor(Color.BLUE);
        mTextView.setText("GOOD JOB!");
        mNotificationBuilder
                .setContentTitle("Good Job!")
                .setContentText("Gold star for you.")
                .setUsesChronometer(false)
                .setVibrate(mSuccessVibrate);
        // Build the notification and issues it with notification manager.
        mNotificationManager.notify(3, mNotificationBuilder.build());

        finish();
    }





}
