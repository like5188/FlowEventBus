package com.like.floweventbus.sample

import android.util.Log
import com.like.floweventbus.TAG
import com.like.floweventbus_annotations.BusObserver

class MainViewModel1 {

    @BusObserver(["like4"])
    fun test(i: Int) {
        Log.e(TAG, "MainViewModel1 test tag=like4，数据：$i")
    }

}