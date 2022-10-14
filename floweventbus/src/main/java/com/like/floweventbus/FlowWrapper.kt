package com.like.floweventbus

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect

/*
StateFlow就是一个replaySize=1的sharedFlow,同时它必须有一个初始值，此外，每次更新数据都会和旧数据做一次比较，只有不同时候才会更新数值。
StateFlow重点在状态，ui永远有状态，所以StateFlow必须有初始值，同时对ui而言，过期的状态毫无意义，所以stateFLow永远更新最新的数据（和liveData相似），所以必须有粘滞度=1的粘滞事件，让ui状态保持到最新。具体应用时，StateFlow适合那些长期保持某种状态的ui，比如一些开关值之类。
SharedFlow侧重在事件，当某个事件触发，发送到队列之中，按照挂起或者非挂起、缓存策略等将事件发送到接受方，在具体使用时，SharedFlow更适合通知ui界面的一些事件，比如toast等，也适合作为viewModel和repository之间的桥梁用作数据的传输。
 */
/**
 * Flow 由 tag、requestCode、paramType 组合决定
 */
data class FlowWrapper<T>(
    val tag: String,// 标签
    val requestCode: String,// 请求码。当标签相同时，可以使用请求码区分
    val paramType: String,// 被@BusObserver注解标注的方法的参数类型。只支持一个参数
    val isNullable: Boolean,
    val isSticky: Boolean,
    val flow: MutableSharedFlow<T>,
) {

    suspend inline fun collect(crossinline action: suspend (value: T) -> Unit) {
        flow.collect(action)
    }

    /**
     * 对sharedflow发送一条数据，如果buffer满了同时采用了suspend策略，emit方法会被挂起。
     */
    suspend fun emit(value: T) {
        flow.emit(value)
    }

    /**
     * 非挂起函数，如果发送成功了，返回true，如果失败则返回false。注意：当策略不采用suspend时，tryEmit永远返回true。（也就是说，只有当采用emit会被挂起的时候，采用tryEmit会返回false）
     */
    fun tryEmit(value: T): Boolean {
        return flow.tryEmit(value)
    }

    override fun toString(): String {
        return "tag=$tag,${if (requestCode.isNotEmpty()) " requestCode=$requestCode," else ""} paramType=$paramType isNullable=$isNullable isSticky=$isSticky"
    }

}
