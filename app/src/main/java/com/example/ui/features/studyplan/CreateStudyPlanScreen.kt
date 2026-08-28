package com.example.ui.features.studyplan

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import com.example.ui.core.components.NetworkErrorView
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import com.example.ui.core.components.shimmerEffect
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.R
import com.example.domain.date.DateTransformer
import com.example.domain.date.JalaliDate
import com.example.ui.core.toPersianNumber
import com.example.ui.theme.IranSansFontFamily
import kotlinx.coroutines.flow.collectLatest

val PlanBg = Color(0xFFF8F9FD)

@Composable
fun CreateStudyPlanScreen(
    navController: NavController,
    viewModel: CreateStudyPlanViewModel = viewModel(),
) {
    val navigateToTasks = {
        navController.navigate("study_plan") {
            popUpTo("dashboard") { inclusive = false }
            launchSingleTop = true
        }
    }
    val onBackClick = {
        val popped = navController.popBackStack()
        if (!popped) {
            navController.navigate("dashboard") {
                popUpTo("dashboard") { inclusive = false }
                launchSingleTop = true
            }
        }
    }
    CreateStudyPlanScreen(
        onBackClick = onBackClick,
        onNavigateToTasks = navigateToTasks,
        viewModel = viewModel,
    )
}

@Composable
fun CreateStudyPlanScreen(
    onBackClick: () -> Unit,
    onNavigateToTasks: () -> Unit = onBackClick,
    viewModel: CreateStudyPlanViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    BackHandler {
        onBackClick()
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is CreateStudyPlanEvent.PlanSaved -> {
                    Toast.makeText(context, "برنامه مطالعاتی با موفقیت ثبت شد", Toast.LENGTH_SHORT).show()
                    onNavigateToTasks()
                }
                is CreateStudyPlanEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            containerColor = PlanBg,
            topBar = {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding(),
                    color = PlanBg,
                ) {
                    CreatePlanTopBar(
                        onBackClick = onBackClick,
                    )
                }
            },
            bottomBar = {
                // Bottom Fixed Action Bar with navigationBarsPadding
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(16.dp, spotColor = Color(0x1A000000)),
                    color = Color.White,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable(enabled = !state.isSubmitting) {
                                    viewModel.saveStudyPlan()
                                }
                                .testTag("save_study_plan_button"),
                            shape = RoundedCornerShape(16.dp),
                            color = PlanPurple,
                            shadowElevation = 3.dp,
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                if (state.isSubmitting) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(22.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp,
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "در حال ثبت برنامه...",
                                        color = Color.White,
                                        fontFamily = IranSansFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                    )
                                } else {
                                    Text(
                                        text = stringResource(id = R.string.create_plan_save_button),
                                        color = Color.White,
                                        fontFamily = IranSansFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            },
        ) { paddingValues ->
            if (state.subjects.isEmpty() && !state.isLoadingCatalog) {
                NetworkErrorView(
                    modifier = Modifier.padding(paddingValues),
                    title = stringResource(R.string.error_network_title),
                    description = state.errorMessage ?: stringResource(R.string.error_network_desc),
                    isRetrying = state.isLoadingCatalog,
                    fullScreen = true,
                    backgroundColor = PlanBg,
                    onRetry = { viewModel.retryCatalog() }
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .testTag("create_study_plan_lazy_column"),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                // 0. Section 0: Day & Date Selection (تعیین روز اجرای برنامه)
                item(key = "scheduled_date_selector") {
                    CreatePlanDateSelectorSection(
                        selectedDate = state.selectedDate,
                        onDateSelected = { viewModel.selectDate(it) },
                    )
                }

                // 1. Section 1: Grade & Book Selection (پایه و کتاب) with minimal book names & skeleton loader
                item(key = "grade_and_book") {
                    GradeAndBookSection(
                        selectedGrade = state.selectedGrade,
                        selectedGradeName = state.selectedGradeName,
                        grades = state.grades,
                        onGradeSelected = { key, name -> viewModel.selectGrade(key, name) },
                        subjects = state.subjects,
                        selectedSubjectId = state.selectedSubjectId,
                        onSubjectSelected = { viewModel.selectSubject(it) },
                        isLoading = state.isLoadingCatalog,
                    )
                }

                // 2. Section 2: Chapter & Topics Selection (Multi-block support & ChapterBlockSkeleton when fetching)
                if (state.isLoadingCatalog) {
                    item(key = "chapter_skeleton") {
                        ChapterBlockSkeleton()
                    }
                } else {
                    items(
                        items = state.chapterBlocks,
                        key = { it.blockId },
                    ) { block ->
                        ChapterBlockCard(
                            subject = state.selectedSubject,
                            block = block,
                            canDelete = state.chapterBlocks.size > 1,
                            onDeleteBlock = { viewModel.removeChapterBlock(block.blockId) },
                            onChapterSelected = { chapterId ->
                                viewModel.selectChapterForBlock(block.blockId, chapterId)
                            },
                            onTopicToggle = { topicId ->
                                viewModel.toggleTopicForBlock(block.blockId, topicId)
                            },
                        )
                    }
                }

                // 3. "اضافه کردن فصل" Action Button (Adds a new chapter block box below)
                item(key = "add_chapter_action") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { viewModel.addChapterBlock() }
                                .testTag("add_chapter_button"),
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFF5F3FF),
                            border = BorderStroke(1.dp, Color(0xFFDDD6FE)),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = PlanPurple,
                                    modifier = Modifier.size(18.dp),
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = stringResource(id = R.string.create_plan_add_chapter_action),
                                    color = PlanPurple,
                                    fontFamily = IranSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                )
                            }
                        }
                    }
                }

                // 4. Section 3: Study Periods (دوره‌های مطالعه)
                item(key = "study_periods") {
                    StudyPeriodsSection(
                        periodCount = state.periodCount,
                        onIncrement = { viewModel.incrementPeriod() },
                        onDecrement = { viewModel.decrementPeriod() },
                    )
                }

                // 5. Section 4: Manual Timing & Capsule Sliders (زمان‌بندی دستی)
                item(key = "timing_section") {
                    TimingSection(
                        isManualTiming = state.isManualTiming,
                        onManualTimingToggle = { viewModel.setManualTiming(it) },
                        studyDurationMinutes = state.studyDurationMinutes,
                        breakDurationMinutes = state.breakDurationMinutes,
                        onStudyDurationChange = { viewModel.setStudyDuration(it) },
                        onBreakDurationChange = { viewModel.setBreakDuration(it) },
                    )
                }
            }
            }

            if (state.isSummaryModalVisible) {
                StudyPlanSummaryBottomSheet(
                    state = state,
                    onDismiss = { viewModel.hideSummaryModal() },
                    onConfirmSubmit = {
                        viewModel.confirmAndSubmitPlan(onSuccess = onNavigateToTasks)
                    },
                )
            }
        }
    }
}

/**
 * Modern Date Selector at the top of CreateStudyPlanScreen
 */
@Composable
fun CreatePlanDateSelectorSection(
    selectedDate: JalaliDate,
    onDateSelected: (JalaliDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val today = remember { DateTransformer.getTodayJalali() }
    val tomorrow = remember { today.plusDays(1) }
    var showCalendarModal by remember { mutableStateOf(false) }

    val isTodaySelected = selectedDate == today
    val isTomorrowSelected = selectedDate == tomorrow
    val isCustomSelected = !isTodaySelected && !isTomorrowSelected

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(1.dp, RoundedCornerShape(22.dp), spotColor = Color(0x0D000000))
            .testTag("create_plan_date_section"),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = BorderStroke(1.dp, PlanCardBorder),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Header Row: Icon, Title, and Formatted Date Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF3EEFF)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = null,
                            tint = PlanPurple,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    Text(
                        text = "روز اجرای برنامه",
                        color = PlanNavy,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF5F3FF),
                    border = BorderStroke(1.dp, Color(0xFFDDD6FE)),
                ) {
                    Text(
                        text = DateTransformer.formatFullPersianDate(selectedDate),
                        color = PlanPurple,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                }
            }

            // Quick Selection Chips Row: امروز, فردا, تقویم
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // 1. امروز
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onDateSelected(today) }
                        .testTag("quick_date_today_btn"),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isTodaySelected) PlanPurple else Color(0xFFF8F9FD),
                    border = if (isTodaySelected) null else BorderStroke(1.dp, Color(0xFFE2E8F0)),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "امروز",
                            color = if (isTodaySelected) Color.White else PlanNavy,
                            fontFamily = IranSansFontFamily,
                            fontWeight = if (isTodaySelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.5.sp,
                        )
                    }
                }

                // 2. فردا
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onDateSelected(tomorrow) }
                        .testTag("quick_date_tomorrow_btn"),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isTomorrowSelected) PlanPurple else Color(0xFFF8F9FD),
                    border = if (isTomorrowSelected) null else BorderStroke(1.dp, Color(0xFFE2E8F0)),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "فردا",
                            color = if (isTomorrowSelected) Color.White else PlanNavy,
                            fontFamily = IranSansFontFamily,
                            fontWeight = if (isTomorrowSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.5.sp,
                        )
                    }
                }

                // 3. تقویم ماهانه
                Surface(
                    modifier = Modifier
                        .weight(1.3f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showCalendarModal = true }
                        .testTag("quick_date_custom_calendar_btn"),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isCustomSelected) PlanPurple else Color(0xFFF8F9FD),
                    border = if (isCustomSelected) null else BorderStroke(1.dp, Color(0xFFE2E8F0)),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = null,
                            tint = if (isCustomSelected) Color.White else PlanPurple,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isCustomSelected) "${selectedDate.day.toPersianNumber()} ${selectedDate.monthName}" else "تقویم",
                            color = if (isCustomSelected) Color.White else PlanNavy,
                            fontFamily = IranSansFontFamily,
                            fontWeight = if (isCustomSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }

    if (showCalendarModal) {
        JalaliMonthCalendarDialog(
            initialDate = selectedDate,
            onDismiss = { showCalendarModal = false },
            onDateSelected = { newDate ->
                showCalendarModal = false
                onDateSelected(newDate)
            },
        )
    }
}

@Composable
fun CreatePlanTopBar(
    onBackClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 2.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Right circular button (Back arrow in RTL pointing right) - Navigates back to Task List
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, PlanCardBorder, CircleShape)
                .clickable { onBackClick() }
                .testTag("create_plan_back_button"),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "بازگشت",
                tint = PlanNavy,
                modifier = Modifier.size(19.dp),
            )
        }

        // Center Title (Elevated higher per instruction 1)
        Text(
            text = stringResource(id = R.string.create_plan_title),
            color = PlanNavy,
            fontFamily = IranSansFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            textAlign = TextAlign.Center,
        )

        // Invisible placeholder to keep center title balanced
        Spacer(modifier = Modifier.size(40.dp))
    }
}

@Composable
fun BookItemSkeleton() {
    Surface(
        modifier = Modifier
            .height(40.dp)
            .width(72.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF1F5F9),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shimmerEffect(RoundedCornerShape(12.dp)),
        )
    }
}

@Composable
fun ChapterBlockSkeleton() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(1.dp, RoundedCornerShape(22.dp), spotColor = Color(0x0D000000)),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = BorderStroke(1.dp, PlanCardBorder),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            // Header Row Skeleton
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .shimmerEffect(CircleShape),
                    )
                    Box(
                        modifier = Modifier
                            .width(110.dp)
                            .height(18.dp)
                            .shimmerEffect(RoundedCornerShape(6.dp)),
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Chapter Dropdown Field Skeleton
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .shimmerEffect(RoundedCornerShape(14.dp)),
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Topics preview skeleton pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .width(96.dp)
                        .height(34.dp)
                        .shimmerEffect(RoundedCornerShape(10.dp)),
                )
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(34.dp)
                        .shimmerEffect(RoundedCornerShape(10.dp)),
                )
            }
        }
    }
}

@Composable
fun GradeAndBookSection(
    selectedGrade: String,
    selectedGradeName: String,
    grades: List<Pair<String, String>>,
    onGradeSelected: (String, String) -> Unit,
    subjects: List<SubjectVisualItem>,
    selectedSubjectId: String,
    onSubjectSelected: (String) -> Unit,
    isLoading: Boolean = false,
) {
    var isGradeMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
    ) {
        // Section Header Row: "پایه و کتاب" + Grade Dropdown
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Right side: Icon + Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                    contentDescription = null,
                    tint = PlanPurple,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = stringResource(id = R.string.create_plan_section_grade_book),
                    color = PlanNavy,
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                )
            }

            // Left side: Grade Dropdown Selector Pill
            Box {
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { isGradeMenuExpanded = true }
                        .testTag("grade_selector_dropdown"),
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, PlanCardBorder),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = PlanPurple,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = selectedGradeName,
                            color = PlanNavy,
                            fontFamily = IranSansFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                        )
                    }
                }

                DropdownMenu(
                    expanded = isGradeMenuExpanded,
                    onDismissRequest = { isGradeMenuExpanded = false },
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(14.dp))
                        .border(1.dp, PlanCardBorder, RoundedCornerShape(14.dp)),
                ) {
                    grades.forEach { (key, name) ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = name,
                                    fontFamily = IranSansFontFamily,
                                    fontWeight = if (key == selectedGrade) FontWeight.Bold else FontWeight.Normal,
                                    color = if (key == selectedGrade) PlanPurple else PlanNavy,
                                )
                            },
                            onClick = {
                                onGradeSelected(key, name)
                                isGradeMenuExpanded = false
                            },
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (isLoading) {
            // Skeleton loader for books row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("subjects_skeleton_row"),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(5) {
                    BookItemSkeleton()
                }
            }
        } else {
            // Horizontal Subject Cards Row with Minimal Names
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("subjects_horizontal_row"),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(subjects, key = { it.id }) { subject ->
                    val isSelected = subject.id == selectedSubjectId
                    SubjectCardItem(
                        subject = subject,
                        isSelected = isSelected,
                        onClick = { onSubjectSelected(subject.id) },
                    )
                }
            }
        }
    }
}

fun formatMinimalChapterName(index: Int, rawName: String): String {
    val persianIndex = (index + 1).toPersianNumber()
    val cleaned = rawName
        .replace(Regex("^فصل\\s*[^:]+:\\s*"), "")
        .replace(Regex("^فصل\\s*\\d+\\s*:?\\s*"), "")
        .replace(Regex("^فصل\\s*[\\u06F0-\\u06F9]+\\s*:?\\s*"), "")
        .replace(Regex("^فصل\\s*اول\\s*:?\\s*"), "")
        .replace(Regex("^فصل\\s*دوم\\s*:?\\s*"), "")
        .replace(Regex("^فصل\\s*سوم\\s*:?\\s*"), "")
        .replace(Regex("^فصل\\s*چهارم\\s*:?\\s*"), "")
        .replace(Regex("^فصل\\s*پنجم\\s*:?\\s*"), "")
        .replace(Regex("^فصل\\s*ششم\\s*:?\\s*"), "")
        .replace(Regex("^فصل\\s*هفتم\\s*:?\\s*"), "")
        .replace(Regex("^فصل\\s*هشتم\\s*:?\\s*"), "")
        .replace(Regex("^فصل\\s*نهم\\s*:?\\s*"), "")
        .replace(Regex("^فصل\\s*دهم\\s*:?\\s*"), "")
        .trim()
    return if (cleaned.isNotBlank()) "$persianIndex: $cleaned" else persianIndex
}

fun formatPersianChapterName(index: Int, rawName: String): String = formatMinimalChapterName(index, rawName)

@Composable
fun SubjectCardItem(
    subject: SubjectVisualItem,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = if (isSelected) PlanPurple else Color(0xFFE2E8F0)
    val backgroundColor = if (isSelected) Color(0xFFF5F3FF) else Color(0xFFF8F9FE)

    Surface(
        modifier = Modifier
            .height(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("subject_card_${subject.id}"),
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor),
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(PlanPurple),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(10.dp),
                    )
                }
            }

            Text(
                text = subject.minimalName,
                color = if (isSelected) PlanPurple else PlanNavy,
                fontFamily = IranSansFontFamily,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
    }
}

@Composable
fun ChapterBlockCard(
    subject: SubjectVisualItem?,
    block: ChapterBlockState,
    canDelete: Boolean,
    onDeleteBlock: () -> Unit,
    onChapterSelected: (String) -> Unit,
    onTopicToggle: (String) -> Unit,
) {
    val chapters = subject?.chapters ?: emptyList()
    val chapter = chapters.firstOrNull { it.id == block.selectedChapterId }
    var isChapterMenuOpen by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(1.dp, RoundedCornerShape(22.dp), spotColor = Color(0x0D000000))
            .testTag("chapter_block_${block.blockId}"),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = BorderStroke(1.dp, PlanCardBorder),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            // Header Row: Assignment Icon + "فصل و مباحث" + Delete action if multi-box
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Assignment,
                        contentDescription = null,
                        tint = PlanPurple,
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        text = stringResource(id = R.string.create_plan_section_chapter_topics),
                        color = PlanNavy,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp,
                    )
                }

                if (canDelete) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFEE2E2))
                            .clickable { onDeleteBlock() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "حذف فصل",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(15.dp),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Chapter Selector Button with Floating Overlay Dropdown (No parent layout push)
            val arrowRotation by animateFloatAsState(
                targetValue = if (isChapterMenuOpen) 180f else 0f,
                label = "arrow_rotation",
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            if (chapters.isNotEmpty()) {
                                isChapterMenuOpen = !isChapterMenuOpen
                                searchQuery = ""
                            }
                        }
                        .testTag("chapter_selector_button_${block.blockId}"),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF8F9FE),
                    border = BorderStroke(1.dp, if (isChapterMenuOpen) PlanPurple else Color(0xFFE2E8F0)),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        val selectedIndex = chapters.indexOfFirst { it.id == block.selectedChapterId }
                        val displayName = if (chapter != null && selectedIndex >= 0) {
                            formatMinimalChapterName(selectedIndex, chapter.name)
                        } else {
                            "انتخاب فصل"
                        }

                        Text(
                            text = displayName,
                            color = if (chapter != null) PlanNavy else PlanMuted,
                            fontFamily = IranSansFontFamily,
                            fontWeight = if (chapter != null) FontWeight.Medium else FontWeight.Normal,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Right,
                        )

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "انتخاب فصل",
                            tint = PlanPurple,
                            modifier = Modifier
                                .size(18.dp)
                                .rotate(arrowRotation),
                        )
                    }
                }

                // Inline Expandable Chapter Selector (Strict RTL, Stable during typing, compact search)
                AnimatedVisibility(
                    visible = isChapterMenuOpen,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut(),
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            shadowElevation = 2.dp,
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                            ) {
                                if (chapters.size > 2) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(38.dp)
                                            .background(Color(0xFFF8F9FE), RoundedCornerShape(10.dp))
                                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                                            .padding(horizontal = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = null,
                                            tint = PlanMuted,
                                            modifier = Modifier.size(16.dp),
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        BasicTextField(
                                            value = searchQuery,
                                            onValueChange = { searchQuery = it },
                                            singleLine = true,
                                            maxLines = 1,
                                            textStyle = TextStyle(
                                                fontFamily = IranSansFontFamily,
                                                fontSize = 12.sp,
                                                textAlign = TextAlign.Right,
                                                color = PlanNavy,
                                            ),
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("chapter_search_input_${block.blockId}"),
                                            decorationBox = { innerTextField ->
                                                Box(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    contentAlignment = Alignment.CenterStart,
                                                ) {
                                                    if (searchQuery.isEmpty()) {
                                                        Text(
                                                            text = "جستجوی فصل...",
                                                            fontFamily = IranSansFontFamily,
                                                            fontSize = 11.5.sp,
                                                            color = PlanMuted,
                                                            maxLines = 1,
                                                            softWrap = false,
                                                            textAlign = TextAlign.Right,
                                                        )
                                                    }
                                                    innerTextField()
                                                }
                                            },
                                        )
                                        if (searchQuery.isNotBlank()) {
                                            IconButton(
                                                onClick = { searchQuery = "" },
                                                modifier = Modifier.size(24.dp),
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "پاک کردن",
                                                    tint = PlanMuted,
                                                    modifier = Modifier.size(14.dp),
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                }

                                val filteredChapters = remember(chapters, searchQuery) {
                                    if (searchQuery.isBlank()) {
                                        chapters.mapIndexed { idx, ch -> Triple(idx, ch, formatMinimalChapterName(idx, ch.name)) }
                                    } else {
                                        val q = searchQuery.trim().lowercase()
                                        chapters.mapIndexed { idx, ch -> Triple(idx, ch, formatMinimalChapterName(idx, ch.name)) }
                                            .filter { (idx, ch, title) ->
                                                title.lowercase().contains(q) ||
                                                    ch.name.lowercase().contains(q) ||
                                                    (idx + 1).toString().contains(q) ||
                                                    (idx + 1).toPersianNumber().contains(q)
                                            }
                                    }
                                }

                                if (filteredChapters.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = "فصلی یافت نشد",
                                            fontFamily = IranSansFontFamily,
                                            fontSize = 12.sp,
                                            color = PlanMuted,
                                            textAlign = TextAlign.Center,
                                        )
                                    }
                                } else {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(max = 210.dp)
                                            .verticalScroll(rememberScrollState()),
                                        verticalArrangement = Arrangement.spacedBy(3.dp),
                                    ) {
                                        filteredChapters.forEach { (index, ch, formattedTitle) ->
                                            val isSelected = ch.id == chapter?.id
                                            Surface(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable {
                                                        onChapterSelected(ch.id)
                                                        isChapterMenuOpen = false
                                                    }
                                                    .testTag("chapter_item_${ch.id}"),
                                                shape = RoundedCornerShape(8.dp),
                                                color = if (isSelected) PlanPurple.copy(alpha = 0.08f) else Color.Transparent,
                                                border = if (isSelected) BorderStroke(1.dp, PlanPurple.copy(alpha = 0.35f)) else null,
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 10.dp, vertical = 9.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                ) {
                                                    Text(
                                                        text = formattedTitle,
                                                        fontFamily = IranSansFontFamily,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                        color = if (isSelected) PlanPurple else PlanNavy,
                                                        fontSize = 12.5.sp,
                                                        textAlign = TextAlign.Right,
                                                        modifier = Modifier.weight(1f),
                                                    )
                                                    if (isSelected) {
                                                        Icon(
                                                            imageVector = Icons.Default.Check,
                                                            contentDescription = null,
                                                            tint = PlanPurple,
                                                            modifier = Modifier.size(16.dp),
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
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Horizontal Scrolling Topic Chips or Empty Chapter prompt
            val topics = chapter?.topics ?: emptyList()

            if (topics.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("topics_row_${block.blockId}"),
                    contentPadding = PaddingValues(horizontal = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(topics, key = { it.id }) { topic ->
                        val isSelected = block.selectedTopicIds.contains(topic.id)
                        TopicChipItem(
                            topic = topic,
                            isSelected = isSelected,
                            onClick = { onTopicToggle(topic.id) },
                        )
                    }
                }
            } else {
                // Subtle helper prompt when chapter is not selected yet
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF8F9FE),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0).copy(alpha = 0.5f)),
                ) {
                    Text(
                        text = "لطفاً ابتدا فصل مورد نظر را از منوی بالا انتخاب کنید.",
                        color = PlanMuted,
                        fontFamily = IranSansFontFamily,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                    )
                }
            }
        }
    }
}

/**
 * Topic chip: When selected, it shows ONLY a green checkmark badge.
 * Does NOT change border color to green or purple, and does NOT bold text (Instruction 6).
 */
@Composable
fun TopicChipItem(
    topic: TopicVisualItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("topic_chip_${topic.id}"),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF8F9FE),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = topic.name,
                color = PlanNavy,
                fontFamily = IranSansFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                maxLines = 1,
            )

            if (isSelected) {
                // Green checkmark indicator only (Instruction 6)
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF22C55E)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "انتخاب شده",
                        tint = Color.White,
                        modifier = Modifier.size(10.dp),
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .border(1.2.dp, Color(0xFFCBD5E1), CircleShape),
                )
            }
        }
    }
}

@Composable
fun StudyPeriodsSection(
    periodCount: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(1.dp, RoundedCornerShape(22.dp), spotColor = Color(0x0D000000))
            .testTag("study_periods_card"),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = BorderStroke(1.dp, PlanCardBorder),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            // Header Row: Calendar Icon + "دوره‌های مطالعه"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = null,
                        tint = PlanPurple,
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        text = stringResource(id = R.string.create_plan_section_periods),
                        color = PlanNavy,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                    )
                }

                Text(
                    text = stringResource(id = R.string.create_plan_period_hint),
                    color = PlanMuted,
                    fontFamily = IranSansFontFamily,
                    fontSize = 11.sp,
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stepper Container with smaller + / - circle buttons (Instruction 5)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF8F9FE),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "تعداد دوره‌ها",
                        color = PlanNavy,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                    )

                    // Plus / Minus Stepper with smaller circle buttons
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        // Minus button (26dp circle per instruction 5)
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEDE9FE))
                                .clickable { onDecrement() }
                                .testTag("period_minus_button"),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "کاهش دوره",
                                tint = PlanPurple,
                                modifier = Modifier.size(14.dp),
                            )
                        }

                        // Count label
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                        ) {
                            Text(
                                text = periodCount.toPersianNumber(),
                                color = PlanNavy,
                                fontFamily = IranSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                            )
                            Text(
                                text = stringResource(id = R.string.create_plan_period_unit),
                                color = PlanMuted,
                                fontFamily = IranSansFontFamily,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(bottom = 2.dp),
                            )
                        }

                        // Plus button (26dp circle per instruction 5)
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEDE9FE))
                                .clickable { onIncrement() }
                                .testTag("period_plus_button"),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "افزایش دوره",
                                tint = PlanPurple,
                                modifier = Modifier.size(14.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimingSection(
    isManualTiming: Boolean,
    onManualTimingToggle: (Boolean) -> Unit,
    studyDurationMinutes: Int,
    breakDurationMinutes: Int,
    onStudyDurationChange: (Int) -> Unit,
    onBreakDurationChange: (Int) -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(1.dp, RoundedCornerShape(22.dp), spotColor = Color(0x0D000000))
            .testTag("timing_card"),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = null,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = if (isManualTiming) 10.dp else 6.dp),
        ) {
            // Header Row: Clock Icon + "زمان‌بندی" + Toggle "زمان‌بندی دستی"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                // Right: Clock + Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF3E8FF)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AccessTime,
                            contentDescription = null,
                            tint = Color(0xFF6C5CE7),
                            modifier = Modifier.size(13.dp),
                        )
                    }
                    Text(
                        text = stringResource(id = R.string.create_plan_section_timing),
                        color = PlanNavy,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                    )
                }

                // Left: Toggle "زمان‌بندی دستی"
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.create_plan_manual_timing),
                        color = PlanMuted,
                        fontFamily = IranSansFontFamily,
                        fontSize = 11.sp,
                    )
                    Switch(
                        checked = isManualTiming,
                        onCheckedChange = { onManualTimingToggle(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF6C5CE7),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFCBD5E1),
                            checkedBorderColor = Color.Transparent,
                            uncheckedBorderColor = Color.Transparent,
                        ),
                        modifier = Modifier
                            .scale(0.72f)
                            .testTag("timing_manual_switch"),
                    )
                }
            }

            // Animated visibility: Only show sliders if isManualTiming is TRUE (Instruction 7)
            AnimatedVisibility(
                visible = isManualTiming,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                ) {
                    // Two-column layout: Right column (Study Time), Left column (Break Time), separated by a subtle dashed line
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        // 1. Right Column in RTL: زمان مطالعه
                        Column(
                            modifier = Modifier.weight(1f),
                        ) {
                            // Header of Study Time: Right (Book Icon + Text) and Left (Value in Purple)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                // Value on left in RTL
                                Text(
                                    text = "${studyDurationMinutes.toPersianNumber()} دقیقه",
                                    color = Color(0xFF6C5CE7),
                                    fontFamily = IranSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                )

                                // Title on right in RTL
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.create_plan_study_duration),
                                        color = PlanNavy,
                                        fontFamily = IranSansFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp,
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                                        contentDescription = null,
                                        tint = PlanMuted,
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            val studySteps = listOf(15, 30, 60, 90)
                            val studyCurrentIndex = when {
                                studyDurationMinutes <= 15 -> 0
                                studyDurationMinutes <= 30 -> 1
                                studyDurationMinutes <= 60 -> 2
                                else -> 3
                            }

                            // Donut Step Slider matching instruction 4
                            DonutStepSlider(
                                stepCount = 4,
                                currentStepIndex = studyCurrentIndex,
                                onStepSelected = { index ->
                                    onStudyDurationChange(studySteps[index])
                                },
                                stepLabels = listOf("۱۵", "۳۰", "۶۰", "۹۰"),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("study_duration_capsule_slider"),
                            )
                        }

                        // Vertical Dashed Divider
                        Canvas(
                            modifier = Modifier
                                .width(1.dp)
                                .height(82.dp)
                                .padding(vertical = 4.dp),
                        ) {
                            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                            drawLine(
                                color = Color(0xFFE2E8F0),
                                start = Offset(0f, 0f),
                                end = Offset(0f, size.height),
                                strokeWidth = 1.2f,
                                pathEffect = pathEffect,
                            )
                        }

                        // 2. Left Column in RTL: استراحت
                        Column(
                            modifier = Modifier.weight(1f),
                        ) {
                            // Header of Break Time: Right (Cup Icon + Text) and Left (Value in Purple)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                // Value on left in RTL
                                Text(
                                    text = "${breakDurationMinutes.toPersianNumber()} دقیقه",
                                    color = Color(0xFF6C5CE7),
                                    fontFamily = IranSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                )

                                // Title on right in RTL
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.create_plan_break_duration),
                                        color = PlanNavy,
                                        fontFamily = IranSansFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp,
                                    )
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_coffee_cup),
                                        contentDescription = null,
                                        tint = PlanMuted,
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            val breakSteps = listOf(5, 10, 15, 30)
                            val breakCurrentIndex = when {
                                breakDurationMinutes <= 5 -> 0
                                breakDurationMinutes <= 10 -> 1
                                breakDurationMinutes <= 15 -> 2
                                else -> 3
                            }

                            // Donut Step Slider matching instruction 4
                            DonutStepSlider(
                                stepCount = 4,
                                currentStepIndex = breakCurrentIndex,
                                onStepSelected = { index ->
                                    onBreakDurationChange(breakSteps[index])
                                },
                                stepLabels = listOf("۵", "۱۰", "۱۵", "۳۰"),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("break_duration_capsule_slider"),
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Custom Donut Step Slider designed per User Instruction 4:
 * - Thumb (Handle): Circular Ring / Donut Thumb (circle with purple border and solid white center).
 * - Track: Narrow line background (subtle light gray).
 * - Progress / Filled Track: Part of the track filled with purple from start to the current step.
 * - Marks / Step Indicators: Fine vertical tick lines underneath the track for discrete steps.
 * - Mark Labels: Persian step labels centered directly below each tick mark.
 */
@Composable
fun DonutStepSlider(
    stepCount: Int,
    currentStepIndex: Int,
    onStepSelected: (Int) -> Unit,
    stepLabels: List<String>,
    modifier: Modifier = Modifier,
) {
    val activeColor = Color(0xFF6C5CE7)
    val inactiveTrackColor = Color(0xFFE2E8F0)
    val markTickColor = Color(0xFFCBD5E1)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .pointerInput(stepCount) {
                        detectTapGestures { offset ->
                            val width = size.width.toFloat()
                            if (width > 0 && stepCount > 1) {
                                val fraction = (offset.x / width).coerceIn(0f, 1f)
                                val selectedStep = Math.round(fraction * (stepCount - 1)).toInt().coerceIn(0, stepCount - 1)
                                onStepSelected(selectedStep)
                            }
                        }
                    }
                    .pointerInput(stepCount) {
                        detectHorizontalDragGestures { change, _ ->
                            val width = size.width.toFloat()
                            if (width > 0 && stepCount > 1) {
                                val fraction = (change.position.x / width).coerceIn(0f, 1f)
                                val selectedStep = Math.round(fraction * (stepCount - 1)).toInt().coerceIn(0, stepCount - 1)
                                onStepSelected(selectedStep)
                            }
                        }
                    },
                contentAlignment = Alignment.CenterStart,
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    val thumbRadius = 8.5.dp.toPx()
                    val thumbBorderWidth = 3.dp.toPx()
                    val trackHeight = 3.5.dp.toPx()
                    val trackY = canvasHeight / 2f

                    val startX = thumbRadius
                    val endX = canvasWidth - thumbRadius
                    val availableWidth = endX - startX

                    val stepFraction = if (stepCount > 1) currentStepIndex.toFloat() / (stepCount - 1) else 0f
                    val thumbCenterX = startX + (availableWidth * stepFraction)

                    // 1. Draw Inactive Track Line (Narrow gray line)
                    drawLine(
                        color = inactiveTrackColor,
                        start = Offset(startX, trackY),
                        end = Offset(endX, trackY),
                        strokeWidth = trackHeight,
                        cap = StrokeCap.Round,
                    )

                    // 2. Draw Active Progress Track Line (Purple)
                    if (currentStepIndex > 0) {
                        drawLine(
                            color = activeColor,
                            start = Offset(startX, trackY),
                            end = Offset(thumbCenterX, trackY),
                            strokeWidth = trackHeight,
                            cap = StrokeCap.Round,
                        )
                    }

                    // 3. Draw Fine Vertical Tick Marks under each step
                    val tickHeight = 5.dp.toPx()
                    val tickStartY = trackY + (trackHeight / 2f) + 2.dp.toPx()
                    val tickEndY = tickStartY + tickHeight

                    for (i in 0 until stepCount) {
                        val stepX = if (stepCount > 1) {
                            startX + (availableWidth * (i.toFloat() / (stepCount - 1)))
                        } else {
                            canvasWidth / 2f
                        }

                        val tickColor = if (i <= currentStepIndex) activeColor.copy(alpha = 0.7f) else markTickColor
                        drawLine(
                            color = tickColor,
                            start = Offset(stepX, tickStartY),
                            end = Offset(stepX, tickEndY),
                            strokeWidth = 1.5.dp.toPx(),
                            cap = StrokeCap.Round,
                        )
                    }

                    // 4. Draw Circular Ring / Donut Thumb (Purple ring with white center)
                    // Solid white circle background inside
                    drawCircle(
                        color = Color.White,
                        radius = thumbRadius,
                        center = Offset(thumbCenterX, trackY),
                    )
                    // Purple border stroke
                    drawCircle(
                        color = activeColor,
                        radius = thumbRadius - (thumbBorderWidth / 2f),
                        center = Offset(thumbCenterX, trackY),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = thumbBorderWidth),
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Step labels under the track aligned with steps
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                stepLabels.forEach { label ->
                    Text(
                        text = label,
                        color = PlanMuted,
                        fontFamily = IranSansFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyPlanSummaryBottomSheet(
    state: CreateStudyPlanUiState,
    onDismiss: () -> Unit,
    onConfirmSubmit: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val selectedSubject = state.selectedSubject

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(44.dp)
                    .height(4.5.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0)),
            )
        },
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 28.dp)
                    .testTag("study_plan_summary_modal"),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Header with soft gradient accent & badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFF8B5CF6), Color(0xFF6366F1))
                                    )
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Assignment,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "خلاصه برنامه مطالعاتی",
                                fontFamily = IranSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = PlanNavy,
                            )
                            Text(
                                text = "پیش‌نمایش جزئیات پارت‌ها و مباحث انتخابی",
                                fontFamily = IranSansFontFamily,
                                fontSize = 11.5.sp,
                                color = PlanMuted,
                            )
                        }
                    }

                    // Grade Badge (Only grade, no major)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF3F0FF),
                        border = BorderStroke(1.dp, Color(0xFFE9D5FF)),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = PlanPurple,
                                modifier = Modifier.size(15.dp),
                            )
                            Text(
                                text = state.selectedGradeName,
                                fontFamily = IranSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                color = PlanPurple,
                            )
                        }
                    }
                }

                // 3 Quick Stat Metric Cards in a Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    // Metric 1: Study Periods Count
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFEEF2F6)),
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEDE9FE)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Layers,
                                    contentDescription = null,
                                    tint = PlanPurple,
                                    modifier = Modifier.size(17.dp),
                                )
                            }
                            Text(
                                text = "${state.periodCount.toPersianNumber()} پارت",
                                fontFamily = IranSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = PlanNavy,
                            )
                            Text(
                                text = "تعداد دوره",
                                fontFamily = IranSansFontFamily,
                                fontSize = 10.5.sp,
                                color = PlanMuted,
                            )
                        }
                    }

                    // Metric 2: Duration per part
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFEEF2F6)),
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE0F2FE)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Timer,
                                    contentDescription = null,
                                    tint = Color(0xFF0284C7),
                                    modifier = Modifier.size(17.dp),
                                )
                            }
                            Text(
                                text = "${state.studyDurationMinutes.toPersianNumber()} دقیقه",
                                fontFamily = IranSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = PlanNavy,
                            )
                            Text(
                                text = "مدت هر پارت",
                                fontFamily = IranSansFontFamily,
                                fontSize = 10.5.sp,
                                color = PlanMuted,
                            )
                        }
                    }

                    // Metric 3: Total Topics Selected
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFEEF2F6)),
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDCFCE7)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF16A34A),
                                    modifier = Modifier.size(17.dp),
                                )
                            }
                            Text(
                                text = "${state.selectedTopicCount.toPersianNumber()} مبحث",
                                fontFamily = IranSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = PlanNavy,
                            )
                            Text(
                                text = "مباحث انتخابی",
                                fontFamily = IranSansFontFamily,
                                fontSize = 10.5.sp,
                                color = PlanMuted,
                            )
                        }
                    }
                }

                // Total Study Time Banner (Gradient Pill Card)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFFFAF5FF),
                    border = BorderStroke(1.dp, Color(0xFFF3E8FF)),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = null,
                                tint = PlanPurple,
                                modifier = Modifier.size(20.dp),
                            )
                            Text(
                                text = "مجموع زمان مطالعه برنامه:",
                                fontFamily = IranSansFontFamily,
                                fontSize = 12.sp,
                                color = PlanNavy,
                            )
                        }
                        Text(
                            text = if (state.totalHours > 0) {
                                "${state.totalHours.toPersianNumber()} ساعت و ${state.remainingMinutes.toPersianNumber()} دقیقه"
                            } else {
                                "${state.remainingMinutes.toPersianNumber()} دقیقه"
                            },
                            fontFamily = IranSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = PlanPurple,
                        )
                    }
                }

                // Selected Book, Chapters & Topics Container
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFFFFFFF),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shadowElevation = 1.dp,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        // Book Header Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            selectedSubject?.tintHex?.let { Color(it.toInt()).copy(alpha = 0.15f) }
                                                ?: Color(0xFFEDE9FE)
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                                        contentDescription = null,
                                        tint = selectedSubject?.tintHex?.let { Color(it.toInt()) } ?: PlanPurple,
                                        modifier = Modifier.size(18.dp),
                                    )
                                }
                                Text(
                                    text = "کتاب انتخابی",
                                    fontFamily = IranSansFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.5.sp,
                                    color = PlanMuted,
                                )
                            }
                            Text(
                                text = selectedSubject?.name ?: selectedSubject?.minimalName ?: "-",
                                fontFamily = IranSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = PlanNavy,
                            )
                        }

                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                        // Selected Chapters & Topics List
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            state.chapterBlocks.forEachIndexed { blockIdx, block ->
                                val chIndex = selectedSubject?.chapters?.indexOfFirst { it.id == block.selectedChapterId } ?: -1
                                val ch = selectedSubject?.chapters?.firstOrNull { it.id == block.selectedChapterId }
                                if (ch != null) {
                                    val chTitle = formatMinimalChapterName(if (chIndex >= 0) chIndex else blockIdx, ch.name)
                                    val selectedTopics = ch.topics.filter { block.selectedTopicIds.contains(it.id) }
                                    
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp),
                                        color = Color(0xFFF8FAFC),
                                        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 10.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp),
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(6.dp)
                                                        .clip(CircleShape)
                                                        .background(PlanPurple)
                                                )
                                                Text(
                                                    text = chTitle,
                                                    fontFamily = IranSansFontFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.5.sp,
                                                    color = PlanNavy,
                                                )
                                            }

                                            if (selectedTopics.isNotEmpty()) {
                                                // Topics chip badges
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                ) {
                                                    selectedTopics.forEach { topic ->
                                                        Surface(
                                                            shape = RoundedCornerShape(8.dp),
                                                            color = Color.White,
                                                            border = BorderStroke(0.5.dp, Color(0xFFE2E8F0)),
                                                        ) {
                                                            Text(
                                                                text = topic.name,
                                                                fontFamily = IranSansFontFamily,
                                                                fontSize = 11.sp,
                                                                color = PlanMuted,
                                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
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
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // Back/Edit Button
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(enabled = !state.isSubmitting) { onDismiss() }
                            .testTag("summary_modal_edit_button"),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF1F5F9),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = "ویرایش و بازگشت",
                                fontFamily = IranSansFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.5.sp,
                                color = PlanNavy,
                            )
                        }
                    }

                    // Confirm & Submit Button
                    Surface(
                        modifier = Modifier
                            .weight(1.4f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(enabled = !state.isSubmitting) { onConfirmSubmit() }
                            .testTag("summary_modal_confirm_button"),
                        shape = RoundedCornerShape(16.dp),
                        color = PlanPurple,
                        shadowElevation = 2.dp,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            if (state.isSubmitting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "در حال ثبت...",
                                    fontFamily = IranSansFontFamily,
                                    fontSize = 13.5.sp,
                                    color = Color.White,
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp),
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "تایید و ثبت نهایی",
                                    fontFamily = IranSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.White,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
