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
//        FlowEventBus.register(this)
        val b1 = B(this)
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
        FlowEventBus.post("like1")
//        FlowEventBus.postAcrossProcess<User?>("like222", null)
    }

    fun changeData2(view: View) {
        FlowEventBus.post("like2")
//        FlowEventBus.postAcrossProcess("like222", User("like1", 189))
    }

    fun startActivity2(view: View) {
        startActivity(Intent(this, SecondActivity::class.java))
    }

//    @BusObserver(["like1", "like2"])
//    fun observer1(str: String?) {
//        Log.w(TAG, "MainActivity observer1 String? $str")
//    }
//
//    @BusObserver(["like1", "like2"])
//    fun observer2(str: String) {
//        Log.w(TAG, "MainActivity observer2 String $str")
//    }
//
//    @BusObserver(["like1", "like2"])
//    fun observer3(str: Int?) {
//        Log.w(TAG, "MainActivity observer3 Int? $str")
//    }
//
//    @BusObserver(["like1", "like2"])
//    fun observer4(str: Int) {
//        Log.w(TAG, "MainActivity observer4 Int $str")
//    }
//
//    @BusObserver(["like1", "like2"])
//    fun observer5(str: IntArray?) {
//        Log.w(TAG, "MainActivity observer5 IntArray? $str")
//    }
//
//    @BusObserver(["like1", "like2"])
//    fun observer6(str: IntArray) {
//        Log.w(TAG, "MainActivity observer6 IntArray $str")
//    }
//
//    @BusObserver(["like1", "like2"])
//    fun observer7() {
//        Log.w(TAG, "MainActivity observer7")
//    }
//
//    @BusObserver(["like222"])
//    fun observer8(user: User?) {
//        Log.w(TAG, "MainActivity observer8 User $user")
//    }
}
