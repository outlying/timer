package com.antyzero.timer.app.core.timer

import com.antyzero.timer.app.core.timer.Timer.State
import com.antyzero.timer.app.core.timer.utils.TestTimeProvider
import com.antyzero.timer.app.core.timer.utils.createStateRecorder
import com.antyzero.timer.app.core.timer.utils.runBlockingUnit
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class TestTimer {

    @Test
    internal fun running() = runBlockingUnit {
        val timeProvider = TestTimeProvider()
        val timer = Timer(timeProvider, 4)
        val stateRecorder = timer.createStateRecorder()

        timer.start()
        timeProvider += 3

        assertThat(stateRecorder.list).run {
            hasSize(2)
            containsExactly(State.Running(4000), State.Running(1000)).inOrder()
        }
    }

    @Test
    internal fun done() = runBlocking {
        val timeProvider = TestTimeProvider()
        val timer = Timer(timeProvider, 2)
        val stateRecorder = timer.createStateRecorder()

        timer.start()
        timeProvider += 3

        assertThat(stateRecorder.list).run {
            hasSize(2)
            containsExactly(State.Running(2000), State.Done).inOrder()
        }
    }

    @Test
    internal fun unstarted() = runBlockingUnit {
        val timer = Timer(TestTimeProvider(), 2)

        // assertThat(timer.state.poll()).isInstanceOf(State.Unstarted::class.java)
    }

    @Test
    internal fun pause() = runBlockingUnit {
        val timeProvider = TestTimeProvider()
        val timer = Timer(timeProvider, 2)
        val stateRecorder = timer.createStateRecorder()

        timer.start()
        timeProvider += 1
        timer.pause()

        assertThat(stateRecorder.list).run {
            hasSize(3)
            containsExactly(
                State.Running(2000L),
                State.Running(1000L),
                State.Pause(1000)
            )
        }
    }

    @Test
    internal fun runningAfterPause() = runBlockingUnit {
        val timeProvider = TestTimeProvider()
        val timer = Timer(timeProvider, 3)
        val stateRecorder = timer.createStateRecorder()

        timer.start()
        timeProvider += 1
        timer.pause()
        timeProvider += 1
        timer.resume()
        timeProvider += 1

        assertThat(stateRecorder.list).run {
            hasSize(5)
            containsExactly(
                State.Running(3000),
                State.Running(2000),
                State.Pause(2000),
                State.Running(2000),
                State.Running(1000)
            )
        }
    }

    @Test
    internal fun runningWithStartAfterPause() = runBlockingUnit {
        val timeProvider = TestTimeProvider()
        val timer = Timer(timeProvider, 3)
        val stateRecorder = timer.createStateRecorder()

        timer.start()
        timeProvider += 1
        timer.pause()
        timeProvider += 1
        timer.start()

        assertThat(stateRecorder.list).run {
            hasSize(4)
            containsExactly(
                State.Running(3000),
                State.Running(2000),
                State.Pause(2000),
                State.Running(3000)
            )
        }
    }

    @Test
    internal fun restart() = runBlockingUnit {
        val timeProvider = TestTimeProvider()
        val timer = Timer(timeProvider, 3)
        val stateRecorder = timer.createStateRecorder()

        timer.start()
        timeProvider += 1
        timer.start()

        assertThat(stateRecorder.list).run {
            hasSize(3)
            containsExactly(
                State.Running(3000),
                State.Running(2000),
                State.Running(3000)
            )
        }
    }

    suspend operator fun TestTimeProvider.plusAssign(i: Int) {
        seconds += i
        delay(50)
    }
}