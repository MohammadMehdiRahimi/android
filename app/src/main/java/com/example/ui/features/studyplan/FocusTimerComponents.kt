package com.example.ui.features.studyplan

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.ui.core.components.AppBackground
import com.example.ui.theme.IranSansFontFamily
import com.example.ui.theme.LocalShetabColors
import com.example.ui.core.toPersianNumber
import kotlinx.coroutines.delay
import kotlin.random.Random
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalView

@Composable
fun StudyCyclesStepper(
    totalRounds: Int,
    completedRounds: Int,
    breakDurationMins: Int,
    isBreak: Boolean = false,
    modifier: Modifier = Modifier
) {
    val displayRounds = maxOf(3, totalRounds)
    val breakTimeText = "${breakDurationMins.toPersianString()}:۰۰"

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (index in 0 until displayRounds) {
                val roundNum = index + 1
                val isCompleted = index < completedRounds
                val isActive = (index == completedRounds && !isBreak) || (isCompleted && index == displayRounds - 1)
                val isPending = index > completedRounds

                // Round Step Column
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier.size(36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            isActive -> {
                                // Active: Glowing purple halo border with solid purple inner circle and white number
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .border(2.dp, Color(0xFFC4B5FD), CircleShape)
                                        .padding(2.5.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF6366F1)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = roundNum.toPersianString(),
                                        color = Color.White,
                                        fontFamily = IranSansFontFamily,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            isCompleted -> {
                                // Completed: Soft purple circle with checkmark or number
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEEF2FF))
                                        .border(1.2.dp, Color(0xFF818CF8), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = roundNum.toPersianString(),
                                        color = Color(0xFF6366F1),
                                        fontFamily = IranSansFontFamily,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            else -> {
                                // Pending: Light gray border with gray number
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFF8FAFC))
                                        .border(1.2.dp, Color(0xFFCBD5E1), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = roundNum.toPersianString(),
                                        color = Color(0xFF64748B),
                                        fontFamily = IranSansFontFamily,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Labels below circle
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        Text(
                            text = "دور ${roundNum.toPersianString()}",
                            fontFamily = IranSansFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = when {
                                isActive -> "در حال اجرا"
                                isCompleted -> "انجام شده"
                                else -> "در انتظار"
                            },
                            fontFamily = IranSansFontFamily,
                            fontSize = 10.sp,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                            color = when {
                                isActive -> Color(0xFF6366F1)
                                isCompleted -> Color(0xFF4F46E5)
                                else -> Color(0xFF94A3B8)
                            }
                        )
                    }
                }

                // Connector between steps
                if (index < displayRounds - 1) {
                    val isLineActive = index < completedRounds || (index == 0 && isActive)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "استراحت",
                            fontFamily = IranSansFontFamily,
                            fontSize = 10.sp,
                            color = if (isLineActive) Color(0xFF475569) else Color(0xFF94A3B8),
                            fontWeight = FontWeight.Medium
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Connecting line
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .background(
                                        if (isLineActive) Color(0xFF818CF8) else Color(0xFFCBD5E1),
                                        CircleShape
                                    )
                            )
                            // Subtle node dot in center of connector
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isLineActive) Color(0xFF6366F1) else Color(0xFF94A3B8)
                                    )
                            )
                        }

                        Text(
                            text = breakTimeText,
                            fontFamily = IranSansFontFamily,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isLineActive) Color(0xFF475569) else Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EqualizerBarAnimated(isRunning: Boolean, customColor: Color? = null) {
    val infiniteTransition = rememberInfiniteTransition(label = "Equalizer")
    val heights = List(4) { index ->
        if (isRunning) {
            infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(Random.nextInt(350, 700), easing = EaseInOutSine),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "eq_$index"
            ).value
        } else {
            0.2f
        }
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.height(14.dp)
    ) {
        heights.forEach { heightBase ->
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight(heightBase)
                    .background(customColor ?: LocalShetabColors.current.accentMain, RoundedCornerShape(100.dp))
            )
        }
    }
}
@Composable
fun DriftingCosmosCanvas(isRunning: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "CosmicStars")
    
    // We create multiple continuous drift indices
    val starsCount = 18
    val starStates = List(starsCount) { i ->
        val duration = remember { Random.nextInt(12000, 24000) }
        val startXFraction = remember { Random.nextFloat() }
        val startYFraction = remember { Random.nextFloat() }
        
        val alpha by if (isRunning) {
            infiniteTransition.animateFloat(
                initialValue = 0.1f,
                targetValue = 0.7f,
                animationSpec = infiniteRepeatable(
                    animation = tween(duration / 2, easing = EaseInOutSine),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "star_alpha_$i"
            )
        } else {
            remember { mutableStateOf(0.3f) }
        }

        val driftX by if (isRunning) {
            infiniteTransition.animateFloat(
                initialValue = -25f,
                targetValue = 25f,
                animationSpec = infiniteRepeatable(
                    animation = tween(duration, easing = EaseInOutSine),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "star_drift_x_$i"
            )
        } else {
            remember { mutableStateOf(0f) }
        }

        Triple(startXFraction, startYFraction, alpha to driftX)
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        starStates.forEachIndexed { idx, state ->
            val (xFrac, yFrac, animPair) = state
            val (alphaVal, driftVal) = animPair
            val baseX = width * xFrac
            val baseY = height * yFrac
            
            drawCircle(
                color = Color.White.copy(alpha = alphaVal),
                radius = if (idx % 3 == 0) 2.5f else 1.5f,
                center = Offset(baseX + driftVal, baseY + (driftVal * 0.5f))
            )
        }
    }
}
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TimeTicker(minutes: Int, seconds: Int, fontSize: androidx.compose.ui.unit.TextUnit, color: Color, fontWeight: FontWeight) {
    androidx.compose.runtime.CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Ltr) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AnimatedContent(
                targetState = minutes,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInVertically(animationSpec = tween(300)) { height -> -height } + fadeIn(animationSpec = tween(300))).togetherWith(
                            slideOutVertically(animationSpec = tween(300)) { height -> height } + fadeOut(animationSpec = tween(300))
                        )
                    } else {
                        (slideInVertically(animationSpec = tween(300)) { height -> height } + fadeIn(animationSpec = tween(300))).togetherWith(
                            slideOutVertically(animationSpec = tween(300)) { height -> -height } + fadeOut(animationSpec = tween(300))
                        )
                    }
                },
                label = "minutesAnimation"
            ) { targetMin ->
                Text(
                    text = String.format("%02d", targetMin).toPersianNumber(),
                    fontSize = fontSize,
                    fontWeight = fontWeight,
                    color = color,
                    maxLines = 1
                )
            }
            
            Text(
                text = ":",
                fontSize = fontSize,
                fontWeight = fontWeight,
                color = color,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 2.dp)
            )

            AnimatedContent(
                targetState = seconds,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInVertically(animationSpec = tween(300)) { height -> -height } + fadeIn(animationSpec = tween(300))).togetherWith(
                            slideOutVertically(animationSpec = tween(300)) { height -> height } + fadeOut(animationSpec = tween(300))
                        )
                    } else {
                        (slideInVertically(animationSpec = tween(300)) { height -> height } + fadeIn(animationSpec = tween(300))).togetherWith(
                            slideOutVertically(animationSpec = tween(300)) { height -> -height } + fadeOut(animationSpec = tween(300))
                        )
                    }
                },
                label = "secondsAnimation"
            ) { targetSec ->
                Text(
                    text = String.format("%02d", targetSec).toPersianNumber(),
                    fontSize = fontSize,
                    fontWeight = fontWeight,
                    color = color,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun FocusBottomAudioPlayer(
    currentSound: String,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onStop: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onOpenSoundPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Simulated track progress (looping with timer or smooth animated position)
    var progressFraction by remember { mutableFloatStateOf(0.348f) } // approx 08:43 of 25:00
    var isRepeatActive by rememberSaveable { mutableStateOf(false) }

    // Auto-advance progress slightly while audio is active
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(1000L)
            progressFraction = (progressFraction + 0.002f).let { 
                if (it > 1f) {
                    if (isRepeatActive) 0f else 1f
                } else it 
            }
        }
    }

    val totalDurationSecs = 25 * 60
    val currentSecs = (progressFraction * totalDurationSecs).toInt()
    val curM = currentSecs / 60
    val curS = currentSecs % 60
    val currentFormatted = String.format("%02d:%02d", curM, curS).toPersianNumber()
    val totalFormatted = "۲۵:۰۰"

    // Primary Violet brand color from design system
    val brandViolet = Color(0xFF6C4ED9)
    val navyText = Color(0xFF1E1B4B)
    val graySubText = Color(0xFF64748B)

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 0.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
            shadowElevation = 1.5.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Main Row: [Right: Info + Controls] and [Left: Album Art]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Right in RTL: Music Title & Controls
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        // Title & Subtitle
                        Column(
                            modifier = Modifier.clickable { onOpenSoundPicker() }
                        ) {
                            Text(
                                text = "موسیقی تمرکز",
                                fontFamily = IranSansFontFamily,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = navyText,
                                maxLines = 1
                            )
                            Text(
                                text = if (currentSound == "سکوت ملو") "صدای طبیعت و موج آرام" else currentSound,
                                fontFamily = IranSansFontFamily,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Normal,
                                color = graySubText,
                                maxLines = 1
                            )
                        }

                        // Controls Row (RTL Order: 1. Next, 2. Previous, 3. Stop, 4. Repeat)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(1.dp)
                        ) {
                            // 1. آهنگ بعدی (Next Track)
                            IconButton(
                                onClick = onNext,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SkipNext,
                                    contentDescription = "آهنگ بعدی",
                                    tint = brandViolet,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // 2. آهنگ قبلی (Previous Track)
                            IconButton(
                                onClick = onPrevious,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SkipPrevious,
                                    contentDescription = "آهنگ قبلی",
                                    tint = brandViolet,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // 3. دکمه Stop (با آیکون مربع Stop، یا در صورت متوقف بودن Play)
                            IconButton(
                                onClick = {
                                    if (isPlaying) onStop() else onTogglePlay()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                                    contentDescription = if (isPlaying) "توقف" else "پخش",
                                    tint = brandViolet,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // 4. دکمه Repeat (با هایلایت بسیار ملایم بنفش در صورت فعال بودن)
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (isRepeatActive) brandViolet.copy(alpha = 0.12f) else Color.Transparent)
                                    .clickable { isRepeatActive = !isRepeatActive },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isRepeatActive) Icons.Filled.RepeatOne else Icons.Outlined.Repeat,
                                    contentDescription = "تکرار",
                                    tint = if (isRepeatActive) brandViolet else graySubText,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Left in RTL: Album Art (40x40dp, 10dp rounded corner)
                    Surface(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onOpenSoundPicker() },
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFEDE9FE)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF6C4ED9),
                                            Color(0xFF818CF8),
                                            Color(0xFF38BDF8)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Landscape,
                                contentDescription = "کاور موسیقی تمرکز",
                                tint = Color.White.copy(alpha = 0.95f),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                // Progress Bar with current time and total time in Persian
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = currentFormatted,
                        fontFamily = IranSansFontFamily,
                        fontSize = 10.sp,
                        color = graySubText,
                        fontWeight = FontWeight.Medium
                    )

                    // Minimal custom progress bar with tiny thumb
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Background inactive track
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.5.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFFE2E8F0))
                        )

                        // Active track
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = progressFraction.coerceIn(0f, 1f))
                                .height(2.5.dp)
                                .align(Alignment.CenterStart)
                                .clip(RoundedCornerShape(2.dp))
                                .background(brandViolet)
                        )

                        // Small circular thumb
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterStart)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction = progressFraction.coerceIn(0f, 1f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(brandViolet)
                                )
                            }
                        }
                    }

                    Text(
                        text = totalFormatted,
                        fontFamily = IranSansFontFamily,
                        fontSize = 10.sp,
                        color = graySubText,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}


