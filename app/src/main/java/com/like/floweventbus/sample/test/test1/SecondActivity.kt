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
import com.like.floweventbus.annotations.BusObserver

class SecondActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivitySecondBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_second)
        FlowEventBus.register(this)
        Log.e(TAG, "SecondActivity onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "SecondActivity onDestroy")
    }

    fun changeData1(view: View?) {
        FlowEventBus.post("like222")
    }

    fun unregister(view: View?) {
        FlowEventBus.unregister(this)
    }

    @BusObserver(["like222"], isSticky = true)
    fun observer1(user1: User?) {
        Log.w(TAG, "SecondActivity observer1 User? $user1")
    }

    @BusObserver(["like222"], isSticky = true)
    fun observer2(user1: User) {
        Log.w(TAG, "SecondActivity observer2 User $user1")
    }
}
