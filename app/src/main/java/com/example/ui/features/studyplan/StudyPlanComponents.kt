package com.example.ui.features.studyplan

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.data.StudyTaskEntity
import com.example.ui.core.components.AppBackground
import com.example.ui.theme.LocalShetabColors
import com.example.ui.core.toPersianNumber
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Chat
import com.example.ui.screens.generate14DaySchedule
import com.example.ui.screens.saveScheduleToDatabase
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.draw.shadow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.BorderStroke

data class SubjectTheme(
    val bgPastel: Color,
    val accentColor: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String
)

fun getSubjectTheme(subject: String): SubjectTheme {
    val lower = subject.lowercase()
    return when {
        lower.contains("زیست") -> SubjectTheme(
            bgPastel = Color(0xFFF3E8FF),
            accentColor = Color(0xFF9333EA),
            icon = Icons.Outlined.Book,
            label = "زیست‌شناسی"
        )
        lower.contains("ریاضی") || lower.contains("حسابان") || lower.contains("هندسه") || lower.contains("آمار") -> SubjectTheme(
            bgPastel = Color(0xFFDCFCE7),
            accentColor = Color(0xFF16A34A),
            icon = Icons.Outlined.EditNote,
            label = "ریاضی"
        )
        lower.contains("زبان") || lower.contains("انگلیسی") -> SubjectTheme(
            bgPastel = Color(0xFFE0F2FE),
            accentColor = Color(0xFF0284C7),
            icon = Icons.Outlined.ChatBubbleOutline,
            label = "زبان انگلیسی"
        )
        lower.contains("فیزیک") -> SubjectTheme(
            bgPastel = Color(0xFFFEF3C7),
            accentColor = Color(0xFFD97706),
            icon = Icons.Outlined.Lightbulb,
            label = "فیزیک"
        )
        lower.contains("شیمی") -> SubjectTheme(
            bgPastel = Color(0xFFFFEDD5),
            accentColor = Color(0xFFEA580C),
            icon = Icons.Outlined.Science,
            label = "شیمی"
        )
        lower.contains("ادبیات") || lower.contains("فارسی") -> SubjectTheme(
            bgPastel = Color(0xFFCCFBF1),
            accentColor = Color(0xFF0D9488),
            icon = Icons.Outlined.AutoStories,
            label = "ادبیات"
        )
        else -> SubjectTheme(
            bgPastel = Color(0xFFF3E8FF),
            accentColor = Color(0xFF9333EA),
            icon = Icons.Outlined.Book,
            label = subject
        )
    }
}

@Composable
fun blendColor(base: Color, overlay: Color, alpha: Float): Color {
    return Color(
        red = base.red + (overlay.red - base.red) * alpha,
        green = base.green + (overlay.green - base.green) * alpha,
        blue = base.blue + (overlay.blue - base.blue) * alpha,
        alpha = 1.0f
    )
}

@Composable
fun TaskCard(
    task: StudyTaskEntity,
    colors: com.example.ui.theme.ShetabColorPalette,
    onPlayClick: () -> Unit,
    onEditClick: () -> Unit,
    onToggleComplete: () -> Unit
) {
    val theme = getSubjectTheme(task.subject)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(22.dp), clip = false)
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White)
            .clickable { onEditClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Right Side (Far right in RTL): Rounded Square pastel box with Play button
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(theme.bgPastel)
                    .clickable { onPlayClick() },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(theme.accentColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "شروع مطالعه (تمرکز)",
                        tint = Color.White,
                        modifier = Modifier
                            .size(22.dp)
                            .padding(start = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Center Column: Title, Subject Tag, Details Row (Grade & Duration)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = task.title,
                    color = Color(0xFF1E293B),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Subject Pill Tag
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = theme.bgPastel
                ) {
                    Text(
                        text = task.subject,
                        color = theme.accentColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                // Details Row: Grade (دوازدهم) • Duration (۴۵ دقیقه)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Layers,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "دوازدهم",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "•",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = task.timeLimit.toPersianNumber(),
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Left Side (Far left in RTL): Options ⋮ and Toggle Checkbox Circle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.height(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "گزینه‌ها",
                    tint = Color(0xFFCBD5E1),
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { onEditClick() }
                )

                IconButton(
                    onClick = onToggleComplete,
                    modifier = Modifier.size(24.dp)
                ) {
                    if (task.isCompleted) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "تکمیل شده",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color(0xFF3B82F6), CircleShape)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StudyPlanDateSelectorCard(
    currentDate: String,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onCalendarClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(20.dp), clip = false)
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(20.dp))
            .clickable { onCalendarClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onPreviousDay,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "روز قبل",
                    tint = Color(0xFF8B5CF6),
                    modifier = Modifier.size(22.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarToday,
                    contentDescription = null,
                    tint = Color(0xFF8B5CF6),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = currentDate.toPersianNumber(),
                    color = Color(0xFF8B5CF6),
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp
                )
            }

            IconButton(
                onClick = onNextDay,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "روز بعد",
                    tint = Color(0xFF8B5CF6),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
fun StudyPlanTodayStatsCard(
    completedCount: Int,
    totalCount: Int,
    todayScore: Int,
    progressPercent: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(24.dp), clip = false)
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Far Right in RTL: امتیاز کل امروز
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "امتیاز کل امروز",
                    fontSize = 9.5.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = todayScore.toString().toPersianNumber(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E1B4B)
                    )
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFEF3C7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⭐", fontSize = 12.sp)
                    }
                }
            }

            // 2. انجام شده
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "انجام شده",
                    fontSize = 9.5.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = completedCount.toString().toPersianNumber(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF16A34A)
                    )
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDCFCE7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF16A34A),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // 3. تعداد کل وظایف
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "تعداد کل وظایف",
                    fontSize = 9.5.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = totalCount.toString().toPersianNumber(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF9333EA)
                    )
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF3E8FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = null,
                            tint = Color(0xFF9333EA),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            // 4. Far Left in RTL: پیشرفت روزانه
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "پیشرفت روزانه",
                    fontSize = 9.5.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "$progressPercent٪".toPersianNumber(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF8B5CF6)
                    )
                    // Progress bar track & fill
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9))
                    ) {
                        val progressFraction = (progressPercent / 100f).coerceIn(0f, 1f)
                        if (progressFraction > 0f) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(progressFraction)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF8B5CF6), Color(0xFFA855F7))
                                        )
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun PersianCalendarDialog(
    colors: com.example.ui.theme.ShetabColorPalette,
    currentDate: String,
    onDismiss: () -> Unit,
    onDateSelected: (Int, String) -> Unit
) {
    val months = listOf("فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور", "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند")
    var currentMonthIndex by remember { mutableIntStateOf(5) } // Default to شهریور
    var currentYear by remember { mutableIntStateOf(1403) }

    val daysInMonth = if (currentMonthIndex < 6) 31 else if (currentMonthIndex < 11) 30 else 29
    val startDayOfWeek = (currentMonthIndex * 2 + currentYear) % 7 // Mock offset

    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        if (currentMonthIndex == 0) {
                            currentMonthIndex = 11
                            currentYear--
                        } else {
                            currentMonthIndex--
                        }
                    }) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Previous Month", tint = colors.accentMain)
                    }
                    Text("${months[currentMonthIndex]} ${currentYear.toString().toPersianNumber()}", color = colors.primaryText, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    IconButton(onClick = {
                        if (currentMonthIndex == 11) {
                            currentMonthIndex = 0
                            currentYear++
                        } else {
                            currentMonthIndex++
                        }
                    }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Next Month", tint = colors.accentMain)
                    }
                }
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    listOf("ش", "ی", "د", "س", "چ", "پ", "ج").forEach {
                        Text(it, color = colors.secondaryText, fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier.fillMaxWidth().height(250.dp)
                ) {
                    items(startDayOfWeek) {
                        Spacer(modifier = Modifier.aspectRatio(1f))
                    }
                    items(daysInMonth) { index ->
                        val day = index + 1
                        val isSelected = currentDate.contains("$day") && currentDate.contains(months[currentMonthIndex]) || (currentDate.contains("امروز") && day == 4 && currentMonthIndex == 5)
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) colors.accentMain else Color.Transparent)
                                .clickable { onDateSelected(day, "${months[currentMonthIndex]} $currentYear") },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$day".toPersianNumber(),
                                color = if (isSelected) Color.White else colors.primaryText,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        containerColor = colors.bgMain,
        shape = RoundedCornerShape(24.dp)
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDialog(
    colors: com.example.ui.theme.ShetabColorPalette,
    initialTask: StudyTaskEntity? = null,
    onDismiss: () -> Unit,
    onSave: (String, Int, Int, Int, String) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var title by remember { mutableStateOf(initialTask?.title ?: "") }
    var subject by remember { mutableStateOf(initialTask?.subject ?: "زیست شناسی") }
    var cyclesStr by remember { mutableStateOf(initialTask?.totalCycles?.toString() ?: "1") }
    
    var showCustomTime by remember { mutableStateOf(false) }
    var focusDurationStr by remember { mutableStateOf(initialTask?.focusDuration?.toString() ?: "60") }
    var restDurationStr by remember { mutableStateOf(initialTask?.restDuration?.toString() ?: "15") }

    val subjects = listOf("زیست شناسی", "فیزیک", "شیمی", "ریاضیات", "زبان انگلیسی", "عربی", "ادبیات")
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        scrimColor = Color.Black.copy(alpha = 0.4f),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color(0xFFCBD5E1), CircleShape)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFF8B5CF6).copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color(0xFF8B5CF6),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = if (initialTask == null) "افزودن برنامه درسی جدید" else "ویرایش برنامه درسی",
                        color = Color(0xFF1E1B4B),
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }

                if (onDelete != null) {
                    IconButton(
                        onClick = { onDelete(); onDismiss() },
                        modifier = Modifier.background(Color(0xFFFEE2E2), CircleShape)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9))

            // Task Title Input
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "عنوان درس یا مبحث",
                    color = Color(0xFF1E1B4B),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("مثلاً: مطالعه فصل اول زیست شناسی", color = Color(0xFF94A3B8), fontSize = 13.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC),
                        focusedTextColor = Color(0xFF1E1B4B),
                        unfocusedTextColor = Color(0xFF1E1B4B),
                        focusedBorderColor = Color(0xFF8B5CF6),
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )
            }

            // Subject Chips Selection
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "انتخاب درس مربوطه",
                    color = Color(0xFF1E1B4B),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                androidx.compose.foundation.lazy.LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    items(subjects) { s ->
                        val isSelected = subject == s
                        val subjectColor = getSubjectTheme(s).accentColor
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) subjectColor else Color(0xFFF8FAFC))
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) subjectColor else Color(0xFFE2E8F0),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { subject = s }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(subjectColor, CircleShape)
                                    )
                                }
                                Text(
                                    text = s,
                                    color = if (isSelected) Color.White else Color(0xFF334155),
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Study Cycles
            val cycles = cyclesStr.toIntOrNull() ?: 1
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("تعداد دوره‌های مطالعه", color = Color(0xFF1E1B4B), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("هر دوره پومودورو شامل مطالعه و استراحت است", color = Color(0xFF64748B), fontSize = 11.sp)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF8B5CF6).copy(alpha = 0.12f))
                                .clickable { if (cycles > 1) cyclesStr = (cycles - 1).toString() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("-", color = Color(0xFF8B5CF6), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }

                        Text(
                            text = cycles.toString().toPersianNumber(),
                            color = Color(0xFF1E1B4B),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF8B5CF6))
                                .clickable { if (cycles < 30) cyclesStr = (cycles + 1).toString() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Time Customization Switch
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("زمان‌بندی دوره‌ها", color = Color(0xFF1E1B4B), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (showCustomTime) "تنظیم سفارشی زمان" else "پیش‌فرض: ۶۰ دقیقه مطالعه، ۱۵ دقیقه استراحت",
                                color = if (showCustomTime) Color(0xFF8B5CF6) else Color(0xFF64748B),
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = showCustomTime,
                            onCheckedChange = { showCustomTime = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF8B5CF6),
                                uncheckedThumbColor = Color(0xFF94A3B8),
                                uncheckedTrackColor = Color(0xFFE2E8F0)
                            )
                        )
                    }

                    if (showCustomTime) {
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color(0xFFE2E8F0))
                        Spacer(modifier = Modifier.height(12.dp))

                        val focus = focusDurationStr.toIntOrNull() ?: 60
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("مدت مطالعه هر دوره:", color = Color(0xFF334155), fontSize = 12.sp)
                                Text("${focus.toString().toPersianNumber()} دقیقه", color = Color(0xFF8B5CF6), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = focus.toFloat(),
                                onValueChange = { focusDurationStr = (Math.round(it / 5.0) * 5).toInt().toString() },
                                valueRange = 10f..120f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF8B5CF6),
                                    activeTrackColor = Color(0xFF8B5CF6),
                                    inactiveTrackColor = Color(0xFF8B5CF6).copy(alpha = 0.2f)
                                )
                            )
                        }

                        val rest = restDurationStr.toIntOrNull() ?: 15
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("مدت استراحت:", color = Color(0xFF334155), fontSize = 12.sp)
                                Text("${rest.toString().toPersianNumber()} دقیقه", color = Color(0xFF8B5CF6), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = rest.toFloat(),
                                onValueChange = { restDurationStr = (Math.round(it / 5.0) * 5).toInt().toString() },
                                valueRange = 5f..60f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF8B5CF6),
                                    activeTrackColor = Color(0xFF8B5CF6),
                                    inactiveTrackColor = Color(0xFF8B5CF6).copy(alpha = 0.2f)
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Submit Button
            Button(
                onClick = {
                    val cycles = cyclesStr.toIntOrNull() ?: 1
                    val focus = focusDurationStr.toIntOrNull() ?: 60
                    val rest = restDurationStr.toIntOrNull() ?: 15
                    if (title.isNotBlank()) {
                        onSave(title, cycles, focus, rest, subject)
                        onDismiss()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = if (initialTask == null) "ثبت و ذخیره برنامه" else "ویرایش و به‌روزرسانی",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
