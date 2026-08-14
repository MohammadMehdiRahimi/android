package com.example.ui.features.studyplan

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ShetabColorPalette
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.text.style.TextAlign

@Composable
fun PlanWizard(
    colors: ShetabColorPalette,
    onTakingExamStateChange: (Boolean) -> Unit,
    onGuideActive: (Boolean) -> Unit,
    onExit: () -> Unit,
    onFinish: (Map<String, Int>, List<String>, Map<String, List<String>>, Boolean, String, String, String) -> Unit
) {
    var step by remember { mutableIntStateOf(1) }
    var additionalNotes by remember { mutableStateOf("") }
    
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { context.getSharedPreferences("shetab_wizard_prefs", android.content.Context.MODE_PRIVATE) }
    var isFirstTime by remember { mutableStateOf(prefs.getBoolean("first_time_wizard_v8", true)) }
    var isGuideActive by remember { mutableStateOf(isFirstTime) }
    var currentGuideStep by remember { mutableIntStateOf(1) }
    var wizardCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

    var wedCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var thuCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var firstBookCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var firstChapterCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var notesCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var examStartCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }

    val guideTargetBounds by remember(currentGuideStep, wizardCoordinates, wedCoords, thuCoords, firstBookCoords, firstChapterCoords, notesCoords, examStartCoords) {
        derivedStateOf {
            val wizard = wizardCoordinates ?: return@derivedStateOf Rect.Zero
            if (!wizard.isAttached) return@derivedStateOf Rect.Zero

            when (currentGuideStep) {
                1 -> {
                    val wed = wedCoords
                    val thu = thuCoords
                    if (wed != null && wed.isAttached && thu != null && thu.isAttached) {
                        val wedRect = wizard.localBoundingBoxOf(wed)
                        val thuRect = wizard.localBoundingBoxOf(thu)
                        Rect(
                            left = minOf(wedRect.left, thuRect.left),
                            top = minOf(wedRect.top, thuRect.top),
                            right = maxOf(wedRect.right, thuRect.right),
                            bottom = maxOf(wedRect.bottom, thuRect.bottom)
                        )
                    } else {
                        Rect.Zero
                    }
                }
                2 -> {
                    val book = firstBookCoords
                    if (book != null && book.isAttached) {
                        wizard.localBoundingBoxOf(book)
                    } else {
                        Rect.Zero
                    }
                }
                3 -> {
                    val chap = firstChapterCoords
                    if (chap != null && chap.isAttached) {
                        wizard.localBoundingBoxOf(chap)
                    } else {
                        val book = firstBookCoords
                        if (book != null && book.isAttached) {
                            wizard.localBoundingBoxOf(book)
                        } else {
                            Rect.Zero
                        }
                    }
                }
                4 -> {
                    val notes = notesCoords
                    if (notes != null && notes.isAttached) {
                        wizard.localBoundingBoxOf(notes)
                    } else {
                        Rect.Zero
                    }
                }
                5 -> {
                    val exam = examStartCoords
                    if (exam != null && exam.isAttached) {
                        wizard.localBoundingBoxOf(exam)
                    } else {
                        Rect.Zero
                    }
                }
                else -> Rect.Zero
            }
        }
    }

    val dailyHoursMap = remember {
        mutableStateMapOf(
            "شنبه" to 6,
            "یکشنبه" to 6,
            "دوشنبه" to 6,
            "سه شنبه" to 6,
            "چهارشنبه" to 6,
            "پنجشنبه" to 4,
            "جمعه" to 2
        )
    }
    
    val allBooks = listOf("زیست‌شناسی", "شیمی", "ریاضیات", "فیزیک", "ادبیات", "عربی", "زبان")
    val bookChapters = mapOf(
        "زیست‌شناسی" to listOf("فصل ۱: مولکول‌های اطلاعاتی", "فصل ۲: جریان اطلاعات در یاخته", "فصل ۳: انتقال اطلاعات در نسل‌ها"),
        "شیمی" to listOf("فصل ۱: کیهان زادگاه الفبای هستی", "فصل ۲: ردپای گازها در زندگی", "فصل ۳: آب، آهنگ زندگی"),
        "ریاضیات" to listOf("فصل ۱: مجموعه، الگو و دنباله", "فصل ۲: مثلثات", "فصل ۳: توان‌های گویا و عبارت‌های جبری"),
        "فیزیک" to listOf("فصل ۱: فیزیک و اندازه‌گیری", "فصل ۲: کار، انرژی و توان", "فصل ۳: ویژگی‌های فیزیکی مواد"),
        "ادبیات" to listOf("درس ۱: چشمه", "درس ۲: قاضی بست", "درس ۳: پرورده عشق"),
        "عربی" to listOf("الدرس الأول", "الدرس الثاني", "الدرس الثالث"),
        "زبان" to listOf("Lesson 1: Sense of Appreciation", "Lesson 2: A Healthy Lifestyle", "Lesson 3: Art and Culture")
    )
    
    val selectedBooks = remember { mutableStateListOf<String>() }
    val selectedChapters = remember { mutableStateMapOf<String, MutableList<String>>() }
    val examChapters = remember { mutableStateListOf<String>() }
    val examDays = remember { mutableStateMapOf<String, String>() }
    var isTakingExam by remember { mutableStateOf(false) }

    val currentIsTaking = step == 4 && isTakingExam
    LaunchedEffect(currentIsTaking) {
        onTakingExamStateChange(currentIsTaking)
    }

    LaunchedEffect(isFirstTime, isGuideActive) {
        onGuideActive(isFirstTime && isGuideActive)
    }

    LaunchedEffect(step) {
        if (isFirstTime) {
            when (step) {
                1 -> {
                    currentGuideStep = 1
                    isGuideActive = true
                }
                2 -> {
                    currentGuideStep = 2
                    isGuideActive = true
                }
                3, 4 -> {
                    isGuideActive = false
                }
            }
        }
    }

    androidx.activity.compose.BackHandler(enabled = true) {
        if (isFirstTime && isGuideActive) {
            if (currentGuideStep == 3) {
                currentGuideStep = 2
            } else if (currentGuideStep == 2) {
                isGuideActive = false
            } else if (currentGuideStep == 1) {
                isGuideActive = false
            }
        } else {
            if (step > 1) {
                step--
            } else {
                onExit()
            }
        }
    }

    if (step == 4 && isTakingExam) {
        val primaryBook = selectedBooks.firstOrNull() ?: "زیست‌شناسی"
        val chaptersText = selectedChapters[primaryBook]?.joinToString("، ") ?: "کل مباحث پایه"
        com.example.ui.features.exams.ExamTakingContent(
            colors = colors,
            questions = com.example.ui.features.exams.mockExamQuestions.take(3),
            isDescriptive = false,
            initialRemainingSeconds = 180,
            onFinishExam = { selAns, descAns, uplImgs ->
                isTakingExam = false
                val hasExams = examChapters.isNotEmpty()
                val calculatedExamBook = if (hasExams) {
                    val firstExam = examChapters.first()
                    bookChapters.entries.firstOrNull { it.value.contains(firstExam) }?.key ?: ""
                } else ""
                val calculatedExamChapter = if (hasExams) {
                    examChapters.joinToString("، ") { chap ->
                        val day = examDays[chap] ?: "شنبه"
                        "$chap (امتحان در روز $day)"
                    }
                } else ""
                onFinish(
                    dailyHoursMap.toMap(),
                    selectedBooks.toList(),
                    selectedChapters.toMap(),
                    hasExams,
                    calculatedExamBook,
                    calculatedExamChapter,
                    additionalNotes
                )
            },
            onExitExam = {
                isTakingExam = false
            }
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { wizardCoordinates = it }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .padding(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .border(
                            width = 1.5.dp,
                            color = colors.primaryText.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "مرحله $step از ۴",
                            color = colors.secondaryText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            (1..4).forEach { i ->
                                Box(
                                    modifier = Modifier
                                        .height(6.dp)
                                        .width(if (i == step) 24.dp else 6.dp)
                                        .clip(CircleShape)
                                        .background(if (i <= step) colors.accentMain else colors.primaryText.copy(alpha = 0.1f))
                                )
                            }
                        }
                    }
                    
                    Box(modifier = Modifier.weight(1f)) {
                        when (step) {
                            1 -> Step1Hours(
                                colors,
                                dailyHoursMap,
                                onWedPositioned = { wedCoords = it },
                                onThuPositioned = { thuCoords = it }
                            )
                            2 -> Step2Books(
                                colors,
                                allBooks,
                                bookChapters,
                                selectedBooks,
                                selectedChapters,
                                examChapters,
                                examDays,
                                onFirstBookPositioned = { firstBookCoords = it },
                                onFirstChapterPositioned = { firstChapterCoords = it }
                            )
                            3 -> Step3Notes(
                                colors,
                                additionalNotes,
                                { additionalNotes = it },
                                onNotesPositioned = { notesCoords = it }
                            )
                            4 -> {
                                val primaryBook = selectedBooks.firstOrNull() ?: "زیست‌شناسی"
                                val chaptersText = selectedChapters[primaryBook]?.joinToString("، ") ?: "کل مباحث پایه"
                                
                                com.example.ui.features.exams.ExamStartScreenContent(
                                    colors = colors,
                                    examTitle = "آزمون تعیین سطح علمی رایا 🤖",
                                    grade = "یازدهم",
                                    field = "علوم تجربی",
                                    book = "$primaryBook - $chaptersText",
                                    questionCount = 3,
                                    durationMinutes = 3,
                                    isDescriptive = false,
                                    hasNegativeScore = false,
                                    isPlacementWizardIntro = true,
                                    onStartClick = {
                                        isTakingExam = true
                                    }
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (step > 1 && step < 4) {
                        OutlinedButton(
                            onClick = { step-- },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primaryText),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "قبلی",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    } else if (step == 4) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    
                    if (step < 4) {
                        Button(
                            onClick = { step++ },
                            colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = "بعدی",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else {
                        Button(
                            onClick = { isTakingExam = true },
                            modifier = Modifier.onGloballyPositioned { examStartCoords = it },
                            colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = "شروع آزمون سنجش",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            if (isFirstTime && isGuideActive) {
                val guideTitle = when (currentGuideStep) {
                    1 -> "تنظیم ساعت‌های مطالعه هفتگی 🕒"
                    2 -> "انتخاب کتاب‌های درسی 📚"
                    else -> "انتخاب مباحث و امتحانات 📖"
                }
                val guideDesc = when (currentGuideStep) {
                    1 -> "ساعت مطالعه دلخواه روزهای هفته را تنظیم کن تا رایا برنامه شما را شخصی‌سازی کند."
                    2 -> "کتاب‌های درسی موردنیاز را انتخاب کن تا فصل‌های آن‌ها نمایش داده شوند."
                    else -> "فصل‌ها را تیک بزن و با انتخاب گزینه «امتحان»، روز آن را مشخص کن."
                }
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(enabled = false) {}
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(alpha = 0.99f)
                    ) {
                        drawRect(
                            color = Color.Black.copy(alpha = 0.85f),
                            size = size
                        )

                        if (guideTargetBounds != Rect.Zero) {
                            val padding = 4.dp.toPx()
                            drawRoundRect(
                                color = Color.Transparent,
                                topLeft = Offset(guideTargetBounds.left - padding, guideTargetBounds.top - padding),
                                size = Size(guideTargetBounds.width + padding * 2, guideTargetBounds.height + padding * 2),
                                cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
                                blendMode = BlendMode.Clear
                            )
                        }
                    }

                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (guideTargetBounds != Rect.Zero) {
                            val padding = 4.dp.toPx()
                            drawRoundRect(
                                color = Color(0xFF4CAF50),
                                topLeft = Offset(guideTargetBounds.left - padding, guideTargetBounds.top - padding),
                                size = Size(guideTargetBounds.width + padding * 2, guideTargetBounds.height + padding * 2),
                                cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
                                style = Stroke(width = 2.dp.toPx())
                            )
                        }
                    }

                    val alignTop = when (currentGuideStep) {
                        1 -> true
                        2 -> false
                        3 -> false
                        else -> true
                    }
                    Card(
                        colors = CardDefaults.cardColors(containerColor = colors.cardBg),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .align(if (alignTop) Alignment.TopCenter else Alignment.BottomCenter)
                            .padding(top = if (alignTop) 60.dp else 0.dp, bottom = if (alignTop) 0.dp else 40.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = guideTitle,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.accentMain,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = guideDesc,
                                fontSize = 13.sp,
                                color = colors.primaryText,
                                lineHeight = 22.sp,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    when (currentGuideStep) {
                                        1 -> {
                                            isGuideActive = false
                                        }
                                        2 -> {
                                            currentGuideStep = 3
                                            if (!selectedBooks.contains("زیست‌شناسی")) {
                                                selectedBooks.add("زیست‌شناسی")
                                            }
                                            if (selectedChapters["زیست‌شناسی"] == null) {
                                                selectedChapters["زیست‌شناسی"] = mutableStateListOf("فصل ۱: مولکول‌های اطلاعاتی")
                                            }
                                        }
                                        3 -> {
                                            isGuideActive = false
                                            isFirstTime = false
                                            prefs.edit().putBoolean("first_time_wizard_v8", false).apply()
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.align(Alignment.Start)
                            ) {
                                Text(
                                    text = if (currentGuideStep == 2) "بعدی" else "متوجه شدم",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Step3Notes(
    colors: ShetabColorPalette,
    notes: String,
    onNotesChange: (String) -> Unit,
    onNotesPositioned: (LayoutCoordinates) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "ثبت نکات و توضیحات دلخواه ✍️",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = colors.primaryText
        )
        Text(
            text = "قهرمان! هرگونه توضیح یا نکته خاصی که رایا باید در برنامه‌ریزی شما لحاظ کند را در زیر بنویس. مثلاً کلاس‌های ورزشی، ساعات خواب خاص، یا منابع مطالعاتی مشخص.",
            fontSize = 13.sp,
            color = colors.secondaryText,
            modifier = Modifier.padding(top = 6.dp, bottom = 16.dp)
        )
        
        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChange,
            placeholder = { 
                Text(
                    text = "مثال: دوشنبه‌ها عصر کلاس بسکتبال دارم... منابع شیمی من خیلی سنگین است و وقت بیشتری می‌خواهد...",
                    fontSize = 13.sp,
                    color = colors.secondaryText.copy(alpha = 0.5f),
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            modifier = Modifier
                .onGloballyPositioned { coordinates -> onNotesPositioned(coordinates) }
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(16.dp),
            textStyle = androidx.compose.ui.text.TextStyle(
                color = colors.primaryText,
                fontSize = 14.sp,
                textAlign = TextAlign.Right
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.accentMain,
                unfocusedBorderColor = colors.primaryText.copy(alpha = 0.15f),
                focusedContainerColor = colors.cardBg,
                unfocusedContainerColor = colors.cardBg
            )
        )
    }
}

@Composable
fun Step1Hours(
    colors: ShetabColorPalette,
    dailyHoursMap: MutableMap<String, Int>,
    onWedPositioned: (LayoutCoordinates) -> Unit,
    onThuPositioned: (LayoutCoordinates) -> Unit
) {
    val daysOfWeek = remember { listOf("شنبه", "یکشنبه", "دوشنبه", "سه شنبه", "چهارشنبه", "پنجشنبه", "جمعه") }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "ساعت مطالعه روزانه ⏰",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = colors.primaryText
        )
        Text(
            text = "لطفاً مشخص کن در هر روز هفته چند ساعت می‌تونی درس بخونی تا برنامه دقیق برات طراحی بشه.",
            fontSize = 13.sp,
            color = colors.secondaryText,
            modifier = Modifier.padding(top = 6.dp, bottom = 12.dp)
        )
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f)
        ) {
            itemsIndexed(
                items = daysOfWeek,
                span = { _, item ->
                    if (item == "جمعه") {
                        GridItemSpan(2)
                    } else {
                        GridItemSpan(1)
                    }
                }
            ) { _, day ->
                val hours = dailyHoursMap[day] ?: 0
                val isActive = hours > 0
                
                Column(
                    modifier = Modifier
                        .onGloballyPositioned { coordinates ->
                            if (day == "چهارشنبه") onWedPositioned(coordinates)
                            if (day == "پنجشنبه") onThuPositioned(coordinates)
                        }
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.cardBg)
                        .border(
                            width = 1.dp,
                            color = if (isActive) colors.accentMain.copy(alpha = 0.25f) else colors.primaryText.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isActive) colors.accentMain else colors.secondaryText.copy(alpha = 0.25f))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = day,
                            fontWeight = FontWeight.Bold,
                            color = colors.primaryText,
                            fontSize = 14.sp
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth(0.98f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(colors.bgMain)
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        IconButton(
                            onClick = {
                                if (hours > 0) {
                                    dailyHoursMap[day] = hours - 1
                                }
                            },
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = null,
                                modifier = Modifier.size(13.dp),
                                tint = Color.White
                            )
                        }
                        
                        Text(
                            text = "$hours ساعت",
                            fontWeight = FontWeight.Bold,
                            color = if (isActive) colors.accentMain else colors.secondaryText,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        
                        IconButton(
                            onClick = {
                                if (hours < 16) {
                                    dailyHoursMap[day] = hours + 1
                                }
                            },
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4CAF50))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(13.dp),
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Step2Books(
    colors: ShetabColorPalette,
    allBooks: List<String>,
    bookChapters: Map<String, List<String>>,
    selectedBooks: MutableList<String>,
    selectedChapters: MutableMap<String, MutableList<String>>,
    examChapters: MutableList<String>,
    examDays: MutableMap<String, String>,
    onFirstBookPositioned: (LayoutCoordinates) -> Unit,
    onFirstChapterPositioned: (LayoutCoordinates) -> Unit
) {
    val filteredBooks = allBooks

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "انتخاب مباحث و امتحانات 📚",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = colors.primaryText
        )
        Text(
            text = "کتاب‌ها و فصل‌های مورد نظرت را انتخاب کن. اگر برای هر فصلی امتحان داری، تیک امتحان آن را هم بزن تا برنامه متمرکزتر شود.",
            fontSize = 13.sp,
            color = colors.secondaryText,
            modifier = Modifier.padding(top = 6.dp, bottom = 12.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            itemsIndexed(filteredBooks) { index, book ->
                val isExpanded = selectedBooks.contains(book)
                val bookChaps = bookChapters[book] ?: emptyList()
                val selectedCount = selectedChapters[book]?.size ?: 0
                val totalCount = bookChaps.size
                
                Column(
                    modifier = Modifier
                        .onGloballyPositioned { coordinates ->
                            if (index == 0) onFirstBookPositioned(coordinates)
                        }
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.cardBg)
                        .border(
                            width = 1.dp,
                            color = if (isExpanded) colors.accentMain.copy(alpha = 0.2f) else Color.Transparent,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (isExpanded) {
                                    selectedBooks.remove(book)
                                    selectedChapters.remove(book)
                                    bookChaps.forEach { 
                                        examChapters.remove(it)
                                        examDays.remove(it)
                                    }
                                } else {
                                    selectedBooks.add(book)
                                    selectedChapters[book] = mutableStateListOf()
                                }
                            }
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (selectedCount > 0) colors.accentMain.copy(alpha = 0.1f) else colors.bgMain),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = getBookIcon(book),
                                    fontSize = 18.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = book,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.primaryText,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = if (selectedCount > 0) "$selectedCount از $totalCount مبحث انتخاب شده" else "هیچ مبحثی انتخاب نشده",
                                    fontSize = 12.sp,
                                    color = if (selectedCount > 0) colors.accentMain else colors.secondaryText
                                )
                            }
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = colors.secondaryText
                            )
                        }
                    }
                    
                    if (isExpanded) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colors.bgMain)
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            bookChaps.forEachIndexed { chapIndex, chapter ->
                                val isChapSelected = selectedChapters[book]?.contains(chapter) == true
                                val hasExamForChap = examChapters.contains(chapter)
                                
                                Column(
                                    modifier = Modifier
                                        .onGloballyPositioned { coordinates ->
                                            if (index == 0 && chapIndex == 0) onFirstChapterPositioned(coordinates)
                                        }
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(colors.cardBg)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                val list = selectedChapters[book] ?: mutableStateListOf()
                                                if (isChapSelected) {
                                                    list.remove(chapter)
                                                    examChapters.remove(chapter)
                                                    examDays.remove(chapter)
                                                } else {
                                                    list.add(chapter)
                                                }
                                                selectedChapters[book] = list
                                            }
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Checkbox(
                                                checked = isChapSelected,
                                                onCheckedChange = null,
                                                modifier = Modifier.scale(0.85f),
                                                colors = CheckboxDefaults.colors(checkedColor = colors.accentMain)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = chapter,
                                                fontSize = 13.sp,
                                                color = colors.primaryText,
                                                fontWeight = if (isChapSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }
                                        
                                        if (isChapSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(
                                                        if (hasExamForChap) colors.accentMain else colors.accentMain.copy(
                                                            alpha = 0.1f
                                                        )
                                                    )
                                                    .clickable {
                                                        if (hasExamForChap) {
                                                            examChapters.remove(chapter)
                                                            examDays.remove(chapter)
                                                        } else {
                                                            examChapters.add(chapter)
                                                            examDays[chapter] = "شنبه"
                                                        }
                                                    }
                                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                            ) {
                                                Text(
                                                    text = "امتحان 📝",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (hasExamForChap) Color.White else colors.accentMain
                                                )
                                            }
                                        }
                                    }

                                    AnimatedVisibility(
                                        visible = isChapSelected && hasExamForChap,
                                        enter = expandVertically() + fadeIn(),
                                        exit = shrinkVertically() + fadeOut()
                                    ) {
                                        val selectedDay = examDays[chapter] ?: "شنبه"
                                        val daysOfWeek = listOf("شنبه", "یکشنبه", "دوشنبه", "سه شنبه", "چهارشنبه", "پنجشنبه", "جمعه")
                                        
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 12.dp, end = 12.dp, bottom = 12.dp, top = 2.dp)
                                                .background(colors.bgMain, RoundedCornerShape(8.dp))
                                                .padding(10.dp)
                                        ) {
                                            Text(
                                                text = "انتخاب روز امتحان:",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = colors.secondaryText,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            )
                                            androidx.compose.foundation.lazy.LazyRow(
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                items(daysOfWeek) { d ->
                                                    val isDaySelected = selectedDay == d
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .background(
                                                                if (isDaySelected) colors.accentMain else colors.cardBg
                                                            )
                                                            .border(
                                                                width = 1.dp,
                                                                color = if (isDaySelected) colors.accentMain else colors.primaryText.copy(alpha = 0.1f),
                                                                shape = RoundedCornerShape(8.dp)
                                                            )
                                                            .clickable {
                                                                examDays[chapter] = d
                                                            }
                                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                                    ) {
                                                        Text(
                                                            text = d,
                                                            fontSize = 11.sp,
                                                            fontWeight = if (isDaySelected) FontWeight.Bold else FontWeight.Normal,
                                                            color = if (isDaySelected) Color.White else colors.primaryText
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
        }
    }
}



fun getBookIcon(book: String): String {
    return when (book) {
        "زیست‌شناسی" -> "🧬"
         "شیمی" -> "🧪"
        "ریاضیات" -> "📐"
        "فیزیک" -> "⚛️"
        "ادبیات" -> "🖋️"
        "عربی" -> "🕌"
        "زبان" -> "🌍"
        else -> "📚"
    }
}
