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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step1ExamStructureScreen(
    examType: String,
    onExamTypeChange: (String) -> Unit,
    grade: String,
    onGradeChange: (String) -> Unit,
    field: String,
    onFieldChange: (String) -> Unit,
    selectedBooks: List<SelectedExamBook>,
    onAddBook: (SelectedExamBook) -> Unit,
    onRemoveBook: (String) -> Unit,
    questionSource: String,
    onQuestionSourceChange: (String) -> Unit,
    onNextStep: () -> Unit,
    onCancel: () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val colors = LocalShetabColors.current
        var showInlineAddBook by remember { mutableStateOf(false) }

        val grades = listOf("دهم", "یازدهم", "دوازدهم")
        val fields = listOf("ریاضی فیزیک", "علوم تجربی", "علوم انسانی", "فنی و حرفه‌ای")

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // ==========================================
            // 1. SECTION: نوع آزمون (Exam Type) - Compact Height & Smaller Text
            // ==========================================
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
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Header (RTL: Icon on right, Text on left)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Description,
                            contentDescription = null,
                            tint = colors.primaryText.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "نوع آزمون",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primaryText,
                            fontFamily = IranSansFontFamily
                        )
                    }

                    // Two Compact Toggle Buttons (Right: تستی / Left: تشریحی) - Height 40dp
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // آزمون تستی (Right card in RTL)
                        val isMultiple = examType == "تستی"
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isMultiple) Color(0xFFF3E8FF) else Color.White)
                                .border(
                                    width = if (isMultiple) 1.2.dp else 1.dp,
                                    color = if (isMultiple) Color(0xFF7C3AED) else colors.primaryText.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onExamTypeChange("تستی") }
                                .testTag("exam_type_multiple_choice"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.FormatListBulleted,
                                    contentDescription = null,
                                    tint = if (isMultiple) Color(0xFF7C3AED) else colors.primaryText.copy(alpha = 0.6f),
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "آزمون تستی",
                                    fontSize = 12.sp,
                                    fontWeight = if (isMultiple) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isMultiple) Color(0xFF7C3AED) else colors.primaryText,
                                    fontFamily = IranSansFontFamily
                                )
                            }
                        }

                        // آزمون تشریحی (Left card in RTL)
                        val isDescriptive = examType == "تشریحی"
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDescriptive) Color(0xFFF3E8FF) else Color.White)
                                .border(
                                    width = if (isDescriptive) 1.2.dp else 1.dp,
                                    color = if (isDescriptive) Color(0xFF7C3AED) else colors.primaryText.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onExamTypeChange("تشریحی") }
                                .testTag("exam_type_descriptive"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = null,
                                    tint = if (isDescriptive) Color(0xFF7C3AED) else colors.primaryText.copy(alpha = 0.6f),
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "آزمون تشریحی",
                                    fontSize = 12.sp,
                                    fontWeight = if (isDescriptive) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isDescriptive) Color(0xFF7C3AED) else colors.primaryText,
                                    fontFamily = IranSansFontFamily
                                )
                            }
                        }
                    }

                    // Subtitle / helper note (Smaller)
                    Text(
                        text = "می‌توانید نوع آزمون را در هر زمان تغییر دهید.",
                        fontSize = 10.sp,
                        color = colors.secondaryText.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        fontFamily = IranSansFontFamily
                    )
                }
            }

            // ==========================================
            // 2. SECTION: منبع سوالات (Question Source) - Moved below Exam Type
            // ==========================================
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
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Header (RTL)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Layers,
                            contentDescription = null,
                            tint = colors.primaryText.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "منبع سوالات",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primaryText,
                            fontFamily = IranSansFontFamily
                        )
                    }

                    // 3 Options: Right (تألیفی) / Center (سوالات کنکور) / Left (سوالات نهایی)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Option 1: تألیفی (Right in RTL)
                        val isAuthor = questionSource == "تألیفی"
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isAuthor) Color(0xFFF3E8FF) else Color.White)
                                .border(
                                    width = if (isAuthor) 1.2.dp else 1.dp,
                                    color = if (isAuthor) Color(0xFF7C3AED) else colors.primaryText.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onQuestionSourceChange("تألیفی") }
                                .testTag("source_author"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = null,
                                    tint = if (isAuthor) Color(0xFF7C3AED) else colors.primaryText.copy(alpha = 0.6f),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "تألیفی",
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isAuthor) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isAuthor) Color(0xFF7C3AED) else colors.primaryText,
                                    fontFamily = IranSansFontFamily
                                )
                            }
                        }

                        // Option 2: سوالات کنکور (Center in RTL)
                        val isKonkur = questionSource == "سوالات کنکور"
                        Box(
                            modifier = Modifier
                                .weight(1.2f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isKonkur) Color(0xFFF3E8FF) else Color.White)
                                .border(
                                    width = if (isKonkur) 1.2.dp else 1.dp,
                                    color = if (isKonkur) Color(0xFF7C3AED) else colors.primaryText.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onQuestionSourceChange("سوالات کنکور") }
                                .testTag("source_konkur"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.AccountBalance,
                                    contentDescription = null,
                                    tint = if (isKonkur) Color(0xFF7C3AED) else colors.primaryText.copy(alpha = 0.6f),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "سوالات کنکور",
                                    fontSize = 11.sp,
                                    fontWeight = if (isKonkur) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isKonkur) Color(0xFF7C3AED) else colors.primaryText,
                                    fontFamily = IranSansFontFamily
                                )
                            }
                        }

                        // Option 3: سوالات نهایی (Left in RTL)
                        val isFinalDisabled = examType == "تستی"
                        val isFinalSelected = questionSource == "سوالات نهایی" && !isFinalDisabled
                        Box(
                            modifier = Modifier
                                .weight(1.2f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isFinalDisabled) Color(0xFFF3F4F6) else if (isFinalSelected) Color(0xFFF3E8FF) else Color.White)
                                .border(
                                    width = 1.dp,
                                    color = if (isFinalSelected) Color(0xFF7C3AED) else colors.primaryText.copy(alpha = 0.08f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable(enabled = !isFinalDisabled) { onQuestionSourceChange("سوالات نهایی") }
                                .testTag("source_final"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Article,
                                    contentDescription = null,
                                    tint = if (isFinalDisabled) colors.secondaryText.copy(alpha = 0.35f) else colors.primaryText.copy(alpha = 0.6f),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "سوالات نهایی",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = if (isFinalDisabled) colors.secondaryText.copy(alpha = 0.4f) else if (isFinalSelected) Color(0xFF7C3AED) else colors.primaryText,
                                    fontFamily = IranSansFontFamily
                                )
                            }
                        }
                    }

                    if (examType == "تستی") {
                        Text(
                            text = "منبع نهایی فقط برای آزمون تشریحی فعال است.",
                            fontSize = 10.sp,
                            color = colors.secondaryText.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            fontFamily = IranSansFontFamily
                        )
                    }
                }
            }

            // ==========================================
            // 3. SECTION: پایه و رشته (Grade & Field) - Compact
            // ==========================================
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
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Header (RTL)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.School,
                            contentDescription = null,
                            tint = colors.primaryText.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "پایه و رشته",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primaryText,
                            fontFamily = IranSansFontFamily
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // پایه Dropdown (Right side in RTL)
                        ExamSelectDropdown(
                            label = "پایه",
                            selected = grade,
                            options = grades,
                            onSelect = onGradeChange,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("grade_dropdown")
                        )

                        // رشته Dropdown (Left side in RTL)
                        ExamSelectDropdown(
                            label = "رشته",
                            selected = field,
                            options = fields,
                            onSelect = onFieldChange,
                            modifier = Modifier
                                .weight(1.3f)
                                .testTag("field_dropdown")
                        )
                    }
                }
            }

            // ==========================================
            // 4. SECTION: کتاب‌ها و محدوده آزمون (Books & Scope)
            // ==========================================
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
                    // Header (RTL)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.MenuBook,
                            contentDescription = null,
                            tint = colors.primaryText.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "کتاب‌ها و محدوده آزمون",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primaryText,
                            fontFamily = IranSansFontFamily
                        )
                    }

                    // List of Selected Book Cards (Strict RTL order)
                    selectedBooks.forEach { book ->
                        SelectedBookCardItem(
                            book = book,
                            onDelete = { onRemoveBook(book.id) }
                        )
                    }

                    // Inline Expandable Box for Adding a Book (No modal dialog!)
                    if (showInlineAddBook) {
                        InlineAddBookBox(
                            grade = grade,
                            field = field,
                            onCancel = { showInlineAddBook = false },
                            onConfirmAdd = { newBook ->
                                onAddBook(newBook)
                                showInlineAddBook = false
                            }
                        )
                    } else {
                        // "+ افزودن کتاب دیگر" Dashed Button
                        val purpleColor = Color(0xFF7C3AED)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .drawBehind {
                                    drawRoundRect(
                                        color = purpleColor,
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                                            width = 1.2.dp.toPx(),
                                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)
                                        ),
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx(), 12.dp.toPx())
                                    )
                                }
                                .clickable { showInlineAddBook = true }
                                .testTag("add_book_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = purpleColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "افزودن کتاب دیگر",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = purpleColor,
                                    fontFamily = IranSansFontFamily
                                )
                            }
                        }
                    }

                    // Helper Note with Info icon (RTL)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = colors.secondaryText.copy(alpha = 0.6f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "می‌توانید از یک یا چند کتاب برای ساخت آزمون استفاده کنید.",
                            fontSize = 10.sp,
                            color = colors.secondaryText.copy(alpha = 0.75f),
                            fontFamily = IranSansFontFamily
                        )
                    }
                }
            }

            // ==========================================
            // 5. BOTTOM ACTIONS: ادامه به مرحله بعد / انصراف
            // ==========================================
            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = onNextStep,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("step1_next_button"),
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

            Button(
                onClick = onCancel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("step1_cancel_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF4444),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "انصراف",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = IranSansFontFamily
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
fun SelectedBookCardItem(
    book: SelectedExamBook,
    onDelete: () -> Unit
) {
    val colors = LocalShetabColors.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Right Side in RTL: Book Cover + Details
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                // Book Thumbnail with Gradient / Cover Art (Rightmost in RTL)
                Box(
                    modifier = Modifier
                        .size(width = 38.dp, height = 48.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = book.gradientColors
                            )
                        )
                        .border(1.dp, Color.Black.copy(alpha = 0.08f), RoundedCornerShape(6.dp)),
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
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = book.bookName.take(6),
                            fontSize = 7.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontFamily = IranSansFontFamily
                        )
                    }
                }

                // Text Details (Title, Chapter, Topic tags)
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = book.bookName,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primaryText,
                        fontFamily = IranSansFontFamily
                    )
                    Text(
                        text = book.chapter,
                        fontSize = 10.5.sp,
                        color = colors.secondaryText.copy(alpha = 0.8f),
                        fontFamily = IranSansFontFamily
                    )

                    // Topic Tags
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        book.topics.forEach { topic ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFF3E8FF)
                            ) {
                                Text(
                                    text = topic,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF7C3AED),
                                    fontFamily = IranSansFontFamily,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Left Side in RTL: Delete button (✕)
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, colors.primaryText.copy(alpha = 0.1f), CircleShape)
                    .clickable { onDelete() }
                    .testTag("delete_book_${book.id}"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "حذف کتاب",
                    tint = colors.secondaryText.copy(alpha = 0.7f),
                    modifier = Modifier.size(13.dp)
                )
            }
        }
    }
}

// ====================================================
// Inline Expandable Add Book Box (Inside the page, No modal)
// ====================================================
@Composable
fun InlineAddBookBox(
    grade: String,
    field: String,
    onCancel: () -> Unit,
    onConfirmAdd: (SelectedExamBook) -> Unit
) {
    val catalog = remember(grade, field) {
        listOf(
            CatalogBook(
                id = "physic",
                name = "فیزیک $grade",
                gradientColors = listOf(Color(0xFF2563EB), Color(0xFF1D4ED8)),
                chapters = listOf(
                    CatalogChapter("ch1", "فصل ۱: فیزیک و اندازه‌گیری", listOf(CatalogTopic("t1", "کمیت‌های فیزیکی"), CatalogTopic("t2", "چگالی"), CatalogTopic("t3", "دقت اندازه‌گیری"))),
                    CatalogChapter("ch2", "فصل ۲: کار، انرژی و توان", listOf(CatalogTopic("t4", "انرژی جنبشی"), CatalogTopic("t5", "کار و قضیه کار-انرژی"), CatalogTopic("t6", "پایستگی انرژی"))),
                    CatalogChapter("ch3", "فصل ۳: ویژگی‌های ماده و فشار", listOf(CatalogTopic("t7", "فشار در شاره‌ها"), CatalogTopic("t8", "اصل ارشمیدس"), CatalogTopic("t9", "اصل برنولی")))
                )
            ),
            CatalogBook(
                id = "chemistry",
                name = "شیمی $grade",
                gradientColors = listOf(Color(0xFFEA580C), Color(0xFFC2410C)),
                chapters = listOf(
                    CatalogChapter("ch1", "فصل ۱: کیهان زادگاه هستی", listOf(CatalogTopic("t1", "ساختار اتم"), CatalogTopic("t2", "جدول دوره‌ای"), CatalogTopic("t3", "جرم اتمی"))),
                    CatalogChapter("ch2", "فصل ۲: ردپای گازها در زندگی", listOf(CatalogTopic("t4", "ساختار هواکره"), CatalogTopic("t5", "شیمی سبز"), CatalogTopic("t6", "قانون گازها")))
                )
            ),
            CatalogBook(
                id = "biology",
                name = "زیست‌شناسی $grade",
                gradientColors = listOf(Color(0xFF059669), Color(0xFF047857)),
                chapters = listOf(
                    CatalogChapter("ch1", "فصل ۱: دنیای زنده", listOf(CatalogTopic("t1", "یاخته و بافت"), CatalogTopic("t2", "نگرش به دنیای زنده"))),
                    CatalogChapter("ch2", "فصل ۲: گوارش و جذب مواد", listOf(CatalogTopic("t3", "ساختار دستگاه گوارش"), CatalogTopic("t4", "جذب مواد")))
                )
            ),
            CatalogBook(
                id = "geometry",
                name = "هندسه $grade",
                gradientColors = listOf(Color(0xFF7C3AED), Color(0xFF6D28D9)),
                chapters = listOf(
                    CatalogChapter("ch1", "فصل ۱: ترسیم‌های هندسی", listOf(CatalogTopic("t1", "استدلال هندسی"), CatalogTopic("t2", "قضیه تالس")))
                )
            ),
            CatalogBook(
                id = "farsi",
                name = "فارسی $grade",
                gradientColors = listOf(Color(0xFFD97706), Color(0xFFB45309)),
                chapters = listOf(
                    CatalogChapter("ch1", "فصل ۱: ادبیات تعلیمی", listOf(CatalogTopic("t1", "آرایه‌های ادبی"), CatalogTopic("t2", "معنی و مفهوم")))
                )
            )
        )
    }

    var selectedBookIndex by remember { mutableIntStateOf(0) }
    var selectedChapterIndex by remember { mutableIntStateOf(0) }
    var selectedTopicIds by remember { mutableStateOf(setOf<String>()) }
    var isChapterMenuOpen by remember { mutableStateOf(false) }

    val currentBook = catalog.getOrNull(selectedBookIndex) ?: catalog.first()
    val chapters = currentBook.chapters
    val currentChapter = chapters.getOrNull(selectedChapterIndex) ?: chapters.firstOrNull()
    val topics = currentChapter?.topics ?: emptyList()

    // Initialize with first topic when chapter changes if empty
    LaunchedEffect(currentChapter) {
        if (selectedTopicIds.isEmpty() && topics.isNotEmpty()) {
            selectedTopicIds = setOf(topics.first().id)
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFFAF5FF),
        border = BorderStroke(1.2.dp, Color(0xFF7C3AED).copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            // Header: Title and Cancel button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "انتخاب کتاب، فصل و مبحث",
                    color = Color(0xFF7C3AED),
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.5.sp
                )
                IconButton(
                    onClick = onCancel,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "انصراف",
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // 1. Book Selector Horizontal Row
            Text(
                text = "کتاب:",
                color = Color(0xFF1E1B4B),
                fontFamily = IranSansFontFamily,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(catalog.size) { index ->
                    val book = catalog[index]
                    val isSelected = selectedBookIndex == index
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                selectedBookIndex = index
                                selectedChapterIndex = 0
                                selectedTopicIds = emptySet()
                                isChapterMenuOpen = false
                            },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) Color(0xFF7C3AED) else Color.White,
                        border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Text(
                            text = book.name,
                            color = if (isSelected) Color.White else Color(0xFF1E1B4B),
                            fontFamily = IranSansFontFamily,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            // 2. Chapter Selector
            Text(
                text = "فصل:",
                color = Color(0xFF1E1B4B),
                fontFamily = IranSansFontFamily,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { isChapterMenuOpen = !isChapterMenuOpen },
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Layers,
                                contentDescription = null,
                                tint = Color(0xFF7C3AED),
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = currentChapter?.name ?: "انتخاب فصل...",
                                color = Color(0xFF1E1B4B),
                                fontFamily = IranSansFontFamily,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Icon(
                            imageVector = Icons.Outlined.ArrowDropDown,
                            contentDescription = null,
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                if (isChapterMenuOpen) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 3.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        shadowElevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                        ) {
                            chapters.forEachIndexed { idx, ch ->
                                val isSelected = idx == selectedChapterIndex
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable {
                                            selectedChapterIndex = idx
                                            selectedTopicIds = emptySet()
                                            isChapterMenuOpen = false
                                        },
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isSelected) Color(0xFFF3E8FF) else Color.Transparent
                                ) {
                                    Text(
                                        text = ch.name,
                                        fontFamily = IranSansFontFamily,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color(0xFF7C3AED) else Color(0xFF1E1B4B),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Topics (Multi-select)
            Text(
                text = "مباحث:",
                color = Color(0xFF1E1B4B),
                fontFamily = IranSansFontFamily,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            if (topics.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(topics, key = { it.id }) { topic ->
                        val isSelected = selectedTopicIds.contains(topic.id)
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    selectedTopicIds = if (isSelected) {
                                        selectedTopicIds - topic.id
                                    } else {
                                        selectedTopicIds + topic.id
                                    }
                                },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFFF3E8FF) else Color.White,
                            border = BorderStroke(1.dp, if (isSelected) Color(0xFF7C3AED) else Color(0xFFE2E8F0))
                        ) {
                            Text(
                                text = topic.name,
                                color = if (isSelected) Color(0xFF7C3AED) else Color(0xFF1E1B4B),
                                fontFamily = IranSansFontFamily,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 10.5.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }

            // 4. Confirm Button
            Button(
                onClick = {
                    val chosenTopics = if (selectedTopicIds.isNotEmpty()) {
                        topics.filter { selectedTopicIds.contains(it.id) }.map { it.name }
                    } else {
                        topics.take(2).map { it.name }
                    }

                    val newSelectedBook = SelectedExamBook(
                        id = "${currentBook.id}_${System.currentTimeMillis()}",
                        bookName = currentBook.name,
                        chapter = currentChapter?.name ?: "فصل اول",
                        topics = chosenTopics.ifEmpty { listOf("مباحث فصل") },
                        gradientColors = currentBook.gradientColors
                    )
                    onConfirmAdd(newSelectedBook)
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .testTag("confirm_add_book_button")
            ) {
                Text(
                    text = "افزودن کتاب به آزمون",
                    color = Color.White,
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

// Dropdown Helper Component
@Composable
fun ExamSelectDropdown(
    label: String,
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalShetabColors.current
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = label,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Medium,
                color = colors.secondaryText,
                fontFamily = IranSansFontFamily
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { expanded = !expanded },
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(1.dp, colors.primaryText.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = selected,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primaryText,
                        fontFamily = IranSansFontFamily,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        imageVector = Icons.Outlined.ArrowDropDown,
                        contentDescription = null,
                        tint = colors.primaryText.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            fontSize = 12.sp,
                            fontFamily = IranSansFontFamily,
                            color = if (option == selected) Color(0xFF7C3AED) else colors.primaryText
                        )
                    },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
