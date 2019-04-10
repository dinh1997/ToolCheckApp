package info.androidhive.sqlite.view;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("checklocation intent", "onReceive: " + intent.getAction());

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d("checklocation", "onReceive: ACTION_BOOT_COMPLETED");
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
//            context.startForegroundService(new Intent(context, AppCheckServices.class));
//        } else{
        context.startService(new Intent(context, MyService.class));
//        }

//        /*-------alarm setting after boot again--------*/
//        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 999, alarmIntent, 0);
//        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        int interval = (86400 * 1000) / 4;
//        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);

    }



}