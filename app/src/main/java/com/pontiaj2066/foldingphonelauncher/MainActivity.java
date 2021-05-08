package com.pontiaj2066.foldingphonelauncher;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;
import androidx.window.DisplayFeature;
import androidx.window.FoldingFeature;
import androidx.window.WindowLayoutInfo;
import androidx.window.WindowManager;

import com.pontiaj2066.foldingphonelauncher.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {
    private WindowManager windowManager;
    private ActivityMainBinding binding;
    private PackageManager packageManager;
    private LayoutStateChangeCallback layoutStateChangeCallback;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    Spinner foldedSpinner;
    Spinner unfoldedSpinner;

    int foldingState = 0;

    HashMap<String, String> appDictionary;

    //Shared preferences strings
    private final String prefsString = "LauncherSettings";
    private final String foldedString = "foldedSpinner";
    private final String unfoldedString = "unfoldedSpinner";

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        windowManager = new WindowManager(this);
        packageManager = getPackageManager();
        layoutStateChangeCallback = new LayoutStateChangeCallback();

        //preferences
        sharedPreferences = getSharedPreferences(prefsString,MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //spinners
        foldedSpinner = findViewById(R.id.foldedSpinner);
        unfoldedSpinner = findViewById(R.id.unfoldedSpinner);

        appDictionary = new HashMap<>();

    }

    @Override
    protected void onStart() {
        super.onStart();
        //This creates an executor to be used by the window manager callback
        Executor executor = runnable -> new Handler(Looper.getMainLooper()).post(runnable);
        windowManager.registerLayoutChangeCallback(executor, layoutStateChangeCallback);

        setupSpinners();

    }

    @Override
    protected void onStop() {
        super.onStop();
        //unregisters the callback if the app is closed
        windowManager.unregisterLayoutChangeCallback(layoutStateChangeCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Launch correct launcher when app is reopened
        launchLauncher();
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
        launcherList.add("Please select a launcher");

        for (ResolveInfo resolveInfo : resolveInfoList) {
            String appName = "" + packageManager.getApplicationLabel(resolveInfo.activityInfo.applicationInfo);
            String packageName = "" + resolveInfo.activityInfo.packageName;

            appDictionary.put(appName, packageName);
            launcherList.add(appName);
        }

        //Creates an array adapter for the spinners, then sets the spinners' adapter to it
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(this, R.layout.spinner_item, launcherList);
        foldedSpinner.setAdapter(arrayAdapter);
        unfoldedSpinner.setAdapter(arrayAdapter);

        //Loads the saved spinner states into the spinners
        foldedSpinner.setSelection(getSpinnerIndex(foldedSpinner, foldedString));
        unfoldedSpinner.setSelection(getSpinnerIndex(unfoldedSpinner, unfoldedString));
    }

    //Takes a string and spinner, and gets the spinner index of the string, Returns 0 if no string found
    private int getSpinnerIndex(Spinner spinner, String string) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(sharedPreferences.getString(string, ""))) {
                return i;
            }
        }
        return 0;
    }

    //Saves the currently selected launcher for each spinner then calls the launcher launcher
    public void launchAndSave(View view) {
        editor.putString(foldedString, foldedSpinner.getSelectedItem().toString());
        editor.putString(unfoldedString, unfoldedSpinner.getSelectedItem().toString());
        editor.apply();

        launchLauncher();
    }

    //Discards changes then calls the launcher launcher
    public void launchNoSave(View view) {
        launchLauncher();
    }

    private void launchLauncher() {
        if (foldedSpinner.getSelectedItemPosition() < 1 || unfoldedSpinner.getSelectedItemPosition() < 1) {
            //textView.setText("Please Select a valid launcher");
            return;
        }

        int resX = windowManager.getCurrentWindowMetrics().getBounds().width();
        int resY = windowManager.getCurrentWindowMetrics().getBounds().height();

        if (foldingState != 2) foldingState = (resX < resY / 2) ? 0 : 1;

        //This determines what state the fold is in, then sets the appropriate intent
        Intent launcherIntent;
        switch (foldingState) {
            case 1:
                launcherIntent = packageManager.getLaunchIntentForPackage(appDictionary.get(unfoldedSpinner.getSelectedItem().toString()));
                break;
            case 2:
                return;
            default:
                launcherIntent = packageManager.getLaunchIntentForPackage(appDictionary.get(foldedSpinner.getSelectedItem().toString()));
                break;
        }

        //Launches the chosen intent
        startActivity(launcherIntent);

    }

    class LayoutStateChangeCallback implements Consumer<WindowLayoutInfo> {
        @Override
        public void accept(WindowLayoutInfo windowLayoutInfo) {

            List<DisplayFeature> displayFeatures = windowLayoutInfo.getDisplayFeatures();

            foldingState = (displayFeatures.isEmpty())
                    ? 0 : ((FoldingFeature) displayFeatures.get(displayFeatures.size() - 1)).getState();

        }
    }
}
