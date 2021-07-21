package com.like.floweventbus.sample

import android.util.Log
import com.like.floweventbus.FlowEventBus
import com.like.floweventbus.TAG

class MainViewModel {

    init {
        FlowEventBus.register<Int>(null, "tag2", "requestCode2", false) {
            Log.e(TAG, "MainViewModel tag=tag2 requestCode=requestCode2，数据：$it")
        }
    }

}
