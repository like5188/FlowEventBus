package com.like.floweventbus

import androidx.lifecycle.LifecycleOwner

object FlowEventBus {
    fun <T> register(owner: LifecycleOwner?, tag: String, requestCode: String, isSticky: Boolean, callback: (T) -> Unit) {
        EventManager.register(owner, tag, requestCode, isSticky, callback)
    }

    fun post(tag: String) {
        EventManager.post(tag, "", Unit)
    }

    fun <T> post(tag: String, t: T) {
        EventManager.post(tag, "", t)
    }

    fun <T> post(tag: String, requestCode: String, t: T) {
        EventManager.post(tag, requestCode, t)
    }

}
