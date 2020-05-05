package com.antyzero.timer.app.core.timer

import com.antyzero.timer.app.core.TimeProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class TestTimer {

    @Test
    internal fun test() = runBlocking {


        Timer(TimeProvider.Default, 4).start()

        delay(6000)
    }
}