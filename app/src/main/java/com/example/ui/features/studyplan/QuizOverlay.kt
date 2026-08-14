package com.example.ui.features.studyplan

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.example.ui.core.toPersianNumber
import com.example.ui.theme.ShetabColorPalette

@Composable
fun QuizOverlay(
    colors: ShetabColorPalette,
    onFinishQuiz: () -> Unit
) {
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedAnswers by remember { mutableStateOf(mapOf<Int, Int>()) }
    var remainingSeconds by remember { mutableIntStateOf(180) }
    var showFinishConfirmation by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }

    val questions = remember {
        listOf(
            Triple(
                "در کدام مرحله از چرخه سلولی، کروموزوم‌ها بیشترین فشردگی را دارند؟",
                listOf("پروفاز", "متافاز", "آنافاز", "تلوفاز"),
                1 // متافاز
            ),
            Triple(
                "کدام گاز بیشترین درصد حجمی هواکره را تشکیل می‌دهد؟",
                listOf("اکسیژن", "کربن دی‌اکسید", "نیتروژن", "آرگون"),
                2 // نیتروژن
            ),
            Triple(
                "در یک دنباله حسابی، اگر جمله اول ۲ و قدر نسبت ۳ باشد، جمله پنجم کدام است؟",
                listOf("۱۴", "۱۷", "۱۱", "۲۰"),
                0 // ۱۴
            )
        )
    }

    val totalQuestions = questions.size
    val answeredCount = selectedAnswers.size
    val remainingCount = totalQuestions - answeredCount

    LaunchedEffect(showResult) {
        if (!showResult) {
            while (remainingSeconds > 0) {
                kotlinx.coroutines.delay(1000L)
                remainingSeconds--
            }
        }
    }

    val minutesStr = (remainingSeconds / 60).toString().padStart(2, '0')
    val secondsStr = (remainingSeconds % 60).toString().padStart(2, '0')

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgMain)
    ) {
        if (!showResult) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    Surface(
                        color = colors.cardBg,
                        tonalElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.statusBarsPadding()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "تعیین سطح علمی 🧠",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.primaryText
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = colors.accentMain,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "مانده: ${remainingCount.toString().toPersianNumber()}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = colors.accentMain
                                        )
                                    }

                                    Text(
                                        text = "$minutesStr:$secondsStr".toPersianNumber(),
                                        color = if (remainingSeconds < 30) Color(0xFFF44336) else colors.accentMain,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp
                                    )

                                    Button(
                                        onClick = { showFinishConfirmation = true },
                                        contentPadding = PaddingValues(horizontal = 12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFE53935),
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("پایان آزمون", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                },
                bottomBar = {
                    Surface(
                        color = colors.cardBg,
                        tonalElevation = 4.dp,
                        shadowElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { if (currentQuestionIndex < totalQuestions - 1) currentQuestionIndex++ },
                                enabled = currentQuestionIndex < totalQuestions - 1,
                                colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("سوال بعدی", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Next",
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Text(
                                text = "سوال ${(currentQuestionIndex + 1).toString().toPersianNumber()} از ${totalQuestions.toString().toPersianNumber()}",
                                fontWeight = FontWeight.Bold,
                                color = colors.secondaryText,
                                fontSize = 13.sp
                            )

                            Button(
                                onClick = { if (currentQuestionIndex > 0) currentQuestionIndex-- },
                                enabled = currentQuestionIndex > 0,
                                colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Previous",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("قبلی", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val q = questions[currentQuestionIndex]

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.cardBg),
                        border = BorderStroke(1.dp, colors.primaryText.copy(alpha = 0.05f))
                    ) {
                        Text(
                            text = q.first,
                            fontSize = 16.sp,
                            lineHeight = 26.sp,
                            color = colors.primaryText,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(20.dp)
                        )
                    }

                    q.second.forEachIndexed { idx, option ->
                        val isSelected = selectedAnswers[currentQuestionIndex] == idx
                        val bgColor = if (isSelected) colors.accentMain.copy(alpha = 0.08f) else colors.cardBg
                        val borderColor = if (isSelected) colors.accentMain else colors.primaryText.copy(alpha = 0.05f)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(bgColor)
                                .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                                .clickable {
                                    selectedAnswers = selectedAnswers + (currentQuestionIndex to idx)
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        color = if (isSelected) colors.accentMain else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .border(
                                        width = 2.dp,
                                        color = if (isSelected) colors.accentMain else colors.secondaryText.copy(alpha = 0.4f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                } else {
                                    Text(
                                        text = (idx + 1).toString().toPersianNumber(),
                                        color = colors.secondaryText,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = option,
                                color = colors.primaryText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        } else {
            // HIGH-FIDELITY LEVEL PLACEMENT RESULT SCREEN
            val correctCount = questions.filterIndexed { index, triple ->
                selectedAnswers[index] == triple.third
            }.size

            val scorePercent = (correctCount * 100) / totalQuestions

            val (levelTitle, levelBadge, levelDesc) = when (correctCount) {
                3 -> Triple("سطح پیشرفته (A+)", "🏆 عالی", "پاسخ‌دهی کامل و بدون اشتباه! تسلط علمی شما بر مفاهیم پایه زیست، شیمی و ریاضیات تجربی بی‌نظیر است. برنامه طلایی شما با رویکرد تست‌زنی پیشرفته و ارتقای سرعت تولید شده است.")
                2 -> Triple("سطح متوسط به بالا (B+)", "📈 بسیار خوب", "تسلط بسیار خوب بر مباحث اصلی. شما پایه‌های درسی مستحکمی دارید و با مرور منظم و تکنیک‌های تست‌زنی به راحتی به سطح صد در صد نزدیک خواهید شد.")
                1 -> Triple("سطح متوسط (B)", "📊 نیازمند تقویت", "مفاهیم کلی را به خوبی متوجه شده‌اید اما در جزئیات تست‌زنی نیاز به تسلط بیشتری دارید. برنامه شما بر روی آموزش عمیق‌تر مفاهیم و حل تمرینات متمرکز خواهد بود.")
                else -> Triple("سطح نیازمند تلاش (C)", "📚 شروع قوی", "نقطه شروع شما اینجاست! هوش مصنوعی رایا برنامه‌ای گام‌به‌گام از صفر برای شما طراحی کرده است تا بتوانید به مرور در تمامی دروس تخصص پیدا کنید.")
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            Brush.linearGradient(
                                listOf(colors.accentMain, colors.accentMain.copy(alpha = 0.4f))
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$scorePercent٪".toPersianNumber(),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "نمره نهایی",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Text(
                    text = "کارنامه تعیین سطح علمی ⚡",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primaryText,
                    textAlign = TextAlign.Center
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.cardBg),
                    border = BorderStroke(1.dp, colors.accentMain.copy(alpha = 0.15f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFB300),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = levelTitle,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = colors.accentMain
                            )
                        }

                        SuggestionChip(
                            onClick = {},
                            label = { Text(levelBadge, fontWeight = FontWeight.Bold) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                labelColor = colors.accentMain,
                                containerColor = colors.accentMain.copy(alpha = 0.08f)
                            ),
                            border = BorderStroke(1.dp, colors.accentMain.copy(alpha = 0.2f))
                        )

                        Text(
                            text = levelDesc,
                            fontSize = 13.sp,
                            lineHeight = 22.sp,
                            color = colors.secondaryText,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }

                Text(
                    text = "آنالیز مباحث درسی:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primaryText,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )

                // Biology Diagnosis
                SubjectProgressRow(
                    subject = "زیست‌شناسی (مبحث چرخه سلولی)",
                    isCorrect = selectedAnswers[0] == questions[0].third,
                    colors = colors
                )

                // Chemistry Diagnosis
                SubjectProgressRow(
                    subject = "شیمی (مبحث گازهای هواکره)",
                    isCorrect = selectedAnswers[1] == questions[1].third,
                    colors = colors
                )

                // Math Diagnosis
                SubjectProgressRow(
                    subject = "ریاضیات (مبحث دنباله حسابی)",
                    isCorrect = selectedAnswers[2] == questions[2].third,
                    colors = colors
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { onFinishQuiz() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "مشاهده برنامه طلایی من ✨",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }

        if (showFinishConfirmation) {
            androidx.compose.ui.window.Dialog(
                onDismissRequest = { showFinishConfirmation = false }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.cardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(colors.accentMain.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = colors.accentMain,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Text(
                            text = "ثبت نهایی آزمون",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = colors.primaryText
                        )

                        Text(
                            text = "آیا از ثبت کارنامه تعیین سطح خود مطمئن هستید؟ رایا بر اساس این پاسخ‌ها برنامه شما را طراحی می‌کند.",
                            fontSize = 13.sp,
                            color = colors.secondaryText,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    showFinishConfirmation = false
                                    showResult = true
                                },
                                modifier = Modifier.weight(1f).height(46.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("ثبت قطعی", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            OutlinedButton(
                                onClick = { showFinishConfirmation = false },
                                modifier = Modifier.weight(1f).height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, colors.primaryText.copy(alpha = 0.15f))
                            ) {
                                Text("بازگشت", color = colors.primaryText, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubjectProgressRow(
    subject: String,
    isCorrect: Boolean,
    colors: ShetabColorPalette
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.cardBg)
            .border(1.dp, colors.primaryText.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = subject,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colors.primaryText
            )
            Text(
                text = if (isCorrect) "پاسخ صحیح" else "بدون پاسخ یا پاسخ نادرست",
                fontSize = 10.sp,
                color = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFE53935),
                fontWeight = FontWeight.Medium
            )
        }

        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    if (isCorrect) Color(0xFF4CAF50).copy(alpha = 0.1f) else Color(0xFFE53935).copy(alpha = 0.1f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isCorrect) "✓" else "✗",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFE53935)
            )
        }
    }
}
