package com.antyzero.timer.app.core

import org.threeten.bp.LocalTime

interface TimeProvider {

    fun now(): LocalTime

    object Default : TimeProvider {

        override fun now(): LocalTime = LocalTime.now()
    }
}