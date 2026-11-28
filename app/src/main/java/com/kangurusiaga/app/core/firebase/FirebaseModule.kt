package com.kangurusiaga.app.core.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.kangurusiaga.app.BuildConfig
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.common.ConnectivityNetworkMonitor
import com.kangurusiaga.app.core.common.NetworkMonitor
import com.kangurusiaga.app.data.remote.firestore.BabyFirestoreSyncManager
import com.kangurusiaga.app.data.remote.firestore.BabyFirestoreSyncManagerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val minIntervalSeconds = if (BuildConfig.DEBUG || BuildConfig.IS_PREPRODUCTION) 60L else 3600L
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = minIntervalSeconds
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        return remoteConfig
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class FirebaseBindingModule {

    @Binds
    @Singleton
    abstract fun bindNetworkMonitor(
        impl: ConnectivityNetworkMonitor
    ): NetworkMonitor

    @Binds
    @Singleton
    abstract fun bindBabyFirestoreSyncManager(
        impl: BabyFirestoreSyncManagerImpl
    ): BabyFirestoreSyncManager
}
