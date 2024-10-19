/*
 * MIT License
 * Copyright 2024 Nils Jäkel & David Ernst
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the “Software”),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software.
 */

@file:Suppress("MemberVisibilityCanBePrivate")

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
public class IntervalPoller<T> internal constructor(
    private val interval: Duration,
    private val builder: suspend IntervalPoller<T>.() -> T
) {
    /**
     * Whether the poller has been cancelled.
     *
     * @since 0.0.1
     * @author Nils Jäkel
     * */
    public var isCancelled: Boolean = false
        private set

    /**
     * Cancels the poller.
     *
     * @since 0.0.1
     * @author Nils Jäkel
     * */
    public fun cancel() {
        isCancelled = true
    }

    /**
     * Polls the builder in an interval.
     *
     * @param cond The condition to check if the poller should stop.
     * @return The result of the builder or null if the condition is false.
     *
     * @since 0.0.1
     * @author Nils Jäkel
     * */
    internal suspend fun poll(cond: suspend () -> Boolean): T? {
        while (cond() && !isCancelled) {
            delay(interval.toLong(DurationUnit.SECONDS))
            return builder() ?: continue
        }
        return null
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
public suspend fun <T> interval(
    interval: Duration,
    cond: suspend () -> Boolean = { true },
    builder: suspend IntervalPoller<T>.() -> T
): T? {
    val poller = IntervalPoller(interval, builder)
    return poller.poll(cond)
}
