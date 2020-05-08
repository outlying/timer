package com.antyzero.timer.app.core.timer.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

fun <T> runBlockingUnit(block: suspend CoroutineScope.() -> T): Unit = runBlocking {
    block.invoke(this)
    Unit
}