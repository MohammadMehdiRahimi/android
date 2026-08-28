package com.example.ui.features.exams

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.core.toPersianNumber
import com.example.ui.theme.IranSansFontFamily
import com.example.ui.theme.LocalShetabColors

/**
 * Step 3: خلاصه و ساخت آزمون
 * Pixel-perfect implementation matching the provided design specification (exam-create-3.png).
 */
@Composable
fun Step3ExamSummaryScreen(
    examType: String,
    grade: String,
    field: String,
    selectedBooks: List<SelectedExamBook>,
    questionConfigs: Map<String, BookQuestionConfig>,
    questionSource: String,
    hasNegativeScore: Boolean,
    randomQuestionOrder: Boolean,
    estimatedTimeMinutes: String,
    onStartExam: () -> Unit,
    onPrevStep: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalShetabColors.current

    // Aggregate statistics across all selected books
    val totalQuestions = questionConfigs.values.sumOf { it.totalCount }.coerceAtLeast(1)
    val totalEasy = questionConfigs.values.sumOf { it.easyCount }
    val totalMedium = questionConfigs.values.sumOf { it.mediumCount }
    val totalHard = questionConfigs.values.sumOf { it.hardCount }
    val totalVeryHard = questionConfigs.values.sumOf { it.veryHardCount }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ====================================================
        // 1. خلاصه کلی آزمون (General Summary 2x4 Grid Card)
        // ====================================================
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("step3_general_summary_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Section Title (خلاصه کلی آزمون)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Assignment,
                        contentDescription = null,
                        tint = colors.primaryText,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "خلاصه کلی آزمون",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primaryText,
                        fontFamily = IranSansFontFamily
                    )
                }

                // Grid Row 1: نوع آزمون, تعداد کتاب‌ها, منبع سوال
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Item 1: نوع آزمون
                    SummaryMetricBox(
                        label = "نوع آزمون",
                        value = if (examType == "تستی") "آزمون تستی" else "آزمون تشریحی",
                        icon = Icons.Outlined.FormatListBulleted,
                        iconTint = Color(0xFF7C3AED),
                        iconBg = Color(0xFFF3E8FF),
                        modifier = Modifier.weight(1f)
                    )

                    // Item 2: تعداد کتاب‌ها
                    SummaryMetricBox(
                        label = "تعداد کتاب‌ها",
                        value = "${selectedBooks.size.toString().toPersianNumber()} کتاب",
                        icon = Icons.Outlined.MenuBook,
                        iconTint = Color(0xFF7C3AED),
                        iconBg = Color(0xFFF3E8FF),
                        modifier = Modifier.weight(1f)
                    )

                    // Item 3: منبع سوال
                    SummaryMetricBox(
                        label = "منبع سوال",
                        value = questionSource,
                        icon = Icons.Outlined.AccountBalance,
                        iconTint = Color(0xFF7C3AED),
                        iconBg = Color(0xFFF3E8FF),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Grid Row 2: نمره منفی, چینش سوالات, مدت زمان تقریبی
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Item 4: نمره منفی
                    SummaryMetricBox(
                        label = "نمره منفی",
                        value = if (hasNegativeScore) "فعال" else "غیرفعال",
                        icon = Icons.Outlined.RemoveCircleOutline,
                        iconTint = Color(0xFF7C3AED),
                        iconBg = Color(0xFFF3E8FF),
                        modifier = Modifier.weight(1f)
                    )

                    // Item 5: چینش سوالات
                    SummaryMetricBox(
                        label = "چینش سوالات",
                        value = if (randomQuestionOrder) "تصادفی" else "ترتیبی",
                        icon = Icons.Outlined.Shuffle,
                        iconTint = Color(0xFF7C3AED),
                        iconBg = Color(0xFFF3E8FF),
                        modifier = Modifier.weight(1f)
                    )

                    // Item 6: مدت زمان تقریبی
                    SummaryMetricBox(
                        label = "مدت زمان تقریبی",
                        value = "${estimatedTimeMinutes.toPersianNumber()} دقیقه",
                        icon = Icons.Outlined.Schedule,
                        iconTint = Color(0xFF7C3AED),
                        iconBg = Color(0xFFF3E8FF),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // ====================================================
        // 2. آمار سوالات آزمون (Question Statistics Card)
        // ====================================================
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("step3_question_stats_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Section Title (آمار سوالات آزمون)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.BarChart,
                        contentDescription = null,
                        tint = colors.primaryText,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "آمار سوالات آزمون",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primaryText,
                        fontFamily = IranSansFontFamily
                    )
                }

                // Metrics Row (مجموع سوالات + آسان + متوسط + دشوار + خیلی دشوار)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Right: مجموع سوالات
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "مجموع سوالات",
                            fontSize = 11.sp,
                            color = colors.secondaryText,
                            fontFamily = IranSansFontFamily
                        )
                        Text(
                            text = "${totalQuestions.toString().toPersianNumber()} سوال",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF4F46E5),
                            fontFamily = IranSansFontFamily
                        )
                    }

                    // Vertical Divider
                    Box(
                        modifier = Modifier
                            .height(28.dp)
                            .width(1.dp)
                            .background(Color(0xFFF3F4F6))
                    )

                    // آسان
                    DifficultyStatItem(
                        label = "آسان",
                        count = totalEasy,
                        dotColor = Color(0xFF10B981) // Green
                    )

                    // متوسط
                    DifficultyStatItem(
                        label = "متوسط",
                        count = totalMedium,
                        dotColor = Color(0xFFF59E0B) // Yellow / Amber
                    )

                    // دشوار
                    DifficultyStatItem(
                        label = "دشوار",
                        count = totalHard,
                        dotColor = Color(0xFFF97316) // Orange
                    )

                    // خیلی دشوار
                    DifficultyStatItem(
                        label = "خیلی دشوار",
                        count = totalVeryHard,
                        dotColor = Color(0xFFEF4444) // Red
                    )
                }

                // Multi-segmented Colored Progress Bar
                MultiSegmentDifficultyBar(
                    easy = totalEasy,
                    medium = totalMedium,
                    hard = totalHard,
                    veryHard = totalVeryHard,
                    total = totalQuestions
                )
            }
        }

        // ====================================================
        // 3. بخش‌های آزمون (Exam Sections / Book Cards)
        // ====================================================
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("step3_exam_sections_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Section Title (بخش‌های آزمون)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MenuBook,
                        contentDescription = null,
                        tint = colors.primaryText,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "بخش‌های آزمون",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primaryText,
                        fontFamily = IranSansFontFamily
                    )
                }

                // Sub-cards for each selected book
                selectedBooks.forEach { book ->
                    val config = questionConfigs[book.id] ?: BookQuestionConfig(
                        bookId = book.id,
                        bookName = book.bookName,
                        chapter = book.chapter,
                        topics = book.topics,
                        gradientColors = book.gradientColors
                    )

                    ExamSectionBookItemCard(config = config)
                }
            }
        }

        // ====================================================
        // 4. نکات آزمون (Exam Tips Guidelines Card)
        // ====================================================
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("step3_exam_tips_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Section Title (نکات آزمون)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = Color(0xFF6366F1),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "نکات آزمون",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primaryText,
                        fontFamily = IranSansFontFamily
                    )
                }

                // Tip 1
                ExamTipRow(
                    icon = Icons.Outlined.FormatListBulleted,
                    text = "بعد از ساخت، آزمون در لیست آزمون‌های شما قرار می‌گیرد."
                )

                // Tip 2
                ExamTipRow(
                    icon = Icons.Outlined.Edit,
                    text = "می‌توانید بعداً آزمون را ویرایش یا دوباره استفاده کنید."
                )

                // Tip 3
                ExamTipRow(
                    icon = Icons.Outlined.Assessment,
                    text = "پاسخ‌نامه و گزارش بعد از اتمام آزمون فعال می‌شود."
                )
            }
        }

        // ====================================================
        // 5. BOTTOM ACTIONS: ساخت آزمون / مرحله قبل
        // ====================================================
        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = onStartExam,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("step3_build_exam_button"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ساخت آزمون",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    fontFamily = IranSansFontFamily
                )
            }
        }

        TextButton(
            onClick = onPrevStep,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .testTag("step3_prev_button")
        ) {
            Text(
                text = "مرحله قبل",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6366F1),
                fontFamily = IranSansFontFamily
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

/**
 * 2x4 Grid item cell for General Summary
 */
@Composable
private fun SummaryMetricBox(
    label: String,
    value: String,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    modifier: Modifier = Modifier
) {
    val colors = LocalShetabColors.current

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFF9FAFB),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, Color(0xFFE5E7EB)),
        modifier = modifier.height(68.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Content Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = colors.secondaryText.copy(alpha = 0.8f),
                    fontFamily = IranSansFontFamily,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primaryText,
                    fontFamily = IranSansFontFamily,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Icon Circle on left in cell
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}

/**
 * Single difficulty stat item with dot and count
 */
@Composable
private fun DifficultyStatItem(
    label: String,
    count: Int,
    dotColor: Color
) {
    val colors = LocalShetabColors.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Text(
                text = label,
                fontSize = 10.5.sp,
                color = colors.secondaryText,
                fontFamily = IranSansFontFamily
            )
        }

        Text(
            text = count.toString().toPersianNumber(),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = colors.primaryText,
            fontFamily = IranSansFontFamily
        )
    }
}

/**
 * Multi-segmented colored horizontal bar
 */
@Composable
private fun MultiSegmentDifficultyBar(
    easy: Int,
    medium: Int,
    hard: Int,
    veryHard: Int,
    total: Int
) {
    val safeTotal = if (total <= 0) 1f else total.toFloat()
    val easyWeight = if (easy > 0) easy.toFloat() / safeTotal else 0.001f
    val mediumWeight = if (medium > 0) medium.toFloat() / safeTotal else 0.001f
    val hardWeight = if (hard > 0) hard.toFloat() / safeTotal else 0.001f
    val veryHardWeight = if (veryHard > 0) veryHard.toFloat() / safeTotal else 0.001f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFFF3F4F6)),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Right side (Start in RTL): Easy (Green)
        Box(
            modifier = Modifier
                .weight(easyWeight)
                .fillMaxHeight()
                .clip(RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp))
                .background(Color(0xFF10B981))
        )
        // Medium (Yellow)
        Box(
            modifier = Modifier
                .weight(mediumWeight)
                .fillMaxHeight()
                .background(Color(0xFFF59E0B))
        )
        // Hard (Orange)
        Box(
            modifier = Modifier
                .weight(hardWeight)
                .fillMaxHeight()
                .background(Color(0xFFF97316))
        )
        // Very Hard (Red)
        Box(
            modifier = Modifier
                .weight(veryHardWeight)
                .fillMaxHeight()
                .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                .background(Color(0xFFEF4444))
        )
    }
}

/**
 * Single book section card in Step 3
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ExamSectionBookItemCard(
    config: BookQuestionConfig
) {
    val colors = LocalShetabColors.current

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFFAFAFC),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEF2F6)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Row 1: Book Info on Right (Start), Total Question Badge on Left (End)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Book Artwork + Titles
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    // Book Cover Artwork
                    Box(
                        modifier = Modifier
                            .size(width = 38.dp, height = 48.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Brush.verticalGradient(config.gradientColors)),
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
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = config.bookName.take(6),
                                fontSize = 6.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontFamily = IranSansFontFamily,
                                maxLines = 1
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = config.bookName,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primaryText,
                            fontFamily = IranSansFontFamily
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = config.chapter,
                            fontSize = 11.sp,
                            color = colors.secondaryText.copy(alpha = 0.8f),
                            fontFamily = IranSansFontFamily
                        )
                    }
                }

                // Total Questions Text (e.g. ۱۴ سوال in black with small font)
                Text(
                    text = "${config.totalCount.toString().toPersianNumber()} سوال",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.primaryText,
                    fontFamily = IranSansFontFamily
                )
            }

            // Row 2: Selected Topics Pill Tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "موضوعات:",
                    fontSize = 10.5.sp,
                    color = colors.secondaryText,
                    fontFamily = IranSansFontFamily
                )
                Spacer(modifier = Modifier.width(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    config.topics.forEach { topic ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFEDE9FE)
                        ) {
                            Text(
                                text = topic,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF6D28D9),
                                fontFamily = IranSansFontFamily,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 0.8.dp)

            // Row 3: Difficulty stats with colored dots
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                DifficultyInlineBadge(label = "آسان", count = config.easyCount, dotColor = Color(0xFF10B981))
                Text(text = "|", color = Color(0xFFE5E7EB), fontSize = 10.sp)
                DifficultyInlineBadge(label = "متوسط", count = config.mediumCount, dotColor = Color(0xFFF59E0B))
                Text(text = "|", color = Color(0xFFE5E7EB), fontSize = 10.sp)
                DifficultyInlineBadge(label = "دشوار", count = config.hardCount, dotColor = Color(0xFFF97316))
                Text(text = "|", color = Color(0xFFE5E7EB), fontSize = 10.sp)
                DifficultyInlineBadge(label = "خیلی دشوار", count = config.veryHardCount, dotColor = Color(0xFFEF4444))
            }
        }
    }
}

@Composable
private fun DifficultyInlineBadge(
    label: String,
    count: Int,
    dotColor: Color
) {
    val colors = LocalShetabColors.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Text(
            text = "$label ${count.toString().toPersianNumber()}",
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Medium,
            color = colors.primaryText,
            fontFamily = IranSansFontFamily
        )
    }
}

/**
 * Single exam tip row with dashed connecting line
 */
@Composable
private fun ExamTipRow(
    icon: ImageVector,
    text: String
) {
    val colors = LocalShetabColors.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Icon Circle (Left in RTL is Start, so Rightmost)
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(Color(0xFFF3E8FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF7C3AED),
                modifier = Modifier.size(15.dp)
            )
        }

        // Dashed connection guide line
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .drawBehind {
                    drawLine(
                        color = Color(0xFFE5E7EB),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                    )
                }
        )

        // Text with purple dot
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = text,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Normal,
                color = colors.secondaryText,
                fontFamily = IranSansFontFamily
            )
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF7C3AED))
            )
        }
    }
}
