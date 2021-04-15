package com.example.androidfoldablelauncher

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
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
import android.widget.TextView
import androidx.window.FoldingFeature
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.window.WindowLayoutInfo


class MainActivity : AppCompatActivity() {

    private lateinit var windowManager: WindowManager
    private lateinit var binding: ActivityMainBinding
    private lateinit var pm: PackageManager

    private val stateLog: StringBuilder = StringBuilder()
    private val stateContainer = StateContainer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        windowManager = WindowManager(this)
        pm = packageManager



    }

    override fun onStart() {
        super.onStart()
        windowManager.registerLayoutChangeCallback(Executor { r: Runnable -> Handler(Looper.getMainLooper()).post(r) }, stateContainer)

        var i:Intent = Intent(Intent.ACTION_MAIN)
        i.addCategory(Intent.CATEGORY_HOME)
        var lst:List<ResolveInfo> = pm.queryIntentActivities(i,0)
        var string:String = ""
        for (resolveInfo: ResolveInfo in lst) {
            string += resolveInfo.activityInfo.packageName + "\n"
        }
        //setContentView(R.layout.activity_main)
        var tv: TextView = findViewById(R.id.textView)
        tv.text = string

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