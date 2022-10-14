package com.like.floweventbus.sample

import a.b.c.test1.B
import a.b.c.test2.C
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.like.floweventbus.FlowEventBus
import com.like.floweventbus.TAG
import com.like.floweventbus.sample.databinding.ActivityMainBinding
import com.like.floweventbus.sample.test.test1.SecondActivity
import com.like.floweventbus_annotations.BusObserver

class MainActivity : BaseActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
        Log.w(TAG, "MainActivity onCreate")
        FlowEventBus.init()
        FlowEventBus.register(this)
        B(this)
        C(this)
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.w(TAG, "MainActivity onDestroy")
    }

    fun changeData1(view: View) {
        FlowEventBus.post<String?>("like1", null)
        FlowEventBus.post("like1", "1")
        FlowEventBus.post<Int?>("like1", null)
        FlowEventBus.post("like1", 1)
        FlowEventBus.post<Boolean?>("like1", null)
        FlowEventBus.post("like1", true)
        FlowEventBus.post<IntArray?>("like1", null)
        FlowEventBus.post("like1", intArrayOf(1))
        FlowEventBus.post<Array<String>?>("like1", null)
        FlowEventBus.post("like1", arrayOf(""))
    }

    fun changeData2(view: View) {
        FlowEventBus.post<User?>("like222", null)
        FlowEventBus.post("like222", User("like", 18))
    }

    fun startActivity2(view: View) {
        startActivity(Intent(this, SecondActivity::class.java))
    }

    @BusObserver(["like1"])
    fun observer1(str: String?) {
        Log.w(TAG, "MainActivity observer1 $str")
    }

    @BusObserver(["like1"])
    fun observer2(str: String) {
        Log.w(TAG, "MainActivity observer2 $str")
    }

    @BusObserver(["like1"])
    fun observer3(str: Int?) {
        Log.w(TAG, "MainActivity observer3 $str")
    }

    @BusObserver(["like1"])
    fun observer4(str: Int) {
        Log.w(TAG, "MainActivity observer4 $str")
    }

    @BusObserver(["like1"])
    fun observer5(str: Boolean?) {
        Log.w(TAG, "MainActivity observer3 $str")
    }

    @BusObserver(["like1"])
    fun observer6(str: Boolean) {
        Log.w(TAG, "MainActivity observer4 $str")
    }

    @BusObserver(["like1"])
    fun observer7(str: IntArray?) {
        Log.w(TAG, "MainActivity observer4 $str")
    }

    @BusObserver(["like1"])
    fun observer8(str: IntArray) {
        Log.w(TAG, "MainActivity observer4 $str")
    }

    @BusObserver(["like1"])
    fun observer9(str: Array<String>?) {
        Log.w(TAG, "MainActivity observer4 $str")
    }

    @BusObserver(["like1"])
    fun observer10(str: Array<String>) {
        Log.w(TAG, "MainActivity observer4 $str")
    }

}
