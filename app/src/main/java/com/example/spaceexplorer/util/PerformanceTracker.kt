package com.example.spaceexplorer.util

import android.util.Log

/**
 * Lightweight in-process metrics tracker.
 * Replace logcat output with Firebase Performance / Datadog in production.
 */
object PerformanceTracker {
    private const val TAG = "SpaceMetrics"

    private val loadTimes = mutableMapOf<String, Long>()
    private var cacheHits = 0L
    private var cacheMisses = 0L
    private var totalRequests = 0L

    fun startTrace(key: String) {
        loadTimes[key] = System.currentTimeMillis()
    }

    fun endTrace(key: String, fromCache: Boolean) {
        val start = loadTimes.remove(key) ?: return
        val elapsed = System.currentTimeMillis() - start
        totalRequests++
        if (fromCache) cacheHits++ else cacheMisses++

        Log.i(TAG, buildString {
            append("[$key] ${elapsed}ms")
            append(" | source=${if (fromCache) "CACHE" else "NETWORK"}")
            append(" | cache_hit_rate=${cacheHitRate()}%")
        })
    }

    fun recordError(key: String, error: Throwable) {
        totalRequests++
        cacheMisses++
        Log.w(TAG, "[$key] ERROR: ${error.message} | cache_hit_rate=${cacheHitRate()}%")
    }

    fun cacheHitRate(): Int {
        if (totalRequests == 0L) return 0
        return ((cacheHits.toDouble() / totalRequests) * 100).toInt()
    }

    fun summary(): String = buildString {
        appendLine("=== Space Explorer Metrics ===")
        appendLine("Total requests : $totalRequests")
        appendLine("Cache hits     : $cacheHits")
        appendLine("Cache misses   : $cacheMisses")
        appendLine("Cache hit rate : ${cacheHitRate()}%")
    }

    fun reset() {
        loadTimes.clear()
        cacheHits = 0L
        cacheMisses = 0L
        totalRequests = 0L
    }
}

// Extension for easy use in repository
inline fun <T> measureLoad(key: String, block: () -> T): T {
    PerformanceTracker.startTrace(key)
    return block()
}
