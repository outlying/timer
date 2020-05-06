package com.antyzero.timer.app.core

import org.threeten.bp.LocalDateTime

interface TimeProvider {

    fun now(): LocalDateTime

    object Default : TimeProvider {

        override fun now(): LocalDateTime = LocalDateTime.now()
    }
}