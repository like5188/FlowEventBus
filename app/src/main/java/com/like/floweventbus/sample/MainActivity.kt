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

    fun changeData(view: View) {
//        FlowEventBus.post<String?>("MainActivity", null)
//        FlowEventBus.post<String?>("MainActivity", "1")
//        FlowEventBus.post("MainActivity", "1")

//        FlowEventBus.post<Int?>("MainActivity", null)
//        FlowEventBus.post<Int?>("MainActivity", 2)
//        FlowEventBus.post("MainActivity", 2)

//        FlowEventBus.post<IntArray?>("MainActivity", null)
//        FlowEventBus.post<IntArray?>("MainActivity", intArrayOf(3))
//        FlowEventBus.post("MainActivity", intArrayOf(3))

//        FlowEventBus.post<Array<Int?>?>("MainActivity", null)
//        FlowEventBus.post<Array<Int?>?>("MainActivity", arrayOf(4, null))
//        FlowEventBus.post("MainActivity", arrayOf(4, null))

//        FlowEventBus.post<Array<Int>?>("MainActivity", null)
//        FlowEventBus.post<Array<Int>?>("MainActivity", arrayOf(5))
//        FlowEventBus.post("MainActivity", arrayOf(5))

//        FlowEventBus.post("MainActivity")
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

//    @BusObserver(["MainActivity"])
//    fun observer7(data: Array<Int?>?) {
//        Log.w(TAG, "MainActivity observer7 Array<Int?>? ${Arrays.toString(data)}")
//    }
//
//    @BusObserver(["MainActivity"])
//    fun observer8(data: Array<Int?>) {
//        Log.w(TAG, "MainActivity observer8 Array<Int?> ${Arrays.toString(data)}")
//    }

    // Array<Int?>? 和 Array<Int>? 一样，没有做泛型为空时的区分
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

    @BusObserver(["SecondActivity"])
    fun observer12(user: User?) {
        Log.w(TAG, "MainActivity observer12 User? $user")
    }

    @BusObserver(["SecondActivity"])
    fun observer13(user: User) {
        Log.w(TAG, "MainActivity observer13 User $user")
    }

    @BusObserver(["SecondActivity"])
    fun observer14(bundle: Bundle?) {
        when {
            bundle == null -> {
                Log.w(TAG, "MainActivity observer14 Bundle")
            }
            bundle.containsKey("users") -> {
                val users: List<User>? = bundle.getParcelableArray("users")?.map {
                    it as User
                }
                Log.w(TAG, "MainActivity observer14 Bundle users=$users")
            }
        }
    }

    @BusObserver(["SecondActivity"])
    fun observer15(bundle: Bundle) {
        when {
            bundle.containsKey("users") -> {
                val users: List<User>? = bundle.getParcelableArray("users")?.map {
                    it as User
                }
                Log.w(TAG, "MainActivity observer15 Bundle users=$users")
            }
        }
    }

}
