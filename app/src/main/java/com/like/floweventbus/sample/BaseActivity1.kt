package com.like.floweventbus.sample

import android.util.Log
import com.like.floweventbus.TAG
import com.like.floweventbus_annotations.BusObserver

open class BaseActivity1 : BaseActivity() {

    @BusObserver(["like5"])
    fun test1(i: Int) {
        Log.e(TAG, "BaseActivity1 test tag=like5，数据：$i")
    }

}