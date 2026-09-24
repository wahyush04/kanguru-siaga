package com.kangurusiaga.app.presentation.pmk.video

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import com.kangurusiaga.app.R

/**
 * Data model for PMK educational videos based on Google Stitch design
 * "Kanguru Siaga - Video Edukasi PMK" and "Kanguru Siaga - Detail Pemutar Video PMK".
 */
data class PmkVideoItem(
    val id: Int,
    val title: String,
    val duration: String,
    val durationSeconds: Int,
    @DrawableRes val thumbnailRes: Int,
    @RawRes val videoRes: Int = R.raw.video_pmk_posisi,
    val description: String,
    val keyPoints: List<String>
)

object PmkVideoDataSource {
    val videos: List<PmkVideoItem> = listOf(
        PmkVideoItem(
            id = 1,
            title = "1. Pengertian PMK",
            duration = "02:15",
            durationSeconds = 135,
            thumbnailRes = R.drawable.thumb_pmk_video_1,
            description = "Pengenalan dasar mengenai Perawatan Metode Kanguru (PMK) dan bagaimana metode ini membantu bayi BBLR tumbuh optimal.",
            keyPoints = listOf(
                "Kontak kulit ke kulit (skin-to-skin contact) langsung antara bayi dan ibu/ayah",
                "Menstabilkan denyut jantung, pernapasan, dan saturasi oksigen bayi",
                "Membantu meningkatkan kenaikan berat badan bayi lebih optimal",
                "Membangun ikatan emosional (bonding) yang kuat antara orang tua dan bayi"
            )
        ),
        PmkVideoItem(
            id = 2,
            title = "2. Manfaat PMK untuk bayi BBLR",
            duration = "03:20",
            durationSeconds = 200,
            thumbnailRes = R.drawable.thumb_pmk_video_2,
            description = "Berbagai manfaat fisiologis dan psikologis PMK untuk bayi prematur dan BBLR selama perawatan di rumah sakit dan di rumah.",
            keyPoints = listOf(
                "Menjaga suhu tubuh bayi tetap hangat dan mencegah risiko hipotermia",
                "Mendukung dan memperlancar keberhasilan pemberian ASI eksklusif",
                "Membuat bayi tidur lebih tenang, nyenyak, dan mengurangi waktu menangis",
                "Mengurangi risiko infeksi nosokomial serta memperpendek lama rawat inap"
            )
        ),
        PmkVideoItem(
            id = 3,
            title = "3. Persiapan sebelum PMK",
            duration = "04:10",
            durationSeconds = 250,
            thumbnailRes = R.drawable.thumb_pmk_video_3,
            description = "Panduan persiapan orang tua dan bayi sebelum sesi PMK dimulai, termasuk kebersihan dan kenyamanan pakaian.",
            keyPoints = listOf(
                "Cuci tangan dengan sabun dan air mengalir hingga bersih",
                "Ibu/ayah mandi bersih dan memakai pakaian berkancing depan yang longgar",
                "Bayi hanya mengenakan popok, topi hangat, dan kaus kaki",
                "Pastikan suhu ruangan tenang, hangat (22-26°C), dan bebas dari angin langsung"
            )
        ),
        PmkVideoItem(
            id = 4,
            title = "4. Posisi bayi yang benar",
            duration = "04:35",
            durationSeconds = 275,
            thumbnailRes = R.drawable.thumb_pmk_video_4,
            description = "Video ini menjelaskan posisi bayi yang tepat saat melakukan PMK agar bayi merasa nyaman, aman, dan pernapasan tetap stabil.",
            keyPoints = listOf(
                "Posisi tegak di dada ibu/ayah",
                "Kepala bayi menghadap ke samping",
                "Jalan napas tetap terbuka & tidak tertekuk",
                "Kaki bayi dalam posisi seperti katak (frog position)",
                "Pastikan bayi merasa hangat dan nyaman"
            )
        ),
        PmkVideoItem(
            id = 5,
            title = "5. Cara melakukan PMK",
            duration = "05:28",
            durationSeconds = 328,
            thumbnailRes = R.drawable.thumb_pmk_video_5,
            description = "Langkah demi langkah memposisikan bayi ke dalam kain atau gendongan khusus PMK secara aman dan mandiri.",
            keyPoints = listOf(
                "Rapatkan dada bayi secara vertikal langsung ke dada orang tua",
                "Ikat kain selendang atau baju kanguru dengan pas dan aman menyangga pantat bayi",
                "Orang tua dapat beristirahat dalam posisi duduk santai atau setengah berbaring (30-45°)",
                "Lakukan PMK secara kontinu dengan durasi minimal 60 menit setiap sesi"
            )
        ),
        PmkVideoItem(
            id = 6,
            title = "6. Hal yang perlu diperhatikan",
            duration = "03:40",
            durationSeconds = 220,
            thumbnailRes = R.drawable.thumb_pmk_video_6,
            description = "Tanda-tanda bahaya dan hal krusial yang wajib diperhatikan orang tua saat memantau kondisi bayi selama PMK berlangsung.",
            keyPoints = listOf(
                "Waspadai bila kulit bayi tampak pucat atau kebiruan (sianosis)",
                "Perhatikan bila napas bayi tersengal-sengal atau berhenti sejenak (>20 detik)",
                "Cek suhu tubuh secara berkala agar tidak kedinginan (<36.5°C) atau kepanasan (>37.5°C)",
                "Segera letakkan bayi dalam posisi aman dan hubungi tenaga kesehatan bila ada kegawatan"
            )
        )
    )

    fun getVideoById(id: Int): PmkVideoItem {
        return videos.find { it.id == id } ?: videos[3] // Default to #4 (Posisi bayi yang benar)
    }
}
