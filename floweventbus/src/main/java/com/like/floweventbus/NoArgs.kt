package com.like.floweventbus

import java.io.Serializable

/**
 * 用于 BusObserver 注解的方法没有参数时的处理。
 * 因为 flow.emit() 方法必须得传参数，如果直接传 null 的话，又会和那些可以为 null 的数据类型冲突。
 */
class NoArgs : Serializable