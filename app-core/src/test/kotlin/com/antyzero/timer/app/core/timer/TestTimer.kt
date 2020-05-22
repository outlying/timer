package com.antyzero.timer.app.core.timer

import com.antyzero.timer.app.core.timer.StandardTimer.State
import com.antyzero.timer.app.core.timer.utils.TestTimeProvider
import com.antyzero.timer.app.core.timer.utils.createStateRecorder
import com.antyzero.timer.app.core.timer.utils.runBlockingUnit
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class TestTimer {

    @Test
    internal fun running() = runBlockingUnit {
        val timeProvider = TestTimeProvider()
        val timer = StandardTimer(timeProvider, 4)
        val stateRecorder = timer.createStateRecorder()

        timer.start()
        timeProvider += 3

        assertThat(stateRecorder.list)
            .containsExactly(
                State.Idle,
                State.Running(4000),
                State.Running(1000)
            ).inOrder()
    }

    @Test
    internal fun done() = runBlocking {
        val timeProvider = TestTimeProvider()
        val timer = StandardTimer(timeProvider, 2)
        val stateRecorder = timer.createStateRecorder()

        timer.start()
        timeProvider += 3

        assertThat(stateRecorder.list).run {
            containsExactly(
                State.Idle,
                State.Running(2000),
                State.Done
            ).inOrder()
        }
    }

    @Test
    internal fun unstarted() = runBlockingUnit {
        val timer = StandardTimer(TestTimeProvider(), 2)

        assertThat(timer.state.value).isInstanceOf(State.Idle::class.java)
    }

    @Test
    internal fun pause() = runBlockingUnit {
        val timeProvider = TestTimeProvider()
        val timer = StandardTimer(timeProvider, 2)
        val stateRecorder = timer.createStateRecorder()

        timer.start()
        timeProvider += 1
        timer.pause()

        assertThat(stateRecorder.list).run {
            containsExactly(
                State.Idle,
                State.Running(2000L),
                State.Running(1000L),
                State.Pause(1000)
            )
        }
    }

    @Test
    internal fun runningAfterPause() = runBlockingUnit {
        val timeProvider = TestTimeProvider()
        val timer = StandardTimer(timeProvider, 3)
        val stateRecorder = timer.createStateRecorder()

        timer.start()
        timeProvider += 1
        timer.pause()
        timeProvider += 1
        timer.resume()
        timeProvider += 1

        assertThat(stateRecorder.list).run {
            containsExactly(
                State.Idle,
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
        val timer = StandardTimer(timeProvider, 3)
        val stateRecorder = timer.createStateRecorder()

        timer.start()
        timeProvider += 1
        timer.pause()
        timeProvider += 1
        timer.start()

        assertThat(stateRecorder.list).run {
            containsExactly(
                State.Idle,
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
        val timer = StandardTimer(timeProvider, 3)
        val stateRecorder = timer.createStateRecorder()

        timer.start()
        timeProvider += 1
        timer.start()

        assertThat(stateRecorder.list).run {
            containsExactly(
                State.Idle,
                State.Running(3000),
                State.Running(2000),
                State.Running(3000)
            )
        }
    }
}