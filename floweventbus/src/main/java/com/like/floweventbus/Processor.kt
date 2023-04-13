package com.like.floweventbus

import android.content.Intent
import java.io.Serializable

fun interface Processor : Serializable {
    fun onReceive(intent: Intent)
}
