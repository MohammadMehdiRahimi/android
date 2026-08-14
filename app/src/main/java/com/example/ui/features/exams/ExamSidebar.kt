package com.example.ui.features.exams

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExamQuestion
import com.example.ui.core.toPersianNumber
import com.example.ui.theme.ShetabColorPalette
import com.example.ui.theme.IranSansFontFamily

sealed class SidebarItem {
    data class Header(val title: String) : SidebarItem()
    data class QuestionRow(val questions: List<IndexedValue<ExamQuestion>>) : SidebarItem()
}

@Composable
fun ExamSidebar(
    isViewingAnswerSheet: Boolean,
    onExitExam: () -> Unit,
    onFinishClick: () -> Unit,
    minutesStr: String,
    secondsStr: String,
    remainingSeconds: Int,
    totalQuestions: Int,
    answeredQuestions: Int,
    questions: List<ExamQuestion>,
    isDescriptive: Boolean,
    descriptiveAnswers: Map<Int, String>,
    uploadedImages: Map<Int, Boolean>,
    selectedAnswers: Map<Int, Int>,
    visitedQuestions: Set<Int>,
    currentQuestionIndex: Int,
    onQuestionClick: (Int) -> Unit,
    onNextClick: () -> Unit,
    onPrevClick: () -> Unit,
    colors: ShetabColorPalette
) {
    Surface(
        modifier = Modifier
            .width(160.dp)
            .fillMaxHeight(),
        color = colors.cardBg,
        shadowElevation = 4.dp
    ) {
        val listState = rememberLazyListState()
        
        val sidebarItems = remember(questions) {
            val items = mutableListOf<SidebarItem>()
            val grouped = questions.withIndex().groupBy { it.value.book }
            grouped.forEach { (book, qList) ->
                items.add(SidebarItem.Header(book))
                qList.chunked(2).forEach { chunk ->
                    items.add(SidebarItem.QuestionRow(chunk))
                }
            }
            items
        }

        LaunchedEffect(currentQuestionIndex) {
            val activeItemIndex = sidebarItems.indexOfFirst {
                it is SidebarItem.QuestionRow && it.questions.any { q -> q.index == currentQuestionIndex }
            }
            if (activeItemIndex >= 0) {
                listState.animateScrollToItem(activeItemIndex)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Bottom + WindowInsetsSides.Start))
                .padding(top = 12.dp, bottom = 12.dp, start = 6.dp, end = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header stats & Actions
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { 
                        if (isViewingAnswerSheet) {
                            onExitExam()
                        } else {
                            onFinishClick()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = if (isViewingAnswerSheet) Icons.AutoMirrored.Filled.ArrowBack else Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isViewingAnswerSheet) "خروج" else "پایان آزمون", 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 10.sp,
                        fontFamily = IranSansFontFamily
                    )
                }
                
                HorizontalDivider(color = colors.primaryText.copy(alpha = 0.08f))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (!isViewingAnswerSheet) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(1.dp)
                        ) {
                            Text(
                                text = "باقی‌مانده",
                                fontSize = 9.sp,
                                color = colors.secondaryText,
                                fontWeight = FontWeight.Bold,
                                fontFamily = IranSansFontFamily
                            )
                            Text(
                                text = "$minutesStr:$secondsStr".toPersianNumber(),
                                color = if (remainingSeconds < 60) Color(0xFFE53935) else colors.accentMain,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                fontFamily = IranSansFontFamily
                            )
                        }
                    }
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        Text(
                            text = if (isViewingAnswerSheet) "کل سوالات" else "پاسخ‌ها",
                            fontSize = 9.sp,
                            color = colors.secondaryText,
                            fontWeight = FontWeight.Bold,
                            fontFamily = IranSansFontFamily
                        )
                        Text(
                            text = if (isViewingAnswerSheet) totalQuestions.toString().toPersianNumber() else "${answeredQuestions.toString().toPersianNumber()}/${totalQuestions.toString().toPersianNumber()}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primaryText
                        )
                    }
                }
                
                HorizontalDivider(color = colors.primaryText.copy(alpha = 0.08f))
            }
            
            // Question Grid
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(sidebarItems.size) { index ->
                    when (val item = sidebarItems[index]) {
                        is SidebarItem.Header -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                HorizontalDivider(
                                    modifier = Modifier.weight(1f),
                                    color = colors.primaryText.copy(alpha = 0.2f)
                                )
                                Text(
                                    text = item.title,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.primaryText,
                                    fontFamily = IranSansFontFamily,
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    textAlign = TextAlign.Center
                                )
                                HorizontalDivider(
                                    modifier = Modifier.weight(1f),
                                    color = colors.primaryText.copy(alpha = 0.2f)
                                )
                            }
                        }
                        is SidebarItem.QuestionRow -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                            ) {
                                item.questions.forEach { (idx, q) ->
                                    val isDone = if (isDescriptive) {
                                        (descriptiveAnswers[q.order]?.isNotBlank() == true) || (uploadedImages[q.order] == true)
                                    } else {
                                        selectedAnswers[q.order] != null
                                    }
                                    
                                    val isCurrent = currentQuestionIndex == idx
                                    val isSkipped = !isDone && (idx in visitedQuestions) && !isCurrent
                                    
                                    val bg = when {
                                        isViewingAnswerSheet -> {
                                            if (isDone) Color(0xFF1E88E5) else Color(0xFFFFA726)
                                        }
                                        isDone -> Color(0xFF1E88E5) // Blue
                                        isSkipped -> Color(0xFFFFA726) // Yellow-Orange
                                        else -> Color.Transparent
                                    }
                                    
                                    val textColor = when {
                                        isViewingAnswerSheet -> Color.White
                                        isDone || isSkipped -> Color.White
                                        else -> colors.primaryText
                                    }

                                    val isUnanswered =
                                        !isViewingAnswerSheet && bg == Color.Transparent
                                    val borderStroke = when {
                                        isCurrent -> androidx.compose.foundation.BorderStroke(2.dp, colors.accentMain)
                                        isUnanswered -> androidx.compose.foundation.BorderStroke(1.dp, colors.primaryText.copy(alpha = 0.25f))
                                        else -> null
                                    }
                                    
                                    Box(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clip(CircleShape)
                                            .background(bg)
                                            .then(
                                                if (borderStroke != null) Modifier.border(borderStroke, CircleShape) else Modifier
                                            )
                                            .clickable { onQuestionClick(idx) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = (idx + 1).toString().toPersianNumber(),
                                            color = textColor,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = IranSansFontFamily
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // Footer Actions
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onNextClick,
                    enabled = currentQuestionIndex < totalQuestions - 1,
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("بعدی", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                }
                
                Button(
                    onClick = onPrevClick,
                    enabled = currentQuestionIndex > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF78909C),
                        contentColor = Color.White,
                        disabledContainerColor = colors.primaryText.copy(alpha = 0.05f),
                        disabledContentColor = colors.primaryText.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("قبلی", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }
}
