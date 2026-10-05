package com.kangurusiaga.app.presentation.education

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.BrandBackground
import com.kangurusiaga.app.core.designsystem.theme.BrandCardBorder
import com.kangurusiaga.app.core.designsystem.theme.BrandLightPink
import com.kangurusiaga.app.core.designsystem.theme.BrandPink
import com.kangurusiaga.app.core.designsystem.theme.CardBorder
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.core.designsystem.theme.TextPrimary
import com.kangurusiaga.app.core.designsystem.theme.TextSecondary
import com.kangurusiaga.app.core.designsystem.theme.TextTertiary
import com.kangurusiaga.app.core.designsystem.theme.White
import com.kangurusiaga.app.domain.model.education.EducationModule
import com.kangurusiaga.app.presentation.education.components.EducationModuleCard
import com.kangurusiaga.app.presentation.home.HomeBottomBar
import com.kangurusiaga.app.presentation.home.HomeTab

@Composable
fun EducationListRoute(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToHome: () -> Unit = onNavigateBack,
    onNavigateToPmk: () -> Unit = {},
    onNavigateToAlarm: () -> Unit = {},
    onNavigateToEducation: () -> Unit = onNavigateBack,
    modifier: Modifier = Modifier,
    viewModel: EducationListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EducationListScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onNavigateToDetail = onNavigateToDetail,
        onNavigateToHome = onNavigateToHome,
        onNavigateToPmk = onNavigateToPmk,
        onNavigateToAlarm = onNavigateToAlarm,
        onNavigateToEducation = onNavigateToEducation,
        onSelectTab = viewModel::selectTab,
        onToggleBookmark = viewModel::toggleBookmark,
        modifier = modifier
    )
}

@Composable
fun EducationListScreen(
    uiState: EducationListUiState,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToPmk: () -> Unit,
    onNavigateToAlarm: () -> Unit = {},
    onNavigateToEducation: () -> Unit = onNavigateBack,
    onSelectTab: (EducationTab) -> Unit,
    onToggleBookmark: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BrandBackground,
        topBar = {
            Surface(
                color = KanguruTheme.colors.surface,
                shadowElevation = 0.5.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(KanguruTheme.colors.surfaceVariant)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.common_back),
                                tint = TextPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Text(
                            text = stringResource(R.string.edu_list_title),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )

                        // Placeholder for symmetry
                        Spacer(modifier = Modifier.size(38.dp))
                    }

                    // Segmented Tabs: Daftar Materi vs Materi Favorit
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        TabButton(
                            title = stringResource(R.string.edu_list_tab_all),
                            selected = uiState.selectedTab == EducationTab.DAFTAR_MATERI,
                            onClick = { onSelectTab(EducationTab.DAFTAR_MATERI) }
                        )

                        TabButton(
                            title = stringResource(R.string.edu_list_tab_favorites),
                            selected = uiState.selectedTab == EducationTab.MATERI_FAVORIT,
                            onClick = { onSelectTab(EducationTab.MATERI_FAVORIT) }
                        )
                    }

                    HorizontalDivider(color = CardBorder, thickness = 1.dp)
                }
            }
        },
        bottomBar = {
            HomeBottomBar(
                currentTab = HomeTab.EDUKASI,
                onTabSelected = { tab ->
                    when (tab) {
                        HomeTab.BERANDA -> onNavigateToHome()
                        HomeTab.PMK -> onNavigateToPmk()
                        HomeTab.EDUKASI -> onNavigateToEducation()
                        HomeTab.ALARM -> onNavigateToAlarm()
                        else -> {}
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BrandPink)
                }
            } else {
                val modules = uiState.displayedModules

                if (modules.isEmpty()) {
                    EmptyEducationContent(
                        isFavoriteTab = uiState.selectedTab == EducationTab.MATERI_FAVORIT,
                        onGoToAllModules = { onSelectTab(EducationTab.DAFTAR_MATERI) },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(modules, key = { it.id }) { module ->
                            EducationModuleCard(
                                module = module,
                                onClick = { onNavigateToDetail(module.id) },
                                onToggleBookmark = { onToggleBookmark(module.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TabButton(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) BrandPink else TextTertiary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .height(2.5.dp)
                .width(48.dp)
                .clip(RoundedCornerShape(50))
                .background(if (selected) BrandPink else Color.Transparent)
        )
    }
}

@Composable
private fun EmptyEducationContent(
    isFavoriteTab: Boolean,
    onGoToAllModules: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(BrandLightPink),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.BookmarkBorder,
                contentDescription = null,
                tint = BrandPink,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isFavoriteTab) stringResource(R.string.edu_list_empty_fav_title) else stringResource(R.string.edu_list_empty_all_title),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isFavoriteTab) {
                stringResource(R.string.edu_list_empty_fav_desc)
            } else {
                stringResource(R.string.edu_list_empty_all_desc)
            },
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )

        if (isFavoriteTab) {
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                modifier = Modifier.clickable(onClick = onGoToAllModules),
                shape = RoundedCornerShape(12.dp),
                color = BrandLightPink,
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandPink.copy(alpha = 0.3f))
            ) {
                Text(
                    text = stringResource(R.string.edu_list_btn_see_all),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandPink,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}


