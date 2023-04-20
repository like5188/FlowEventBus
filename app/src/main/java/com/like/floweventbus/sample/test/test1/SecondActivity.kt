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
        FlowEventBus.postAcrossProcess("SecondActivity")
        FlowEventBus.postAcrossProcess("SecondActivity", Bundle().apply {
            putParcelable("user", User("SecondActivity", 19))
        })
        FlowEventBus.postAcrossProcess("SecondActivity", Bundle().apply {
            putParcelableArray("users", arrayOf(User("SecondActivity", 19)))
        })
    }

    fun changeData2(view: View?) {
    }

    fun unregister(view: View?) {
//        FlowEventBus.unregister(this)
    }

}
