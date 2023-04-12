package com.like.floweventbus

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Process
import android.util.Log

class IpcReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "IpcReceiver onReceive ${Process.myPid()}")
        intent?.apply {
            if (action == ACTION) {
                val tag = getStringExtra(KEY_TAG) ?: return
                val requestCode = getStringExtra(KEY_REQUEST_CODE) ?: return
                val processor = getSerializableExtra(KEY_VALUE_PROCESSOR) as Processor
                intent.extras?.let {
                    val value = processor.readFromBundle(KEY_VALUE, it)
                    FlowEventBus.post(tag, requestCode, value)
                }
            }
        }
    }

    companion object {
        const val ACTION = "intent.action.ACTION_IPC"
        const val KEY_TAG = "KEY_TAG"
        const val KEY_REQUEST_CODE = "KEY_REQUEST_CODE"
        const val KEY_VALUE = "KEY_VALUE"
        const val KEY_VALUE_PROCESSOR = "KEY_VALUE_PROCESSOR"
    }
}