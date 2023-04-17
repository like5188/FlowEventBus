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
        FlowEventBus.post<String?>("MainActivity", null)
        FlowEventBus.post<String?>("MainActivity", "1")
        FlowEventBus.post("MainActivity", "1")
        FlowEventBus.post<Int?>("MainActivity", null)
        FlowEventBus.post("MainActivity", 1)
        FlowEventBus.post<IntArray?>("MainActivity", null)
        FlowEventBus.post("MainActivity", intArrayOf(1))
        FlowEventBus.post("MainActivity")

//        FlowEventBus.post<User?>("like222", null)
//        FlowEventBus.post("like222", User("MainActivity", 1))
    }

    fun changeData2(view: View) {
        FlowEventBus.postAcrossProcess<User?>("SecondActivity", null)
        FlowEventBus.postAcrossProcess("SecondActivity", User("like2", 2))
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
    fun observer7() {
        Log.w(TAG, "MainActivity observer7")
    }

//    @BusObserver(["SecondActivity"])
//    fun observer8(user: User?) {
//        Log.w(TAG, "MainActivity observer8 User $user")
//    }
}
