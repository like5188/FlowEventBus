package a.b.c.test2

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.like.floweventbus.FlowEventBus
import com.like.floweventbus.TAG
import com.like.floweventbus.annotations.BusObserver

class C(owner: LifecycleOwner) {
    init {
        FlowEventBus.register(this, owner)
    }

//    @BusObserver(["like2"])
//    fun observer1() {
//        Log.w(TAG, "C observer1")
//    }

}
