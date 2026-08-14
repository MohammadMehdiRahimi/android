package com.example.ui.features.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Brush
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import com.example.ui.core.components.shimmerEffect
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp
import com.example.R
import com.example.network.ApiClient
import com.example.network.NetworkResult
import com.example.network.TokenManager
import com.example.network.safeApiCall
import com.example.ui.theme.LocalShetabColors

@Composable
fun MenuItemCard(
    title: String,
    subtitle: String,
    iconRes: Int,
    tag: String?,
    accentColor: Color,
    onClick: () -> Unit
) {
    val colors = LocalShetabColors.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(105.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp), clip = false)
            .border(1.dp, colors.primaryText.copy(alpha = 0.04f), RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.cardBg
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = accentColor.copy(alpha = 0.05f),
                    radius = 70.dp.toPx(),
                    center = Offset(size.width, size.height)
                )
            }
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(accentColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = iconRes),
                            contentDescription = title,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    if (tag != null) {
                        Box(
                            modifier = Modifier
                                .background(accentColor.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = tag,
                                color = accentColor,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }
                }
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = title,
                        color = colors.primaryText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Start,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        color = colors.secondaryText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun HorizontalMenuSlider(
    onChatRayaClick: () -> Unit = {},
    onExamsClick: () -> Unit = {},
    onStudyPlanClick: () -> Unit = {},
    onFlashcardsClick: () -> Unit = {},
    onPeerTroubleClick: () -> Unit = {},
    onAcademicReportClick: () -> Unit = {},
    onCardPositioned: (String, androidx.compose.ui.geometry.Rect) -> Unit = { _, _ -> }
) {
    val colors = LocalShetabColors.current
    var showFeatureAlert by remember { mutableStateOf<String?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .onGloballyPositioned { coords ->
                            onCardPositioned("study_plan", coords.boundsInWindow())
                        }
                ) {
                    MenuItemCard(
                        title = "برنامه مطالعاتی",
                        subtitle = "منظم و مربی هوشمند",
                        iconRes = R.drawable.plan,
                        tag = "هدفمند",
                        accentColor = Color(0xFFE91E63),
                        onClick = onStudyPlanClick
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .onGloballyPositioned { coords ->
                            onCardPositioned("exams", coords.boundsInWindow())
                        }
                ) {
                    MenuItemCard(
                        title = "آزمون‌ها",
                        subtitle = "سنجش و شبیه‌ساز",
                        iconRes = R.drawable.exam,
                        tag = "جامع",
                        accentColor = Color(0xFF9C27B0),
                        onClick = onExamsClick
                    )
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .onGloballyPositioned { coords ->
                            onCardPositioned("flashcards", coords.boundsInWindow())
                        }
                ) {
                    MenuItemCard(
                        title = "گروه‌های درسی",
                        subtitle = "مطالعه گروهی و فلش‌کارت",
                        iconRes = R.drawable.study_group,
                        tag = "تثبیت",
                        accentColor = Color(0xFF4CAF50),
                        onClick = onFlashcardsClick
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .onGloballyPositioned { coords ->
                            onCardPositioned("peer_trouble", coords.boundsInWindow())
                        }
                ) {
                    MenuItemCard(
                        title = "رفع اشکال همگانی",
                        subtitle = "ارتباطات و حل اشتراکی مسائل",
                        iconRes = R.drawable.communication,
                        tag = "همیاری با رفقا",
                        accentColor = Color(0xFF00BCD4),
                        onClick = onPeerTroubleClick
                    )
                }
            }
        }
    }
    
    if (showFeatureAlert != null) {
        AlertDialog(
            onDismissRequest = { showFeatureAlert = null },
            confirmButton = {
                TextButton(onClick = { showFeatureAlert = null }) {
                    Text("متوجه شدم", color = colors.accentMain, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text(
                    text = "سرویس ویژه",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right,
                    fontWeight = FontWeight.Bold,
                    color = colors.primaryText,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    text = showFeatureAlert ?: "",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right,
                    color = colors.secondaryText,
                    fontSize = 13.sp
                )
            },
            containerColor = colors.cardBg,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun DashboardTopHeader(
    userName: String = "دانشجو",
    isLoading: Boolean = false,
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    val colors = LocalShetabColors.current
    val context = LocalContext.current
    val sharedPrefs = remember {
        context.getSharedPreferences("shetab_onboarding_prefs", android.content.Context.MODE_PRIVATE)
    }
    val tokenManager = remember(context) {
        ApiClient.getTokenManager() ?: TokenManager(context)
    }
    val isLoggedIn = tokenManager.isLoggedIn()
    var savedName by remember(sharedPrefs, tokenManager) {
        mutableStateOf(
            sharedPrefs.getString("user_name", null)?.takeIf { it.isNotBlank() }
                ?: tokenManager.getUserFullName()?.takeIf { it.isNotBlank() },
        )
    }
    val savedTitle = remember(sharedPrefs) { sharedPrefs.getString("user_title", null) }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn && savedName.isNullOrBlank()) {
            when (val result = safeApiCall { ApiClient.apiService.getMe() }) {
                is NetworkResult.Success -> result.data.body?.fullName
                    ?.takeIf { it.isNotBlank() }
                    ?.let { fullName ->
                        savedName = fullName.trim()
                        sharedPrefs.edit().putString("user_name", savedName).apply()
                        tokenManager.saveUserData(
                            tokenManager.getUserId(),
                            tokenManager.getUserPhone(),
                            tokenManager.getUserRole(),
                            savedName,
                        )
                    }
                else -> Unit
            }
        }
    }

    val displayName = if (isLoggedIn) savedName ?: "کاربر شتاب" else "کاربر مهمان"
    val displayTitle = if (isLoggedIn) {
        if (!savedTitle.isNullOrBlank()) savedTitle else "دانش‌آموز شتاب"
    } else {
        "برای ورود ضربه بزنید"
    }

    val handleHeaderClick = {
        if (isLoggedIn) {
            onProfileClick()
        } else {
            onLoginClick()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // User Profile & Name Section (Right in RTL)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { handleHeaderClick() }
                .padding(vertical = 4.dp, horizontal = 2.dp)
        ) {
            // User Profile Avatar Circle (User Icon)
            Box(
                modifier = Modifier.size(46.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1F5F9))
                        .border(1.5.dp, Color(0xFFCBD5E1), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "پروفایل کاربر",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(26.dp)
                    )
                }
                // Online Status Green Dot
                Box(
                    modifier = Modifier
                        .size(11.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF22C55E))
                        .border(1.5.dp, Color.White, CircleShape)
                        .align(Alignment.BottomEnd)
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .width(88.dp)
                            .height(16.dp)
                            .shimmerEffect(RoundedCornerShape(6.dp))
                    )
                    Box(
                        modifier = Modifier
                            .width(58.dp)
                            .height(12.dp)
                            .shimmerEffect(RoundedCornerShape(4.dp))
                    )
                } else {
                    Text(
                        text = displayName,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = displayTitle,
                        fontSize = 11.5.sp,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Bell Notification Button with Badge (Left in RTL)
        Box(
            modifier = Modifier
                .size(44.dp)
                .shadow(2.dp, CircleShape, clip = false)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, Color(0xFFF1F5F9), CircleShape)
                .clickable(onClick = onNotificationClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notifications",
                tint = Color(0xFF1E293B),
                modifier = Modifier.size(22.dp)
            )
            // Notification Badge Dot
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEF4444))
                    .align(Alignment.TopEnd)
                    .padding(2.dp)
            )
        }
    }
}

@Composable
fun Top4StatsRow(
    rank: String = "۰",
    level: String = "سطح ۰",
    totalStudyHours: String = "۰ ساعت",
    points: String = "۰",
    isLoading: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Card 1 (Rightmost in RTL): رتبه من (Purple)
        StatItemCard(
            title = "رتبه من",
            value = rank,
            isLoading = isLoading,
            iconEmoji = "👑",
            cardBg = Color(0xFFFBF8FF),
            iconBg = Color(0xFFF3E8FF),
            accentColor = Color(0xFF8B5CF6),
            modifier = Modifier.weight(1f)
        )

        // Card 2: ناشگر برتر (Green)
        StatItemCard(
            title = "ناشگر برتر",
            value = level,
            isLoading = isLoading,
            iconEmoji = "⭐",
            cardBg = Color(0xFFF4FBF6),
            iconBg = Color(0xFFDCFCE7),
            accentColor = Color(0xFF22C55E),
            modifier = Modifier.weight(1f)
        )

        // Card 3: کل مطالعه (Blue)
        StatItemCard(
            title = "کل مطالعه",
            value = totalStudyHours,
            isLoading = isLoading,
            iconEmoji = "🕒",
            cardBg = Color(0xFFF2F8FE),
            iconBg = Color(0xFFE0F2FE),
            accentColor = Color(0xFF3B82F6),
            modifier = Modifier.weight(1f)
        )

        // Card 4 (Leftmost in RTL): امتیاز من (Gold/Yellow)
        StatItemCard(
            title = "امتیاز من",
            value = points,
            isLoading = isLoading,
            iconEmoji = "🏆",
            cardBg = Color(0xFFFFFDF5),
            iconBg = Color(0xFFFEF3C7),
            accentColor = Color(0xFFF59E0B),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatItemCard(
    title: String,
    value: String,
    iconEmoji: String,
    cardBg: Color,
    iconBg: Color,
    accentColor: Color,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(1.dp, RoundedCornerShape(20.dp), clip = false),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp, start = 4.dp, end = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            // Circle Icon Container
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Text(text = iconEmoji, fontSize = 16.sp)
            }

            Text(
                text = title,
                fontSize = 9.5.sp,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(14.dp)
                        .shimmerEffect(RoundedCornerShape(4.dp))
                )
            } else {
                Text(
                    text = value,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Bottom Colored Indicator Line
            Box(
                modifier = Modifier
                    .width(32.dp)
                    .height(2.5.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accentColor)
            )
        }
    }
}

@Composable
fun OverallPerformanceChart(
    hasPerformanceData: Boolean = false,
    isLoading: Boolean = false,
    onStartClick: () -> Unit = {}
) {
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(1f, tween(1200, easing = FastOutSlowInEasing))
    }

    var selectedFilter by remember { mutableStateOf("هفته گذشته") }
    var showFilterMenu by remember { mutableStateOf(false) }
    val filterOptions = listOf("عملکرد روزانه", "هفته گذشته", "عملکرد ماهانه")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .shadow(1.dp, RoundedCornerShape(24.dp), clip = false),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card Header in RTL: "نمای کلی عملکرد" on RIGHT, Filter dropdown button on LEFT
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // First child in RTL Row -> Renders on RIGHT
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.BarChart,
                        contentDescription = null,
                        tint = Color(0xFF8B5CF6),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "نمای کلی عملکرد",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A)
                    )
                }

                // Second child in RTL Row -> Renders Filter selector on LEFT
                Box {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF8FAFC))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                            .clickable { showFilterMenu = true }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = selectedFilter,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B)
                            )
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { showFilterMenu = false },
                        containerColor = Color.White,
                        shape = RoundedCornerShape(14.dp),
                        shadowElevation = 8.dp,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        filterOptions.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = option,
                                        fontSize = 12.sp,
                                        fontWeight = if (option == selectedFilter) FontWeight.Bold else FontWeight.Medium,
                                        color = if (option == selectedFilter) Color(0xFF8B5CF6) else Color(0xFF334155)
                                    )
                                },
                                onClick = {
                                    selectedFilter = option
                                    showFilterMenu = false
                                },
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Chart Canvas Area with Y-axis values on RIGHT & Smooth Bezier Line Chart
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                val boxWidth = maxWidth

                // Background Y-Axis Dashed Grid Lines & Labels (Right aligned in RTL)
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    val yLabels = listOf("100", "75", "50", "25", "0")
                    yLabels.forEach { label ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // First child in RTL Row -> Renders on RIGHT
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8),
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.width(22.dp),
                                textAlign = TextAlign.Start
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            // Second child in RTL Row -> Renders Grid line towards LEFT
                            Canvas(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(1.dp)
                            ) {
                                drawLine(
                                    color = Color(0xFFF1F5F9),
                                    start = Offset(0f, 0f),
                                    end = Offset(size.width, 0f),
                                    strokeWidth = 1.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                                )
                            }
                        }
                    }
                }

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 28.dp, top = 6.dp, bottom = 24.dp)
                            .shimmerEffect(RoundedCornerShape(16.dp))
                    )
                } else if (!hasPerformanceData) {
                    // Empty State Overlay when user has not started yet
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 28.dp, top = 6.dp, bottom = 24.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF8FAFC).copy(alpha = 0.96f))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                            .clickable { onStartClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF3E8FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.BarChart,
                                    contentDescription = null,
                                    tint = Color(0xFF8B5CF6),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Text(
                                text = "هنوز مطالعه‌ای ثبت نکردی؛ اولین مطالعه را شروع کن",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B),
                                textAlign = TextAlign.Center
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF8B5CF6))
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                text = "شروع مطالعه",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                } else {
                    // Smooth Curved Purple Chart Canvas (start = 32.dp leaves room on RIGHT for Y-axis)
                    Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 32.dp, bottom = 24.dp, top = 22.dp)
                ) {
                    val w = size.width
                    val h = size.height

                    // Key Points along the RTL week curve (Right to Left on Canvas: Saturday on Far Right, Friday/Future on Far Left)
                    val pShanbe = Offset(w * 0.95f, h * 0.72f)      // شنبه (Far Right)
                    val pYekshanbe = Offset(w * 0.80f, h * 0.50f)   // یکشنبه
                    val pDoshanbe = Offset(w * 0.65f, h * 0.46f)    // دوشنبه
                    val pSeshambe = Offset(w * 0.50f, h * 0.22f)    // سه شنبه (PEAK - Center)
                    val pCharshanbe = Offset(w * 0.35f, h * 0.54f)  // چهارشنبه
                    val pPanjshanbe = Offset(w * 0.20f, h * 0.36f)  // پنجشنبه
                    val pJomeh = Offset(w * 0.05f, h * 0.80f)        // جمعه / آینده (Far Left - Dashed)

                    // Continuous Solid Path starting from شنبه (Right) to پنجشنبه (Left)
                    val solidPath = Path().apply {
                        moveTo(pShanbe.x, pShanbe.y)
                        cubicTo(
                            w * 0.90f, h * 0.65f,
                            w * 0.85f, h * 0.52f,
                            pYekshanbe.x, pYekshanbe.y
                        )
                        cubicTo(
                            w * 0.75f, h * 0.48f,
                            w * 0.70f, h * 0.46f,
                            pDoshanbe.x, pDoshanbe.y
                        )
                        cubicTo(
                            w * 0.60f, h * 0.28f,
                            w * 0.55f, h * 0.22f,
                            pSeshambe.x, pSeshambe.y
                        )
                        cubicTo(
                            w * 0.45f, h * 0.38f,
                            w * 0.40f, h * 0.54f,
                            pCharshanbe.x, pCharshanbe.y
                        )
                        cubicTo(
                            w * 0.30f, h * 0.52f,
                            w * 0.25f, h * 0.36f,
                            pPanjshanbe.x, pPanjshanbe.y
                        )
                    }

                    // Soft Gradient Fill below solid curve
                    val fillPath = Path().apply {
                        addPath(solidPath)
                        lineTo(pPanjshanbe.x, h)
                        lineTo(pShanbe.x, h)
                        close()
                    }

                    clipRect(
                        left = w * (1f - animProgress.value),
                        top = 0f,
                        right = w,
                        bottom = h
                    ) {
                        // Draw Gradient Area below chart
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF8B5CF6).copy(alpha = 0.22f),
                                    Color(0xFF8B5CF6).copy(alpha = 0.03f),
                                    Color.Transparent
                                )
                            )
                        )

                        // Vertical Dashed Guideline at Peak (سه شنبه)
                        drawLine(
                            color = Color(0xFFDDD6FE),
                            start = pSeshambe,
                            end = Offset(pSeshambe.x, h),
                            strokeWidth = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )

                        // Draw Main Purple Line
                        drawPath(
                            path = solidPath,
                            color = Color(0xFF8B5CF6),
                            style = Stroke(
                                width = 3.5.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )

                        // Dashed Path to "جمعه" (Future Day on Far Left)
                        val dashedPath = Path().apply {
                            moveTo(pPanjshanbe.x, pPanjshanbe.y)
                            cubicTo(
                                w * 0.15f, h * 0.50f,
                                w * 0.10f, h * 0.75f,
                                pJomeh.x, pJomeh.y
                            )
                        }
                        drawPath(
                            path = dashedPath,
                            color = Color(0xFFC4B5FD),
                            style = Stroke(
                                width = 2.8.dp.toPx(),
                                cap = StrokeCap.Round,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            )
                        )

                        // Passed Days Nodes (Solid Filled Circles)
                        val passedNodes = listOf(
                            pShanbe, pYekshanbe, pDoshanbe,
                            pCharshanbe, pPanjshanbe
                        )
                        passedNodes.forEach { pt ->
                            drawCircle(color = Color(0xFF8B5CF6), radius = 5.dp.toPx(), center = pt)
                        }

                        // Peak Circle at "سه شنبه" (Highlighted Solid Circle)
                        drawCircle(color = Color(0xFF8B5CF6), radius = 6.5.dp.toPx(), center = pSeshambe)
                        drawCircle(color = Color.White, radius = 2.5.dp.toPx(), center = pSeshambe)

                        // Future Day Node ("جمعه" - Empty / Outlined Circle)
                        drawCircle(
                            color = Color(0xFF8B5CF6),
                            radius = 5.dp.toPx(),
                            center = pJomeh,
                            style = Stroke(width = 2.dp.toPx())
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 3.dp.toPx(),
                            center = pJomeh
                        )
                    }
                }

                // Floating Purple Badge Tooltip above Peak ("سه شنبه" at Center)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = (boxWidth.value * 0.40f).dp, top = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .shadow(2.dp, RoundedCornerShape(12.dp), clip = false)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF8B5CF6))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (selectedFilter == "عملکرد روزانه") "۱۴:۰۰" else if (selectedFilter == "عملکرد ماهانه") "هفته ۲" else "سه شنبه",
                            color = Color.White,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    // Small Dot below tooltip
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF8B5CF6))
                    )
                }
                }

                // X-Axis Day Labels Row at Bottom (ordered Right to Left in RTL Row)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(start = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // First item in RTL Row renders on FAR RIGHT -> "شنبه"
                    val days = when (selectedFilter) {
                        "عملکرد روزانه" -> listOf("۸:۰۰", "۱۰:۰۰", "۱۲:۰۰", "۱۴:۰۰", "۱۶:۰۰", "۱۸:۰۰", "۲۰:۰۰")
                        "عملکرد ماهانه" -> listOf("هفته ۴", "هفته ۳", "هفته ۲", "هفته ۱")
                        else -> listOf("شنبه", "یکشنبه", "دوشنبه", "سه شنبه", "چهارشنبه", "پنجشنبه", "جمعه")
                    }
                    val highlightDay = when (selectedFilter) {
                        "عملکرد روزانه" -> "۱۴:۰۰"
                        "عملکرد ماهانه" -> "هفته ۲"
                        else -> "سه شنبه"
                    }
                    days.forEach { day ->
                        Text(
                            text = day,
                            fontSize = 10.sp,
                            color = if (hasPerformanceData && day == highlightDay) Color(0xFF8B5CF6) else Color(0xFF94A3B8),
                            fontWeight = if (hasPerformanceData && day == highlightDay) FontWeight.Black else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MountainPerformanceChart() {
    OverallPerformanceChart()
}

@Composable
fun DashboardTopCard(
    isLoading: Boolean = false,
    onLoginClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        DashboardTopHeader(
            isLoading = isLoading,
            onProfileClick = onProfileClick,
            onLoginClick = onLoginClick
        )
        Top4StatsRow(
            rank = "۰",
            level = "سطح ۰",
            totalStudyHours = "۰ ساعت",
            points = "۰",
            isLoading = isLoading
        )
        OverallPerformanceChart(
            hasPerformanceData = false,
            isLoading = isLoading,
            onStartClick = onLoginClick
        )
    }
}

@Composable
fun TopDashboardSection() {
    val colors = LocalShetabColors.current
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(1f, tween(1200, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .background(Color.Transparent)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val padding = 36.dp.toPx()
            val stepX = (w - 2 * padding) / 6f

            val x0 = w - padding
            val x1 = w - padding - stepX
            val x2 = w - padding - 2 * stepX
            val x3 = w - padding - 3 * stepX
            val x4 = w - padding - 4 * stepX
            val x5 = w - padding - 5 * stepX
            val x6 = w - padding - 6 * stepX

            val y0 = h * 0.70f
            val y1 = h * 0.75f
            val y2 = h * 0.55f
            val y3 = h * 0.60f
            val y4 = h * 0.50f
            val y5 = h * 0.65f
            val y6 = h * 0.58f

            val points = listOf(
                Offset(w, h * 0.65f),
                Offset(x0, y0),
                Offset(x1, y1),
                Offset(x2, y2),
                Offset(x3, y3),
                Offset(x4, y4),
                Offset(x5, y5)
            )

            val fullLinePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 0 until points.size - 1) {
                    val p1 = points[i]
                    val p2 = points[i + 1]
                    val cx = (p1.x + p2.x) / 2
                    cubicTo(cx, p1.y, cx, p2.y, p2.x, p2.y)
                }
            }

            val fullFilledPath = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 0 until points.size - 1) {
                    val p1 = points[i]
                    val p2 = points[i + 1]
                    val cx = (p1.x + p2.x) / 2
                    cubicTo(cx, p1.y, cx, p2.y, p2.x, p2.y)
                }
                lineTo(x5, h)
                lineTo(w, h)
                close()
            }

            clipRect(
                left = w - w * animProgress.value,
                top = 0f,
                right = w,
                bottom = h
            ) {
                drawPath(
                    path = fullFilledPath,
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(colors.chartLine.copy(alpha = 0.28f), Color.Transparent)
                    )
                )
                drawPath(
                    path = fullLinePath,
                    color = colors.chartLine,
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }

            val dashedPoints = listOf(
                Offset(x5, y5),
                Offset(x6, y6),
                Offset(0f, h * 0.60f)
            )
            val dashedLinePath = Path().apply {
                moveTo(dashedPoints.first().x, dashedPoints.first().y)
                for (i in 0 until dashedPoints.size - 1) {
                    val p1 = dashedPoints[i]
                    val p2 = dashedPoints[i + 1]
                    val cx = (p1.x + p2.x) / 2
                    cubicTo(cx, p1.y, cx, p2.y, p2.x, p2.y)
                }
            }
            
            val animatedDashedPath = Path()
            val measureDashed = PathMeasure()
            measureDashed.setPath(dashedLinePath, false)
            measureDashed.getSegment(0f, measureDashed.length * animProgress.value, animatedDashedPath, true)

            drawPath(
                path = animatedDashedPath,
                color = colors.chartLine,
                style = Stroke(
                    width = 4.dp.toPx(), 
                    cap = StrokeCap.Round, 
                    join = StrokeJoin.Round,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
                )
            )

            if (animProgress.value > 0.95f) {
                drawCircle(
                    color = colors.chartLine, 
                    radius = 8.dp.toPx(), 
                    center = Offset(x5, y5)
                )
                drawCircle(
                    color = colors.cardBg,
                    radius = 3.dp.toPx(),
                    center = Offset(x5, y5)
                )
            }
        }

        AnimatedVisibility(
            visible = animProgress.value > 0.95f,
            enter = fadeIn() + scaleIn()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)
                        val paddingPx = 36.dp.toPx()
                        val stepXPx = (constraints.maxWidth - 2 * paddingPx) / 6f
                        val targetX = constraints.maxWidth - paddingPx - 5 * stepXPx
                        val targetY = constraints.maxHeight * 0.65f

                        val xPos = (targetX - placeable.width / 2).toInt()
                        val yPos = (targetY - placeable.height - 8.dp.toPx()).toInt()

                        layout(constraints.maxWidth, constraints.maxHeight) {
                            placeable.place(xPos, yPos)
                        }
                    },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.tooltipBg)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("امروز", color = colors.tooltipText, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                    Canvas(modifier = Modifier.size(width = 10.dp, height = 6.dp)) {
                        val p = Path().apply {
                            moveTo(0f, 0f)
                            lineTo(size.width, 0f)
                            lineTo(size.width / 2, size.height)
                            close()
                        }
                        drawPath(p, colors.tooltipBg)
                    }
                    Box(modifier = Modifier
                        .width(1.5.dp)
                        .height(20.dp)
                        .background(colors.tooltipBg))
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
                .padding(horizontal = 24.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ChartDay("پنج", "۲۵")
            ChartDay("جمعه", "۲۶")
            ChartDay("شنبه", "۲۷")
            ChartDay("یک", "۲۸")
            ChartDay("دو", "۲۹")
            ChartDay("امروز", "۳۰")
            ChartDay("فردا", "۳۱")
        }
    }
}

@Composable
fun ChartDay(day: String, num: String) {
    val colors = LocalShetabColors.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
         Text(day, fontWeight = FontWeight.Bold, color = colors.primaryText, fontSize = 12.sp)
         Text(num, color = colors.secondaryText, fontSize = 11.sp)
    }
}

@Composable
fun TodayTasksSection() {
    val colors = LocalShetabColors.current
    
    var task1 by remember { mutableStateOf(false) }
    var task2 by remember { mutableStateOf(false) }
    var task3 by remember { mutableStateOf(false) }
    
    val totalTasks = 3
    val completedCount = (if (task1) 1 else 0) + (if (task2) 1 else 0) + (if (task3) 1 else 0)
    val progressPercent = (completedCount * 100) / totalTasks
    
    val animatedProgress by animateFloatAsState(
        targetValue = completedCount.toFloat() / totalTasks.toFloat(),
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "progress_circular"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🎯 برنامه‌ریزی مطالعاتی امروز شما",
                color = colors.primaryText,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Box(
                modifier = Modifier
                    .background(colors.accentMain.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "$completedCount از $totalTasks کار نهایی",
                    color = colors.accentMain,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = colors.cardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TaskCheckItem(
                        title = "ریاضیات جامع کنکور - مبحث مثلثات",
                        time = "۲ ساعت",
                        isDone = task1,
                        onCheckChange = { task1 = it }
                    )
                    
                    HorizontalDivider(color = colors.primaryText.copy(alpha = 0.05f))
                    
                    TaskCheckItem(
                        title = "تست و تمرین فیزیک دوازدهم - حرکت‌شناسی",
                        time = "۱.۵ ساعت",
                        isDone = task2,
                        onCheckChange = { task2 = it }
                    )
                    
                    HorizontalDivider(color = colors.primaryText.copy(alpha = 0.05f))
                    
                    TaskCheckItem(
                        title = "خلاصه‌نویسی شیمی دهم - آرایش الکترونی",
                        time = "۱ ساعت",
                        isDone = task3,
                        onCheckChange = { task3 = it }
                    )
                }

                Box(
                    modifier = Modifier.size(90.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val accentMain = colors.accentMain
                    val cardIconBg = colors.cardIconBg
                    Canvas(modifier = Modifier.fillMaxSize().padding(6.dp)) {
                        drawArc(
                            color = cardIconBg,
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 14f, cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = accentMain,
                            startAngle = -90f,
                            sweepAngle = animatedProgress * 360f,
                            useCenter = false,
                            style = Stroke(width = 14f, cap = StrokeCap.Round)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$progressPercent%",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = colors.primaryText
                        )
                        Text(
                            text = "تکمیل شده",
                            fontSize = 8.sp,
                            color = colors.secondaryText,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskCheckItem(
    title: String,
    time: String,
    isDone: Boolean,
    onCheckChange: (Boolean) -> Unit
) {
    val colors = LocalShetabColors.current
    val animatedBg by animateColorAsState(
        targetValue = if (isDone) colors.accentMain.copy(alpha = 0.05f) else Color.Transparent,
        label = "item_done_bg"
    )
    val animatedTextAlpha by animateFloatAsState(
        targetValue = if (isDone) 0.5f else 1.0f,
        label = "item_done_alpha"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(animatedBg)
            .clickable { onCheckChange(!isDone) }
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (isDone) colors.accentMain else colors.primaryText.copy(alpha = 0.05f))
                .border(2.dp, if (isDone) colors.accentMain else colors.primaryText.copy(alpha = 0.15f), RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Canvas(modifier = Modifier.size(10.dp)) {
                    val path = Path().apply {
                        moveTo(size.width * 0.15f, size.height * 0.5f)
                        lineTo(size.width * 0.42f, size.height * 0.78f)
                        lineTo(size.width * 0.88f, size.height * 0.22f)
                    }
                    drawPath(
                        path = path,
                        color = Color.White,
                        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.width(10.dp))
        
        Column(
            modifier = Modifier
                .weight(1f)
                .alpha(animatedTextAlpha),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = colors.primaryText,
                textAlign = TextAlign.Start,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_feature_clock),
                    contentDescription = null,
                    modifier = Modifier.size(10.dp),
                    tint = colors.secondaryText
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(time, fontSize = 8.sp, color = colors.secondaryText, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
