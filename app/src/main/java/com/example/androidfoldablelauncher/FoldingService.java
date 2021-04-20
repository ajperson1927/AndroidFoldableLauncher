package com.example.androidfoldablelauncher;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.util.Consumer;
import androidx.window.DisplayFeature;
import androidx.window.FoldingFeature;
import androidx.window.WindowLayoutInfo;
import androidx.window.WindowManager;

import java.util.List;

public class FoldingService extends Service {

    WindowManager windowManager;
    int foldingState = 0;
    LayoutStateChangeCallback layoutStateChangeCallback;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //windowManager = new WindowManager(this);
        //layoutStateChangeCallback = new LayoutStateChangeCallback();
        //windowManager.registerLayoutChangeCallback(getMainExecutor(), layoutStateChangeCallback);

        NotificationChannel channel = new NotificationChannel("Foldable", "Foldable", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("description");
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Foldable")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("Folding Service")
                .setContentText("Service successfully started!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);



        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(10303, builder.build());

    }

    @Override
    public boolean stopService(Intent name) {
        windowManager.unregisterLayoutChangeCallback(layoutStateChangeCallback);
        stopSelf();
        return super.stopService(name);
    }

    class LayoutStateChangeCallback implements Consumer<WindowLayoutInfo> {
        @Override
        public void accept(WindowLayoutInfo windowLayoutInfo) {
            /*List<DisplayFeature> displayFeatures = windowLayoutInfo.getDisplayFeatures();
            if (displayFeatures.isEmpty()) {
                foldingState = 0;
            } else {
                FoldingFeature foldingFeature = (FoldingFeature) displayFeatures.get(displayFeatures.size() - 1);
                foldingState = foldingFeature.getState();
            }*/
        }
    }
}
