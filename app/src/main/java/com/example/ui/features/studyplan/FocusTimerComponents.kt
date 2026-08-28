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
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.PauseCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Celebration
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCompletionBottomSheet(
    taskTitle: String,
    onDismiss: () -> Unit,
    onCompleteFull: () -> Unit,
    onCompletePartial: (percent: Int, note: String?) -> Unit,
    onPauseAndExit: () -> Unit,
) {
    var isPartialMode by remember { mutableStateOf(false) }
    var partialPercent by remember { mutableFloatStateOf(50f) }
    var partialNote by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .background(Color(0xFFCBD5E1), CircleShape)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFF3E8FF),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isPartialMode) Icons.Outlined.EditNote else Icons.Outlined.Celebration,
                                contentDescription = null,
                                tint = PlanPurple,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Text(
                        text = if (isPartialMode) "ثبت پایان نیمه‌کاره" else "پایان مطالعه و اتمام تسک",
                        fontFamily = com.example.ui.theme.IranSansFontFamily,
                        fontSize = 16.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E1B4B)
                    )
                }

                if (isPartialMode) {
                    TextButton(onClick = { isPartialMode = false }) {
                        Text(
                            text = "بازگشت",
                            fontFamily = com.example.ui.theme.IranSansFontFamily,
                            color = PlanPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "بستن",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            if (taskTitle.isNotBlank()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFE0E7FF),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.TaskAlt,
                                    contentDescription = null,
                                    tint = Color(0xFF4338CA),
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                        Text(
                            text = taskTitle,
                            fontFamily = com.example.ui.theme.IranSansFontFamily,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF334155),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            if (!isPartialMode) {
                // 1. Full Completion Card (100%)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onCompleteFull() },
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFFFAF5FF),
                    border = BorderStroke(1.5.dp, PlanPurple.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = PlanPurple,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "اتمام کامل تسک (۱۰۰٪)",
                                fontFamily = com.example.ui.theme.IranSansFontFamily,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PlanPurpleDark
                            )
                            Text(
                                text = "مطالعه این تسک به پایان رسید و امتیاز آن ثبت می‌شود",
                                fontFamily = com.example.ui.theme.IranSansFontFamily,
                                fontSize = 11.5.sp,
                                color = Color(0xFF6B21A8)
                            )
                        }
                    }
                }

                // 2. Partial Completion Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { isPartialMode = true },
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFEEF2FF),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.PieChart,
                                    contentDescription = null,
                                    tint = Color(0xFF4F46E5),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "پایان نیمه‌کاره و ثبت پیشرفت",
                                fontFamily = com.example.ui.theme.IranSansFontFamily,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "تعیین درصد مطالعه‌شده و یادداشت برای دور بعدی",
                                fontFamily = com.example.ui.theme.IranSansFontFamily,
                                fontSize = 11.5.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }

                // 3. Pause & Exit Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onPauseAndExit() },
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.PauseCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF475569),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "توقف موقت و ذخیره زمان",
                                fontFamily = com.example.ui.theme.IranSansFontFamily,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF334155)
                            )
                            Text(
                                text = "ثبت زمان مطالعه تا این لحظه و خروج بدون اتمام تسک",
                                fontFamily = com.example.ui.theme.IranSansFontFamily,
                                fontSize = 11.5.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }

                // 4. Continue Study TextButton
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "انصراف و ادامه مطالعه",
                        fontFamily = com.example.ui.theme.IranSansFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                }
            } else {
                // Partial Mode Form
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFAF5FF),
                        border = BorderStroke(1.dp, Color(0xFFEDE9FE))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "میزان پیشرفت مطالعه:",
                                    fontFamily = com.example.ui.theme.IranSansFontFamily,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E1B4B)
                                )
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = PlanPurple
                                ) {
                                    Text(
                                        text = "${partialPercent.toInt().toPersianString()} ٪",
                                        fontFamily = com.example.ui.theme.IranSansFontFamily,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Slider(
                                value = partialPercent,
                                onValueChange = { partialPercent = it },
                                valueRange = 1f..99f,
                                steps = 97,
                                colors = SliderDefaults.colors(
                                    thumbColor = PlanPurple,
                                    activeTrackColor = PlanPurple,
                                    inactiveTrackColor = Color(0xFFE2E8F0)
                                )
                            )

                            // Quick Chips (25%, 50%, 75%, 90%)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(25, 50, 75, 90).forEach { pct ->
                                    val isSelected = partialPercent.toInt() == pct
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable { partialPercent = pct.toFloat() },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) PlanPurpleLight else Color.White,
                                        border = BorderStroke(1.dp, if (isSelected) PlanPurple else Color(0xFFE2E8F0))
                                    ) {
                                        Box(
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "$pct ٪".toPersianNumber(),
                                                fontFamily = com.example.ui.theme.IranSansFontFamily,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) PlanPurpleDark else Color(0xFF64748B)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Optional Note Input
                    OutlinedTextField(
                        value = partialNote,
                        onValueChange = { partialNote = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("توضیحات یا علت توقف (اختیاری)", fontFamily = com.example.ui.theme.IranSansFontFamily) },
                        placeholder = { Text("مثال: تا صفحه ۴۵ مطالعه شد", fontFamily = com.example.ui.theme.IranSansFontFamily) },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PlanPurple,
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        ),
                        minLines = 2,
                        maxLines = 3
                    )

                    // Submit Partial Completion Button
                    Button(
                        onClick = {
                            onCompletePartial(partialPercent.toInt(), partialNote.ifBlank { null })
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PlanPurple),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "ثبت پایان نیمه‌کاره (${partialPercent.toInt().toPersianString()}٪)",
                            fontFamily = com.example.ui.theme.IranSansFontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

