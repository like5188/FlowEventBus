package com.like.floweventbus

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner

object FlowEventBus {

    /**
     * 注册宿主
     *
     * @param host  宿主，包含被[com.like.floweventbus_annotations.BusObserver]注解的方法的类。会通过它来调用这些方法。
     * @param owner 宿主所属的生命周期类。
     *
     * 注意：
     * 1、如果 host 是 LifecycleOwner 类型，不需要传递 owner。host 会直接作为 owner。
     * 2、如果 host 是 View 类型，不需要传递 owner。会自动获取它的 findViewTreeLifecycleOwner，此时需要注意不能在 View 的 init{} 代码块中进行注册，因为此时还不能获取到它的 LifecycleOwner。
     * 3、如果 host 是 其它无法获取到 LifecycleOwner 的类型，则需要传递 owner。如果不传 owner的话，则会使用 GlobalScope.launch 进行收集数据，此时就需要在合适的时机手动调用 [unregister] 方法取消注册。
     * 4、同一个宿主重复注册会被忽略。这里根据宿主类的实例来判断是否重复。
     */
    @JvmStatic
    @JvmOverloads
    fun register(
        host: Any,
        owner: LifecycleOwner? = when (host) {
            is LifecycleOwner -> host
            is View -> host.findViewTreeLifecycleOwner()
            else -> null
        }
    ) {
        EventManager.register(host, owner)
    }

    /**
     * 取消注册的宿主
     */
    @JvmStatic
    fun unregister(host: Any) {
        EventManager.unregister(host)
    }

    @JvmStatic
    fun post(tag: String) {
        EventManager.post(tag, "", NoArgs())
    }

    @JvmStatic
    fun <T> post(tag: String, t: T) {
        EventManager.post(tag, "", t)
    }

    @JvmStatic
    fun <T> post(tag: String, requestCode: String, t: T) {
        EventManager.post(tag, requestCode, t)
    }

}
