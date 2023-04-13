package com.like.floweventbus

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class IpcReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.apply {
            if (action == ACTION) {
                val processor = getSerializableExtra(KEY_VALUE_PROCESSOR) as Processor
                processor.onReceive(intent)
            }
        }
    }

    companion object {
        const val ACTION = "intent.action.ACTION_IPC"
        const val KEY_VALUE_PROCESSOR = "KEY_VALUE_PROCESSOR"
    }
}