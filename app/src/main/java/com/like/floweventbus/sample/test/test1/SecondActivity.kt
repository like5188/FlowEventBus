package com.like.floweventbus.sample.test.test1

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.floweventbus.FlowEventBus
import com.like.floweventbus.TAG
import com.like.floweventbus.annotations.BusObserver
import com.like.floweventbus.sample.R
import com.like.floweventbus.sample.User
import com.like.floweventbus.sample.databinding.ActivitySecondBinding
import java.util.*

class SecondActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivitySecondBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "SecondActivity onCreate")
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_second)
//        FlowEventBus.register(this)
//        B(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "SecondActivity onDestroy")
    }

    fun changeData1(view: View?) {
//        FlowEventBus.post<Array<Int?>?>("SecondActivity", null)
//        FlowEventBus.post<Array<Int?>?>("SecondActivity", arrayOf(0, null, 2))
//        FlowEventBus.post<Array<Int?>>("SecondActivity", arrayOf(0, null, 2))
//
//        FlowEventBus.post<Array<Int>?>("SecondActivity", null)
//        FlowEventBus.post<Array<Int>?>("SecondActivity", arrayOf(0, 2))
//        FlowEventBus.post<Array<Int>>("SecondActivity", arrayOf(0, 2))
//
//        FlowEventBus.post<IntArray?>("SecondActivity", null)
//        FlowEventBus.post<IntArray?>("SecondActivity", intArrayOf(0))
//        FlowEventBus.post<IntArray>("SecondActivity", intArrayOf(0))

//        FlowEventBus.post("MainActivity")
//        FlowEventBus.post("MainActivity", 1)
//        FlowEventBus.post<Array<String>>("like333", arrayOf("1", "2"))
//        FlowEventBus.post<Array<String>?>("like333", null)
//        FlowEventBus.post<IntArray>("like444", intArrayOf(1, 2))
//        FlowEventBus.post<IntArray?>("like444", null)
//        FlowEventBus.post<User?>("like222", null)
//        FlowEventBus.post("SecondActivity", User("MainActivity", 1))
    }

    fun changeData2(view: View?) {
//        FlowEventBus.postAcrossProcess("MainActivity")
//        FlowEventBus.postAcrossProcess("MainActivity", 1)
//        FlowEventBus.postAcrossProcess<Array<String>>("SecondActivity", arrayOf("1", "2"))
//        FlowEventBus.postAcrossProcess<Array<String>?>("SecondActivity", null)
//        FlowEventBus.postAcrossProcess<IntArray>("SecondActivity", intArrayOf(1, 2))
//        FlowEventBus.postAcrossProcess<IntArray?>("SecondActivity", null)
//        FlowEventBus.postAcrossProcess<User?>("SecondActivity", null)
//        FlowEventBus.postAcrossProcess("SecondActivity", User("like3", 3))
    }

    fun unregister(view: View?) {
        FlowEventBus.unregister(this)
    }

//    @BusObserver(["SecondActivity"], isSticky = true)
//    fun observer1(data: Array<Int?>?) {
//        Log.w(TAG, "SecondActivity observer1 Array<Int?>? ${Arrays.toString(data)}")
//    }
//
//    @BusObserver(["SecondActivity"], isSticky = true)
//    fun observer2(data: Array<Int?>) {
//        Log.w(TAG, "SecondActivity observer2 Array<Int?> ${Arrays.toString(data)}")
//    }
//
//    @BusObserver(["SecondActivity"], isSticky = true)
//    fun observer3(data: Array<Int>?) {
//        Log.w(TAG, "SecondActivity observer3 Array<Int>? ${Arrays.toString(data)}")
//    }
//
//    @BusObserver(["SecondActivity"], isSticky = true)
//    fun observer4(data: Array<Int>) {
//        Log.w(TAG, "SecondActivity observer4 Array<Int> ${Arrays.toString(data)}")
//    }
//
//    @BusObserver(["SecondActivity"], isSticky = true)
//    fun observer5(data: IntArray?) {
//        Log.w(TAG, "SecondActivity observer5 IntArray? ${Arrays.toString(data)}")
//    }
//
//    @BusObserver(["SecondActivity"], isSticky = true)
//    fun observer6(data: IntArray) {
//        Log.w(TAG, "SecondActivity observer6 IntArray ${Arrays.toString(data)}")
//    }
//
//    @BusObserver(["SecondActivity"], isSticky = true)
//    fun observer7(data: Int?) {
//        Log.w(TAG, "SecondActivity observer7 Int? $data")
//    }
//
//    @BusObserver(["SecondActivity"], isSticky = true)
//    fun observer8(data: Int) {
//        Log.w(TAG, "SecondActivity observer8 Int $data")
//    }

//    @BusObserver(["SecondActivity"], isSticky = true)
//    fun observer9(user: User?) {
//        Log.w(TAG, "SecondActivity observer9 User? $user")
//    }
//
//    @BusObserver(["SecondActivity"], isSticky = true)
//    fun observer10(user: User) {
//        Log.w(TAG, "SecondActivity observer10 User $user")
//    }

}
