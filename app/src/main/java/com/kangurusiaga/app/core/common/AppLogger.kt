package com.kangurusiaga.app.core.common

import android.util.Log
import com.kangurusiaga.app.BuildConfig

object AppLogger {
    fun d(tag: String, message: String) {
        if (!BuildConfig.DEBUG) return
        try {
            Log.d(tag, message)
        } catch (_: Throwable) {}
    }

    fun i(tag: String, message: String) {
        if (!BuildConfig.DEBUG) return
        try {
            Log.i(tag, message)
        } catch (_: Throwable) {}
    }

    fun w(tag: String, message: String) {
        try {
            Log.w(tag, message)
        } catch (_: Throwable) {}
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        try {
            if (throwable != null) {
                Log.e(tag, message, throwable)
            } else {
                Log.e(tag, message)
            }
        } catch (_: Throwable) {}
    }
}
