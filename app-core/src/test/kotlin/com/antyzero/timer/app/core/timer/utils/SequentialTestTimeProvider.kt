package com.antyzero.timer.app.core.timer.utils

import com.antyzero.timer.app.core.TimeProvider
import kotlinx.coroutines.runBlocking
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import java.util.LinkedList
import java.util.Queue

class SequentialTestTimeProvider(vararg secondsSequence: Long) : TimeProvider {

    private val sequenceFlow: Queue<LocalDateTime> = LinkedList(listOf(0L).plus(secondsSequence.toList())
        .map { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) })

    override fun now(): LocalDateTime = runBlocking { sequenceFlow.poll() }
}