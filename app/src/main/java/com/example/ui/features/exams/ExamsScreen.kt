package com.example.ui.features.exams

import com.example.ui.theme.IranSansFontFamily
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ui.core.components.AppBackground
import com.example.ui.theme.LocalShetabColors
import com.example.ui.core.toPersianNumber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamsScreen(navController: NavController) {
    val colors = LocalShetabColors.current
    var visibleExamCount by remember { mutableStateOf(4) }
    var selectedExamForDetails by remember { mutableStateOf<com.example.data.MyExamHistoryItem?>(null) }
    var showComingSoonModal by remember { mutableStateOf(false) }

    val examHistory = com.example.data.MockExamData.examsHistoryList

    if (selectedExamForDetails != null) {
        val exam = selectedExamForDetails!!
        AlertDialog(
            onDismissRequest = { selectedExamForDetails = null },
            confirmButton = {
                Button(
                    onClick = { selectedExamForDetails = null },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("متوجه شدم", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = IranSansFontFamily)
                }
            },
            title = {
                Text(
                    text = "جزئیات آزمون ${exam.subject}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colors.primaryText,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth(),
                    fontFamily = IranSansFontFamily
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "مبحث آموزشی: ${exam.topic}",
                        fontSize = 13.sp,
                        color = colors.secondaryText,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth(),
                        fontFamily = IranSansFontFamily
                    )
                    Text(
                        text = "تعداد کل سوالات: ${exam.questionCount.toString().toPersianNumber()} سوال",
                        fontSize = 13.sp,
                        color = colors.secondaryText,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth(),
                        fontFamily = IranSansFontFamily
                    )
                    Text(
                        text = "مدت زمان پاسخگویی: ${exam.durationMinutes.toString().toPersianNumber()} دقیقه",
                        fontSize = 13.sp,
                        color = colors.secondaryText,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth(),
                        fontFamily = IranSansFontFamily
                    )
                    Text(
                        text = "تاریخ برگزاری: ${exam.date.toPersianNumber()}",
                        fontSize = 13.sp,
                        color = colors.secondaryText,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth(),
                        fontFamily = IranSansFontFamily
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    HorizontalDivider(color = colors.primaryText.copy(alpha = 0.08f))
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "درصد نهایی شما:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = colors.primaryText,
                            fontFamily = IranSansFontFamily
                        )
                        Text(
                            text = "${exam.scorePercentage.toString().toPersianNumber()}٪",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (exam.scorePercentage >= 80) Color(0xFF10B981) else colors.accentMain,
                            fontFamily = IranSansFontFamily
                        )
                    }
                }
            },
            containerColor = colors.cardBg,
            shape = RoundedCornerShape(24.dp)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AppBackground(customBgColor = colors.bgMain)
        
        Column(modifier = Modifier.fillMaxSize()) {
            // Modern Elegant Colorless / Transparent Top Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Transparent,
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(72.dp)
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(colors.cardBg)
                            .border(1.dp, colors.primaryText.copy(alpha = 0.08f), CircleShape)
                            .clickable { navController.popBackStack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "بازگشت",
                            tint = colors.primaryText,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = "آزمون‌ها",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primaryText,
                        letterSpacing = (-0.5).sp
                    )

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(colors.cardBg)
                            .border(1.dp, colors.primaryText.copy(alpha = 0.08f), CircleShape)
                            .clickable { /* Info help */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "راهنما",
                            tint = colors.primaryText,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top Row: Custom Exam Builder
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .padding(horizontal = 20.dp)
                            .clickable { navController.navigate("build_exam") }
                            .border(
                                width = 1.5.dp,
                                color = colors.accentMain.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(24.dp)
                            ),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.cardBg),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(
                                    color = colors.accentMain.copy(alpha = 0.04f),
                                    radius = 110.dp.toPx(),
                                    center = Offset(0f, size.height)
                                )
                            }
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .background(colors.accentMain.copy(alpha = 0.12f), RoundedCornerShape(14.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            tint = colors.accentMain,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = colors.accentMain.copy(alpha = 0.08f)
                                    ) {
                                        Text(
                                            text = "هوشمند 🧠",
                                            color = colors.accentMain,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                            fontFamily = IranSansFontFamily
                                        )
                                    }
                                }
                                
                                Column {
                                    Text(
                                        text = "ساخت آزمون شخصی",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = colors.primaryText,
                                        fontFamily = IranSansFontFamily
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "طراحی سنجش دلخواه و سفارشی با سامانه ارزیابی هوشمند",
                                        fontSize = 9.5.sp,
                                        color = colors.secondaryText,
                                        maxLines = 2,
                                        lineHeight = 14.sp,
                                        fontFamily = IranSansFontFamily
                                    )
                                }
                            }
                        }
                    }
                }

                // Section Header: My Exams
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "آزمون‌های من",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primaryText,
                            fontFamily = IranSansFontFamily
                        )
                        
                        Box(
                            modifier = Modifier
                                .background(colors.accentMain.copy(alpha = 0.08f), CircleShape)
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "آخرین عملکردها".toPersianNumber(),
                                color = colors.accentMain,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = IranSansFontFamily
                            )
                        }
                    }
                }

                // Exams List (Table Layout with Circular Progress on the right)
                val visibleExams = examHistory.take(visibleExamCount)
                items(visibleExams.size) { index ->
                    val exam = visibleExams[index]
                    ExamHistoryItemRow(
                        item = exam,
                        onMoreClick = { selectedExamForDetails = exam },
                        onViewReport = { showComingSoonModal = true }
                    )
                }

                // Expandable Action Button (Pill shaped and text-rich)
                if (visibleExamCount < examHistory.size) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            TextButton(
                                onClick = { visibleExamCount = examHistory.size },
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .background(colors.accentMain.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                                    .border(1.dp, colors.accentMain.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "نمایش آزمون‌های قدیمی‌تر",
                                        color = colors.accentMain,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = IranSansFontFamily
                                    )
                                    Icon(
                                        imageVector = Icons.Default.MoreHoriz,
                                        contentDescription = null,
                                        tint = colors.accentMain,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showComingSoonModal) {
            AlertDialog(
                onDismissRequest = { showComingSoonModal = false },
                confirmButton = {
                    Button(
                        onClick = { showComingSoonModal = false },
                        colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("متوجه شدم", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = IranSansFontFamily)
                    }
                },
                title = {
                    Text(
                        text = "به زودی",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = colors.primaryText,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth(),
                        fontFamily = IranSansFontFamily
                    )
                },
                text = {
                    Text(
                        text = "به زودی این ویژگی اضافه خواهد شد.",
                        color = colors.secondaryText,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth(),
                        fontFamily = IranSansFontFamily
                    )
                },
                containerColor = colors.cardBg,
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}

@Composable
fun ScoreCircularIndicator(percentage: Int, modifier: Modifier = Modifier) {
    val colors = LocalShetabColors.current
    
    val strokeColor = when {
        percentage >= 85 -> Color(0xFF10B981) // Vibrant Emerald Green
        percentage >= 70 -> colors.accentMain // Deep indigo/blue
        percentage >= 60 -> colors.accentSecondary // Cyan/blue
        else -> Color(0xFFEF4444) // Vibrant Ruby Red
    }
    
    Box(
        modifier = modifier
            .size(56.dp)
            .background(strokeColor.copy(alpha = 0.04f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = percentage / 100f,
            modifier = Modifier.fillMaxSize(),
            color = strokeColor,
            strokeWidth = 4.5.dp,
            trackColor = colors.primaryText.copy(alpha = 0.06f)
        )
        Text(
            text = "${percentage.toString().toPersianNumber()}٪",
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            color = colors.primaryText,
            fontFamily = IranSansFontFamily
        )
    }
}

@Composable
fun ExamHistoryItemRow(
    item: com.example.data.MyExamHistoryItem,
    onMoreClick: () -> Unit,
    onViewReport: () -> Unit
) {
    val colors = LocalShetabColors.current
    var showMenu by remember { mutableStateOf(false) }
    
    val performanceColor = when {
        item.scorePercentage >= 85 -> Color(0xFF10B981) // Emerald Green
        item.scorePercentage >= 70 -> colors.accentMain
        item.scorePercentage >= 60 -> colors.accentSecondary
        else -> Color(0xFFEF4444) // Ruby Red
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable { onViewReport() }
            .border(
                width = 1.dp,
                color = colors.primaryText.copy(alpha = 0.06f),
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScoreCircularIndicator(percentage = item.scorePercentage)
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = item.subject,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = colors.primaryText,
                        fontFamily = IranSansFontFamily
                    )
                    
                    Box(
                        modifier = Modifier
                            .background(performanceColor.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = item.topic,
                            fontSize = 9.5.sp,
                            color = performanceColor,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            fontFamily = IranSansFontFamily
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(10.dp))
                
                // Row A: Primary stats (Questions, Correct, Incorrect)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(Color(0xFFE8F5E9), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(11.dp)
                            )
                        }
                        Text(
                            text = "${item.questionCount.toString().toPersianNumber()} سوال",
                            fontSize = 10.sp,
                            color = colors.secondaryText,
                            fontWeight = FontWeight.Medium,
                            fontFamily = IranSansFontFamily
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(Color(0xFFE8F5E9), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(11.dp)
                            )
                        }
                        Text(
                            text = "${item.correctCount.toString().toPersianNumber()} درست",
                            fontSize = 10.sp,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold,
                            fontFamily = IranSansFontFamily
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(Color(0xFFFFEBEE), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = Color(0xFFC62828),
                                modifier = Modifier.size(11.dp)
                            )
                        }
                        Text(
                            text = "${item.incorrectCount.toString().toPersianNumber()} غلط",
                            fontSize = 10.sp,
                            color = Color(0xFFC62828),
                            fontWeight = FontWeight.Bold,
                            fontFamily = IranSansFontFamily
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                // Row B: Secondary stats (Duration, Date)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(Color(0xFFE3F2FD), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = Color(0xFF1565C0),
                                modifier = Modifier.size(11.dp)
                            )
                        }
                        Text(
                            text = "${item.durationMinutes.toString().toPersianNumber()} دقیقه",
                            fontSize = 10.sp,
                            color = colors.secondaryText,
                            fontWeight = FontWeight.Medium,
                            fontFamily = IranSansFontFamily
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(Color(0xFFFFF3E0), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Event,
                                contentDescription = null,
                                tint = Color(0xFFEF6C00),
                                modifier = Modifier.size(11.dp)
                            )
                        }
                        Text(
                            text = item.date.toPersianNumber(),
                            fontSize = 10.sp,
                            color = colors.secondaryText,
                            fontWeight = FontWeight.Medium,
                            fontFamily = IranSansFontFamily
                        )
                    }
                }
            }
            
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "گزینه‌ها",
                        tint = colors.secondaryText.copy(alpha = 0.8f)
                    )
                }
                
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier
                        .background(colors.cardBg)
                        .border(1.dp, colors.primaryText.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                ) {
                    DropdownMenuItem(
                        text = { Text("توضیحات بیشتر", color = colors.primaryText, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = IranSansFontFamily) },
                        onClick = {
                            showMenu = false
                            onMoreClick()
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.HelpOutline,
                                contentDescription = null,
                                tint = colors.accentMain,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("مشاهده کارنامه جامع", color = colors.primaryText, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = IranSansFontFamily) },
                        onClick = {
                            showMenu = false
                            onViewReport()
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Assignment,
                                contentDescription = null,
                                tint = colors.accentSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }
        }
    }
}

