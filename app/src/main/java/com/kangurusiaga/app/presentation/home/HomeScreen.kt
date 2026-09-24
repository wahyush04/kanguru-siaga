package com.kangurusiaga.app.presentation.home

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.BrandBackground
import com.kangurusiaga.app.core.designsystem.theme.BrandCardBorder
import com.kangurusiaga.app.core.designsystem.theme.BrandDarkPink
import com.kangurusiaga.app.core.designsystem.theme.BrandLightPink
import com.kangurusiaga.app.core.designsystem.theme.BrandPink
import com.kangurusiaga.app.core.designsystem.theme.BrandSoftAmber
import com.kangurusiaga.app.core.designsystem.theme.BrandSoftBlue
import com.kangurusiaga.app.core.designsystem.theme.BrandSoftGreen
import com.kangurusiaga.app.core.designsystem.theme.BrandTextAmber
import com.kangurusiaga.app.core.designsystem.theme.BrandTextBlue
import com.kangurusiaga.app.core.designsystem.theme.BrandTextGreen
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.core.designsystem.theme.TextPrimary
import com.kangurusiaga.app.core.designsystem.theme.TextSecondary
import com.kangurusiaga.app.core.designsystem.theme.White
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.Gender
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        uiState = uiState,
        modifier = modifier
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    modifier: Modifier = Modifier
) {
    val dimensions = KanguruTheme.dimensions

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BrandBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        color = BrandPink
                    )
                }
                uiState.activeBaby != null -> {
                    ActiveBabyContent(
                        baby = uiState.activeBaby,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(dimensions.spaceMedium)
                    )
                }
                else -> {
                    EmptyHomeContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(dimensions.spaceLarge)
                    )
                }
            }
        }
    }
}

@Composable
private fun ActiveBabyContent(
    baby: Baby,
    modifier: Modifier = Modifier
) {
    val dimensions = KanguruTheme.dimensions
    val formattedDate = remember(baby.birthDateEpochMillis) {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        sdf.format(Date(baby.birthDateEpochMillis))
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimensions.spaceMedium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_kangaroo_mascot),
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(dimensions.spaceSmall))
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = BrandPink
            )
        }

        Spacer(modifier = Modifier.height(dimensions.spaceSmall))

        // Baby Profile Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BrandCardBorder, RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = White),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Photo avatar
                Box(
                    modifier = Modifier
                        .size(108.dp)
                        .clip(CircleShape)
                        .background(BrandLightPink)
                        .border(3.dp, BrandPink, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (baby.photoUri != null) {
                        AsyncImage(
                            model = Uri.parse(baby.photoUri),
                            contentDescription = "Foto ${baby.name}",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_kangaroo_mascot),
                            contentDescription = null,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Baby name
                Text(
                    text = baby.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Gender badge
                val (genderText, genderBg, genderColor) = when (baby.gender) {
                    Gender.FEMALE -> Triple("Perempuan", BrandLightPink, BrandDarkPink)
                    Gender.MALE -> Triple("Laki-laki", BrandSoftBlue, BrandTextBlue)
                    Gender.UNSPECIFIED -> Triple("Tidak ditentukan", BrandBackground, TextSecondary)
                }

                Surface(
                    color = genderBg,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = genderText,
                        color = genderColor,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = BrandCardBorder)
                Spacer(modifier = Modifier.height(16.dp))

                // Details grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    InfoItem(
                        label = "Tanggal Lahir",
                        value = formattedDate
                    )
                    InfoItem(
                        label = "Berat Lahir",
                        value = "${baby.birthWeightGram} gram"
                    )
                }

                if (baby.birthWeightGram in 1..2499) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        color = BrandSoftAmber,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Kategori BBLR (< 2.500 g) — Siap Didampingi PMK",
                                color = BrandTextAmber,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Onboarding completion banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BrandCardBorder, RoundedCornerShape(18.dp)),
            colors = CardDefaults.cardColors(containerColor = BrandSoftGreen),
            shape = RoundedCornerShape(18.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Profil bayi Anda telah aktif. Fitur pemantauan metode kanguru (PMK), jadwal menyusui, dan edukasi akan tersedia di tahapan berikutnya.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BrandTextGreen,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun InfoItem(
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    }
}

@Composable
private fun EmptyHomeContent(
    modifier: Modifier = Modifier
) {
    val dimensions = KanguruTheme.dimensions
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_kangaroo_mascot),
            contentDescription = null,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(dimensions.spaceMedium))
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
            color = BrandPink,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(dimensions.spaceSmall))
        Text(
            text = "Aplikasi Pendamping Perawatan BBLR & Metode Kanguru",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(dimensions.spaceLarge))
        Text(
            text = "Belum ada data bayi aktif.",
            style = MaterialTheme.typography.labelLarge,
            color = TextSecondary
        )
    }
}
