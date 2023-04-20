package com.like.floweventbus

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle

private const val ACTION = "intent.action.ACTION_IPC"
private const val KEY_TAG = "tag"
private const val KEY_REQUEST_CODE = "requestCode"
private const val KEY_DATA = "data"

/**
 * 跨进程通信工具类
 */
object Ipc {
    private val ipcReceiver = IpcReceiver()

    fun sendBroadcast(context: Context, tag: String, requestCode: String, data: Bundle?) {
        Intent(ACTION).apply {
            setPackage(context.packageName)
            putExtra(KEY_TAG, tag)
            putExtra(KEY_REQUEST_CODE, requestCode)
            if (data != null) {
                putExtra(KEY_DATA, data)
            }
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
                val data = getBundleExtra(KEY_DATA)
                if (data == null) {
                    RealFlowEventBus.doPost(tag, requestCode, NoArgs(), false, NoArgs::class.java.name)
                } else {
                    RealFlowEventBus.doPost(tag, requestCode, data, false, Bundle::class.java.name)
                }
            }
        }
    }
}
