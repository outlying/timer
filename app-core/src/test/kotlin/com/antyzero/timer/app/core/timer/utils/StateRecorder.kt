package com.antyzero.timer.app.core.timer.utils

import com.antyzero.timer.app.core.timer.State
import com.antyzero.timer.app.core.timer.Timer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class StateRecorder(timer: Timer) {

    private val _list: MutableList<State> = mutableListOf()
    private val scope = CoroutineScope(Dispatchers.Unconfined)

    val list: List<State>
        get() = _list

    init {
        scope.launch(Dispatchers.Unconfined) {
            timer.state.collect {
                _list.add(it)
            }
        }
    }
}

fun Timer.createStateRecorder(): StateRecorder = StateRecorder(this)