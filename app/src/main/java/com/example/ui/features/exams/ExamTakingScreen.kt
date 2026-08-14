package com.example.ui.features.exams

import com.example.ui.theme.IranSansFontFamily
import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import com.example.ui.core.components.LatexText
import com.example.ui.core.components.LatexSkeletonType
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ui.core.components.AppBackground
import com.example.ui.theme.LocalShetabColors
import com.example.ui.core.toPersianNumber
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.ui.viewinterop.AndroidView

import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.decode.SvgDecoder

@Composable
fun SvgWebView(
    assetPath: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data("file:///android_asset/$assetPath")
            .decoderFactory(SvgDecoder.Factory())
            .crossfade(true)
            .build(),
        contentDescription = "SVG Graphic",
        modifier = modifier,
        contentScale = androidx.compose.ui.layout.ContentScale.Fit
    )
}



val mockExamQuestions: List<com.example.data.ExamQuestion> get() = com.example.data.MockExamData.questions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamTakingScreen(
    navController: NavController,
    isPlacementTest: Boolean = false,
    customQuestions: List<com.example.data.ExamQuestion>? = null,
    isViewingAnswerSheet: Boolean = false,
    savedSelectedAnswers: Map<Int, Int> = emptyMap(),
    onPlacementFinish: ((Map<Int, Int>) -> Unit)? = null
) {
    val colors = LocalShetabColors.current
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context as? Activity
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
    var selectedAnswers by remember { mutableStateOf(mapOf<Int, Int>()) }
    var remainingSeconds by remember { mutableIntStateOf(if (isPlacementTest) 180 else 45 * 60) }
    var showBackWarning by remember { mutableStateOf(false) }
    var showFinishConfirmation by remember { mutableStateOf(false) }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var showPlacementResult by remember { mutableStateOf(false) }
    
    var isViewingAnswerSheetState by remember { mutableStateOf(isViewingAnswerSheet) }
    var savedSelectedAnswersState by remember { mutableStateOf(savedSelectedAnswers) }
    var showCustomExamReport by remember { mutableStateOf(false) }
    var examDurationMinutesUsed by remember { mutableIntStateOf(0) }

    val examType = com.example.data.MockExamData.globalExamType
    val isDescriptive = !isPlacementTest && (examType == "تشریحی")
    var descriptiveAnswers by remember { mutableStateOf(com.example.data.MockExamData.globalDescriptiveAnswers) }
    var uploadedImages by remember { mutableStateOf(mapOf<Int, Boolean>()) }

    val coroutineScope = rememberCoroutineScope()

    val questions = remember(isPlacementTest, customQuestions) {
        if (isPlacementTest) {
            listOf(
                com.example.data.ExamQuestion(
                    order = 1,
                    grade = "یازدهم",
                    fieldOfStudy = "تجربی",
                    book = "زیست‌شناسی",
                    chapter = "مبحث چرخه سلولی",
                    questionCategory = "تستی",
                    level = "متوسط",
                    type = "تستی",
                    question = "در کدام مرحله از چرخه سلولی، کروموزوم‌ها بیشترین فشردگی را دارند؟",
                    options = listOf("پروفاز", "متافاز", "آنافاز", "تلوفاز"),
                    answer = 1,
                    explanation = "",
                    tips = emptyList(),
                    needsImage = false,
                    topic = "",
                    examSource = "",
                    isProblem = false,
                    problem = emptyList(),
                    sourcePdf = ""
                ),
                com.example.data.ExamQuestion(
                    order = 2,
                    grade = "یازدهم",
                    fieldOfStudy = "تجربی",
                    book = "شیمی",
                    chapter = "مبحث گازهای هواکره",
                    questionCategory = "تستی",
                    level = "متوسط",
                    type = "تستی",
                    question = "کدام گاز بیشترین درصد حجمی هواکره را تشکیل می‌دهد؟",
                    options = listOf("اکسیژن", "کربن دی‌اکسید", "نیتروژن", "آرگون"),
                    answer = 2,
                    explanation = "",
                    tips = emptyList(),
                    needsImage = false,
                    topic = "",
                    examSource = "",
                    isProblem = false,
                    problem = emptyList(),
                    sourcePdf = ""
                ),
                com.example.data.ExamQuestion(
                    order = 3,
                    grade = "یازدهم",
                    fieldOfStudy = "تجربی",
                    book = "ریاضیات",
                    chapter = "مبحث دنباله حسابی",
                    questionCategory = "تستی",
                    level = "متوسط",
                    type = "تستی",
                    question = "در یک دنباله حسابی، اگر جمله اول ۲ و قدر نسبت ۳ باشد، جمله پنجم کدام است؟",
                    options = listOf("۱۴", "۱۷", "۱۱", "۲۰"),
                    answer = 0,
                    explanation = "",
                    tips = emptyList(),
                    needsImage = false,
                    topic = "",
                    examSource = "",
                    isProblem = false,
                    problem = emptyList(),
                    sourcePdf = ""
                )
            )
        } else {
            customQuestions ?: mockExamQuestions
        }
    }

    val totalQuestions = questions.size

    Box(modifier = Modifier.fillMaxSize()) {
        AppBackground()
        
        if (showPlacementResult) {
            val correctCount = questions.count { q ->
                selectedAnswers[q.order] == q.answer
            }
            val scorePercent = (correctCount * 100) / totalQuestions
            val resultsTuple = when (correctCount) {
                3 -> Triple("سطح پیشرفته (A+)", "🏆 عالی", "پاسخ‌دهی کامل و بدون اشتباه! تسلط علمی شما بر مفاهیم پایه زیست، شیمی و ریاضیات تجربی بی‌نظیر است. برنامه طلایی شما با رویکرد تست‌زنی پیشرفته و ارتقای سرعت تولید شده است.")
                2 -> Triple("سطح متوسط به بالا (B+)", "📈 بسیار خوب", "تسلط بسیار خوب بر مباحث اصلی. شما پایه‌های درسی مستحکمی دارید و با مرور منظم و تکنیک‌های تست‌زنی به راحتی به سطح صد در صد نزدیک خواهید شد.")
                1 -> Triple("سطح متوسط (B)", "📊 نیازمند تقویت", "مفاهیم کلی را به خوبی متوجه شده‌اید اما در جزئیات تست‌زنی نیاز به تسلط بیشتری دارید. برنامه شما بر روی آموزش عمیق‌تر مفاهیم و حل تمرینات متمرکز خواهد بود.")
                else -> Triple("سطح نیازمند تلاش (C)", "📚 شروع قوی", "نقطه شروع شما اینجاست! هوش مصنوعی رایا برنامه‌ای گام‌به‌گام از صفر برای شما طراحی کرده است تا بتوانید به مرور در تمامی دروس تخصص پیدا کنید.")
            }
            val levelTitle = resultsTuple.first
            val levelBadge = resultsTuple.second
            val levelDesc = resultsTuple.third

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(colors.accentMain.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🧠", fontSize = 48.sp)
                }
                Text(
                    text = "نتایج تعیین سطح علمی شما",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primaryText,
                    textAlign = TextAlign.Center
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.cardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = levelBadge,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.accentMain
                        )
                        Text(
                            text = levelTitle,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primaryText
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colors.bgMain, RoundedCornerShape(16.dp))
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "پاسخ درست", fontSize = 11.sp, color = colors.secondaryText)
                                Text(
                                    text = correctCount.toString().toPersianNumber(),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .height(30.dp)
                                    .width(1.dp)
                                    .background(colors.primaryText.copy(alpha = 0.1f))
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "درصد تسلط", fontSize = 11.sp, color = colors.secondaryText)
                                Text(
                                    text = "$scorePercent٪".toPersianNumber(),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.accentMain
                                )
                            }
                        }
                        Text(
                            text = levelDesc,
                            fontSize = 14.sp,
                            color = colors.secondaryText,
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp
                        )
                    }
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.accentMain.copy(alpha = 0.08f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "💡", fontSize = 24.sp)
                        Text(
                            text = "برنامه مطالعاتی هوشمند رایا با تکیه بر نقاط قوت و ضعف شناسایی شده در این آزمون شخصی‌سازی شد.",
                            fontSize = 12.sp,
                            color = colors.primaryText,
                            lineHeight = 20.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { onPlacementFinish?.invoke(selectedAnswers) },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = "مشاهده برنامه طلایی من ✨",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        

        } else if (showCustomExamReport) {
            // Gorgeous Full-Screen Custom Exam Report Screen
            val correctCount = remember { questions.count { q -> selectedAnswers[q.order] != null && (selectedAnswers[q.order]!! + 1 == q.answer) } }
            val incorrectCount = remember { selectedAnswers.size - correctCount }
            val unansweredCount = remember { questions.size - selectedAnswers.size }
            val scorePercentage = remember { if (questions.isNotEmpty()) (correctCount * 100) / questions.size else 0 }
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.bgMain),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .fillMaxHeight(0.85f)
                        .padding(8.dp)
                        .border(1.dp, colors.accentMain.copy(alpha = 0.15f), RoundedCornerShape(24.dp)),
                    colors = CardDefaults.cardColors(containerColor = colors.cardBg),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left Pane: Score & Congratulations
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "🎉 آزمون به پایان رسید!",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                color = colors.accentMain,
                                fontFamily = IranSansFontFamily,
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // Percentage Circle
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(110.dp)
                            ) {
                                CircularProgressIndicator(
                                    progress = { scorePercentage.toFloat() / 100f },
                                    modifier = Modifier.fillMaxSize(),
                                    color = if (scorePercentage >= 70) Color(0xFF10B981) else colors.accentMain,
                                    strokeWidth = 8.dp,
                                    trackColor = colors.primaryText.copy(alpha = 0.06f),
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${scorePercentage.toString().toPersianNumber()}%",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp,
                                        color = colors.primaryText
                                    )
                                    Text(
                                        text = "درصد نمره",
                                        fontSize = 9.sp,
                                        color = colors.secondaryText,
                                        fontFamily = IranSansFontFamily
                                    )
                                }
                            }
                        }
                        
                        // Right Pane: Stats & Actions
                        Column(
                            modifier = Modifier
                                .weight(1.2f)
                                .fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "کارنامه و گزارش تحلیل رایا برای سنجش شما:",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = colors.secondaryText,
                                    textAlign = TextAlign.Center,
                                    fontFamily = IranSansFontFamily
                                )
                                
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(colors.bgMain.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                        .padding(vertical = 10.dp, horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = correctCount.toString().toPersianNumber(),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = Color(0xFF10B981)
                                        )
                                        Text(
                                            text = "درست",
                                            fontSize = 10.sp,
                                            color = colors.secondaryText,
                                            fontFamily = IranSansFontFamily
                                        )
                                    }
                                    
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = incorrectCount.toString().toPersianNumber(),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = Color(0xFFEF4444)
                                        )
                                        Text(
                                            text = "نادرست",
                                            fontSize = 10.sp,
                                            color = colors.secondaryText,
                                            fontFamily = IranSansFontFamily
                                        )
                                    }
                                    
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = unansweredCount.toString().toPersianNumber(),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = Color(0xFF78909C)
                                        )
                                        Text(
                                            text = "بدون پاسخ",
                                            fontSize = 10.sp,
                                            color = colors.secondaryText,
                                            fontFamily = IranSansFontFamily
                                        )
                                    }
                                }
                            }
                            
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Button(
                                    onClick = {
                                        savedSelectedAnswersState = selectedAnswers
                                        isViewingAnswerSheetState = true
                                        showCustomExamReport = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().height(42.dp)
                                ) {
                                    Text(
                                        text = "ورود به صفحه نمایش پاسخ‌نامه تشریحی",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color.White,
                                        fontFamily = IranSansFontFamily
                                    )
                                }
                                
                                TextButton(
                                    onClick = { navController.popBackStack() },
                                    modifier = Modifier.fillMaxWidth().height(36.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(
                                        text = "بازگشت به پرتال آزمون‌ها",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.secondaryText,
                                        fontFamily = IranSansFontFamily
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            ExamTakingContent(
                colors = colors,
                questions = questions,
                isDescriptive = isDescriptive,
                initialRemainingSeconds = if (isPlacementTest) 180 else 45 * 60,
                isViewingAnswerSheet = isViewingAnswerSheetState,
                savedAnswers = savedSelectedAnswersState,
                onFinishExam = { selAns, descAns, uplImgs ->
                    selectedAnswers = selAns
                    if (isPlacementTest) {
                        showPlacementResult = true
                    } else {
                        // Generate stats and add to history
                        val corr = questions.count { q -> selAns[q.order] != null && (selAns[q.order]!! + 1 == q.answer) }
                        val incorr = selAns.size - corr
                        val pct = if (questions.isNotEmpty()) (corr * 100) / questions.size else 0
                        
                        // Add to our persistent reactive history
                        com.example.data.MockExamData.addExamToHistory(
                            subject = questions.firstOrNull()?.book ?: "آزمون شخصی",
                            topic = questions.firstOrNull()?.chapter ?: "مبحث دلخواه",
                            questionCount = questions.size,
                            correctCount = corr,
                            incorrectCount = incorr,
                            durationMinutes = 45,
                            percentage = pct
                        )
                        
                        showCustomExamReport = true
                    }
                },
                onExitExam = {
                    if (isPlacementTest) {
                        onPlacementFinish?.invoke(emptyMap())
                    } else {
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}
@Composable
fun QuestionReportDialog(
    onDismissRequest: () -> Unit,
    onSubmitReport: (String) -> Unit
) {
    val colors = LocalShetabColors.current
    val context = androidx.compose.ui.platform.LocalContext.current
    var customText by remember { mutableStateOf("") }
    
    val predefinedReasons = listOf(
        "غلط تایپی در سوال",
        "عدم تطابق پاسخنامه",
        "سوال مبهم یا گنگ",
        "اشکال در تصویر سوال",
        "پاسخ تشریحی اشتباه",
        "سایر اشکالات علمی"
    )

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismissRequest,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .heightIn(max = 450.dp)
                .padding(12.dp),
            colors = CardDefaults.cardColors(containerColor = colors.cardBg),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "⚠️", fontSize = 18.sp)
                    Text(
                        text = "گزارش خطا در سوال",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primaryText,
                        fontFamily = IranSansFontFamily
                    )
                }

                HorizontalDivider(color = colors.primaryText.copy(alpha = 0.08f))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "لطفاً توضیحات خطا یا اشکال مشاهده شده در سوال را وارد کنید:",
                        fontSize = 11.sp,
                        color = colors.secondaryText,
                        fontWeight = FontWeight.Medium,
                        fontFamily = IranSansFontFamily,
                        lineHeight = 16.sp
                    )

                    OutlinedTextField(
                        value = customText,
                        onValueChange = { customText = it },
                        placeholder = { 
                            Text(
                                "توضیحات خود را اینجا بنویسید یا از متن‌های آماده زیر انتخاب کنید...", 
                                fontSize = 10.sp, 
                                color = colors.secondaryText.copy(alpha = 0.5f),
                                fontFamily = IranSansFontFamily,
                                lineHeight = 14.sp
                            ) 
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 11.sp, 
                            color = colors.primaryText,
                            fontFamily = IranSansFontFamily
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.accentMain,
                            unfocusedBorderColor = colors.primaryText.copy(alpha = 0.12f),
                            cursorColor = colors.accentMain,
                            focusedContainerColor = colors.bgMain.copy(alpha = 0.03f),
                            unfocusedContainerColor = colors.bgMain.copy(alpha = 0.01f)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "متن‌های آماده (جهت اضافه شدن سریع به توضیحات):",
                        fontSize = 11.sp,
                        color = colors.secondaryText,
                        fontWeight = FontWeight.Bold,
                        fontFamily = IranSansFontFamily
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        predefinedReasons.forEach { reason ->
                            Box(
                                modifier = Modifier
                                    .background(colors.accentMain.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
                                    .border(1.dp, colors.accentMain.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                                    .clickable {
                                        val currentText = customText.trim()
                                        customText = if (currentText.isEmpty()) {
                                            reason
                                        } else {
                                            "$currentText - $reason"
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "➕",
                                        fontSize = 8.sp
                                    )
                                    Text(
                                        text = reason,
                                        fontSize = 10.sp,
                                        color = colors.accentMain,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = IranSansFontFamily
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = colors.primaryText.copy(alpha = 0.08f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismissRequest
                    ) {
                        Text(
                            text = "انصراف",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.secondaryText,
                            fontFamily = IranSansFontFamily
                        )
                    }

                    Button(
                        onClick = {
                            if (customText.trim().isEmpty()) {
                                android.widget.Toast.makeText(context, "لطفاً متنی بنویسید یا از گزینه‌های آماده انتخاب کنید", android.widget.Toast.LENGTH_SHORT).show()
                            } else {
                                onSubmitReport(customText.trim())
                                android.widget.Toast.makeText(context, "گزارش خطای شما با موفقیت ثبت شد", android.widget.Toast.LENGTH_SHORT).show()
                                onDismissRequest()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "ارسال گزارش",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = IranSansFontFamily
                        )
                    }
                }
            }
        }
    }
}
