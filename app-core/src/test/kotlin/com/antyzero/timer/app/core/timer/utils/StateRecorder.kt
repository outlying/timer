package com.antyzero.timer.app.core.timer.utils

import com.antyzero.timer.app.core.timer.Timer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

class StateRecorder(timer: Timer) {

    private val _list: MutableList<Timer.State> = mutableListOf()
    private val scope = CoroutineScope(Dispatchers.Unconfined)

    val list: List<Timer.State>
        get() = _list

    init {
        scope.launch(Dispatchers.IO) {
            timer.state.consumeEach {
                _list.add(it)
            }
        }
    }
}

fun Timer.createStateRecorder(): StateRecorder = StateRecorder(this)