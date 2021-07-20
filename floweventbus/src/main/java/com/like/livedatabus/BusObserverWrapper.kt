package com.like.livedatabus

import android.util.Log
import androidx.lifecycle.Observer

class BusObserverWrapper<T>(
    val host: Any,
    val tag: String,
    val requestCode: String,
    private val observer: Observer<T>,
    private val liveData: BusLiveData<T>
) : Observer<T> {

    override fun onChanged(t: T?) {
        if (liveData.mSetValue) {
            try {
                observer.onChanged(t)
            } catch (e: Exception) {
                Log.e(TAG, "发送消息失败：发送的数据类型和接收的数据类型不一致。host=$host，tag=$tag，requestCode=$requestCode Exception=$e")
            }
        }
    }
}