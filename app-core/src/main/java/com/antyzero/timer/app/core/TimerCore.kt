package com.antyzero.timer.app.core

class TimerCore(
    private val timeProvider: TimeProvider
) {

    fun startTimer(seconds: Int) {
        print(seconds)
    }
}
