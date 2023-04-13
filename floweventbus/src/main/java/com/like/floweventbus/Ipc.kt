package com.like.floweventbus

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.io.Serializable

class IpcReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.apply {
            if (action == ACTION) {
                (getSerializableExtra(KEY_RECEIVER) as? Receiver)?.onReceive()
            }
        }
    }

    companion object {
        const val ACTION = "intent.action.ACTION_IPC"
        const val KEY_RECEIVER = "KEY_RECEIVER"
    }
}

fun interface Receiver : Serializable {
    fun onReceive()
}
