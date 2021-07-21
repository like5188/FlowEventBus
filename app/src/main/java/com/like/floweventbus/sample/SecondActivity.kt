package com.like.floweventbus.sample

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.floweventbus.FlowEventBus
import com.like.floweventbus.TAG
import com.like.floweventbus.sample.databinding.ActivitySecondBinding
import com.like.floweventbus_annotations.BusObserver

class SecondActivity : AppCompatActivity() {
    private var mBinding: ActivitySecondBinding? = null
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
        FlowEventBus.post("like1", 100)
    }

    fun changeData2(view: View?) {
        FlowEventBus.post("like2", User("name", 18))
    }

    @BusObserver(value = ["like1"], isSticky = true)
    fun observer1(i: Int) {
        Log.e(TAG, "SecondActivity onChanged tag=like1")
        mBinding!!.tv1.text = i.toString()
    }

    @BusObserver(value = ["like2"], requestCode = "re")
    fun observer2(u: User) {
        Log.e(TAG, "SecondActivity onChanged tag=like2")
        mBinding!!.tv2.text = u.toString()
    }
}