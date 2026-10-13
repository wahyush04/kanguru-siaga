package com.kangurusiaga.app

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KanguruSiagaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initCrashlytics()
    }

    private fun initCrashlytics() {
        val crashlytics = FirebaseCrashlytics.getInstance()
        // Ensure Crashlytics collection is enabled
        crashlytics.setCrashlyticsCollectionEnabled(true)
        // Set baseline environment and build diagnostic tags
        crashlytics.setCustomKey("environment", BuildConfig.ENVIRONMENT)
        crashlytics.setCustomKey("version_name", BuildConfig.VERSION_NAME)
        crashlytics.setCustomKey("version_code", BuildConfig.VERSION_CODE)
        crashlytics.setCustomKey("is_preproduction", BuildConfig.IS_PREPRODUCTION)
    }
}
