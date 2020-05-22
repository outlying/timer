package com.antyzero.timer.app.core.timer

sealed class State {

    object Idle : StandardTimer.State() {

        override fun toString() = "Idle"
    }

    data class Running(val remainingTime: Long) : StandardTimer.State()

    data class Pause(val remainingTime: Long) : StandardTimer.State()

    object Done : StandardTimer.State()
}