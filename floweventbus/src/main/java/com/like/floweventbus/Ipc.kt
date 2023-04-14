package com.like.floweventbus

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import java.io.Serializable

const val ACTION = "intent.action.ACTION_IPC"
const val KEY_TAG = "tag"
const val KEY_REQUEST_CODE = "requestCode"
const val KEY_IS_NULLABLE = "isNullable"
const val KEY_DATA_TYPE = "dataType"
const val KEY_DATA = "data"

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
                val data = getExtra(KEY_DATA, dataType)
                RealFlowEventBus.doPost(tag, requestCode, data, isNullable, dataType)
            }
        }
    }
}

private fun Intent.putExtra(key: String, dataType: String?, value: Any?) {
    /*
    dataType 是 java 数据类型，对应的 kotlin 数据类型需要转换，例如：
        java                    kotlin
        int                     Int
        java.lang.Integer       Int?
        int[]                   IntArray、IntArray?
        java.lang.Integer[]     Array<Int>、Array<Int>?、Array<Int?>、Array<Int?>?
    */
    putExtra(key, value as? Serializable)
}

private fun Intent.getExtra(key: String, dataType: String?): Any? {
    return extras?.get(key)
}
