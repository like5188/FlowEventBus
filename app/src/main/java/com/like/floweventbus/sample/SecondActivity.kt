package com.like.floweventbus.sample

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.floweventbus.FlowEventBus
import com.like.floweventbus.TAG
import com.like.floweventbus.sample.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivitySecondBinding>(this, R.layout.activity_second)
        FlowEventBus.register<Int>(this, "tag3", "", true) {
            Log.e(TAG, "MainActivity tag=tag3，数据：$it")
        }
        Log.e("LiveDataBus", "SecondActivity onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("LiveDataBus", "SecondActivity onDestroy")
    }

    fun changeData1(view: View?) {
        FlowEventBus.post("tag3", "33")
    }

    fun changeData2(view: View?) {
        FlowEventBus.post("tag3", 33)
    }

}
