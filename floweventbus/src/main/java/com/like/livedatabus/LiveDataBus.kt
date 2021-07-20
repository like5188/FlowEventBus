package com.like.livedatabus

import android.util.Log
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.like.livedatabus.LiveDataBus.unregister

object LiveDataBus {

    /**
     * 注册宿主
     *
     * @param host  宿主，包含被[com.like.livedatabus_annotations.BusObserver]注解的方法的类。
     * @param owner 宿主所属的生命周期类。
     *
     * 注意：
     * 1、如果 host 是 LifecycleOwner 类型，则不需要传递 owner。host 会直接作为 owner。
     * 2、如果 host 是 View 类型，则不需要传递 owner。会自动获取它的 LifecycleOwner，此时需要注意不能在 View 的 init{} 代码块中进行注册，因为此时还不能获取到它的 LifecycleOwner。
     * 3、如果 host 是 其它无法获取到 LifecycleOwner 的类型，则需要传递 owner。如果不传 owner的话，则会使用 liveData.observeForever(observer) 进行注册。那么就需要在合适的时机手动调用 [unregister] 方法取消注册。
     * 4、同一个宿主不能重复注册，根据宿主类的全限定类名来判断是否重复。
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
        if (EventManager.isRegistered(host)) {
            Log.e(TAG, "已经注册过宿主：$host")
            return
        }
        Log.i(TAG, "注册宿主：$host")
        registerAllHierarchyFromOwner(host, owner, host.javaClass)
    }

    /**
     * 取消注册的宿主
     */
    @JvmStatic
    fun unregister(host: Any) {
        EventManager.removeHost(host)
    }

    @JvmStatic
    fun post(tag: String) {
        EventManager.post(tag, "", NoObserverParams())
    }

    @JvmStatic
    fun <T> post(tag: String, t: T) {
        EventManager.post(tag, "", t)
    }

    @JvmStatic
    fun <T> post(tag: String, requestCode: String, t: T) {
        EventManager.post(tag, requestCode, t)
    }

    /**
     * 查找并实例化由 javapoet 自动生成的代理类。并调用它们的 register 方法进行注册。
     *
     * @param host      宿主类
     * @param clazz     需要查找是否有 clazz 对应的代理类（"${clazz.name}_Proxy"）的类
     */
    private fun registerAllHierarchyFromOwner(host: Any, owner: LifecycleOwner?, clazz: Class<*>?) {
        clazz ?: return
        Log.v(TAG, "registerAllHierarchyFromOwner --> $clazz")
        try {
            // 查找并实例化由 javapoet 自动生成的宿主代理类，此类继承自 HostProxy 类。
            Class.forName("${clazz.name}_Proxy")?.newInstance()?.apply {
                if (this is HostProxy) {
                    this.register(host, owner)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "registerAllHierarchyFromOwner --> $clazz 不是宿主类，无需注册！")
        }
        // 继续查找父类。以便能调用父类中被 BusObserver 注解的方法。这里过滤开始的字符，及过滤 android 和 java 系统自带的类。
        clazz.superclass?.apply {
            if (
                !name.startsWith("android.") &&
                !name.startsWith("androidx.") &&
                !name.startsWith("java.") &&
                !name.startsWith("javax.")
            ) {
                registerAllHierarchyFromOwner(host, owner, this)
            }
        }
    }
}