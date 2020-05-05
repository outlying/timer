package com.antyzero.timer.app.core.timer

import com.antyzero.timer.app.core.TimeProvider
import kotlinx.coroutines.channels.TickerMode
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.runBlocking
import org.threeten.bp.LocalTime

class Timer(
    private val timeProvider: TimeProvider,
    private val seconds: Long
) {

    private var expireTime: LocalTime? = null

    fun start() {
        expireTime = timeProvider.now().plusSeconds(seconds)

        runBlocking {

            ticker(
                delayMillis = 1000,
                initialDelayMillis = 0L,
                mode = TickerMode.FIXED_PERIOD
            ).consumeAsFlow().collect {
                println(System.currentTimeMillis())
            }
        }
    }
}