# 🎨 Dokumentasi Typography - Kanguru Siaga

## 📌 Ringkasan
Aplikasi **Kanguru Siaga** menggunakan sistem tipografi terpadu berbasis **Material Design 3 Typography**, yang diakses melalui `KanguruTheme.typography.*` (atau `MaterialTheme.typography.*`).

Seluruh komponen `Text` **wajib** menggunakan token typography dari tema ini untuk menjamin konsistensi visual di seluruh layar dan mendukung fitur **Text Scaling (`AppTextScale`)**.

---

## 🔤 Font Family

Aplikasi ini mengintegrasikan 2 keluarga font utama:

1. **Plus Jakarta Sans** (Primary Font Family)
   - Digunakan untuk seluruh komponen UI teks standar (Title, Body, Label, Display, Headline).
   - Menggunakan Variable Font (`plusjakartasans_variablefont_wght.ttf` dan `plusjakartasans_italic_variablefont_wght.ttf`).
   - Mendukung Font Weight: `Light` (300), `Normal` (400), `Medium` (500), `SemiBold` (600), `Bold` (700), `ExtraBold` (800), `Black` (900).

2. **JetBrains Mono** (Timer / Monospace Display Font)
   - Digunakan khusus untuk tampilan angka Stopwatch/Timer PMK agar lebar angka konsisten saat berjalan (*tabular numerals*).
   - Menggunakan Font Asset `jetbrainsmono_extrabold.ttf`.

---

## 📐 Daftar Token Typography (Material 3)

| Token Typography | Font Family | Weight | Size | Line Height | Penggunaan & Panduan |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `displayLarge` | Plus Jakarta Sans | ExtraBold | `32.sp` | `40.sp` | Banner Hero Utama, Splash Screen, Heading besar |
| `displayMedium` | Plus Jakarta Sans | ExtraBold | `28.sp` | `36.sp` | Judul Modul / Screen Level 1 |
| `displaySmall` | Plus Jakarta Sans | ExtraBold | `24.sp` | `32.sp` | Sub-banner, Ringkasan Besar |
| `headlineLarge` | Plus Jakarta Sans | Bold | `24.sp` | `32.sp` | Judul Seksi Utama / Dialog Hero |
| `headlineMedium` | Plus Jakarta Sans | Bold | `20.sp` | `28.sp` | Judul Card Utama, Pop-up Header |
| `headlineSmall` | Plus Jakarta Sans | Bold | `18.sp` | `24.sp` | Sub-judul Seksi / Card Medium |
| `titleLarge` | Plus Jakarta Sans | Bold | `18.sp` | `24.sp` | TopAppBar Title, Card Header |
| `titleMedium` | Plus Jakarta Sans | SemiBold | `16.sp` | `22.sp` | Judul ListItem, Sub-header Card |
| `titleSmall` | Plus Jakarta Sans | Bold | `14.sp` | `20.sp` | Judul Komponen Kecil, Input Field Label |
| `bodyLarge` | Plus Jakarta Sans | Medium | `16.sp` | `24.sp` | Teks Paragraf Utama, Deskripsi Panjang |
| `bodyMedium` | Plus Jakarta Sans | Medium | `14.sp` | `20.sp` | Teks Kontak/Body Standar, Deskripsi Card |
| `bodySmall` | Plus Jakarta Sans | Medium | `12.sp` | `16.sp` | Keterangan Tambahan, Sub-label, Hint Text |
| `labelLarge` | Plus Jakarta Sans | SemiBold | `14.sp` | `20.sp` | Teks Tombol Utama (`Button`), Action Chip |
| `labelMedium` | Plus Jakarta Sans | SemiBold | `12.sp` | `16.sp` | Teks Tombol Kecil, Badge Text, Chip |
| `labelSmall` | Plus Jakarta Sans | Medium | `11.sp` | `14.sp` | Tag Kecil, Timestamp, Micro-copy |

---

## ⏱️ Token Typography Khusus (Custom Display)

| Token | Font Family | Weight | Size | Letter Spacing | Penggunaan |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `KanguruTheme.timerDisplay` | JetBrains Mono | ExtraBold | `38.sp` | `-1.5.sp` | Tampilan Angka Stopwatch PMK (`00:00:00`) |

---

## ⚡ Fitur Text Scaling (`AppTextScale`)

Aplikasi mendukung 3 tingkatan skala ukuran teks yang dapat disesuaikan pengguna melalui layar Pengaturan:

1. **Ringkas (Compact / 0.9x)** - `fontScale * 0.9f`
2. **Standar (Standard / 1.0x)** - `fontScale * 1.0f` *(Default)*
3. **Besar (Large / 1.15x)** - `fontScale * 1.15f`

### Mekanisme Skalabilitas:
Di dalam `KanguruSiagaTheme`, `LocalDensity` disesuaikan secara otomatis berdasarkan opsi `textScale`:

```kotlin
val baseDensity = LocalDensity.current
val scaledDensity = Density(
    density = baseDensity.density,
    fontScale = baseDensity.fontScale * textScale.scaleFactor
)

CompositionLocalProvider(
    LocalDensity provides scaledDensity,
    ...
)
```

> **⚠️ ATURAN PENTING (AGENTS.md):**
> 1. Always use typography style from `KanguruTheme.typography.*` or `MaterialTheme.typography.*`.
> 2. **Dilarang keras melakukan hardcode ukuran font inline** (misal: `fontSize = 14.sp`) secara langsung pada komponen `Text` tanpa menggunakan style tema typography.
> 3. Penggunaan tema typography menjamin fitur **Text Scaling** berfungsi presisi di seluruh layar aplikasi.

---

## 💻 Panduan Penggunaan dalam Kode (Best Practices)

### 1. Penggunaan Standar (`Text` Composable)
```kotlin
// ✅ BENAR: Menggunakan style dari tema
Text(
    text = "Panduan Metode Kanguru",
    style = KanguruTheme.typography.titleMedium,
    color = KanguruTheme.colors.textPrimary
)

// ❌ SALAH: Hardcode fontSize inline
Text(
    text = "Panduan Metode Kanguru",
    fontSize = 16.sp, // DILARANG!
    fontWeight = FontWeight.Bold
)
```

### 2. Penggunaan Warna Teks
Gunakan selalu token warna teks dari `KanguruTheme.colors`:
- `textPrimary`: Teks utama (`TypographyDefaultColor` di Light Mode, `#F5F3EF` di Dark Mode).
- `textSecondary`: Teks sekunder/keterangan (`#6B7280` / `#A8A399`).
- `textTertiary`: Teks placeholder/disabled (`#9CA3AF` / `#757065`).
- `textOnDark`: Teks di atas latar belakang gelap/gradient pink.

```kotlin
Text(
    text = "Catatan Sesi PMK",
    style = KanguruTheme.typography.bodyMedium,
    color = KanguruTheme.colors.textSecondary
)
```

### 3. Tampilan Stopwatch / Timer PMK
```kotlin
Text(
    text = "02:15:30",
    style = KanguruTheme.timerDisplay,
    color = KanguruTheme.colors.textPrimary
)
```
