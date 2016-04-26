package com.unixarmy.apps.mealtimer;

import android.content.Intent;
import android.provider.AlarmClock;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG="MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    final static int mealIntervalSeconds = (int)Math.floor(60 * 60 * 2.5);
    final static int secondsInDay = 86400;

    public void createTimers_Click(View view) {

        String[] meals = {"Breakfast", "Morning Snack", "Lunch", "Afternoon Snack", "Dinner", "Evening Snack" };
        String messageFormat = "Time for %s!";

        TimePicker startTime = (TimePicker) findViewById(R.id.timePicker);
        assert startTime !=  null;

        Log.v(TAG, "Date picker hour  [" + startTime.getCurrentHour() + "]");
        Log.v(TAG, "Date picker min   [" + startTime.getCurrentMinute() + "]");

        Date startDate = new Date();
        startDate.setHours(startTime.getCurrentHour());
        startDate.setMinutes(startTime.getCurrentMinute());


        // we now have the start time in seconds-from-midnight
        // each timer will add the interval in seconds then convert that to HH:MM

        // we skip the first meal because we assume the user is starting the timers at the first
        // mealtime

        Intent[] intents = new Intent[meals.length];
        for (int i=1; i < meals.length; i++) {

            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);

            cal.add(Calendar.SECOND, mealIntervalSeconds * i);

            Log.v(TAG, "Creating timer for " + meals[i] + " at " + cal.getTime().getHours() + ":" + cal.getTime().getMinutes() + ")");

            Intent mealIntent = new Intent(AlarmClock.ACTION_SET_ALARM)
                    .putExtra(AlarmClock.EXTRA_MESSAGE, String.format(messageFormat, meals[i]))
                    .putExtra(AlarmClock.EXTRA_ALARM_SEARCH_MODE,AlarmClock.ALARM_SEARCH_MODE_LABEL)
                    .putExtra(AlarmClock.EXTRA_HOUR,cal.getTime().getHours())
                    .putExtra(AlarmClock.EXTRA_MINUTES,cal.getTime().getMinutes())
                    .putExtra(AlarmClock.EXTRA_SKIP_UI, true);

            if (mealIntent.resolveActivity(getPackageManager()) != null) {
                intents[i] = mealIntent;
            } else {
                Snackbar.make(view, R.string.no_intent_handlers, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                return;
            }
        }

        try {
            startActivities(intents);
        } catch (SecurityException e) {
            Snackbar.make(view,R.string.no_permission_for_timers, Snackbar.LENGTH_INDEFINITE).setAction("Action", null).show();
            return;
        }

        // all done, show a message
        Snackbar.make(view, R.string.timers_created, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        // show newly created alarms
        Intent showAlarmsIntent = new Intent(AlarmClock.ACTION_SHOW_ALARMS);
        if (showAlarmsIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(showAlarmsIntent);
        }

    }
}
