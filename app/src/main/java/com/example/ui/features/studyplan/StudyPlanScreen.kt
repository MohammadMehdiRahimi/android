package com.example.ui.features.studyplan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.network.StudyTaskBookDto
import com.example.network.StudyTaskChapterDto
import com.example.network.StudyTaskDto
import com.example.network.StudyTaskTopicDto
import com.example.ui.core.toPersianNumber
import com.example.ui.theme.IranSansFontFamily
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

private val PlanPurple = Color(0xFF7656F5)
private val PlanPurpleDark = Color(0xFF5236CB)
private val PlanNavy = Color(0xFF172353)
private val PlanMuted = Color(0xFF9299B2)
private val PlanBackground = Color(0xFFF8F8FC)
private val PlanGreen = Color(0xFF27BD83)
private val PlanOrange = Color(0xFFFFA829)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyPlanScreen(
    navController: NavController,
    onBackClick: (() -> Unit)? = null,
    viewModel: StudyPlanViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    var showCreator by remember { mutableStateOf(false) }

    LaunchedEffect(state.mutationMessage) {
        state.mutationMessage?.let {
            snackbar.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        containerColor = PlanBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "برنامهٔ مطالعه",
                            color = PlanNavy,
                            fontFamily = IranSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                        )
                        Text(
                            text = "مسیر امروزت را کامل کن",
                            color = PlanMuted,
                            fontFamily = IranSansFontFamily,
                            fontSize = 11.sp,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (onBackClick != null) onBackClick() else navController.popBackStack()
                        },
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "بازگشت",
                            tint = PlanNavy,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::refresh) {
                        if (state.refreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = PlanPurple,
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = "به‌روزرسانی", tint = PlanNavy)
                        }
                    }
                },
            )
        },
        floatingActionButton = {
            Button(
                onClick = { showCreator = true },
                enabled = state.catalog?.books?.isNotEmpty() == true,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PlanPurple),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
                modifier = Modifier
                    .navigationBarsPadding()
                    .shadow(12.dp, RoundedCornerShape(18.dp), ambientColor = PlanPurple.copy(alpha = .18f)),
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("تسک دستی", fontFamily = IranSansFontFamily, fontWeight = FontWeight.Bold)
            }
        },
    ) { padding ->
        when {
            state.loading -> LoadingPlan(Modifier.padding(padding))
            state.error != null && state.day == null -> ErrorPlan(
                message = state.error.orEmpty(),
                onRetry = viewModel::retry,
                modifier = Modifier.padding(padding),
            )
            else -> {
                val day = state.day
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 112.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    item {
                        DateNavigator(
                            selectedDate = state.selectedDate,
                            onPrevious = viewModel::selectPreviousDay,
                            onNext = viewModel::selectNextDay,
                            onSelect = viewModel::selectDate,
                        )
                    }
                    item {
                        ProgressSummary(
                            total = day?.summary?.total ?: 0,
                            completed = day?.summary?.completed ?: 0,
                            percent = day?.summary?.completionPercent ?: 0,
                        )
                    }
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = if (isToday(state.selectedDate)) "کارهای امروز" else "کارهای این روز",
                                color = PlanNavy,
                                fontFamily = IranSansFontFamily,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "${(day?.summary?.total ?: 0).toString().toPersianNumber()} تسک",
                                color = PlanMuted,
                                fontFamily = IranSansFontFamily,
                                fontSize = 12.sp,
                            )
                        }
                    }
                    if (day?.items.isNullOrEmpty()) {
                        item { EmptyTasks(onAdd = { showCreator = true }) }
                    } else {
                        items(day!!.items, key = { it.id }) { task ->
                            StudyTaskCard(
                                task = task,
                                busy = state.busyTaskId == task.id,
                                onStart = { viewModel.startTask(task) },
                                onDone = { viewModel.markTaskDone(task) },
                                onDelete = { viewModel.cancelTask(task) },
                            )
                        }
                    }
                    if (state.error != null) {
                        item {
                            InlineError(state.error.orEmpty(), viewModel::refresh)
                        }
                    }
                }
            }
        }
    }

    if (showCreator) {
        ManualTaskCreator(
            books = state.catalog?.books.orEmpty(),
            selectedDate = state.selectedDate,
            creating = state.creating,
            onDismiss = { if (!state.creating) showCreator = false },
            onCreate = { topic, periods, minutes ->
                viewModel.createManualTask(topic.id, periods, minutes) {
                    showCreator = false
                }
            },
        )
    }
}

@Composable
private fun DateNavigator(
    selectedDate: LocalDate,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSelect: (LocalDate) -> Unit,
) {
    val dates = remember(selectedDate) { (-2L..2L).map(selectedDate::plusDays) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onNext) {
                Icon(Icons.Default.ArrowForwardIos, contentDescription = "روز بعد", tint = PlanMuted, modifier = Modifier.size(16.dp))
            }
            Text(
                text = dateTitle(selectedDate),
                color = PlanNavy,
                fontFamily = IranSansFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
            )
            IconButton(onClick = onPrevious) {
                Icon(Icons.Default.ArrowBackIosNew, contentDescription = "روز قبل", tint = PlanMuted, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(Modifier.height(10.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            items(dates) { date ->
                val selected = date == selectedDate
                Column(
                    modifier = Modifier
                        .width(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (selected) PlanPurple else Color.Transparent)
                        .clickable { onSelect(date) }
                        .padding(vertical = 9.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = shortWeekday(date),
                        color = if (selected) Color.White.copy(alpha = .78f) else PlanMuted,
                        fontFamily = IranSansFontFamily,
                        fontSize = 10.sp,
                    )
                    Text(
                        text = date.dayOfMonth.toString().toPersianNumber(),
                        color = if (selected) Color.White else PlanNavy,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressSummary(total: Int, completed: Int, percent: Int) {
    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(listOf(Color(0xFF6D50ED), Color(0xFF9B7CFF))),
                    RoundedCornerShape(26.dp),
                )
                .padding(22.dp),
        ) {
            Column(Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text("پیشرفت برنامه", color = Color.White, fontFamily = IranSansFontFamily, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "${completed.toString().toPersianNumber()} از ${total.toString().toPersianNumber()} کار انجام شده",
                            color = Color.White.copy(alpha = .78f),
                            fontFamily = IranSansFontFamily,
                            fontSize = 12.sp,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .background(Color.White.copy(alpha = .16f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "${percent.toString().toPersianNumber()}٪",
                            color = Color.White,
                            fontFamily = IranSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                        )
                    }
                }
                Spacer(Modifier.height(18.dp))
                LinearProgressIndicator(
                    progress = { percent.coerceIn(0, 100) / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(CircleShape),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = .2f),
                )
            }
        }
    }
}

@Composable
private fun StudyTaskCard(
    task: StudyTaskDto,
    busy: Boolean,
    onStart: () -> Unit,
    onDone: () -> Unit,
    onDelete: () -> Unit,
) {
    val status = task.execution?.status
    val completed = status == "COMPLETED"
    val active = status in setOf("ACTIVE", "PAUSED", "AWAITING_COMPLETION")
    val terminalIncomplete = status in setOf("PARTIAL", "SKIPPED")
    val canExecute = task.scheduledOn == LocalDate.now(ZoneId.of("Asia/Tehran")).toString()
    val accent = when {
        completed -> PlanGreen
        active -> PlanOrange
        else -> PlanPurple
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(accent.copy(alpha = .1f), RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (completed) Icons.Default.CheckCircle else Icons.Default.AutoStories,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(28.dp),
                )
            }
            Spacer(Modifier.width(13.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = task.title,
                        color = PlanNavy,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    if (task.sourceType == "MANUAL") {
                        Text(
                            "دستی",
                            color = PlanPurple,
                            fontFamily = IranSansFontFamily,
                            fontSize = 9.sp,
                            modifier = Modifier
                                .background(PlanPurple.copy(alpha = .08f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 7.dp, vertical = 3.dp),
                        )
                    }
                }
                Spacer(Modifier.height(5.dp))
                Text(
                    text = "${task.book.name} • ${task.chapter.name}",
                    color = PlanMuted,
                    fontFamily = IranSansFontFamily,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(9.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = PlanMuted, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "${task.periodCount.toString().toPersianNumber()} دوره • ${task.minutesPerPeriod.toString().toPersianNumber()} دقیقه",
                        color = PlanMuted,
                        fontFamily = IranSansFontFamily,
                        fontSize = 10.sp,
                    )
                }
            }
            Spacer(Modifier.width(10.dp))
            if (busy) {
                CircularProgressIndicator(modifier = Modifier.size(28.dp), color = accent, strokeWidth = 2.dp)
            } else if (completed) {
                Text("انجام شد", color = PlanGreen, fontFamily = IranSansFontFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            } else if (terminalIncomplete) {
                Text(
                    if (status == "PARTIAL") "نیمه‌کاره" else "انجام نشد",
                    color = Color(0xFFE57B86),
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = if (active) onDone else onStart,
                        enabled = canExecute,
                        modifier = Modifier
                            .size(44.dp)
                            .background(if (canExecute) accent else PlanMuted.copy(alpha = .5f), CircleShape),
                    ) {
                        Icon(
                            if (active) Icons.Default.Check else Icons.Default.PlayArrow,
                            contentDescription = if (active) "پایان تسک" else "شروع تسک",
                            tint = Color.White,
                        )
                    }
                    Text(
                        if (active) "پایان" else "شروع",
                        color = if (canExecute) accent else PlanMuted,
                        fontFamily = IranSansFontFamily,
                        fontSize = 9.sp,
                    )
                }
            }
        }
        if (!completed && task.execution == null) {
            HorizontalDivider(color = Color(0xFFF0F1F6), thickness = 1.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = onDone, enabled = canExecute) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = PlanGreen, modifier = Modifier.size(17.dp))
                    Spacer(Modifier.width(5.dp))
                    Text("انجام شد", color = PlanGreen, fontFamily = IranSansFontFamily, fontSize = 11.sp)
                }
                if (task.sourceType == "MANUAL") {
                    IconButton(onClick = onDelete, modifier = Modifier.size(38.dp)) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "حذف تسک", tint = Color(0xFFE57B86), modifier = Modifier.size(19.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyTasks(onAdd: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 38.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(82.dp)
                .background(PlanPurple.copy(alpha = .08f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Default.MenuBook, contentDescription = null, tint = PlanPurple, modifier = Modifier.size(42.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text("برای این روز کاری ثبت نشده", color = PlanNavy, fontFamily = IranSansFontFamily, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(Modifier.height(6.dp))
        Text("یک مبحث انتخاب کن و برنامهٔ مطالعه‌ات را بساز", color = PlanMuted, fontFamily = IranSansFontFamily, fontSize = 11.sp, textAlign = TextAlign.Center)
        Spacer(Modifier.height(18.dp))
        OutlinedButton(onClick = onAdd, shape = RoundedCornerShape(14.dp)) {
            Icon(Icons.Default.Add, contentDescription = null, tint = PlanPurple)
            Spacer(Modifier.width(6.dp))
            Text("ساخت تسک", color = PlanPurple, fontFamily = IranSansFontFamily, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun LoadingPlan(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().background(PlanBackground), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = PlanPurple)
    }
}

@Composable
private fun ErrorPlan(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PlanBackground)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(message, color = PlanNavy, fontFamily = IranSansFontFamily, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = PlanPurple)) {
            Text("تلاش دوباره", fontFamily = IranSansFontFamily)
        }
    }
}

@Composable
private fun InlineError(message: String, onRetry: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF2F3), RoundedCornerShape(18.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(message, color = Color(0xFFB44754), fontFamily = IranSansFontFamily, fontSize = 11.sp, modifier = Modifier.weight(1f))
        IconButton(onClick = onRetry) { Icon(Icons.Default.Refresh, contentDescription = "تلاش دوباره", tint = Color(0xFFB44754)) }
    }
}

@Composable
private fun ManualTaskCreator(
    books: List<StudyTaskBookDto>,
    selectedDate: LocalDate,
    creating: Boolean,
    onDismiss: () -> Unit,
    onCreate: (StudyTaskTopicDto, Int, Int) -> Unit,
) {
    var selectedBook by remember(books) { mutableStateOf<StudyTaskBookDto?>(books.firstOrNull()) }
    var selectedChapter by remember(selectedBook) { mutableStateOf<StudyTaskChapterDto?>(selectedBook?.chapters?.firstOrNull()) }
    var selectedTopic by remember(selectedChapter) { mutableStateOf<StudyTaskTopicDto?>(selectedChapter?.topics?.firstOrNull()) }
    var periods by remember { mutableIntStateOf(2) }
    var minutes by remember { mutableIntStateOf(45) }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(color = PlanBackground, modifier = Modifier.fillMaxSize()) {
            Scaffold(
                containerColor = PlanBackground,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                topBar = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .statusBarsPadding()
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = onDismiss, enabled = !creating) {
                            Icon(Icons.Default.Close, contentDescription = "بستن", tint = PlanNavy)
                        }
                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("ساخت تسک جدید", color = PlanNavy, fontFamily = IranSansFontFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(dateTitle(selectedDate), color = PlanMuted, fontFamily = IranSansFontFamily, fontSize = 10.sp)
                        }
                        Spacer(Modifier.width(48.dp))
                    }
                },
                bottomBar = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .navigationBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                    ) {
                        Button(
                            onClick = { selectedTopic?.let { onCreate(it, periods, minutes) } },
                            enabled = selectedTopic != null && !creating,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PlanPurple),
                        ) {
                            if (creating) {
                                CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Check, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("ساخت تسک مطالعه", fontFamily = IranSansFontFamily, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                },
            ) { padding ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(22.dp),
                ) {
                    item { CreatorTitle("کتاب درسی", "متناسب با پایه و رشتهٔ شما") }
                    item {
                        if (books.isEmpty()) {
                            Text("برای پروفایل شما کتابی ثبت نشده است", color = PlanMuted, fontFamily = IranSansFontFamily)
                        } else {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(books, key = { it.id }) { book ->
                                    SelectionPill(
                                        text = book.name,
                                        selected = selectedBook?.id == book.id,
                                        onClick = {
                                            selectedBook = book
                                            selectedChapter = book.chapters.firstOrNull()
                                            selectedTopic = selectedChapter?.topics?.firstOrNull()
                                        },
                                        leadingBook = true,
                                    )
                                }
                            }
                        }
                    }
                    item { CreatorTitle("فصل", "فصل موردنظر را انتخاب کنید") }
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                            items(selectedBook?.chapters.orEmpty(), key = { it.id }) { chapter ->
                                SelectionPill(
                                    text = chapter.name,
                                    selected = selectedChapter?.id == chapter.id,
                                    onClick = {
                                        selectedChapter = chapter
                                        selectedTopic = chapter.topics.firstOrNull()
                                    },
                                )
                            }
                        }
                    }
                    item { CreatorTitle("مبحث مطالعه", "یک مبحث را برای این تسک مشخص کنید") }
                    items(selectedChapter?.topics.orEmpty(), key = { it.id }) { topic ->
                        val selected = selectedTopic?.id == topic.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (selected) PlanPurple.copy(alpha = .08f) else Color.White)
                                .clickable { selectedTopic = topic }
                                .padding(15.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(if (selected) PlanPurple else Color(0xFFF0F1F6), CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (selected) Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                            }
                            Spacer(Modifier.width(11.dp))
                            Text(topic.name, color = PlanNavy, fontFamily = IranSansFontFamily, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal, fontSize = 13.sp)
                        }
                    }
                    item { CreatorTitle("تنظیم مطالعه", "تعداد دوره و زمان پیش‌فرض هر دوره") }
                    item {
                        SettingCard(
                            title = "تعداد دوره‌های مطالعه",
                            value = periods,
                            suffix = "دوره",
                            onMinus = { if (periods > 1) periods-- },
                            onPlus = { if (periods < minOf(20, 1440 / minutes)) periods++ },
                        )
                    }
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(22.dp)).padding(16.dp),
                        ) {
                            Text("زمان هر دوره", color = PlanNavy, fontFamily = IranSansFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(Modifier.height(12.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(25, 45, 60, 90).forEach { option ->
                                    Surface(
                                        color = if (minutes == option) PlanPurple else Color(0xFFF4F4F8),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier.weight(1f).clickable {
                                            minutes = option
                                            periods = periods.coerceAtMost(1440 / option)
                                        },
                                    ) {
                                        Text(
                                            "${option.toString().toPersianNumber()} دقیقه",
                                            color = if (minutes == option) Color.White else PlanMuted,
                                            fontFamily = IranSansFontFamily,
                                            fontWeight = if (minutes == option) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 10.sp,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 11.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(PlanPurple.copy(alpha = .08f), RoundedCornerShape(22.dp))
                                .padding(17.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text("زمان کل مطالعه", color = PlanMuted, fontFamily = IranSansFontFamily, fontSize = 10.sp)
                                Text(
                                    "${(periods * minutes).toString().toPersianNumber()} دقیقه",
                                    color = PlanPurpleDark,
                                    fontFamily = IranSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                )
                            }
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = PlanPurple, modifier = Modifier.size(34.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CreatorTitle(title: String, subtitle: String) {
    Column {
        Text(title, color = PlanNavy, fontFamily = IranSansFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(subtitle, color = PlanMuted, fontFamily = IranSansFontFamily, fontSize = 10.sp)
    }
}

@Composable
private fun SelectionPill(text: String, selected: Boolean, onClick: () -> Unit, leadingBook: Boolean = false) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) PlanPurple else Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingBook) {
            Icon(Icons.Default.MenuBook, contentDescription = null, tint = if (selected) Color.White else PlanPurple, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(7.dp))
        }
        Text(
            text,
            color = if (selected) Color.White else PlanNavy,
            fontFamily = IranSansFontFamily,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 12.sp,
            maxLines = 1,
        )
    }
}

@Composable
private fun SettingCard(
    title: String,
    value: Int,
    suffix: String,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(22.dp)).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, color = PlanNavy, fontFamily = IranSansFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Surface(color = Color(0xFFF3F3F8), shape = CircleShape, modifier = Modifier.size(36.dp).clickable(onClick = onMinus)) {
            Box(contentAlignment = Alignment.Center) { Text("−", color = PlanNavy, fontSize = 20.sp) }
        }
        Text(
            "${value.toString().toPersianNumber()} $suffix",
            color = PlanPurpleDark,
            fontFamily = IranSansFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(74.dp),
        )
        Surface(color = PlanPurple, shape = CircleShape, modifier = Modifier.size(36.dp).clickable(onClick = onPlus)) {
            Box(contentAlignment = Alignment.Center) { Text("+", color = Color.White, fontSize = 20.sp) }
        }
    }
}

private fun isToday(date: LocalDate): Boolean = date == LocalDate.now(ZoneId.of("Asia/Tehran"))

private fun dateTitle(date: LocalDate): String {
    val today = LocalDate.now(ZoneId.of("Asia/Tehran"))
    return when (date) {
        today -> "امروز"
        today.plusDays(1) -> "فردا"
        today.minusDays(1) -> "دیروز"
        else -> "${date.dayOfMonth.toString().toPersianNumber()} ${date.month.getDisplayName(TextStyle.FULL, Locale("fa"))}"
    }
}

private fun shortWeekday(date: LocalDate): String = when (date.dayOfWeek.value) {
    6 -> "شنبه"
    7 -> "یکش"
    1 -> "دوش"
    2 -> "سه‌ش"
    3 -> "چهار"
    4 -> "پنج"
    else -> "جمعه"
}
