package com.example.ui.main

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.example.R
import com.example.network.ApiClient
import com.example.network.NetworkResult
import com.example.network.PerformanceBucketDto
import com.example.network.ProgressDashboardBodyDto
import com.example.network.TokenManager
import com.example.network.safeApiCall
import com.example.ui.theme.IranSansFontFamily

private val HomeNavy = Color(0xFF172554)
private val HomeMuted = Color(0xFFA0A8C0)
private val HomePurple = Color(0xFF7556F6)

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
    var unreadCount by remember { mutableStateOf(0) }
    var reload by remember { mutableStateOf(0) }

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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        ReferenceHomeHeader(
            isGuest = isGuest,
            onLoginClick = onLoginClick,
            unreadCount = unreadCount,
            onNotificationsClick = { navController.navigate("notifications") },
        )
        ReferenceStatsRow(dashboard, loading)
        PerformanceCard(
            range = range,
            buckets = buckets,
            loading = loading,
            error = error,
            onRangeChange = { range = it },
            onRetry = { reload++ },
        )
        ReferenceFeatureGrid(navController)
    }
}

@Composable
private fun ReferenceHomeHeader(
    isGuest: Boolean,
    onLoginClick: () -> Unit,
    unreadCount: Int,
    onNotificationsClick: () -> Unit,
) {
    val context = LocalContext.current
    val tokenManager = remember(context) { ApiClient.getTokenManager() ?: TokenManager(context) }
    var displayName by remember { mutableStateOf(tokenManager.getUserFullName()) }
    var displayTitle by remember { mutableStateOf(tokenManager.getUserTitle()) }

    LaunchedEffect(isGuest) {
        if (!isGuest) {
            safeApiCall { ApiClient.apiService.checkIn() }
            when (val result = safeApiCall { ApiClient.apiService.getMe() }) {
                is NetworkResult.Success -> result.data.body?.let { profile ->
                    displayName = profile.fullName?.trim()
                    displayTitle = profile.progression?.title?.nameFa
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

    androidx.compose.runtime.CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(
          modifier = Modifier
              .fillMaxWidth()
              .padding(top = 10.dp, bottom = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
      ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .shadow(3.dp, CircleShape, ambientColor = Color(0x162A3760), spotColor = Color(0x162A3760))
                .background(Color.White, CircleShape)
                .clickable(enabled = !isGuest, onClick = onNotificationsClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Default.NotificationsNone, contentDescription = "اعلان‌ها", tint = HomeNavy, modifier = Modifier.size(25.dp))
            if (unreadCount > 0) Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(17.dp)
                    .background(HomePurple, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(if (unreadCount > 9) "۹+" else unreadCount.toString(), color = Color.White, fontSize = 7.sp)
            }
        }

        Spacer(Modifier.width(13.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable(enabled = isGuest, onClick = onLoginClick),
            horizontalAlignment = Alignment.End,
        ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isGuest) {
                            "شتاب"
                        } else {
                            displayName?.takeIf { it.isNotBlank() } ?: "کاربر شتاب"
                        },
                        color = HomeNavy,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.width(5.dp))
                    Icon(Icons.Default.Bolt, contentDescription = null, tint = HomePurple, modifier = Modifier.size(31.dp))
                }
                Text(
                    text = if (isGuest) {
                        "برای شروع وارد شوید"
                    } else {
                        displayTitle?.takeIf { it.isNotBlank() } ?: "تازه‌نفس"
                    },
                    color = HomeMuted,
                    fontFamily = IranSansFontFamily,
                    fontSize = 9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
      }
    }
}

@Composable
private fun ReferenceStatsRow(dashboard: ProgressDashboardBodyDto?, loading: Boolean) {
    val placeholder = if (loading) "…" else "—"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        HomeStatCard(
            modifier = Modifier.weight(1f),
            title = "رتبه در لیگ",
            value = dashboard?.rank?.toString() ?: placeholder,
            icon = Icons.Default.WorkspacePremium,
            iconTint = Color(0xFF7957EF),
            background = Color(0xFFF5F0FF),
            accent = Color(0xFF9B75F5),
        )
        HomeStatCard(
            modifier = Modifier.weight(1f),
            title = "لیگ",
            value = dashboard?.league?.nameFa ?: placeholder,
            icon = Icons.Default.MilitaryTech,
            iconTint = Color(0xFF21B982),
            background = Color(0xFFEDF9F5),
            accent = Color(0xFF30C58B),
        )
        HomeStatCard(
            modifier = Modifier.weight(1f),
            title = "کل مطالعه",
            value = dashboard?.totalStudySeconds?.let(::studyDuration) ?: placeholder,
            icon = Icons.Default.AccessTime,
            iconTint = Color(0xFF3D70EF),
            background = Color(0xFFF1F5FF),
            accent = Color(0xFF4D78EF),
        )
        HomeStatCard(
            modifier = Modifier.weight(1f),
            title = "امتیاز من",
            value = dashboard?.points?.toString() ?: placeholder,
            icon = Icons.Default.EmojiEvents,
            iconTint = Color(0xFFFFB416),
            background = Color(0xFFFFF8E9),
            accent = Color(0xFFFFB914),
        )
    }
}

@Composable
private fun HomeStatCard(
    modifier: Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: Color,
    background: Color,
    accent: Color,
) {
    Column(
        modifier = modifier
            .height(126.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .padding(horizontal = 7.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(43.dp)
                .background(Color.White.copy(alpha = .62f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(27.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(title, color = HomeNavy.copy(alpha = .72f), fontFamily = IranSansFontFamily, fontSize = 9.sp, maxLines = 1)
        Text(value, color = HomeNavy, fontFamily = IranSansFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, maxLines = 1)
        Spacer(Modifier.weight(1f))
        Box(
            modifier = Modifier
                .fillMaxWidth(.72f)
                .height(3.dp)
                .background(accent.copy(alpha = .13f), CircleShape),
        ) {
            Box(Modifier.fillMaxWidth(.72f).height(3.dp).background(accent, CircleShape))
        }
    }
}

@Composable
private fun PerformanceCard(
    range: String,
    buckets: List<PerformanceBucketDto>,
    loading: Boolean,
    error: String?,
    onRangeChange: (String) -> Unit,
    onRetry: () -> Unit,
) {
    val ranges = listOf(
        "TODAY" to "امروز",
        "LAST_7_DAYS" to "۷ روز",
        "LAST_30_DAYS" to "۳۰ روز",
        "LAST_12_MONTHS" to "۱۲ ماه",
    )
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ShowChart, contentDescription = null, tint = HomePurple, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(7.dp))
                    Text("نمای کلی عملکرد", color = HomeNavy, fontFamily = IranSansFontFamily, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
            Spacer(Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                items(ranges) { item ->
                    Text(
                        text = item.second,
                        color = if (range == item.first) Color.White else HomeNavy,
                        fontFamily = IranSansFontFamily,
                        fontSize = 9.sp,
                        modifier = Modifier
                            .background(if (range == item.first) HomePurple else Color(0xFFF5F4FA), RoundedCornerShape(10.dp))
                            .clickable { onRangeChange(item.first) }
                            .padding(horizontal = 10.dp, vertical = 7.dp),
                    )
                }
            }
            Spacer(Modifier.height(13.dp))
            when {
                loading -> Box(Modifier.fillMaxWidth().height(155.dp), contentAlignment = Alignment.Center) { Text("در حال دریافت آمار…", color = HomeMuted, fontFamily = IranSansFontFamily, fontSize = 10.sp) }
                error != null -> Box(Modifier.fillMaxWidth().height(155.dp).clickable(onClick = onRetry), contentAlignment = Alignment.Center) { Text("$error\nبرای تلاش دوباره لمس کنید", color = Color(0xFFD84C4C), fontFamily = IranSansFontFamily, fontSize = 10.sp) }
                buckets.isEmpty() -> Box(Modifier.fillMaxWidth().height(155.dp), contentAlignment = Alignment.Center) { Text("هنوز داده‌ای ثبت نشده", color = HomeMuted, fontFamily = IranSansFontFamily, fontSize = 10.sp) }
                else -> PerformanceBars(buckets, range == "TODAY")
            }
        }
    }
}

@Composable
private fun PerformanceBars(buckets: List<PerformanceBucketDto>, studyMinutes: Boolean) {
    val maxValue = buckets.maxOfOrNull { it.value }?.coerceAtLeast(1) ?: 1
    androidx.compose.runtime.CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        LazyRow(
            modifier = Modifier.fillMaxWidth().height(155.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(buckets) { bucket ->
                Column(
                    modifier = Modifier.width(if (buckets.size > 15) 34.dp else 42.dp).fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    Text(
                        text = if (studyMinutes) "${bucket.value}د" else bucket.value.toString(),
                        color = HomeNavy,
                        fontSize = 7.sp,
                    )
                    Spacer(Modifier.height(4.dp))
                    Box(
                        Modifier
                            .width(18.dp)
                            .height((105f * bucket.value.toFloat() / maxValue).coerceAtLeast(3f).dp)
                            .background(if (bucket.isFuture) Color(0xFFE5E7EF) else HomePurple, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                    )
                    Spacer(Modifier.height(5.dp))
                    Text(bucket.label, color = HomeMuted, fontFamily = IranSansFontFamily, fontSize = 7.sp, maxLines = 1)
                }
            }
        }
    }
}

private fun studyDuration(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return if (hours > 0) "$hours س $minutes د" else "$minutes دقیقه"
}

@Composable
private fun ReferenceFeatureGrid(navController: NavController) {
    androidx.compose.runtime.CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(
            modifier = Modifier.fillMaxWidth().height(282.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            IllustratedFeatureCard(
                modifier = Modifier.weight(1f).fillMaxSize(),
                title = "برنامه‌ریز هوشمند شتاب",
                subtitle = "برنامه‌ریزی هوشمند و پیشرفت مداوم",
                imageRes = R.drawable.home_plan_illustration,
                background = listOf(Color(0xFFF8F4FF), Color(0xFFF1EBFF)),
                large = true,
                onClick = { navController.navigate("study_plan") },
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                IllustratedFeatureCard(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    title = "لیدرهای رقابتی فعال",
                    subtitle = "تو یک قدم جایزه‌ای",
                    imageRes = R.drawable.home_league_illustration,
                    background = listOf(Color(0xFFF1F6FF), Color(0xFFEAF2FF)),
                    onClick = { navController.navigate("league") },
                )
                IllustratedFeatureCard(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    title = "گروه‌های مطالعاتی من",
                    subtitle = "با هم بهتر می‌تونیم",
                    imageRes = R.drawable.home_study_group_illustration,
                    background = listOf(Color(0xFFF0FBF6), Color(0xFFE9F9F1)),
                    onClick = { navController.navigate("my_group") },
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth().height(120.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IllustratedFeatureCard(
                modifier = Modifier.weight(1f).fillMaxSize(),
                title = "آزمون‌ساز",
                subtitle = "آزمون بساز و تمرین کن",
                imageRes = R.drawable.home_exam_illustration,
                background = listOf(Color(0xFFFFFAEF), Color(0xFFFFF3DC)),
                onClick = { navController.navigate("exams_screen") },
            )
            IllustratedFeatureCard(
                modifier = Modifier.weight(1f).fillMaxSize(),
                title = "پرسش از همکلاسی‌ها",
                subtitle = "سوالت رو سریع بپرس",
                imageRes = R.drawable.home_communication_illustration,
                background = listOf(Color(0xFFF9F4FF), Color(0xFFF1E9FF)),
                onClick = { navController.navigate("peer_trouble") },
            )
        }
    }
}

@Composable
private fun IllustratedFeatureCard(
    modifier: Modifier,
    title: String,
    subtitle: String,
    imageRes: Int,
    background: List<Color>,
    large: Boolean = false,
    onClick: () -> Unit,
) {
    androidx.compose.runtime.CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = modifier
                .shadow(2.dp, RoundedCornerShape(22.dp), ambientColor = Color(0x102B3762), spotColor = Color(0x102B3762))
                .clip(RoundedCornerShape(22.dp))
                .background(Brush.linearGradient(background))
                .clickable(onClick = onClick),
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = if (large) {
                    Modifier.align(Alignment.BottomCenter).fillMaxWidth(.9f).height(160.dp).padding(bottom = 10.dp)
                } else {
                    Modifier.align(Alignment.CenterEnd).size(92.dp)
                },
            )
            Column(
                modifier = Modifier
                    .align(if (large) Alignment.TopStart else Alignment.CenterStart)
                    .padding(if (large) 17.dp else 14.dp)
                    .fillMaxWidth(if (large) .92f else .58f),
            ) {
                Text(
                    title,
                    color = HomeNavy,
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = if (large) 15.sp else 11.sp,
                    maxLines = if (large) 2 else 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    subtitle,
                    color = HomeMuted,
                    fontFamily = IranSansFontFamily,
                    fontSize = if (large) 10.sp else 8.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (large) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "شروع کنید ‹",
                        color = Color.White,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.background(HomePurple, RoundedCornerShape(12.dp)).padding(horizontal = 12.dp, vertical = 8.dp),
                    )
                }
            }
        }
    }
}
