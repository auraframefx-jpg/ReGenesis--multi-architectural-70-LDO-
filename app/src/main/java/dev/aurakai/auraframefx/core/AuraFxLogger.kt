package dev.aurakai.auraframefx.core

import timber.log.Timber

/**
 * Genesis Logger Interface - Complete
 * Provides both short-form (i, d, w, e) and long-form (info, debug, warn, error) methods.
 */
interface AuraFxLogger {
    // Short-form methods (for compatibility with existing call sites)
    fun i(tag: String, message: String) = info(tag, message)
    fun d(tag: String, message: String) = debug(tag, message)
    fun w(tag: String, message: String, throwable: Throwable? = null) =
        warn(tag, message, throwable)

    fun e(tag: String, message: String, throwable: Throwable? = null) =
        error(tag, message, throwable)

    // Long-form methods
    fun debug(tag: String, message: String, throwable: Throwable? = null)
    fun info(tag: String, message: String, throwable: Throwable? = null)
    fun warn(tag: String, message: String, throwable: Throwable? = null)
    fun error(tag: String, message: String, throwable: Throwable? = null)
    fun security(tag: String, message: String, throwable: Throwable? = null)

    fun performance(
        tag: String,
        operation: String,
        durationMs: Long,
        metadata: Map<String, Any> = emptyMap()
    )

    fun userInteraction(
        tag: String,
        action: String,
        metadata: Map<String, Any> = emptyMap()
    )

    fun aiOperation(
        tag: String,
        operation: String,
        confidence: Float,
        metadata: Map<String, Any> = emptyMap()
    )

    fun setLoggingEnabled(enabled: Boolean)
    fun setLogLevel(level: LogLevel)
    suspend fun flush()
    fun cleanup()

    /**
     * Companion object providing static-like access to logging methods.
     * Delegates directly to Timber.
     */
    companion object {
        fun i(tag: String, message: String) = Timber.tag(tag).i(message)
        fun d(tag: String, message: String) = Timber.tag(tag).d(message)
        fun w(tag: String, message: String, throwable: Throwable? = null) =
            Timber.tag(tag).w(throwable, message)
        fun e(tag: String, message: String, throwable: Throwable? = null) =
            Timber.tag(tag).e(throwable, message)

        fun info(tag: String, message: String, throwable: Throwable? = null) =
            Timber.tag(tag).i(throwable, message)
        fun debug(tag: String, message: String, throwable: Throwable? = null) =
            Timber.tag(tag).d(throwable, message)
        fun warn(tag: String, message: String, throwable: Throwable? = null) =
            Timber.tag(tag).w(throwable, message)
        fun error(tag: String, message: String, throwable: Throwable? = null) =
            Timber.tag(tag).e(throwable, message)
    }
}

/**
 * Log levels for AuraFxLogger
 */
enum class LogLevel {
    DEBUG,
    INFO,
    WARN,
    ERROR,
    SECURITY
}
