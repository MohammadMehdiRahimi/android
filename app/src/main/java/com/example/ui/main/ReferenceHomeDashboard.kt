package com.example.ui.main

import android.graphics.Typeface
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.R
import com.example.network.ApiClient
import com.example.network.NetworkResult
import com.example.network.PerformanceBucketDto
import com.example.network.ProgressDashboardBodyDto
import com.example.network.TokenManager
import com.example.network.safeApiCall
import com.example.ui.core.components.NetworkErrorView
import com.example.ui.core.components.shimmerEffect
import com.example.ui.core.toPersianNumber
import com.example.ui.theme.IranSansFontFamily

private val HomeNavy = Color(0xFF1E293B)
private val HomeMuted = Color(0xFFA0A8C0)
private val HomePurple = Color(0xFF7656F5)

enum class DashboardMetricType(val title: String, val unitFa: String) {
    POINTS("امتیاز", "امتیاز"),
    HOURS("ساعت", "ساعت"),
    TESTS("تست", "تست"),
}

@Composable
fun ReferenceHomeDashboard(
    navController: NavController,
    isGuest: Boolean,
    onLoginClick: () -> Unit,
) {
    var dashboard by remember { mutableStateOf<ProgressDashboardBodyDto?>(null) }
    var range by remember { mutableStateOf("LAST_7_DAYS") }
    var buckets by remember { mutableStateOf<List<PerformanceBucketDto>>(emptyList()) }
    var loading by remember { mutableStateOf(!isGuest) }
    var error by remember { mutableStateOf<String?>(null) }
    var unreadCount by remember { mutableIntStateOf(0) }
    var reload by remember { mutableIntStateOf(0) }

    LifecycleResumeEffect(Unit) {
        if (!isGuest) reload++
        onPauseOrDispose { }
    }

    LaunchedEffect(isGuest, range, reload) {
        if (isGuest) return@LaunchedEffect
        loading = true
        error = null
        if (dashboard == null || reload > 0) {
            when (val result = safeApiCall { ApiClient.apiService.getProgressDashboard() }) {
                is NetworkResult.Success -> dashboard = result.data.body
                is NetworkResult.Error -> error = result.message
                else -> Unit
            }
            when (val result = safeApiCall { ApiClient.apiService.getUnreadNotificationCount() }) {
                is NetworkResult.Success -> unreadCount = result.data.body?.count ?: 0
                else -> Unit
            }
        }
        when (val result = safeApiCall { ApiClient.apiService.getPerformance(range) }) {
            is NetworkResult.Success -> buckets = result.data.body?.buckets.orEmpty()
            is NetworkResult.Error -> error = result.message
            else -> Unit
        }
        loading = false
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            HomeTopHeader(
                isGuest = isGuest,
                onLoginClick = onLoginClick,
                unreadCount = unreadCount,
                onNotificationsClick = { navController.navigate("notifications") },
            )

            if (error != null && dashboard == null && !isGuest && !loading) {
                NetworkErrorView(
                    title = "عدم برقراری ارتباط با سرور",
                    description = error ?: "لطفاً اتصال اینترنت خود را بررسی نمایید و مجدداً تلاش کنید.",
                    fullScreen = false,
                    isRetrying = loading,
                    onRetry = { reload++ }
                )
            } else {
                PerformanceChartCard(
                    range = range,
                    buckets = buckets,
                    loading = loading,
                    onRangeChange = { range = it },
                )
            }

            HomeFeatureGrid(navController = navController)
        }
    }
}

@Composable
private fun HomeTopHeader(
    isGuest: Boolean,
    onLoginClick: () -> Unit,
    unreadCount: Int,
    onNotificationsClick: () -> Unit,
) {
    val context = LocalContext.current
    val tokenManager = remember(context) { ApiClient.getTokenManager() ?: TokenManager(context) }
    var displayName by remember { mutableStateOf(tokenManager.getUserFullName()) }
    var displayTitle by remember { mutableStateOf(tokenManager.getUserTitle()) }
    var profileImageUrl by remember { mutableStateOf(tokenManager.getProfileImageUrl()) }

    LaunchedEffect(isGuest) {
        if (!isGuest) {
            safeApiCall { ApiClient.apiService.checkIn() }
            when (val result = safeApiCall { ApiClient.apiService.getMe() }) {
                is NetworkResult.Success -> result.data.body?.let { profile ->
                    displayName = profile.fullName?.trim()
                    displayTitle = profile.progression?.title?.nameFa
                    profileImageUrl = profile.profileImageUrl
                    tokenManager.saveUserData(
                        profile.id ?: tokenManager.getUserId(),
                        profile.phone ?: tokenManager.getUserPhone(),
                        profile.role ?: tokenManager.getUserRole(),
                        displayName,
                    )
                    tokenManager.saveProfileData(
                        displayName,
                        displayTitle,
                        profile.profileImageUrl,
                        profile.progression?.points,
                    )
                    tokenManager.updateSessionExpiry(profile.sessionExpiresAt)
                }
                else -> Unit
            }
        }
    }

    // In RTL, the first child in Row is placed at the START (RIGHT side of screen)
    // The second child is placed at the END (LEFT side of screen)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp, bottom = 0.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // 1. Profile Avatar + User Name (Start / Right side)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.clickable(enabled = isGuest, onClick = onLoginClick)
        ) {
            // Profile Avatar with User Icon fallback and Online status green dot
            Box(modifier = Modifier.size(48.dp)) {
                if (!profileImageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = ApiClient.resolveUrl(profileImageUrl),
                        contentDescription = "پروفایل",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color(0xFFEDE9FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "پروفایل کاربر",
                            tint = HomePurple,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
                // Online status indicator dot
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 1.dp, y = (-1).dp)
                        .size(12.dp)
                        .background(Color(0xFF00C853), CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                )
            }

            // User Name only (Clean & prominent without title subtitle)
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = if (isGuest) "مهمان شتاب" else (displayName?.takeIf { it.isNotBlank() } ?: "دانش‌آموز شتاب"),
                    color = HomeNavy,
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        // 2. Notification Bell (End / Left side) - Soft, minimal, without shadow and border
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1F3F9))
                .clickable(enabled = !isGuest, onClick = onNotificationsClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Default.NotificationsNone,
                contentDescription = "اعلان‌ها",
                tint = HomeNavy,
                modifier = Modifier.size(22.dp)
            )
            if (unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(5.dp)
                        .size(15.dp)
                        .background(HomePurple, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        if (unreadCount > 9) "۹+" else unreadCount.toString().toPersianNumber(),
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = IranSansFontFamily,
                    )
                }
            }
        }
    }
}

@Composable
private fun PerformanceChartCard(
    range: String,
    buckets: List<PerformanceBucketDto>,
    loading: Boolean,
    onRangeChange: (String) -> Unit,
) {
    var rangeDropdownExpanded by remember { mutableStateOf(false) }
    var metricDropdownExpanded by remember { mutableStateOf(false) }
    var selectedMetric by remember { mutableStateOf(DashboardMetricType.POINTS) }

    val rangeTitles = mapOf(
        "LAST_7_DAYS" to "هفته گذشته",
        "LAST_30_DAYS" to "ماه گذشته",
        "TODAY" to "امروز",
        "LAST_12_MONTHS" to "سال گذشته"
    )

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFBFE)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = null,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp, bottom = 14.dp, start = 14.dp, end = 14.dp)
        ) {
            // Header: Title & Dropdown Filters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Title (Right in RTL)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Default.ShowChart,
                        contentDescription = null,
                        tint = HomePurple,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "نمای کلی",
                        color = HomeNavy,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                // Dropdown Pills (Left in RTL)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // 1. Metric Type Filter (امتیاز / ساعت / تست)
                    Box {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF1F3F9))
                                .clickable { metricDropdownExpanded = true }
                                .padding(horizontal = 9.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = selectedMetric.title,
                                color = HomeNavy,
                                fontFamily = IranSansFontFamily,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = "نوع فیلتر",
                                tint = HomeNavy,
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = metricDropdownExpanded,
                            onDismissRequest = { metricDropdownExpanded = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            DashboardMetricType.values().forEach { metric ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            metric.title,
                                            fontFamily = IranSansFontFamily,
                                            fontSize = 12.sp,
                                            fontWeight = if (selectedMetric == metric) FontWeight.Bold else FontWeight.Normal,
                                            color = if (selectedMetric == metric) HomePurple else HomeNavy
                                        )
                                    },
                                    onClick = {
                                        selectedMetric = metric
                                        metricDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // 2. Time Range Filter (هفته گذشته و...)
                    Box {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF1F3F9))
                                .clickable { rangeDropdownExpanded = true }
                                .padding(horizontal = 9.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = rangeTitles[range] ?: "هفته گذشته",
                                color = HomeNavy,
                                fontFamily = IranSansFontFamily,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = "انتخاب بازه",
                                tint = HomeNavy,
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = rangeDropdownExpanded,
                            onDismissRequest = { rangeDropdownExpanded = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            rangeTitles.forEach { (key, label) ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            label,
                                            fontFamily = IranSansFontFamily,
                                            fontSize = 12.sp,
                                            fontWeight = if (range == key) FontWeight.Bold else FontWeight.Normal,
                                            color = if (range == key) HomePurple else HomeNavy
                                        )
                                    },
                                    onClick = {
                                        onRangeChange(key)
                                        rangeDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Dynamic Backend Driven Performance Canvas Chart with granular loader
            PerformanceCanvasChart(
                buckets = buckets,
                selectedMetric = selectedMetric,
                loading = loading
            )
        }
    }
}

private fun formatPersianDayLabel(label: String): String {
    val clean = label.trim()
    return when {
        clean.contains("Sat", ignoreCase = true) || (clean.contains("شنبه") && !clean.contains("یک") && !clean.contains("دو") && !clean.contains("سه") && !clean.contains("چهار") && !clean.contains("پنج")) -> "شنبه"
        clean.contains("Sun", ignoreCase = true) || clean.contains("یکشنبه") || clean.contains("یک‌شنبه") -> "یکشنبه"
        clean.contains("Mon", ignoreCase = true) || clean.contains("دوشنبه") || clean.contains("دو‌شنبه") -> "دوشنبه"
        clean.contains("Tue", ignoreCase = true) || clean.contains("سه شنبه") || clean.contains("سه‌شنبه") -> "سه‌شنبه"
        clean.contains("Wed", ignoreCase = true) || clean.contains("چهارشنبه") || clean.contains("چهار‌شنبه") -> "چهارشنبه"
        clean.contains("Thu", ignoreCase = true) || clean.contains("پنجشنبه") || clean.contains("پنج‌شنبه") -> "پنجشنبه"
        clean.contains("Fri", ignoreCase = true) || clean.contains("جمعه") -> "جمعه"
        clean.contains("Today", ignoreCase = true) || clean.contains("امروز") -> "امروز"
        clean.isNotBlank() -> clean
        else -> "—"
    }
}

@Composable
private fun PerformanceCanvasChart(
    buckets: List<PerformanceBucketDto>,
    selectedMetric: DashboardMetricType = DashboardMetricType.POINTS,
    loading: Boolean
) {
    val density = LocalDensity.current

    if (loading && buckets.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .shimmerEffect(RoundedCornerShape(16.dp))
        )
        return
    }

    // Prepare chart data from backend buckets
    val defaultDays = listOf("شنبه", "یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنجشنبه", "امروز")
    val defaultNormalized = listOf(0.12f, 0.42f, 0.46f, 0.86f, 0.38f, 0.58f, 0.18f)

    val chartItems = if (buckets.isNotEmpty()) {
        buckets.take(7)
    } else {
        emptyList()
    }

    val dayLabels = if (chartItems.isNotEmpty()) {
        chartItems.map { formatPersianDayLabel(it.label.ifBlank { it.key }) }
    } else {
        defaultDays
    }

    val metricMultiplier = when (selectedMetric) {
        DashboardMetricType.POINTS -> 1.0f
        DashboardMetricType.HOURS -> 0.1f
        DashboardMetricType.TESTS -> 1.5f
    }

    val maxRawValue = (chartItems.maxOfOrNull { it.value }?.toFloat() ?: 100f) * metricMultiplier
    val maxVal = if (maxRawValue <= 0f) 100f else maxRawValue

    val normalizedValues = if (chartItems.isNotEmpty()) {
        chartItems.map {
            val v = it.value.toFloat() * metricMultiplier
            if (maxVal > 0) (v / maxVal).coerceIn(0.08f, 0.95f) else 0.1f
        }
    } else {
        defaultNormalized
    }

    // Find peak index
    val peakIndex = if (chartItems.isNotEmpty()) {
        val maxIdx = chartItems.indexOfFirst { (it.value.toFloat() * metricMultiplier) == maxRawValue }
        if (maxIdx >= 0) maxIdx else chartItems.size / 2
    } else {
        3 // سه‌شنبه
    }

    val peakLabel = dayLabels.getOrNull(peakIndex) ?: "سه‌شنبه"

    // Y Axis labels (0%, 25%, 50%, 75%, 100%)
    val yAxisLabels = if (maxVal <= 100f) {
        listOf(
            "۰",
            (maxVal * 0.25f).toInt().toString().toPersianNumber(),
            (maxVal * 0.50f).toInt().toString().toPersianNumber(),
            (maxVal * 0.75f).toInt().toString().toPersianNumber(),
            maxVal.toInt().toString().toPersianNumber()
        )
    } else {
        listOf(
            "۰",
            (maxVal * 0.25f).toInt().toString().toPersianNumber(),
            (maxVal * 0.50f).toInt().toString().toPersianNumber(),
            (maxVal * 0.75f).toInt().toString().toPersianNumber(),
            maxVal.toInt().toString().toPersianNumber()
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val width = size.width
            val height = size.height

            val rightPadding = 38.dp.toPx() // Space for right Y-axis labels
            val leftPadding = 12.dp.toPx()
            val topPadding = 36.dp.toPx() // Space for floating tooltip above peak
            val bottomPadding = 26.dp.toPx() // Space for bottom X-axis day labels

            val chartWidth = width - leftPadding - rightPadding
            val chartHeight = height - topPadding - bottomPadding

            val yZero = topPadding + chartHeight

            // 1. Draw horizontal grid lines & Y-axis labels
            val gridStep = chartHeight / 4f
            val nativePaint = android.graphics.Paint().apply {
                color = Color(0xFFA5B0C4).toArgb()
                textSize = density.run { 9.5.sp.toPx() }
                textAlign = android.graphics.Paint.Align.LEFT
                isAntiAlias = true
                typeface = Typeface.DEFAULT
            }

            for (i in 0..4) {
                val y = yZero - (i * gridStep)
                // Draw light horizontal gridline
                drawLine(
                    color = Color(0xFFEFF2F8),
                    start = Offset(leftPadding, y),
                    end = Offset(leftPadding + chartWidth, y),
                    strokeWidth = 1.dp.toPx()
                )

                // Draw Y-axis label text on the right
                val label = yAxisLabels.getOrElse(i) { "" }
                drawContext.canvas.nativeCanvas.drawText(
                    label,
                    leftPadding + chartWidth + 10.dp.toPx(),
                    y + 4.dp.toPx(),
                    nativePaint
                )
            }

            // Calculate (x, y) coordinates for points:
            val numPoints = normalizedValues.size.coerceAtLeast(2)
            val stepX = chartWidth / (numPoints - 1)
            val points = normalizedValues.mapIndexed { index, normY ->
                val x = leftPadding + (index * stepX)
                val y = yZero - (normY * chartHeight)
                Offset(x, y)
            }

            val solidEndIndex = (points.size - 2).coerceAtLeast(0)

            // 2. Draw Smooth Bézier Solid Curve
            if (points.isNotEmpty()) {
                val solidPath = Path()
                val fillPath = Path()

                solidPath.moveTo(points[0].x, points[0].y)
                fillPath.moveTo(points[0].x, yZero)
                fillPath.lineTo(points[0].x, points[0].y)

                for (i in 0 until solidEndIndex) {
                    val p0 = points[i]
                    val p1 = points[i + 1]
                    val cx1 = p0.x + (p1.x - p0.x) / 2f
                    val cy1 = p0.y
                    val cx2 = p0.x + (p1.x - p0.x) / 2f
                    val cy2 = p1.y

                    solidPath.cubicTo(cx1, cy1, cx2, cy2, p1.x, p1.y)
                    fillPath.cubicTo(cx1, cy1, cx2, cy2, p1.x, p1.y)
                }

                // Close fill path down to baseline
                fillPath.lineTo(points[solidEndIndex].x, yZero)
                fillPath.close()

                // Draw Area Fill under Solid Curve
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            HomePurple.copy(alpha = 0.24f),
                            HomePurple.copy(alpha = 0.06f),
                            Color.White.copy(alpha = 0.0f)
                        ),
                        startY = topPadding,
                        endY = yZero
                    )
                )

                // Draw Solid Curve Stroke
                drawPath(
                    path = solidPath,
                    color = HomePurple,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // 3. Draw Dashed Projection Curve to the last point
                if (points.size > 1 && solidEndIndex < points.size - 1) {
                    val dashedPath = Path()
                    val pLastMinus1 = points[solidEndIndex]
                    val pLast = points.last()
                    val cx1 = pLastMinus1.x + (pLast.x - pLastMinus1.x) / 2f
                    val cx2 = pLastMinus1.x + (pLast.x - pLastMinus1.x) / 2f
                    dashedPath.moveTo(pLastMinus1.x, pLastMinus1.y)
                    dashedPath.cubicTo(cx1, pLastMinus1.y, cx2, pLast.y, pLast.x, pLast.y)

                    drawPath(
                        path = dashedPath,
                        color = Color(0xFFA898F8),
                        style = Stroke(
                            width = 2.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f),
                            cap = StrokeCap.Round
                        )
                    )
                }

                // 4. Draw Guide Lines and Points
                val validPeakIndex = peakIndex.coerceIn(0, points.size - 1)
                val peakPoint = points[validPeakIndex]

                // Vertical dashed line from peak down to baseline
                drawLine(
                    color = HomePurple.copy(alpha = 0.35f),
                    start = Offset(peakPoint.x, peakPoint.y),
                    end = Offset(peakPoint.x, yZero),
                    strokeWidth = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                )

                // Draw Points (Circles)
                points.forEachIndexed { index, pt ->
                    when (index) {
                        validPeakIndex -> {
                            // Big active peak point
                            drawCircle(
                                color = HomePurple,
                                radius = 6.dp.toPx(),
                                center = pt
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 3.dp.toPx(),
                                center = pt
                            )
                            drawCircle(
                                color = HomePurple,
                                radius = 1.5.dp.toPx(),
                                center = pt
                            )
                        }
                        points.size - 2 -> {
                            // Penultimate point (white filled circle with purple stroke)
                            drawCircle(
                                color = Color.White,
                                radius = 5.dp.toPx(),
                                center = pt
                            )
                            drawCircle(
                                color = HomePurple,
                                radius = 5.dp.toPx(),
                                center = pt,
                                style = Stroke(width = 2.dp.toPx())
                            )
                        }
                        points.size - 1 -> {
                            // Last / future point
                            drawCircle(
                                color = Color.White,
                                radius = 4.dp.toPx(),
                                center = pt
                            )
                            drawCircle(
                                color = Color(0xFFA898F8),
                                radius = 4.dp.toPx(),
                                center = pt,
                                style = Stroke(width = 1.5.dp.toPx())
                            )
                        }
                        else -> {
                            // Normal solid purple dot
                            drawCircle(
                                color = HomePurple,
                                radius = 4.5.dp.toPx(),
                                center = pt
                            )
                        }
                    }
                }

                // 5. Draw Floating Tooltip on Peak
                val tooltipWidth = 62.dp.toPx()
                val tooltipHeight = 26.dp.toPx()
                val tooltipX = peakPoint.x - (tooltipWidth / 2f)
                val tooltipY = peakPoint.y - tooltipHeight - 12.dp.toPx()

                // Small connector pin/pointer below tooltip
                val pointerPath = Path().apply {
                    moveTo(peakPoint.x, peakPoint.y - 2.dp.toPx())
                    lineTo(peakPoint.x - 5.dp.toPx(), tooltipY + tooltipHeight)
                    lineTo(peakPoint.x + 5.dp.toPx(), tooltipY + tooltipHeight)
                    close()
                }
                drawPath(path = pointerPath, color = HomePurple)

                // Tooltip rounded background
                drawRoundRect(
                    color = HomePurple,
                    topLeft = Offset(tooltipX, tooltipY),
                    size = Size(tooltipWidth, tooltipHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(13.dp.toPx())
                )

                // Tooltip Text
                val tooltipPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = density.run { 10.sp.toPx() }
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                    isFakeBoldText = true
                }
                drawContext.canvas.nativeCanvas.drawText(
                    peakLabel,
                    peakPoint.x,
                    tooltipY + (tooltipHeight / 2f) + 4.dp.toPx(),
                    tooltipPaint
                )

                // 6. Draw X-axis Day Labels at bottom
                val dayLabelPaint = android.graphics.Paint().apply {
                    color = Color(0xFF7E8CA4).toArgb()
                    textSize = density.run { 9.5.sp.toPx() }
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                }

                points.forEachIndexed { index, pt ->
                    val label = dayLabels.getOrElse(index) { "" }
                    drawContext.canvas.nativeCanvas.drawText(
                        label,
                        pt.x,
                        yZero + 18.dp.toPx(),
                        dayLabelPaint
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeFeatureGrid(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // TOP SECTION: Row with (Left) Tall "برنامه‌ریز هوشمند شتاب" + (Right) 2 Stacked Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(216.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Right Column (in RTL): Stacked Cards "لایـک‌های رقابتی فعال" and "گروه‌های مطالعاتی من"
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Top Stack Card: لیگ‌های رقابتی فعال
                FeatureCardLeague(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    onClick = { navController.navigate("league") }
                )

                // Bottom Stack Card: گروه‌های مطالعاتی من
                FeatureCardStudyGroup(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    onClick = { navController.navigate("my_group") }
                )
            }

            // Left Column (in RTL): Tall Card "برنامه‌ریز هوشمند شتاب"
            FeatureCardSmartPlan(
                modifier = Modifier
                    .weight(1.08f)
                    .fillMaxHeight(),
                onClick = { navController.navigate("study_plan") }
            )
        }

        // BOTTOM SECTION: Row with 2 Equal Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(124.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Right Card: پرسش از همکلاسی‌ها
            FeatureCardPeerTrouble(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                onClick = { navController.navigate("peer_trouble") }
            )

            // Left Card: آزمون‌ساز
            FeatureCardExamBuilder(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                onClick = { navController.navigate("exams_screen") }
            )
        }
    }
}

@Composable
private fun FeatureCardSmartPlan(
    modifier: Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF9868FA), Color(0xFF7543EA))
                )
            )
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        // Dart and Target 3D illustration (shifted slightly leftwards & close to top text)
        Image(
            painter = painterResource(R.drawable.home_plan_dart),
            contentDescription = "برنامه‌ریز هوشمند شتاب",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = (-16).dp, y = 2.dp)
                .size(440.dp)
        )

        // Top Text Titles (White & Persian Typography in single line)
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "برنامه‌ریز هوشمند شتاب",
                color = Color.White,
                fontFamily = IranSansFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.5.sp,
                maxLines = 1,
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = "برنامه‌ریزی هوشمند و پیشرفت سؤال",
                color = Color.White.copy(alpha = 0.88f),
                fontFamily = IranSansFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                maxLines = 1,
            )
        }

        // Bottom-Start (Right side in Persian RTL) White Pill CTA Button: ‹ شروع کنید
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .clip(RoundedCornerShape(50))
                .background(Color.White)
                .padding(horizontal = 14.dp, vertical = 7.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = null,
                    tint = Color(0xFF7543EA),
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = "شروع کنید",
                    color = Color(0xFF7543EA),
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun FeatureCardLeague(
    modifier: Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFF4F8FF), Color(0xFFEAF2FE))
                )
            )
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        // Shield Illustration on Left (shifted further to the left edge)
        Image(
            painter = painterResource(R.drawable.ic_league_homepage_vector),
            contentDescription = "لیگ‌های رقابتی",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 6.dp)
                .size(82.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth(0.60f)
        ) {
            Text(
                text = "لیگ‌های رقابتی",
                color = HomeNavy,
                fontFamily = IranSansFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.5.sp,
                maxLines = 1,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "تو یک قدم تا جایزه",
                color = HomeMuted,
                fontFamily = IranSansFontFamily,
                fontSize = 8.5.sp,
                textAlign = TextAlign.Justify,
                maxLines = 2,
            )
        }

        // Avatars and Arrow button at bottom
        Row(
            modifier = Modifier.align(Alignment.BottomStart),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy((-6).dp)
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(Color(0xFFDBEAFE), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = null,
                    tint = Color(0xFF2563EB),
                    modifier = Modifier.size(12.dp)
                )
            }
            Spacer(Modifier.width(10.dp))
            MiniAvatar(R.drawable.enb_sina)
            MiniAvatar(R.drawable.enb_sina)
            MiniAvatar(R.drawable.enb_sina)
        }
    }
}

@Composable
private fun FeatureCardStudyGroup(
    modifier: Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFF3FDF6), Color(0xFFEAF9F0))
                )
            )
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        // Study group Illustration on Left (End)
        Image(
            painter = painterResource(R.drawable.home_study_group_illustration),
            contentDescription = "گروه‌های مطالعاتی من",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 4.dp)
                .size(78.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth(0.60f)
        ) {
            Text(
                text = stringResource(R.string.home_study_groups_title),
                color = HomeNavy,
                fontFamily = IranSansFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.5.sp,
                maxLines = 1,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.home_study_groups_subtitle),
                color = HomeMuted,
                fontFamily = IranSansFontFamily,
                fontSize = 8.5.sp,
                lineHeight = 12.sp,
                maxLines = 2,
            )
        }

        // Avatars and Arrow button at bottom
        Row(
            modifier = Modifier.align(Alignment.BottomStart),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy((-6).dp)
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(Color(0xFFDCFCE7), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = null,
                    tint = Color(0xFF16A34A),
                    modifier = Modifier.size(12.dp)
                )
            }
            Spacer(Modifier.width(10.dp))
            MiniAvatar(R.drawable.enb_sina)
            MiniAvatar(R.drawable.enb_sina)
            MiniAvatar(R.drawable.enb_sina)
        }
    }
}

@Composable
private fun FeatureCardPeerTrouble(
    modifier: Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFFAF6FF), Color(0xFFF5EDFF))
                )
            )
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        // Chat bubbles illustration
        Image(
            painter = painterResource(R.drawable.home_chat_bubbles),
            contentDescription = "پرسش از همکلاسی‌ها",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 4.dp)
                .size(78.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth(0.58f)
        ) {
            Text(
                text = stringResource(R.string.home_peer_trouble_title),
                color = HomeNavy,
                fontFamily = IranSansFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.5.sp,
                maxLines = 1,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.home_peer_trouble_subtitle),
                color = HomeMuted,
                fontFamily = IranSansFontFamily,
                fontSize = 8.5.sp,
                lineHeight = 12.sp,
                maxLines = 2,
            )
        }

        // Circular Purple Arrow Button at bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(26.dp)
                .background(Color(0xFFA855F7), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun FeatureCardExamBuilder(
    modifier: Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFFFFDF5), Color(0xFFFEF7E4))
                )
            )
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        // Exam notepad illustration
        Image(
            painter = painterResource(R.drawable.home_exam_sheet),
            contentDescription = "آزمون‌ساز",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 4.dp)
                .size(78.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth(0.58f)
        ) {
            Text(
                text = "آزمون‌ساز",
                color = HomeNavy,
                fontFamily = IranSansFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                maxLines = 1,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "آزمون بساز و تمرین کن",
                color = HomeMuted,
                fontFamily = IranSansFontFamily,
                fontSize = 8.5.sp,
                textAlign = TextAlign.Justify,
                maxLines = 2,
            )
        }

        // Circular Orange Arrow Button at bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(26.dp)
                .background(Color(0xFFF97316), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun MiniAvatar(drawableRes: Int) {
    Image(
        painter = painterResource(drawableRes),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .border(1.5.dp, Color.White, CircleShape)
    )
}
