package com.example.ui.features.studyplan

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.domain.date.CalendarDayItem
import com.example.domain.date.DateTransformer
import com.example.domain.date.JalaliDate
import com.example.network.StudyTaskBookDto
import com.example.network.StudyTaskDto
import com.example.ui.core.components.shimmerEffect
import com.example.ui.core.toPersianNumber
import com.example.ui.theme.IranSansFontFamily

val PlanPurple = Color(0xFF7656F5)
val PlanPurpleDark = Color(0xFF5236CB)
val PlanPurpleLight = Color(0xFFEDE9FE)
val PlanNavy = Color(0xFF18234D)
val PlanMuted = Color(0xFF7E859E)
val PlanBackground = Color(0xFFFBFBFD)
val PlanGreen = Color(0xFF16A34A)
val PlanGreenLight = Color(0xFFDCFCE7)
val PlanOrange = Color(0xFFF97316)
val PlanOrangeLight = Color(0xFFFFEDD5)
val PlanRed = Color(0xFFEF4444)
val PlanRedLight = Color(0xFFFEE2E2)
val PlanCardBorder = Color(0xFFF0F2F7)

data class SubjectVisualConfig(
    val title: String,
    val icon: ImageVector,
    val iconTint: Color,
    val containerBg: Color,
)

fun getSubjectVisualConfig(subjectName: String, bookName: String? = null): SubjectVisualConfig {
    val text = (subjectName + " " + (bookName ?: "")).lowercase()
    return when {
        text.contains("ریاضی") || text.contains("هندسه") || text.contains("حسابان") || text.contains("آمار") -> SubjectVisualConfig(
            title = "ریاضی",
            icon = Icons.Outlined.MenuBook,
            iconTint = Color(0xFF7656F5),
            containerBg = Color(0xFFF5F3FF),
        )
        text.contains("فیزیک") -> SubjectVisualConfig(
            title = "فیزیک",
            icon = Icons.Outlined.Science,
            iconTint = Color(0xFF2563EB),
            containerBg = Color(0xFFEFF6FF),
        )
        text.contains("شیمی") -> SubjectVisualConfig(
            title = "شیمی",
            icon = Icons.Outlined.Science,
            iconTint = Color(0xFFEA580C),
            containerBg = Color(0xFFFFF7ED),
        )
        text.contains("زیست") -> SubjectVisualConfig(
            title = "زیست‌شناسی",
            icon = Icons.Outlined.Spa,
            iconTint = Color(0xFF16A34A),
            containerBg = Color(0xFFF0FDF4),
        )
        text.contains("تاریخ") -> SubjectVisualConfig(
            title = "تاریخ",
            icon = Icons.Outlined.MenuBook,
            iconTint = Color(0xFF7656F5),
            containerBg = Color(0xFFF5F3FF),
        )
        text.contains("جغرافیا") -> SubjectVisualConfig(
            title = "جغرافیا",
            icon = Icons.Outlined.MenuBook,
            iconTint = Color(0xFF0284C7),
            containerBg = Color(0xFFF0F9FF),
        )
        text.contains("ادبیات") || text.contains("فارسی") -> SubjectVisualConfig(
            title = "ادبیات فارسی",
            icon = Icons.Outlined.MenuBook,
            iconTint = Color(0xFF9333EA),
            containerBg = Color(0xFFFAF5FF),
        )
        text.contains("عربی") -> SubjectVisualConfig(
            title = "عربی",
            icon = Icons.Outlined.MenuBook,
            iconTint = Color(0xFF0D9488),
            containerBg = Color(0xFFF0FDFA),
        )
        text.contains("زبان") || text.contains("انگلیسی") -> SubjectVisualConfig(
            title = "زبان انگلیسی",
            icon = Icons.Outlined.MenuBook,
            iconTint = Color(0xFF4F46E5),
            containerBg = Color(0xFFEEF2FF),
        )
        text.contains("دین") || text.contains("قرآن") || text.contains("معارف") -> SubjectVisualConfig(
            title = "دین و زندگی",
            icon = Icons.Outlined.MenuBook,
            iconTint = Color(0xFF059669),
            containerBg = Color(0xFFECFDF5),
        )
        text.contains("فلسفه") || text.contains("منطق") -> SubjectVisualConfig(
            title = "فلسفه و منطق",
            icon = Icons.Outlined.MenuBook,
            iconTint = Color(0xFFD97706),
            containerBg = Color(0xFFFFFBEB),
        )
        text.contains("جامعه") -> SubjectVisualConfig(
            title = "جامعه‌شناسی",
            icon = Icons.Outlined.MenuBook,
            iconTint = Color(0xFF475569),
            containerBg = Color(0xFFF8FAFC),
        )
        text.contains("روانشناسی") -> SubjectVisualConfig(
            title = "روانشناسی",
            icon = Icons.Outlined.MenuBook,
            iconTint = Color(0xFFE11D48),
            containerBg = Color(0xFFFFF1F2),
        )
        text.contains("اقتصاد") -> SubjectVisualConfig(
            title = "اقتصاد",
            icon = Icons.Outlined.MenuBook,
            iconTint = Color(0xFF059669),
            containerBg = Color(0xFFECFDF5),
        )
        else -> {
            val cleanTitle = (bookName ?: subjectName).split("•", "؛", ":", "-").firstOrNull()?.trim() ?: "درس"
            SubjectVisualConfig(
                title = cleanTitle.ifBlank { "درس" },
                icon = Icons.Outlined.MenuBook,
                iconTint = Color(0xFF7656F5),
                containerBg = Color(0xFFF5F3FF),
            )
        }
    }
}

@Composable
fun StudyPlanTopHeader(
    onNotificationClick: () -> Unit = {},
    unreadNotification: Boolean = false,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(id = R.string.study_plan_title),
            color = PlanNavy,
            fontFamily = IranSansFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * Persian Calendar Header with centered date title and previous/next day navigation.
 * Clicking the date title opens a Jalali Date Picker dialog.
 */
@Composable
fun StudyPlanCalendarHeader(
    selectedDateTitle: String,
    selectedJalaliDate: JalaliDate,
    onDaySelected: (JalaliDate) -> Unit,
    onPreviousDayClick: () -> Unit,
    onNextDayClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDatePickerDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .testTag("study_plan_calendar_header"),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(1.dp, PlanCardBorder),
        shadowElevation = 0.5.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Right button (Start side in RTL / Right side) is arrow right
            IconButton(
                onClick = onNextDayClick,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PlanPurpleLight.copy(alpha = 0.5f))
                    .testTag("calendar_next_day_btn"),
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "روز بعد",
                    tint = PlanPurple,
                    modifier = Modifier.size(20.dp),
                )
            }

            // Center Date Title (Clickable to open DatePicker)
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { showDatePickerDialog = true }
                    .testTag("calendar_selected_date_title_btn"),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF9FAFB),
                border = BorderStroke(1.dp, Color(0xFFF0F2F7)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = selectedDateTitle,
                        color = PlanNavy,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("calendar_selected_date_title"),
                    )
                }
            }

            // Left button (End side in RTL / Left side) is arrow left
            IconButton(
                onClick = onPreviousDayClick,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PlanPurpleLight.copy(alpha = 0.5f))
                    .testTag("calendar_prev_day_btn"),
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "روز قبل",
                    tint = PlanPurple,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }

    if (showDatePickerDialog) {
        JalaliDatePickerDialog(
            initialDate = selectedJalaliDate,
            onDismiss = { showDatePickerDialog = false },
            onDateSelected = { date ->
                showDatePickerDialog = false
                onDaySelected(date)
            },
        )
    }
}

/**
 * Interactive Jalali Date Picker Dialog for selecting Year, Month, and Day.
 */
@Composable
fun JalaliDatePickerDialog(
    initialDate: JalaliDate,
    onDismiss: () -> Unit,
    onDateSelected: (JalaliDate) -> Unit,
) {
    var selectedYear by remember { mutableIntStateOf(initialDate.year) }
    var selectedMonth by remember { mutableIntStateOf(initialDate.month) }
    var selectedDay by remember { mutableIntStateOf(initialDate.day) }

    val monthNames = listOf(
        "فروردین", "اردیبهشت", "خرداد",
        "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر",
        "دی", "بهمن", "اسفند",
    )

    val maxDays = when {
        selectedMonth in 1..6 -> 31
        selectedMonth in 7..11 -> 30
        selectedMonth == 12 -> if (JalaliDate.isJalaliLeapYear(selectedYear)) 30 else 29
        else -> 30
    }

    if (selectedDay > maxDays) {
        selectedDay = maxDays
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(26.dp))
                .testTag("jalali_date_picker_dialog"),
            shape = RoundedCornerShape(26.dp),
            color = Color.White,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(id = R.string.date_picker_dialog_title),
                        color = PlanNavy,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.5.sp,
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "بستن", tint = PlanMuted)
                    }
                }

                // Month Grid Selector
                Text(
                    text = "انتخاب ماه:",
                    color = PlanNavy,
                    fontFamily = IranSansFontFamily,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (row in 0 until 4) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            for (col in 0 until 3) {
                                val monthIndex = row * 3 + col + 1
                                val monthName = monthNames[monthIndex - 1]
                                val isSelected = selectedMonth == monthIndex
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { selectedMonth = monthIndex },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) PlanPurple else Color(0xFFF8F9FD),
                                    border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFE5E7EB)),
                                ) {
                                    Text(
                                        text = monthName,
                                        color = if (isSelected) Color.White else PlanNavy,
                                        fontFamily = IranSansFontFamily,
                                        fontSize = 11.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 7.dp),
                                    )
                                }
                            }
                        }
                    }
                }

                // Day and Year Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Day Selector
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(text = "روز:", color = PlanNavy, fontFamily = IranSansFontFamily, fontSize = 12.5.sp)
                        FilledIconButton(
                            onClick = { if (selectedDay > 1) selectedDay-- },
                            modifier = Modifier.size(30.dp),
                            shape = CircleShape,
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = PlanPurpleLight),
                        ) {
                            Text("-", color = PlanPurple, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Text(
                            text = selectedDay.toPersianNumber(),
                            color = PlanNavy,
                            fontFamily = IranSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 4.dp),
                        )
                        FilledIconButton(
                            onClick = { if (selectedDay < maxDays) selectedDay++ },
                            modifier = Modifier.size(30.dp),
                            shape = CircleShape,
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = PlanPurpleLight),
                        ) {
                            Text("+", color = PlanPurple, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    // Year Selector
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(text = "سال:", color = PlanNavy, fontFamily = IranSansFontFamily, fontSize = 12.5.sp)
                        FilledIconButton(
                            onClick = { if (selectedYear > 1400) selectedYear-- },
                            modifier = Modifier.size(30.dp),
                            shape = CircleShape,
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = PlanPurpleLight),
                        ) {
                            Text("-", color = PlanPurple, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Text(
                            text = selectedYear.toPersianNumber(),
                            color = PlanNavy,
                            fontFamily = IranSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 4.dp),
                        )
                        FilledIconButton(
                            onClick = { if (selectedYear < 1410) selectedYear++ },
                            modifier = Modifier.size(30.dp),
                            shape = CircleShape,
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = PlanPurpleLight),
                        ) {
                            Text("+", color = PlanPurple, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }

                Button(
                    onClick = {
                        onDateSelected(JalaliDate(selectedYear, selectedMonth, selectedDay))
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PlanPurple),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("confirm_date_picker_button"),
                ) {
                    Text(
                        text = stringResource(id = R.string.date_picker_confirm),
                        color = Color.White,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarDayChip(
    item: CalendarDayItem,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .width(42.dp)
            .height(58.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("calendar_day_${item.jalaliDate}"),
        shape = RoundedCornerShape(16.dp),
        color = if (item.isSelected) PlanPurple else if (item.isToday) PlanPurpleLight else Color.White,
        border = if (item.isSelected) null else BorderStroke(1.dp, if (item.isToday) PlanPurple else PlanCardBorder),
        shadowElevation = if (item.isSelected) 3.dp else 0.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = item.dayOfWeekShort,
                color = if (item.isSelected) Color.White.copy(alpha = 0.85f) else PlanMuted,
                fontFamily = IranSansFontFamily,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Medium,
            )

            Text(
                text = item.dayNumberPersian,
                color = if (item.isSelected) Color.White else if (item.isToday) PlanPurple else PlanNavy,
                fontFamily = IranSansFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )

            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(
                        if (item.isSelected) Color.White
                        else if (item.isToday) PlanPurple
                        else Color.Transparent
                    ),
            )
        }
    }
}

@Composable
fun StudyPlanSummaryCard(
    totalDurationMinutes: Int = 0,
    remainingCount: Int = 0,
    completedCount: Int = 0,
    totalCount: Int = 0,
    progressFraction: Float = 0f,
    isLoading: Boolean = false,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(2.dp, RoundedCornerShape(24.dp), spotColor = Color(0x1A000000))
            .testTag("study_plan_summary_card"),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = BorderStroke(1.dp, PlanCardBorder),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 18.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SummaryStatItem(
                    icon = Icons.Outlined.AccessTime,
                    iconTint = PlanPurple,
                    title = stringResource(id = R.string.study_plan_stat_duration),
                    value = "${totalDurationMinutes.toPersianNumber()} دقیقه",
                    isLoading = isLoading,
                    shimmerWidth = 44.dp,
                    modifier = Modifier.weight(1.3f),
                )

                VerticalSummaryDivider()

                SummaryStatItem(
                    icon = Icons.Outlined.AccessTime,
                    iconTint = PlanOrange,
                    title = stringResource(id = R.string.study_plan_stat_remaining),
                    value = remainingCount.toPersianNumber(),
                    isLoading = isLoading,
                    shimmerWidth = 24.dp,
                    modifier = Modifier.weight(0.9f),
                )

                VerticalSummaryDivider()

                SummaryStatItem(
                    icon = Icons.Outlined.CheckCircle,
                    iconTint = PlanGreen,
                    title = stringResource(id = R.string.study_plan_stat_completed),
                    value = completedCount.toPersianNumber(),
                    isLoading = isLoading,
                    shimmerWidth = 24.dp,
                    modifier = Modifier.weight(0.9f),
                )

                VerticalSummaryDivider()

                SummaryStatItem(
                    icon = Icons.Outlined.BookmarkBorder,
                    iconTint = PlanPurple,
                    title = stringResource(id = R.string.study_plan_stat_total),
                    value = totalCount.toPersianNumber(),
                    isLoading = isLoading,
                    shimmerWidth = 24.dp,
                    modifier = Modifier.weight(1.0f),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                if (isLoading) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "از ",
                            color = PlanMuted,
                            fontFamily = IranSansFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                        )
                        Box(
                            modifier = Modifier
                                .width(18.dp)
                                .height(12.dp)
                                .shimmerEffect(RoundedCornerShape(3.dp)),
                        )
                        Text(
                            text = " تسک انجام شده",
                            color = PlanMuted,
                            fontFamily = IranSansFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                } else {
                    Text(
                        text = "از ${totalCount.toPersianNumber()} تسک انجام شده",
                        color = PlanMuted,
                        fontFamily = IranSansFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(7.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFECEFF7)),
                ) {
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.35f)
                                .clip(CircleShape)
                                .shimmerEffect(CircleShape),
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(progressFraction.coerceIn(0f, 1f))
                                .clip(CircleShape)
                                .background(PlanPurple),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryStatItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    value: String,
    isLoading: Boolean = false,
    shimmerWidth: androidx.compose.ui.unit.Dp = 28.dp,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(14.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = title,
                color = PlanMuted,
                fontFamily = IranSansFontFamily,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (isLoading) {
            Box(
                modifier = Modifier
                    .width(shimmerWidth)
                    .height(18.dp)
                    .shimmerEffect(RoundedCornerShape(4.dp)),
            )
        } else {
            Text(
                text = value,
                color = PlanNavy,
                fontFamily = IranSansFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun VerticalSummaryDivider() {
    Box(
        modifier = Modifier
            .height(34.dp)
            .width(1.dp)
            .background(Color(0xFFF1F3F9)),
    )
}

@Composable
fun StudyPlanFilterRow(
    selectedFilter: StudyTaskFilter,
    onFilterSelected: (StudyTaskFilter) -> Unit,
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("study_plan_filter_row"),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        item(key = "filter_all") {
            FilterChipPill(
                text = stringResource(id = R.string.study_plan_filter_all),
                isSelected = selectedFilter == StudyTaskFilter.ALL,
                onClick = { onFilterSelected(StudyTaskFilter.ALL) },
                testTag = "filter_all",
            )
        }

        item(key = "filter_in_progress") {
            FilterChipPill(
                text = stringResource(id = R.string.study_plan_filter_in_progress),
                icon = Icons.Outlined.PlayCircleOutline,
                isSelected = selectedFilter == StudyTaskFilter.IN_PROGRESS,
                onClick = { onFilterSelected(StudyTaskFilter.IN_PROGRESS) },
                testTag = "filter_in_progress",
            )
        }

        item(key = "filter_pending") {
            FilterChipPill(
                text = stringResource(id = R.string.study_plan_filter_pending),
                icon = Icons.Outlined.AccessTime,
                isSelected = selectedFilter == StudyTaskFilter.PENDING,
                onClick = { onFilterSelected(StudyTaskFilter.PENDING) },
                testTag = "filter_pending",
            )
        }

        item(key = "filter_completed") {
            FilterChipPill(
                text = stringResource(id = R.string.study_plan_filter_completed),
                icon = Icons.Outlined.CheckCircle,
                iconTint = if (selectedFilter == StudyTaskFilter.COMPLETED) Color.White else PlanGreen,
                isSelected = selectedFilter == StudyTaskFilter.COMPLETED,
                onClick = { onFilterSelected(StudyTaskFilter.COMPLETED) },
                testTag = "filter_completed",
            )
        }
    }
}

@Composable
private fun FilterChipPill(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconTint: Color? = null,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    testTag: String,
) {
    Surface(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(19.dp))
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(19.dp),
        color = if (isSelected) PlanPurple else Color.White,
        border = if (isSelected) null else BorderStroke(1.dp, PlanCardBorder),
        shadowElevation = if (isSelected) 2.dp else 0.dp,
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else (iconTint ?: PlanNavy),
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                color = if (isSelected) Color.White else PlanNavy,
                fontFamily = IranSansFontFamily,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1,
            )
        }
    }
}

@Composable
fun RemainingTasksSectionHeader(
    count: Int,
    onSortClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(id = R.string.study_plan_remaining_tasks_title),
                color = PlanNavy,
                fontFamily = IranSansFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = PlanPurpleLight,
            ) {
                Text(
                    text = count.toPersianNumber(),
                    color = PlanPurple,
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                )
            }
        }
    }
}

@Composable
fun CompletedTasksSectionHeader(
    count: Int,
    onSeeAllClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(id = R.string.study_plan_completed_tasks_title),
                color = PlanNavy,
                fontFamily = IranSansFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = PlanGreenLight,
            ) {
                Text(
                    text = count.toPersianNumber(),
                    color = PlanGreen,
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                )
            }
        }

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onSeeAllClick() }
                .padding(horizontal = 6.dp, vertical = 4.dp)
                .testTag("see_all_completed_button"),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
                tint = PlanMuted,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = stringResource(id = R.string.study_plan_see_all),
                color = PlanMuted,
                fontFamily = IranSansFontFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
fun StudyTaskItemCard(
    task: StudyTaskDto,
    onStartClick: () -> Unit,
    onContinueClick: () -> Unit,
    onMarkDoneClick: () -> Unit,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    isBusy: Boolean = false,
) {
    val subjectConfig = getSubjectVisualConfig(
        subjectName = task.book.name,
        bookName = task.title,
    )

    val isInProgress = task.isInProgress
    val isEditable = task.isEditable
    val isDeletable = task.isDeletable
    var showConfirmDoneDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(2.dp, RoundedCornerShape(22.dp), spotColor = Color(0x14000000))
            .testTag("task_card_${task.id}"),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = BorderStroke(1.dp, PlanCardBorder),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Right in RTL: Subject Badge
                SubjectBadgeBox(
                    config = subjectConfig,
                    modifier = Modifier.size(width = 62.dp, height = 64.dp),
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Middle Column: Title, Topic, Time & Cycle
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = task.title,
                            color = PlanNavy,
                            fontFamily = IranSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false),
                        )

                        if (task.sourceType == "MANUAL") {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFF3F4F6),
                            ) {
                                Text(
                                    text = "دستی",
                                    color = PlanMuted,
                                    fontFamily = IranSansFontFamily,
                                    fontSize = 9.sp,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                )
                            }
                        }
                    }

                    Text(
                        text = "${task.chapter.name}: ${task.topic.name}",
                        color = PlanMuted,
                        fontFamily = IranSansFontFamily,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AccessTime,
                                contentDescription = null,
                                tint = PlanMuted,
                                modifier = Modifier.size(12.dp),
                            )
                            val timeText = if (isInProgress) {
                                "${task.elapsedMinutes.toPersianNumber()} دقیقه از ${task.plannedMinutes.toPersianNumber()} دقیقه"
                            } else {
                                "${task.plannedMinutes.toPersianNumber()} دقیقه"
                            }
                            Text(
                                text = timeText,
                                color = PlanMuted,
                                fontFamily = IranSansFontFamily,
                                fontSize = 10.5.sp,
                            )
                        }

                        val cycleNumber = task.periodCount.coerceAtLeast(1)
                        Text(
                            text = "دور ${cycleNumber.toPersianNumber()}/۳",
                            color = PlanMuted,
                            fontFamily = IranSansFontFamily,
                            fontSize = 10.5.sp,
                        )

                        PrioritySignalBars(level = (cycleNumber % 4) + 1)
                    }
                }
            }

            if (isInProgress) {
                val fraction = if (task.plannedMinutes > 0) {
                    (task.elapsedMinutes.toFloat() / task.plannedMinutes.toFloat()).coerceIn(0.1f, 1f)
                } else 0.5f

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(Color(0xFFECEFF7)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction)
                            .background(PlanPurple),
                    )
                }
            }

            // Bottom Actions Bar (Split into 3 equal-width columns)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFFAFBFC),
                border = BorderStroke(0.5.dp, Color(0xFFF1F3F7)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    val isManual = task.sourceType == "MANUAL"
                    val showEdit = isManual && isEditable && onEditClick != null
                    val showDelete = isManual && isDeletable && onDeleteClick != null

                    // Column 1 (weight 1f): Edit and Delete actions
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (showEdit && showDelete) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                // Edit Text Button
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable(enabled = !isBusy) { onEditClick?.invoke() }
                                        .testTag("edit_task_item_${task.id}"),
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFF3F4F6),
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = "ویرایش",
                                            color = PlanNavy,
                                            fontFamily = IranSansFontFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 11.sp,
                                        )
                                    }
                                }

                                // Delete Text Button
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable(enabled = !isBusy) { onDeleteClick?.invoke() }
                                        .testTag("delete_task_item_${task.id}"),
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFFEE2E2).copy(alpha = 0.7f),
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = "حذف",
                                            color = PlanRed,
                                            fontFamily = IranSansFontFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 11.sp,
                                        )
                                    }
                                }
                            }
                        } else if (showEdit) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable(enabled = !isBusy) { onEditClick?.invoke() }
                                    .testTag("edit_task_item_${task.id}"),
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF3F4F6),
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "ویرایش",
                                        color = PlanNavy,
                                        fontFamily = IranSansFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 11.5.sp,
                                    )
                                }
                            }
                        } else if (showDelete) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable(enabled = !isBusy) { onDeleteClick?.invoke() }
                                    .testTag("delete_task_item_${task.id}"),
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFEE2E2).copy(alpha = 0.7f),
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "حذف",
                                        color = PlanRed,
                                        fontFamily = IranSansFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 11.5.sp,
                                    )
                                }
                            }
                        } else {
                            // Non-manual / auto-generated task (no edit/delete) -> Clean placeholder to keep layout balanced
                            Spacer(modifier = Modifier.fillMaxSize())
                        }
                    }

                    // Column 2 (weight 1f): Start/Continue Button filling the entire width of column
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(enabled = !isBusy) {
                                if (isInProgress) onContinueClick() else onStartClick()
                            }
                            .testTag(if (isInProgress) "continue_button_${task.id}" else "start_button_${task.id}"),
                        shape = RoundedCornerShape(10.dp),
                        color = PlanPurpleLight,
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = if (isInProgress) {
                                    stringResource(id = R.string.study_plan_action_continue)
                                } else {
                                    stringResource(id = R.string.study_plan_action_start)
                                },
                                color = PlanPurple,
                                fontFamily = IranSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
                            )
                        }
                    }

                    // Column 3 (weight 1f): Confirm Done button (Text Only, No circle icon, filling column width)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(enabled = !isBusy) { showConfirmDoneDialog = true }
                            .testTag("mark_done_button_${task.id}"),
                        shape = RoundedCornerShape(10.dp),
                        color = PlanGreenLight,
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (isBusy) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = PlanGreen,
                                    strokeWidth = 2.dp,
                                )
                            } else {
                                Text(
                                    text = "تأیید انجام",
                                    color = PlanGreen,
                                    fontFamily = IranSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showConfirmDoneDialog) {
        TaskCompletionConfirmDialog(
            taskTitle = task.title,
            onDismiss = { showConfirmDoneDialog = false },
            onConfirm = {
                showConfirmDoneDialog = false
                onMarkDoneClick()
            },
        )
    }
}

/**
 * Confirmation dialog before marking a task as completed.
 */
@Composable
fun TaskCompletionConfirmDialog(
    taskTitle: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(26.dp))
                .testTag("task_completion_confirm_dialog"),
            shape = RoundedCornerShape(26.dp),
            color = Color.White,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(PlanGreenLight),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = PlanGreen,
                        modifier = Modifier.size(30.dp),
                    )
                }

                Text(
                    text = stringResource(id = R.string.task_confirm_complete_title),
                    color = PlanNavy,
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = "آیا مطمئن هستید که می‌خواهید تسک «$taskTitle» را به عنوان انجام‌شده ثبت کنید؟",
                    color = PlanMuted,
                    fontFamily = IranSansFontFamily,
                    fontSize = 12.5.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = stringResource(id = R.string.task_confirm_complete_negative),
                            color = PlanNavy,
                            fontFamily = IranSansFontFamily,
                        )
                    }

                    Button(
                        onClick = onConfirm,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PlanGreen),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("confirm_mark_done_dialog_button"),
                    ) {
                        Text(
                            text = stringResource(id = R.string.task_confirm_complete_positive),
                            color = Color.White,
                            fontFamily = IranSansFontFamily,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CompletedTaskItemCard(
    task: StudyTaskDto,
) {
    val subjectConfig = getSubjectVisualConfig(
        subjectName = task.book.name,
        bookName = task.title,
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(0.5.dp, RoundedCornerShape(22.dp), spotColor = Color(0x08000000))
            .testTag("completed_task_card_${task.id}"),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFFF9FAFB),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.alpha(0.7f)) {
                SubjectBadgeBox(
                    config = subjectConfig,
                    modifier = Modifier.size(width = 68.dp, height = 72.dp),
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text = task.title,
                    color = PlanNavy.copy(alpha = 0.6f),
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = "${task.chapter.name}: ${task.topic.name}",
                    color = PlanMuted.copy(alpha = 0.7f),
                    fontFamily = IranSansFontFamily,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = null,
                        tint = PlanMuted.copy(alpha = 0.6f),
                        modifier = Modifier.size(12.dp),
                    )
                    Text(
                        text = "${task.plannedMinutes.toPersianNumber()} دقیقه",
                        color = PlanMuted.copy(alpha = 0.7f),
                        fontFamily = IranSansFontFamily,
                        fontSize = 10.5.sp,
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.study_plan_action_done),
                    color = PlanGreen.copy(alpha = 0.8f),
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                )
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(PlanGreen.copy(alpha = 0.75f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun SubjectBadgeBox(
    config: SubjectVisualConfig,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(config.containerBg),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(4.dp),
        ) {
            Icon(
                imageVector = config.icon,
                contentDescription = null,
                tint = config.iconTint,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = config.title,
                color = config.iconTint,
                fontFamily = IranSansFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun PrioritySignalBars(level: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        for (i in 1..4) {
            val isActive = i <= level
            val height = (5 + i * 2).dp
            val barColor = if (isActive) {
                if (level >= 4) PlanGreen else PlanOrange
            } else {
                Color(0xFFE5E7EB)
            }
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(height)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(barColor),
            )
        }
    }
}

@Composable
fun AddTaskFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .size(56.dp)
            .shadow(10.dp, CircleShape, spotColor = Color(0x667656F5))
            .clip(CircleShape)
            .clickable { onClick() }
            .testTag("add_study_task_fab"),
        shape = CircleShape,
        color = PlanPurple,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(id = R.string.study_plan_add_task),
                tint = Color.White,
                modifier = Modifier.size(28.dp),
            )
        }
    }
}

@Composable
fun StudyPlanSkeletonLoading() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        StudyPlanSummaryCard(
            totalDurationMinutes = 0,
            remainingCount = 0,
            completedCount = 0,
            totalCount = 0,
            progressFraction = 0f,
            isLoading = true,
        )

        StudyPlanFilterRow(
            selectedFilter = StudyTaskFilter.ALL,
            onFilterSelected = {},
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(96.dp)
                        .shimmerEffect(RoundedCornerShape(22.dp)),
                )
            }
        }
    }
}

@Composable
fun StudyPlanEmptyState(
    onAddTaskClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = BorderStroke(1.dp, PlanCardBorder),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(PlanPurpleLight),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.MenuBook,
                    contentDescription = null,
                    tint = PlanPurple,
                    modifier = Modifier.size(32.dp),
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(id = R.string.study_plan_empty_title),
                color = PlanNavy,
                fontFamily = IranSansFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(id = R.string.study_plan_empty_subtitle),
                color = PlanMuted,
                fontFamily = IranSansFontFamily,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onAddTaskClick,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PlanPurple),
                modifier = Modifier.testTag("empty_state_add_task_button"),
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(id = R.string.study_plan_add_task),
                    color = Color.White,
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                )
            }
        }
    }
}

@Composable
fun CreateTaskDialog(
    books: List<StudyTaskBookDto>,
    isCreating: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (topicId: String, periodCount: Int, minutesPerPeriod: Int) -> Unit,
) {
    var selectedBookIndex by remember { mutableIntStateOf(0) }
    var selectedChapterIndex by remember { mutableIntStateOf(0) }
    var selectedTopicIndex by remember { mutableIntStateOf(0) }
    var periodCount by remember { mutableIntStateOf(1) }
    var minutesPerPeriod by remember { mutableIntStateOf(45) }

    val safeBooks = books.ifEmpty { emptyList() }
    val currentBook = safeBooks.getOrNull(selectedBookIndex)
    val chapters = currentBook?.chapters ?: emptyList()
    val currentChapter = chapters.getOrNull(selectedChapterIndex)
    val topics = currentChapter?.topics ?: emptyList()
    val currentTopic = topics.getOrNull(selectedTopicIndex)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(26.dp)),
            shape = RoundedCornerShape(26.dp),
            color = Color.White,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "افزودن تسک مطالعه جدید",
                        color = PlanNavy,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "بستن", tint = PlanMuted)
                    }
                }

                if (safeBooks.isEmpty()) {
                    Text(
                        text = "کتابی برای رشته شما در دسترس نیست.",
                        color = PlanMuted,
                        fontFamily = IranSansFontFamily,
                        fontSize = 12.sp,
                    )
                } else {
                    Text(text = "انتخاب کتاب:", color = PlanNavy, fontFamily = IranSansFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        safeBooks.take(4).forEachIndexed { index, book ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        selectedBookIndex = index
                                        selectedChapterIndex = 0
                                        selectedTopicIndex = 0
                                    },
                                shape = RoundedCornerShape(12.dp),
                                color = if (selectedBookIndex == index) PlanPurpleLight else Color(0xFFF8F9FD),
                                border = if (selectedBookIndex == index) BorderStroke(1.dp, PlanPurple) else null,
                            ) {
                                Text(
                                    text = book.name,
                                    color = if (selectedBookIndex == index) PlanPurple else PlanNavy,
                                    fontFamily = IranSansFontFamily,
                                    fontWeight = if (selectedBookIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                )
                            }
                        }
                    }

                    if (topics.isNotEmpty()) {
                        Text(text = "مبحث:", color = PlanNavy, fontFamily = IranSansFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${currentChapter?.name ?: ""} - ${currentTopic?.name ?: ""}",
                            color = PlanPurple,
                            fontFamily = IranSansFontFamily,
                            fontSize = 12.sp,
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = "مدت زمان (دقیقه):", color = PlanNavy, fontFamily = IranSansFontFamily, fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(30, 45, 60, 90).forEach { mins ->
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { minutesPerPeriod = mins },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (minutesPerPeriod == mins) PlanPurple else Color(0xFFF0F2F7),
                                ) {
                                    Text(
                                        text = mins.toPersianNumber(),
                                        color = if (minutesPerPeriod == mins) Color.White else PlanNavy,
                                        fontFamily = IranSansFontFamily,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = {
                        val topicId = currentTopic?.id ?: ""
                        if (topicId.isNotBlank()) {
                            onConfirm(topicId, periodCount, minutesPerPeriod)
                        }
                    },
                    enabled = currentTopic != null && !isCreating,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PlanPurple),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("confirm_create_task_button"),
                ) {
                    if (isCreating) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text(
                            text = "ثبت و افزودن به برنامه",
                            color = Color.White,
                            fontFamily = IranSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                        )
                    }
                }
            }
        }
    }
}

/**
 * Edit Task Dialog for manual tasks that have NOT started yet (execution == null).
 */
@Composable
fun EditTaskDialog(
    task: StudyTaskDto,
    books: List<StudyTaskBookDto>,
    isUpdating: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (periodCount: Int, minutesPerPeriod: Int) -> Unit,
) {
    var periodCount by remember { mutableIntStateOf(task.periodCount.coerceAtLeast(1)) }
    var minutesPerPeriod by remember { mutableIntStateOf(task.minutesPerPeriod.coerceAtLeast(15)) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(26.dp)),
            shape = RoundedCornerShape(26.dp),
            color = Color.White,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "ویرایش تسک مطالعه",
                        color = PlanNavy,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "بستن", tint = PlanMuted)
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF9FAFB),
                    border = BorderStroke(1.dp, PlanCardBorder),
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = task.title,
                            color = PlanNavy,
                            fontFamily = IranSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                        )
                        Text(
                            text = "${task.book.name} | ${task.chapter.name} | ${task.topic.name}",
                            color = PlanMuted,
                            fontFamily = IranSansFontFamily,
                            fontSize = 11.sp,
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = "تعداد دوره‌ها:", color = PlanNavy, fontFamily = IranSansFontFamily, fontSize = 12.5.sp)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        FilledIconButton(
                            onClick = { if (periodCount > 1) periodCount-- },
                            modifier = Modifier.size(32.dp),
                            shape = CircleShape,
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = PlanPurpleLight),
                        ) {
                            Text("-", color = PlanPurple, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        Text(
                            text = periodCount.toPersianNumber(),
                            color = PlanNavy,
                            fontFamily = IranSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                        )

                        FilledIconButton(
                            onClick = { if (periodCount < 10) periodCount++ },
                            modifier = Modifier.size(32.dp),
                            shape = CircleShape,
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = PlanPurpleLight),
                        ) {
                            Text("+", color = PlanPurple, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = "مدت هر دوره (دقیقه):", color = PlanNavy, fontFamily = IranSansFontFamily, fontSize = 12.5.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(30, 45, 60, 90).forEach { mins ->
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { minutesPerPeriod = mins },
                                shape = RoundedCornerShape(10.dp),
                                color = if (minutesPerPeriod == mins) PlanPurple else Color(0xFFF0F2F7),
                            ) {
                                Text(
                                    text = mins.toPersianNumber(),
                                    color = if (minutesPerPeriod == mins) Color.White else PlanNavy,
                                    fontFamily = IranSansFontFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = { onConfirm(periodCount, minutesPerPeriod) },
                    enabled = !isUpdating,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PlanPurple),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("confirm_edit_task_button"),
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text(
                            text = "ذخیره تغییرات",
                            color = Color.White,
                            fontFamily = IranSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                        )
                    }
                }
            }
        }
    }
}

/**
 * Delete confirmation dialog for manual study tasks.
 */
@Composable
fun DeleteTaskConfirmDialog(
    task: StudyTaskDto,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(PlanRedLight),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = null,
                        tint = PlanRed,
                        modifier = Modifier.size(28.dp),
                    )
                }

                Text(
                    text = "حذف تسک مطالعه",
                    color = PlanNavy,
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = "آیا از حذف تسک «${task.title}» اطمینان دارید؟ این عملیات قابل بازگشت نیست.",
                    color = PlanMuted,
                    fontFamily = IranSansFontFamily,
                    fontSize = 12.5.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("انصراف", color = PlanNavy, fontFamily = IranSansFontFamily)
                    }

                    Button(
                        onClick = onConfirm,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PlanRed),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("confirm_delete_dialog_button"),
                    ) {
                        Text("حذف", color = Color.White, fontFamily = IranSansFontFamily, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
