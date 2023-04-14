package com.like.floweventbus.sample.test.test1

import a.b.c.test1.B
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
        B(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "SecondActivity onDestroy")
    }

    fun changeData1(view: View?) {
        FlowEventBus.post("like1")
        FlowEventBus.post("like2")
        FlowEventBus.post<User?>("like222", null)
        FlowEventBus.post("like222", User("like3", 3))
    }

    fun changeData2(view: View?) {
        FlowEventBus.postAcrossProcess("like1")
        FlowEventBus.postAcrossProcess("like2")
        FlowEventBus.postAcrossProcess<User?>("like222", null)
        FlowEventBus.postAcrossProcess("like222", User("like4", 4))
    }

    fun unregister(view: View?) {
        FlowEventBus.unregister(this)
    }

    @BusObserver(["like222"], isSticky = true)
    fun observer1(user: User?) {
        Log.w(TAG, "SecondActivity observer1 User? $user")
    }

    @BusObserver(["like222"], isSticky = true)
    fun observer2(user: User) {
        Log.w(TAG, "SecondActivity observer2 User $user")
    }
}
