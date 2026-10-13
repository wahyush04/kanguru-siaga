package com.kangurusiaga.app.presentation.pmk.video

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import com.kangurusiaga.app.R

/**
 * Structured clinical key point for PMK educational materials.
 * Matches Stitch "Poin Penting Klinis" card with numbered badges and title/description pairs.
 */
data class ClinicalPoint(
    val number: Int,
    val title: String,
    val description: String
)

/**
 * Data model for PMK educational videos based on Google Stitch design
 * "Kanguru Siaga - Detail Video PMK (Landscape Player)" and "Kanguru Siaga - Video Edukasi PMK".
 */
data class PmkVideoItem(
    val id: Int,
    val title: String,
    val category: String = "Perawatan Metode Kanguru",
    val duration: String,
    val durationSeconds: Int,
    @DrawableRes val thumbnailRes: Int,
    @RawRes val videoRes: Int = R.raw.video_pengertian_pmk_1,
    val videoUrl: String? = null,
    val description: String,
    val clinicalPoints: List<ClinicalPoint> = emptyList(),
    val calloutTip: String? = "Ayah juga dapat melakukan PMK secara bergantian untuk membantu istirahat Bunda!",
    val nextLessonId: Int? = null,
    val keyPoints: List<String> = clinicalPoints.map { "${it.title}: ${it.description}" }
) {
    val badgeText: String get() = "Video Edukasi #$id"
}

object PmkVideoDataSource {
    val videos: List<PmkVideoItem> = listOf(
        PmkVideoItem(
            id = 1,
            title = "1. Pengertian PMK",
            category = "Perawatan Metode Kanguru",
            duration = "00:27",
            durationSeconds = 27,
            thumbnailRes = R.drawable.thumb_pmk_video_2,
            videoRes = R.raw.video_pengertian_pmk_1,
            description = "Penjelasan dasar mengenai apa itu Perawatan Metode Kanguru (PMK / Kangaroo Mother Care), prinsip kontak kulit-ke-kulit (skin-to-skin) antara dada bayi dan dada orang tua, serta fungsinya sebagai inkubator alami yang aman bagi bayi berat lahir rendah (BBLR).",
            clinicalPoints = listOf(
                ClinicalPoint(
                    number = 1,
                    title = "Definisi Kontak Kulit (Skin-to-Skin)",
                    description = "Kontak langsung dada bayi telanjang (hanya mengenakan popok, topi, dan kaus kaki) menempel lekat pada dada telanjang ibu atau ayah secara vertikal dan terfiksasi aman."
                ),
                ClinicalPoint(
                    number = 2,
                    title = "Inkubator Alami (Thermal Synchrony)",
                    description = "Suhu tubuh dada ibu beradaptasi secara otomatis mengikuti kebutuhan termal bayi, menjaga kehangatan stabil (36,5°C–37,5°C) dan melindungi dari risiko hipotermia."
                ),
                ClinicalPoint(
                    number = 3,
                    title = "Stabilisasi Tanda Vital Neonatus",
                    description = "Membantu menstabilkan denyut jantung, irama pernapasan, meningkatkan saturasi oksigen, serta menekan risiko henti napas sesaat (apnea) pada bayi prematur."
                ),
                ClinicalPoint(
                    number = 4,
                    title = "Stimulasi Hormon Kasih Sayang & ASI",
                    description = "Dekapan hangat memicu produksi hormon oksitosin dan prolaktin pada Bunda, melancarkan produksi ASI, serta mempercepat pematangan refleks hisap si kecil."
                ),
                ClinicalPoint(
                    number = 5,
                    title = "Durasi Efektif Sesi PMK",
                    description = "Disarankan minimal 60 menit per sesi agar siklus tidur lelap (deep sleep cycle) bayi tidak terputus dan pemulihan tubuh berlangsung secara optimal."
                )
            ),
            calloutTip = "Ayah dan anggota keluarga lainnya juga sangat dianjurkan melakukan PMK secara bergantian untuk membantu istirahat Bunda sekaligus mempererat ikatan kasih sayang dengan si kecil!",
            nextLessonId = 2
        ),
        PmkVideoItem(
            id = 2,
            title = "2. Manfaat PMK untuk Bayi Berat Lahir Rendah",
            category = "Perawatan Metode Kanguru",
            duration = "01:05",
            durationSeconds = 65,
            thumbnailRes = R.drawable.thumb_pmk_video_2,
            videoRes = R.raw.video_manfaat_pmk_2,
            description = "Memahami berbagai manfaat fisiologis, neurologis, dan emosional PMK untuk bayi prematur dan BBLR, mulai dari stabilitas suhu tubuh, percepatan kenaikan berat badan, hingga pematangan fungsi organ vital.",
            clinicalPoints = listOf(
                ClinicalPoint(
                    number = 1,
                    title = "Termoregulasi & Efisiensi Energi",
                    description = "Mencegah stres dingin (cold stress) sehingga kalori dan nutrisi dari ASI tidak terbuang sia-sia untuk memanaskan tubuh, melainkan disalurkan untuk pertumbuhan sel."
                ),
                ClinicalPoint(
                    number = 2,
                    title = "Kenaikan Berat Badan Lebih Cepat",
                    description = "Bayi yang mendapatkan PMK secara rutin mengalami peningkatan berat badan harian yang lebih konsisten dan pesat dibandingkan metode perawatan konvensional."
                ),
                ClinicalPoint(
                    number = 3,
                    title = "Kualitas Tidur Tenang (Quiet Sleep)",
                    description = "Meningkatkan persentase tidur lelap yang sangat dibutuhkan bagi pematangan sistem saraf pusat, perkembangan otak, serta regenerasi sel tubuh bayi."
                ),
                ClinicalPoint(
                    number = 4,
                    title = "Proteksi Alami dari Infeksi",
                    description = "Kolonisasi bakteri baik dari kulit orang tua memperkuat pertahanan alami tubuh bayi dan menurunkan risiko infeksi nosokomial berat selama perawatan."
                ),
                ClinicalPoint(
                    number = 5,
                    title = "Memperpendek Masa Rawat Inap",
                    description = "Stabilitas klinis yang lebih cepat tercapai membantu mempercepat bayi pulang ke rumah bersama keluarga dan menurunkan tingkat stres orang tua."
                )
            ),
            calloutTip = "Riset WHO membuktikan bahwa penerapan Perawatan Metode Kanguru secara rutin dapat menurunkan angka kematian neonatal pada bayi BBLR hingga lebih dari 30%.",
            nextLessonId = 3
        ),
        PmkVideoItem(
            id = 3,
            title = "3. Persiapan sebelum PMK",
            category = "Perawatan Metode Kanguru",
            duration = "01:29",
            durationSeconds = 89,
            thumbnailRes = R.drawable.thumb_pmk_video_3,
            videoRes = R.raw.video_persiapan_pmk_3,
            description = "Panduan persiapan menyeluruh sebelum memulai sesi PMK demi menjaga higienitas, kenyamanan, dan keselamatan bayi maupun orang tua, mulai dari kebersihan diri, busana, hingga lingkungan sekitar.",
            clinicalPoints = listOf(
                ClinicalPoint(
                    number = 1,
                    title = "Kebersihan Tangan & Tubuh Pengasuh",
                    description = "Cuci tangan 6 langkah dengan sabun di bawah air mengalir selama 40–60 detik. Pastikan kuku dipotong pendek, lepas perhiasan/jam tangan, dan hindari parfum beraroma menyengat."
                ),
                ClinicalPoint(
                    number = 2,
                    title = "Pakaian Pengasuh yang Tepat",
                    description = "Kenakan baju atau kemeja berkancing depan yang longgar dan bersih, atau kain gendong/baju kanguru khusus agar bayi dapat didekap dengan aman dan nyaman."
                ),
                ClinicalPoint(
                    number = 3,
                    title = "Pakaian Minimal untuk Bayi",
                    description = "Bayi hanya mengenakan popok bersih, topi katun hangat, dan kaus kaki lembut untuk memaksimalkan area kontak kulit dada tanpa risiko pelepasan panas di kepala."
                ),
                ClinicalPoint(
                    number = 4,
                    title = "Suhu & Suasana Ruangan Nyaman",
                    description = "Pastikan suhu ruangan hangat (24°C–26°C), terhindar dari hembusan angin langsung jendela/AC/kipas angin, serta bebas dari kebisingan dan asap rokok."
                ),
                ClinicalPoint(
                    number = 5,
                    title = "Kesiapan Fisik & Posisi Pengasuh",
                    description = "Kosongkan kandung kemih, siapkan air minum di dekat jangkauan, serta gunakan kursi dengan sandaran bantal penyangga punggung agar sesi tidak terputus di tengah jalan."
                )
            ),
            calloutTip = "Sediakan bantal penyangga punggung dan lengan sebelum memulai sesi agar Bunda atau Ayah tidak mudah lelah selama mendekap si kecil minimal 60 menit.",
            nextLessonId = 4
        ),
        PmkVideoItem(
            id = 4,
            title = "4. Cara Perawatan Bayi Berat Lahir Rendah",
            category = "Perawatan Bayi BBLR",
            duration = "00:39",
            durationSeconds = 39,
            thumbnailRes = R.drawable.thumb_pmk_video_4,
            videoRes = R.raw.video_perawatan_bblr_4,
            description = "Panduan praktis tata cara perawatan harian bagi Bayi Berat Lahir Rendah (BBLR) di rumah, meliputi pemenuhan nutrisi ASI teratur, pemeliharaan kehangatan tubuh, kebersihan tali pusat, serta pemantauan tumbuh kembang harian.",
            clinicalPoints = listOf(
                ClinicalPoint(
                    number = 1,
                    title = "Menjaga Kehangatan Tubuh Sepanjang Hari",
                    description = "Pertahankan suhu normal (36,5°C–37,5°C) melalui kombinasi PMK rutin, penggunaan topi katun, dan selimut hangat untuk mencegah stres dingin (hipotermia)."
                ),
                ClinicalPoint(
                    number = 2,
                    title = "Jadwal Pemberian ASI Teratur Tiap 2–3 Jam",
                    description = "Disiplin memberikan ASI 8–12 kali sehari tanpa menunggu bayi menangis. Bangunkan bayi tepat waktu untuk menyusu guna mencegah penurunan kadar gula darah (hipoglikemia)."
                ),
                ClinicalPoint(
                    number = 3,
                    title = "Perawatan Tali Pusat Bersih & Kering",
                    description = "Biarkan tali pusat terbuka dan kering alami tanpa ramuan atau alkohol. Lipat tepi atas popok di bawah tali pusat agar tidak terkena urin dan mencegah infeksi (omfalitis)."
                ),
                ClinicalPoint(
                    number = 4,
                    title = "Teknik Mandi Seka dengan Air Hangat",
                    description = "Jika berat badan bayi masih di bawah 2.000 gram, hindari mandi berendam di bak. Cukup seka tubuh lembut dengan waslap air hangat (~37°C) di dalam ruangan tertutup."
                ),
                ClinicalPoint(
                    number = 5,
                    title = "Posisi Tidur Aman & Pantau Berat Badan",
                    description = "Tidurkan bayi selalu telentang di kasur padat tanpa bantal empuk berlebih (mencegah SIDS), dan timbang berat badan secara berkala dengan target kenaikan 15–30 gram per hari."
                )
            ),
            calloutTip = "Lambung bayi BBLR berukuran kecil dan cepat lelah saat menyusu. Berikan ASI sedikit demi sedikit namun sering, lalu sendawakan bayi dengan posisi kepala sedikit terangkat (30°) untuk mencegah gumoh atau tersedak.",
            nextLessonId = 5
        ),
        PmkVideoItem(
            id = 5,
            title = "5. Tanda Kegawatan Bayi Berat Lahir Rendah",
            category = "Tanda Kegawatan BBLR",
            duration = "00:54",
            durationSeconds = 54,
            thumbnailRes = R.drawable.thumb_pmk_video_5,
            videoRes = R.raw.video_tanda_kegawatan_bblr_5,
            description = "Edukasi kritis mengenai deteksi dini tanda bahaya dan kegawatan klinis (red flags) pada Bayi Berat Lahir Rendah (BBLR) yang memerlukan tindakan darurat segera ke fasilitas pelayanan kesehatan atau rumah sakit.",
            clinicalPoints = listOf(
                ClinicalPoint(
                    number = 1,
                    title = "Gangguan Pernapasan Akut",
                    description = "Napas sangat cepat (>60 kali/menit), napas berhenti (apnea) lebih dari 20 detik, tampak tarikan dinding dada ke dalam (retraksi), hidung kembang kempis, atau napas berbunyi merintih (grunting)."
                ),
                ClinicalPoint(
                    number = 2,
                    title = "Perubahan Warna Kulit & Bibir",
                    description = "Bibir atau lidah membiru (sianosis sentral), kulit sangat pucat keabu-abuan, atau tampak kuning pekat yang muncul dalam 24 jam pertama kehidupan atau menjalar hingga ke telapak tangan dan kaki."
                ),
                ClinicalPoint(
                    number = 3,
                    title = "Hipotermia Berat atau Demam",
                    description = "Suhu ketiak tetap di bawah 36,0°C meski telah dihangatkan dengan metode kanguru dan selimut, atau suhu tubuh meningkat tinggi di atas 37,5°C disertai tubuh menggigil."
                ),
                ClinicalPoint(
                    number = 4,
                    title = "Penurunan Kesadaran & Tubuh Lemas",
                    description = "Bayi tidur terus-menerus dan sangat sulit dibangunkan meski sudah waktunya minum, tidak ada respons saat telapak kaki ditepuk lembut, atau tonus otot terkulai lunglai (floppy)."
                ),
                ClinicalPoint(
                    number = 5,
                    title = "Refleks Hisap Hilang, Muntah Hijau & Kejang",
                    description = "Bayi menolak minum sama sekali, muntah berwarna hijau kekuningan atau menyembur deras, perut membuncit tegang, atau timbul kedutan kaku dan gerakan kejang berulang."
                )
            ),
            calloutTip = "TANGGAP DARURAT: Jika Bunda atau Ayah menemukan SALAH SATU tanda kegawatan di atas, JANGAN MENUNDA! Segera hubungi ambulans 119 atau bawa si kecil ke IGD / Puskesmas terdekat!",
            nextLessonId = null
        )
    )

    fun getVideoById(id: Int): PmkVideoItem {
        return videos.find { it.id == id } ?: videos.first()
    }
}
