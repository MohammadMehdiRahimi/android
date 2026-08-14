package com.example.ui.features.academicreport

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.ui.features.academicleaderboard.AcademicLeaderboardScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademicReportScreen(
    navController: NavController,
    showHeader: Boolean = true
) {
    AcademicLeaderboardScreen(navController = navController)
}
