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
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.EditCalendar
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
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
import com.example.ui.core.components.NetworkErrorView
import com.example.ui.core.components.shimmerEffect
import com.example.ui.core.toPersianNumber
import com.example.ui.theme.IranSansFontFamily
import kotlinx.coroutines.flow.collectLatest

private val CreatePlanBg = Color(0xFFF8F9FD)

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
            containerColor = CreatePlanBg,
            topBar = {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding(),
                    color = CreatePlanBg,
                ) {
                    CreatePlanTopBar(
                        onBackClick = onBackClick,
                    )
                }
            },
            bottomBar = {
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
            if (state.subjectsByGrade.isEmpty() && !state.isLoadingCatalog) {
                NetworkErrorView(
                    modifier = Modifier.padding(paddingValues),
                    title = stringResource(R.string.error_network_title),
                    description = state.errorMessage ?: stringResource(R.string.error_network_desc),
                    isRetrying = state.isLoadingCatalog,
                    fullScreen = true,
                    backgroundColor = CreatePlanBg,
                    onRetry = { viewModel.retryCatalog() }
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .testTag("create_study_plan_lazy_column"),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    // 1. Direct Interactive Date Bar (Clicking opens calendar)
                    item(key = "interactive_date_bar") {
                        InteractiveDateSelectorCard(
                            selectedDate = state.selectedDate,
                            onDateSelected = { viewModel.selectDate(it) },
                        )
                    }

                    // 2. Multi-Book & Multi-Chapter Planning Blocks
                    if (state.isLoadingCatalog) {
                        item(key = "book_skeleton") {
                            BookPlanBlockSkeleton()
                        }
                    } else {
                        items(
                            items = state.bookBlocks,
                            key = { it.bookBlockId },
                        ) { bookBlock ->
                            val subjects = state.getSubjectsForGrade(bookBlock.selectedGrade)
                            val currentSubject = subjects.firstOrNull { it.id == bookBlock.selectedSubjectId } ?: subjects.firstOrNull()

                            BookPlanCard(
                                bookIndex = state.bookBlocks.indexOf(bookBlock),
                                bookBlock = bookBlock,
                                grades = state.grades,
                                subjects = subjects,
                                selectedSubject = currentSubject,
                                canDeleteBook = state.bookBlocks.size > 1,
                                onGradeSelected = { gradeKey, gradeName ->
                                    viewModel.selectGradeForBook(bookBlock.bookBlockId, gradeKey, gradeName)
                                },
                                onSubjectSelected = { subjectId ->
                                    viewModel.selectSubjectForBook(bookBlock.bookBlockId, subjectId)
                                },
                                onAddChapter = {
                                    viewModel.addChapterBlockToBook(bookBlock.bookBlockId)
                                },
                                onRemoveChapter = { chBlockId ->
                                    viewModel.removeChapterBlockFromBook(bookBlock.bookBlockId, chBlockId)
                                },
                                onSelectChapter = { chBlockId, chId ->
                                    viewModel.selectChapterForBookBlock(bookBlock.bookBlockId, chBlockId, chId)
                                },
                                onToggleTopic = { chBlockId, topId ->
                                    viewModel.toggleTopicForBookBlock(bookBlock.bookBlockId, chBlockId, topId)
                                },
                                onIncrementPeriod = {
                                    viewModel.incrementPeriodForBook(bookBlock.bookBlockId)
                                },
                                onDecrementPeriod = {
                                    viewModel.decrementPeriodForBook(bookBlock.bookBlockId)
                                },
                                onStudyDurationChange = { minutes ->
                                    viewModel.setStudyDurationForBook(bookBlock.bookBlockId, minutes)
                                },
                                onBreakDurationChange = { minutes ->
                                    viewModel.setBreakDurationForBook(bookBlock.bookBlockId, minutes)
                                },
                                onDeleteBook = {
                                    viewModel.removeBookBlock(bookBlock.bookBlockId)
                                },
                            )
                        }
                    }

                    // 3. Add Another Book Button ("+ افزودن کتاب جدید به برنامه")
                    item(key = "add_book_action") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                        ) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { viewModel.addBookBlock() }
                                    .testTag("add_book_button"),
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFF5F3FF),
                                border = BorderStroke(1.2.dp, Color(0xFFC4B5FD)),
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
                                        modifier = Modifier.size(20.dp),
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(id = R.string.create_plan_add_book_action),
                                        color = PlanPurple,
                                        fontFamily = IranSansFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (state.isSummaryModalVisible) {
                MultiBookPlanSummaryBottomSheet(
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
 * 1. Interactive Date Selector Bar (Replaces today/tomorrow chips with a clean interactive banner)
 */
@Composable
fun InteractiveDateSelectorCard(
    selectedDate: JalaliDate,
    onDateSelected: (JalaliDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showCalendarModal by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(1.dp, RoundedCornerShape(20.dp), spotColor = Color(0x0D000000))
            .clip(RoundedCornerShape(20.dp))
            .clickable { showCalendarModal = true }
            .testTag("interactive_date_selector_card"),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, PlanCardBorder),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF3EEFF)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = "تقویم",
                        tint = PlanPurple,
                        modifier = Modifier.size(22.dp),
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.create_plan_date_title),
                        color = PlanMuted,
                        fontFamily = IranSansFontFamily,
                        fontSize = 11.5.sp,
                    )
                    Text(
                        text = DateTransformer.formatFullPersianDate(selectedDate),
                        color = PlanNavy,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                    )
                }
            }

            // Change Date Prompt Pill
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF5F3FF),
                border = BorderStroke(1.dp, Color(0xFFDDD6FE)),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.EditCalendar,
                        contentDescription = null,
                        tint = PlanPurple,
                        modifier = Modifier.size(15.dp),
                    )
                    Text(
                        text = "تغییر تاریخ",
                        color = PlanPurple,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                    )
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

/**
 * 2. Book Plan Card (Each book contains Grade, Book selector, Chapters with Add Chapter, and Direct Timing)
 */
@Composable
fun BookPlanCard(
    bookIndex: Int,
    bookBlock: BookPlanBlock,
    grades: List<Pair<String, String>>,
    subjects: List<SubjectVisualItem>,
    selectedSubject: SubjectVisualItem?,
    canDeleteBook: Boolean,
    onGradeSelected: (String, String) -> Unit,
    onSubjectSelected: (String) -> Unit,
    onAddChapter: () -> Unit,
    onRemoveChapter: (String) -> Unit,
    onSelectChapter: (String, String) -> Unit,
    onToggleTopic: (String, String) -> Unit,
    onIncrementPeriod: () -> Unit,
    onDecrementPeriod: () -> Unit,
    onStudyDurationChange: (Int) -> Unit,
    onBreakDurationChange: (Int) -> Unit,
    onDeleteBook: () -> Unit,
) {
    var isGradeMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(2.dp, RoundedCornerShape(24.dp), spotColor = Color(0x0D000000))
            .testTag("book_plan_card_${bookBlock.bookBlockId}"),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = BorderStroke(1.dp, PlanCardBorder),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Header Row: Book Icon + Title (e.g. "کتاب ۱: زیست‌شناسی") + Grade Dropdown + Delete Book
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
                            .size(32.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFEDE9FE)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.MenuBook,
                            contentDescription = null,
                            tint = PlanPurple,
                            modifier = Modifier.size(18.dp),
                        )
                    }

                    Text(
                        text = "درس و کتاب ${(bookIndex + 1).toPersianNumber()}",
                        color = PlanNavy,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    // Grade Selector Dropdown
                    Box {
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { isGradeMenuExpanded = true }
                                .testTag("grade_dropdown_${bookBlock.bookBlockId}"),
                            color = Color(0xFFF8F9FD),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = PlanPurple,
                                    modifier = Modifier.size(14.dp),
                                )
                                Text(
                                    text = bookBlock.selectedGradeName,
                                    color = PlanNavy,
                                    fontFamily = IranSansFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.5.sp,
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
                                            fontWeight = if (key == bookBlock.selectedGrade) FontWeight.Bold else FontWeight.Normal,
                                            color = if (key == bookBlock.selectedGrade) PlanPurple else PlanNavy,
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

                    // Delete Book Button (only if more than 1 book exists)
                    if (canDeleteBook) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFEE2E2))
                                .clickable { onDeleteBook() }
                                .testTag("delete_book_btn_${bookBlock.bookBlockId}"),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "حذف کتاب",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                }
            }

            // Horizontal Book Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("book_subjects_row_${bookBlock.bookBlockId}"),
                contentPadding = PaddingValues(horizontal = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(subjects, key = { it.id }) { subject ->
                    val isSelected = subject.id == bookBlock.selectedSubjectId
                    SubjectCardItem(
                        subject = subject,
                        isSelected = isSelected,
                        onClick = { onSubjectSelected(subject.id) },
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // Chapters for this Book
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                bookBlock.chapterBlocks.forEachIndexed { chIndex, chBlock ->
                    BookChapterItemBox(
                        chapterIndex = chIndex,
                        subject = selectedSubject,
                        block = chBlock,
                        canDelete = bookBlock.chapterBlocks.size > 1,
                        onDeleteBlock = { onRemoveChapter(chBlock.blockId) },
                        onChapterSelected = { chId -> onSelectChapter(chBlock.blockId, chId) },
                        onTopicToggle = { topId -> onToggleTopic(chBlock.blockId, topId) },
                    )
                }

                // "+ افزودن فصل" Button inside this Book Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onAddChapter() }
                        .testTag("add_chapter_btn_${bookBlock.bookBlockId}"),
                    shape = RoundedCornerShape(12.dp),
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
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(id = R.string.create_plan_add_chapter_action),
                            color = PlanPurple,
                            fontFamily = IranSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp,
                        )
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // Direct Open Timing Section for this Book (Always Open per User Request)
            DirectBookTimingSection(
                periodCount = bookBlock.periodCount,
                studyDurationMinutes = bookBlock.studyDurationMinutes,
                breakDurationMinutes = bookBlock.breakDurationMinutes,
                onIncrementPeriod = onIncrementPeriod,
                onDecrementPeriod = onDecrementPeriod,
                onStudyDurationChange = onStudyDurationChange,
                onBreakDurationChange = onBreakDurationChange,
                bookBlockId = bookBlock.bookBlockId,
            )
        }
    }
}

/**
 * Single Chapter Box inside a Book Card
 */
@Composable
fun BookChapterItemBox(
    chapterIndex: Int,
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
            .clip(RoundedCornerShape(16.dp))
            .testTag("chapter_box_${block.blockId}"),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF8F9FE),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Header Row: Assignment Icon + "فصل X" + Delete Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Assignment,
                        contentDescription = null,
                        tint = PlanPurple,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = "فصل و مباحث ${(chapterIndex + 1).toPersianNumber()}",
                        color = PlanNavy,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                    )
                }

                if (canDelete) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFEE2E2))
                            .clickable { onDeleteBlock() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "حذف فصل",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(13.dp),
                        )
                    }
                }
            }

            // Chapter Dropdown Selector
            val arrowRotation by animateFloatAsState(
                targetValue = if (isChapterMenuOpen) 180f else 0f,
                label = "arrow_rotation",
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        if (chapters.isNotEmpty()) {
                            isChapterMenuOpen = !isChapterMenuOpen
                            searchQuery = ""
                        }
                    }
                    .testTag("chapter_selector_${block.blockId}"),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(1.dp, if (isChapterMenuOpen) PlanPurple else Color(0xFFCBD5E1)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
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
                        fontSize = 12.5.sp,
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

            // Expandable Chapter Menu
            AnimatedVisibility(
                visible = isChapterMenuOpen,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        shadowElevation = 2.dp,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(6.dp),
                        ) {
                            if (chapters.size > 2) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(36.dp)
                                        .background(Color(0xFFF8F9FE), RoundedCornerShape(8.dp))
                                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = PlanMuted,
                                        modifier = Modifier.size(15.dp),
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
                                        modifier = Modifier.weight(1f),
                                        decorationBox = { innerTextField ->
                                            Box(
                                                modifier = Modifier.fillMaxWidth(),
                                                contentAlignment = Alignment.CenterStart,
                                            ) {
                                                if (searchQuery.isEmpty()) {
                                                    Text(
                                                        text = "جستجوی فصل...",
                                                        fontFamily = IranSansFontFamily,
                                                        fontSize = 11.sp,
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
                                            modifier = Modifier.size(22.dp),
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "پاک کردن",
                                                tint = PlanMuted,
                                                modifier = Modifier.size(13.dp),
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
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
                                        .padding(10.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "فصلی یافت نشد",
                                        fontFamily = IranSansFontFamily,
                                        fontSize = 11.5.sp,
                                        color = PlanMuted,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            } else {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 180.dp)
                                        .verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(2.dp),
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
                                                .testTag("ch_item_${ch.id}"),
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSelected) PlanPurple.copy(alpha = 0.08f) else Color.Transparent,
                                            border = if (isSelected) BorderStroke(1.dp, PlanPurple.copy(alpha = 0.35f)) else null,
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 8.dp, vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                            ) {
                                                Text(
                                                    text = formattedTitle,
                                                    fontFamily = IranSansFontFamily,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSelected) PlanPurple else PlanNavy,
                                                    fontSize = 12.sp,
                                                    textAlign = TextAlign.Right,
                                                    modifier = Modifier.weight(1f),
                                                )
                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = PlanPurple,
                                                        modifier = Modifier.size(15.dp),
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

            // Topics Chips
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
            }
        }
    }
}

/**
 * 3. Direct Open Timing Section inside each Book (No Toggle/Switch needed)
 */
@Composable
fun DirectBookTimingSection(
    periodCount: Int,
    studyDurationMinutes: Int,
    breakDurationMinutes: Int,
    onIncrementPeriod: () -> Unit,
    onDecrementPeriod: () -> Unit,
    onStudyDurationChange: (Int) -> Unit,
    onBreakDurationChange: (Int) -> Unit,
    bookBlockId: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("book_timing_section_$bookBlockId"),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Section Header: Timing Icon + Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF3E8FF)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = null,
                        tint = PlanPurple,
                        modifier = Modifier.size(14.dp),
                    )
                }
                Text(
                    text = "زمان‌بندی و دوره‌های مطالعه",
                    color = PlanNavy,
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                )
            }

            // Stepper for Periods
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF8F9FE),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEDE9FE))
                            .clickable { onDecrementPeriod() }
                            .testTag("minus_period_$bookBlockId"),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "کاهش دوره",
                            tint = PlanPurple,
                            modifier = Modifier.size(13.dp),
                        )
                    }

                    Text(
                        text = "${periodCount.toPersianNumber()} دوره",
                        color = PlanNavy,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                    )

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEDE9FE))
                            .clickable { onIncrementPeriod() }
                            .testTag("plus_period_$bookBlockId"),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "افزایش دوره",
                            tint = PlanPurple,
                            modifier = Modifier.size(13.dp),
                        )
                    }
                }
            }
        }

        // Two Column Sliders: Study Time & Break Time
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            // 1. Study Time Column
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "${studyDurationMinutes.toPersianNumber()} د",
                        color = PlanPurple,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Text(
                            text = "زمان مطالعه",
                            color = PlanNavy,
                            fontFamily = IranSansFontFamily,
                            fontSize = 11.5.sp,
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                            contentDescription = null,
                            tint = PlanMuted,
                            modifier = Modifier.size(14.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                val studySteps = listOf(15, 30, 45, 60, 90)
                val studyIndex = when {
                    studyDurationMinutes <= 15 -> 0
                    studyDurationMinutes <= 30 -> 1
                    studyDurationMinutes <= 45 -> 2
                    studyDurationMinutes <= 60 -> 3
                    else -> 4
                }

                DonutStepSlider(
                    stepCount = 5,
                    currentStepIndex = studyIndex,
                    onStepSelected = { idx -> onStudyDurationChange(studySteps[idx]) },
                    stepLabels = listOf("۱۵", "۳۰", "۴۵", "۶۰", "۹۰"),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // Vertical Dashed Divider
            Canvas(
                modifier = Modifier
                    .width(1.dp)
                    .height(72.dp)
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

            // 2. Break Time Column
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "${breakDurationMinutes.toPersianNumber()} د",
                        color = PlanPurple,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Text(
                            text = "استراحت",
                            color = PlanNavy,
                            fontFamily = IranSansFontFamily,
                            fontSize = 11.5.sp,
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_coffee_cup),
                            contentDescription = null,
                            tint = PlanMuted,
                            modifier = Modifier.size(14.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                val breakSteps = listOf(5, 10, 15, 20, 30)
                val breakIndex = when {
                    breakDurationMinutes <= 5 -> 0
                    breakDurationMinutes <= 10 -> 1
                    breakDurationMinutes <= 15 -> 2
                    breakDurationMinutes <= 20 -> 3
                    else -> 4
                }

                DonutStepSlider(
                    stepCount = 5,
                    currentStepIndex = breakIndex,
                    onStepSelected = { idx -> onBreakDurationChange(breakSteps[idx]) },
                    stepLabels = listOf("۵", "۱۰", "۱۵", "۲۰", "۳۰"),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
fun BookPlanBlockSkeleton() {
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
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(20.dp)
                        .shimmerEffect(RoundedCornerShape(6.dp)),
                )
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(28.dp)
                        .shimmerEffect(RoundedCornerShape(12.dp)),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                repeat(4) {
                    Box(
                        modifier = Modifier
                            .width(70.dp)
                            .height(36.dp)
                            .shimmerEffect(RoundedCornerShape(10.dp)),
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .shimmerEffect(RoundedCornerShape(14.dp)),
            )
        }
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

        Text(
            text = stringResource(id = R.string.create_plan_title),
            color = PlanNavy,
            fontFamily = IranSansFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.size(40.dp))
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
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
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
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
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

                    val thumbRadius = 8.dp.toPx()
                    val thumbBorderWidth = 2.8.dp.toPx()
                    val trackHeight = 3.dp.toPx()
                    val trackY = canvasHeight / 2f

                    val startX = thumbRadius
                    val endX = canvasWidth - thumbRadius
                    val availableWidth = endX - startX

                    val stepFraction = if (stepCount > 1) currentStepIndex.toFloat() / (stepCount - 1) else 0f
                    val thumbCenterX = startX + (availableWidth * stepFraction)

                    // Inactive Track
                    drawLine(
                        color = inactiveTrackColor,
                        start = Offset(startX, trackY),
                        end = Offset(endX, trackY),
                        strokeWidth = trackHeight,
                        cap = StrokeCap.Round,
                    )

                    // Active Progress Track
                    if (currentStepIndex > 0) {
                        drawLine(
                            color = activeColor,
                            start = Offset(startX, trackY),
                            end = Offset(thumbCenterX, trackY),
                            strokeWidth = trackHeight,
                            cap = StrokeCap.Round,
                        )
                    }

                    // Tick Marks
                    val tickHeight = 4.dp.toPx()
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
                            strokeWidth = 1.2.dp.toPx(),
                            cap = StrokeCap.Round,
                        )
                    }

                    // Donut Thumb
                    drawCircle(
                        color = Color.White,
                        radius = thumbRadius,
                        center = Offset(thumbCenterX, trackY),
                    )
                    drawCircle(
                        color = activeColor,
                        radius = thumbRadius - (thumbBorderWidth / 2f),
                        center = Offset(thumbCenterX, trackY),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = thumbBorderWidth),
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

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
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

/**
 * 4. Multi-Book Plan Summary Modal (Displays all books, chapters, topics, and timing)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiBookPlanSummaryBottomSheet(
    state: CreateStudyPlanUiState,
    onDismiss: () -> Unit,
    onConfirmSubmit: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 6.dp)
                    .width(42.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFCBD5E1)),
            )
        },
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .padding(bottom = 20.dp)
                    .testTag("study_plan_summary_modal"),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Header
                Box(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable { onDismiss() }
                            .testTag("summary_modal_close_button"),
                        shape = CircleShape,
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "بستن",
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 36.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Text(
                            text = "✨ خلاصه برنامه روزانه شما",
                            fontFamily = IranSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF0F172A),
                        )
                        Text(
                            text = "تاریخ: ${DateTransformer.formatFullPersianDate(state.selectedDate)}",
                            fontFamily = IranSansFontFamily,
                            fontSize = 12.sp,
                            color = PlanPurple,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }

                // Books Summary Cards Container
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 340.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    state.bookBlocks.forEachIndexed { idx, book ->
                        val subjects = state.getSubjectsForGrade(book.selectedGrade)
                        val sub = subjects.firstOrNull { it.id == book.selectedSubjectId } ?: subjects.firstOrNull()
                        val subName = sub?.name ?: sub?.minimalName ?: "کتاب"

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                // Book Header
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFFEDE9FE)),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.MenuBook,
                                                contentDescription = null,
                                                tint = PlanPurple,
                                                modifier = Modifier.size(14.dp),
                                            )
                                        }
                                        Text(
                                            text = "$subName (${book.selectedGradeName})",
                                            fontFamily = IranSansFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color(0xFF0F172A),
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFEDE9FE),
                                    ) {
                                        Text(
                                            text = "${book.periodCount.toPersianNumber()} دوره × ${book.studyDurationMinutes.toPersianNumber()} د",
                                            fontFamily = IranSansFontFamily,
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PlanPurple,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.5.dp),
                                        )
                                    }
                                }

                                // Selected Chapters & Topics
                                book.chapterBlocks.forEach { chBlock ->
                                    val chIndex = sub?.chapters?.indexOfFirst { it.id == chBlock.selectedChapterId } ?: -1
                                    val ch = sub?.chapters?.firstOrNull { it.id == chBlock.selectedChapterId }
                                    if (ch != null) {
                                        val chTitle = formatMinimalChapterName(if (chIndex >= 0) chIndex else 0, ch.name)
                                        val selectedTopics = ch.topics.filter { chBlock.selectedTopicIds.contains(it.id) }
                                        val topicsText = if (selectedTopics.isNotEmpty()) {
                                            selectedTopics.joinToString("، ") { it.name }
                                        } else {
                                            "تمام گفتارها و مباحث"
                                        }

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                        ) {
                                            Row(
                                                modifier = Modifier.weight(1f, fill = false),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(5.dp)
                                                        .clip(CircleShape)
                                                        .background(PlanPurple),
                                                )
                                                Text(
                                                    text = chTitle,
                                                    fontFamily = IranSansFontFamily,
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 11.5.sp,
                                                    color = Color(0xFF334155),
                                                    maxLines = 1,
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(6.dp))

                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = Color.White,
                                                border = BorderStroke(0.5.dp, Color(0xFFCBD5E1)),
                                            ) {
                                                Text(
                                                    text = topicsText,
                                                    fontFamily = IranSansFontFamily,
                                                    fontSize = 10.sp,
                                                    color = if (selectedTopics.isNotEmpty()) Color(0xFF0F172A) else Color(0xFF64748B),
                                                    maxLines = 1,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Total Estimated Time Badge
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF5F3FF),
                    border = BorderStroke(1.dp, Color(0xFFDDD6FE)),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = null,
                                tint = PlanPurple,
                                modifier = Modifier.size(16.dp),
                            )
                            Text(
                                text = "مجموع زمان مطالعه و استراحت امروز",
                                fontFamily = IranSansFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                color = PlanNavy,
                            )
                        }

                        Text(
                            text = if (state.totalHours > 0) {
                                "${state.totalHours.toPersianNumber()} ساعت و ${state.remainingMinutes.toPersianNumber()} دقیقه"
                            } else {
                                "${state.totalEstimatedMinutes.toPersianNumber()} دقیقه"
                            },
                            fontFamily = IranSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = PlanPurple,
                        )
                    }
                }

                // Submit Button
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(enabled = !state.isSubmitting) { onConfirmSubmit() }
                        .testTag("summary_modal_confirm_button"),
                    shape = RoundedCornerShape(14.dp),
                    color = PlanPurple,
                    shadowElevation = 1.dp,
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        if (state.isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp,
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "در حال ثبت...",
                                fontFamily = IranSansFontFamily,
                                fontSize = 13.sp,
                                color = Color.White,
                            )
                        } else {
                            Text(
                                text = "تایید و ثبت نهایی برنامه روزانه",
                                fontFamily = IranSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = Color.White,
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
