package com.antyzero.timer.app.core.timer

import com.antyzero.timer.app.core.timer.Timer.State
import com.antyzero.timer.app.core.timer.utils.TestTimeProvider
import com.antyzero.timer.app.core.timer.utils.createStateRecorder
import com.antyzero.timer.app.core.timer.utils.runBlockingUnit
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.util.concurrent.CancellationException

class TestTimer {

    @Test
    internal fun running() = runBlockingUnit {
        val timeProvider = TestTimeProvider()
        val timer = Timer(timeProvider, 4)
        val stateRecorder = timer.createStateRecorder()

        timer.start()
        timeProvider += 3
        delay(20)

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
        delay(20)

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
        delay(20)
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
        delay(20)
        timer.pause()
        timeProvider += 1
        delay(20)
        timer.resume()
        timeProvider += 1
        delay(20)

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
        delay(20)
        timer.pause()
        timeProvider += 1
        delay(20)
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
        delay(20)
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

    @Test
    internal fun name(): Unit = runBlockingUnit {

        val _broadcastChannel = BroadcastChannel<Int>(Channel.BUFFERED)
        val sharedFlow = _broadcastChannel.asFlow()

        launch(Dispatchers.IO) {
            for (i in 0..5) {
                if (!_broadcastChannel.isClosedForSend) {
                    _broadcastChannel.offer(i)
                }
                delay(1000L)
            }
        }

        launch {
            delay(3000)
            _broadcastChannel.cancel()
        }

        launch {
            val openSubscription = _broadcastChannel.openSubscription()
            launch {
                for (item in openSubscription) {
                    println("A: $item")
                }
            }
            delay(5000)
            openSubscription.cancel(CancellationException("I don;t know"))
        }
    }
}