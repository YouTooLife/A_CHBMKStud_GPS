package net.youtoolife.chbmkstud;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by youtoolife on 6/20/18.
 */


public class BootCompletedIntentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BOOT_CIR","receive");
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Log.d("BOOT_CIR","its boot");
            Intent pushIntent = new Intent(context, CHBMKGpsService.class);
            context.startService(pushIntent);
        }
    }

}