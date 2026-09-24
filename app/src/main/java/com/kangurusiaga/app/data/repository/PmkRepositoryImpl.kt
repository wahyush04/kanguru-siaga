package com.kangurusiaga.app.data.repository

import com.kangurusiaga.app.core.common.AppDispatchers
import com.kangurusiaga.app.core.common.Dispatcher
import com.kangurusiaga.app.data.local.dao.PmkSessionDao
import com.kangurusiaga.app.data.mapper.toDomain
import com.kangurusiaga.app.data.mapper.toEntity
import com.kangurusiaga.app.domain.model.PmkSession
import com.kangurusiaga.app.domain.repository.PmkRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PmkRepositoryImpl @Inject constructor(
    private val pmkSessionDao: PmkSessionDao,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : PmkRepository {

    override fun getSessionsForBaby(babyId: Long): Flow<List<PmkSession>> {
        return pmkSessionDao.getSessionsForBaby(babyId)
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override fun getSessionsForBabyInRange(
        babyId: Long,
        startEpoch: Long,
        endEpoch: Long
    ): Flow<List<PmkSession>> {
        return pmkSessionDao.getSessionsForBabyInRange(babyId, startEpoch, endEpoch)
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override suspend fun getSessionById(sessionId: Long): PmkSession? {
        return withContext(ioDispatcher) {
            pmkSessionDao.getSessionById(sessionId)?.toDomain()
        }
    }

    override suspend fun saveSession(session: PmkSession): Long {
        return withContext(ioDispatcher) {
            if (session.id == 0L) {
                pmkSessionDao.insertSession(session.toEntity())
            } else {
                pmkSessionDao.updateSession(session.toEntity())
                session.id
            }
        }
    }

    override suspend fun deleteSession(sessionId: Long) {
        withContext(ioDispatcher) {
            pmkSessionDao.deleteSessionById(sessionId)
        }
    }

    override fun getSessionCountInRange(
        babyId: Long,
        startEpoch: Long,
        endEpoch: Long
    ): Flow<Int> {
        return pmkSessionDao.getSessionCountInRange(babyId, startEpoch, endEpoch)
            .flowOn(ioDispatcher)
    }

    override fun getTotalMinutesInRange(
        babyId: Long,
        startEpoch: Long,
        endEpoch: Long
    ): Flow<Int> {
        return pmkSessionDao.getTotalMinutesInRange(babyId, startEpoch, endEpoch)
            .map { it ?: 0 }
            .flowOn(ioDispatcher)
    }
}
