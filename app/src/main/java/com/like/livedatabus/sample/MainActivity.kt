package com.like.livedatabus.sample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.like.livedatabus.LiveDataBus
import com.like.livedatabus.sample.databinding.ActivityMainBinding
import com.like.livedatabus_annotations.BusObserver
import kotlin.concurrent.thread

class MainActivity : BaseActivity1() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
        val mainViewModel1 = MainViewModel()
        LiveDataBus.register(mainViewModel1, this)
        val mainViewModel2 = MainViewModel()
        LiveDataBus.register(mainViewModel2, this)
        Log.w("LiveDataBus", "MainActivity onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.w("LiveDataBus", "MainActivity onDestroy")
    }

    @BusObserver(["like1", "like2"], requestCode = "1")
    fun observer1() {
        Log.e("LiveDataBus", "MainActivity observer1 tag=like1 requestCode=1")
    }

    @BusObserver(["like1", "like2"])
    fun observer2(s: Int?) {
        Log.e("LiveDataBus", "MainActivity observer2 tag=like1，数据：$s")
    }

    fun changeData1(view: View) {
        LiveDataBus.post("like1", null)
        LiveDataBus.post("like2", 123)
    }

    fun changeData2(view: View) {
        thread {
            LiveDataBus.post("like2", "1", 2)
        }
    }

    fun changeData3(view: View) {
        LiveDataBus.post("like3", 3)
    }

    fun changeData4(view: View) {
        LiveDataBus.post("like4", 4)
    }

    fun changeData5(view: View) {
        LiveDataBus.post("like5", 5)
    }

    fun startActivity2(view: View) {
        startActivity(Intent(this, SecondActivity::class.java))
    }
}
