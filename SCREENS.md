# 📱 Dokumentasi Layar (Screens) - Kanguru Siaga

Dokumentasi lengkap mengenai seluruh layar (*screens*) yang ada di dalam aplikasi **Kanguru Siaga**. Berkas ini disusun berdasarkan modul aplikasi, memuat nama kelas/fungsi Composable, *route navigation*, lokasi berkas, serta deskripsi singkat fungsinya.

---

## 📑 Ringkasan Modul

1. [🚀 Onboarding & Registrasi Awal](#1-onboarding--registrasi-awal)
2. [🦘 Modul Perawatan Metode Kanguru (PMK)](#2-modul-perawatan-metode-kanguru-pmk)
3. [📚 Modul Edukasi & Tanda Kegawatan BBLR](#3-modul-edukasi--tanda-kegawatan-bblr)
4. [⏰ Modul Nutrisi & Alarm ASI](#4-modul-nutrisi--alarm-asi)
5. [📈 Modul Pemantauan Pertumbuhan (Fenton Growth Chart)](#5-modul-pemantauan-pertumbuhan-fenton-growth-chart)
6. [👤 Modul Profil Bayi & Pengaturan](#6-modul-profil-bayi--pengaturan)

---

## 1. Onboarding & Registrasi Awal

| Layar / Nama Screen | Class / Composable | Route Navigation | File Path | Deskripsi Singkat |
| :--- | :--- | :--- | :--- | :--- |
| **Splash Screen** | `SplashScreen`<br>`SplashRoute` | `Screen.Splash`<br>(`splash`) | `app/src/main/java/com/kangurusiaga/app/presentation/onboarding/SplashScreen.kt` | Tampilan awal (*splash screen*) aplikasi dengan logo dan animasi pembuka sebelum memeriksa status registrasi profil bayi. |
| **Onboarding Screen** | `OnboardingScreen`<br>`OnboardingRoute` | `Screen.Onboarding`<br>(`onboarding`) | `app/src/main/java/com/kangurusiaga/app/presentation/onboarding/OnboardingScreen.kt` | Pengenalan fitur-fitur utama aplikasi (edukasi PMK, grafik pertumbuhan Fenton, alarm pemberian ASI/OGT) bagi pengguna baru. |
| **Baby Profile Setup Screen** | `BabyProfileSetupScreen`<br>`BabyProfileSetupRoute` | `Screen.ProfileSetup`<br>(`profile_setup`) | `app/src/main/java/com/kangurusiaga/app/presentation/babyprofile/BabyProfileSetupScreen.kt` | Form registrasi awal data lahir bayi prematur/BBLR (nama, tanggal lahir, jenis kelamin, usia gestasi minggu/hari, BB, PB, LK). |
| **Home Dashboard Screen** | `HomeScreen`<br>`HomeRoute` | `Screen.Home`<br>(`home`) | `app/src/main/java/com/kangurusiaga/app/presentation/home/HomeScreen.kt` | Halaman utama (*dashboard*) dengan kartu status bayi (Usia Koreksi), pencatat cepat PMK, jadwal ASI berikutnya, serta navigasi antar modul. |

---

## 2. Modul Perawatan Metode Kanguru (PMK)

| Layar / Nama Screen | Class / Composable | Route Navigation | File Path | Deskripsi Singkat |
| :--- | :--- | :--- | :--- | :--- |
| **PMK Center Screen** | `PmkCenterScreen` | `Screen.PmkCenter`<br>(`pmk_center`) | `app/src/main/java/com/kangurusiaga/app/presentation/pmk/center/PmkCenterScreen.kt` | Pusat kendali PMK yang menyediakan akses cepat ke Timer, Riwayat, Grafik Statistik, Video Edukasi, dan Pengingat. |
| **PMK Continuous Timer Screen** | `PmkTimerScreen`<br>`PmkTimerRoute` | `Screen.PmkTimer`<br>(`pmk_timer`) | `app/src/main/java/com/kangurusiaga/app/presentation/pmk/timer/PmkTimerScreen.kt` | Layar pencatat waktu (*stopwatch*) PMK kontinu *real-time*, dilengkapi pemilihan pengasuh (Ibu/Ayah/Pendamping), modal suhu, dan serah terima pengasuh. |
| **PMK Guide Screen** | `PmkGuideScreen` | `Screen.PmkGuide`<br>(`pmk_guide`) | `app/src/main/java/com/kangurusiaga/app/presentation/pmk/guide/PmkGuideScreen.kt` | Panduan praktis pelaksanaan PMK kontinu (posisi kanguru, persiapan, kriteria keamanan, dan tanda bahaya saat kontak kulit). |
| **PMK Manual Log Screen** | `PmkManualLogScreen`<br>`PmkManualLogRoute` | `Screen.PmkManualLog`<br>(`pmk_manual_log`) | `app/src/main/java/com/kangurusiaga/app/presentation/pmk/manual/PmkManualLogScreen.kt` | Form pencatatan manual untuk sesi PMK yang telah selesai tanpa menggunakan stopwatch *real-time* (tanggal, waktu, pengasuh, suhu). |
| **PMK History Screen** | `PmkHistoryScreen`<br>`PmkHistoryRoute` | `Screen.PmkHistory`<br>(`pmk_history`) | `app/src/main/java/com/kangurusiaga/app/presentation/pmk/history/PmkHistoryScreen.kt` | Layar riwayat kontak PMK dengan *bar chart* mingguan, *timeline 24 jam* interaktif, serta daftar log sesi individual. |
| **PMK Statistics Screen** | `PmkStatisticsScreen`<br>`PmkStatisticsRoute` | `Screen.PmkStatisticsDetail`<br>(`pmk_statistics_detail`) | `app/src/main/java/com/kangurusiaga/app/presentation/pmk/statistics/PmkStatisticsScreen.kt` | Tampilan statistik mendalam mengenai pencapaian target durasi harian PMK (target 20 jam/hari) dan distribusi peran pengasuh. |
| **PMK Reminders Screen** | `PmkRemindersScreen`<br>`PmkRemindersRoute` | `Screen.PmkReminders`<br>(`pmk_reminders`) | `app/src/main/java/com/kangurusiaga/app/presentation/pmk/reminders/PmkRemindersScreen.kt` | Layar pengaturan notifikasi/pengingat jadwal rutin kontak kulit PMK dan pemeriksaan suhu tubuh bayi. |
| **PMK Video List Screen** | `PmkVideoListScreen` | `Screen.PmkVideoList`<br>(`pmk_video_list`) | `app/src/main/java/com/kangurusiaga/app/presentation/pmk/video/PmkVideoListScreen.kt` | Daftar modul video pembelajaran teknik dan manfaat PMK bagi ibu dan keluarga. |
| **PMK Video Detail Screen** | `PmkVideoScreen` | `Screen.PmkVideoDetail`<br>(`pmk_video_detail/{videoId}`) | `app/src/main/java/com/kangurusiaga/app/presentation/pmk/video/PmkVideoScreen.kt` | Layar pemutar video edukasi PMK interaktif beserta ringkasan poin penting dari setiap materi video. |

---

## 3. Modul Edukasi & Tanda Kegawatan BBLR

| Layar / Nama Screen | Class / Composable | Route Navigation | File Path | Deskripsi Singkat |
| :--- | :--- | :--- | :--- | :--- |
| **Education Center Screen** | `EducationCenterScreen` | `Screen.EducationCenter`<br>(`education_center`) | `app/src/main/java/com/kangurusiaga/app/presentation/education/EducationCenterScreen.kt` | Pusat edukasi perawatan BBLR yang memuat modul bacaan, video PMK, dan pintasan tanda kegawatan. |
| **Education List Screen** | `EducationListScreen`<br>`EducationListRoute` | `Screen.EducationList`<br>(`education_list`) | `app/src/main/java/com/kangurusiaga/app/presentation/education/EducationListScreen.kt` | Daftar katalog materi edukasi perawatan khusus bayi BBLR di rumah. |
| **Education Detail Screen** | `EducationDetailScreen`<br>`EducationDetailRoute` | `Screen.EducationDetail`<br>(`education_detail/{moduleId}`) | `app/src/main/java/com/kangurusiaga/app/presentation/education/EducationDetailScreen.kt` | Tampilan detail modul edukasi yang kaya akan bacaan terstruktur, gambar panduan, dan langkah-langkah praktis. |
| **Emergency Warning List Screen** | `EmergencyWarningListScreen`<br>`EmergencyWarningListRoute` | `Screen.EmergencyWarningList`<br>(`emergency_warning_list`) | `app/src/main/java/com/kangurusiaga/app/presentation/emergency/EmergencyWarningListScreen.kt` | Daftar tanda-tanda bahaya/kegawatan klinis pada bayi BBLR yang memerlukan penanganan medis segera. |
| **Emergency Warning Detail Screen** | `EmergencyWarningDetailScreen`<br>`EmergencyWarningDetailRoute` | `Screen.EmergencyWarningDetail`<br>(`emergency_warning_detail/{moduleId}`) | `app/src/main/java/com/kangurusiaga/app/presentation/emergency/EmergencyWarningDetailScreen.kt` | Detail penjelasan satu tanda bahaya tertentu beserta langkah pertolongan pertama dan arahan rujukan medis. |

---

## 4. Modul Nutrisi & Alarm ASI

| Layar / Nama Screen | Class / Composable | Route Navigation | File Path | Deskripsi Singkat |
| :--- | :--- | :--- | :--- | :--- |
| **Feeding Alarm Screen** | `AlarmScreen`<br>`AlarmRoute` | `Screen.Feeding`<br>(`feeding` / `alarm`) | `app/src/main/java/com/kangurusiaga/app/presentation/feeding/AlarmScreen.kt` | Pengelolaan jadwal dan alarm pengingat pemberian ASI/OGT/NGT berkala (misal tiap 2-3 jam) untuk mencegah hipoglikemia. |

---

## 5. Modul Pemantauan Pertumbuhan (Fenton Growth Chart)

| Layar / Nama Screen | Class / Composable | Route Navigation | File Path | Deskripsi Singkat |
| :--- | :--- | :--- | :--- | :--- |
| **Growth Hub Screen** | `GrowthHubScreen` | `Screen.GrowthHub`<br>(`growth_hub` / `growth`) | `app/src/main/java/com/kangurusiaga/app/presentation/growth/hub/GrowthHubScreen.kt` | Hub utama pertumbuhan bayi prematur dengan indikator status terkini (Berat Badan, Panjang Badan, Lingkar Kepala). |
| **Growth Chart Screen** | `GrowthChartScreen` | `Screen.GrowthChart`<br>(`growth_chart/{parameter}?tab={tab}`) | `app/src/main/java/com/kangurusiaga/app/presentation/growth/chart/GrowthChartScreen.kt` | Kurva pertumbuhan interaktif Fenton 2013 Preterm Growth Chart dengan persentil (P3, P10, P50, P90, P97) berbasis Usia Koreksi. |
| **Add Growth Measurement Screen** | `AddGrowthMeasurementScreen` | `Screen.GrowthAdd`<br>(`growth_add/{parameter}`) | `app/src/main/java/com/kangurusiaga/app/presentation/growth/add/AddGrowthMeasurementScreen.kt` | Form pencatatan pengukuran fisik baru (BB gram/kg, PB cm, LK cm) beserta tanggal pengukuran. |
| **Growth Summary Screen** | `GrowthSummaryScreen` | `Screen.GrowthSummary`<br>(`growth_summary`) | `app/src/main/java/com/kangurusiaga/app/presentation/growth/summary/GrowthSummaryScreen.kt` | Ringkasan rekapitulasi riwayat pertumbuhan bayi dari waktu ke waktu beserta kalkulasi rata-rata kenaikan BB harian. |
| **About Fenton Screen** | `AboutFentonScreen` | `Screen.AboutFenton`<br>(`about_fenton`) | `app/src/main/java/com/kangurusiaga/app/presentation/growth/about/AboutFentonScreen.kt` | Penjelasan edukasi mengenai metodologi Kurva Pertumbuhan Fenton 2013 dan konsep Usia Koreksi bayi prematur. |

---

## 6. Modul Profil Bayi & Pengaturan

| Layar / Nama Screen | Class / Composable | Route Navigation | File Path | Deskripsi Singkat |
| :--- | :--- | :--- | :--- | :--- |
| **Baby Profile Screen** | `BabyProfileScreen`<br>`BabyProfileRoute` | `Screen.BabyProfile`<br>(`baby_profile`) | `app/src/main/java/com/kangurusiaga/app/presentation/profile/BabyProfileScreen.kt` | Tampilan profil bayi lengkap dengan kalkulasi Usia Kronologis & Usia Koreksi serta data fisik terkini. |
| **Settings Screen** | `SettingsScreen`<br>`SettingsRoute` | `Screen.Settings`<br>(`settings`) | `app/src/main/java/com/kangurusiaga/app/presentation/settings/SettingsScreen.kt` | Pengaturan aplikasi meliputi edit profil bayi, pilihan tema (Terang Hangat / Redup Ruang Menyusui), skala ukuran teks, dan bantuan. |
| **User Guide Screen** | `UserGuideScreen` | `Screen.UserGuide`<br>(`user_guide`) | `app/src/main/java/com/kangurusiaga/app/presentation/settings/info/UserGuideScreen.kt` | Petunjuk lengkap cara penggunaan aplikasi Kanguru Siaga bagi pengguna/keluarga pasien. |
| **Medical Disclaimer Screen** | `MedicalDisclaimerScreen` | `Screen.MedicalDisclaimer`<br>(`medical_disclaimer`) | `app/src/main/java/com/kangurusiaga/app/presentation/settings/info/MedicalDisclaimerScreen.kt` | Pernyataan penolakan tanggung jawab medis (*disclaimer*) bahwa aplikasi tidak menggantikan pertimbangan medis dokter/tenaga kesehatan. |
| **About App Screen** | `AboutAppScreen` | `Screen.AboutApp`<br>(`about_app`) | `app/src/main/java/com/kangurusiaga/app/presentation/settings/info/AboutAppScreen.kt` | Informasi versi aplikasi, informasi tim pengembang, serta visi dan misi Kanguru Siaga. |
| **Clinical Guidelines Screen** | `ClinicalGuidelinesScreen` | `Screen.ClinicalGuidelines`<br>(`clinical_guidelines`) | `app/src/main/java/com/kangurusiaga/app/presentation/settings/info/ClinicalGuidelinesScreen.kt` | Referensi acuan pedoman klinis kesehatan bayi prematur/BBLR yang digunakan aplikasi (Kemenkes RI & WHO). |
