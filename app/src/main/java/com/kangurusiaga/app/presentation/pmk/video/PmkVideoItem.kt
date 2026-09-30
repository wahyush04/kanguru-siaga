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
    @RawRes val videoRes: Int = R.raw.video_module_pmk_1,
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
            duration = "02:15",
            durationSeconds = 135,
            thumbnailRes = R.drawable.thumb_pmk_video_2,
            videoRes = R.raw.video_module_pmk_1,
            description = "Penjelasan dasar mengenai apa itu PMK (Kangaroo Mother Care), tujuan kontak kulit-ke-kulit (skin-to-skin contact), dan prinsip utamanya sebagai inkubator alami bagi bayi BBLR di rumah.",
            clinicalPoints = listOf(
                ClinicalPoint(
                    number = 1,
                    title = "Definisi Dasar",
                    description = "Kontak kulit langsung (skin-to-skin) antara dada bayi telanjang dengan dada orang tua secara berkelanjutan dan terfiksasi aman."
                ),
                ClinicalPoint(
                    number = 2,
                    title = "Inkubator Alami (Thermal Synchrony)",
                    description = "Penyesuaian suhu tubuh orang tua secara cerdas dapat mencegah hipotermia berbahaya pada bayi dengan berat lahir rendah (BBLR)."
                ),
                ClinicalPoint(
                    number = 3,
                    title = "Stabilisasi Tanda Vital",
                    description = "Membantu menstabilkan denyut jantung, keteraturan irama napas, saturasi oksigen, serta menekan risiko henti napas sesaat (apnea)."
                ),
                ClinicalPoint(
                    number = 4,
                    title = "Dukungan Laktasi & Ikatan Kasih",
                    description = "Rangsangan sentuhan dan aroma ibu memicu hormon oksitosin yang melancarkan produksi ASI serta membangun kedekatan emosional (bonding)."
                ),
                ClinicalPoint(
                    number = 5,
                    title = "Durasi Minimal Sesi",
                    description = "Disarankan minimal 60 menit per sesi agar siklus tidur lelap (deep sleep cycle) bayi tidak terputus dan pemulihan optimal."
                )
            ),
            calloutTip = "Ayah juga dapat melakukan PMK secara bergantian untuk membantu istirahat Bunda!",
            nextLessonId = 2
        ),
        PmkVideoItem(
            id = 2,
            title = "2. Manfaat PMK untuk bayi BBLR",
            category = "Perawatan Metode Kanguru",
            duration = "03:20",
            durationSeconds = 200,
            thumbnailRes = R.drawable.thumb_pmk_video_2,
            videoRes = R.raw.video_module_pmk_1,
            description = "Berbagai manfaat fisiologis dan psikologis PMK untuk bayi prematur dan BBLR selama masa perawatan di fasilitas kesehatan maupun di rumah.",
            clinicalPoints = listOf(
                ClinicalPoint(
                    number = 1,
                    title = "Termoregulasi Efektif",
                    description = "Mencegah hipotermia melalui transfer panas biologis yang aman tanpa risiko hipertermia akibat alat inkubator eksternal."
                ),
                ClinicalPoint(
                    number = 2,
                    title = "Kenaikan Berat Badan Lebih Cepat",
                    description = "Energi bayi tidak terbuang untuk mempertahankan suhu tubuh, sehingga kalori dialihkan secara efisien untuk pertumbuhan."
                ),
                ClinicalPoint(
                    number = 3,
                    title = "Pola Tidur Tenang & Teratur",
                    description = "Meningkatkan persentase quiet sleep yang sangat dibutuhkan bagi pematangan sistem saraf pusat dan otak bayi."
                ),
                ClinicalPoint(
                    number = 4,
                    title = "Pencegahan Infeksi Nosokomial",
                    description = "Kolonisasi bakteri baik dari kulit orang tua memperkuat pertahanan alami saluran cerna dan kulit si kecil."
                ),
                ClinicalPoint(
                    number = 5,
                    title = "Memperpendek Masa Rawat Inap",
                    description = "Stabilitas klinis yang lebih cepat dicapai memungkinkan bayi pulang lebih awal bersama keluarga."
                )
            ),
            calloutTip = "PMK terbukti menurunkan angka kematian neonatal pada bayi BBLR hingga lebih dari 30%.",
            nextLessonId = 3
        ),
        PmkVideoItem(
            id = 3,
            title = "3. Persiapan sebelum PMK",
            category = "Perawatan Metode Kanguru",
            duration = "04:10",
            durationSeconds = 250,
            thumbnailRes = R.drawable.thumb_pmk_video_3,
            videoRes = R.raw.video_module_pmk_1,
            description = "Panduan persiapan orang tua dan bayi sebelum sesi PMK dimulai, termasuk kebersihan tubuh, kesiapan pakaian, dan kondisi lingkungan.",
            clinicalPoints = listOf(
                ClinicalPoint(
                    number = 1,
                    title = "Kebersihan Tangan & Tubuh",
                    description = "Cuci tangan 6 langkah dengan sabun di bawah air mengalir. Ibu/ayah sebaiknya mandi dan tidak mengenakan parfum beraroma tajam."
                ),
                ClinicalPoint(
                    number = 2,
                    title = "Pakaian Pengasuh yang Sesuai",
                    description = "Kenakan baju berkancing depan yang longgar atau selendang/kain kanguru yang bersih dan tidak berbahan kasar."
                ),
                ClinicalPoint(
                    number = 3,
                    title = "Persiapan Pakaian Bayi",
                    description = "Bayi hanya mengenakan popok bersih, topi bayi hangat, dan kaus kaki untuk memaksimalkan area kontak kulit dada."
                ),
                ClinicalPoint(
                    number = 4,
                    title = "Suhu & Suasana Ruangan",
                    description = "Pastikan ruangan hangat (24°C - 26°C), terhindar dari hembusan angin langsung jendela/AC, serta tenang dan bebas asap."
                ),
                ClinicalPoint(
                    number = 5,
                    title = "Kenyamanan Orang Tua",
                    description = "Kosongkan kandung kemih dan siapkan air minum dekat jangkauan agar sesi PMK dapat berlangsung nyaman tanpa interupsi."
                )
            ),
            calloutTip = "Gunakan kursi dengan sandaran yang nyaman dan bantal penyangga punggung sebelum memulai sesi.",
            nextLessonId = 4
        ),
        PmkVideoItem(
            id = 4,
            title = "4. Posisi bayi yang benar",
            category = "Perawatan Metode Kanguru",
            duration = "04:35",
            durationSeconds = 275,
            thumbnailRes = R.drawable.thumb_pmk_video_4,
            videoRes = R.raw.video_module_pmk_1,
            description = "Video ini menjelaskan posisi bayi yang tepat saat melakukan PMK agar bayi merasa nyaman, aman, dan pernapasan tetap stabil.",
            clinicalPoints = listOf(
                ClinicalPoint(
                    number = 1,
                    title = "Posisi Tegak (Upright)",
                    description = "Letakkan bayi dalam posisi vertikal tegak di antara kedua payudara ibu dengan dada bayi menempel langsung pada dada orang tua."
                ),
                ClinicalPoint(
                    number = 2,
                    title = "Kepala Menoleh & Leher Tengadah",
                    description = "Kepala bayi dimiringkan ke samping dengan leher sedikit tengadah (sniffing position) agar jalan napas selalu terbuka bebas."
                ),
                ClinicalPoint(
                    number = 3,
                    title = "Kaki Posisi Kodok (Frog Position)",
                    description = "Paha dan lutut bayi tertekuk fleksi menyerupai katak, menopang perkembangan anatomis sendi panggul yang sehat."
                ),
                ClinicalPoint(
                    number = 4,
                    title = "Fiksasi Kain Menopang Punggung",
                    description = "Kain atau baju penggendong harus menopang punggung dan leher bawah bayi dengan aman tanpa menekan perut dan dada terlalu kencang."
                ),
                ClinicalPoint(
                    number = 5,
                    title = "Jalan Napas Bebas Hambatan",
                    description = "Pastikan hidung dan mulut bayi tidak tertutup lipatan kain atau payudara ibu, dan pantau gerakan napas teratur."
                )
            ),
            calloutTip = "Selalu periksa bahwa Anda dapat mencium dahi si kecil dengan mudah tanpa menunduk terlalu dalam.",
            nextLessonId = 5
        ),
        PmkVideoItem(
            id = 5,
            title = "5. Cara melakukan PMK",
            category = "Perawatan Metode Kanguru",
            duration = "05:28",
            durationSeconds = 328,
            thumbnailRes = R.drawable.thumb_pmk_video_5,
            videoRes = R.raw.video_module_pmk_1,
            description = "Langkah demi langkah memposisikan bayi ke dalam kain atau gendongan khusus PMK secara aman, mandiri, dan berkesinambungan.",
            clinicalPoints = listOf(
                ClinicalPoint(
                    number = 1,
                    title = "Memasukkan Bayi ke Gendongan",
                    description = "Buka kancing baju ibu, selipkan bayi perlahan ke dalam kantong kain dengan satu tangan menopang leher dan bokong."
                ),
                ClinicalPoint(
                    number = 2,
                    title = "Mengatur Ketinggian Posisi",
                    description = "Posisikan dagu bayi berada tepat di atas belahan dada ibu agar jalan napas tidak tertekuk saat pengasuh bernapas."
                ),
                ClinicalPoint(
                    number = 3,
                    title = "Mengikat Simpul Penopang",
                    description = "Kencangkan kain di bagian bawah bokong dan silangkan ke punggung, pastikan simpul mati terikat kuat dan tidak melorot."
                ),
                ClinicalPoint(
                    number = 4,
                    title = "Posisi Istirahat Pengasuh",
                    description = "Ibu atau ayah dapat duduk santai dengan sudut sandaran 30-45 derajat untuk relaksasi otot leher dan bahu."
                ),
                ClinicalPoint(
                    number = 5,
                    title = "Durasi Berkesinambungan",
                    description = "Lakukan PMK selama mungkin, idealnya berkelanjutan minimal 60 hingga 120 menit setiap sesi harian."
                )
            ),
            calloutTip = "Kain jarik tradisional atau selendang katun yang bersih dapat digunakan bila baju kanguru khusus belum tersedia.",
            nextLessonId = 6
        ),
        PmkVideoItem(
            id = 6,
            title = "6. Hal yang perlu diperhatikan",
            category = "Perawatan Metode Kanguru",
            duration = "03:40",
            durationSeconds = 220,
            thumbnailRes = R.drawable.thumb_pmk_video_6,
            videoRes = R.raw.video_module_pmk_1,
            description = "Tanda-tanda bahaya dan hal krusial yang wajib diperhatikan orang tua saat memantau kondisi bayi selama PMK berlangsung.",
            clinicalPoints = listOf(
                ClinicalPoint(
                    number = 1,
                    title = "Pantau Suhu Tubuh Berkala",
                    description = "Pastikan suhu tubuh bayi berada dalam rentang normal (36.5°C - 37.5°C). Waspadai bila telapak kaki teraba dingin."
                ),
                ClinicalPoint(
                    number = 2,
                    title = "Waspadai Perubahan Warna Kulit",
                    description = "Segera periksa jika bibir, lidah, atau ujung jari bayi tampak pucat atau kebiruan (sianosis)."
                ),
                ClinicalPoint(
                    number = 3,
                    title = "Tanda Kesulitan Bernapas",
                    description = "Hentikan sesi bila bayi tampak sesak, ada tarikan dinding dada ke dalam (retraksi), atau terdengar merintih (grunting)."
                ),
                ClinicalPoint(
                    number = 4,
                    title = "Bayi Sulit Dibangunkan (Letargis)",
                    description = "Bila bayi lemas tidak bereaksi terhadap rangsangan sentuhan lembut dan menolak minum ASI, segera hubungi tenaga medis."
                ),
                ClinicalPoint(
                    number = 5,
                    title = "Kondisi Kegawatan Medis",
                    description = "Segera bawa bayi ke fasilitas kesehatan terdekat jika ditemukan salah satu tanda bahaya di atas."
                )
            ),
            calloutTip = "Bila bayi sedang demam tinggi (>38°C) atau dalam kondisi kejang, tunda sesi PMK dan segera konsultasikan ke dokter.",
            nextLessonId = null
        )
    )

    fun getVideoById(id: Int): PmkVideoItem {
        return videos.find { it.id == id } ?: videos[3]
    }
}
