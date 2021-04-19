package com.example.androidfoldablelauncher;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
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
        layoutStateChangeCallback = new LayoutStateChangeCallback();
        windowManager.registerLayoutChangeCallback(getMainExecutor(), layoutStateChangeCallback);
        return super.onStartCommand(intent, flags, startId);
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
            List<DisplayFeature> displayFeatures = windowLayoutInfo.getDisplayFeatures();
            if (displayFeatures.isEmpty()) {
                foldingState = 0;
            } else {
                FoldingFeature foldingFeature = (FoldingFeature) displayFeatures.get(displayFeatures.size() - 1);
                foldingState = foldingFeature.getState();
            }
        }
    }
}
