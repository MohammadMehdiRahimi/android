package com.example.ui.features.exams

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ui.core.components.AppBackground
import com.example.ui.core.toPersianNumber
import com.example.ui.theme.IranSansFontFamily
import com.example.ui.theme.LocalShetabColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildExamScreen(navController: NavController) {
    val colors = LocalShetabColors.current
    var currentStep by remember { mutableIntStateOf(1) }
    var showHelpDialog by remember { mutableStateOf(false) }

    // === Step 1 State ===
    var examType by remember { mutableStateOf("تستی") }
    var grade by remember { mutableStateOf("دهم") }
    var field by remember { mutableStateOf("ریاضی‌فیزیک") }
    var selectedBooks by remember {
        mutableStateOf(
            listOf(
                SelectedExamBook(
                    id = "math_10",
                    bookName = "ریاضی دهم",
                    chapter = "فصل ۲: تابع",
                    topics = listOf("تابع و نمودار", "دامنه و برد", "ترکیب توابع"),
                    gradientColors = listOf(Color(0xFF7C3AED), Color(0xFF4F46E5))
                ),
                SelectedExamBook(
                    id = "math_11",
                    bookName = "ریاضی یازدهم",
                    chapter = "فصل ۱: مجموعه‌ها",
                    topics = listOf("مفهوم مجموعه", "عملیات روی مجموعه‌ها"),
                    gradientColors = listOf(Color(0xFF059669), Color(0xFF10B981))
                )
            )
        )
    }
    var questionSource by remember { mutableStateOf("تألیفی / کنکور") }

    // === Step 2 State ===
    var questionConfigs by remember {
        mutableStateOf(
            mapOf(
                "math_10" to BookQuestionConfig(
                    bookId = "math_10",
                    bookName = "ریاضی دهم",
                    chapter = "فصل ۲: تابع",
                    topics = listOf("تابع و نمودار", "دامنه و برد", "ترکیب توابع"),
                    gradientColors = listOf(Color(0xFF7C3AED), Color(0xFF4F46E5)),
                    easyCount = 4,
                    mediumCount = 5,
                    hardCount = 3,
                    veryHardCount = 2
                ),
                "math_11" to BookQuestionConfig(
                    bookId = "math_11",
                    bookName = "ریاضی یازدهم",
                    chapter = "فصل ۱: مجموعه‌ها",
                    topics = listOf("مفهوم مجموعه", "عملیات روی مجموعه‌ها"),
                    gradientColors = listOf(Color(0xFF059669), Color(0xFF10B981)),
                    easyCount = 4,
                    mediumCount = 4,
                    hardCount = 3,
                    veryHardCount = 3
                )
            )
        )
    }
    var hasNegativeScore by remember { mutableStateOf(true) }
    var randomQuestionOrder by remember { mutableStateOf(true) }

    // === Step 3 State ===
    var examName by remember { mutableStateOf("") }
    var examTimeMinutes by remember { mutableStateOf("45") }
    var hasTimeLimit by remember { mutableStateOf(true) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(modifier = Modifier.fillMaxSize().testTag("build_exam_screen")) {
            AppBackground(customBgColor = Color(0xFFFAFAFC))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // ====================================================
                // Top App Bar (Title, Subtitle, Help ?, Back Arrow)
                // ====================================================
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Arrow Button (Right in RTL)
                    IconButton(
                        onClick = {
                            if (currentStep > 1) {
                                currentStep--
                            } else {
                                navController.popBackStack()
                            }
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("wizard_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "بازگشت",
                            tint = colors.primaryText,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Title & Subtitle (Center)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "طراحی آزمون جدید",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = colors.primaryText,
                            fontFamily = IranSansFontFamily
                        )

                        Text(
                            text = when (currentStep) {
                                1 -> "مرحله ۱: انتخاب ساختار آزمون"
                                2 -> "مرحله ۲: تنظیم سوالات"
                                else -> "مرحله ۳: ساخت آزمون"
                            },
                            fontSize = 11.5.sp,
                            color = colors.secondaryText.copy(alpha = 0.8f),
                            fontFamily = IranSansFontFamily
                        )
                    }

                    // Help ? Button (Left in RTL)
                    IconButton(
                        onClick = { showHelpDialog = true },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("wizard_help_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.HelpOutline,
                            contentDescription = "راهنما",
                            tint = colors.secondaryText,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // ====================================================
                // 3-Step Stepper Progress Header
                // ====================================================
                ExamWizardStepper(
                    currentStep = currentStep,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // ====================================================
                // Step Contents (1, 2, 3)
                // ====================================================
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (currentStep) {
                        1 -> {
                            // Step 1 Screen matching exam-create-1.png with inline add book
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Step1ExamStructureScreen(
                                    examType = examType,
                                    onExamTypeChange = {
                                        examType = it
                                        com.example.data.MockExamData.globalExamType = it
                                    },
                                    grade = grade,
                                    onGradeChange = { grade = it },
                                    field = field,
                                    onFieldChange = { field = it },
                                    selectedBooks = selectedBooks,
                                    onRemoveBook = { bookId ->
                                        selectedBooks = selectedBooks.filter { it.id != bookId }
                                        questionConfigs = questionConfigs.filterKeys { it != bookId }
                                    },
                                    onAddBook = { newBook ->
                                        if (selectedBooks.none { it.id == newBook.id }) {
                                            selectedBooks = selectedBooks + newBook
                                            questionConfigs = questionConfigs + (newBook.id to BookQuestionConfig(
                                                bookId = newBook.id,
                                                bookName = newBook.bookName,
                                                chapter = newBook.chapter,
                                                topics = newBook.topics,
                                                gradientColors = newBook.gradientColors
                                            ))
                                        }
                                    },
                                    questionSource = questionSource,
                                    onQuestionSourceChange = { questionSource = it },
                                    onNextStep = { currentStep = 2 },
                                    onCancel = { navController.popBackStack() }
                                )
                            }
                        }

                        2 -> {
                            // Step 2 Screen: Pixel-perfect question setup matching exam-create-2.png
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Step2QuestionSettingsScreen(
                                    examType = examType,
                                    grade = grade,
                                    field = field,
                                    selectedBooks = selectedBooks,
                                    questionConfigs = questionConfigs,
                                    onConfigChange = { id, updated ->
                                        questionConfigs = questionConfigs + (id to updated)
                                    },
                                    onRemoveBook = { bookId ->
                                        selectedBooks = selectedBooks.filter { it.id != bookId }
                                        questionConfigs = questionConfigs.filterKeys { it != bookId }
                                    },
                                    questionSource = questionSource,
                                    hasNegativeScore = hasNegativeScore,
                                    onNegativeScoreChange = { hasNegativeScore = it },
                                    randomQuestionOrder = randomQuestionOrder,
                                    onRandomQuestionOrderChange = { randomQuestionOrder = it },
                                    onNextStep = { currentStep = 3 },
                                    onPrevStep = { currentStep = 1 }
                                )
                            }
                        }

                        3 -> {
                            // Step 3 Screen: Finalize, Exam Name, Duration, Start
                            val totalQuestionSum = questionConfigs.values.sumOf { it.totalCount }
                            Step3ExamFinalizeScreen(
                                examName = examName,
                                onExamNameChange = { examName = it },
                                examTimeMinutes = examTimeMinutes,
                                onExamTimeMinutesChange = { examTimeMinutes = it },
                                hasNegativeScore = hasNegativeScore,
                                onNegativeScoreChange = { hasNegativeScore = it },
                                hasTimeLimit = hasTimeLimit,
                                onTimeLimitChange = { hasTimeLimit = it },
                                totalQuestions = totalQuestionSum,
                                selectedBooksCount = selectedBooks.size,
                                onStartExam = {
                                    navController.navigate("exam_taking")
                                },
                                onPrevStep = { currentStep = 2 }
                            )
                        }
                    }
                }
            }

            // Help Dialog
            if (showHelpDialog) {
                AlertDialog(
                    onDismissRequest = { showHelpDialog = false },
                    title = {
                        Text(
                            text = "راهنمای ساخت آزمون",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primaryText,
                            fontFamily = IranSansFontFamily,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    text = {
                        Text(
                            text = "در این سامانه می‌توانید با انتخاب نوع آزمون (تستی یا تشریحی)، مشخص کردن پایه‌ها و کتاب‌های درسی، تعیین تعداد و سطح دشواری سوالات، آزمون‌های اختصاصی و هوشمند ایجاد نمایید.",
                            fontSize = 13.sp,
                            color = colors.secondaryText,
                            lineHeight = 22.sp,
                            fontFamily = IranSansFontFamily,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = { showHelpDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("متوجه شدم", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = IranSansFontFamily)
                        }
                    },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }
    }
}

// ====================================================
// 3-Step Progress Stepper
// ====================================================
@Composable
fun ExamWizardStepper(
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    val activeColor = Color(0xFF6366F1) // Purple / Indigo
    val inactiveColor = Color(0xFFE5E7EB) // Gray
    val inactiveTextColor = Color(0xFF6B7280)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Step 1 (Right in RTL): ۱. ساختار آزمون (Shows checkmark if passed)
        StepIndicatorItem(
            stepNumber = 1,
            title = "۱. ساختار آزمون",
            isActive = currentStep >= 1,
            isCurrent = currentStep == 1,
            isCompleted = currentStep > 1,
            activeColor = activeColor,
            inactiveColor = inactiveColor,
            inactiveTextColor = inactiveTextColor,
            modifier = Modifier.weight(1f)
        )

        // Dashed Divider 1-2
        DashedStepDivider(
            isActive = currentStep >= 2,
            activeColor = activeColor,
            inactiveColor = inactiveColor,
            modifier = Modifier.width(36.dp)
        )

        // Step 2 (Center in RTL): ۲. تنظیم سوالات
        StepIndicatorItem(
            stepNumber = 2,
            title = "۲. تنظیم سوالات",
            isActive = currentStep >= 2,
            isCurrent = currentStep == 2,
            isCompleted = currentStep > 2,
            activeColor = activeColor,
            inactiveColor = inactiveColor,
            inactiveTextColor = inactiveTextColor,
            modifier = Modifier.weight(1f)
        )

        // Dashed Divider 2-3
        DashedStepDivider(
            isActive = currentStep >= 3,
            activeColor = activeColor,
            inactiveColor = inactiveColor,
            modifier = Modifier.width(36.dp)
        )

        // Step 3 (Left in RTL): ۳. ساخت آزمون
        StepIndicatorItem(
            stepNumber = 3,
            title = "۳. ساخت آزمون",
            isActive = currentStep >= 3,
            isCurrent = currentStep == 3,
            isCompleted = false,
            activeColor = activeColor,
            inactiveColor = inactiveColor,
            inactiveTextColor = inactiveTextColor,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StepIndicatorItem(
    stepNumber: Int,
    title: String,
    isActive: Boolean,
    isCurrent: Boolean,
    isCompleted: Boolean,
    activeColor: Color,
    inactiveColor: Color,
    inactiveTextColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isActive) activeColor else Color.White)
                .border(1.5.dp, if (isActive) activeColor else inactiveColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Text(
                    text = stepNumber.toString().toPersianNumber(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isActive) Color.White else inactiveTextColor,
                    fontFamily = IranSansFontFamily
                )
            }
        }

        Text(
            text = title,
            fontSize = 10.5.sp,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
            color = if (isCurrent) activeColor else inactiveTextColor,
            fontFamily = IranSansFontFamily,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
fun DashedStepDivider(
    isActive: Boolean,
    activeColor: Color,
    inactiveColor: Color,
    modifier: Modifier = Modifier
) {
    val color = if (isActive) activeColor else inactiveColor
    Box(
        modifier = modifier
            .padding(bottom = 16.dp)
            .height(2.dp)
            .drawBehind {
                drawLine(
                    color = color,
                    start = Offset(0f, size.height / 2),
                    end = Offset(size.width, size.height / 2),
                    strokeWidth = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                )
            }
    )
}

@Composable
fun VerticalMetricDivider() {
    VerticalDivider(
        modifier = Modifier
            .height(28.dp)
            .padding(horizontal = 2.dp),
        color = Color(0xFFE5E7EB)
    )
}

// ====================================================
// Step 3 Support Screen
// ====================================================
@Composable
fun Step3ExamFinalizeScreen(
    examName: String,
    onExamNameChange: (String) -> Unit,
    examTimeMinutes: String,
    onExamTimeMinutesChange: (String) -> Unit,
    hasNegativeScore: Boolean,
    onNegativeScoreChange: (Boolean) -> Unit,
    hasTimeLimit: Boolean,
    onTimeLimitChange: (Boolean) -> Unit,
    totalQuestions: Int,
    selectedBooksCount: Int,
    onStartExam: () -> Unit,
    onPrevStep: () -> Unit
) {
    val colors = LocalShetabColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("مشخصات نهایی آزمون", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = colors.primaryText, fontFamily = IranSansFontFamily)

                OutlinedTextField(
                    value = examName,
                    onValueChange = onExamNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("نام آزمون (اختیاری)", fontFamily = IranSansFontFamily) },
                    placeholder = { Text("مثلاً آزمون جامع فصل ۱ و ۲", fontFamily = IranSansFontFamily) },
                    shape = RoundedCornerShape(14.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("نمره منفی برای پاسخ نادرست", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = colors.primaryText, fontFamily = IranSansFontFamily)
                    CustomSwitch(checked = hasNegativeScore, onCheckedChange = onNegativeScoreChange)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("محدودیت زمان آزمون", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = colors.primaryText, fontFamily = IranSansFontFamily)
                    CustomSwitch(checked = hasTimeLimit, onCheckedChange = onTimeLimitChange)
                }

                if (hasTimeLimit) {
                    OutlinedTextField(
                        value = examTimeMinutes,
                        onValueChange = onExamTimeMinutesChange,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("مدت زمان آزمون (دقیقه)", fontFamily = IranSansFontFamily) },
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onStartExam,
            modifier = Modifier.fillMaxWidth().height(52.dp).testTag("start_exam_button"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("شروع آزمون", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = IranSansFontFamily)
        }

        TextButton(onClick = onPrevStep, modifier = Modifier.fillMaxWidth()) {
            Text("مرحله قبل", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6366F1), fontFamily = IranSansFontFamily)
        }
    }
}

@Composable
fun CustomSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = Color(0xFF7C3AED),
            uncheckedThumbColor = Color.White,
            uncheckedTrackColor = Color(0xFFD1D5DB)
        )
    )
}
