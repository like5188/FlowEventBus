package com.like.livedatabus.sample

import android.util.Log
import com.like.livedatabus_annotations.BusObserver

open class BaseActivity1 : BaseActivity() {

    @BusObserver(["like5"])
    fun test1(i: Int) {
        Log.e("LiveDataBus", "BaseActivity1 test tag=like5，数据：$i")
    }

}