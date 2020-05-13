package com.antyzero.timer.app.core.timer.utils

import com.antyzero.timer.app.core.TimeProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.threeten.bp.LocalDateTime

class TestTimeProvider : TimeProvider {

    override val currentTime: StateFlow<LocalDateTime>
        get() = mutableCurrentTime

    private val mutableCurrentTime = MutableStateFlow<LocalDateTime>(
        LocalDateTime.of(0, 1, 1, 0, 0)
    )

    operator fun plusAssign(seconds: Int) {
        mutableCurrentTime.value = mutableCurrentTime.value.plusSeconds(seconds.toLong())
    }
}