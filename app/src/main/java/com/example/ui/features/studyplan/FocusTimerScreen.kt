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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
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

enum class TimerStyle(val title: String) {
    CIRCLE("دایره‌ای مدرن"),
    LUXURY_FULLSCREEN("سینمایی لوکس")
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
    
    var currentStyle by remember { mutableStateOf(TimerStyle.valueOf(sharedPrefs.getString("timer_style", TimerStyle.CIRCLE.name) ?: TimerStyle.CIRCLE.name)) }

    var currentMode by rememberSaveable { mutableStateOf(TimerMode.FOCUS) }
    var completedIntervals by rememberSaveable { mutableIntStateOf(0) }
    
    var totalSeconds by rememberSaveable { mutableIntStateOf(focusMins * 60) }
    var remainingSeconds by rememberSaveable { mutableIntStateOf(focusMins * 60) }
    var isRunning by rememberSaveable { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    var showCompletionSheet by remember { mutableStateOf(false) }
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
                remainingSeconds = totalSeconds
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
                    remainingSeconds = totalSeconds
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
        } else if (backendEventSequence > 0 && remainingSeconds > 0) {
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
                        completedRounds = minOf(totalRounds, completedRounds + 1)
                        roomTask?.let { t ->
                            val newCompleted = t.completedCycles + 1
                            val isDone = newCompleted >= t.totalCycles
                            db.taskDao().updateTask(t.copy(completedCycles = newCompleted, isCompleted = isDone || t.isCompleted))
                        }
                        if (completedRounds >= totalRounds) {
                            sendBackendEvent(
                                eventType = "ACTIVITY_COMPLETED",
                                completionOutcome = "FULL",
                                completionPercentage = 100
                            )
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

                    if (taskTitle.isNotBlank()) {
                        val cycleText = if (currentMode == TimerMode.FOCUS) " • دوره مطالعاتی ${getCycleString(completedRounds)} از ${totalRounds.toPersianString()}" else ""
                        Text(
                            text = "$taskTitle$cycleText",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 12.dp),
                            textAlign = TextAlign.Center
                        )
                        if (taskSubtitle.isNotBlank()) {
                            Text(
                                text = taskSubtitle,
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp),
                                textAlign = TextAlign.Center
                            )
                        }
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
        // --- 2. LUXURIOUS CIRCULAR MODERN STUDY TIMER MATCHING DESIGN SPEC ---
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
                            .size(44.dp)
                            .clip(CircleShape)
                            .clickable {
                                if (isRunning || remainingSeconds < totalSeconds) {
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

                    // Center Header Title & Subtitle
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "تمرکز و مطالعه",
                            fontFamily = IranSansFontFamily,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = PlanNavy
                        )
                        Text(
                            text = "الان روی این تسک تمرکز کن",
                            fontFamily = IranSansFontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = PlanMuted
                        )
                    }

                    // Left in RTL: Settings (Gear) Button
                    Surface(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .clickable { showSettings = true }
                            .testTag("focus_settings_button"),
                        shape = CircleShape,
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                        shadowElevation = 1.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = "تنظیمات",
                                tint = PlanNavy,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // 2. TASK INFO CARD
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFF0F2F7)),
                    shadowElevation = 1.dp
                ) {
                    val visualConfig = getSubjectVisualConfig(bookName.ifBlank { taskSubtitle }, bookName)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Left box in RTL (Book Icon + Subject Tag)
                        Surface(
                            modifier = Modifier
                                .size(width = 72.dp, height = 76.dp)
                                .clip(RoundedCornerShape(18.dp)),
                            shape = RoundedCornerShape(18.dp),
                            color = visualConfig.containerBg
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = visualConfig.icon,
                                    contentDescription = null,
                                    tint = visualConfig.iconTint,
                                    modifier = Modifier.size(26.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = visualConfig.title,
                                    fontFamily = IranSansFontFamily,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = visualConfig.iconTint,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Right details in RTL
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = taskTitle.ifBlank { "حل تمرین‌های درس" },
                                    fontFamily = IranSansFontFamily,
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PlanNavy,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.Outlined.BookmarkBorder,
                                    contentDescription = null,
                                    tint = PlanNavy.copy(alpha = 0.7f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            if (chapterSubtitle.isNotBlank()) {
                                Text(
                                    text = chapterSubtitle,
                                    fontFamily = IranSansFontFamily,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = PlanMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Metadata Row (Rounds & Duration)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        tint = PlanMuted,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "دور ${(completedRounds + 1).coerceAtMost(totalRounds).toPersianString()}/${totalRounds.toPersianString()}",
                                        fontFamily = IranSansFontFamily,
                                        fontSize = 11.5.sp,
                                        color = PlanMuted,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(12.dp)
                                        .background(Color(0xFFE2E8F0))
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.AccessTime,
                                        contentDescription = null,
                                        tint = PlanMuted,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "${focusMins.toPersianString()} دقیقه",
                                        fontFamily = IranSansFontFamily,
                                        fontSize = 11.5.sp,
                                        color = PlanMuted,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. TIMER MODE SWITCHER (Clean Dedicated Row Above the Clock)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Right in RTL: Study Mode Indicator
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .clickable {
                                if (currentMode != TimerMode.FOCUS) {
                                    currentMode = TimerMode.FOCUS
                                    totalSeconds = focusMins * 60
                                    remainingSeconds = totalSeconds
                                    isRunning = false
                                }
                            },
                        shape = RoundedCornerShape(100.dp),
                        color = if (currentMode == TimerMode.FOCUS) PlanPurpleLight else Color.Transparent,
                        border = if (currentMode == TimerMode.FOCUS) BorderStroke(1.dp, PlanPurple.copy(alpha = 0.4f)) else null
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (currentMode == TimerMode.FOCUS) PlanPurple else Color(0xFFCBD5E1))
                            )
                            Text(
                                text = "حالت مطالعه",
                                fontFamily = IranSansFontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentMode == TimerMode.FOCUS) PlanPurple else PlanMuted
                            )
                        }
                    }

                    // Left in RTL: Break Pill
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .clickable {
                                if (currentMode == TimerMode.FOCUS) {
                                    currentMode = TimerMode.SHORT_BREAK
                                    totalSeconds = shortBreakMins * 60
                                    remainingSeconds = totalSeconds
                                    isRunning = false
                                }
                            },
                        shape = RoundedCornerShape(100.dp),
                        color = if (currentMode != TimerMode.FOCUS) PlanPurpleLight else Color.White,
                        border = BorderStroke(1.dp, if (currentMode != TimerMode.FOCUS) PlanPurple else Color(0xFFE2E8F0)),
                        shadowElevation = 0.5.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.LocalCafe,
                                contentDescription = null,
                                tint = if (currentMode != TimerMode.FOCUS) PlanPurple else PlanMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "استراحت",
                                fontFamily = IranSansFontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentMode != TimerMode.FOCUS) PlanPurple else PlanMuted
                            )
                        }
                    }
                }

                // 4. CIRCULAR CLOCK DIAL (Spacious & Clean, no overlapping pills)
                Box(
                    modifier = Modifier
                        .size(245.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidthVal = 6.5.dp.toPx()
                        val canvasRadius = size.minDimension / 2f - 18.dp.toPx()
                        val centerOffset = Offset(size.width / 2f, size.height / 2f)

                        // Outer Dial Tick Marks (60 ticks)
                        val totalTicks = 60
                        for (i in 0 until totalTicks) {
                            val tickAngle = (i * (360f / totalTicks)) * (Math.PI / 180f)
                            val innerR = canvasRadius + 8.dp.toPx()
                            val outerR = canvasRadius + if (i % 5 == 0) 13.dp.toPx() else 11.dp.toPx()
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
                            color = Color(0xFFF3E8FF).copy(alpha = 0.7f),
                            radius = canvasRadius,
                            center = centerOffset,
                            style = Stroke(width = strokeWidthVal, cap = StrokeCap.Round)
                        )

                        // Dynamic Progress Arc
                        val sweepAngle = ((1f - animatedProgress) * 360f).coerceIn(0.1f, 360f)
                        drawArc(
                            color = PlanPurple,
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
                            color = PlanPurple.copy(alpha = 0.25f),
                            radius = 9.dp.toPx(),
                            center = Offset(knobCenterX, knobCenterY)
                        )
                        drawCircle(
                            color = PlanPurple,
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
                    val minutes = remainingSeconds / 60
                    val seconds = remainingSeconds % 60

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Psychology,
                            contentDescription = null,
                            tint = PlanPurple,
                            modifier = Modifier.size(32.dp)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        TimeTicker(
                            minutes = minutes,
                            seconds = seconds,
                            fontSize = 46.sp,
                            color = PlanNavy,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "زمان باقی‌مانده",
                            fontFamily = IranSansFontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = PlanMuted
                        )
                    }
                }

                // 5. ACTION CONTROLS IN CRESCENT ARC (شکل هلال)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Right in RTL: Settings (offset down to form crescent)
                    Column(
                        modifier = Modifier.offset(y = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .clickable { showSettings = true }
                                .testTag("timer_settings_btn"),
                            shape = CircleShape,
                            color = Color.White,
                            border = BorderStroke(1.dp, Color(0xFFF0F2F7)),
                            shadowElevation = 1.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.Tune,
                                    contentDescription = "تنظیمات",
                                    tint = PlanNavy,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }
                        Text(
                            text = "تنظیمات",
                            fontFamily = IranSansFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = PlanNavy
                        )
                    }

                    // Reset Button (offset slightly down)
                    Column(
                        modifier = Modifier.offset(y = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .clickable {
                                    isRunning = false
                                    remainingSeconds = totalSeconds
                                }
                                .testTag("timer_reset_btn"),
                            shape = CircleShape,
                            color = Color.White,
                            border = BorderStroke(1.dp, Color(0xFFF0F2F7)),
                            shadowElevation = 1.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.RestartAlt,
                                    contentDescription = "بازنشانی",
                                    tint = PlanNavy,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }
                        Text(
                            text = "بازنشانی",
                            fontFamily = IranSansFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = PlanNavy
                        )
                    }

                    // Center Hero Button: Play / Pause (elevated at center of crescent)
                    Column(
                        modifier = Modifier.offset(y = (-6).dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .width(94.dp)
                                .height(54.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { isRunning = !isRunning }
                                .testTag("timer_toggle_play_btn"),
                            shape = RoundedCornerShape(20.dp),
                            color = PlanPurple,
                            shadowElevation = 4.dp
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isRunning) "توقف" else "شروع",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = if (isRunning) "توقف" else "شروع",
                                    fontFamily = IranSansFontFamily,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Skip Button (offset slightly down)
                    Column(
                        modifier = Modifier.offset(y = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .clickable {
                                    isRunning = false
                                    when (currentMode) {
                                        TimerMode.FOCUS -> {
                                            completedIntervals++
                                            completedRounds = minOf(totalRounds, completedRounds + 1)
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
                                    remainingSeconds = totalSeconds
                                }
                                .testTag("timer_skip_btn"),
                            shape = CircleShape,
                            color = Color.White,
                            border = BorderStroke(1.dp, Color(0xFFF0F2F7)),
                            shadowElevation = 1.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.SkipNext,
                                    contentDescription = "رد کردن",
                                    tint = PlanNavy,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }
                        Text(
                            text = "رد کردن",
                            fontFamily = IranSansFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = PlanNavy
                        )
                    }

                    // Left in RTL: Sound / Music Selection (offset down to form crescent)
                    Column(
                        modifier = Modifier.offset(y = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .clickable { showSoundDialog = true }
                                .testTag("timer_sound_btn"),
                            shape = CircleShape,
                            color = Color.White,
                            border = BorderStroke(1.dp, Color(0xFFF0F2F7)),
                            shadowElevation = 1.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.MusicNote,
                                    contentDescription = "صدا/موسیقی",
                                    tint = PlanNavy,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }
                        Text(
                            text = "صدا/موسیقی",
                            fontFamily = IranSansFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = PlanNavy
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 6. TASK PROGRESS CARD ("پیشرفت این تسک" - Compact, without "0 از 3", without stats card)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFF0F2F7)),
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Right in RTL: Title only
                            Text(
                                text = "پیشرفت این تسک",
                                fontFamily = IranSansFontFamily,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = PlanNavy
                            )

                            // Left in RTL (Remaining rounds flag)
                            val remRounds = maxOf(0, totalRounds - completedRounds)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.OutlinedFlag,
                                    contentDescription = null,
                                    tint = PlanMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "$remRounds دور باقی مانده".toPersianNumber(),
                                    fontFamily = IranSansFontFamily,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = PlanMuted
                                )
                            }
                        }

                        // Linear Progress Bar
                        val taskProgress = (completedRounds.toFloat() / maxOf(1, totalRounds)).coerceIn(0f, 1f)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(100.dp))
                                .background(Color(0xFFF1F5F9))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction = maxOf(0.02f, taskProgress))
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(PlanPurple)
                            )
                        }
                    }
                }

                // 7. BOTTOM ENCOURAGEMENT BANNER ("عالی پیش می‌ری ✨")
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(22.dp),
                    color = Color(0xFFF5F3FF),
                    border = BorderStroke(1.dp, Color(0xFFEDE9FE))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left in RTL: Finish Task Button
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    showCompletionSheet = true
                                },
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, PlanPurple.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Celebration,
                                    contentDescription = null,
                                    tint = PlanPurple,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "اتمام تسک",
                                    fontFamily = IranSansFontFamily,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PlanPurple
                                )
                            }
                        }

                        // Right in RTL: Cheerful title & subtitle
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "عالی پیش می‌ری",
                                    fontFamily = IranSansFontFamily,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PlanNavy
                                )
                                Text(text = "✨", fontSize = 14.sp)
                            }
                            Text(
                                text = "تمرکزت رو حفظ کن، به هدفت نزدیک‌تری!",
                                fontFamily = IranSansFontFamily,
                                fontSize = 11.5.sp,
                                color = PlanMuted,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }
            }
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

    if (showCompletionSheet) {
        TaskCompletionBottomSheet(
            taskTitle = taskTitle,
            onDismiss = { showCompletionSheet = false },
            onCompleteFull = {
                showCompletionSheet = false
                isRunning = false
                sendBackendEvent(
                    eventType = "ACTIVITY_COMPLETED",
                    completionOutcome = "FULL",
                    completionPercentage = 100,
                    onDone = {
                        navController.popBackStack()
                    }
                )
            },
            onCompletePartial = { percent, note ->
                showCompletionSheet = false
                isRunning = false
                sendBackendEvent(
                    eventType = "ACTIVITY_COMPLETED",
                    completionOutcome = "PARTIAL",
                    completionPercentage = percent,
                    note = note,
                    onDone = {
                        navController.popBackStack()
                    }
                )
            },
            onPauseAndExit = {
                showCompletionSheet = false
                isRunning = false
                sendBackendEvent(
                    eventType = "ACTIVITY_PAUSED",
                    onDone = {
                        navController.popBackStack()
                    }
                )
            }
        )
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





