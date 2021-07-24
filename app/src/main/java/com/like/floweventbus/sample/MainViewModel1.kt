package com.like.floweventbus.sample

import android.util.Log
import com.like.floweventbus.TAG
import com.like.floweventbus_annotations.BusObserver

class MainViewModel1 {

    @BusObserver(["like3"])
    fun test(data: Int) {
        Log.e(TAG, "MainViewModel1 test tag=like4 数据：$data")
    }

}