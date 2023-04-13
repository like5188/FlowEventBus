package com.like.floweventbus

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import java.io.Serializable

private const val ACTION = "intent.action.ACTION_IPC"
private const val KEY_ON_RECEIVE_CALLBACK = "KEY_ON_RECEIVE_CALLBACK"

/**
 * 跨进程通信工具类
 */
object Ipc {
    private val ipcReceiver = IpcReceiver()

    fun sendBroadcast(context: Context, callback: OnReceiveCallback) {
        val intent = Intent(ACTION)
        intent.setPackage(context.packageName)
        intent.putExtra(KEY_ON_RECEIVE_CALLBACK, callback)
        context.sendBroadcast(intent)
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
                (getSerializableExtra(KEY_ON_RECEIVE_CALLBACK) as? OnReceiveCallback)?.onReceive()
            }
        }
    }
}

fun interface OnReceiveCallback : Serializable {
    fun onReceive()
}
