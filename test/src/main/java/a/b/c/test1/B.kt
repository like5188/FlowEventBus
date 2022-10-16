package a.b.c.test1

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.like.floweventbus.FlowEventBus
import com.like.floweventbus.TAG
import com.like.floweventbus_annotations.BusObserver

class B(owner: LifecycleOwner) {
    init {
        FlowEventBus.register(this, owner)
    }

    @BusObserver(["like1"])
    fun observer1() {
        Log.w(TAG, "B observer1")
    }

}
