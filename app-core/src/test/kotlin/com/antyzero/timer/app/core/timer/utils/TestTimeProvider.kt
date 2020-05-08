package com.antyzero.timer.app.core.timer.utils

import com.antyzero.timer.app.core.TimeProvider
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset

class TestTimeProvider : TimeProvider {

    var seconds: Long = 0
        set(value) {
            localDateTime = LocalDateTime.ofEpochSecond(value, 0, ZoneOffset.UTC)
            field = value
        }

    private var localDateTime = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC)

    override fun now(): LocalDateTime = localDateTime

    operator fun plusAssign(i: Int) {
        seconds += i
    }
}