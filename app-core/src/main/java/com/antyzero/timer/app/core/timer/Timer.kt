package com.antyzero.timer.app.core.timer

import com.antyzero.timer.app.core.TimeProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset

class Timer(
    private val timeProvider: TimeProvider,
    private val seconds: Long
) {

    val state: StateFlow<State>
        get() = mutableStateFlow

    private val scope = CoroutineScope(Job() + Dispatchers.Unconfined)

    private var tickerJob = scope.launch { }
    private var expireTime: LocalDateTime = LocalDateTime.MAX
    private var remainingTime = Long.MAX_VALUE

    private var _state: State = State.Idle
        set(value) {
            // Only unique state is handled
            if (field != value) {
                scope.launch {
                    mutableStateFlow.value = value
                }
            }
            field = value
        }

    // Do not modify directly, use [_state]
    private val mutableStateFlow = MutableStateFlow<State>(State.Idle)

    fun start() {
        expireTime = timeProvider.currentTime.value.plusSeconds(seconds)
        startTicker()
    }

    /**
     * Pause timer, we won't track time
     */
    fun pause() {
        stopTicker()
        _state = State.Pause(remainingTime)
    }

    /**
     * Resume timer from pause
     */
    fun resume() {
        expireTime = timeProvider.currentTime.value.plusNanos(remainingTime * 1_000_000)
        startTicker()
    }

    /**
     * Stop tracking time
     */
    private fun stopTicker() {
        tickerJob.cancel()
    }

    /**
     * Starts ticker
     *
     * If ticker was running it will stop and started again, however this does not change timer
     * expire time, it start counting towards remaining time again.
     */
    private fun startTicker() {
        stopTicker()

        tickerJob = scope.launch {
            timeProvider.currentTime.collect { now ->
                if (now.isAfter(expireTime)) {
                    _state = State.Done
                    cancel()
                } else {
                    remainingTime =
                        expireTime.toInstant(ZoneOffset.UTC).toEpochMilli() -
                                now.toInstant(ZoneOffset.UTC).toEpochMilli()

                    _state = State.Running(remainingTime)
                }
            }
        }
    }

    sealed class State {

        object Idle : State() {

            override fun toString() = "Idle"
        }

        data class Running(val remainingTime: Long) : State()

        data class Pause(val remainingTime: Long) : State()

        object Done : State()
    }
}