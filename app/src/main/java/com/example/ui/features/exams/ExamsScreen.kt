package com.example.ui.features.exams

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.R
import com.example.ui.core.components.AppBackground
import com.example.ui.core.components.shimmerEffect
import com.example.ui.core.toPersianNumber
import com.example.ui.theme.IranSansFontFamily
import com.example.ui.theme.LocalShetabColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamsScreen(
    navController: NavController,
    viewModel: ExamsViewModel = viewModel()
) {
    val colors = LocalShetabColors.current
    val uiState by viewModel.uiState.collectAsState()
    var selectedExamForDetails by remember { mutableStateOf<ExamListItem?>(null) }
    var examIdInput by remember { mutableStateOf("") }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .testTag("exams_screen")
        ) {
            AppBackground(customBgColor = Color(0xFFFAFAFA))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // === Top Header ===
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Title + Clipboard Icon (Right in RTL)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "آزمون‌های من",
                            fontSize = 21.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = colors.primaryText,
                            fontFamily = IranSansFontFamily
                        )

                        Icon(
                            painter = painterResource(id = R.drawable.ic_clipboard_check),
                            contentDescription = null,
                            tint = Color(0xFF7C3AED),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Notification Bell (Left in RTL)
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, colors.primaryText.copy(alpha = 0.08f), CircleShape)
                            .clickable { navController.navigate("notifications") }
                            .testTag("notification_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "اعلان‌ها",
                            tint = colors.primaryText.copy(alpha = 0.75f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // === Exam ID Search Box ===
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                        .testTag("exam_id_search_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.primaryText.copy(alpha = 0.08f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_clipboard_check),
                            contentDescription = null,
                            tint = Color(0xFF7C3AED),
                            modifier = Modifier.size(22.dp)
                        )

                        OutlinedTextField(
                            value = examIdInput,
                            onValueChange = { examIdInput = it },
                            placeholder = {
                                Text(
                                    text = "ورود با شناسه آزمون (مثلاً: ۱۲۳)...",
                                    fontSize = 12.sp,
                                    color = colors.secondaryText.copy(alpha = 0.6f),
                                    fontFamily = IranSansFontFamily
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("exam_id_search_input"),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontSize = 13.sp,
                                color = colors.primaryText,
                                fontFamily = IranSansFontFamily,
                                fontWeight = FontWeight.Bold
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                cursorColor = Color(0xFF7C3AED),
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                imeAction = androidx.compose.ui.text.input.ImeAction.Go
                            ),
                            keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                                onGo = {
                                    val trimmed = examIdInput.trim()
                                    if (trimmed.isNotEmpty()) {
                                        navController.navigate("exam_details/${android.net.Uri.encode(trimmed)}")
                                    }
                                }
                            )
                        )

                        Button(
                            onClick = {
                                val trimmed = examIdInput.trim()
                                if (trimmed.isNotEmpty()) {
                                    navController.navigate("exam_details/${android.net.Uri.encode(trimmed)}")
                                }
                            },
                            modifier = Modifier
                                .height(40.dp)
                                .testTag("submit_exam_id_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                            contentPadding = PaddingValues(horizontal = 14.dp)
                        ) {
                            Text(
                                text = "ورود",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontFamily = IranSansFontFamily
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // === Filter Chips Row ===
                ExamFilterChipsRow(
                    selectedDate = uiState.selectedDate,
                    selectedSubject = uiState.selectedSubject,
                    selectedTopic = uiState.selectedTopic,
                    onOpenFilter = { viewModel.openFilterSheet(it) }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // === Total Count Label ===
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "تعداد کل: ",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = colors.secondaryText.copy(alpha = 0.8f),
                        fontFamily = IranSansFontFamily
                    )
                    if (uiState.isLoading) {
                        Box(
                            modifier = Modifier
                                .width(54.dp)
                                .height(16.dp)
                                .shimmerEffect(RoundedCornerShape(6.dp))
                        )
                    } else {
                        Text(
                            text = "${uiState.filteredExams.size.toString().toPersianNumber()} آزمون",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF7C3AED),
                            fontFamily = IranSansFontFamily,
                            modifier = Modifier.testTag("total_exams_count")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // === Exams LazyColumn List with Granular Skeleton Loading ===
                if (uiState.isLoading) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        userScrollEnabled = false
                    ) {
                        items(5) {
                            ExamItemCardSkeleton()
                        }
                    }
                } else if (uiState.filteredExams.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_clipboard_check),
                                contentDescription = null,
                                tint = colors.secondaryText.copy(alpha = 0.3f),
                                modifier = Modifier.size(56.dp)
                            )
                            Text(
                                text = "آزمونی با فیلترهای انتخابی یافت نشد",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.secondaryText,
                                fontFamily = IranSansFontFamily
                            )
                            OutlinedButton(
                                onClick = { viewModel.clearAllFilters() },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF7C3AED))
                            ) {
                                Text(
                                    text = "حذف همه فیلترها",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = IranSansFontFamily
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(
                            items = uiState.filteredExams,
                            key = { it.id }
                        ) { exam ->
                            ExamItemCard(
                                item = exam,
                                onClick = { selectedExamForDetails = exam }
                            )
                        }
                    }
                }
            }

            // === Floating Action Button (Bottom-Left in RTL: Alignment.BottomEnd) ===
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 20.dp, bottom = 20.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                FloatingActionButton(
                    onClick = { navController.navigate("build_exam") },
                    shape = CircleShape,
                    containerColor = Color(0xFF7C3AED),
                    contentColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                    modifier = Modifier
                        .size(56.dp)
                        .testTag("create_exam_fab")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "ساخت آزمون جدید",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // === Filter Modal Sheet ===
            if (uiState.activeFilterType != null) {
                ExamFilterModalSheet(
                    filterType = uiState.activeFilterType!!,
                    availableDates = uiState.availableDates,
                    availableSubjects = uiState.availableSubjects,
                    availableTopics = uiState.availableTopics,
                    selectedDate = uiState.selectedDate,
                    selectedSubject = uiState.selectedSubject,
                    selectedTopic = uiState.selectedTopic,
                    onSelectDate = { viewModel.selectDate(it) },
                    onSelectSubject = { viewModel.selectSubject(it) },
                    onSelectTopic = { viewModel.selectTopic(it) },
                    onDismiss = { viewModel.dismissFilterSheet() }
                )
            }

            // === Details Dialog ===
            if (selectedExamForDetails != null) {
                val exam = selectedExamForDetails!!
                AlertDialog(
                    onDismissRequest = { selectedExamForDetails = null },
                    confirmButton = {
                        Button(
                            onClick = { selectedExamForDetails = null },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("متوجه شدم", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = IranSansFontFamily)
                        }
                    },
                    title = {
                        Text(
                            text = "جزئیات ${exam.subject}",
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
                                text = "مبحث: ${exam.topic}",
                                fontSize = 13.sp,
                                color = colors.secondaryText,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth(),
                                fontFamily = IranSansFontFamily
                            )
                            Text(
                                text = "نوع آزمون: ${exam.examType.title}",
                                fontSize = 13.sp,
                                color = colors.secondaryText,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth(),
                                fontFamily = IranSansFontFamily
                            )
                            Text(
                                text = "تاریخ: ${exam.date.toPersianNumber()} (${exam.dayOfWeek})",
                                fontSize = 13.sp,
                                color = colors.secondaryText,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth(),
                                fontFamily = IranSansFontFamily
                            )
                            Text(
                                text = "تعداد تست: ${exam.questionCount.toString().toPersianNumber()} سوال",
                                fontSize = 13.sp,
                                color = colors.secondaryText,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth(),
                                fontFamily = IranSansFontFamily
                            )
                            Text(
                                text = "مدت زمان: ${exam.durationMinutes.toString().toPersianNumber()} دقیقه",
                                fontSize = 13.sp,
                                color = colors.secondaryText,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth(),
                                fontFamily = IranSansFontFamily
                            )
                            Text(
                                text = "نمره کسب شده: ${exam.score.toPersianNumber()}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF7C3AED),
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth(),
                                fontFamily = IranSansFontFamily
                            )
                        }
                    },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(24.dp)
                )
            }
        }
    }
}
