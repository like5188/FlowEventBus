package com.like.floweventbus

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Parcelable
import java.io.Serializable

private const val ACTION = "intent.action.ACTION_IPC"
private const val KEY_TAG = "tag"
private const val KEY_REQUEST_CODE = "requestCode"
private const val KEY_IS_NULLABLE = "isNullable"
private const val KEY_DATA_TYPE = "dataType"
private const val KEY_DATA = "data"

/**
 * 跨进程通信工具类
 */
object Ipc {
    private val ipcReceiver = IpcReceiver()

    fun sendBroadcast(context: Context, tag: String, requestCode: String, data: Any?, isNullable: Boolean, dataType: String) {
        Intent(ACTION).apply {
            setPackage(context.packageName)
            putExtra(KEY_TAG, tag)
            putExtra(KEY_REQUEST_CODE, requestCode)
            putExtra(KEY_IS_NULLABLE, isNullable)
            putExtra(KEY_DATA_TYPE, dataType)
            putExtra(KEY_DATA, dataType, data)
            context.sendBroadcast(this)
        }
    }

    fun register(context: Context) {
        context.registerReceiver(ipcReceiver, IntentFilter(ACTION))
    }

    fun unregister(context: Context) {
        context.unregisterReceiver(ipcReceiver)
    }
}

private class IpcReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.apply {
            if (action == ACTION) {
                val tag = getStringExtra(KEY_TAG)
                val requestCode = getStringExtra(KEY_REQUEST_CODE)
                val isNullable = getBooleanExtra(KEY_IS_NULLABLE, false)
                val dataType = getStringExtra(KEY_DATA_TYPE)
                val data = getExtra(KEY_DATA)
                RealFlowEventBus.doPost(tag, requestCode, data, isNullable, dataType)
            }
        }
    }
}

private fun Intent.putExtra(key: String, dataType: String?, value: Any?) {
    if (dataType.isNullOrEmpty()) {
        return
    }
    try {
        // 注意：这里有些数据类型是无法转换成功的，比如：int[]、java.lang.Integer[]
        val clazz = Class.forName(dataType)
        when {
            Parcelable::class.java.isAssignableFrom(clazz) -> putExtra(key, value as? Parcelable)
//            else -> putExtra(key, value as? Serializable)
        }
    } catch (e: Exception) {
        putExtra(key, value as? Serializable)
    }
}

private fun Intent.getExtra(key: String): Any? {
    return extras?.get(key)
}
