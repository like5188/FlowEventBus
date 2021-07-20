package com.like.livedatabus

import androidx.lifecycle.LifecycleOwner

/**
 * 宿主代理类基类。
 * javapoet 自动生成的代码必须继承自此类，用于注册宿主。
 */
abstract class HostProxy {

    /**
     * 对 host 中所有注册的 tag 进行 [EventManager.observe] 方法的调用
     */
    abstract fun register(host: Any, owner: LifecycleOwner?)

}