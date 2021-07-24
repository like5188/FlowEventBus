package com.like.floweventbus.sample

import android.util.Log
import com.like.floweventbus.TAG
import com.like.floweventbus_annotations.BusObserver

class MainViewModel {

    @BusObserver(["like3"])
    fun test(data: Int) {
        Log.e(TAG, "MainViewModel test tag=like3 数据：$data")
    }

}