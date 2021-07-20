package com.like.livedatabus

import android.os.Looper
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

object EventManager {
    private val mEventList = mutableListOf<Event<*>>()

    fun isRegistered(host: Any) = mEventList.any { it.host == host }

    @JvmStatic
    fun <T> observe(host: Any, owner: LifecycleOwner?, tag: String, requestCode: String, isSticky: Boolean, observer: Observer<T>) {
        if (tag.isEmpty()) {
            return
        }
        // LiveData由tag、requestCode组合决定
        val liveData = getLiveDataIfNullCreate<T>(tag, requestCode)
        // 设置mSetValue标记为isSticky。即当isSticky为true时。则会在注册的时候就收到之前发送的最新一条消息。当为false时，则不会收到消息。
        liveData.mSetValue = isSticky

        val busObserverWrapper = BusObserverWrapper(host, tag, requestCode, observer, liveData)
        // 创建 Event 对象，会自动订阅（liveData.observe 或者 liveData.observeForever）。
        val event = Event(host, owner, tag, requestCode, busObserverWrapper, liveData)
        // event由host、tag、requestCode组合决定
        if (mEventList.contains(event)) {
            Log.e(TAG, "已经订阅过事件：$event")
            return
        }
        mEventList.add(event)
        Log.i(TAG, "订阅事件成功：$event")
        logHostOwnerEventDetails()
    }

    fun <T> post(tag: String, requestCode: String, t: T) {
        val requestCodeLogMessage = if (requestCode.isNotEmpty()) ", requestCode='$requestCode'" else ""
        val liveData = getLiveData<T>(tag, requestCode)
        if (liveData != null) {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                Log.v(TAG, "在主线程发送消息 --> tag=$tag$requestCodeLogMessage，内容=$t")
                liveData.setValue(t)
            } else {
                Log.v(TAG, "在非主线程发送消息 --> tag=$tag$requestCodeLogMessage，内容=$t")
                liveData.postValue(t)
            }
        } else {
            Log.e(TAG, "发送消息失败，没有订阅事件： --> tag=$tag$requestCodeLogMessage")
        }
    }

    fun removeHost(host: Any) {
        mEventList.filter { it.host == host }.forEach {
            it.removeObserver()// 此方法最终会调用 fun <T> removeObserver(observer: Observer<T>) 方法
        }
    }

    fun <T> removeObserver(observer: Observer<T>) {
        mEventList.removeAll { it.observer == observer }
        if (observer is BusObserverWrapper) {
            val logMessage =
                "Event(host=${observer.host}, tag='${observer.tag}'${if (observer.requestCode.isNotEmpty()) ", requestCode='${observer.requestCode}'" else ""})"
            Log.i(TAG, "取消事件：$logMessage")
        } else {
            Log.i(TAG, "取消事件：$observer")
        }
        logHostOwnerEventDetails()
    }

    /**
     * 获取缓存的LiveData对象，如果没有缓存，则创建。用于注册时
     */
    private fun <T> getLiveDataIfNullCreate(tag: String, requestCode: String): BusLiveData<T> {
        return getLiveData(tag, requestCode) ?: BusLiveData()
    }

    /**
     * 获取缓存的LiveData对象。用于发送消息时
     */
    private fun <T> getLiveData(tag: String, requestCode: String): BusLiveData<T>? {
        val filter = mEventList.filter {
            it.tag == tag && it.requestCode == requestCode
        }
        return if (filter.isNotEmpty()) {
            filter[0].liveData as BusLiveData<T>
        } else {
            null
        }
    }

    /**
     * 打印缓存的事件、宿主、宿主所属生命周期类的详情
     */
    private fun logHostOwnerEventDetails() {
        val events = mEventList.toSet()
        Log.d(TAG, "事件总数：${events.size}${if (events.isEmpty()) "" else "，包含：$events"}")

        val hosts = mEventList.distinctBy { it.host }.map { it.host }
        Log.d(TAG, "宿主总数：${hosts.size}${if (hosts.isEmpty()) "" else "，包含：$hosts"}")

        val owners = mEventList.distinctBy { it.owner }.map { if (it.owner != null) it.owner::class.java.name else "null" }
        Log.d(TAG, "宿主所属生命周期类总数：${owners.size}${if (owners.isEmpty()) "" else "，包含：$owners"}")
    }

}