package com.example.ui.features.academicreport

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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
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

// Theme Palette Constants matching UI
val AppPrimaryPurple = Color(0xFF5B42F3)
val AppLightPurpleBg = Color(0xFFF3F0FF)
val AppSoftBorder = Color(0xFFEDE9FE)
val AppTextDark = Color(0xFF0F172A)
val AppTextMuted = Color(0xFF64748B)
val AppTextBody = Color(0xFF334155)

val StatGreen = Color(0xFF10B981)
val StatGreenBg = Color(0xFFDCFCE7)
val StatRed = Color(0xFFEF4444)
val StatRedBg = Color(0xFFFEE2E2)
val StatOrange = Color(0xFFF97316)
val StatOrangeBg = Color(0xFFFFEDD5)
val StatBlue = Color(0xFF0284C7)
val StatBlueBg = Color(0xFFE0F2FE)

/**
 * 1. Top Header Component (Strict RTL, without bell icon)
 */
@Composable
fun AnalyzerTopHeader(
    userName: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // User Profile Avatar with Online Green Dot (RTL Start / Right)
        Box(
            modifier = Modifier.size(52.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, Color(0xFFE2E8F0), CircleShape),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.enb_sina),
                    contentDescription = userName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            // Online Green Status Dot (Bottom-Start in RTL)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(2.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(StatGreen),
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Title & Subtitle Column (RTL Start / Right)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = "تحلیل",
                color = AppTextDark,
                fontFamily = IranSansFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                textAlign = TextAlign.Start,
            )
            Text(
                text = "گزارش کامل پیشرفت شما",
                color = AppTextMuted,
                fontFamily = IranSansFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                textAlign = TextAlign.Start,
            )
        }
    }
}

/**
 * 2. Timeframe Filter Bar (هفته گذشته / ۳ ماه گذشته / ماه گذشته) - Strict RTL
 */
@Composable
fun TimeframeFilterBar(
    selectedTimeframe: AnalyzerTimeframe,
    onTimeframeSelected: (AnalyzerTimeframe) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Tab 1: هفته گذشته (First in RTL - on the right)
        val isWeekSelected = selectedTimeframe == AnalyzerTimeframe.LAST_WEEK
        Surface(
            modifier = Modifier
                .weight(1.3f)
                .height(42.dp)
                .clip(RoundedCornerShape(14.dp))
                .clickable { onTimeframeSelected(AnalyzerTimeframe.LAST_WEEK) }
                .testTag("timeframe_week_tab"),
            shape = RoundedCornerShape(14.dp),
            color = if (isWeekSelected) AppPrimaryPurple else Color.White,
            border = if (isWeekSelected) null else BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shadowElevation = if (isWeekSelected) 2.dp else 0.dp,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                // Calendar icon at start (right in RTL)
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    tint = if (isWeekSelected) Color.White else AppTextMuted,
                    modifier = Modifier.size(17.dp),
                )
                Text(
                    text = "هفته گذشته",
                    color = if (isWeekSelected) Color.White else AppTextMuted,
                    fontFamily = IranSansFontFamily,
                    fontWeight = if (isWeekSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 12.5.sp,
                )
                // Dropdown arrow at end (left in RTL)
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = if (isWeekSelected) Color.White else AppTextMuted,
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        // Tab 2: ۳ ماه گذشته
        val is3MonthsSelected = selectedTimeframe == AnalyzerTimeframe.LAST_3_MONTHS
        Surface(
            modifier = Modifier
                .weight(1.1f)
                .height(42.dp)
                .clip(RoundedCornerShape(14.dp))
                .clickable { onTimeframeSelected(AnalyzerTimeframe.LAST_3_MONTHS) }
                .testTag("timeframe_3months_tab"),
            shape = RoundedCornerShape(14.dp),
            color = if (is3MonthsSelected) AppPrimaryPurple else Color.White,
            border = if (is3MonthsSelected) null else BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shadowElevation = if (is3MonthsSelected) 2.dp else 0.dp,
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "۳ ماه گذشته",
                    color = if (is3MonthsSelected) Color.White else AppTextMuted,
                    fontFamily = IranSansFontFamily,
                    fontWeight = if (is3MonthsSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 12.5.sp,
                )
            }
        }

        // Tab 3: ماه گذشته
        val isMonthSelected = selectedTimeframe == AnalyzerTimeframe.LAST_MONTH
        Surface(
            modifier = Modifier
                .weight(1.1f)
                .height(42.dp)
                .clip(RoundedCornerShape(14.dp))
                .clickable { onTimeframeSelected(AnalyzerTimeframe.LAST_MONTH) }
                .testTag("timeframe_month_tab"),
            shape = RoundedCornerShape(14.dp),
            color = if (isMonthSelected) AppPrimaryPurple else Color.White,
            border = if (isMonthSelected) null else BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shadowElevation = if (isMonthSelected) 2.dp else 0.dp,
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "ماه گذشته",
                    color = if (isMonthSelected) Color.White else AppTextMuted,
                    fontFamily = IranSansFontFamily,
                    fontWeight = if (isMonthSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 12.5.sp,
                )
            }
        }
    }
}

/**
 * 3. AI Smart Analysis Card ("تحلیل هوشمند Ai") - Strict RTL, without learning style
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
            .shadow(2.dp, RoundedCornerShape(24.dp), spotColor = Color(0x155B42F3))
            .testTag("ai_smart_analysis_card"),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = BorderStroke(1.dp, AppSoftBorder),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Header: Title "تحلیل هوشمند" + Purple "Ai" Badge (RTL Start / Right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "تحلیل هوشمند",
                    color = AppTextDark,
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AppPrimaryPurple,
                ) {
                    Text(
                        text = "Ai",
                        color = Color.White,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                    )
                }
            }

            // Middle: Text on RTL Start (Right) + Robot Image on RTL End (Left)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // AI Text Paragraphs on RTL Start / Right
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    horizontalAlignment = Alignment.Start,
                ) {
                    paragraphs.forEach { paragraph ->
                        Text(
                            text = paragraph,
                            color = AppTextBody,
                            fontFamily = IranSansFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            lineHeight = 19.sp,
                            textAlign = TextAlign.Justify,
                        )
                    }
                }

                // Robot Illustration on RTL End / Left
                Box(
                    modifier = Modifier.size(105.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ai_vector),
                        contentDescription = "تحلیل هوشمند هوش مصنوعی",
                        modifier = Modifier.size(95.dp),
                        contentScale = ContentScale.Fit,
                    )

                    // Sparkle Speech Bubble near top corner
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-2).dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AppLightPurpleBg)
                            .border(1.dp, AppSoftBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp),
                    ) {
                        Text(
                            text = "✨",
                            fontSize = 11.sp,
                        )
                    }
                }
            }

            // Bottom: 2 Insight Cards (پیشنهاد ما / بهترین تمرکز)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                insights.forEach { insight ->
                    AiInsightPillCard(
                        item = insight,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun AiInsightPillCard(
    item: AiInsightItem,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF9F8FE),
        border = BorderStroke(1.dp, Color(0xFFEDE8FC)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Icon at start (right in RTL)
            when (item.type) {
                InsightType.RECOMMENDATION -> {
                    Icon(
                        imageVector = Icons.Outlined.GpsFixed,
                        contentDescription = null,
                        tint = StatRed,
                        modifier = Modifier.size(18.dp),
                    )
                }
                InsightType.PEAK_FOCUS -> {
                    Icon(
                        imageVector = Icons.Outlined.Nightlight,
                        contentDescription = null,
                        tint = AppPrimaryPurple,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            // Titles
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = item.title,
                    color = AppPrimaryPurple,
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.5.sp,
                    maxLines = 1,
                    textAlign = TextAlign.Start,
                )
                Text(
                    text = item.value,
                    color = AppTextDark,
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

/**
 * 4. 2x2 Performance Metrics Grid - Strict RTL
 */
@Composable
fun PerformanceMetricsGrid(
    correctCount: Int,
    correctSubtitle: String,
    totalTestsCount: Int,
    totalTestsSubtitle: String,
    totalExamsCount: Int,
    totalExamsSubtitle: String,
    wrongCount: Int,
    wrongSubtitle: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Row 1: Right = تست صحیح, Left = تعداد تست
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Right: تست صحیح (First child in RTL Row)
            SingleMetricCard(
                title = "تست صحیح",
                titleColor = AppTextMuted,
                value = correctCount.toString().toPersianNumber(),
                subtitle = correctSubtitle,
                subtitleColor = AppTextMuted,
                icon = Icons.Outlined.GpsFixed,
                iconTint = AppPrimaryPurple,
                iconBg = AppLightPurpleBg,
                modifier = Modifier.weight(1f),
            )

            // Left: تعداد تست (Second child in RTL Row)
            SingleMetricCard(
                title = "تعداد تست",
                titleColor = AppTextMuted,
                value = totalTestsCount.toString().toPersianNumber(),
                subtitle = totalTestsSubtitle,
                subtitleColor = StatGreen,
                icon = Icons.Outlined.Check,
                iconTint = StatGreen,
                iconBg = StatGreenBg,
                modifier = Modifier.weight(1f),
            )
        }

        // Row 2: Right = تعداد آزمون, Left = تست غلط
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Right: تعداد آزمون (First child in RTL Row)
            SingleMetricCard(
                title = "تعداد آزمون",
                titleColor = AppTextMuted,
                value = totalExamsCount.toString().toPersianNumber(),
                subtitle = totalExamsSubtitle,
                subtitleColor = AppTextMuted,
                icon = Icons.Outlined.Description,
                iconTint = StatBlue,
                iconBg = StatBlueBg,
                modifier = Modifier.weight(1f),
            )

            // Left: تست غلط (Second child in RTL Row)
            SingleMetricCard(
                title = "تست غلط",
                titleColor = StatOrange,
                value = wrongCount.toString().toPersianNumber(),
                subtitle = wrongSubtitle,
                subtitleColor = StatRed,
                icon = Icons.Outlined.Close,
                iconTint = StatOrange,
                iconBg = StatOrangeBg,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SingleMetricCard(
    title: String,
    titleColor: Color,
    value: String,
    subtitle: String,
    subtitleColor: Color,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .shadow(1.dp, RoundedCornerShape(20.dp), spotColor = Color(0x0A000000)),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            // Header Row: Title at start (right), Circular Icon at end (left)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                // Title (Right / Start)
                Text(
                    text = title,
                    color = titleColor,
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                )

                // Icon (Left / End)
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(iconBg),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(15.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Main Counter
            Text(
                text = value,
                color = AppTextDark,
                fontFamily = IranSansFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
            )

            // Subtitle text / trend
            Text(
                text = subtitle,
                color = subtitleColor,
                fontFamily = IranSansFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
    }
}

/**
 * 5. Strengths & Weaknesses Section (نقاط قوت / نقاط ضعف) - Strict RTL
 */
@Composable
fun StrengthsAndWeaknessesSection(
    activeTab: AnalysisTabType,
    strengths: List<SubjectPerformance>,
    weaknesses: List<SubjectPerformance>,
    onTabSelected: (AnalysisTabType) -> Unit,
    onViewDetailsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(1.dp, RoundedCornerShape(22.dp), spotColor = Color(0x0A000000)),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Segmented Header: نقاط قوت (Right/First) vs نقاط ضعف (Left/Second)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Tab 1: نقاط قوت (Right in RTL - first child)
                val isStrengthsActive = activeTab == AnalysisTabType.STRENGTHS
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onTabSelected(AnalysisTabType.STRENGTHS) }
                        .padding(bottom = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = "👍",
                            fontSize = 14.sp,
                        )
                        Text(
                            text = "نقاط قوت",
                            color = if (isStrengthsActive) StatGreen else AppTextMuted,
                            fontFamily = IranSansFontFamily,
                            fontWeight = if (isStrengthsActive) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(3.dp)
                            .clip(RoundedCornerShape(1.5.dp))
                            .background(if (isStrengthsActive) StatGreen else Color.Transparent),
                    )
                }

                // Tab 2: نقاط ضعف (Left in RTL - second child)
                val isWeaknessActive = activeTab == AnalysisTabType.WEAKNESSES
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onTabSelected(AnalysisTabType.WEAKNESSES) }
                        .padding(bottom = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = "👎",
                            fontSize = 14.sp,
                        )
                        Text(
                            text = "نقاط ضعف",
                            color = if (isWeaknessActive) StatRed else AppTextMuted,
                            fontFamily = IranSansFontFamily,
                            fontWeight = if (isWeaknessActive) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(3.dp)
                            .clip(RoundedCornerShape(1.5.dp))
                            .background(if (isWeaknessActive) StatRed else Color.Transparent),
                    )
                }
            }

            // Subject Progress Bars List (Right = Subject Name, Middle = Bar, Left = Percentage)
            val currentItems = if (activeTab == AnalysisTabType.STRENGTHS) strengths else weaknesses
            val activeBarColor = if (activeTab == AnalysisTabType.STRENGTHS) StatGreen else StatRed

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                currentItems.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        // Subject Name on RTL Start / Right
                        Text(
                            text = item.name,
                            color = AppTextDark,
                            fontFamily = IranSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.width(85.dp),
                            textAlign = TextAlign.Start,
                        )

                        // Smooth Horizontal Progress Bar in Middle
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .padding(horizontal = 10.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFEDE9FE)),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(item.percentage / 100f)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(activeBarColor),
                            )
                        }

                        // Percentage on RTL End / Left
                        Text(
                            text = "${item.percentage.toString().toPersianNumber()}٪",
                            color = AppTextDark,
                            fontFamily = IranSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.width(42.dp),
                            textAlign = TextAlign.End,
                        )
                    }
                }
            }

            // Footer: "مشاهده جزئیات" with left arrow on RTL Start (Right)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onViewDetailsClick() }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
            ) {
                Text(
                    text = "مشاهده جزئیات",
                    color = AppPrimaryPurple,
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.5.sp,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = null,
                    tint = AppPrimaryPurple,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

/**
 * 6. Study Time Distribution Chart (توزیع زمان مطالعه در طول روز) - Strict RTL
 */
@Composable
fun DailyStudyDistributionChart(
    points: List<StudyDistributionPoint>,
    peakBadgeText: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(1.dp, RoundedCornerShape(22.dp), spotColor = Color(0x0A000000)),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Header Row: Title & Clock icon on Start (Right), Peak Badge on End (Left)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Right / Start: Title and Clock icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(AppLightPurpleBg),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AccessTime,
                            contentDescription = null,
                            tint = AppPrimaryPurple,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                    Text(
                        text = "توزیع زمان مطالعه در طول روز",
                        color = AppTextDark,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp,
                    )
                }

                // Left / End: Peak Badge "۳ ساعت"
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AppPrimaryPurple,
                ) {
                    Text(
                        text = peakBadgeText,
                        color = Color.White,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                }
            }

            // Spline Canvas Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
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

        val leftMargin = 30.dp.toPx()
        val rightMargin = 20.dp.toPx()
        val topMargin = 28.dp.toPx()
        val bottomMargin = 24.dp.toPx()

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

        // 2. Map coordinates (RTL: 0-4 on right/start, 20-24 on left/end)
        val count = points.size
        val stepX = chartWidth / (count - 1).coerceAtLeast(1)

        val coords = points.mapIndexed { index, point ->
            val x = width - rightMargin - index * stepX
            val y = topMargin + chartHeight * (1f - (point.hours / maxHours).coerceIn(0f, 1f))
            Offset(x, y)
        }

        // 3. Draw gradient area fill and smooth spline curve
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

            // Fill under path
            val fillPath = Path().apply {
                addPath(path)
                lineTo(coords.last().x, topMargin + chartHeight)
                lineTo(coords.first().x, topMargin + chartHeight)
                close()
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        AppPrimaryPurple.copy(alpha = 0.25f),
                        AppPrimaryPurple.copy(alpha = 0.02f),
                    ),
                    startY = topMargin,
                    endY = topMargin + chartHeight,
                ),
            )

            // Stroke line
            drawPath(
                path = path,
                color = AppPrimaryPurple,
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round),
            )

            // Circular nodes
            coords.forEach { offset ->
                drawCircle(
                    color = Color.White,
                    radius = 4.5.dp.toPx(),
                    center = offset,
                )
                drawCircle(
                    color = AppPrimaryPurple,
                    radius = 4.5.dp.toPx(),
                    center = offset,
                    style = Stroke(width = 2.dp.toPx()),
                )
            }
        }
    }

    // Y and X Axis Labels Overlay
    Box(modifier = modifier) {
        // Y-axis Label: "ساعت"
        Text(
            text = "ساعت",
            color = AppTextMuted,
            fontFamily = IranSansFontFamily,
            fontSize = 9.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 2.dp, top = 2.dp),
        )

        // Y-axis Numbers
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 4.dp, top = 18.dp, bottom = 26.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            listOf("۴", "۳", "۲", "۱", "۰").forEach { label ->
                Text(
                    text = label,
                    color = AppTextMuted,
                    fontFamily = IranSansFontFamily,
                    fontSize = 9.5.sp,
                )
            }
        }

        // X-axis Time Slots at Bottom (RTL: 0-4 on right/start, 20-24 on left/end)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 28.dp, end = 12.dp, bottom = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            points.forEach { point ->
                Text(
                    text = point.timeSlot,
                    color = AppTextMuted,
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 9.5.sp,
                )
            }
        }
    }
}

/**
 * 7. Periodic Reports Card (گزارش‌های دوره‌ای) - Strict RTL
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
            .shadow(1.dp, RoundedCornerShape(22.dp), spotColor = Color(0x105B42F3))
            .testTag("periodic_reports_banner"),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFFFBFBFE),
        border = BorderStroke(1.dp, Color(0xFFECE7FE)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Text & Buttons Column on Right (RTL Start)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = "گزارش‌های دوره‌ای",
                    color = AppTextDark,
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Start,
                )
                Text(
                    text = "گزارش‌های هفتگی و ماهانه خود را دانلود کنید و پیشرفتتان را با دوستان مقایسه کنید.",
                    color = AppTextMuted,
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.5.sp,
                    lineHeight = 17.sp,
                    textAlign = TextAlign.Start,
                )

                // Buttons Row: Solid Download (Right) + Outlined Compare (Left)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Solid "دریافت گزارش" Button (Purple - Right)
                    Button(
                        onClick = onDownloadReport,
                        modifier = Modifier
                            .weight(1.1f)
                            .height(38.dp)
                            .testTag("download_report_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppPrimaryPurple),
                        contentPadding = PaddingValues(horizontal = 4.dp),
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
                                fontSize = 11.sp,
                            )
                        }
                    }

                    // Outlined "مقایسه با دوستان" Button (Left)
                    Surface(
                        modifier = Modifier
                            .weight(1.2f)
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
                                tint = AppPrimaryPurple,
                                modifier = Modifier.size(15.dp),
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "مقایسه با دوستان",
                                color = AppTextDark,
                                fontFamily = IranSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                            )
                        }
                    }
                }
            }

            // Illustration on Left (RTL End)
            Box(
                modifier = Modifier
                    .size(75.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppLightPurpleBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Assignment,
                    contentDescription = null,
                    tint = AppPrimaryPurple,
                    modifier = Modifier.size(38.dp),
                )
            }
        }
    }
}
