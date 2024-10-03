/*
 * MIT License
 * Copyright 2024 Nils Jäkel
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the “Software”),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software.
 */

package dev.redtronics.mokt.network

import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.DurationUnit

/**
 * Poller for an interval.
 *
 * @since 0.0.1
 * @author Nils Jäkel
 */
public class IntervalPoller internal constructor(
    private val interval: Duration,
    private val builder: suspend IntervalPoller.() -> Unit
) {
    internal suspend fun poll(cond: suspend () -> Boolean) {
        while (cond()) {
            delay(interval.toLong(DurationUnit.SECONDS))
            builder()
        }
    }
}

/**
 * Starts an interval poller. The poller will check the condition in the given [cond] function.
 * If the condition is true the builder will be called in an [interval].
 * The poller will stop if the condition is false.
 *
 * @param interval The interval in which the builder will be called.
 * @param cond The condition to check if the poller should stop.
 * @param builder The builder to be called in an interval.
 *
 * @since 0.0.1
 * @author Nils Jäkel
 */
public suspend fun interval(
    interval: Duration,
    cond: suspend () -> Boolean = { true },
    builder: suspend IntervalPoller.() -> Unit
) {
    val poller = IntervalPoller(interval, builder)
    return poller.poll(cond)
}
