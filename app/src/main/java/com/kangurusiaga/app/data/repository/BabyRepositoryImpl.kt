package com.kangurusiaga.app.data.repository

import com.kangurusiaga.app.core.common.AppDispatchers
import com.kangurusiaga.app.core.common.Dispatcher
import com.kangurusiaga.app.data.local.dao.BabyDao
import com.kangurusiaga.app.data.mapper.toDomain
import com.kangurusiaga.app.data.mapper.toEntity
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.repository.BabyRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BabyRepositoryImpl @Inject constructor(
    private val babyDao: BabyDao,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : BabyRepository {

    override fun getActiveBaby(): Flow<Baby?> {
        return babyDao.getActiveBaby()
            .map { entity -> entity?.toDomain() }
            .flowOn(ioDispatcher)
    }

    override suspend fun saveBaby(baby: Baby): Long {
        return withContext(ioDispatcher) {
            babyDao.insertBaby(baby.toEntity(isActive = true))
        }
    }

    override suspend fun updateBaby(baby: Baby) {
        withContext(ioDispatcher) {
            babyDao.updateBaby(baby.toEntity(isActive = true))
        }
    }

    override suspend fun deleteBaby(baby: Baby) {
        withContext(ioDispatcher) {
            babyDao.deleteBaby(baby.toEntity())
        }
    }
}
