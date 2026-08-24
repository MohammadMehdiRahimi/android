package com.example.ui.main

import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.activity.compose.BackHandler
import android.app.Activity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.geometry.Offset
import androidx.navigation.NavController
import androidx.compose.ui.graphics.graphicsLayer
import com.example.ui.core.components.*
import com.example.ui.core.toPersianNumber
import com.example.ui.features.academicreport.AcademicReportScreen
import com.example.ui.features.dashboard.DashboardTopCard
import com.example.ui.features.exams.ExamsScreen
import com.example.ui.features.profile.ProfileScreen
import com.example.ui.features.studyplan.StudyPlanScreen
import com.example.ui.features.studyplan.StudyPlanSkeletonLoading
import com.example.ui.theme.AppTheme
import com.example.ui.theme.LocalShetabColors
import kotlinx.coroutines.delay

@Composable
fun ShetabApp(
    selectedTheme: AppTheme,
    navController: NavController,
    onThemeSelected: (AppTheme) -> Unit,
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val colors = LocalShetabColors.current
    val context = LocalContext.current

    val tokenManager = remember(context) {
        com.example.network.ApiClient.getTokenManager()
            ?: com.example.network.TokenManager(context)
    }
    var isGuest by remember { mutableStateOf(!tokenManager.isLoggedIn()) }
    val authVersion by tokenManager.authVersion.collectAsState()
    LaunchedEffect(authVersion) {
        isGuest = !tokenManager.isLoggedIn()
        if (isGuest && navController.currentDestination?.route != "login_phone") {
            navController.navigate("login_phone") {
                popUpTo("dashboard") { inclusive = true }
                launchSingleTop = true
            }
        }
    }
    var lastBackPressTime by remember { mutableLongStateOf(0L) }

    BackHandler {
        if (selectedTab != 0) {
            selectedTab = 0
        } else {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastBackPressTime < 2000) {
                (context as? Activity)?.finish()
            } else {
                lastBackPressTime = currentTime
                android.widget.Toast.makeText(
                    context,
                    "برای خروج، یک بار دیگر دکمه بازگشت را بزنید",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            ShetabBottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        containerColor = colors.bgMain,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            AppBackground(showPattern = false, customBgColor = colors.bgMain)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Crossfade(targetState = selectedTab, label = "tabTransition") { tabIndex ->
                    var isTabLoading by remember(tabIndex) { mutableStateOf(true) }

                    LaunchedEffect(tabIndex) {
                        isTabLoading = true
                        delay(350)
                        isTabLoading = false
                    }

                    if (isTabLoading && tabIndex != 0) {
                        when (tabIndex) {
                            1 -> StudyPlanSkeletonLoading()
                            2 -> AcademicReportSkeletonLoading()
                            3 -> ExamsSkeletonLoading()
                            4 -> ProfileSkeletonLoading()
                        }
                    } else {
                        when (tabIndex) {
                            0 -> HomeScreenContent(
                                navController = navController,
                                isGuest = isGuest,
                                isLoading = isTabLoading,
                                onLoginClick = {
                                    if (navController.currentDestination?.route != "login_phone") {
                                        navController.navigate("login_phone") {
                                            launchSingleTop = true
                                        }
                                    }
                                },
                            )
                            1 -> StudyPlanScreen(navController = navController, onBackClick = { selectedTab = 0 })
                            2 -> AcademicReportScreen(navController = navController)
                            3 -> ExamsScreen(navController = navController)
                            4 -> ProfileScreen(
                                selectedTheme = selectedTheme,
                                onThemeSelected = onThemeSelected,
                                onUpgradeClick = { navController.navigate("premium_plans") },
                                onLoggedOut = {
                                    isGuest = true
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreenContent(
    navController: NavController,
    isGuest: Boolean,
    isLoading: Boolean = false,
    onLoginClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFFBFBFD))
            .padding(top = 4.dp, bottom = 0.dp)
    ) {
        ReferenceHomeDashboard(
            navController = navController,
            isGuest = isGuest,
            onLoginClick = onLoginClick,
        )
    }
}

@Composable
fun StudyPulseHeroCard(navController: NavController) {
    val colors = LocalShetabColors.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp), clip = false)
            .border(1.dp, colors.primaryText.copy(alpha = 0.08f), RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "نبض مطالعه امروز",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = colors.primaryText
                )
            }

            // Main Row: Left (Gauge 78%) + Right (Connected Milestones)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Circular Progress Gauge (78% پیشرفت روزانه)
                Box(
                    modifier = Modifier.size(86.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val accentMain = colors.accentMain
                    val cardIconBg = colors.cardIconBg
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 9.dp.toPx()
                        drawArc(
                            color = cardIconBg,
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = accentMain,
                            startAngle = -90f,
                            sweepAngle = 280f, // ~78%
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "۷۸٪",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = colors.accentMain
                        )
                        Text(
                            text = "پیشرفت روزانه",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.secondaryText
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Right: 4 Connected Milestones with horizontal connecting line
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    // Connecting line behind icons
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .padding(horizontal = 16.dp)
                            .align(Alignment.Center)
                    ) {
                        drawLine(
                            color = colors.primaryText.copy(alpha = 0.12f),
                            start = Offset(0f, size.height / 2),
                            end = Offset(size.width, size.height / 2),
                            strokeWidth = 2.dp.toPx()
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val steps = listOf(
                            Triple("هدف امروز", "۸۰٪", Color(0xFFE8F5E9) to Color(0xFF2E7D32)),
                            Triple("مطالعه", "۶۰٪", Color(0xFFE3F2FD) to Color(0xFF1976D2)),
                            Triple("تمرکز", "۹۰٪", Color(0xFFF3E5F5) to Color(0xFF7B1FA2)),
                            Triple("تکمیل", "۷۸٪", Color(0xFFFFF8E1) to Color(0xFFF57C00))
                        )

                        val stepIcons = listOf("🎯", "📖", "〰️", "✅")

                        steps.forEachIndexed { idx, (label, valText, colorPair) ->
                            val (bgColor, fgColor) = colorPair
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(bgColor)
                                        .border(1.dp, fgColor.copy(alpha = 0.25f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stepIcons[idx],
                                        fontSize = 14.sp
                                    )
                                }

                                Text(
                                    text = label,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.primaryText
                                )

                                Text(
                                    text = valText,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = colors.secondaryText
                                )
                            }
                        }
                    }
                }
            }

            // Bottom CTA Button: Full-width blue pill "شروع مطالعه"
            Button(
                onClick = { navController.navigate("study_plan") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.accentMain
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "شروع مطالعه",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ProBannerCard(navController: NavController) {
    val colors = LocalShetabColors.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(3.dp, RoundedCornerShape(20.dp), clip = false)
            .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(20.dp))
            .clickable { navController.navigate("premium_plans") },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "فعالسازی رایگان اشتراک ۱ ماه پرو",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF92400E)
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF59E0B),
                        shadowElevation = 1.dp
                    ) {
                        Text(
                            text = "PRO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "دسترسی نامحدود به برنامه‌ریزی، آزمون و رایا",
                    fontSize = 11.sp,
                    color = Color(0xFFB45309),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFFEF3C7)),
                contentAlignment = Alignment.Center
            ) {
                Text("👑", fontSize = 24.sp)
            }
        }
    }
}

@Composable
fun FeatureGridHub(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // TOP ASYMMETRIC SECTION: Stacked Cards on Right (Leagues & Groups), Tall Lavender Card on Left
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(245.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Right Side in RTL: Column of 2 Stacked Cards
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card 1: لیگ‌های رقابتی فعال (Soft Blue)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .shadow(1.dp, RoundedCornerShape(22.dp), clip = false)
                        .clickable { navController.navigate("academic_report") },
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        // Title & Subtitle (Top Start / Right in RTL)
                        Column(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(end = 70.dp)
                        ) {
                            Text(
                                text = "لیگ‌های رقابتی فعال",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E3A8A)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "تو یک قدم تا جایزه‌های بزرگ...",
                                fontSize = 9.sp,
                                color = Color(0xFF2563EB),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // League Trophy Vector Image Artwork (Top End / Left side in RTL, enlarged)
                        Image(
                            painter = painterResource(id = R.drawable.league),
                            contentDescription = "لیگ‌های رقابتی",
                            modifier = Modifier
                                .size(72.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = 6.dp, y = (-6).dp),
                            contentScale = ContentScale.Fit
                        )

                        // Arrow Button (Bottom End / Visual Left side in RTL)
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .align(Alignment.BottomEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ChevronLeft,
                                contentDescription = null,
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        // Avatars Row (Positioned next to Arrow Button on the Left side)
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 30.dp),
                            horizontalArrangement = Arrangement.spacedBy((-6).dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val avatarColors = listOf(
                                Color(0xFF3B82F6),
                                Color(0xFF10B981),
                                Color(0xFFF59E0B)
                            )
                            avatarColors.forEach { color ->
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(1.5.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Person,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Card 2: گروه‌های مطالعاتی من (Soft Mint Green)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .shadow(1.dp, RoundedCornerShape(22.dp), clip = false)
                        .clickable { navController.navigate("my_group") },
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(end = 70.dp)
                        ) {
                            Text(
                                text = "گروه‌های مطالعاتی من",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF064E3B)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "با هم بهتر می‌تونیم",
                                fontSize = 9.sp,
                                color = Color(0xFF059669),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Group Image Icon (Top End - Enlarged)
                        Image(
                            painter = painterResource(id = R.drawable.study_group),
                            contentDescription = "گروه‌های مطالعاتی",
                            modifier = Modifier
                                .size(68.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = 6.dp, y = (-4).dp),
                            contentScale = ContentScale.Fit
                        )

                        // Arrow Button (Bottom End / Visual Left in RTL)
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .align(Alignment.BottomEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ChevronLeft,
                                contentDescription = null,
                                tint = Color(0xFF047857),
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        // Avatars Row (Positioned next to Arrow Button on the Left side)
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 30.dp),
                            horizontalArrangement = Arrangement.spacedBy((-6).dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val avatarColors = listOf(
                                Color(0xFF059669),
                                Color(0xFF0284C7),
                                Color(0xFFD97706)
                            )
                            avatarColors.forEach { color ->
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(1.5.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Person,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Left Side in RTL: Tall Lavender Purple Card (برنامه‌ریز هوشمند شتاب)
            Card(
                modifier = Modifier
                    .weight(0.92f)
                    .fillMaxHeight()
                    .shadow(2.dp, RoundedCornerShape(24.dp), clip = false)
                    .clickable { navController.navigate("study_plan") },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F3FF))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Header Text
                        Column {
                            Text(
                                text = "برنامه‌ریز هوشمند",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4C1D95)
                            )
                            Text(
                                text = "شتاب",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6D28D9)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "برنامه‌ریزی هوشمند و پیشرفت سوال",
                                fontSize = 9.sp,
                                color = Color(0xFF7C3AED),
                                lineHeight = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Center Plan Image Artwork (Significantly Enlarged)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.plan),
                                contentDescription = "برنامه‌ریز هوشمند",
                                modifier = Modifier.size(100.dp),
                                contentScale = ContentScale.Fit
                            )
                        }

                        // Purple Pill Button "شروع کنید >" (Full Width)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF7C3AED))
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "شروع کنید",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Filled.ChevronLeft,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // BOTTOM ROW: Symmetric Row (پرسش از همکلاسی‌ها on Right, آزمون‌ساز on Left)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Right Card in RTL: پرسش از همکلاسی‌ها (Soft Purple/Pink)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .shadow(1.dp, RoundedCornerShape(22.dp), clip = false)
                    .clickable { navController.navigate("peer_trouble") },
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(end = 70.dp)
                    ) {
                        Text(
                            text = "پرسش از همکلاسی‌ها",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF581C87)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "سوالت رو سریع پاسخ بگیر",
                            fontSize = 9.sp,
                            color = Color(0xFF9333EA),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Communication Image Icon (Top End - Enlarged)
                    Image(
                        painter = painterResource(id = R.drawable.communication),
                        contentDescription = "پرسش از همکلاسی‌ها",
                        modifier = Modifier
                            .size(70.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 6.dp, y = (-2).dp),
                        contentScale = ContentScale.Fit
                    )

                    // Purple Circle Arrow Button (Bottom End / Visual Left in RTL)
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF9333EA))
                            .align(Alignment.BottomEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ChevronLeft,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Left Card in RTL: آزمون‌ساز (Warm Yellow/Orange)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .shadow(1.dp, RoundedCornerShape(22.dp), clip = false)
                    .clickable { navController.navigate("exams_screen") },
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(end = 70.dp)
                    ) {
                        Text(
                            text = "آزمون‌ساز",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF78350F)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "آزمون بساز و تمرین کن",
                            fontSize = 9.sp,
                            color = Color(0xFFD97706),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Exam Image Icon (Top End - Enlarged)
                    Image(
                        painter = painterResource(id = R.drawable.exam),
                        contentDescription = "آزمون‌ساز",
                        modifier = Modifier
                            .size(70.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 6.dp, y = (-2).dp),
                        contentScale = ContentScale.Fit
                    )

                    // Orange Circle Arrow Button (Bottom End / Visual Left in RTL)
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD97706))
                            .align(Alignment.BottomEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ChevronLeft,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExamsSection(navController: NavController) {
    val colors = LocalShetabColors.current

    data class ExamInfo(
        val id: String,
        val title: String,
        val tags: List<String>,
        val questions: String,
        val duration: String,
        val topColor: Color
    )

    val exams = remember {
        listOf(
            ExamInfo(
                id = "1",
                title = "تعیین سطح",
                tags = listOf("دهم", "زیست", "جانوری"),
                questions = "۳۰ سوال",
                duration = "۴۵ دقیقه",
                topColor = Color(0xFFF59E0B)
            ),
            ExamInfo(
                id = "2",
                title = "هفته اول",
                tags = listOf("یازدهم", "ریاضی", "تابع"),
                questions = "۲۰ سوال",
                duration = "۲۵ دقیقه",
                topColor = Color(0xFF3B82F6)
            ),
            ExamInfo(
                id = "3",
                title = "هفته دوم",
                tags = listOf("دهم", "شیمی", "کیهان"),
                questions = "۲۵ سوال",
                duration = "۳۰ دقیقه",
                topColor = Color(0xFF10B981)
            ),
            ExamInfo(
                id = "4",
                title = "میان‌دوره",
                tags = listOf("دوازدهم", "فیزیک", "حرکت"),
                questions = "۴۰ سوال",
                duration = "۵۰ دقیقه",
                topColor = Color(0xFF8B5CF6)
            ),
            ExamInfo(
                id = "5",
                title = "جامع پایان دوره",
                tags = listOf("کنکور", "تجربی", "جامع"),
                questions = "۵۰ سوال",
                duration = "۷۵ دقیقه",
                topColor = Color(0xFFF43F5E)
            )
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section Header - Styled & Improved
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    colors.accentMain.copy(alpha = 0.2f),
                                    colors.accentMain.copy(alpha = 0.08f)
                                )
                            )
                        )
                        .border(1.dp, colors.accentMain.copy(alpha = 0.25f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📝", fontSize = 18.sp)
                }
                Column {
                    Text(
                        text = "آزمون‌های آنلاین",
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.Black,
                        color = colors.primaryText
                    )
                    Text(
                        text = "ارزیابی هدفمند و سنجش سطح علمی",
                        fontSize = 10.5.sp,
                        color = colors.secondaryText
                    )
                }
            }

            // Styled "All Exams" Pill Button
            Surface(
                onClick = { navController.navigate("exams_screen") },
                shape = RoundedCornerShape(20.dp),
                color = colors.accentMain.copy(alpha = 0.1f),
                contentColor = colors.accentMain,
                modifier = Modifier.border(1.dp, colors.accentMain.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "مشاهده همه",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowLeft,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Horizontal Exams Scroller - Cards with Entrance Animation & Glassmorphism Badges
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(exams) { index, exam ->
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    delay(index * 80L)
                    visible = true
                }

                val cardAlpha by animateFloatAsState(
                    targetValue = if (visible) 1f else 0f,
                    animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
                    label = "cardAlpha"
                )
                val cardTranslationY by animateFloatAsState(
                    targetValue = if (visible) 0f else 28f,
                    animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
                    label = "cardTranslationY"
                )

                Card(
                    modifier = Modifier
                        .width(210.dp)
                        .graphicsLayer {
                            alpha = cardAlpha
                            translationY = cardTranslationY
                        }
                        .shadow(3.dp, RoundedCornerShape(14.dp), clip = false)
                        .border(
                            width = 1.dp,
                            color = colors.primaryText.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { navController.navigate("exams_screen") },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.cardBg)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Top Colored Border Strip
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            exam.topColor,
                                            exam.topColor.copy(alpha = 0.4f)
                                        )
                                    )
                                )
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Title & Play Action Button Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = exam.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = colors.primaryText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(exam.topColor.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.PlayArrow,
                                        contentDescription = null,
                                        tint = exam.topColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            // Glassmorphism Badges Row - Spans full width with high legibility
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val tagGlassStyles = listOf(
                                    Triple(Color(0xFF1D4ED8), Color(0xFFEFF6FF).copy(alpha = 0.85f), Color(0xFFBFDBFE)), // Grade: Glass Blue
                                    Triple(Color(0xFF047857), Color(0xFFECFDF5).copy(alpha = 0.85f), Color(0xFFA7F3D0)), // Subject: Glass Emerald
                                    Triple(Color(0xFF6D28D9), Color(0xFFF5F3FF).copy(alpha = 0.85f), Color(0xFFDDD6FE))  // Topic: Glass Purple
                                )
                                exam.tags.forEachIndexed { tagIdx, tag ->
                                    val (textColor, bgColor, borderColor) = tagGlassStyles[tagIdx % tagGlassStyles.size]
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(bgColor)
                                            .border(0.8.dp, borderColor, RoundedCornerShape(8.dp))
                                            .padding(vertical = 4.dp, horizontal = 2.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = tag,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black,
                                            color = textColor,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }

                            // Questions & Duration Meta Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Assignment,
                                        contentDescription = null,
                                        tint = colors.secondaryText,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = exam.questions,
                                        fontSize = 10.5.sp,
                                        color = colors.secondaryText
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Timer,
                                        contentDescription = null,
                                        tint = colors.secondaryText,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = exam.duration,
                                        fontSize = 10.5.sp,
                                        color = colors.secondaryText
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudyGroupsSection(navController: NavController) {
    val colors = LocalShetabColors.current
    val context = LocalContext.current

    data class StudyGroup(
        val id: String,
        val title: String,
        val members: String,
        val tag: String,
        val tagBg: Color,
        val icon: String,
        val isJoined: Boolean = false
    )

    var groupsList by remember {
        mutableStateOf(
            listOf(
                StudyGroup(
                    id = "1",
                    title = "کنکوری‌های تجربی ۱۴۰۴",
                    members = "۲۴۸ عضو فعال • چالش روزانه",
                    tag = "🔥 سطح الماس",
                    tagBg = Color(0xFFF59E0B),
                    icon = "🧬",
                    isJoined = true
                ),
                StudyGroup(
                    id = "2",
                    title = "ماراتن زیست و شیمی",
                    members = "۱۹۵ عضو فعال • رفع اشکال",
                    tag = "🎯 چالش روزانه",
                    tagBg = Color(0xFF10B981),
                    icon = "🧪"
                ),
                StudyGroup(
                    id = "3",
                    title = "باشگاه سحرخیزان ۵ صبح",
                    members = "۳۲۰ عضو فعال • پومودورو",
                    tag = "⚡ تمرکز بالا",
                    tagBg = Color(0xFF6366F1),
                    icon = "⏰"
                ),
                StudyGroup(
                    id = "4",
                    title = "تست‌زنی حسابان و فیزیک",
                    members = "۱۴۲ عضو فعال • رفع اشکال",
                    tag = "📐 ریاضی-فیزیک",
                    tagBg = Color(0xFF3B82F6),
                    icon = "📐"
                )
            )
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }
    var newGroupName by remember { mutableStateOf("") }
    var newGroupTag by remember { mutableStateOf("") }

    val filteredGroups = remember(searchQuery, groupsList) {
        if (searchQuery.isBlank()) {
            groupsList
        } else {
            groupsList.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.tag.contains(searchQuery, ignoreCase = true) ||
                it.members.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header Row with compact Create Group Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.accentMain.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👥", fontSize = 16.sp)
                }
                Column {
                    Text(
                        text = "گروه‌های مطالعاتی",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = colors.primaryText
                    )
                    Text(
                        text = "مطالعه گروهی و چالش‌های هم‌مسیران",
                        fontSize = 10.5.sp,
                        color = colors.secondaryText
                    )
                }
            }

            // Compact Create Group Button
            Surface(
                onClick = { showCreateDialog = true },
                shape = RoundedCornerShape(10.dp),
                color = colors.accentMain,
                contentColor = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "ایجاد گروه",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                placeholder = {
                    Text(
                        text = "جستجوی نام یا موضوع گروه...",
                        fontSize = 12.sp,
                        color = colors.secondaryText
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = colors.secondaryText,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = null,
                                tint = colors.secondaryText,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = colors.cardBg,
                    unfocusedContainerColor = colors.cardBg,
                    focusedBorderColor = colors.accentMain,
                    unfocusedBorderColor = colors.primaryText.copy(alpha = 0.1f),
                    focusedTextColor = colors.primaryText,
                    unfocusedTextColor = colors.primaryText
                )
            )
        }

        // Vertical List of Groups (Stacked one below another)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (filteredGroups.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "گروهی با این مشخصات یافت نشد",
                        fontSize = 12.sp,
                        color = colors.secondaryText
                    )
                }
            } else {
                filteredGroups.forEach { group ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(16.dp), clip = false)
                            .border(1.dp, colors.primaryText.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                            .clickable { navController.navigate("my_group") },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.cardBg)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Avatar Icon Box
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colors.accentMain.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(group.icon, fontSize = 20.sp)
                            }

                            // Group Details Column
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = group.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.primaryText,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(group.tagBg.copy(alpha = 0.12f))
                                            .padding(horizontal = 5.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = group.tag,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = group.tagBg
                                        )
                                    }
                                }

                                Text(
                                    text = group.members,
                                    fontSize = 10.5.sp,
                                    color = colors.secondaryText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Action Button
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (group.isJoined) colors.accentMain.copy(alpha = 0.12f) else colors.accentMain,
                                contentColor = if (group.isJoined) colors.accentMain else Color.White
                            ) {
                                Text(
                                    text = if (group.isJoined) "ورود" else "عضویت",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Create New Group Dialog
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = {
                Text(
                    text = "ایجاد گروه مطالعاتی جدید",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primaryText
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = newGroupName,
                        onValueChange = { newGroupName = it },
                        label = { Text("نام گروه", fontSize = 12.sp) },
                        placeholder = { Text("مثلاً: کنکوری‌های ۱۴۰۴", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newGroupTag,
                        onValueChange = { newGroupTag = it },
                        label = { Text("موضوع یا هدف گروه", fontSize = 12.sp) },
                        placeholder = { Text("مثلاً: زیست‌شناسی", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newGroupName.isNotBlank()) {
                            val newGroup = StudyGroup(
                                id = (groupsList.size + 1).toString(),
                                title = newGroupName,
                                members = "۱ عضو • تازه ساخت",
                                tag = if (newGroupTag.isNotBlank()) newGroupTag else "جدید ✨",
                                tagBg = colors.accentMain,
                                icon = "📚",
                                isJoined = true
                            )
                            groupsList = listOf(newGroup) + groupsList
                            showCreateDialog = false
                            newGroupName = ""
                            newGroupTag = ""
                            android.widget.Toast.makeText(context, "گروه مطالعاتی با موفقیت ایجاد شد!", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("ایجاد گروه", fontWeight = FontWeight.Bold, color = colors.accentMain)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("انصراف", color = colors.secondaryText)
                }
            },
            containerColor = colors.cardBg,
            shape = RoundedCornerShape(18.dp)
        )
    }
}

@Composable
fun RayaCoachHub(navController: NavController) {
    val colors = LocalShetabColors.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("🤖", fontSize = 18.sp)
                Text(
                    text = "مشاور هوشمند رایا",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = colors.primaryText
                )
            }

            Text(
                text = "گفتگوی کامل ←",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = colors.accentMain,
                modifier = Modifier.clickable { navController.navigate("raya_chat") }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            RayaPromptChip(
                emoji = "⚡",
                title = "تنظیم برنامه",
                subtitle = "برنامه‌ریزی فوری",
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("raya_chat") }
            )
            RayaPromptChip(
                emoji = "✍️",
                title = "رفع اشکال",
                subtitle = "حل سوالات سخت",
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("raya_chat") }
            )
            RayaPromptChip(
                emoji = "📊",
                title = "تحلیل تراز",
                subtitle = "پیش‌بینی کنکور",
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("academic_report") }
            )
        }
    }
}

@Composable
fun RayaPromptChip(
    emoji: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colors = LocalShetabColors.current

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = colors.cardBg,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            colors.primaryText.copy(alpha = 0.08f)
        ),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = emoji, fontSize = 20.sp)
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colors.primaryText,
                maxLines = 1
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = colors.secondaryText,
                maxLines = 1
            )
        }
    }
}

@Composable
fun DailyGoalsCard(onNavigateToSchedule: () -> Unit) {
    val colors = LocalShetabColors.current
    var task1Done by remember { mutableStateOf(true) }
    var task2Done by remember { mutableStateOf(false) }
    var task3Done by remember { mutableStateOf(false) }

    val doneCount = listOf(task1Done, task2Done, task3Done).count { it }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp), clip = false)
            .border(1.dp, colors.primaryText.copy(alpha = 0.06f), RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🎯", fontSize = 18.sp)
                    Column {
                        Text(
                            text = "اهداف درسی امروز",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = colors.primaryText
                        )
                        Text(
                            text = "$doneCount از ۳ هدف تکمیل شد",
                            fontSize = 11.sp,
                            color = colors.secondaryText
                        )
                    }
                }

                Text(
                    text = "برنامه‌ریزی کامل ←",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.accentMain,
                    modifier = Modifier.clickable { onNavigateToSchedule() }
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                GoalItemRow(
                    subject = "زیست‌شناسی",
                    title = "مطالعه و خلاصله‌نویسی فصل ۳ (گوارش)",
                    time = "۶۰ دقیقه",
                    isDone = task1Done,
                    onToggle = { task1Done = !task1Done }
                )
                GoalItemRow(
                    subject = "فیزیک",
                    title = "تست‌زنی مبحث چگالی و فشار (۲۵ تست)",
                    time = "۴۵ دقیقه",
                    isDone = task2Done,
                    onToggle = { task2Done = !task2Done }
                )
                GoalItemRow(
                    subject = "ادبیات",
                    title = "مرور واژگان و قرابت معنایی درس ۵",
                    time = "۳۰ دقیقه",
                    isDone = task3Done,
                    onToggle = { task3Done = !task3Done }
                )
            }
        }
    }
}

@Composable
fun GoalItemRow(
    subject: String,
    title: String,
    time: String,
    isDone: Boolean,
    onToggle: () -> Unit
) {
    val colors = LocalShetabColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(colors.primaryText.copy(alpha = 0.03f))
            .clickable { onToggle() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(
                        if (isDone) colors.accentMain else Color.Transparent
                    )
                    .border(
                        1.5.dp,
                        if (isDone) colors.accentMain else colors.secondaryText.copy(alpha = 0.4f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isDone) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    fontSize = 12.5.sp,
                    fontWeight = if (isDone) FontWeight.Medium else FontWeight.Bold,
                    color = if (isDone) colors.secondaryText else colors.primaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subject,
                    fontSize = 10.sp,
                    color = colors.secondaryText
                )
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(colors.primaryText.copy(alpha = 0.06f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = time,
                fontSize = 10.sp,
                color = colors.secondaryText,
                fontWeight = FontWeight.Medium
            )
        }
    }
}



@Composable
fun DailyQuoteCard() {
    val colors = LocalShetabColors.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.cardBg
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            colors.accentMain.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("✨", fontSize = 24.sp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "جمله انگیزشی روز",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.accentMain
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "«موفقیت حاصل تکرار روزانه و مداوم قدم‌های کوچک است.»",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.primaryText
                )
            }
        }
    }
}

@Composable
fun ShetabBottomNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp)
                .navigationBarsPadding(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(26.dp),
                color = Color.White,
                shadowElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val navItems = listOf(
                        NavItemData(4, "پروفایل", Icons.Filled.Person, Icons.Outlined.PersonOutline),
                        NavItemData(3, "آزمون‌ساز", Icons.Filled.Assignment, Icons.Outlined.Assignment),
                        NavItemData(0, "خانه", Icons.Filled.Home, Icons.Outlined.Home),
                        NavItemData(2, "تحلیل‌گر", Icons.Filled.PieChart, Icons.Outlined.PieChart),
                        NavItemData(1, "برنامه‌ریزی", Icons.Filled.CalendarToday, Icons.Outlined.CalendarToday),
                    )
                    navItems.forEach { item ->
                        val selected = selectedTab == item.index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                ) { onTabSelected(item.index) },
                            contentAlignment = Alignment.Center,
                        ) {
                            if (item.index == 0) {
                                Text(
                                    text = "خانه",
                                    color = Color(0xFF7656F5),
                                    fontFamily = com.example.ui.theme.IranSansFontFamily,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 5.dp),
                                )
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.title,
                                        tint = if (selected) Color(0xFF7656F5) else Color(0xFF9CA4BC),
                                        modifier = Modifier.size(20.dp),
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = item.title,
                                        color = if (selected) Color(0xFF7656F5) else Color(0xFF9CA4BC),
                                        fontFamily = com.example.ui.theme.IranSansFontFamily,
                                        fontSize = 9.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        maxLines = 1,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Central Minimal Action Button for Home (خانه)
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-10).dp)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF7656F5))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onTabSelected(0) },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "خانه",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

private data class NavItemData(
    val index: Int,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)
