package com.antyzero.timer.app.core.timer

sealed class State {

    object Idle : State() {

        override fun toString() = "Idle"
    }

    data class Running(val remainingTime: Long) : State()

    data class Pause(val remainingTime: Long) : State()

    object Done : State()
}