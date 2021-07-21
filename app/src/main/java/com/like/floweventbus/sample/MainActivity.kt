package com.like.floweventbus.sample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.like.floweventbus.FlowEventBus
import com.like.floweventbus.TAG
import com.like.floweventbus.sample.databinding.ActivityMainBinding
import kotlin.concurrent.thread

class MainActivity : BaseActivity1() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
        val mainViewModel1 = MainViewModel()
        FlowEventBus.register<Int>(this, "tag1", "", false) {
            Log.e(TAG, "MainActivity tag=tag1，数据：$it")
        }
        Log.w(TAG, "MainActivity onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.w(TAG, "MainActivity onDestroy")
    }

    fun changeData1(view: View) {
        FlowEventBus.post("tag1", 1)
    }

    fun changeData2(view: View) {
        thread {
            FlowEventBus.post("tag2", 2)
        }
    }

    fun changeData3(view: View) {
        FlowEventBus.post("tag2", "requestCode2", 22)
    }

    fun changeData4(view: View) {
        FlowEventBus.post("tag3", 3)
    }

    fun changeData5(view: View) {
    }

    fun startActivity2(view: View) {
        startActivity(Intent(this, SecondActivity::class.java))
    }
}
