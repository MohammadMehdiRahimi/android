package com.example.ui.features.academicreport

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademicReportScreen(
    navController: NavController,
    showHeader: Boolean = true,
    viewModel: AnalyzerViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFBFBFE))
                .testTag("academic_report_screen"),
            containerColor = Color(0xFFFBFBFE),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .statusBarsPadding(),
                contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // 1. Top Header: User Avatar + Online Dot + Title/Subtitle (No bell icon)
                if (showHeader) {
                    item(key = "top_header") {
                        AnalyzerTopHeader(
                            userName = uiState.userName,
                        )
                    }
                }

                // 2. Timeframe Filter Row (هفته گذشته / ۳ ماه گذشته / ماه گذشته)
                item(key = "timeframe_filter") {
                    TimeframeFilterBar(
                        selectedTimeframe = uiState.selectedTimeframe,
                        onTimeframeSelected = { timeframe ->
                            viewModel.selectTimeframe(timeframe)
                        },
                    )
                }

                // 3. AI Smart Analysis Section ("تحلیل هوشمند Ai")
                item(key = "ai_analysis_card") {
                    AiSmartAnalysisCard(
                        paragraphs = uiState.aiInsightParagraphs,
                        insights = uiState.aiInsights,
                    )
                }

                // 4. Key Performance Metrics Grid (تست صحیح، تعداد تست، تعداد آزمون، تست غلط)
                item(key = "performance_metrics") {
                    PerformanceMetricsGrid(
                        correctCount = uiState.correctTestsCount,
                        correctSubtitle = uiState.correctTestsSubtitle,
                        totalTestsCount = uiState.totalTestsCount,
                        totalTestsSubtitle = uiState.totalTestsSubtitle,
                        totalExamsCount = uiState.totalExamsCount,
                        totalExamsSubtitle = uiState.totalExamsSubtitle,
                        wrongCount = uiState.wrongTestsCount,
                        wrongSubtitle = uiState.wrongTestsSubtitle,
                    )
                }

                // 5. Strengths & Weaknesses (نقاط قوت و نقاط ضعف)
                item(key = "strengths_and_weaknesses") {
                    StrengthsAndWeaknessesSection(
                        activeTab = uiState.activeStrengthsTab,
                        strengths = uiState.strengths,
                        weaknesses = uiState.weaknesses,
                        onTabSelected = { tab ->
                            viewModel.selectStrengthsTab(tab)
                        },
                        onViewDetailsClick = {
                            val msg = if (uiState.activeStrengthsTab == AnalysisTabType.STRENGTHS) {
                                "گزارش تفصیلی نقاط قوت در حال بارگذاری است"
                            } else {
                                "گزارش تفصیلی نقاط ضعف در حال بارگذاری است"
                            }
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                    )
                }

                // 6. Daily Study Time Distribution Chart (توزیع زمان مطالعه در طول روز)
                item(key = "study_distribution_chart") {
                    DailyStudyDistributionChart(
                        points = uiState.studyDistribution,
                        peakBadgeText = uiState.peakStudyHoursBadge,
                    )
                }

                // 7. Periodic Reports Banner (گزارش‌های دوره‌ای)
                item(key = "periodic_reports_banner") {
                    PeriodicReportsBanner(
                        onDownloadReport = {
                            Toast.makeText(context, "گزارش تحلیلی با موفقیت دانلود شد", Toast.LENGTH_SHORT).show()
                        },
                        onCompareWithFriends = {
                            navController.navigate("league")
                        },
                    )
                }
            }
        }
    }
}
