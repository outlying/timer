package com.antyzero.timer.app.core.timer

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class TestTimer {

    @Test
    internal fun running() = runBlocking {
        val testTimeProvider = TestTimeProvider()
        val timer = Timer(testTimeProvider, 4)

        timer.start()

        assert(timer.state is Timer.State.Running)
    }

    @Test
    internal fun done() = runBlocking {
        val testTimeProvider = TestTimeProvider()
        val timer = Timer(testTimeProvider, 2)

        timer.start()
        testTimeProvider.seconds = 3
        delay(20)

        assertThat(timer.state).isInstanceOf(Timer.State.Done::class.java)
    }

    @Test
    internal fun unstarted() = runBlocking {
        val timer = Timer(TestTimeProvider(), 2)

        assertThat(timer.state).isInstanceOf(Timer.State.Unstarted::class.java)
    }

    @Test
    internal fun pause() = runBlocking {
        val testTimeProvider = TestTimeProvider()
        val timer = Timer(testTimeProvider, 2)

        timer.start()
        delay(20)
        timer.pause()

        assertThat(timer.state).isInstanceOf(Timer.State.Pause::class.java)
    }

    @Test
    internal fun runningAfterPause() = runBlocking {
        val testTimeProvider = TestTimeProvider()
        val timer = Timer(testTimeProvider, 2)

        timer.start()
        delay(20)
        timer.pause()
        delay(20)
        timer.resume()

        assertThat(timer.state).isInstanceOf(Timer.State.Running::class.java)
    }
}