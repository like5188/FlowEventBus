package com.like.floweventbus

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Parcelable
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
    val dataClass = dataType.toKotlinDataType()
    when {
        dataClass == null -> throw IllegalArgumentException("Intent extra $key has wrong type $dataType")
        dataClass == Byte::class.java -> putExtra(key, value as? Byte)
        dataClass == Short::class.java -> putExtra(key, value as? Short)
        dataClass == Int::class.java -> putExtra(key, value as? Int)
        dataClass == Long::class.java -> putExtra(key, value as? Long)
        dataClass == Float::class.java -> putExtra(key, value as? Float)
        dataClass == Double::class.java -> putExtra(key, value as? Double)
        dataClass == Char::class.java -> putExtra(key, value as? Char)
        dataClass == Boolean::class.java -> putExtra(key, value as? Boolean)
        Serializable::class.java.isAssignableFrom(dataClass) -> putExtra(key, value as? Serializable)
        Parcelable::class.java.isAssignableFrom(dataClass) -> putExtra(key, value as? Parcelable)
        dataClass == Bundle::class.java -> putExtra(key, value as? Bundle)
        dataClass == IntArray::class.java -> putExtra(key, value as? IntArray)
        dataClass == LongArray::class.java -> putExtra(key, value as? LongArray)
        dataClass == FloatArray::class.java -> putExtra(key, value as? FloatArray)
        dataClass == DoubleArray::class.java -> putExtra(key, value as? DoubleArray)
        dataClass == CharArray::class.java -> putExtra(key, value as? CharArray)
        dataClass == ShortArray::class.java -> putExtra(key, value as? ShortArray)
        dataClass == BooleanArray::class.java -> putExtra(key, value as? BooleanArray)
        dataClass == Array::class.java -> {
            when (dataClass.componentType) {
                CharSequence::class.java -> putExtra(key, value as? Array<CharSequence>)
                String::class.java -> putExtra(key, value as? Array<String>)
                Parcelable::class.java -> putExtra(key, value as? Array<Parcelable>)
                else -> throw IllegalArgumentException("Intent extra $key has wrong type $dataType")
            }
        }
        else -> throw IllegalArgumentException("Intent extra $key has wrong type $dataType")
    }
}

private fun Intent.getExtra(key: String, dataType: String?): Any? {
    val dataClass = dataType.toKotlinDataType()
    return when {
        dataClass == null -> null
        dataClass == Byte::class.java -> getByteExtra(key, 0)
        dataClass == Short::class.java -> getShortExtra(key, 0)
        dataClass == Int::class.java -> getIntExtra(key, 0)
        dataClass == Long::class.java -> getLongExtra(key, 0L)
        dataClass == Float::class.java -> getFloatExtra(key, 0F)
        dataClass == Double::class.java -> getDoubleExtra(key, 0.0)
        dataClass == Char::class.java -> getCharExtra(key, 0.toChar())
        dataClass == Boolean::class.java -> getBooleanExtra(key, false)
        Serializable::class.java.isAssignableFrom(dataClass) -> getSerializableExtra(key)
        Parcelable::class.java.isAssignableFrom(dataClass) -> getParcelableExtra(key)
        dataClass == Bundle::class.java -> getBundleExtra(key)
        dataClass == IntArray::class.java -> getIntArrayExtra(key)
        dataClass == LongArray::class.java -> getLongArrayExtra(key)
        dataClass == FloatArray::class.java -> getFloatArrayExtra(key)
        dataClass == DoubleArray::class.java -> getDoubleArrayExtra(key)
        dataClass == CharArray::class.java -> getCharArrayExtra(key)
        dataClass == ShortArray::class.java -> getShortArrayExtra(key)
        dataClass == BooleanArray::class.java -> getBooleanArrayExtra(key)
        dataClass == Array::class.java -> {
            when (dataClass.componentType) {
                CharSequence::class.java -> getCharSequenceArrayExtra(key)
                String::class.java -> getStringArrayExtra(key)
                Parcelable::class.java -> getParcelableArrayExtra(key)
                else -> throw IllegalArgumentException("Intent extra $key has wrong type $dataType")
            }
        }
        else -> throw IllegalArgumentException("Intent extra $key has wrong type $dataType")
    }
}
