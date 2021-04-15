package com.example.androidfoldablelauncher;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.window.WindowLayoutInfo;
import androidx.window.WindowManager;

import com.example.androidfoldablelauncher.databinding.ActivityMainBinding;


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
    }

    @Override
    protected void onStart() {
        super.onStart();
        TextView tv = findViewById(R.id.textView);
        tv.setText("TESTING TESTING TESTING");
    }



    void updateStateLog(WindowLayoutInfo layoutInfo) {
        stateLog.append(" ").append(layoutInfo).append("\n");
        binding.stateUpdateLog.setText(stateLog);
    }

    class StateContainer {
        StateContainer(WindowLayoutInfo windowLayoutInfo) {
            updateStateLog(windowLayoutInfo);
        }
    }
}
