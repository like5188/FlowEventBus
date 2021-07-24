package com.like.floweventbus

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
class Event<T>(
    val hostClass: Class<*>,// 宿主类
    val tag: String,// 标签
    val requestCode: String,// 请求码。当标签相同时，可以使用请求码区分
    val isSticky: Boolean,
    val flow: MutableSharedFlow<T>,
    val methodName: String,// 被@BusObserver注解标注的方法名字
    val paramType: Class<T>// 被@BusObserver注解标注的方法的参数类型。只支持一个参数
) {
    var host: Any? = null// 宿主
    var owner: LifecycleOwner? = null// 宿主所属的生命周期类
    private var job: Job? = null

    fun bind(host: Any, owner: LifecycleOwner?) {
        this.host = host
        this.owner = owner

        val method = if (paramType == NoArgs::class.java) {
            hostClass.getDeclaredMethod(methodName)
        } else {
            Log.e(TAG, "paramType1=$paramType")
            Log.e(TAG, "paramType2=${paramType.javaClass}")
            Log.e(TAG, "paramType3=${paramType::class}")
            Log.e(TAG, "paramType4=${paramType::class.java}")
            Log.e(TAG, "paramType5=${paramType.name}")
            Log.e(TAG, "paramType6=${Int::class.java}")
            hostClass.getDeclaredMethod(methodName, paramType)
        }
        job = (owner?.lifecycleScope?.launch {
            owner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collect {
                    method.invoke(host, it)
                }
            }
        } ?: GlobalScope.launch {
            flow.collect {
                method.invoke(host, it)
            }
        }).apply {
            invokeOnCompletion {
                Log.w(TAG, "解绑事件 --> ${this@Event}")
                this@Event.host = null
                this@Event.owner = null
                this@Event.job = null
                EventManager.logEvent()
                EventManager.logHostAndOwner()
            }
        }
    }

    fun unbind() {
        this.job?.cancel()
    }

    fun post(data: T) {
        val scope = owner?.lifecycleScope ?: GlobalScope
        scope.launch {
            flow.emit(data)
        }
    }

    override fun toString(): String {
        return "Event(host=$host, tag='$tag'${if (requestCode.isNotEmpty()) ", requestCode='$requestCode'" else ""}, isSticky='$isSticky')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Event<*>

        if (host != other.host) return false
        if (tag != other.tag) return false
        if (requestCode != other.requestCode) return false

        return true
    }

    override fun hashCode(): Int {
        var result = host.hashCode()
        result = 31 * result + tag.hashCode()
        result = 31 * result + requestCode.hashCode()
        return result
    }

}
