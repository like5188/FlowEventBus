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

class MainActivity : BaseActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
        Log.w(TAG, "MainActivity onCreate")
        FlowEventBus.init()
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
        FlowEventBus.post("like1")
        FlowEventBus.post("like2")
        FlowEventBus.post("like222", User("like", 18))
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
