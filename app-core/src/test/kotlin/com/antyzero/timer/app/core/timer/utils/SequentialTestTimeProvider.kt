package com.antyzero.timer.app.core.timer.utils

import com.antyzero.timer.app.core.TimeProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset

class SequentialTestTimeProvider(vararg secondsSequence: Long) : TimeProvider {

    private val sequenceFlow: Flow<LocalDateTime> = secondsSequence.asFlow()
        .map { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) }

    override fun now(): LocalDateTime = runBlocking { sequenceFlow.first() }
}