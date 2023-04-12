package com.like.floweventbus.sample

import android.app.Application
import com.like.floweventbus.FlowEventBus

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        FlowEventBus.init(this)
    }
}