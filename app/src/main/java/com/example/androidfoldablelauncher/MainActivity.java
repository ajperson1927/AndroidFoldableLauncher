package com.example.androidfoldablelauncher;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.ConsumerIrManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;
import androidx.window.WindowLayoutInfo;
import androidx.window.WindowManager;

import com.example.androidfoldablelauncher.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;


public class MainActivity extends AppCompatActivity {
    private WindowManager windowManager;
    private ActivityMainBinding binding;
    private PackageManager packageManager;
    private StringBuilder stateLog;
    private StateContainer stateContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        windowManager = new WindowManager(this);
        packageManager = getPackageManager();
        stateContainer = new StateContainer();
        stateLog = new StringBuilder();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //This creates an executor to be used by the window manager callback
        Executor executor = runnable -> new Handler(Looper.getMainLooper()).post(runnable);
        windowManager.registerLayoutChangeCallback(executor, stateContainer);

        setupSpinners();


    }

    private void setupSpinners() {
        //Creates an intent to sort installed apps. In this case, I'm sorting the apps that are launchers
        Intent appIntent = new Intent(Intent.ACTION_MAIN);
        appIntent.addCategory(Intent.CATEGORY_HOME);

        //This does the actual sorting, and outputs a list
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(appIntent,0);

        //This creates a list of package names for installed launchers. It's intended to be used
        // in a spinner, so the first element is blank for the purpose of the spinner starting out blank
        List<String> launcherList = new ArrayList<>();
        launcherList.add(" ");

        for (ResolveInfo resolveInfo : resolveInfoList) {
            launcherList.add(resolveInfo.activityInfo.packageName);
        }

        //Creates an array adapter for the spinners, the sets the spinners' adapter to it
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, launcherList);
        Spinner foldedSpinner = findViewById(R.id.foldedSpinner);
        Spinner unfoldedSpinner = findViewById(R.id.unfoldedSpinner);
        foldedSpinner.setAdapter(arrayAdapter);
        unfoldedSpinner.setAdapter(arrayAdapter);



    }

    //This updates the log on screen with the current display features when called
    void updateStateLog(WindowLayoutInfo layoutInfo) {
        stateLog.append(" ").append(layoutInfo).append("\n");
        binding.stateUpdateLog.setText(stateLog);
    }


    class StateContainer implements Consumer<WindowLayoutInfo> {
        @Override
        public void accept(WindowLayoutInfo windowLayoutInfo) {
            updateStateLog(windowLayoutInfo);
        }
    }
}
