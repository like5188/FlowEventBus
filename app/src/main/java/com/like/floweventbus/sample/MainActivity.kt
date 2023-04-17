package com.like.floweventbus.sample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.like.floweventbus.FlowEventBus
import com.like.floweventbus.TAG
import com.like.floweventbus.annotations.BusObserver
import com.like.floweventbus.sample.databinding.ActivityMainBinding
import com.like.floweventbus.sample.test.test1.SecondActivity
import java.util.*

class MainActivity : BaseActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
        Log.w(TAG, "MainActivity onCreate")
        FlowEventBus.register(this)
//        B(this)
//        C(this)
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.w(TAG, "MainActivity onDestroy")
    }

    fun changeData1(view: View) {
//        FlowEventBus.post<String?>("MainActivity", null)
//        FlowEventBus.post<String?>("MainActivity", "1")
//        FlowEventBus.post("MainActivity", "1")
//
//        FlowEventBus.post<Int?>("MainActivity", null)
//        FlowEventBus.post<Int?>("MainActivity", 2)
//        FlowEventBus.post("MainActivity", 2)
//
//        FlowEventBus.post<IntArray?>("MainActivity", null)
//        FlowEventBus.post<IntArray?>("MainActivity", intArrayOf(3))
//        FlowEventBus.post("MainActivity", intArrayOf(3))
//
//        FlowEventBus.post<Array<Int?>?>("MainActivity", null)
//        FlowEventBus.post<Array<Int?>?>("MainActivity", arrayOf(4))
//        FlowEventBus.post("MainActivity", arrayOf(4))
//
//        FlowEventBus.post<Array<Int>?>("MainActivity", null)
//        FlowEventBus.post<Array<Int>?>("MainActivity", arrayOf(5))
//        FlowEventBus.post("MainActivity", arrayOf(5))
//
//        FlowEventBus.post("MainActivity")
//
//        FlowEventBus.post<Byte?>("MainActivity", null)
//        FlowEventBus.post<Byte?>("MainActivity", 2)
//        FlowEventBus.post<Byte>("MainActivity", 2)
//
//        FlowEventBus.post<Short?>("MainActivity", null)
//        FlowEventBus.post<Short?>("MainActivity", 2)
//        FlowEventBus.post<Short>("MainActivity", 2)
//
//        FlowEventBus.post<Long?>("MainActivity", null)
//        FlowEventBus.post<Long?>("MainActivity", 2)
//        FlowEventBus.post("MainActivity", 2L)
//
//        FlowEventBus.post<Float?>("MainActivity", null)
//        FlowEventBus.post<Float?>("MainActivity", 2f)
//        FlowEventBus.post("MainActivity", 2f)
//
//        FlowEventBus.post<Double?>("MainActivity", null)
//        FlowEventBus.post<Double?>("MainActivity", 2.0)
//        FlowEventBus.post("MainActivity", 2.0)
//
//        FlowEventBus.post<Char?>("MainActivity", null)
//        FlowEventBus.post<Char?>("MainActivity", 'A')
//        FlowEventBus.post("MainActivity", 'A')
//
//        FlowEventBus.post<Boolean?>("MainActivity", null)
//        FlowEventBus.post<Boolean?>("MainActivity", true)
//        FlowEventBus.post("MainActivity", true)
    }

    fun changeData2(view: View) {
        FlowEventBus.postAcrossProcess<User?>("SecondActivity", null)
        FlowEventBus.postAcrossProcess<User?>("SecondActivity", User("MainActivity", 18))
        FlowEventBus.postAcrossProcess("SecondActivity", User("MainActivity", 18))
    }

    fun startActivity2(view: View) {
        startActivity(Intent(this, SecondActivity::class.java))
    }

    @BusObserver(["MainActivity"])
    fun observer1(str: String?) {
        Log.w(TAG, "MainActivity observer1 String? $str")
    }

    @BusObserver(["MainActivity"])
    fun observer2(str: String) {
        Log.w(TAG, "MainActivity observer2 String $str")
    }

    @BusObserver(["MainActivity"])
    fun observer3(str: Int?) {
        Log.w(TAG, "MainActivity observer3 Int? $str")
    }

    @BusObserver(["MainActivity"])
    fun observer4(str: Int) {
        Log.w(TAG, "MainActivity observer4 Int $str")
    }

    @BusObserver(["MainActivity"])
    fun observer5(str: IntArray?) {
        Log.w(TAG, "MainActivity observer5 IntArray? $str")
    }

    @BusObserver(["MainActivity"])
    fun observer6(str: IntArray) {
        Log.w(TAG, "MainActivity observer6 IntArray $str")
    }

    @BusObserver(["MainActivity"])
    fun observer7(data: Array<Int?>?) {
        Log.w(TAG, "MainActivity observer7 Array<Int?>? ${Arrays.toString(data)}")
    }

    @BusObserver(["MainActivity"])
    fun observer8(data: Array<Int?>) {
        Log.w(TAG, "MainActivity observer8 Array<Int?> ${Arrays.toString(data)}")
    }

    @BusObserver(["MainActivity"])
    fun observer9(data: Array<Int>?) {
        Log.w(TAG, "MainActivity observer9 Array<Int>? ${Arrays.toString(data)}")
    }

    @BusObserver(["MainActivity"])
    fun observer10(data: Array<Int>) {
        Log.w(TAG, "MainActivity observer10 Array<Int> ${Arrays.toString(data)}")
    }

    @BusObserver(["MainActivity"])
    fun observer11() {
        Log.w(TAG, "MainActivity observer11")
    }

    @BusObserver(["MainActivity"])
    fun observer(str: Byte?) {
        Log.w(TAG, "MainActivity observer Byte? $str")
    }

    @BusObserver(["MainActivity"])
    fun observer(str: Byte) {
        Log.w(TAG, "MainActivity observer Byte $str")
    }

    @BusObserver(["MainActivity"])
    fun observer(str: Short?) {
        Log.w(TAG, "MainActivity observer Short? $str")
    }

    @BusObserver(["MainActivity"])
    fun observer(str: Short) {
        Log.w(TAG, "MainActivity observer Short $str")
    }

    @BusObserver(["MainActivity"])
    fun observer(str: Long?) {
        Log.w(TAG, "MainActivity observer Long? $str")
    }

    @BusObserver(["MainActivity"])
    fun observer(str: Long) {
        Log.w(TAG, "MainActivity observer Long $str")
    }

    @BusObserver(["MainActivity"])
    fun observer(str: Float?) {
        Log.w(TAG, "MainActivity observer Float? $str")
    }

    @BusObserver(["MainActivity"])
    fun observer(str: Float) {
        Log.w(TAG, "MainActivity observer Float $str")
    }

    @BusObserver(["MainActivity"])
    fun observer(str: Double?) {
        Log.w(TAG, "MainActivity observer Double? $str")
    }

    @BusObserver(["MainActivity"])
    fun observer(str: Double) {
        Log.w(TAG, "MainActivity observer Double $str")
    }

    @BusObserver(["MainActivity"])
    fun observer(str: Char?) {
        Log.w(TAG, "MainActivity observer Char? $str")
    }

    @BusObserver(["MainActivity"])
    fun observer(str: Char) {
        Log.w(TAG, "MainActivity observer Char $str")
    }

    @BusObserver(["MainActivity"])
    fun observer(str: Boolean?) {
        Log.w(TAG, "MainActivity observer Boolean? $str")
    }

    @BusObserver(["MainActivity"])
    fun observer(str: Boolean) {
        Log.w(TAG, "MainActivity observer Boolean $str")
    }
}
