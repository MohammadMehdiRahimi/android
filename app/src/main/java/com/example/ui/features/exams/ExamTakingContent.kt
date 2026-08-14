package com.example.ui.features.exams

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExamQuestion
import com.example.ui.core.components.AppBackground
import com.example.ui.core.toPersianNumber
import com.example.ui.theme.ShetabColorPalette
import androidx.compose.ui.draw.clip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamTakingContent(
    colors: ShetabColorPalette,
    questions: List<ExamQuestion>,
    isDescriptive: Boolean = false,
    initialRemainingSeconds: Int = 45 * 60,
    isViewingAnswerSheet: Boolean = false,
    savedAnswers: Map<Int, Int> = emptyMap(),
    onFinishExam: (selectedAnswers: Map<Int, Int>, descriptiveAnswers: Map<Int, String>, uploadedImages: Map<Int, Boolean>) -> Unit,
    onExitExam: () -> Unit
) {
    var selectedAnswers by remember { mutableStateOf(savedAnswers) }
    
    LaunchedEffect(savedAnswers) {
        if (isViewingAnswerSheet) {
            selectedAnswers = savedAnswers
        }
    }

    var descriptiveAnswers by remember { mutableStateOf(mapOf<Int, String>()) }
    var uploadedImages by remember { mutableStateOf(mapOf<Int, Boolean>()) }
    
    var remainingSeconds by remember { mutableIntStateOf(initialRemainingSeconds) }
    var showBackWarning by remember { mutableStateOf(false) }
    var showFinishConfirmation by remember { mutableStateOf(false) }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var visitedQuestions by remember { mutableStateOf(setOf<Int>(0)) }

    val totalQuestions = questions.size
    val answeredQuestions = if (isDescriptive) {
        questions.count { q -> (descriptiveAnswers[q.order]?.isNotBlank() == true) || (uploadedImages[q.order] == true) }
    } else {
        selectedAnswers.size
    }
    val remainingQuestions = totalQuestions - answeredQuestions

    LaunchedEffect(isViewingAnswerSheet) {
        if (!isViewingAnswerSheet) {
            while (remainingSeconds > 0) {
                kotlinx.coroutines.delay(1000L)
                remainingSeconds--
            }
        }
    }

    BackHandler(enabled = true) {
        showBackWarning = true
    }

    val minutesStr = (remainingSeconds / 60).toString().padStart(2, '0')
    val secondsStr = (remainingSeconds % 60).toString().padStart(2, '0')

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(modifier = Modifier.fillMaxSize()) {
            AppBackground()
            
            Row(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                ExamSidebar(
                    isViewingAnswerSheet = isViewingAnswerSheet,
                    onExitExam = onExitExam,
                    onFinishClick = { showFinishConfirmation = true },
                    minutesStr = minutesStr,
                    secondsStr = secondsStr,
                    remainingSeconds = remainingSeconds,
                    totalQuestions = totalQuestions,
                    answeredQuestions = answeredQuestions,
                    questions = questions,
                    isDescriptive = isDescriptive,
                    descriptiveAnswers = descriptiveAnswers,
                    uploadedImages = uploadedImages,
                    selectedAnswers = selectedAnswers,
                    visitedQuestions = visitedQuestions,
                    currentQuestionIndex = currentQuestionIndex,
                    onQuestionClick = { 
                        currentQuestionIndex = it 
                        visitedQuestions = visitedQuestions + it
                    },
                    onNextClick = { 
                        if (currentQuestionIndex < totalQuestions - 1) {
                            currentQuestionIndex++ 
                            visitedQuestions = visitedQuestions + currentQuestionIndex
                        }
                    },
                    onPrevClick = { 
                        if (currentQuestionIndex > 0) {
                            currentQuestionIndex-- 
                            visitedQuestions = visitedQuestions + currentQuestionIndex
                        }
                    },
                    colors = colors
                )
                
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom + WindowInsetsSides.End))
                        .padding(top = 4.dp, bottom = 12.dp, start = 12.dp, end = 12.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    val question = questions[currentQuestionIndex]
                    ExamQuestionCard(
                        question = question,
                        questionIndex = currentQuestionIndex,
                        selectedOption = if (isViewingAnswerSheet) savedAnswers[question.order] else selectedAnswers[question.order],
                        onOptionSelected = { optionIndex ->
                            if (!isViewingAnswerSheet) {
                                selectedAnswers = if (selectedAnswers[question.order] == optionIndex) {
                                    selectedAnswers - question.order
                                } else {
                                    selectedAnswers + (question.order to optionIndex)
                                }
                            }
                        },
                        isDescriptive = isDescriptive,
                        typedAnswer = descriptiveAnswers[question.order] ?: "",
                        onTypedAnswerChange = { text ->
                            if (!isViewingAnswerSheet) {
                                descriptiveAnswers = descriptiveAnswers + (question.order to text)
                            }
                        },
                        onUploadImage = {
                            if (!isViewingAnswerSheet) {
                                uploadedImages = uploadedImages + (question.order to true)
                            }
                        },
                        imageUploaded = uploadedImages[question.order] == true,
                        isViewingAnswerSheet = isViewingAnswerSheet,
                        modifier = Modifier.fillMaxSize()
                    )
                }


            }

            if (showBackWarning) {
                androidx.compose.ui.window.Dialog(
                    onDismissRequest = { showBackWarning = false },
                    properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .padding(16.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.cardBg),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color(0xFFE53935).copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = Color(0xFFE53935),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Text(
                                    "خروج از آزمون",
                                    color = colors.primaryText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text(
                                "آیا مطمئن هستید؟ پیشرفت شما در این آزمون ذخیره نخواهد شد.",
                                color = colors.secondaryText,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth(),
                                lineHeight = 18.sp
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = { 
                                        showBackWarning = false
                                        onExitExam()
                                    },
                                    modifier = Modifier.weight(1f).height(40.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("خروج قطعی", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                                OutlinedButton(
                                    onClick = { showBackWarning = false },
                                    modifier = Modifier.weight(1f).height(40.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.primaryText.copy(alpha = 0.2f)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("ادامه آزمون", color = colors.primaryText, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }

            if (showFinishConfirmation) {
                androidx.compose.ui.window.Dialog(
                    onDismissRequest = { showFinishConfirmation = false },
                    properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    Card(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(0.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.bgMain),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .windowInsetsPadding(WindowInsets.safeDrawing)
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(colors.cardBg, RoundedCornerShape(12.dp))
                                    .padding(vertical = 10.dp, horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "کل سوالات : ${totalQuestions.toString().toPersianNumber()}",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.primaryText
                                )
                                VerticalDivider(color = colors.primaryText.copy(alpha = 0.15f), modifier = Modifier.height(18.dp))
                                Text(
                                    text = "پاسخ داده شده : ${answeredQuestions.toString().toPersianNumber()}",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.accentMain
                                )
                                VerticalDivider(color = colors.primaryText.copy(alpha = 0.15f), modifier = Modifier.height(18.dp))
                                Text(
                                    text = "بدون پاسخ : ${remainingQuestions.toString().toPersianNumber()}",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE53935)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                                    columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(2),
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    contentPadding = PaddingValues(bottom = 8.dp)
                                ) {
                                    items(questions.size) { idx ->
                                        val question = questions[idx]
                                        val selected = selectedAnswers[question.order]
                                        val textAns = descriptiveAnswers[question.order] ?: ""
                                        val hasImg = uploadedImages[question.order] == true
                                        val isDone = if (isDescriptive) (textAns.isNotBlank() || hasImg) else (selected != null)
                                        
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { 
                                                    currentQuestionIndex = idx
                                                    showFinishConfirmation = false 
                                                },
                                            colors = CardDefaults.cardColors(containerColor = colors.cardBg),
                                            shape = RoundedCornerShape(12.dp),
                                            border = androidx.compose.foundation.BorderStroke(
                                                width = 1.dp,
                                                color = if (isDone) Color(0xFF1E88E5).copy(alpha = 0.3f) else Color(0xFFFFA726).copy(alpha = 0.3f)
                                            )
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(28.dp)
                                                            .background(
                                                                if (isDone) Color(0xFF1E88E5) else Color(0xFFFFA726),
                                                                CircleShape
                                                            ),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            text = (idx + 1).toString().toPersianNumber(),
                                                            color = Color.White,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 12.sp
                                                        )
                                                    }
                                                    Text(
                                                        text = "سوال ${(idx + 1).toString().toPersianNumber()}",
                                                        color = colors.primaryText,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 14.sp
                                                    )
                                                }
                                                
                                                if (isDescriptive) {
                                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                        if (textAns.isNotBlank()) {
                                                            Surface(
                                                                shape = RoundedCornerShape(8.dp),
                                                                color = Color(0xFF4CAF50).copy(alpha = 0.12f)
                                                            ) {
                                                                Text(
                                                                    "متنی",
                                                                    color = Color(0xFF4CAF50),
                                                                    fontSize = 11.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                                )
                                                            }
                                                        }
                                                        if (hasImg) {
                                                            Surface(
                                                                shape = RoundedCornerShape(8.dp),
                                                                color = colors.accentMain.copy(alpha = 0.12f)
                                                            ) {
                                                                Text(
                                                                    "تصویر",
                                                                    color = colors.accentMain,
                                                                    fontSize = 11.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                                )
                                                            }
                                                        }
                                                        if (!isDone) {
                                                            Text("بی‌پاسخ", color = colors.secondaryText, fontSize = 11.sp)
                                                        }
                                                    }
                                                } else {
                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        (0..3).forEach { optIdx ->
                                                            val isBubbleSelected = selected == optIdx
                                                            val bubbleBg = if (isBubbleSelected) colors.accentMain else Color.Transparent
                                                            val bubbleBorder = if (isBubbleSelected) colors.accentMain else colors.primaryText.copy(alpha = 0.25f)
                                                            val bubbleTextColor = if (isBubbleSelected) Color.White else colors.secondaryText
                                                            
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(24.dp)
                                                                    .background(bubbleBg, CircleShape)
                                                                    .border(1.dp, bubbleBorder, CircleShape),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                Text(
                                                                    text = (optIdx + 1).toString().toPersianNumber(),
                                                                    color = bubbleTextColor,
                                                                    fontSize = 10.5.sp,
                                                                    fontWeight = FontWeight.Bold
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
                            
                            HorizontalDivider(color = colors.primaryText.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 12.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { showFinishConfirmation = false },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.primaryText.copy(alpha = 0.2f)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("بازگشت به آزمون", color = colors.primaryText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Button(
                                    onClick = {
                                        showFinishConfirmation = false
                                        onFinishExam(selectedAnswers, descriptiveAnswers, uploadedImages)
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("تایید و ثبت نهایی", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
