package com.like.livedatabus.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.like.livedatabus.LiveDataBus

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LiveDataBus.register(this)
    }

}