package com.like.floweventbus

import android.os.Bundle
import java.io.Serializable

interface Processor : Serializable {
    fun <T> readFromBundle(key: String, bundle: Bundle): T

    fun writeToBundle(key: String, bundle: Bundle)
}
