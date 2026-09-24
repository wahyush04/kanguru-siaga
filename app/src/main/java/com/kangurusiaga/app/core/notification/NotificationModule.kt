package com.kangurusiaga.app.core.notification

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Provides
    @Singleton
    fun provideKanguruNotificationManager(
        @ApplicationContext context: Context
    ): KanguruNotificationManager {
        return KanguruNotificationManager(context)
    }
}
