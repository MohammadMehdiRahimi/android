package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.animation.*
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.R
import com.example.ui.theme.LocalShetabColors
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Rect
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle

@Composable
fun SplashScreens(navController: NavController) {
    val colors = LocalShetabColors.current
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        com.example.ui.core.components.AppBackground()

        // Very soft luxurious ambient gradient overlay at the bottom for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            colors.bgMain.copy(alpha = 0.5f),
                            colors.bgMain
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Top Bar with logo and Skip Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Shetab Logo",
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF490BD4), RoundedCornerShape(8.dp))
                            .padding(4.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Text(
                        text = "شـتـاب",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primaryText,
                        letterSpacing = 1.sp
                    )
                }

                if (pagerState.currentPage < 2) {
                    TextButton(
                        onClick = {
                            navController.navigate("login_phone") {
                                popUpTo("splash") { inclusive = true }
                            }
                        }
                    ) {
                        Text(
                            text = "رد کردن",
                            color = colors.secondaryText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Pager content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Page Visual Graphic container
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .aspectRatio(1.1f)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        when (page) {
                            0 -> {
                                // Visual 1: Premium Glow brand frame
                                Box(contentAlignment = Alignment.Center) {
                                    // Elegant golden circular aura
                                    Canvas(modifier = Modifier.size(240.dp)) {
                                        drawCircle(
                                            brush = Brush.radialGradient(
                                                colors = listOf(
                                                    colors.accentMain.copy(alpha = 0.25f),
                                                    Color.Transparent
                                                )
                                            )
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(140.dp)
                                            .shadow(24.dp, shape = CircleShape, clip = false)
                                            .background(Color(0xFF490BD4), CircleShape)
                                            .border(1.5.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                                            .padding(24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.logo),
                                            contentDescription = "Shetab Brand",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                }
                            }
                            1 -> {
                                // Visual 2: Intelligent Study Scheduler card mockup
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .shadow(16.dp, RoundedCornerShape(24.dp))
                                        .background(colors.cardBg, RoundedCornerShape(24.dp))
                                        .border(1.dp, colors.accentMain.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                                        .padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "برنامه امروز شما", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.primaryText)
                                        Box(
                                            modifier = Modifier
                                                .background(colors.accentMain.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(text = "رتبه ۱", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = colors.accentMain)
                                        }
                                    }

                                    // Mock planner task 1
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(colors.bgMain.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                            .border(1.dp, colors.cardIconBg.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                        .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .background(colors.accentMain, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(text = "شبیه‌ساز فیزیک کنکور", fontSize = 12.sp, color = colors.primaryText, fontWeight = FontWeight.SemiBold)
                                            Text(text = "ساعت ۱۷:۰۰ تا ۱۹:۳۰", fontSize = 10.sp, color = colors.secondaryText)
                                        }
                                    }

                                    // Mock planner task 2
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(colors.bgMain.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .background(colors.secondaryText.copy(alpha = 0.4f), CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(text = "مرور لغات ادبیات", fontSize = 12.sp, color = colors.primaryText)
                                            Text(text = "ساعت ۲۰:۰۰", fontSize = 10.sp, color = colors.secondaryText)
                                        }
                                    }
                                }
                            }
                            2 -> {
                                // Visual 3: Graph and deep statistics mockup
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .shadow(16.dp, RoundedCornerShape(24.dp))
                                        .background(colors.cardBg, RoundedCornerShape(24.dp))
                                        .border(1.dp, colors.accentMain.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                                        .padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Text(text = "روند درصد و تراز تجمعی", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.primaryText)

                                    // Simple canvas line chart simulation representing study growth
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(80.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Canvas(modifier = Modifier.fillMaxSize()) {
                                            val points = listOf(0.1f, 0.25f, 0.2f, 0.55f, 0.48f, 0.9f)
                                            val path = androidx.compose.ui.graphics.Path()
                                            val stepX = size.width / (points.size - 1)
                                            points.forEachIndexed { i, p ->
                                                val x = i * stepX
                                                val y = size.height * (1f - p)
                                                if (i == 0) {
                                                    path.moveTo(x, y)
                                                } else {
                                                    path.lineTo(x, y)
                                                }
                                            }
                                            drawPath(
                                                path = path,
                                                color = colors.accentMain,
                                                style = androidx.compose.ui.graphics.drawscope.Stroke(
                                                    width = 4.dp.toPx(),
                                                    cap = androidx.compose.ui.graphics.StrokeCap.Round,
                                                    join = androidx.compose.ui.graphics.StrokeJoin.Round
                                                )
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceAround
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(text = "میزان رشد", fontSize = 10.sp, color = colors.secondaryText)
                                            Text(text = "۲۴٪+", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(text = "ساعت مطالعه", fontSize = 10.sp, color = colors.secondaryText)
                                            Text(text = "۵۴ ساعت", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.primaryText)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    val title = when (page) {
                        0 -> "به شتـاب خوش آمدید!"
                        1 -> "برنامه‌ریزی هوشمند و هدفمند"
                        else -> "به سوی رتبه‌های برتر کنکور"
                    }

                    val desc = when (page) {
                        0 -> "بال‌های سرعت و صعود تحصیلی شما. با شتاب مسیر پر پیچ و خم کنکور و آزمون‌های تحصیلی را پر قدرت‌تر از همیشه بپیمایید."
                        1 -> "فرقی نمی‌کنه توی کدوم آزمون شرکت می‌کنی، درس‌هات رو ثبت کن و برنامه‌ای کاملاً متناسب با سرعت و ظرفیتت دریافت کن."
                        else -> "با بررسی مستمر تله‌های تستی، فلش‌کارت‌های پیشرفته و مانیتورینگ آنلاین پیشرفت‌ها، آینده موفقیت‌آمیز رو بساز!"
                    }

                    Text(
                        text = title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = colors.primaryText,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = desc,
                        fontSize = 14.sp,
                        color = colors.secondaryText.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Animated Pager Indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    val isSelected = pagerState.currentPage == index
                    val animWidth by animateFloatAsState(
                        targetValue = if (isSelected) 24f else 8f,
                        animationSpec = tween(durationMillis = 300),
                        label = "width"
                    )
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(animWidth.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) colors.accentMain else colors.accentMain.copy(alpha = 0.2f)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Premium Luxury Button
            Button(
                onClick = {
                    if (pagerState.currentPage < 2) {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        navController.navigate("login_phone") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .padding(bottom = 36.dp)
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Text(
                    text = if (pagerState.currentPage < 2) "ادامـه مسیر" else "بـزن بـریم!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.bgMain
                )
            }
        }
    }
}

@Composable
fun ShetabLightWaveHeader(showLogo: Boolean = false) {
    val infiniteTransition = rememberInfiniteTransition(label = "chart")
    
    // Smooth infinite breathing/pulse animations for the leading glowing node
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 6f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseRadius"
    )

    // Smooth continuous progress representing the rolling camera movement along the growth path (medium speed)
    val travelingProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "travelingProgress"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val centerY = h * 0.5f

            // No background grid, no dark color rectangles to ensure complete transparency!

            val headX = w * 0.72f
            val t = travelingProgress

            // 1. Calculate and draw the trailing growth path
            val pathCurve = Path()
            var first = true

            // Sample points from x = 0 up to headX to build a smooth high-performance path
            for (xi in 0..headX.toInt() step 2) {
                val x = xi.toFloat()
                val dx = headX - x
                // Wave history: tx is the relative time at screen position x
                val tx = t - dx / (w * 1.3f)
                val angleX = tx * 2f * Math.PI.toFloat()
                
                // Growth path formula: goes up, has a slight dip, and climbs back up beautifully
                val y = centerY - 
                        h * 0.25f * Math.sin(angleX.toDouble()).toFloat() - 
                        h * 0.12f * Math.sin((angleX * 2f + Math.PI.toFloat() / 3f).toDouble()).toFloat()

                if (first) {
                    pathCurve.moveTo(x, y)
                    first = false
                } else {
                    pathCurve.lineTo(x, y)
                }
            }

            // Ensure the path extends exactly to the head coordinate
            val headAngle = t * 2f * Math.PI.toFloat()
            val headY = centerY - 
                    h * 0.25f * Math.sin(headAngle.toDouble()).toFloat() - 
                    h * 0.12f * Math.sin((headAngle * 2f + Math.PI.toFloat() / 3f).toDouble()).toFloat()
            
            if (!first) {
                pathCurve.lineTo(headX, headY)
            }

            // Draw the trailing gradient growth line (from transparent blue/indigo to highly visible emerald green)
            drawPath(
                path = pathCurve,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF3B82F6).copy(alpha = 0.0f),  // Fully transparent start
                        Color(0xFF6366F1).copy(alpha = 0.4f),  // Indigo middle
                        Color(0xFF10B981).copy(alpha = 0.95f)  // Emerald active green at head
                    )
                ),
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = 4.5.dp.toPx(),
                    cap = androidx.compose.ui.graphics.StrokeCap.Round,
                    join = androidx.compose.ui.graphics.StrokeJoin.Round
                )
            )

            // 2. Draw a sleek arrow head representing progress and direction (made larger, circles removed)
            val prevX = headX - 4f
            val dxPrev = headX - prevX
            val txPrev = t - dxPrev / (w * 1.3f)
            val angleXPrev = txPrev * 2f * Math.PI.toFloat()
            val prevY = centerY - 
                    h * 0.25f * Math.sin(angleXPrev.toDouble()).toFloat() - 
                    h * 0.12f * Math.sin((angleXPrev * 2f + Math.PI.toFloat() / 3f).toDouble()).toFloat()

            val diffX = headX - prevX
            val diffY = headY - prevY
            val angleRad = Math.atan2(diffY.toDouble(), diffX.toDouble()).toFloat()
            val angleDegrees = Math.toDegrees(angleRad.toDouble()).toFloat()

            drawContext.canvas.save()
            drawContext.transform.rotate(degrees = angleDegrees, pivot = androidx.compose.ui.geometry.Offset(headX, headY))
            
            val arrowPath = Path().apply {
                moveTo(headX + 8.dp.toPx(), headY) // Tip of the arrow
                lineTo(headX - 12.dp.toPx(), headY - 9.dp.toPx())
                lineTo(headX - 6.dp.toPx(), headY)
                lineTo(headX - 12.dp.toPx(), headY + 9.dp.toPx())
                close()
            }
            drawPath(
                path = arrowPath,
                color = Color(0xFF10B981)
            )
            
            drawContext.canvas.restore()
        }
    }
}

@Composable
fun FeatureHighlightRow(icon: String, title: String, desc: String) {
    // Standard RTL layout row: Icon is on the right, texts are on the left
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon is on the right of the row (first in RTL composition)
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(Color(0xFFE3F2FD), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = icon, fontSize = 20.sp)
        }

        // Column for texts is on the left of the row
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF263238),
                fontFamily = com.example.ui.theme.IranSansFontFamily,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                fontSize = 12.sp,
                color = Color(0xFF546E7A),
                fontFamily = com.example.ui.theme.IranSansFontFamily,
                textAlign = TextAlign.Start,
                lineHeight = 18.sp
            )
        }
    }
}
