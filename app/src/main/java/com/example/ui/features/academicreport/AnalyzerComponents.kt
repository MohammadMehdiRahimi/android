package com.example.ui.features.academicreport

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.core.toPersianNumber
import com.example.ui.theme.IranSansFontFamily

// Theme Palette Constants for Analyzer Screen
val AnalyzerPurple = Color(0xFF8B5CF6)
val AnalyzerPurpleDark = Color(0xFF6D28D9)
val AnalyzerPurpleLight = Color(0xFFF3EEFF)
val AnalyzerPurpleBorder = Color(0xFFDDD6FE)
val AnalyzerNavy = Color(0xFF1E1B4B)
val AnalyzerNavyMuted = Color(0xFF475569)
val AnalyzerGraySub = Color(0xFF64748B)
val AnalyzerCardBg = Color.White
val AnalyzerCardBorder = Color(0xFFF1F5F9)

val MetricGreen = Color(0xFF10B981)
val MetricGreenBg = Color(0xFFD1FAE5)
val MetricRed = Color(0xFFEF4444)
val MetricRedBg = Color(0xFFFEE2E2)
val MetricOrange = Color(0xFFF59E0B)
val MetricOrangeBg = Color(0xFFFEF3C7)
val MetricBlue = Color(0xFF3B82F6)
val MetricBlueBg = Color(0xFFDBEAFE)

/**
 * 1. Top Header Component
 */
@Composable
fun AnalyzerTopHeader(
    userName: String,
    unreadNotifications: Int,
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Right in RTL (1st child): User Profile Avatar with Online Badge
        Box(
            modifier = Modifier.size(52.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color(0xFFE2E8F0), CircleShape),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.enb_sina),
                    contentDescription = userName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            // Online Green Status Dot (Bottom-End in RTL)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(13.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(2.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(MetricGreen),
                )
            }
        }

        // Center (2nd child): Title & Subtitle
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "تحلیلگر",
                color = AnalyzerNavy,
                fontFamily = IranSansFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "گزارش کامل پیشرفت شما",
                color = AnalyzerGraySub,
                fontFamily = IranSansFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
            )
        }

        // Left in RTL (3rd child): Notification Bell Icon Box
        Surface(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .clickable { onNotificationClick() }
                .testTag("analyzer_notification_btn"),
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shadowElevation = 1.dp,
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "اعلانات",
                    tint = AnalyzerNavy,
                    modifier = Modifier.size(22.dp),
                )
                if (unreadNotifications > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 9.dp, end = 9.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(AnalyzerPurple),
                    )
                }
            }
        }
    }
}

/**
 * 2. Timeframe Filter Bar (هفته گذشته / ماه گذشته / ۳ ماه گذشته)
 */
@Composable
fun TimeframeFilterBar(
    selectedTimeframe: AnalyzerTimeframe,
    onTimeframeSelected: (AnalyzerTimeframe) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isDropdownOpen by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Right in RTL (1st child): Secondary Text Buttons for Other Timeframes
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val allOptions = listOf(AnalyzerTimeframe.LAST_3_MONTHS, AnalyzerTimeframe.LAST_MONTH, AnalyzerTimeframe.LAST_WEEK)
            val otherTimeframes = allOptions.filter { it != selectedTimeframe }
            otherTimeframes.forEach { timeframe ->
                Text(
                    text = timeframe.title,
                    color = AnalyzerGraySub,
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.5.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onTimeframeSelected(timeframe) }
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                )
            }
        }

        // Left in RTL (2nd child): Active Filter Pill with Dropdown / Calendar Icon
        Box {
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { isDropdownOpen = !isDropdownOpen }
                    .testTag("timeframe_dropdown_trigger"),
                shape = RoundedCornerShape(16.dp),
                color = AnalyzerPurpleLight,
                border = BorderStroke(1.dp, AnalyzerPurpleBorder),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        tint = AnalyzerPurple,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = selectedTimeframe.title,
                        color = AnalyzerPurple,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = AnalyzerPurple,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }

            DropdownMenu(
                expanded = isDropdownOpen,
                onDismissRequest = { isDropdownOpen = false },
                modifier = Modifier.background(Color.White),
            ) {
                AnalyzerTimeframe.values().forEach { timeframe ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = timeframe.title,
                                fontFamily = IranSansFontFamily,
                                fontWeight = if (timeframe == selectedTimeframe) FontWeight.Bold else FontWeight.Normal,
                                color = if (timeframe == selectedTimeframe) AnalyzerPurple else AnalyzerNavy,
                                fontSize = 13.sp,
                            )
                        },
                        onClick = {
                            onTimeframeSelected(timeframe)
                            isDropdownOpen = false
                        },
                    )
                }
            }
        }
    }
}

/**
 * 3. AI Smart Analysis Card ("تحلیل هوشمند Ai")
 */
@Composable
fun AiSmartAnalysisCard(
    paragraphs: List<String>,
    insights: List<AiInsightItem>,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(2.dp, RoundedCornerShape(26.dp), spotColor = Color(0x1A8B5CF6))
            .testTag("ai_smart_analysis_card"),
        shape = RoundedCornerShape(26.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFECE7FE)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFFAF7FF),
                            Color(0xFFFFFFFF),
                        )
                    )
                )
                .padding(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // Top Header Row: "تحلیل هوشمند" + Purple "Ai" Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "تحلیل هوشمند",
                        color = AnalyzerNavy,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF8B5CF6),
                    ) {
                        Text(
                            text = "Ai",
                            color = Color.White,
                            fontFamily = IranSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        )
                    }
                }

                // Middle Row: Right Text Insights + Left 3D Robot Illustration
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Right in RTL (1st child): Bullet-pointed AI Insights Text
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        paragraphs.forEach { paragraph ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 7.dp, start = 2.dp, end = 6.dp)
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(AnalyzerPurple),
                                )
                                Text(
                                    text = paragraph,
                                    color = AnalyzerNavyMuted,
                                    fontFamily = IranSansFontFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 11.5.sp,
                                    lineHeight = 18.sp,
                                )
                            }
                        }
                    }

                    // Left in RTL (2nd child): Vector AI Robot Avatar & Sparkle Bubble
                    Box(
                        modifier = Modifier.size(100.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        AiRobotVectorIllustration(
                            modifier = Modifier.size(92.dp),
                        )
                    }
                }

                // Bottom Row: 3 Insight Chips (پیشنهاد ما, بیشترین تمرکز, سبک یادگیری)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    insights.forEach { insight ->
                        AiInsightChip(
                            item = insight,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

/**
 * Single AI Insight Chip Item
 */
@Composable
fun AiInsightChip(
    item: AiInsightItem,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .shadow(0.5.dp, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFF9F7FE),
        border = BorderStroke(1.dp, Color(0xFFEDE8FC)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Icon & Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                when (item.type) {
                    InsightType.RECOMMENDATION -> {
                        Icon(
                            imageVector = Icons.Outlined.GpsFixed,
                            contentDescription = null,
                            tint = Color(0xFFF43F5E),
                            modifier = Modifier.size(12.dp),
                        )
                    }
                    InsightType.PEAK_FOCUS -> {
                        Icon(
                            imageVector = Icons.Outlined.Nightlight,
                            contentDescription = null,
                            tint = AnalyzerPurple,
                            modifier = Modifier.size(12.dp),
                        )
                    }
                    InsightType.LEARNING_STYLE -> {
                        Icon(
                            imageVector = Icons.Outlined.Visibility,
                            contentDescription = null,
                            tint = Color(0xFF6366F1),
                            modifier = Modifier.size(12.dp),
                        )
                    }
                }
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = item.title,
                    color = AnalyzerGraySub,
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 9.5.sp,
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.value,
                color = AnalyzerNavy,
                fontFamily = IranSansFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 10.5.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/**
 * 4. Performance Metrics Grid (۴ کارت آمار کلیدی)
 */
@Composable
fun PerformanceMetricsGrid(
    metrics: List<MetricCardData>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        metrics.forEach { metric ->
            PerformanceMetricCard(
                metric = metric,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
fun PerformanceMetricCard(
    metric: MetricCardData,
    modifier: Modifier = Modifier,
) {
    val (iconBgColor, iconTintColor, iconVector) = when (metric.iconType) {
        MetricIconType.EXAM_COUNT -> Triple(MetricBlueBg, MetricBlue, Icons.Outlined.Description)
        MetricIconType.WRONG_TESTS -> Triple(MetricOrangeBg, MetricOrange, Icons.Outlined.Close)
        MetricIconType.CORRECT_TESTS -> Triple(AnalyzerPurpleLight, AnalyzerPurple, Icons.Outlined.GpsFixed)
        MetricIconType.TOTAL_TESTS -> Triple(MetricGreenBg, MetricGreen, Icons.Outlined.Check)
    }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .shadow(1.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0A000000)),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFFFBFBFE),
        border = BorderStroke(1.dp, Color(0xFFF1F4F9)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            // Header Row: Title & Circular Icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(iconBgColor),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        tint = iconTintColor,
                        modifier = Modifier.size(11.dp),
                    )
                }
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = metric.title,
                    color = AnalyzerGraySub,
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 9.sp,
                    maxLines = 1,
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Big Number
            Text(
                text = metric.value.toPersianNumber(),
                color = AnalyzerNavy,
                fontFamily = IranSansFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
            )

            // Subtitle / Trend
            Text(
                text = metric.subtitle,
                color = when (metric.trend) {
                    MetricTrend.POSITIVE -> MetricGreen
                    MetricTrend.NEGATIVE -> MetricRed
                    MetricTrend.NEUTRAL -> AnalyzerGraySub
                },
                fontFamily = IranSansFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 8.5.sp,
                maxLines = 1,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/**
 * 5. Strengths & Weaknesses Section (نقاط قوت و نقاط ضعف)
 */
@Composable
fun StrengthsAndWeaknessesSection(
    strengths: List<SubjectPerformance>,
    weaknesses: List<SubjectPerformance>,
    onViewStrengthsDetails: () -> Unit,
    onViewWeaknessesDetails: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Right in RTL (1st child): Weaknesses Card ("نقاط ضعف" with Red Header)
        PerformanceCardGroup(
            title = "نقاط ضعف",
            titleColor = Color(0xFFDC2626),
            iconBg = Color(0xFFFEE2E2),
            iconVector = Icons.Filled.ThumbDown,
            barColor = Color(0xFFEF4444),
            items = weaknesses,
            actionTextColor = Color(0xFFEF4444),
            onDetailsClick = onViewWeaknessesDetails,
            modifier = Modifier.weight(1f),
        )

        // Left in RTL (2nd child): Strengths Card ("نقاط قوت" with Green Header)
        PerformanceCardGroup(
            title = "نقاط قوت",
            titleColor = Color(0xFF059669),
            iconBg = Color(0xFFD1FAE5),
            iconVector = Icons.Filled.ThumbUp,
            barColor = Color(0xFF10B981),
            items = strengths,
            actionTextColor = AnalyzerNavy,
            onDetailsClick = onViewStrengthsDetails,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
fun PerformanceCardGroup(
    title: String,
    titleColor: Color,
    iconBg: Color,
    iconVector: ImageVector,
    barColor: Color,
    items: List<SubjectPerformance>,
    actionTextColor: Color,
    onDetailsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .shadow(1.dp, RoundedCornerShape(20.dp), spotColor = Color(0x0A000000)),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFFCFCFE),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Header: Title + Thumbs Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(iconBg),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        tint = titleColor,
                        modifier = Modifier.size(13.dp),
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    color = titleColor,
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                )
            }

            // Subject Rows with Segmented Bar
            items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    // Subject Name (Right in RTL)
                    Text(
                        text = item.name,
                        color = AnalyzerNavy,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.5.sp,
                        modifier = Modifier.weight(0.9f),
                        maxLines = 1,
                    )

                    // Segmented Progress Bar (Middle)
                    SegmentedProgressBar(
                        percentage = item.percentage,
                        activeColor = barColor,
                        modifier = Modifier
                            .weight(1.3f)
                            .height(6.dp),
                    )

                    // Percentage Text (Left in RTL)
                    Text(
                        text = "${item.percentage.toPersianNumber()}٪",
                        color = AnalyzerNavy,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(0.7f),
                    )
                }
            }

            // Footer: "مشاهده جزئیات" Link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onDetailsClick() }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "مشاهده جزئیات",
                    color = actionTextColor,
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                )
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = null,
                    tint = actionTextColor,
                    modifier = Modifier.size(15.dp),
                )
            }
        }
    }
}

/**
 * Segmented Progress Bar with 3 segments
 */
@Composable
fun SegmentedProgressBar(
    percentage: Int,
    activeColor: Color,
    modifier: Modifier = Modifier,
) {
    val trackBg = Color(0xFFF1F5F9)
    Canvas(modifier = modifier) {
        val totalWidth = size.width
        val barHeight = size.height
        val segmentGap = 2.5.dp.toPx()
        val numSegments = 3
        val segmentWidth = (totalWidth - (numSegments - 1) * segmentGap) / numSegments

        val filledFraction = (percentage / 100f).coerceIn(0f, 1f)
        val activeWidth = totalWidth * filledFraction

        for (i in 0 until numSegments) {
            val startX = i * (segmentWidth + segmentGap)

            // Draw track
            drawRoundRect(
                color = trackBg,
                topLeft = Offset(startX, 0f),
                size = Size(segmentWidth, barHeight),
                cornerRadius = CornerRadius(barHeight / 2, barHeight / 2),
            )

            // Draw active fill
            if (activeWidth > startX) {
                val fillW = (activeWidth - startX).coerceAtMost(segmentWidth)
                drawRoundRect(
                    color = activeColor,
                    topLeft = Offset(startX, 0f),
                    size = Size(fillW, barHeight),
                    cornerRadius = CornerRadius(barHeight / 2, barHeight / 2),
                )
            }
        }
    }
}

/**
 * 6. Daily Study Time Distribution Chart (توزیع زمان مطالعه در طول روز)
 */
@Composable
fun DailyStudyDistributionChart(
    points: List<StudyDistributionPoint>,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(1.dp, RoundedCornerShape(24.dp), spotColor = Color(0x0A000000)),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Header Row: Clock Icon + Title (Right in RTL)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(AnalyzerPurpleLight),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = null,
                        tint = AnalyzerPurple,
                        modifier = Modifier.size(16.dp),
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "توزیع زمان مطالعه در طول روز",
                    color = AnalyzerNavy,
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.5.sp,
                )
            }

            // Spline Chart with Peak Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
            ) {
                DailyStudyChartCanvas(
                    points = points,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
fun DailyStudyChartCanvas(
    points: List<StudyDistributionPoint>,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val leftMargin = 32.dp.toPx()
        val rightMargin = 20.dp.toPx()
        val topMargin = 36.dp.toPx()
        val bottomMargin = 28.dp.toPx()

        val chartWidth = width - leftMargin - rightMargin
        val chartHeight = height - topMargin - bottomMargin

        val maxHours = 4.0f
        val yLevels = listOf(4, 3, 2, 1, 0)

        // 1. Draw horizontal grid lines
        yLevels.forEach { level ->
            val yPos = topMargin + chartHeight * (1f - (level / maxHours))
            drawLine(
                color = Color(0xFFF1F5F9),
                start = Offset(leftMargin, yPos),
                end = Offset(width - rightMargin, yPos),
                strokeWidth = 1.dp.toPx(),
            )
        }

        // 2. Map data points to Canvas coordinates (RTL order: index 0 at right, index count-1 at left)
        val count = points.size
        val stepX = chartWidth / (count - 1).coerceAtLeast(1)

        val coords = points.mapIndexed { index, point ->
            val x = width - rightMargin - index * stepX
            val y = topMargin + chartHeight * (1f - (point.hours / maxHours).coerceIn(0f, 1f))
            Offset(x, y)
        }

        // 3. Draw smooth cubic curve
        if (coords.isNotEmpty()) {
            val path = Path().apply {
                moveTo(coords[0].x, coords[0].y)
                for (i in 0 until coords.size - 1) {
                    val p0 = coords[i]
                    val p1 = coords[i + 1]
                    val controlX1 = p0.x + (p1.x - p0.x) / 2f
                    val controlY1 = p0.y
                    val controlX2 = p0.x + (p1.x - p0.x) / 2f
                    val controlY2 = p1.y
                    cubicTo(controlX1, controlY1, controlX2, controlY2, p1.x, p1.y)
                }
            }

            drawPath(
                path = path,
                color = AnalyzerPurple,
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round),
            )

            // 4. Draw circular nodes
            coords.forEachIndexed { index, offset ->
                drawCircle(
                    color = Color.White,
                    radius = 4.5.dp.toPx(),
                    center = offset,
                )
                drawCircle(
                    color = AnalyzerPurple,
                    radius = 4.5.dp.toPx(),
                    center = offset,
                    style = Stroke(width = 2.dp.toPx()),
                )
            }
        }
    }

    // Compose Overlay for Y-Labels, X-Labels, and Peak Badge
    Box(modifier = modifier) {
        // Y-axis Label: "ساعت"
        Text(
            text = "ساعت",
            color = AnalyzerGraySub,
            fontFamily = IranSansFontFamily,
            fontSize = 9.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 2.dp, top = 2.dp),
        )

        // Y-axis Numbers (4, 3, 2, 1, 0)
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 4.dp, top = 24.dp, bottom = 30.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            listOf("۴", "۳", "۲", "۱", "۰").forEach { label ->
                Text(
                    text = label,
                    color = AnalyzerGraySub,
                    fontFamily = IranSansFontFamily,
                    fontSize = 9.5.sp,
                )
            }
        }

        // X-axis Time Slots at Bottom (RTL: 0-4 on right, 20-24 on left)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 24.dp, end = 12.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            points.forEach { point ->
                Text(
                    text = point.timeSlot,
                    color = AnalyzerGraySub,
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                )
            }
        }

        // Peak Tooltip Badge: "۳ ساعت" pointing at the peak node (12-16)
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(x = 12.dp, y = 6.dp),
            shape = RoundedCornerShape(12.dp),
            color = AnalyzerPurple,
            shadowElevation = 2.dp,
        ) {
            Text(
                text = "۳ ساعت",
                color = Color.White,
                fontFamily = IranSansFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 10.5.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            )
        }
    }
}

/**
 * 7. Periodic Reports Banner (گزارش‌های دوره‌ای)
 */
@Composable
fun PeriodicReportsBanner(
    onDownloadReport: () -> Unit,
    onCompareWithFriends: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(1.dp, RoundedCornerShape(24.dp), spotColor = Color(0x108B5CF6))
            .testTag("periodic_reports_banner"),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFF7F5FE),
        border = BorderStroke(1.dp, Color(0xFFECE7FE)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Right in RTL (1st child): Text & Action Buttons
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp, end = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "گزارش‌های دوره‌ای",
                    color = AnalyzerNavy,
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                )
                Text(
                    text = "گزارش‌های هفتگی و ماهانه خود را دانلود کنید و پیشرفتتان را با دوستان مقایسه کنید.",
                    color = AnalyzerNavyMuted,
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    lineHeight = 17.sp,
                )

                // Buttons Row: Solid Download + Outlined Compare
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Outlined "مقایسه با دوستان" Button
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onCompareWithFriends() }
                            .testTag("compare_with_friends_btn"),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFDDD6FE)),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Group,
                                contentDescription = null,
                                tint = AnalyzerPurple,
                                modifier = Modifier.size(15.dp),
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "مقایسه با دوستان",
                                color = AnalyzerNavy,
                                fontFamily = IranSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp,
                            )
                        }
                    }

                    // Solid "دریافت گزارش" Button
                    Button(
                        onClick = onDownloadReport,
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .testTag("download_report_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AnalyzerPurple),
                        contentPadding = PaddingValues(horizontal = 6.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.FileDownload,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(15.dp),
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "دریافت گزارش",
                                color = Color.White,
                                fontFamily = IranSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp,
                            )
                        }
                    }
                }
            }

            // Left in RTL (2nd child): 3D Clipboard Illustration
            Box(
                modifier = Modifier.size(86.dp),
                contentAlignment = Alignment.Center,
            ) {
                ReportClipboardIllustration(
                    modifier = Modifier.size(80.dp),
                )
            }
        }
    }
}

/**
 * Beautiful Vector 3D AI Robot Illustration with glowing eyes and sparkle bubble
 */
@Composable
fun AiRobotVectorIllustration(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        // Glowing purple halo circle behind robot
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color(0xFFE9D5FF).copy(alpha = 0.8f),
                            Color(0xFFF3E8FF).copy(alpha = 0.3f),
                            Color.Transparent,
                        )
                    )
                ),
        )

        Canvas(modifier = Modifier.size(68.dp)) {
            val w = size.width
            val h = size.height

            // 1. Robot Head (Glossy white capsule shape)
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFFFFFFF), Color(0xFFF3F4F6))
                ),
                topLeft = Offset(w * 0.18f, h * 0.22f),
                size = Size(w * 0.64f, h * 0.48f),
                cornerRadius = CornerRadius(w * 0.22f, h * 0.22f),
            )
            // Head subtle border
            drawRoundRect(
                color = Color(0xFFE5E7EB),
                topLeft = Offset(w * 0.18f, h * 0.22f),
                size = Size(w * 0.64f, h * 0.48f),
                cornerRadius = CornerRadius(w * 0.22f, h * 0.22f),
                style = Stroke(width = 1.2.dp.toPx()),
            )

            // 2. Robot Ears/Antennas with purple glow rings
            drawRoundRect(
                color = Color(0xFF8B5CF6),
                topLeft = Offset(w * 0.11f, h * 0.34f),
                size = Size(w * 0.08f, h * 0.24f),
                cornerRadius = CornerRadius(w * 0.04f, h * 0.04f),
            )
            drawRoundRect(
                color = Color(0xFF8B5CF6),
                topLeft = Offset(w * 0.81f, h * 0.34f),
                size = Size(w * 0.08f, h * 0.24f),
                cornerRadius = CornerRadius(w * 0.04f, h * 0.04f),
            )

            // 3. Dark Purple Visor Face
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF1E1B4B), Color(0xFF312E81))
                ),
                topLeft = Offset(w * 0.25f, h * 0.28f),
                size = Size(w * 0.50f, h * 0.36f),
                cornerRadius = CornerRadius(w * 0.14f, h * 0.14f),
            )

            // 4. Glowing Blue-Purple Digital Eyes
            drawRoundRect(
                color = Color(0xFFC084FC),
                topLeft = Offset(w * 0.32f, h * 0.38f),
                size = Size(w * 0.12f, h * 0.15f),
                cornerRadius = CornerRadius(w * 0.06f, h * 0.06f),
            )
            drawRoundRect(
                color = Color(0xFFC084FC),
                topLeft = Offset(w * 0.56f, h * 0.38f),
                size = Size(w * 0.12f, h * 0.15f),
                cornerRadius = CornerRadius(w * 0.06f, h * 0.06f),
            )

            // 5. Robot Upper Torso/Body
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFFFFFFF), Color(0xFFE5E7EB))
                ),
                topLeft = Offset(w * 0.26f, h * 0.68f),
                size = Size(w * 0.48f, h * 0.28f),
                cornerRadius = CornerRadius(w * 0.12f, h * 0.12f),
            )
        }

        // Sparkle Speech Bubble on top right of the robot
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 4.dp, y = (-2).dp),
            shape = RoundedCornerShape(10.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFDDD6FE)),
            shadowElevation = 2.dp,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "✦",
                    color = AnalyzerPurple,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

/**
 * 3D Styled Clipboard Illustration with chart and checkmarks
 */
@Composable
fun ReportClipboardIllustration(
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // 1. Purple Clipboard Board
        drawRoundRect(
            brush = Brush.verticalGradient(
                listOf(Color(0xFFEDE9FE), Color(0xFFDDD6FE))
            ),
            topLeft = Offset(w * 0.15f, h * 0.15f),
            size = Size(w * 0.70f, h * 0.80f),
            cornerRadius = CornerRadius(w * 0.12f, h * 0.12f),
        )

        // 2. White Paper Sheet
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(w * 0.22f, h * 0.22f),
            size = Size(w * 0.56f, h * 0.68f),
            cornerRadius = CornerRadius(w * 0.08f, h * 0.08f),
        )

        // 3. Top Metal Clip
        drawRoundRect(
            color = Color(0xFF8B5CF6),
            topLeft = Offset(w * 0.35f, h * 0.08f),
            size = Size(w * 0.30f, h * 0.12f),
            cornerRadius = CornerRadius(w * 0.04f, h * 0.04f),
        )

        // 4. Little Bar Chart on paper
        val chartY = h * 0.62f
        val barW = w * 0.08f
        // Bar 1
        drawRoundRect(
            color = Color(0xFFC4B5FD),
            topLeft = Offset(w * 0.30f, chartY - h * 0.12f),
            size = Size(barW, h * 0.18f),
            cornerRadius = CornerRadius(w * 0.02f, h * 0.02f),
        )
        // Bar 2
        drawRoundRect(
            color = Color(0xFF8B5CF6),
            topLeft = Offset(w * 0.44f, chartY - h * 0.22f),
            size = Size(barW, h * 0.28f),
            cornerRadius = CornerRadius(w * 0.02f, h * 0.02f),
        )
        // Bar 3
        drawRoundRect(
            color = Color(0xFFA78BFA),
            topLeft = Offset(w * 0.58f, chartY - h * 0.16f),
            size = Size(barW, h * 0.22f),
            cornerRadius = CornerRadius(w * 0.02f, h * 0.02f),
        )
    }
}

