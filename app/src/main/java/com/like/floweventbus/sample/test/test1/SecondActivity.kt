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

class SecondActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivitySecondBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "SecondActivity onCreate")
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_second)
        FlowEventBus.register(this)
//        B(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "SecondActivity onDestroy")
    }

    fun changeData1(view: View?) {
        FlowEventBus.post<User?>("SecondActivity", null)
        FlowEventBus.post<User?>("SecondActivity", User("SecondActivity", 19))
        FlowEventBus.post("SecondActivity", User("SecondActivity", 19))
    }

    fun changeData2(view: View?) {
    }

    fun unregister(view: View?) {
        FlowEventBus.unregister(this)
    }

    @BusObserver(["SecondActivity"], isSticky = true)
    fun observer1(user: User?) {
        Log.w(TAG, "SecondActivity observer1 User? $user")
    }

    @BusObserver(["SecondActivity"], isSticky = true)
    fun observer2(user: User) {
        Log.w(TAG, "SecondActivity observer2 User $user")
    }

}
