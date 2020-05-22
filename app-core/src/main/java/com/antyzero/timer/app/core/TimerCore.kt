package com.antyzero.timer.app.core

import com.antyzero.timer.app.core.timer.StandardTimer

class TimerCore(
    private val timeProvider: TimeProvider
) {
    private val timers: MutableSet<StandardTimer> = mutableSetOf()

    fun registerNewTimer(timerTimeInSeconds: Long): StandardTimer {
        return StandardTimer(timeProvider, timerTimeInSeconds).also { timers }
    }

    fun deregisterTimer(timer: StandardTimer) {
        timers.remove(timer)
        // TODO should we destroy it ?
    }
}
