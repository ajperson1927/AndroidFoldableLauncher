package com.example.androidfoldablelauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent foldingIntent = new Intent(context, FoldingService.class);
        context.startService(foldingIntent);
    }
}
