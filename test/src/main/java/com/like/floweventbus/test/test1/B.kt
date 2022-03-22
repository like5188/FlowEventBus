package com.like.floweventbus.test.test1

import android.util.Log
import com.like.floweventbus.FlowEventBus
import com.like.floweventbus_annotations.BusObserver

class B {
    init {
        FlowEventBus.register(this)
    }

    @BusObserver(["like1"])
    fun observer1() {
        Log.w("Logger", "like1")
    }

}
