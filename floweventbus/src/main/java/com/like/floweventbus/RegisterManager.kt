package com.like.floweventbus

import android.util.Log
import androidx.lifecycle.LifecycleOwner

object RegisterManager {
    /**
     * 查找并实例化由 javapoet 自动生成的代理类。并调用它们的 register 方法对宿主中的所有 tag 进行注册。
     *
     * @param host      宿主。
     */
    fun register(host: Any, owner: LifecycleOwner?) {
        if (EventManager.isRegistered(host)) {
            Log.e(TAG, "已经注册过宿主 --> $host")
            return
        }
        try {
            // 查找并实例化由 javapoet 自动生成的宿主代理类，此类继承自 HostProxy 类。
            val hostProxy = Class.forName("${host::class.qualifiedName}_Proxy").newInstance() as HostProxy
            Log.v(TAG, "注册宿主：$host")
            hostProxy.register(host, owner)
        } catch (e: Exception) {
            Log.e(TAG, "注册宿主失败 --> ${host::class.qualifiedName} 不是宿主类，无需注册！")
        }
    }

}
