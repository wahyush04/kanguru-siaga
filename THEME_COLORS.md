# Dokumentasi Sistem Tema Warna (Kanguru Siaga Design System)

Dokumentasi resmi arsitektur warna, tokenisasi semantik, dan panduan penggunaan warna tema pada aplikasi **Kanguru Siaga** (Mobile Android Jetpack Compose).

---

## 1. Filosofi & Landasan Desain

- **Single Source of Truth (SSOT):** Desain visual merujuk penuh pada Google Stitch Project `projects/10808370107038581899` (*Kanguru siaga*).
- **Kenyamanan Perawatan Neonatal & Ibu:** 
  - **Light Mode ("Terang Hangat"):** Menggunakan dominasi latar hangat cream lembut (`#FAF7F2` / `#FFF9F6`) dan aksen coral/rose hangat khas Kanguru (`#FF5C77`) untuk menghadirkan suasana penuh kehangatan, harapan, dan kasih sayang bagi orang tua bayi prematur.
  - **Dark Mode ("Redup Ruang Menyusui"):** Didesain khusus menggunakan latar hitam/charcoal hangat yang sangat redup (`#141312` / `#1E1C1A`), meminimalkan emisi cahaya biru ketika ibu menyusui atau melakukan KMC (Kangaroo Mother Care) di ruangan gelap/malam hari agar tidak mengganggu siklus tidur bayi prematur.
- **Standar Klinis & Aksesibilitas:**
  - Seluruh warna status kegawatan (merah, amber, biru, hijau) dan kurva pertumbuhan Fenton (P97, P90, P50, P10, P3) mengacu pada standar visual IDAI & WHO.
  - Memenuhi rasio kontras WCAG AA untuk keterbacaan teks utama dan sekunder.

---

## 2. Arsitektur Kode Tema

Arsitektur sistem warna terletak pada package:
`com.kangurusiaga.app.core.designsystem.theme`

```
app/src/main/java/com/kangurusiaga/app/core/designsystem/theme/
├── Color.kt        # Definisi konstanta statis & dynamic convenience tokens (@Composable @ReadOnlyComposable)
└── Theme.kt        # Data class KanguruColors, LightKanguruColors, DarkKanguruColors, CompositionLocal, dan KanguruTheme
```

### Akses Warna di Composable
Gunakan salah satu dari dua pendekatan standar berikut:
1. **Melalui `KanguruTheme.colors` (Direkomendasikan untuk konsistensi):**
   ```kotlin
   Text(
       text = "Suhu Tubuh",
       color = KanguruTheme.colors.textPrimary
   )
   ```
2. **Melalui dynamic token di `Color.kt` (Convenience getters):**
   ```kotlin
   Box(
       modifier = Modifier.background(BrandBackground)
   )
   ```

---

## 3. Katalog Token Warna Semantik (Light vs Dark)

### A. Latar Belakang, Surface, & Garis Batas (Borders)

| Token Semantik | Light Mode (Terang Hangat) | Dark Mode (Redup Menyusui) | Peran / Kegunaan |
|---|---|---|---|
| `background` | `#FAF7F2` (Warm Ivory) | `#141312` (Dim Charcoal) | Background layar utama aplikasi |
| `surface` | `#FFFFFF` (Pure White) | `#1E1C1A` (Soft Dark Surface) | Card permukaan utama, dialog, sheet |
| `surfaceVariant` | `#F5F0E8` (Light Muted) | `#282522` (Dark Muted) | Card container sekunder, chip inactive |
| `cardBackground` | `#FFFFFF` (Pure White) | `#1E1C1A` (Soft Dark Surface) | Background kartu konten |
| `cardBorder` | `#E8E2D8` (Warm Grey Border) | `#35322D` (Muted Dark Border) | Outline/stroke kartu |
| `divider` | `#E8E2D8` (Warm Grey Divider) | `#2E2B26` (Dark Divider) | Garis pemisah horizontal/vertikal |

---

### B. Brand Utama & Aksen Primer

| Token Semantik | Light Mode (Terang Hangat) | Dark Mode (Redup Menyusui) | Peran / Kegunaan |
|---|---|---|---|
| `primary` | `#FF5C77` (Warm Coral Pink) | `#FF5C77` (Warm Coral Pink) | Warna identitas utama Kanguru, tombol CTA |
| `onPrimary` | `#FFFFFF` | `#FFFFFF` | Teks/ikon di atas warna primary |
| `primaryContainer` | `#FFF0F2` (Soft Pink Cream) | `#2D191D` (Muted Rose Dark) | Background container tombol/badge primer |
| `onPrimaryContainer` | `#9F1239` (Deep Burgundy) | `#FFB4C0` (Pastel Pink) | Teks/ikon di dalam primaryContainer |
| `primaryBorder` | `#FFCCD5` (Rose Border) | `#5C2832` (Muted Rose Border) | Border container primer |
| `secondaryContainer` | `#FFF3E0` (Soft Peach) | `#2E2218` (Dark Peach) | Container aksen sekunder |
| `onSecondaryContainer` | `#9A3412` (Rust Brown) | `#FFCC99` (Pastel Peach) | Teks di dalam secondaryContainer |

---

### C. Tipografi & Hirarki Teks

| Token Semantik | Light Mode (Terang Hangat) | Dark Mode (Redup Menyusui) | Peran / Kegunaan |
|---|---|---|---|
| `textPrimary` | `#1E293B` (Slate 900) | `#FAF7F2` (Warm Off-White) | Judul, heading, body utama |
| `textSecondary` | `#64748B` (Slate 500) | `#A8A399` (Muted Warm Grey) | Subtitle, deskripsi, keterangan waktu |
| `textTertiary` | `#94A3B8` (Slate 400) | `#6A655C` (Subtle Dark Grey) | Placeholder, caption redup, timestamp |

---

### D. Status Klinis (Error, Warning, Info, Success)

| Token Semantik | Light Mode | Dark Mode | Peran / Kegunaan |
|---|---|---|---|
| `error` | `#DC2626` (Red 600) | `#FF8090` (Soft Red) | Status bahaya, tombol hapus, validasi gagal |
| `errorContainer` | `#FFF5F5` (Soft Red Bg) | `#2A1517` (Dark Red Bg) | Callout bahaya, badge kritis |
| `errorBorder` | `#FED7D7` (Red Border) | `#5A2328` (Dark Red Stroke) | Garis pembatas callout bahaya |
| `errorText` | `#9F1239` (Dark Burgundy) | `#FFA5B0` (Pastel Red Text) | Teks peringatan bahaya |
| `warning` | `#D97706` (Amber 600) | `#FFD060` (Soft Amber) | Peringatan tanda waspada, timer jeda |
| `warningContainer` | `#FFF9EB` (Soft Amber Bg) | `#2B2111` (Dark Amber Bg) | Container kartu peringatan |
| `warningBorder` | `#FDE68A` (Amber Border) | `#5E451D` (Dark Amber Stroke) | Garis pembatas kartu peringatan |
| `warningText` | `#92400E` (Dark Amber Text) | `#FFE088` (Pastel Amber Text) | Teks peringatan waspada |
| `info` | `#0284C7` (Sky Blue 600) | `#60A5FA` (Soft Sky Blue) | Informasi klinis, hotline telepon |
| `infoContainer` | `#EFF6FB` (Soft Blue Bg) | `#152233` (Dark Blue Bg) | Background badge & card informasi |
| `infoBorder` | `#BAE6FD` (Sky Blue Border) | `#1E3A5F` (Dark Blue Stroke) | Garis pembatas info card |
| `success` | `#059669` (Emerald 600) | `#34D399` (Soft Emerald) | Target tercapai, switch aktif, data valid |
| `successContainer` | `#ECFDF5` (Soft Emerald Bg)| `#12271E` (Dark Emerald Bg) | Card pencapaian target PMK harian |
| `successBorder` | `#A7F3D0` (Emerald Border) | `#1E4835` (Dark Emerald Stroke)| Garis pembatas card sukses |

---

### E. Kategori Modul Edukasi & Fitur

| Token Semantik | Variasi (Container, Border, Text) | Light Mode | Dark Mode |
|---|---|---|---|
| `categoryPurple` | Modul Perawatan Khusus | `#9333EA` / `#FAF5FF` / `#E9D5FF` / `#6B21A8` | `#C084FC` / `#2B173B` / `#582C7A` / `#E9D5FF` |
| `categoryTeal` | Modul Bayi BBLR / Klinis | `#0D9488` / `#F0FDFA` / `#99F6E4` / `#115E59` | `#2DD4BF` / `#122926` / `#1D524A` / `#99F6E4` |
| `categoryOrange` | Modul Tips Praktis & Nutrisi | `#EA580C` / `#FFF7ED` / `#FED7AA` / `#9A3412` | `#FB923C` / `#2D1D13` / `#59341E` / `#FED7AA` |
| `categoryRose` | Modul Edukasi PMK & Kasih Ibu | `#F43F5E` / `#FFF1F2` / `#FECDD3` / `#9F1239` | `#FB7185` / `#2D161B` / `#5C2430` / `#FECDD3` |

---

### F. Kegawatan & Disclaimer Medis

| Token Semantik | Light Mode | Dark Mode | Peran / Kegunaan |
|---|---|---|---|
| `emergencyBannerBackground` | `#FEF2F2` (Rose 50) | `#2A1316` (Deep Dark Red) | Banner tanda bahaya di atas daftar |
| `emergencyBannerBorder` | `#FEE2E2` (Rose 100) | `#4A1E24` (Muted Dark Stroke) | Border banner tanda bahaya |
| `emergencyBannerTitle` | `#991B1B` (Red 800) | `#FF8A8A` (Bright Soft Red) | Judul besar peringatan darurat |
| `emergencyBannerSubtitle` | `#7F1D1D` (Red 900) | `#E2A0A0` (Muted Soft Red) | Subjudul penjelasan darurat |
| `emergencyAction` | `#DC2626` (Red 600) | `#EF4444` (Vibrant Red) | Tombol Panggil Ambulans / 119 |
| `disclaimerBackground` | `#EFF6FB` (Soft Blue) | `#17212F` (Muted Dark Blue) | Kotak disclaimer hukum & medis |
| `disclaimerBorder` | `#BAE6FD` (Sky 200) | `#243952` (Dark Blue Stroke)| Border disclaimer medis |
| `disclaimerTitle` | `#0369A1` (Sky 700) | `#7DD3FC` (Pastel Sky) | Judul disclaimer medis |
| `disclaimerText` | `#0C4A6E` (Sky 900) | `#BAE6FD` (Light Sky Text) | Isi paragraf disclaimer |

---

### G. Pemantauan Pertumbuhan (Kurva Fenton & Parameter)

| Token Semantik | Nilai Warna Semantik | Peran / Standar Klinis |
|---|---|---|
| `growthLength` / `growthLengthBorder` | `#10B981` / `#DCF5E6` | Parameter Panjang Badan (PB / Height) |
| `growthHead` / `growthHeadBorder` | `#3B82F6` / `#DFEEFF` | Parameter Lingkar Kepala (LK / Head Circ.) |
| `fentonP97` | `#EF4444` (Red) / `#F87171` (Dark) | Garis Persentil 97 (Batas Atas Ekstrem) |
| `fentonP90` | `#F97316` (Orange) / `#FB923C` (Dark) | Garis Persentil 90 (Batas Atas Normal) |
| `fentonP50` | `#10B981` (Emerald) / `#34D399` (Dark) | Garis Persentil 50 (Median Ideal) |
| `fentonP10` | `#F59E0B` (Amber) / `#FCD34D` (Dark) | Garis Persentil 10 (Batas Bawah Normal) |
| `fentonP3` | `#FB7185` (Rose) / `#FDA4AF` (Dark) | Garis Persentil 3 (Batas Bawah Ekstrem) |

---

### H. Perawatan Metode Kanguru (PMK) & Caregiver

| Token Semantik | Nilai Warna | Peran / Penggunaan |
|---|---|---|
| `caregiverIbu` | `#FF5C77` (Warm Coral) | Sesi PMK digendong oleh Ibu |
| `caregiverAyah` | `#3B82F6` (Sky Blue) | Sesi PMK digendong oleh Ayah |
| `caregiverPendamping` | `#10B981` (Emerald Green) | Sesi PMK didampingi Keluarga/Bidan |
| `caregiverPause` | `#F59E0B` (Amber) | Status timer dijeda sementara |
| `timelineTrack` | `#E8E2D8` (Light) / `#2E2B26` (Dark) | Lintasan progress bar 24 jam PMK |
| `dragHandle` | `#D1D5DB` (Light) / `#4B5563` (Dark) | Handle indikator tarikan bottom sheet |

---

## 4. Hasil Audit Screen & Komponen (100% Bebas Hardcoded Inline Color)

Seluruh 6 modul aplikasi telah diaudit dan dimigrasikan penuh:

```
[AUDIT VERIFIKASI SELESAI]
Total Inline Color(0x...) di presentation: 0
Status Kompilasi: 100% Clean Build
```

### Tabel Rangkuman Audit Berdasarkan Modul:

| Modul | File Screen / Komponen | Token Tema yang Diterapkan | Status |
|---|---|---|---|
| **Modul 1: Onboarding & Registrasi** | `SplashScreen.kt`<br>`OnboardingScreen.kt`<br>`BabyProfileSetupScreen.kt`<br>`HomeScreen.kt` | `BrandBackground`, `primaryContainer`, `onboardingCardBg*`, `ambientRosePulse`, `ambientAmberPulse`, `ambientWave1/2`, `genderMale*`, `genderFemale*`, `emergencyAction` | ✅ 100% Sesuai |
| **Modul 2: Perawatan Metode Kanguru** | `PmkTimerScreen.kt`<br>`PmkCaregiverHandoverSheet.kt`<br>`PmkRemindersScreen.kt`<br>`PmkTimeline24HourBar.kt`<br>`PmkGuideScreen.kt`<br>`PmkVideoScreen.kt` | `dragHandle`, `categoryOrange*`, `categoryRose*`, `categoryPurple*`, `caregiver*`, `timelineTrack` | ✅ 100% Sesuai |
| **Modul 3: Edukasi & Kegawatan** | `EmergencyWarningListScreen.kt`<br>`EmergencyWarningDetailScreen.kt`<br>`EmergencySectionRenderer.kt`<br>`EducationCenterScreen.kt`<br>`EducationDetailScreen.kt`<br>`EducationThemeHelper.kt` | `emergencyBanner*`, `emergencyAction`, `info*`, `warning*`, `categoryTeal*`, `categoryRose*`, `categoryPurple*`, `categoryOrange*`, `success` | ✅ 100% Sesuai |
| **Modul 4: Alarm Nutrisi & ASI** | `AlarmScreen.kt`<br>`AddFeedingScheduleBottomSheet.kt`<br>`EditFeedingScheduleBottomSheet.kt` | `categoryOrange`, `info`, `categoryTeal`, `categoryRose`, `success`, `primaryBorder`, `errorContainer`, `errorBorder`, `error`, `textTertiary` | ✅ 100% Sesuai |
| **Modul 5: Pemantauan Pertumbuhan** | `GrowthHubScreen.kt`<br>`GrowthSummaryScreen.kt`<br>`SelectGrowthParameterBottomSheet.kt`<br>`GrowthChartScreen.kt`<br>`FentonChart.kt`<br>`AddGrowthMeasurementScreen.kt` | `growthLength`, `growthLengthBorder`, `growthHead`, `growthHeadBorder`, `BrandPink`, `fentonP97`, `fentonP90`, `fentonP50`, `fentonP10`, `fentonP3`, `error` | ✅ 100% Sesuai |
| **Modul 6: Profil & Pengaturan** | `SettingsScreen.kt`<br>`ThemeSelectionBottomSheet.kt`<br>`TextSizeBottomSheet.kt`<br>`UserGuideScreen.kt`<br>`ClinicalGuidelinesScreen.kt`<br>`MedicalDisclaimerScreen.kt`<br>`AboutAppScreen.kt` | `WarmLightSwatches`, `DimNursingSwatches`, `SystemThemeSwatches`, `disclaimer*`, `info*`, `success*`, `category*`, `error` | ✅ 100% Sesuai |

---

## 5. Panduan Praktis Developer (Do's & Don'ts)

### ❌ Dilarang (Don'ts):
1. **Dilarang keras hardcode warna inline:**
   ```kotlin
   // SALAH:
   Box(modifier = Modifier.background(Color(0xFFEFF6FB)))
   Text(text = "Peringatan", color = Color(0xFFDC2626))
   ```
2. **Dilarang hardcode logika manual `if (isDark)` di Composable jika token sudah tersedia:**
   ```kotlin
   // SALAH:
   val bg = if (isDark) Color(0xFF2A1517) else Color(0xFFFFF5F5)
   ```

### ✅ Wajib Dilakukan (Do's):
1. **Gunakan token semantik dari `KanguruTheme.colors`:**
   ```kotlin
   // BENAR:
   Box(modifier = Modifier.background(KanguruTheme.colors.infoContainer))
   Text(text = "Peringatan", color = KanguruTheme.colors.error)
   ```
2. **Jika membutuhkan warna baru:**
   - Daftarkan property baru pada class `KanguruColors` di `Theme.kt`.
   - Berikan definisi untuk `LightKanguruColors` dan `DarkKanguruColors`.
   - Tambahkan getter convenience di `Color.kt` jika sering digunakan lintas screen.
   - Panggil token baru tersebut pada komponen UI.
