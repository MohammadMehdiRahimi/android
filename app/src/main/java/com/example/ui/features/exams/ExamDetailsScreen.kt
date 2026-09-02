package com.example.ui.features.exams

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.R
import com.example.data.MockExamData
import com.example.ui.core.components.AppBackground
import com.example.ui.core.toPersianNumber
import com.example.ui.theme.IranSansFontFamily
import com.example.ui.theme.LocalShetabColors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ExamDetails(
    val examId: String,
    val title: String,
    val topicBadge: String,
    val organizer: String,
    val date: String,
    val startTime: String,
    val durationMinutes: Int,
    val questionCount: Int,
    val questionType: String,
    val totalScore: Int
)

data class ExamDetailsUiState(
    val isLoading: Boolean = false,
    val examDetails: ExamDetails? = null,
    val error: String? = null
)

class ExamDetailsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ExamDetailsUiState())
    val uiState: StateFlow<ExamDetailsUiState> = _uiState.asStateFlow()

    fun loadExamDetails(examId: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        val matchingExam = MockExamData.examsHistoryList.find { it.id == examId }
        val dynamicQuestions = MockExamData.questions
        val firstQ = dynamicQuestions.firstOrNull()

        val details = if (matchingExam != null) {
            ExamDetails(
                examId = examId,
                title = "آزمون ${matchingExam.subject}",
                topicBadge = matchingExam.topic.ifEmpty { "آزمون جامع فصل ۱ تا ۳" },
                organizer = "استاد احمدی",
                date = "جمعه ۲۴ خرداد ۱۴۰۳",
                startTime = "۱۰:۰۰ صبح",
                durationMinutes = matchingExam.durationMinutes.takeIf { it > 0 } ?: 90,
                questionCount = matchingExam.questionCount.takeIf { it > 0 } ?: 40,
                questionType = "چهارگزینه‌ای",
                totalScore = matchingExam.questionCount.takeIf { it > 0 } ?: 40
            )
        } else {
            val subjectName = firstQ?.book ?: "زیست شناسی دهم"
            val topicName = firstQ?.chapter ?: "آزمون جامع فصل ۱ تا ۳"
            val qCount = if (dynamicQuestions.isNotEmpty()) dynamicQuestions.size else 40
            ExamDetails(
                examId = examId.ifEmpty { "2841" },
                title = "آزمون $subjectName",
                topicBadge = topicName,
                organizer = "استاد احمدی",
                date = "جمعه ۲۴ خرداد ۱۴۰۳",
                startTime = "۱۰:۰۰ صبح",
                durationMinutes = 90,
                questionCount = qCount,
                questionType = "چهارگزینه‌ای",
                totalScore = qCount
            )
        }

        _uiState.update {
            it.copy(
                isLoading = false,
                examDetails = details,
                error = null
            )
        }
    }
}

private val PrimaryPurple = Color(0xFF5B42F3)
private val LightPurpleBg = Color(0xFFF3F0FF)
private val PurpleDotColor = Color(0xFF5B42F3)
private val LightScreenBg = Color(0xFFF9FAFB)
private val TextMainColor = Color(0xFF0F172A)
private val TextSecondaryColor = Color(0xFF64748B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamDetailsScreen(
    navController: NavController,
    examId: String,
    viewModel: ExamDetailsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(examId) {
        viewModel.loadExamDetails(examId)
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LightScreenBg)
                .testTag("exam_details_screen")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // === Top App Bar ===
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    // Back Button on Start
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                            .shadow(2.dp, CircleShape)
                            .testTag("exam_details_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "بازگشت",
                            tint = TextMainColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Title and Subtitle Centered
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "ورود به آزمون",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextMainColor,
                                fontFamily = IranSansFontFamily
                            )
                            Icon(
                                imageVector = Icons.Outlined.Assignment,
                                contentDescription = null,
                                tint = PrimaryPurple,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Text(
                            text = "با وارد کردن شناسه آزمون، وارد آزمون شوید",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = TextSecondaryColor,
                            textAlign = TextAlign.Center,
                            fontFamily = IranSansFontFamily
                        )
                    }
                }

                val details = uiState.examDetails

                if (details != null) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // === Section 1: شناسه آزمون ===
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Section Header with Dot
                            SectionTitleWithDot(title = "شناسه آزمون")

                            // ID Container Card
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("exam_id_input_card"),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                                    ) {
                                        // "ID" Tag
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(LightPurpleBg)
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "ID",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = PrimaryPurple,
                                                fontFamily = IranSansFontFamily
                                            )
                                        }

                                        // ID Number
                                        Text(
                                            text = details.examId.toPersianNumber(),
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextMainColor,
                                            fontFamily = IranSansFontFamily
                                        )
                                    }

                                    // Scanner Viewfinder Icon Button
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(PrimaryPurple),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.CropFree,
                                            contentDescription = "اسکن کد",
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }

                            // Helper text
                            Text(
                                text = "شناسه آزمون را از مدرس یا برگزارکننده دریافت کنید.",
                                fontSize = 12.sp,
                                color = TextSecondaryColor,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 2.dp),
                                fontFamily = IranSansFontFamily
                            )
                        }

                        // === Section 2: مشخصات آزمون ===
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Section Header with Dot
                            SectionTitleWithDot(title = "مشخصات آزمون")

                            // Specifications Card
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("exam_details_hero_card"),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    // Top Banner (Illustration + Title + Topic Chip)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        // Left/Start Exam Checklist Illustration
                                        ExamIllustrationBadge()

                                        // Title & Topic Badge
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = details.title,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = TextMainColor,
                                                fontFamily = IranSansFontFamily,
                                                lineHeight = 26.sp
                                            )

                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(LightPurpleBg)
                                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = details.topicBadge,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = PrimaryPurple,
                                                    fontFamily = IranSansFontFamily
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Specification Rows (7 Items)
                                    ExamSpecRow(
                                        label = "برگزارکننده",
                                        value = details.organizer,
                                        icon = Icons.Outlined.Person
                                    )

                                    HorizontalDivider(color = Color(0xFFF8FAFC), thickness = 1.dp)

                                    ExamSpecRow(
                                        label = "تاریخ برگزاری",
                                        value = details.date,
                                        icon = Icons.Outlined.CalendarToday
                                    )

                                    HorizontalDivider(color = Color(0xFFF8FAFC), thickness = 1.dp)

                                    ExamSpecRow(
                                        label = "ساعت شروع",
                                        value = details.startTime,
                                        icon = Icons.Outlined.AccessTime
                                    )

                                    HorizontalDivider(color = Color(0xFFF8FAFC), thickness = 1.dp)

                                    ExamSpecRow(
                                        label = "مدت زمان",
                                        value = "${details.durationMinutes.toString().toPersianNumber()} دقیقه",
                                        icon = Icons.Outlined.HourglassEmpty
                                    )

                                    HorizontalDivider(color = Color(0xFFF8FAFC), thickness = 1.dp)

                                    ExamSpecRow(
                                        label = "تعداد سوالات",
                                        value = "${details.questionCount.toString().toPersianNumber()} سوال",
                                        icon = Icons.Outlined.Checklist
                                    )

                                    HorizontalDivider(color = Color(0xFFF8FAFC), thickness = 1.dp)

                                    ExamSpecRow(
                                        label = "نوع سوالات",
                                        value = details.questionType,
                                        icon = Icons.Outlined.HelpOutline
                                    )

                                    HorizontalDivider(color = Color(0xFFF8FAFC), thickness = 1.dp)

                                    ExamSpecRow(
                                        label = "نمره کل",
                                        value = "${details.totalScore.toString().toPersianNumber()} نمره",
                                        icon = Icons.Outlined.StarOutline
                                    )
                                }
                            }
                        }

                        // === Section 3: Note / Warning Box ===
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F3FF)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEDE9FE))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = null,
                                    tint = PrimaryPurple,
                                    modifier = Modifier.size(22.dp)
                                )

                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "توجه داشته باشید",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PrimaryPurple,
                                        fontFamily = IranSansFontFamily
                                    )
                                    Text(
                                        text = "پس از شروع آزمون، امکان خروج وجود ندارد و زمان آزمون شروع خواهد شد.",
                                        fontSize = 12.sp,
                                        color = TextSecondaryColor,
                                        fontFamily = IranSansFontFamily,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // === Bottom CTA Action Bar ===
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        shadowElevation = 10.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 16.dp)
                                .navigationBarsPadding()
                        ) {
                            Button(
                                onClick = {
                                    navController.navigate("exam_taking")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .testTag("enter_exam_button"),
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryPurple
                                ),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "ورود به آزمون",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontFamily = IranSansFontFamily
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Login,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
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

@Composable
private fun SectionTitleWithDot(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(PurpleDotColor, CircleShape)
        )
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextMainColor,
            fontFamily = IranSansFontFamily
        )
    }
}

@Composable
private fun ExamIllustrationBadge() {
    Box(
        modifier = Modifier
            .size(90.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFFEDE9FE),
                        Color(0xFFF5F3FF)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Decorative illustration composition
        Box(
            modifier = Modifier
                .size(62.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(1.5.dp, PrimaryPurple.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = null,
                        tint = PrimaryPurple,
                        modifier = Modifier.size(12.dp)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .background(Color(0xFFE2E8F0), RoundedCornerShape(2.dp))
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = null,
                        tint = PrimaryPurple,
                        modifier = Modifier.size(12.dp)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .background(Color(0xFFE2E8F0), RoundedCornerShape(2.dp))
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = null,
                        tint = PrimaryPurple,
                        modifier = Modifier.size(12.dp)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .background(Color(0xFFE2E8F0), RoundedCornerShape(2.dp))
                    )
                }
            }
        }

        // Mini Clock Badge in Corner
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 4.dp, y = 4.dp)
                .size(28.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(2.dp, PrimaryPurple, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.AccessTime,
                contentDescription = null,
                tint = PrimaryPurple,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun ExamSpecRow(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Label on top, Value below
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = TextSecondaryColor,
                fontFamily = IranSansFontFamily
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextMainColor,
                fontFamily = IranSansFontFamily
            )
        }

        // Purple Icon Badge
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(LightPurpleBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryPurple,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
