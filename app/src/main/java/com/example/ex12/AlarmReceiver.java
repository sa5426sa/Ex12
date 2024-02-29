package com.example.ex12;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getExtras() != null)
            Toast.makeText(context, "You haven't set an alarm yet!", Toast.LENGTH_LONG).show();
        else {
            Intent intent1 = new Intent(context, MainActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.putExtra("alarmActivated", 1);
            context.startActivity(intent1);
        }
    }
}