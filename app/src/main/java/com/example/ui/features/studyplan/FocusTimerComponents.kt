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
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Headset
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.ui.core.components.AppBackground
import com.example.ui.theme.LocalShetabColors
import com.example.ui.core.toPersianNumber
import kotlinx.coroutines.delay
import kotlin.random.Random
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalView

@Composable
fun TimerStyleSelector(selectedStyle: TimerStyle, onStyleChanged:(TimerStyle) -> Unit, colors: com.example.ui.theme.ShetabColorPalette) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("نمای بصری تایمر", color = Color(0xFF1E1B4B), fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF8FAFC), RoundedCornerShape(16.dp))
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TimerStyle.values().forEach { style ->
                val isSelected = selectedStyle == style
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0xFF8B5CF6) else Color.Transparent)
                        .clickable { onStyleChanged(style) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = style.title,
                        color = if (isSelected) Color.White else Color(0xFF64748B),
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun TimerSettingItem(title: String, value: Int, onValueChange: (Int) -> Unit, colors: com.example.ui.theme.ShetabColorPalette) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8FAFC), RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, color = Color(0xFF1E1B4B), fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF8B5CF6).copy(alpha = 0.12f))
                    .clickable { if (value > 1) onValueChange(value - 1) },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = Color(0xFF8B5CF6), modifier = Modifier.size(16.dp))
            }
            Text(
                text = value.toPersianString(),
                color = Color(0xFF1E1B4B),
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                modifier = Modifier.widthIn(min = 28.dp),
                textAlign = TextAlign.Center
            )
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF8B5CF6))
                    .clickable { onValueChange(value + 1) },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, contentDescription = "Increase", tint = Color.White, modifier = Modifier.size(16.dp))
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
