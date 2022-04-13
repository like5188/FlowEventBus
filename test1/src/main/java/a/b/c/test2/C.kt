package a.b.c.test2

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.like.floweventbus.FlowEventBus
import com.like.floweventbus.TAG
import com.like.floweventbus_annotations.BusObserver

class C(owner: LifecycleOwner) {
    init {
        FlowEventBus.register(this, owner)
    }

    @BusObserver(["like2"])
    fun observer2() {
        Log.w(TAG, "like2")
    }

}
