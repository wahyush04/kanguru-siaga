package com.kangurusiaga.app.data.repository

import com.kangurusiaga.app.core.common.AppDispatchers
import com.kangurusiaga.app.core.common.Dispatcher
import com.kangurusiaga.app.data.local.dao.PmkSessionDao
import com.kangurusiaga.app.data.mapper.toDomain
import com.kangurusiaga.app.data.mapper.toEntity
import com.kangurusiaga.app.domain.model.PmkSegment
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
        return pmkSessionDao.getSessionsWithSegmentsForBaby(babyId)
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override fun getSessionsForBabyInRange(
        babyId: Long,
        startEpoch: Long,
        endEpoch: Long
    ): Flow<List<PmkSession>> {
        return pmkSessionDao.getSessionsWithSegmentsForBabyInRange(babyId, startEpoch, endEpoch)
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override suspend fun getSessionById(sessionId: Long): PmkSession? {
        return withContext(ioDispatcher) {
            pmkSessionDao.getSessionWithSegmentsById(sessionId)?.toDomain()
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

    override suspend fun getActiveSession(babyId: Long): PmkSession? {
        return withContext(ioDispatcher) {
            pmkSessionDao.getActiveSessionForBaby(babyId)?.toDomain()
        }
    }

    override suspend fun saveSegment(segment: PmkSegment): Long {
        return withContext(ioDispatcher) {
            try {
                if (segment.id == 0L) {
                    pmkSessionDao.insertSegment(segment.toEntity())
                } else {
                    pmkSessionDao.updateSegment(segment.toEntity())
                    segment.id
                }
            } catch (e: Exception) {
                android.util.Log.e("PmkRepository", "Failed to save segment: ${e.message}", e)
                0L
            }
        }
    }

    override suspend fun updateSegment(segment: PmkSegment) {
        withContext(ioDispatcher) {
            try {
                pmkSessionDao.updateSegment(segment.toEntity())
            } catch (e: Exception) {
                android.util.Log.e("PmkRepository", "Failed to update segment: ${e.message}", e)
            }
        }
    }

    override fun getSegmentsInRange(
        babyId: Long,
        startEpoch: Long,
        endEpoch: Long
    ): Flow<List<PmkSegment>> {
        return pmkSessionDao.getSegmentsInRange(babyId, startEpoch, endEpoch)
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override fun getAllSegmentsInRange(
        startEpoch: Long,
        endEpoch: Long
    ): Flow<List<PmkSegment>> {
        return pmkSessionDao.getAllSegmentsInRange(startEpoch, endEpoch)
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }
}
