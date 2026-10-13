package com.kangurusiaga.app.core.firebase

import com.google.firebase.firestore.FirebaseFirestore
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
