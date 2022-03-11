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
import kotlin.concurrent.thread

class MainActivity : BaseActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
        val mainViewModel = MainViewModel()
        FlowEventBus.register(mainViewModel, this)
        val mainViewModel1 = MainViewModel1()
        FlowEventBus.register(mainViewModel1, this)
        Log.w(TAG, "MainActivity onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.w(TAG, "MainActivity onDestroy")
    }

    @BusObserver(["like1", "like2"], requestCode = "1")
    fun observer1() {
        Log.e(TAG, "MainActivity observer1 tag=like1like2 requestCode=1")
    }

    @BusObserver(["like1", "like2"])
    fun observer2(data: String?) {
        Log.e(TAG, "MainActivity observer2 tag=like1like2 数据：$data")
    }

    fun changeData1(view: View) {
        FlowEventBus.post<Int?>("like1", null)
        FlowEventBus.post("like1", 5)
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
