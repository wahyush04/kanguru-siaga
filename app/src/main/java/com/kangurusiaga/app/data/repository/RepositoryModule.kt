package com.kangurusiaga.app.data.repository

import com.kangurusiaga.app.data.repository.emergency.EmergencyWarningRepositoryImpl
import com.kangurusiaga.app.domain.repository.BabyRepository
import com.kangurusiaga.app.domain.repository.EducationRepository
import com.kangurusiaga.app.domain.repository.EmergencyWarningRepository
import com.kangurusiaga.app.domain.repository.PmkReminderRepository
import com.kangurusiaga.app.domain.repository.PmkRepository
import com.kangurusiaga.app.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBabyRepository(
        impl: BabyRepositoryImpl
    ): BabyRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        impl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindPmkRepository(
        impl: PmkRepositoryImpl
    ): PmkRepository

    @Binds
    @Singleton
    abstract fun bindPmkReminderRepository(
        impl: PmkReminderRepositoryImpl
    ): PmkReminderRepository

    @Binds
    @Singleton
    abstract fun bindEducationRepository(
        impl: EducationRepositoryImpl
    ): EducationRepository

    @Binds
    @Singleton
    abstract fun bindEmergencyWarningRepository(
        impl: EmergencyWarningRepositoryImpl
    ): EmergencyWarningRepository

    @Binds
    @Singleton
    abstract fun bindFeedingScheduleRepository(
        impl: FeedingScheduleRepositoryImpl
    ): com.kangurusiaga.app.domain.repository.FeedingScheduleRepository

    @Binds
    @Singleton
    abstract fun bindGrowthRepository(
        impl: GrowthRepositoryImpl
    ): com.kangurusiaga.app.domain.repository.GrowthRepository

    @Binds
    @Singleton
    abstract fun bindFentonGrowthRepository(
        impl: FentonGrowthRepositoryImpl
    ): FentonGrowthRepository
}
