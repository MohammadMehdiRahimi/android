package com.example.ui.features.exams

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.core.toPersianNumber
import com.example.ui.theme.IranSansFontFamily
import com.example.ui.theme.LocalShetabColors

data class BookQuestionConfig(
    val bookId: String,
    val bookName: String,
    val chapter: String,
    val topics: List<String>,
    val gradientColors: List<Color>,
    var easyCount: Int = 4,
    var mediumCount: Int = 5,
    var hardCount: Int = 3,
    var veryHardCount: Int = 2
) {
    val totalCount: Int get() = easyCount + mediumCount + hardCount + veryHardCount
}

@Composable
fun Step2QuestionSettingsScreen(
    examType: String,
    grade: String,
    field: String,
    selectedBooks: List<SelectedExamBook>,
    questionConfigs: Map<String, BookQuestionConfig>,
    onConfigChange: (String, BookQuestionConfig) -> Unit,
    onRemoveBook: (String) -> Unit,
    questionSource: String,
    hasNegativeScore: Boolean,
    onNegativeScoreChange: (Boolean) -> Unit,
    randomQuestionOrder: Boolean,
    onRandomQuestionOrderChange: (Boolean) -> Unit,
    onNextStep: () -> Unit,
    onPrevStep: () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val colors = LocalShetabColors.current

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ====================================================
            // 1. TOP SUMMARY CARD (2 Distinct Persian Design Rows)
            // Row 1: نوع آزمون، پایه، رشته
            // Row 2: تعداد کتاب، منبع سوال
            // ====================================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // --- ROW 1: نوع آزمون + پایه + رشته ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Col 1 (Right): نوع آزمون
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF3E8FF),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(vertical = 7.dp, horizontal = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.FormatListBulleted,
                                    contentDescription = null,
                                    tint = Color(0xFF7C3AED),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (examType == "تستی") "آزمون تستی" else "آزمون تشریحی",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF7C3AED),
                                    fontFamily = IranSansFontFamily
                                )
                            }
                        }

                        // Col 2 (Center): پایه
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF9FAFB),
                            border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                            modifier = Modifier.weight(0.9f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(vertical = 7.dp, horizontal = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.School,
                                    contentDescription = null,
                                    tint = colors.primaryText.copy(alpha = 0.7f),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = grade,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.primaryText,
                                    fontFamily = IranSansFontFamily
                                )
                            }
                        }

                        // Col 3 (Left): رشته
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF9FAFB),
                            border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                            modifier = Modifier.weight(1.3f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(vertical = 7.dp, horizontal = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Science,
                                    contentDescription = null,
                                    tint = colors.primaryText.copy(alpha = 0.7f),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = field,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.primaryText,
                                    fontFamily = IranSansFontFamily,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                    // --- ROW 2: تعداد کتاب + منبع سوال ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Col 1 (Right): تعداد کتاب‌ها
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF9FAFB),
                            border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(vertical = 7.dp, horizontal = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.MenuBook,
                                    contentDescription = null,
                                    tint = Color(0xFF6366F1),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "تعداد کتاب: ${selectedBooks.size.toString().toPersianNumber()}",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.primaryText,
                                    fontFamily = IranSansFontFamily
                                )
                            }
                        }

                        // Col 2 (Left): منبع سوال
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF9FAFB),
                            border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(vertical = 7.dp, horizontal = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Layers,
                                    contentDescription = null,
                                    tint = Color(0xFF059669),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "منبع: $questionSource",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.primaryText,
                                    fontFamily = IranSansFontFamily,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // ====================================================
            // 2. BOOK QUESTION CONFIGURATION CARDS
            // ====================================================
            selectedBooks.forEach { book ->
                val currentConfig = questionConfigs[book.id] ?: BookQuestionConfig(
                    bookId = book.id,
                    bookName = book.bookName,
                    chapter = book.chapter,
                    topics = book.topics,
                    gradientColors = book.gradientColors
                )

                BookQuestionCard(
                    config = currentConfig,
                    onConfigChange = { updated -> onConfigChange(book.id, updated) },
                    onDelete = { onRemoveBook(book.id) }
                )
            }

            // ====================================================
            // 3. EXAM OPTIONS CARD (تنظیمات آزمون تستی)
            // ====================================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = null,
                            tint = colors.primaryText.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "تنظیمات آزمون تستی",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primaryText,
                            fontFamily = IranSansFontFamily
                        )
                    }

                    // Two Settings side by side with vertical divider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Right Setting: نمره منفی
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Switch(
                                checked = hasNegativeScore,
                                onCheckedChange = onNegativeScoreChange,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF6366F1),
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color(0xFFE5E7EB)
                                ),
                                modifier = Modifier.testTag("negative_score_switch")
                            )

                            Column(
                                horizontalAlignment = Alignment.End,
                                modifier = Modifier.padding(start = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "نمره منفی",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.primaryText,
                                        fontFamily = IranSansFontFamily
                                    )
                                    Icon(
                                        imageVector = Icons.Outlined.RemoveCircleOutline,
                                        contentDescription = null,
                                        tint = colors.secondaryText,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Text(
                                    text = "هر ۴ پاسخ غلط = ۱ پاسخ صحیح منفی",
                                    fontSize = 9.5.sp,
                                    color = colors.secondaryText.copy(alpha = 0.8f),
                                    fontFamily = IranSansFontFamily
                                )
                            }
                        }

                        VerticalDivider(
                            modifier = Modifier
                                .height(40.dp)
                                .padding(horizontal = 8.dp),
                            color = Color(0xFFE5E7EB)
                        )

                        // Left Setting: چینش تصادفی سوالات
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Switch(
                                checked = randomQuestionOrder,
                                onCheckedChange = onRandomQuestionOrderChange,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF6366F1),
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color(0xFFE5E7EB)
                                ),
                                modifier = Modifier.testTag("random_order_switch")
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(start = 6.dp)
                            ) {
                                Text(
                                    text = "چینش تصادفی سوالات",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.primaryText,
                                    fontFamily = IranSansFontFamily
                                )
                                Icon(
                                    imageVector = Icons.Outlined.Shuffle,
                                    contentDescription = null,
                                    tint = colors.secondaryText,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            // ====================================================
            // 4. BOTTOM ACTION BUTTONS
            // ====================================================
            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = onNextStep,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("step2_next_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "ادامه به مرحله بعد",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = IranSansFontFamily
                )
            }

            TextButton(
                onClick = onPrevStep,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .testTag("step2_prev_button")
            ) {
                Text(
                    text = "بازگشت",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6366F1),
                    fontFamily = IranSansFontFamily
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BookQuestionCard(
    config: BookQuestionConfig,
    onConfigChange: (BookQuestionConfig) -> Unit,
    onDelete: () -> Unit
) {
    val colors = LocalShetabColors.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header: Delete action (Left) and Book Info (Right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Delete button on Left in RTL
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onDelete() }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                        .testTag("delete_book_card_${config.bookId}")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "حذف",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "حذف",
                        color = Color(0xFFEF4444),
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp
                    )
                }

                // Book Details on Right in RTL
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = config.bookName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primaryText,
                            fontFamily = IranSansFontFamily
                        )
                        Text(
                            text = config.chapter,
                            fontSize = 11.5.sp,
                            color = colors.secondaryText.copy(alpha = 0.8f),
                            fontFamily = IranSansFontFamily
                        )
                    }

                    // Book Cover Artwork
                    Box(
                        modifier = Modifier
                            .size(width = 44.dp, height = 56.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = config.gradientColors
                                )
                            )
                            .border(1.dp, Color.Black.copy(alpha = 0.08f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.MenuBook,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = config.bookName.take(6),
                                fontSize = 8.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontFamily = IranSansFontFamily
                            )
                        }
                    }
                }
            }

            // Selected Topics Row (FlowRow / LazyRow)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    config.topics.forEach { topic ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF3E8FF)
                        ) {
                            Text(
                                text = topic,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF7C3AED),
                                fontFamily = IranSansFontFamily,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "موضوعات انتخابی:",
                    fontSize = 10.5.sp,
                    color = colors.secondaryText,
                    fontFamily = IranSansFontFamily
                )
            }

            // 4 Difficulty Levels Box (Right to Left: آسان, متوسط, دشوار, خیلی دشوار)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFAFAFC),
                border = BorderStroke(1.dp, Color(0xFFE5E7EB))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. آسان (Rightmost in RTL)
                    DifficultyStepper(
                        label = "آسان",
                        count = config.easyCount,
                        onIncrement = { onConfigChange(config.copy(easyCount = config.easyCount + 1)) },
                        onDecrement = { if (config.easyCount > 0) onConfigChange(config.copy(easyCount = config.easyCount - 1)) }
                    )

                    VerticalDivider(modifier = Modifier.height(36.dp), color = Color(0xFFE5E7EB))

                    // 2. متوسط
                    DifficultyStepper(
                        label = "متوسط",
                        count = config.mediumCount,
                        onIncrement = { onConfigChange(config.copy(mediumCount = config.mediumCount + 1)) },
                        onDecrement = { if (config.mediumCount > 0) onConfigChange(config.copy(mediumCount = config.mediumCount - 1)) }
                    )

                    VerticalDivider(modifier = Modifier.height(36.dp), color = Color(0xFFE5E7EB))

                    // 3. دشوار
                    DifficultyStepper(
                        label = "دشوار",
                        count = config.hardCount,
                        onIncrement = { onConfigChange(config.copy(hardCount = config.hardCount + 1)) },
                        onDecrement = { if (config.hardCount > 0) onConfigChange(config.copy(hardCount = config.hardCount - 1)) }
                    )

                    VerticalDivider(modifier = Modifier.height(36.dp), color = Color(0xFFE5E7EB))

                    // 4. خیلی دشوار (Leftmost in RTL)
                    DifficultyStepper(
                        label = "خیلی دشوار",
                        count = config.veryHardCount,
                        onIncrement = { onConfigChange(config.copy(veryHardCount = config.veryHardCount + 1)) },
                        onDecrement = { if (config.veryHardCount > 0) onConfigChange(config.copy(veryHardCount = config.veryHardCount - 1)) }
                    )
                }
            }

            // Total Questions of this section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "جمع سوالات این بخش: ",
                    fontSize = 11.sp,
                    color = colors.secondaryText,
                    fontFamily = IranSansFontFamily
                )
                Text(
                    text = "${config.totalCount.toString().toPersianNumber()} سوال",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6366F1),
                    fontFamily = IranSansFontFamily
                )
            }
        }
    }
}

@Composable
fun DifficultyStepper(
    label: String,
    count: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    val colors = LocalShetabColors.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.5.sp,
            color = colors.secondaryText,
            fontFamily = IranSansFontFamily
        )

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                // + button (Right side in RTL)
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onIncrement() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+",
                        color = Color(0xFF6B7280),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = count.toString().toPersianNumber(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primaryText,
                    fontFamily = IranSansFontFamily
                )

                // - button (Left side in RTL)
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onDecrement() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "−",
                        color = if (count > 0) Color(0xFF6B7280) else Color(0xFFD1D5DB),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
