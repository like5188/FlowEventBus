package com.like.floweventbus.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.like.floweventbus.LiveDataBus

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LiveDataBus.register(this)
    }

}