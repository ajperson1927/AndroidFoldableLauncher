package com.example.androidfoldablelauncher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.window.WindowManager
import com.example.androidfoldablelauncher.databinding.ActivityMainBinding
import java.lang.StringBuilder
import java.util.concurrent.Executor
import androidx.core.util.Consumer

import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.window.FoldingFeature
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.window.WindowLayoutInfo


class MainActivity : AppCompatActivity() {

    private lateinit var windowManager: WindowManager
    private lateinit var binding: ActivityMainBinding

    private val stateLog: StringBuilder = StringBuilder()
    private val stateContainer = StateContainer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        windowManager = WindowManager(this)



    }

    override fun onStart() {
        super.onStart()
        windowManager.registerLayoutChangeCallback(Executor { r: Runnable -> Handler(Looper.getMainLooper()).post(r) }, stateContainer)
    }
    internal fun updateStateLog(layoutInfo: WindowLayoutInfo) {
        stateLog.append(" ").append(layoutInfo).append("\n")
        binding.stateUpdateLog.text = stateLog

    }




    inner class StateContainer : Consumer<WindowLayoutInfo> {
        var lastLayoutInfo: WindowLayoutInfo? = null

        override fun accept(newLayoutInfo: WindowLayoutInfo) {
            println(newLayoutInfo.displayFeatures)
            updateStateLog(newLayoutInfo)
            //lastLayoutInfo = newLayoutInfo
        }
    }
}