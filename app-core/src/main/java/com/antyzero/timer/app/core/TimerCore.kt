package com.antyzero.timer.app.core

import com.antyzero.timer.app.core.timer.StandardTimer
import com.antyzero.timer.app.core.timer.Timer

class TimerCore(
    private val timeProvider: TimeProvider
) {
    private val timers: MutableSet<Timer> = mutableSetOf()

    fun createTimer(timerTimeInSeconds: Long): Timer {
        return StandardTimer(timeProvider, timerTimeInSeconds).also { timers }
    }

    fun deregisterTimer(timer: Timer) {
        timers.remove(timer)
        // TODO should we destroy it ?
    }
}
