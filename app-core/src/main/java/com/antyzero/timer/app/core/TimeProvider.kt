package com.antyzero.timer.app.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime

/**
 * Provides time with [currentTime] updates, it should be updated each time time changes.
 *
 * Useful for testing also to change precision of time
 */
interface TimeProvider {

    val currentTime: StateFlow<LocalDateTime>

    /**
     * Standard [TimeProvider] implementation
     *
     * @property [interval] amount of milliseconds between ticks
     */
    class Standard(private val interval: Long = 10L) : TimeProvider {

        private val _currentTime = MutableStateFlow(LocalDateTime.now()) // private mutable state flow
        override val currentTime: StateFlow<LocalDateTime>
            get() = _currentTime

        private val tickerChannel = kotlinx.coroutines.channels.ticker(interval, 0L)

        private val job = GlobalScope.launch(Dispatchers.Unconfined) {
            for (event in tickerChannel) {
                _currentTime.value = LocalDateTime.now()
            }
        }
    }
}