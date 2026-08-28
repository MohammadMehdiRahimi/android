package com.example.ui.features.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.network.ApiClient
import com.example.network.LeagueLeaderboardBodyDto
import com.example.network.NetworkResult
import com.example.network.safeApiCall

@Composable
fun CompetitiveLeagueScreen(
    navController: NavController,
    onBackClick: () -> Unit = { navController.popBackStack() }
) {
    // Current Active Tab
    var selectedTab by remember { mutableStateOf(LeagueTab.ALL) }

    // Dialog States
    var showRulesDialog by remember { mutableStateOf(false) }
    var showPrizesDialog by remember { mutableStateOf(false) }
    var selectedMemberForDetails by remember { mutableStateOf<LeagueMember?>(null) }

    // Data Source
    val currentUserInfo = remember { LeagueSampleData.currentUser }
    val top3Members = remember { LeagueSampleData.top3Members }

    val displayedMembers = remember(selectedTab) {
        when (selectedTab) {
            LeagueTab.ALL -> LeagueSampleData.allMembers
            LeagueTab.FRIENDS -> LeagueSampleData.friendsMembers
            LeagueTab.AROUND_ME -> LeagueSampleData.aroundMeMembers
            LeagueTab.RULES -> {
                LeagueSampleData.allMembers
            }
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            containerColor = Color(0xFFF8FAFC),
            topBar = {
                LeagueTopHeader(
                    onBackClick = onBackClick,
                    onGiftClick = { showPrizesDialog = true },
                    modifier = Modifier.statusBarsPadding()
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 4.dp, bottom = 28.dp)
            ) {
                // 1. Hero League Card
                item(key = "hero_league_card") {
                    HeroLeagueCard(
                        userLeagueInfo = currentUserInfo
                    )
                }

                // 2. Top 3 Podium Card
                item(key = "top_3_podium_card") {
                    Top3PodiumCard(
                        top3Members = top3Members
                    )
                }

                // 3. Filter Tabs (Chips)
                item(key = "filter_tabs") {
                    LeagueFilterTabs(
                        selectedTab = selectedTab,
                        onTabSelected = { tab ->
                            if (tab == LeagueTab.RULES) {
                                showRulesDialog = true
                            } else {
                                selectedTab = tab
                            }
                        }
                    )
                }

                // 4. League Table Card
                item(key = "league_table_card") {
                    LeagueTableCard(
                        members = displayedMembers,
                        onMemberClick = { member ->
                            selectedMemberForDetails = member
                        }
                    )
                }

                // 5. Bottom Season Prizes Banner
                item(key = "bottom_prizes_banner") {
                    BottomSeasonPrizesBanner(
                        onPrizesClick = { showPrizesDialog = true }
                    )
                }
            }
        }

        // Dialogs
        if (showRulesDialog) {
            LeagueRulesDialog(onDismiss = { showRulesDialog = false })
        }

        if (showPrizesDialog) {
            SeasonPrizesDialog(onDismiss = { showPrizesDialog = false })
        }
    }
}
