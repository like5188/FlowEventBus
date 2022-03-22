package com.like.floweventbus.sample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.like.floweventbus.FlowEventBus
import com.like.floweventbus.TAG
import com.like.floweventbus.sample.databinding.ActivityMainBinding
import com.like.floweventbus_annotations.BusObserver
import java.util.logging.Logger
import kotlin.concurrent.thread

class MainActivity : BaseActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
        Log.w(TAG, "MainActivity onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.w(TAG, "MainActivity onDestroy")
    }

    @BusObserver(["like1"])
    fun observer1() {
        Log.w("Logger", "1")
    }

    @BusObserver(["like1"])
    fun observer2() {
        Log.e("Logger", "2")
    }

    fun changeData1(view: View) {
        FlowEventBus.post("like1")
    }

    fun changeData2(view: View) {
        FlowEventBus.post("like2", "123")
    }

    fun changeData3(view: View) {
        FlowEventBus.post("like3", 3)
    }

    fun changeData4(view: View) {
        FlowEventBus.post("like4", 4)
    }

    fun startActivity2(view: View) {
        startActivity(Intent(this, SecondActivity::class.java))
    }
}
