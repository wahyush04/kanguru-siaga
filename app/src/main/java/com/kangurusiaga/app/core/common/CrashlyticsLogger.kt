package com.kangurusiaga.app.core.common

import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Utility wrapper for Firebase Crashlytics logging, custom keys, and non-fatal exception reporting.
 */
object CrashlyticsLogger {

    private val crashlytics: FirebaseCrashlytics
        get() = FirebaseCrashlytics.getInstance()

    /**
     * Adds a custom log message to the next crash report.
     */
    fun log(message: String) {
        try {
            crashlytics.log(message)
        } catch (_: Exception) {}
    }

    /**
     * Records a non-fatal exception to Firebase Crashlytics.
     */
    fun recordException(throwable: Throwable) {
        try {
            crashlytics.recordException(throwable)
        } catch (_: Exception) {}
    }

    /**
     * Associates a user identifier with future crash reports.
     */
    fun setUserId(userId: String) {
        try {
            crashlytics.setUserId(userId)
        } catch (_: Exception) {}
    }

    /**
     * Sets a custom string key-value pair for diagnostic context.
     */
    fun setCustomKey(key: String, value: String) {
        try {
            crashlytics.setCustomKey(key, value)
        } catch (_: Exception) {}
    }

    /**
     * Sets a custom boolean key-value pair.
     */
    fun setCustomKey(key: String, value: Boolean) {
        try {
            crashlytics.setCustomKey(key, value)
        } catch (_: Exception) {}
    }

    /**
     * Sets a custom integer key-value pair.
     */
    fun setCustomKey(key: String, value: Int) {
        try {
            crashlytics.setCustomKey(key, value)
        } catch (_: Exception) {}
    }

    /**
     * Forces a test crash for verifying Crashlytics installation in the Firebase console.
     * WARNING: Only invoke this deliberately for testing.
     */
    fun forceTestCrash() {
        throw RuntimeException("Test Crash from Kanguru Siaga CrashlyticsLogger")
    }
}
