package com.kangurusiaga.app.data.repository

import com.kangurusiaga.app.data.local.dao.PrematureScreeningDao
import com.kangurusiaga.app.data.local.entity.PrematureScreeningEntity
import com.kangurusiaga.app.domain.model.PrematureScreening
import com.kangurusiaga.app.domain.model.ScreeningType
import com.kangurusiaga.app.domain.repository.PrematureScreeningRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrematureScreeningRepositoryImpl @Inject constructor(
    private val dao: PrematureScreeningDao
) : PrematureScreeningRepository {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))

    override fun getScreeningsForBaby(babyId: Long): Flow<List<PrematureScreening>> {
        return dao.getScreeningsForBaby(babyId).map { entities ->
            entities.map { entity ->
                val date1 = if (entity.targetDateEpoch > 0) dateFormat.format(Date(entity.targetDateEpoch)) else ""
                val date2 = entity.targetDateEpoch2?.let { if (it > 0) dateFormat.format(Date(it)) else "" } ?: ""
                val realDate = entity.realizationDateEpoch?.let { if (it > 0) dateFormat.format(Date(it)) else null }
                entity.toDomain(
                    dateFormatted = date1,
                    date2Formatted = date2,
                    realizationFormatted = realDate
                )
            }
        }
    }

    override suspend fun ensureDefaultScreenings(
        babyId: Long,
        birthDateEpoch: Long,
        gestationalAgeWeeks: Int
    ) {
        val existing = dao.getScreeningsForBaby(babyId).first()
        if (existing.isNotEmpty()) return

        val baseBirth = if (birthDateEpoch > 0) birthDateEpoch else System.currentTimeMillis()
        val ga = if (gestationalAgeWeeks in 22..42) gestationalAgeWeeks else 32
        val weeksToTerm = (40 - ga).coerceAtLeast(0)
        val msPerDay = 24 * 3600 * 1000L

        val usgDate = baseBirth + (14L * msPerDay)
        val ropDate = baseBirth + (29L * msPerDay)
        val hearing1Date = baseBirth + (61L * msPerDay)
        val hearing2Date = baseBirth + (92L * msPerDay)
        val thyroidDate = baseBirth + (14L * msPerDay)

        val defaultList = listOf(
            PrematureScreeningEntity(
                babyId = babyId,
                screeningType = ScreeningType.USG_HEAD.name,
                title = "USG Kepala",
                categoryBadge = "Neurosonografi",
                targetTimingLabel = "Usia 2 Minggu Kronologis",
                targetDateEpoch = usgDate,
                targetDateEpoch2 = null,
                indication = "Terutama bayi lahir < 34 minggu atau sesuai indikasi klinis (sakit berat, trauma lahir, dll). Minimal 1x selama perawatan, atau sebelum rawat jalan, atau sesuai evaluasi dokter spesialis anak.",
                clinicalGoal = "Tujuan: Deteksi dini perdarahan intraventrikular (IVH) & leukomalasia.",
                clinicOrLab = "Poli: Anak / Neurogenetika",
                isAlarmActive = true,
                isAlarm2Active = false,
                isCompleted = false,
                realizationDateEpoch = null,
                findings = null,
                conclusion = null,
                attachmentName = null,
                attachmentCount = 0
            ),
            PrematureScreeningEntity(
                babyId = babyId,
                screeningType = ScreeningType.ROP_EYE.name,
                title = "Retinopati Prematuritas (ROP)",
                categoryBadge = "Oftalmologi Pediatrik",
                targetTimingLabel = "Usia Koreksi 0 Bulan",
                targetDateEpoch = ropDate,
                targetDateEpoch2 = null,
                indication = "Terutama bayi lahir < 34 minggu atau berat lahir < 1.500 gram, atau memiliki riwayat sakit berat (sepsis/asfiksia). Minimal 1x sebelum rawat jalan, selambat-lambatnya usia koreksi 0 bulan.",
                clinicalGoal = "Tujuan: Deteksi dini neovaskularisasi retina & mencegah kebutaan.",
                clinicOrLab = "Poli: Mata Anak (Pediatrik)",
                isAlarmActive = true,
                isAlarm2Active = false,
                isCompleted = false,
                realizationDateEpoch = null,
                findings = null,
                conclusion = null,
                attachmentName = null,
                attachmentCount = 0
            ),
            PrematureScreeningEntity(
                babyId = babyId,
                screeningType = ScreeningType.HEARING_OAE.name,
                title = "Gangguan Pendengaran (OAE & BERA)",
                categoryBadge = "Audiologi / THT-KL",
                targetTimingLabel = "2 - 3 Bulan Kronologis",
                targetDateEpoch = hearing1Date,
                targetDateEpoch2 = hearing2Date,
                indication = "Minimal sebelum bayi dipulangkan, selambat-lambatnya sebelum usia kronologis 3 bulan.",
                clinicalGoal = "Tujuan: Golden period stimulasi bicara & perkembangan auditori.",
                clinicOrLab = "Poli: THT-KL Pediatrik",
                isAlarmActive = true,
                isAlarm2Active = true,
                isCompleted = false,
                realizationDateEpoch = null,
                notes = null
            ),
            PrematureScreeningEntity(
                babyId = babyId,
                screeningType = ScreeningType.THYROID_FUNCTION.name,
                title = "Fungsi Tiroid (SHK & FT4 / TSH)",
                categoryBadge = "Laboratorium Darah",
                targetTimingLabel = "FT4 & TSH Usia PMA 40 Minggu",
                targetDateEpoch = thyroidDate,
                targetDateEpoch2 = null,
                indication = "Penapisan awal usia 48–72 jam. Untuk bayi gestasi < 35 minggu, dianjurkan diulang pada usia 37–42 minggu (PMA 40 mgg) atau sebelum pulang rawat jalan.",
                clinicalGoal = "Tujuan: Cegah hipotiroid kongenital tertunda & gangguan neurokognitif.",
                clinicOrLab = "Pemeriksaan: Serum FT4 + TSH",
                isAlarmActive = true,
                isAlarm2Active = false,
                isCompleted = false,
                realizationDateEpoch = null,
                findings = null,
                conclusion = null,
                attachmentName = null,
                attachmentCount = 0
            )
        )

        dao.insertScreenings(defaultList)
    }

    override suspend fun updateScreening(screening: PrematureScreening) {
        dao.updateScreening(PrematureScreeningEntity.fromDomain(screening))
    }

    override suspend fun insertScreening(screening: PrematureScreening): Long {
        return dao.insertScreening(PrematureScreeningEntity.fromDomain(screening))
    }

    override suspend fun deleteScreening(screeningId: Long) {
        dao.deleteScreeningById(screeningId)
    }
}
