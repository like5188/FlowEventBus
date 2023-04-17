package com.like.floweventbus.sample.test.test1

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.floweventbus.FlowEventBus
import com.like.floweventbus.TAG
import com.like.floweventbus.sample.R
import com.like.floweventbus.sample.User
import com.like.floweventbus.sample.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivitySecondBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "SecondActivity onCreate")
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_second)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "SecondActivity onDestroy")
    }

    fun changeData1(view: View?) {
//        FlowEventBus.postAcrossProcess<String?>("MainActivity", null)
//        FlowEventBus.postAcrossProcess<String?>("MainActivity", "1")
//        FlowEventBus.postAcrossProcess("MainActivity", "1")
//
//        FlowEventBus.postAcrossProcess<Int?>("MainActivity", null)
//        FlowEventBus.postAcrossProcess<Int?>("MainActivity", 2)
//        FlowEventBus.postAcrossProcess("MainActivity", 2)
//
//        FlowEventBus.postAcrossProcess<IntArray?>("MainActivity", null)
//        FlowEventBus.postAcrossProcess<IntArray?>("MainActivity", intArrayOf(3))
//        FlowEventBus.postAcrossProcess("MainActivity", intArrayOf(3))
//
//        FlowEventBus.postAcrossProcess<Array<Int?>?>("MainActivity", null)
//        FlowEventBus.postAcrossProcess<Array<Int?>?>("MainActivity", arrayOf(4))
//        FlowEventBus.postAcrossProcess("MainActivity", arrayOf(4))
//
//        FlowEventBus.postAcrossProcess<Array<Int>?>("MainActivity", null)
//        FlowEventBus.postAcrossProcess<Array<Int>?>("MainActivity", arrayOf(5))
//        FlowEventBus.postAcrossProcess("MainActivity", arrayOf(5))
//
//        FlowEventBus.postAcrossProcess("MainActivity")
    }

    fun changeData2(view: View?) {
//        FlowEventBus.postAcrossProcess<User?>("SecondActivity", null)
//        FlowEventBus.postAcrossProcess<User?>("SecondActivity", User("SecondActivity", 19))
//        FlowEventBus.postAcrossProcess("SecondActivity", User("SecondActivity", 19))

//        FlowEventBus.postAcrossProcess<Array<User>?>("SecondActivity", null)
//        FlowEventBus.postAcrossProcess<Array<User>?>("SecondActivity", arrayOf(User("SecondActivity", 19)))
        FlowEventBus.postAcrossProcess("SecondActivity", arrayOf(User("SecondActivity", 19)))
    }

    fun unregister(view: View?) {
//        FlowEventBus.unregister(this)
    }

}
