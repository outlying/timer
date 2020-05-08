package com.antyzero.timer.app.core.timer

import com.antyzero.timer.app.core.timer.utils.TestTimeProvider
import com.antyzero.timer.app.core.timer.utils.runBlockingUnit
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.util.concurrent.CancellationException

class TestTimer {

    @Test
    internal fun running() = runBlocking {
        val testTimeProvider = TestTimeProvider()
        val timer = Timer(testTimeProvider, 4)

        timer.start()
        testTimeProvider.seconds = 3
        delay(300)

        /*
        val state = timer.state as Timer.State.Running
        assertThat(state.remainingTime).isGreaterThan(3000)

         */
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

    @Test
    internal fun runningWithStartAfterPause() = runBlocking {
        val testTimeProvider = TestTimeProvider()
        val timer = Timer(testTimeProvider, 2)

        timer.start()
        delay(20)
        timer.pause()
        delay(20)
        timer.start()

        assertThat(timer.state).isInstanceOf(Timer.State.Running::class.java)
    }

    @Test
    internal fun name(): Unit = runBlockingUnit {

        val _broadcastChannel = BroadcastChannel<Int>(Channel.BUFFERED)
        val sharedFlow = _broadcastChannel.asFlow()

        launch(Dispatchers.IO) {
            for(i in 0..5){
                if(!_broadcastChannel.isClosedForSend){
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
                for(item in openSubscription){
                    println("A: $item")
                }
            }
            delay(5000)
            openSubscription.cancel(CancellationException("I don;t know"))
        }

    }
}