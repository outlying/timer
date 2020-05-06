package com.antyzero.timer.app.core.timer

import com.antyzero.timer.app.core.TimeProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.TickerMode
import kotlinx.coroutines.channels.ticker
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset

class Timer(
    private val timeProvider: TimeProvider,
    private val seconds: Long
) {

    private val scope = CoroutineScope(Job() + Dispatchers.Unconfined)

    private val tickerFlow = ticker(
        delayMillis = 10,
        initialDelayMillis = 0L,
        mode = TickerMode.FIXED_PERIOD,
        context = Dispatchers.Unconfined
    )

    var state: State = State.Unstarted

    private var tickerJob = scope.launch { }
    private var expireTime: LocalDateTime = LocalDateTime.MAX
    private var remainingTime = Long.MAX_VALUE

    fun start() {
        expireTime = timeProvider.now().plusSeconds(seconds)
        startTicker()
    }

    /**
     * Pause timer, we won't track time
     */
    fun pause() {
        stopTicker()
        state = State.Pause(remainingTime)
    }

    /**
     * Resume timer from pause
     */
    fun resume() {
        expireTime = timeProvider.now().plusNanos(remainingTime * 1_000_000)
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
            for (event in tickerFlow) {
                val now = timeProvider.now()
                if (now.isAfter(expireTime)) {
                    state = State.Done
                    cancel()
                } else {
                    remainingTime =
                        expireTime.toInstant(ZoneOffset.UTC).toEpochMilli() -
                                now.toInstant(ZoneOffset.UTC).toEpochMilli()

                    state = State.Running(remainingTime)
                }
            }
        }
    }

    sealed class State {

        object Unstarted : State()

        data class Running(val remainingTime: Long) : State()

        data class Pause(val remainingTime: Long) : State()

        object Done : State()
    }
}