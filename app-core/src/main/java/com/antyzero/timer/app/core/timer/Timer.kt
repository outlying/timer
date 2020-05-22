package com.antyzero.timer.app.core.timer

import kotlinx.coroutines.flow.StateFlow

interface Timer {

    val state: StateFlow<State>

    fun start()

    /**
     * Pause timer, we won't track time
     */
    fun pause()

    /**
     * Resume timer from pause
     */
    fun resume()
}