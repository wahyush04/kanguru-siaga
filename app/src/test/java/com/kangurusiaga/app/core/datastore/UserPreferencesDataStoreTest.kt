package com.kangurusiaga.app.core.datastore

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@OptIn(ExperimentalCoroutinesApi::class)
class UserPreferencesDataStoreTest {

    @get:Rule
    val tmpFolder: TemporaryFolder = TemporaryFolder()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private fun createDataStore(): UserPreferencesDataStore {
        val testDataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { tmpFolder.newFile("test_user_preferences.preferences_pb") }
        )
        return UserPreferencesDataStore(testDataStore)
    }

    @Test
    fun freshInstall_isOnboardingCompleted_defaultsToFalse() = runTest {
        val dataStore = createDataStore()

        dataStore.isOnboardingCompleted.test {
            assertThat(awaitItem()).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun setOnboardingCompleted_updatesFlagToTrue() = runTest {
        val dataStore = createDataStore()

        dataStore.setOnboardingCompleted(true)

        dataStore.isOnboardingCompleted.test {
            assertThat(awaitItem()).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
