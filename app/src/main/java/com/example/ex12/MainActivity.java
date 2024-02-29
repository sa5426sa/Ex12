package com.example.ex12;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private int ALARM_RQST_CODE = 0;

    static int timesSnoozed = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reminderAlarm(false);

        Intent intent = getIntent();
        if(intent.getExtras() != null) {
            timesSnoozed++;
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Alarm Activated!");
            adb.setMessage("Timed snoozed: " + timesSnoozed);
            adb.setPositiveButton("Snooze", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            adb.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancelAlarm();
                    dialog.cancel();
                }
            });
            AlertDialog ad = adb.create();
            ad.show();
        }
    }

    public void onClick(View view) {
        openTimePickerDialog(true);
    }

    public void onClickFinish(View view) {
        finish();
    }

    private void openTimePickerDialog(boolean is24r) {
        Calendar calendar = Calendar.getInstance();

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), is24r);
        timePickerDialog.setTitle("Choose time");
        timePickerDialog.show();
    }

    private void openDailyTimePickerDialog(boolean is24r) {
        Calendar calendar = Calendar.getInstance();

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onDailyTimeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), is24r);
        timePickerDialog.setTitle("Choose time");
        timePickerDialog.show();
    }

    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            Calendar calNow = Calendar.getInstance();
            Calendar calSet = (Calendar) calNow.clone();

            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSet.set(Calendar.MINUTE, minute);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);

            if (calSet.compareTo(calNow) <= 0) {
                calSet.add(Calendar.DATE, 1);
            }
            setAlarm(calSet, false);
        }
    };

    TimePickerDialog.OnTimeSetListener onDailyTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            Calendar calNow = Calendar.getInstance();
            Calendar calSet = (Calendar) calNow.clone();

            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSet.set(Calendar.MINUTE, minute);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);

            if (calSet.compareTo(calNow) <= 0) {
                calSet.add(Calendar.DATE, 1);
            }
            setAlarm(calSet, true);
        }
    };

    private void setAlarm(Calendar calSet, boolean isDaily) {
        ALARM_RQST_CODE++;
        Intent intent = new Intent(this, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this,
                ALARM_RQST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        if(isDaily) {
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
                    0, AlarmManager.INTERVAL_DAY, alarmIntent);
        }
        else {
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
                    0, 300000, alarmIntent);
        }
        Toast.makeText(this, String.valueOf(ALARM_RQST_CODE) + " Alarm in " + String.valueOf(calSet.getTime()), Toast.LENGTH_LONG).show();
    }

    /*
    private void setDailyAlarm(Calendar calSet) {
        ALARM_RQST_CODE++;
        Intent intent = new Intent(this, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this,
                ALARM_RQST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Toast.makeText(this, String.valueOf(ALARM_RQST_CODE) + " Alarm in " + String.valueOf(calSet.getTime()), Toast.LENGTH_LONG).show();
    }
     */

    public void reminderAlarm(boolean alarmHasBeenSet) {
        if(!alarmHasBeenSet) {
            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.putExtra("alarmType", "reminder");
            alarmIntent = PendingIntent.getBroadcast(this,
                    ALARM_RQST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);
            alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            Calendar calNow = Calendar.getInstance();
            Calendar calSet = (Calendar) calNow.clone();

            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                    calSet.getTimeInMillis() + 1000 * 3600,
                    AlarmManager.INTERVAL_HOUR, alarmIntent);
        }
        else {}
    }

    public void cancelAlarm() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this,
                ALARM_RQST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.cancel(alarmIntent);
        ALARM_RQST_CODE--;
    }

    public void onClickDailyButton(View view) {
        openTimePickerDialog(true);
    }
}