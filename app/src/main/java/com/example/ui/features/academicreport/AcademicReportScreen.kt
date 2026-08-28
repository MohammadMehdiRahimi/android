package com.example.ui.features.academicreport

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ui.theme.LocalShetabColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademicReportScreen(
    navController: NavController,
    showHeader: Boolean = true,
    viewModel: AnalyzerViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val colors = LocalShetabColors.current

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFAF9FD))
                .testTag("academic_report_screen"),
            containerColor = Color(0xFFFAF9FD),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .statusBarsPadding(),
                contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // 1. Top Header: User Avatar + Online Dot + Title/Subtitle + Notification Bell
                if (showHeader) {
                    item(key = "top_header") {
                        AnalyzerTopHeader(
                            userName = uiState.userName,
                            unreadNotifications = uiState.unreadNotificationsCount,
                            onNotificationClick = {
                                navController.navigate("notifications")
                            },
                        )
                    }
                }

                // 2. Timeframe Filter Row (هفته گذشته / ماه گذشته / ۳ ماه گذشته)
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

                // 4. Key Performance Metrics Grid (۴ کارت آمار: تعداد آزمون، تست غلط، تست صحیح، تعداد تست)
                item(key = "performance_metrics") {
                    PerformanceMetricsGrid(
                        metrics = uiState.metrics,
                    )
                }

                // 5. Strengths & Weaknesses (نقاط قوت و نقاط ضعف)
                item(key = "strengths_and_weaknesses") {
                    StrengthsAndWeaknessesSection(
                        strengths = uiState.strengths,
                        weaknesses = uiState.weaknesses,
                        onViewStrengthsDetails = {
                            Toast.makeText(context, "گزارش تفصیلی نقاط قوت در حال بارگذاری است", Toast.LENGTH_SHORT).show()
                        },
                        onViewWeaknessesDetails = {
                            Toast.makeText(context, "گزارش تفصیلی نقاط ضعف در حال بارگذاری است", Toast.LENGTH_SHORT).show()
                        },
                    )
                }

                // 6. Daily Study Time Distribution Chart (توزیع زمان مطالعه در طول روز)
                item(key = "study_distribution_chart") {
                    DailyStudyDistributionChart(
                        points = uiState.studyDistribution,
                    )
                }

                // 7. Periodic Reports Banner (گزارش‌های دوره‌ای با دکمه‌های دریافت و مقایسه)
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
