package com.example.ui.features.studyplan

import android.content.Context
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.network.ApiClient
import com.example.network.StudyExecutionEventDto
import com.example.network.StudyTaskDto
import com.example.network.TokenManager
import com.example.network.currentIsoUtcTimestamp
import com.example.network.safeApiCall
import com.example.ui.core.toPersianNumber
import com.example.ui.theme.IranSansFontFamily
import com.example.ui.theme.LocalShetabColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.math.cos
import kotlin.math.sin

fun Int.toPersianString(): String = this.toString().toPersianNumber()

enum class TimerMode(val title: String) {
    FOCUS("زمان مطالعه"),
    SHORT_BREAK("استراحت کوتاه"),
    LONG_BREAK("استراحت طولانی")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusTimerScreen(navController: NavController, taskId: String) {
    val colors = LocalShetabColors.current
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("pomodoro_prefs", Context.MODE_PRIVATE) }
    val scope = rememberCoroutineScope()
    val tokenManager = remember { TokenManager(context) }
    
    val numericTaskId = taskId.toIntOrNull() ?: 0
    val db = remember { com.example.data.AppDatabase.getDatabase(context) }
    val roomTask by db.taskDao().getTaskById(numericTaskId).collectAsStateWithLifecycle(initialValue = null)
    
    // Find task in StudyPlanDataCache or fetch if available
    var serverTask by remember { mutableStateOf<StudyTaskDto?>(null) }
    var taskTitle by remember { mutableStateOf("") }
    var taskSubtitle by remember { mutableStateOf("") }
    var bookName by remember { mutableStateOf("") }
    var chapterSubtitle by remember { mutableStateOf("") }
    var totalRounds by remember { mutableIntStateOf(1) }
    var completedRounds by remember { mutableIntStateOf(0) }
    var sourceType by remember { mutableStateOf("GENERATED") }

    var focusMins by remember { mutableIntStateOf(sharedPrefs.getInt("focus_mins", 25)) }
    var shortBreakMins by remember { mutableIntStateOf(sharedPrefs.getInt("short_break_mins", 5)) }
    var longBreakMins by remember { mutableIntStateOf(sharedPrefs.getInt("long_break_mins", 15)) }
    var intervalsBeforeLongBreak by remember { mutableIntStateOf(sharedPrefs.getInt("intervals", 3)) }

    var currentMode by rememberSaveable { mutableStateOf(TimerMode.FOCUS) }
    var completedIntervals by rememberSaveable { mutableIntStateOf(0) }
    
    var totalSeconds by rememberSaveable { mutableIntStateOf(focusMins * 60) }
    var elapsedSeconds by rememberSaveable { mutableIntStateOf(0) }
    var isRunning by rememberSaveable { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    var showBatteryDialog by remember { mutableStateOf(false) }
    var showSoundDialog by remember { mutableStateOf(false) }

    val view = LocalView.current
    var backendEventSequence by rememberSaveable { mutableIntStateOf(0) }

    // Fetch and bind task from Server / Cache
    LaunchedEffect(taskId) {
        // Try looking in memory cache first
        val todayStr = java.time.LocalDate.now(java.time.ZoneId.of("Asia/Tehran")).toString()
        val cachedDay = StudyPlanDataCache.getTasks(todayStr)
        val match = cachedDay?.items?.find { it.id == taskId }
        if (match != null) {
            serverTask = match
        } else if (tokenManager.isLoggedIn()) {
            val res = safeApiCall { ApiClient.apiService.getStudyTasks(todayStr) }
            if (res is com.example.network.NetworkResult.Success) {
                res.data.body?.let { dayBody ->
                    StudyPlanDataCache.putTasks(todayStr, dayBody)
                    val found = dayBody.items.find { it.id == taskId }
                    if (found != null) {
                        serverTask = found
                    }
                }
            }
        }
    }

    LaunchedEffect(serverTask, roomTask) {
        val sTask = serverTask
        val rTask = roomTask
        if (sTask != null) {
            taskTitle = sTask.title
            val rawBook = sTask.book.name.trim()
            val cleanBook = rawBook.split("•", "؛", ":", "-").firstOrNull()?.trim() ?: rawBook
            bookName = cleanBook

            val rawChapter = sTask.chapter.name.trim()
            val rawTopic = sTask.topic.name.trim()

            var cleanChapter = rawChapter
            if (cleanChapter.startsWith(cleanBook)) {
                cleanChapter = cleanChapter.substring(cleanBook.length).trim(' ', '•', '؛', ':', '-', '،')
            }
            if (cleanChapter.isBlank()) cleanChapter = rawChapter

            var cleanTopic = rawTopic
            if (cleanTopic.startsWith(cleanBook)) {
                cleanTopic = cleanTopic.substring(cleanBook.length).trim(' ', '•', '؛', ':', '-', '،')
            }

            chapterSubtitle = when {
                cleanTopic.isNotBlank() && cleanTopic != cleanChapter -> "$cleanChapter ($cleanTopic)"
                cleanChapter.isNotBlank() -> cleanChapter
                else -> ""
            }
            taskSubtitle = chapterSubtitle

            sourceType = sTask.sourceType
            totalRounds = maxOf(1, sTask.periodCount)
            intervalsBeforeLongBreak = totalRounds
            
            val durationPerRound = if (sTask.minutesPerPeriod > 0) {
                sTask.minutesPerPeriod
            } else if (sTask.plannedMinutes > 0 && sTask.periodCount > 0) {
                maxOf(5, sTask.plannedMinutes / sTask.periodCount)
            } else {
                45
            }
            
            focusMins = durationPerRound
            if (sTask.execution != null) {
                backendEventSequence = maxOf(backendEventSequence, sTask.execution.eventSequence)
            }
            if (!isRunning && currentMode == TimerMode.FOCUS) {
                totalSeconds = focusMins * 60
            }
        } else if (rTask != null) {
            taskTitle = rTask.title
            bookName = rTask.subject
            chapterSubtitle = rTask.subject
            taskSubtitle = rTask.subject
            totalRounds = maxOf(1, rTask.totalCycles)
            completedRounds = rTask.completedCycles
            intervalsBeforeLongBreak = totalRounds
            if (rTask.focusDuration > 0) {
                focusMins = rTask.focusDuration
                shortBreakMins = rTask.restDuration
                if (!isRunning && currentMode == TimerMode.FOCUS) {
                    totalSeconds = focusMins * 60
                }
            }
        }
    }

    fun sendBackendEvent(
        eventType: String,
        completionOutcome: String? = null,
        completionPercentage: Int? = null,
        note: String? = null,
        onDone: (() -> Unit)? = null,
    ) {
        if (!tokenManager.isLoggedIn()) {
            onDone?.invoke()
            return
        }
        scope.launch(Dispatchers.IO) {
            val event = StudyExecutionEventDto(
                clientEventId = UUID.randomUUID().toString(),
                expectedSequence = backendEventSequence,
                type = eventType,
                occurredAt = currentIsoUtcTimestamp(),
                completionOutcome = completionOutcome,
                completionPercent = completionPercentage,
                note = note,
            )
            val isManual = sourceType.equals("MANUAL", ignoreCase = true)
            val primaryRes = safeApiCall {
                if (isManual) {
                    ApiClient.apiService.submitManualStudyEvent(
                        taskId = taskId,
                        request = event,
                    )
                } else {
                    ApiClient.apiService.submitGeneratedStudyEvent(
                        taskId = taskId,
                        request = event,
                    )
                }
            }
            val finalRes = if (primaryRes is com.example.network.NetworkResult.Error && primaryRes.code == 404) {
                safeApiCall {
                    if (isManual) {
                        ApiClient.apiService.submitGeneratedStudyEvent(
                            taskId = taskId,
                            request = event,
                        )
                    } else {
                        ApiClient.apiService.submitManualStudyEvent(
                            taskId = taskId,
                            request = event,
                        )
                    }
                }
            } else {
                primaryRes
            }
            if (finalRes is com.example.network.NetworkResult.Success) {
                finalRes.data.body?.eventSequence?.let {
                    backendEventSequence = it
                } ?: run {
                    backendEventSequence++
                }
                if (eventType == "ACTIVITY_COMPLETED" || eventType == "ACTIVITY_MARKED_DONE") {
                    StudyPlanDataCache.clear()
                }
            } else {
                backendEventSequence++
            }
            withContext(Dispatchers.Main) {
                onDone?.invoke()
            }
        }
    }

    DisposableEffect(isRunning) {
        if (isRunning) {
            view.keepScreenOn = true
            sendBackendEvent(if (backendEventSequence == 0) "ACTIVITY_STARTED" else "ACTIVITY_RESUMED")
        } else if (backendEventSequence > 0 && elapsedSeconds > 0) {
            sendBackendEvent("ACTIVITY_PAUSED")
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

    BackHandler(enabled = isRunning || elapsedSeconds > 0) {
        if (isRunning || elapsedSeconds > 0) {
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
    
    var startTimeMillis by rememberSaveable { mutableLongStateOf(0L) }
    var initialElapsedOnStart by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(focusMins, shortBreakMins, longBreakMins, currentMode) {
        if (!isRunning) {
            totalSeconds = when(currentMode) {
                TimerMode.FOCUS -> focusMins * 60
                TimerMode.SHORT_BREAK -> shortBreakMins * 60
                TimerMode.LONG_BREAK -> longBreakMins * 60
            }
        }
    }
    
    LaunchedEffect(isRunning) {
        if (isRunning) {
            startTimeMillis = System.currentTimeMillis()
            initialElapsedOnStart = elapsedSeconds
            
            while (isRunning) {
                delay(100L) // Precision check
                val now = System.currentTimeMillis()
                val delta = ((now - startTimeMillis) / 1000).toInt()
                val newElapsed = initialElapsedOnStart + delta
                if (newElapsed != elapsedSeconds) {
                    elapsedSeconds = newElapsed
                }
            }
        }
    }

    val isOvertime = totalSeconds > 0 && elapsedSeconds >= totalSeconds
    val progress = if (totalSeconds > 0) (elapsedSeconds.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f) else 0f
    
    // Smooth progress animation
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 600, easing = LinearEasing),
        label = "progress"
    )

    val overtimeGreen = Color(0xFF10B981)
    val overtimeGreenLight = Color(0xFFDCFCE7)
    val activeProgressColor = if (isOvertime) overtimeGreen else PlanPurple
    val activeTrackColor = if (isOvertime) overtimeGreenLight else Color(0xFFF3E8FF).copy(alpha = 0.8f)

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

    // --- CIRCULAR MODERN STUDY TIMER ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBFBFE))
    ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. TOP APP BAR
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Right in RTL: Back Button
                        Surface(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .clickable {
                                    if (isRunning || elapsedSeconds > 0) {
                                        showExitDialog = true
                                    } else {
                                        navController.popBackStack()
                                    }
                                }
                                .testTag("focus_back_button"),
                            shape = CircleShape,
                            color = Color.White,
                            border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                            shadowElevation = 1.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "بازگشت",
                                    tint = PlanNavy,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Center Header Title
                        Text(
                            text = "تمرکز و مطالعه",
                            fontFamily = IranSansFontFamily,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = PlanNavy
                        )

                        // Placeholder for symmetrical layout balance
                        Spacer(modifier = Modifier.size(46.dp))
                    }
                }

                // 2. TASK INFO & CYCLES STEPPER CARD (COMPACT)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Book Title with MenuBook Icon (Defaults to reference image: فیزیک ۳ - دینامیک و قوانین حرکت 📖)
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = taskTitle.ifBlank { "فیزیک ۳ – دینامیک و قوانین حرکت" },
                                    fontFamily = IranSansFontFamily,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PlanNavy,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Outlined.MenuBook,
                                    contentDescription = null,
                                    tint = PlanPurple,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // 3-Cycles Stepper Progress (Compact)
                        StudyCyclesStepper(
                            totalRounds = totalRounds,
                            completedRounds = completedRounds,
                            breakDurationMins = shortBreakMins,
                            isBreak = currentMode != TimerMode.FOCUS
                        )
                    }
                }

                // 3. CIRCULAR CLOCK DIAL (COMPACT & CLOSER TO BUTTONS)
                Box(
                    modifier = Modifier
                        .size(260.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidthVal = 7.dp.toPx()
                        val canvasRadius = size.minDimension / 2f - 20.dp.toPx()
                        val centerOffset = Offset(size.width / 2f, size.height / 2f)

                        // Outer Dial Radial Tick Marks (60 ticks exactly as in design)
                        val totalTicks = 60
                        for (i in 0 until totalTicks) {
                            val tickAngle = (i * (360f / totalTicks)) * (Math.PI / 180f)
                            val innerR = canvasRadius + 8.dp.toPx()
                            val outerR = canvasRadius + if (i % 5 == 0) 14.dp.toPx() else 11.dp.toPx()
                            val startX = centerOffset.x + (cos(tickAngle) * innerR).toFloat()
                            val startY = centerOffset.y + (sin(tickAngle) * innerR).toFloat()
                            val endX = centerOffset.x + (cos(tickAngle) * outerR).toFloat()
                            val endY = centerOffset.y + (sin(tickAngle) * outerR).toFloat()
                            drawLine(
                                color = if (i % 5 == 0) Color(0xFFCBD5E1) else Color(0xFFE2E8F0),
                                start = Offset(startX, startY),
                                end = Offset(endX, endY),
                                strokeWidth = if (i % 5 == 0) 1.5.dp.toPx() else 1.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }

                        // Background Track Ring
                        drawCircle(
                            color = activeTrackColor,
                            radius = canvasRadius,
                            center = centerOffset,
                            style = Stroke(width = strokeWidthVal, cap = StrokeCap.Round)
                        )

                        // Dynamic Progress Arc (Clockwise progression from 12 o'clock)
                        val sweepAngle = (animatedProgress * 360f).coerceIn(0.1f, 360f)
                        drawArc(
                            color = activeProgressColor,
                            startAngle = -90f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(width = strokeWidthVal, cap = StrokeCap.Round),
                            topLeft = Offset(centerOffset.x - canvasRadius, centerOffset.y - canvasRadius),
                            size = androidx.compose.ui.geometry.Size(canvasRadius * 2f, canvasRadius * 2f)
                        )

                        // Glowing Indicator Knob at the head of the arc
                        val currentRads = ((-90f + sweepAngle) * (Math.PI / 180f)).toDouble()
                        val knobCenterX = centerOffset.x + (cos(currentRads) * canvasRadius).toFloat()
                        val knobCenterY = centerOffset.y + (sin(currentRads) * canvasRadius).toFloat()

                        drawCircle(
                            color = activeProgressColor.copy(alpha = 0.25f),
                            radius = 10.dp.toPx(),
                            center = Offset(knobCenterX, knobCenterY)
                        )
                        drawCircle(
                            color = activeProgressColor,
                            radius = 6.5.dp.toPx(),
                            center = Offset(knobCenterX, knobCenterY)
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 2.5.dp.toPx(),
                            center = Offset(knobCenterX, knobCenterY)
                        )
                    }

                    // Inside Central Digits and Brain Icon
                    val minutes = elapsedSeconds / 60
                    val seconds = elapsedSeconds % 60

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (currentMode == TimerMode.FOCUS) Icons.Outlined.Psychology else Icons.Outlined.LocalCafe,
                            contentDescription = null,
                            tint = activeProgressColor,
                            modifier = Modifier.size(32.dp)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (currentMode == TimerMode.FOCUS) {
                                "مطالعه – دور ${(completedRounds + 1).coerceAtMost(totalRounds).toPersianString()}"
                            } else {
                                "استراحت – دور ${(completedRounds).coerceAtLeast(1).toPersianString()}"
                            },
                            fontFamily = IranSansFontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )

                        TimeTicker(
                            minutes = minutes,
                            seconds = seconds,
                            fontSize = 48.sp,
                            color = PlanNavy,
                            fontWeight = FontWeight.Black
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = if (isOvertime) Color(0xFFDCFCE7) else Color(0xFFF3E8FF)
                        ) {
                            Text(
                                text = if (isOvertime) "مطالعه اضافه ✨" else "زمان سپری‌شده",
                                fontFamily = IranSansFontFamily,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isOvertime) Color(0xFF15803D) else PlanPurple,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                // 4. EXACT 3-BUTTON ACTION CONTROLS ON CRESCENT ARC (SCALED DOWN & CLOSER TO TIMER)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-20).dp)
                        .padding(horizontal = 16.dp, vertical = 0.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Parabolic arc track line passing through button centers
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(68.dp)
                    ) {
                        val strokeWidth = 1.5.dp.toPx()
                        val arcPath = androidx.compose.ui.graphics.Path().apply {
                            val startX = size.width * 0.14f
                            val startY = 16.dp.toPx()
                            val endX = size.width * 0.86f
                            val endY = 16.dp.toPx()
                            val controlX = size.width * 0.5f
                            val controlY = 44.dp.toPx()

                            moveTo(startX, startY)
                            quadraticTo(controlX, controlY, endX, endY)
                        }
                        drawPath(
                            path = arcPath,
                            color = Color(0xFFEDE9FE).copy(alpha = 0.9f),
                            style = Stroke(
                                width = strokeWidth,
                                cap = StrokeCap.Round,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(5.dp.toPx(), 5.dp.toPx()), 0f)
                            )
                        )
                    }

                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 1. Right in RTL: بازنشانی (Reset - Scaled down)
                            Column(
                                modifier = Modifier.offset(y = (-6).dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            isRunning = false
                                            elapsedSeconds = 0
                                        }
                                        .testTag("timer_reset_btn"),
                                    shape = CircleShape,
                                    color = Color.White,
                                    border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                                    shadowElevation = 2.dp
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Outlined.RestartAlt,
                                            contentDescription = "بازنشانی",
                                            tint = PlanPurple,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "بازنشانی",
                                    fontFamily = IranSansFontFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PlanPurple
                                )
                            }

                            // 2. Center Hero: شروع / توقف (Play / Pause Hero Button - Scaled down from 64 to 54)
                            Column(
                                modifier = Modifier.offset(y = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .clickable { isRunning = !isRunning }
                                        .testTag("timer_toggle_play_btn"),
                                    shape = CircleShape,
                                    color = Color.White,
                                    border = BorderStroke(1.5.dp, Color(0xFFEDE9FE)),
                                    shadowElevation = 3.dp
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = if (isRunning) "توقف" else "شروع",
                                            tint = PlanPurple,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = if (isRunning) "توقف" else "شروع",
                                    fontFamily = IranSansFontFamily,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PlanPurple
                                )
                            }

                            // 3. Left in RTL: رد کردن این دور (Skip Round Button - Scaled down)
                            Column(
                                modifier = Modifier.offset(y = (-6).dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            isRunning = false
                                            elapsedSeconds = 0
                                            when (currentMode) {
                                                TimerMode.FOCUS -> {
                                                    currentMode = if (completedIntervals >= intervalsBeforeLongBreak) TimerMode.LONG_BREAK else TimerMode.SHORT_BREAK
                                                }
                                                else -> {
                                                    currentMode = TimerMode.FOCUS
                                                }
                                            }
                                            totalSeconds = when(currentMode) {
                                                TimerMode.FOCUS -> focusMins * 60
                                                TimerMode.SHORT_BREAK -> shortBreakMins * 60
                                                TimerMode.LONG_BREAK -> longBreakMins * 60
                                            }
                                        }
                                        .testTag("timer_skip_btn"),
                                    shape = CircleShape,
                                    color = Color.White,
                                    border = BorderStroke(1.dp, Color(0xFFFECDD3)),
                                    shadowElevation = 2.dp
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Filled.FastForward,
                                            contentDescription = "رد کردن این دور",
                                            tint = Color(0xFFF43F5E),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "رد کردن این دور",
                                    fontFamily = IranSansFontFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF43F5E)
                                )
                            }
                        }
                    }
                }

                // 5. INTEGRATED PROFESSIONAL BOTTOM PLAYER CARD
                var isAudioPlaying by rememberSaveable { mutableStateOf(false) }

                // Sync bottom player audio state with white noise audio generator
                DisposableEffect(isAudioPlaying, selectedSound) {
                    if (isAudioPlaying) {
                        val soundToPlay = if (selectedSound == "سکوت ملو") focusSounds[1] else selectedSound
                        soundPlayer.start(soundToPlay)
                    } else if (!isRunning || selectedSound == "سکوت ملو") {
                        soundPlayer.stop()
                    }
                    onDispose {
                        if (!isRunning) soundPlayer.stop()
                    }
                }

                FocusBottomAudioPlayer(
                    currentSound = selectedSound,
                    isPlaying = isAudioPlaying || (isRunning && selectedSound != "سکوت ملو"),
                    onTogglePlay = {
                        isAudioPlaying = !isAudioPlaying
                        if (isAudioPlaying && selectedSound == "سکوت ملو") {
                            selectedSound = focusSounds[1]
                        }
                    },
                    onStop = {
                        isAudioPlaying = false
                        soundPlayer.stop()
                    },
                    onPrevious = {
                        val currentIndex = focusSounds.indexOf(selectedSound).let { if (it <= 0) focusSounds.size - 1 else it - 1 }
                        selectedSound = focusSounds[currentIndex]
                        if (!isAudioPlaying) isAudioPlaying = true
                    },
                    onNext = {
                        val currentIndex = focusSounds.indexOf(selectedSound).let { if (it >= focusSounds.size - 1) 0 else it + 1 }
                        selectedSound = focusSounds[currentIndex]
                        if (!isAudioPlaying) isAudioPlaying = true
                    },
                    onOpenSoundPicker = { showSoundDialog = true },
                    modifier = Modifier
                        .offset(y = (-24).dp)
                        .padding(bottom = 0.dp)
                )
            }
        }

    // Modal Sound Selector Sheet
    if (showSoundDialog) {
        val soundSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showSoundDialog = false },
            sheetState = soundSheetState,
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎵 اصوات تمرکز و نویز سفید",
                        color = PlanNavy,
                        fontWeight = FontWeight.Bold,
                        fontFamily = IranSansFontFamily,
                        fontSize = 16.sp
                    )
                    if (isRunning && selectedSound != "سکوت ملو") {
                        EqualizerBarAnimated(isRunning = true, customColor = PlanPurple)
                    }
                }

                focusSounds.forEach { sound ->
                    val isSelected = sound == selectedSound
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                selectedSound = sound
                                showSoundDialog = false
                            },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) PlanPurpleLight else Color(0xFFF8FAFC),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) PlanPurple else Color(0xFFE2E8F0)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = sound,
                                fontFamily = IranSansFontFamily,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) PlanPurpleDark else PlanNavy
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = PlanPurple,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
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
                    sendBackendEvent(
                        eventType = "ACTIVITY_PAUSED",
                        onDone = {
                            navController.popBackStack()
                        }
                    )
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





