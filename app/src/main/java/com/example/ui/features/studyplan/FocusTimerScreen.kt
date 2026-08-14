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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

fun Int.toPersianString(): String = this.toString().toPersianNumber()

enum class TimerMode(val title: String) {
    FOCUS("زمان مطالعه"),
    SHORT_BREAK("استراحت کوتاه"),
    LONG_BREAK("استراحت طولانی")
}

enum class TimerStyle(val title: String) {
    CIRCLE("دایره‌ای مدرن"),
    LUXURY_FULLSCREEN("سینمایی لوکس")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusTimerScreen(navController: NavController, taskId: Int) {
    val colors = LocalShetabColors.current
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("pomodoro_prefs", Context.MODE_PRIVATE) }
    
    var focusMins by remember { mutableIntStateOf(sharedPrefs.getInt("focus_mins", 25)) }
    var shortBreakMins by remember { mutableIntStateOf(sharedPrefs.getInt("short_break_mins", 5)) }
    var longBreakMins by remember { mutableIntStateOf(sharedPrefs.getInt("long_break_mins", 15)) }
    var intervalsBeforeLongBreak by remember { mutableIntStateOf(sharedPrefs.getInt("intervals", 3)) }
    
    var currentStyle by remember { mutableStateOf(TimerStyle.valueOf(sharedPrefs.getString("timer_style", TimerStyle.CIRCLE.name) ?: TimerStyle.CIRCLE.name)) }
    
    val db = remember { com.example.data.AppDatabase.getDatabase(context) }
    val task by db.taskDao().getTaskById(taskId).collectAsStateWithLifecycle(initialValue = null)
    val scope = rememberCoroutineScope()

    var currentMode by rememberSaveable { mutableStateOf(TimerMode.FOCUS) }
    var completedIntervals by rememberSaveable { mutableIntStateOf(0) }
    
    var totalSeconds by rememberSaveable { mutableIntStateOf(focusMins * 60) }
    var remainingSeconds by rememberSaveable { mutableIntStateOf(focusMins * 60) }
    var isRunning by rememberSaveable { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    var showBatteryDialog by remember { mutableStateOf(false) }

    val view = LocalView.current
    DisposableEffect(isRunning) {
        if (isRunning) {
            view.keepScreenOn = true
        }
        onDispose {
            view.keepScreenOn = false
        }
    }

    LaunchedEffect(Unit) {
        val pm = context.getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
        val isIgnoring = pm.isIgnoringBatteryOptimizations(context.packageName)
        val hasShown = sharedPrefs.getBoolean("has_shown_battery_opt_dialog", false)
        
        if (!isIgnoring && !hasShown) {
            showBatteryDialog = true
        }
    }

    BackHandler(enabled = isRunning || remainingSeconds < totalSeconds) {
        if (isRunning || remainingSeconds < totalSeconds) {
            showExitDialog = true
        } else {
            navController.popBackStack()
        }
    }

    // White Noise Sound Selection State
    var selectedSound by rememberSaveable { mutableStateOf("سکوت ملو") }
    val focusSounds = listOf("سکوت ملو", "باران بهاری 🌧️", "فرکانس ۴۳۲Hz 🧠", "سنگ کوهستان 🏕️", "گیتار آرامبخش 🎵")

    val soundPlayer = remember { FocusSoundPlayer() }
    DisposableEffect(isRunning, selectedSound) {
        if (isRunning && selectedSound != "سکوت ملو") {
            soundPlayer.start(selectedSound)
        } else {
            soundPlayer.stop()
        }
        onDispose {
            soundPlayer.stop()
        }
    }

    LaunchedEffect(task?.id) {
        task?.let { t ->
            if (t.focusDuration > 0) {
                focusMins = t.focusDuration
                shortBreakMins = t.restDuration
                if (!isRunning && currentMode == TimerMode.FOCUS) {
                    totalSeconds = focusMins * 60
                    remainingSeconds = totalSeconds
                }
            }
        }
    }
    
    var expectedEndTime by rememberSaveable { mutableLongStateOf(0L) }

    LaunchedEffect(focusMins, shortBreakMins, longBreakMins, currentMode) {
        if (!isRunning) {
            totalSeconds = when(currentMode) {
                TimerMode.FOCUS -> focusMins * 60
                TimerMode.SHORT_BREAK -> shortBreakMins * 60
                TimerMode.LONG_BREAK -> longBreakMins * 60
            }
            remainingSeconds = totalSeconds
        }
    }
    
    LaunchedEffect(isRunning) {
        if (isRunning) {
            val nowInitial = System.currentTimeMillis()
            // If expectedEndTime is totally in the past or zero, we just started or resumed after a long pause
            if (expectedEndTime < nowInitial || !isRunning) {
                expectedEndTime = nowInitial + remainingSeconds * 1000L
            }
            
            while (isRunning && remainingSeconds > 0) {
                delay(100L) // Precision check
                val now = System.currentTimeMillis()
                val rem = ((expectedEndTime - now) / 1000).toInt()
                if (rem != remainingSeconds) {
                    remainingSeconds = if (rem > 0) rem else 0
                }
            }
            if (remainingSeconds <= 0 && isRunning) {
                isRunning = false
                // Auto switch Pomodoro states
                when (currentMode) {
                    TimerMode.FOCUS -> {
                        completedIntervals++
                        task?.let { t ->
                            val newCompleted = t.completedCycles + 1
                            val isDone = newCompleted >= t.totalCycles
                            db.taskDao().updateTask(t.copy(completedCycles = newCompleted, isCompleted = isDone || t.isCompleted))
                        }
                        if (completedIntervals >= intervalsBeforeLongBreak) {
                            currentMode = TimerMode.LONG_BREAK
                            completedIntervals = 0
                        } else {
                            currentMode = TimerMode.SHORT_BREAK
                        }
                    }
                    TimerMode.SHORT_BREAK, TimerMode.LONG_BREAK -> {
                        currentMode = TimerMode.FOCUS
                    }
                }
                totalSeconds = when(currentMode) {
                    TimerMode.FOCUS -> focusMins * 60
                    TimerMode.SHORT_BREAK -> shortBreakMins * 60
                    TimerMode.LONG_BREAK -> longBreakMins * 60
                }
                remainingSeconds = totalSeconds
            }
        }
    }

    val progress = if (totalSeconds > 0) remainingSeconds.toFloat() / totalSeconds else 0f
    
    // Smooth progress animation
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 800, easing = LinearEasing),
        label = "progress"
    )

    // Breathing scale animation during task focus
    val infiniteTransition = rememberInfiniteTransition(label = "AtmospherePulse")
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ScaleBreathe"
    )

    fun getCycleString(cycles: Int): String {
        val persianNums = arrayOf("اول", "دوم", "سوم", "چهارم", "پنجم", "ششم", "هفتم", "هشتم", "نهم", "دهم", "یازدهم", "دوازدهم")
        return if (cycles < persianNums.size) persianNums[cycles] else "${cycles + 1}"
    }

    if (currentStyle == TimerStyle.LUXURY_FULLSCREEN) {
        // --- 1. PREMIUM CINEMATIC FULLSCREEN STUDY CLOCK ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF020208))
                .clickable { isRunning = !isRunning },
            contentAlignment = Alignment.Center
        ) {
            // Background cosmic stardust drifting canvas
            DriftingCosmosCanvas(isRunning = isRunning)

            // Giant immersive ambient ring
            Box(
                modifier = Modifier
                    .size(340.dp)
                    .blur(20.dp)
                    .background(colors.accentMain.copy(alpha = 0.08f * breathingScale), CircleShape)
            )

            val minutes = remainingSeconds / 60
            val seconds = remainingSeconds % 60
            
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { 
                            if (isRunning || remainingSeconds < totalSeconds) {
                                showExitDialog = true
                            } else {
                                navController.popBackStack()
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.White.copy(alpha = 0.07f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "نمای عاری از حواس‌پرتی 🌌",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = { showSettings = true },
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.White.copy(alpha = 0.07f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                }

                // Mid segment representing countdown status
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = when(currentMode) {
                            TimerMode.FOCUS -> colors.accentMain.copy(alpha = 0.15f)
                            else -> Color(0xFF10B981).copy(alpha = 0.15f)
                        },
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        if (isRunning) Color(0xFFFBBF24) else Color(0xFFEF4444),
                                        CircleShape
                                    )
                            )
                            Text(
                                text = currentMode.title,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Large glowing digital digits
                    Box(contentAlignment = Alignment.Center) {
                        TimeTicker(
                            minutes = minutes, 
                            seconds = seconds, 
                            fontSize = 114.sp, 
                            color = Color.White, 
                            fontWeight = FontWeight.ExtraLight
                        )
                    }

                    task?.let { t ->
                        val cycleText = if (currentMode == TimerMode.FOCUS) " • دوره مطالعاتی ${getCycleString(t.completedCycles)}" else ""
                        Text(
                            text = "${t.title}$cycleText",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Bottom guidelines
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(bottom = 32.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (isRunning && selectedSound != "سکوت ملو") {
                            EqualizerBarAnimated(isRunning = true, customColor = Color.White)
                        }
                        Text(
                            text = if (isRunning) "برای توقف، روی هرجای صفحه بزنید." else "برای از سرگیری، ضربه بزنید.",
                            color = Color.White.copy(alpha = 0.4f),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "State",
                        tint = Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    } else {
        // --- 2. LUXURIOUS CIRCULAR MODERN STUDY TIMER ---
        Box(modifier = Modifier.fillMaxSize()) {
            AppBackground()
            
            Column(modifier = Modifier.fillMaxSize()) {
                // Redesigned Aesthetic Header
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.statusBarsPadding()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF1F5F9))
                                    .clickable { 
                                        if (isRunning || remainingSeconds < totalSeconds) {
                                            showExitDialog = true
                                        } else {
                                            navController.popBackStack()
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "بازگشت",
                                    tint = Color(0xFF1E1B4B),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Text(
                                text = "تمرکز و بهره‌وری علمی 🧠",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E1B4B)
                            )

                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF1F5F9))
                                    .clickable { showSettings = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "تنظیمات",
                                    tint = Color(0xFF1E1B4B),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
    
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                        .navigationBarsPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    
                    // A. Interactive Segment Control for TimerMode with Durations & Auto-Cycle Info
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF1F5F9), RoundedCornerShape(20.dp))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TimerMode.values().forEach { mode ->
                                val selected = mode == currentMode
                                val mins = when (mode) {
                                    TimerMode.FOCUS -> focusMins
                                    TimerMode.SHORT_BREAK -> shortBreakMins
                                    TimerMode.LONG_BREAK -> longBreakMins
                                }
                                val scale by animateFloatAsState(if (selected) 1.02f else 1.0f, label = "TabScale")
                                
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .scale(scale)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            if (selected) Color(0xFF8B5CF6) else Color.Transparent
                                        )
                                        .clickable {
                                            if (!isRunning) {
                                                currentMode = mode
                                                totalSeconds = mins * 60
                                                remainingSeconds = totalSeconds
                                            }
                                        }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = mode.title,
                                        fontSize = 12.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selected) Color.White else Color(0xFF64748B),
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showSettings = true },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F3FF)),
                            border = BorderStroke(1.dp, Color(0xFFDDD6FE))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF8B5CF6),
                                    modifier = Modifier.size(18.dp)
                                )
                                val safeIntervals = if (intervalsBeforeLongBreak > 0) intervalsBeforeLongBreak else 1
                                val currentIntervalNum = (completedIntervals % safeIntervals) + 1
                                val nextStepText = if (currentIntervalNum >= intervalsBeforeLongBreak) "استراحت طولانی 🌴" else "استراحت کوتاه ☕"
                                Text(
                                    text = "جلسه ${currentIntervalNum.toPersianString()} از ${intervalsBeforeLongBreak.toPersianString()} مطالعه ➔ بعدی: $nextStepText",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E1B4B)
                                )
                            }
                        }
                    }

                    // B. Stunning Dial & Timer Clock with breathing neon effect
                    Box(
                        modifier = Modifier
                            .size(280.dp)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Ambient Soft Breathing Background Glow
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .scale(breathingScale)
                                .shadow(
                                    elevation = 16.dp,
                                    shape = CircleShape,
                                    ambientColor = Color(0xFF8B5CF6).copy(alpha = 0.25f),
                                    spotColor = Color(0xFF8B5CF6).copy(alpha = 0.25f)
                                )
                                .background(Color.White, CircleShape)
                        )

                        val sweepGradient = remember {
                            Brush.sweepGradient(
                                colors = listOf(
                                    Color(0xFF8B5CF6),
                                    Color(0xFFA855F7),
                                    Color(0xFF8B5CF6)
                                )
                            )
                        }

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidthVal = 14.dp.toPx()
                            
                            // Background track ring
                            drawArc(
                                color = Color(0xFFF3E8FF),
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = strokeWidthVal, cap = StrokeCap.Round)
                            )
                            
                            // Dynamic progress arc
                            drawArc(
                                brush = sweepGradient,
                                startAngle = -90f,
                                sweepAngle = (1f - animatedProgress) * 360f,
                                useCenter = false,
                                style = Stroke(width = strokeWidthVal, cap = StrokeCap.Round)
                            )
                        }
                        
                        val minutes = remainingSeconds / 60
                        val seconds = remainingSeconds % 60
                        
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFF3E8FF)
                            ) {
                                Text(
                                    text = if (currentMode == TimerMode.FOCUS) "🎯 در حال یادگیری" else "☕ وقت استراحت",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF7C3AED),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            TimeTicker(
                                minutes = minutes, 
                                seconds = seconds, 
                                fontSize = 52.sp, 
                                color = Color(0xFF1E1B4B), 
                                fontWeight = FontWeight.Black
                            )

                            // Status bullet indicator
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(
                                            if (isRunning) Color(0xFF10B981) else Color(0xFFEF4444),
                                            CircleShape
                                        )
                                )
                                Text(
                                    text = if (isRunning) "فعال" else "متوقف شده",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }

                    // C. Tactile Controls & Action Buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        // Quick Subtract 5 Mins Button
                        Button(
                            onClick = {
                                if (!isRunning) {
                                    val newMins = maxOf(1, focusMins - 5)
                                    focusMins = newMins
                                    totalSeconds = focusMins * 60
                                    remainingSeconds = totalSeconds
                                }
                            },
                            enabled = !isRunning,
                            shape = CircleShape,
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF3E8FF),
                                contentColor = Color(0xFF8B5CF6)
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Text("-۵ د", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        // Stop/Reset Button
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                                .clickable {
                                    isRunning = false
                                    remainingSeconds = totalSeconds
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Stop",
                                tint = Color(0xFF1E1B4B),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        
                        // Play / Pause central orb button
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .shadow(8.dp, CircleShape)
                                .background(Color(0xFF8B5CF6), CircleShape)
                                .clip(CircleShape)
                                .clickable { isRunning = !isRunning },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isRunning) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(34.dp)
                            )
                        }

                        // Quick Add 5 Mins Button
                        Button(
                            onClick = {
                                if (!isRunning) {
                                    val newMins = minOf(180, focusMins + 5)
                                    focusMins = newMins
                                    totalSeconds = focusMins * 60
                                    remainingSeconds = totalSeconds
                                }
                            },
                            enabled = !isRunning,
                            shape = CircleShape,
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF3E8FF),
                                contentColor = Color(0xFF8B5CF6)
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Text("+۵ د", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // D. Beautiful Subject Detail Glassmorphic Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(20.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            task?.let { t ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        Text(
                                            text = "هدف مطالعه جاری:",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF64748B)
                                        )
                                        Text(
                                            text = t.title,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E1B4B),
                                            maxLines = 2,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        color = Color(0xFFF3E8FF)
                                    ) {
                                        Text(
                                            text = "دوره ${(t.completedCycles).toString().toPersianNumber()} از ${(t.totalCycles).toString().toPersianNumber()}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF8B5CF6),
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    repeat(t.totalCycles) { index ->
                                        val active = index < t.completedCycles
                                        val isCurrent = index == t.completedCycles && currentMode == TimerMode.FOCUS
                                        
                                        Box(
                                            modifier = Modifier
                                                .padding(horizontal = 4.dp)
                                                .size(if (isCurrent) 11.dp else 9.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    when {
                                                        active -> Color(0xFF8B5CF6)
                                                        isCurrent -> Color(0xFF8B5CF6).copy(alpha = 0.7f)
                                                        else -> Color(0xFFE2E8F0)
                                                    }
                                                )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            val explanationText = when {
                                !isRunning -> "ثانیه ثانیه تلاش تو مسیر موفقیتت رو می‌سازه. روی شروع کلیک کن! ⚡"
                                currentMode == TimerMode.FOCUS -> "گوشی را روی حالت تمرکز قرار داده و تمام مهارکت رو برای یادگیری بذار. 🎯"
                                else -> "کمی چشمانت رو ببند، آب بنوش و قدری استراحت کن. ☕"
                            }
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFFF8FAFC))
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = explanationText,
                                    color = Color(0xFF475569),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    // E. Interactive Audio Flow & White Noise Soundscape
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(20.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(
                                        imageVector = Icons.Default.Headset,
                                        contentDescription = "sound",
                                        tint = Color(0xFF8B5CF6),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "اصوات تمرکز و محیطی",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E1B4B)
                                    )
                                }

                                if (isRunning && selectedSound != "سکوت ملو") {
                                    EqualizerBarAnimated(isRunning = true, customColor = Color(0xFF8B5CF6))
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(horizontal = 2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(focusSounds) { sound ->
                                    val isSelected = sound == selectedSound
                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        color = if (isSelected) Color(0xFF8B5CF6) else Color(0xFFF8FAFC),
                                        border = BorderStroke(
                                            1.dp, 
                                            if (isSelected) Color(0xFF8B5CF6) else Color(0xFFE2E8F0)
                                        ),
                                        modifier = Modifier.clickable { selectedSound = sound }
                                    ) {
                                        Text(
                                            text = sound,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else Color(0xFF475569),
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // Modal Settings Sheet
    if (showSettings) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showSettings = false },
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .background(Color(0xFFCBD5E1), CircleShape)
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 36.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    text = "⚙️ تنظیمات تایمر تمرکز",
                    color = Color(0xFF1E1B4B),
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
                
                TimerStyleSelector(selectedStyle = currentStyle, onStyleChanged = { currentStyle = it }, colors = colors)

                TimerSettingItem(title = "⏱️ زمان مطالعه (دقیقه)", value = focusMins, onValueChange = { focusMins = it }, colors = colors)
                TimerSettingItem(title = "☕ زمان استراحت کوتاه (دقیقه)", value = shortBreakMins, onValueChange = { shortBreakMins = it }, colors = colors)
                TimerSettingItem(title = "🌴 زمان استراحت طولانی (دقیقه)", value = longBreakMins, onValueChange = { longBreakMins = it }, colors = colors)
                TimerSettingItem(title = "🔄 تعداد مطالعه قبل از استراحت طولانی", value = intervalsBeforeLongBreak, onValueChange = { intervalsBeforeLongBreak = it }, colors = colors)
                
                Button(
                    onClick = {
                        sharedPrefs.edit().apply {
                            putInt("focus_mins", focusMins)
                            putInt("short_break_mins", shortBreakMins)
                            putInt("long_break_mins", longBreakMins)
                            putInt("intervals", intervalsBeforeLongBreak)
                            putString("timer_style", currentStyle.name)
                            apply()
                        }
                        isRunning = false
                        totalSeconds = when(currentMode) {
                            TimerMode.FOCUS -> focusMins * 60
                            TimerMode.SHORT_BREAK -> shortBreakMins * 60
                            TimerMode.LONG_BREAK -> longBreakMins * 60
                        }
                        remainingSeconds = totalSeconds
                        showSettings = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("ذخیره و اعمال تنظیمات", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = {
                Text(
                    text = "قوانین تمرکز 🛡️",
                    color = Color(0xFF1E1B4B),
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Text(
                    text = "تایمر تمرکز فعال است. در صورت خروج محدودیت‌های مطالعه از بین می‌رود و پیشرفت فعلی از دست می‌رود. مطمئن هستید که می‌خواهید خارج شوید؟",
                    color = Color(0xFF475569),
                    fontSize = 13.sp,
                    lineHeight = 22.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    isRunning = false
                    navController.popBackStack()
                }) {
                    Text("خروج و توقف", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showExitDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("ادامه مطالعه", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }

    if (showBatteryDialog) {
        AlertDialog(
            onDismissRequest = { 
                showBatteryDialog = false
                sharedPrefs.edit().putBoolean("has_shown_battery_opt_dialog", true).apply()
            },
            title = {
                Text(
                    text = "جلوگیری از توقف تایمر 🔋",
                    color = Color(0xFF1E1B4B),
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Text(
                    text = "سیستم‌عامل اندروید ممکن است هنگام قفل شدن صفحه، برنامه‌ها را برای ذخیره باتری ببندد. برای اینکه تایمر تمرکز شما قطع نشود، با کلیک روی دکمه زیر، محدودیت باتری را برای این برنامه بردارید (تغییر به Unrestricted).",
                    color = Color(0xFF475569),
                    fontSize = 13.sp,
                    lineHeight = 22.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showBatteryDialog = false
                        sharedPrefs.edit().putBoolean("has_shown_battery_opt_dialog", true).apply()
                        try {
                            val intent = android.content.Intent(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                            intent.data = android.net.Uri.parse("package:" + context.packageName)
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("تنظیمات باتری", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showBatteryDialog = false
                    sharedPrefs.edit().putBoolean("has_shown_battery_opt_dialog", true).apply()
                }) {
                    Text("باشه، بعدا", color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

class FocusSoundPlayer {
    private var audioTrack: android.media.AudioTrack? = null
    private var isPlaying = false
    private var thread: Thread? = null

    fun start(soundType: String) {
        stop()
        if (soundType == "سکوت ملو") return
        isPlaying = true
        thread = Thread {
            val sampleRate = 8000
            val numSamples = sampleRate
            val buffer = ShortArray(numSamples)
            val minBufferSize = android.media.AudioTrack.getMinBufferSize(
                sampleRate,
                android.media.AudioFormat.CHANNEL_OUT_MONO,
                android.media.AudioFormat.ENCODING_PCM_16BIT
            )
            try {
                audioTrack = android.media.AudioTrack(
                    android.media.AudioManager.STREAM_MUSIC,
                    sampleRate,
                    android.media.AudioFormat.CHANNEL_OUT_MONO,
                    android.media.AudioFormat.ENCODING_PCM_16BIT,
                    maxOf(minBufferSize, buffer.size * 2),
                    android.media.AudioTrack.MODE_STREAM
                )
                audioTrack?.play()
                
                var angle = 0.0
                val freq = when {
                    soundType.contains("۴۳۲") || soundType.contains("432") -> 432.0
                    soundType.contains("باران") -> 0.0
                    soundType.contains("سنگ") -> 120.0
                    else -> 220.0
                }

                while (isPlaying) {
                    for (i in buffer.indices) {
                        if (freq == 0.0) {
                            buffer[i] = (kotlin.random.Random.nextInt(-1500, 1500)).toShort()
                        } else {
                            buffer[i] = (kotlin.math.sin(angle) * 1200).toInt().toShort()
                            angle += 2.0 * Math.PI * freq / sampleRate
                            if (angle > 2.0 * Math.PI) {
                                angle -= 2.0 * Math.PI
                            }
                            if (soundType.contains("سنگ")) {
                                buffer[i] = (buffer[i] + kotlin.random.Random.nextInt(-300, 300)).toShort()
                            }
                        }
                    }
                    audioTrack?.write(buffer, 0, buffer.size)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread?.start()
    }

    fun stop() {
        isPlaying = false
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            // Ignore
        }
        audioTrack = null
        thread = null
    }
}





