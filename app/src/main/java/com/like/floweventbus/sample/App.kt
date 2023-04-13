package com.like.floweventbus.sample

import android.app.Application
import android.util.Log
import com.like.floweventbus.FlowEventBus
import com.like.floweventbus.TAG

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "App onCreate")
//        FlowEventBus.init(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        Log.d(TAG, "App onTerminate")
    }
}