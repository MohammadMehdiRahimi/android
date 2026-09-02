package com.example.ui.features.studyplan

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.date.JalaliDate
import com.example.ui.core.toPersianNumber
import com.example.ui.theme.IranSansFontFamily

// Theme Colors matching the reference design exactly
val PlanHeaderNavy = Color(0xFF17203A)
val PlanSubtitleGray = Color(0xFF7180A4)
val PlanBrandPurple = Color(0xFF5B2CFF)
val PlanLightBg = Color(0xFFF9FAFE)
val PlanCardBorderColor = Color(0xFFECEEF5)
val PlanBadgeBg = Color(0xFFF1F4F9)
val PlanLavenderTint = Color(0xFFEDE8FF)
val PlanGreenSuccess = Color(0xFF16A34A)
val PlanOrangeWarn = Color(0xFFD97706)
val PlanDeleteRed = Color(0xFFEF4444)

/**
 * 1. Top Header Bar without user avatar
 * Center: Persian title «برنامه هفتگی» & Subtitle.
 * Left of screen: Circular white button with purple calendar icon.
 */
@Composable
fun PixelPerfectPlanHeader(
    studentName: String,
    onBackClick: () -> Unit,
    onCalendarClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
    ) {
        // Centered Title & Subtitle
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.72f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(R.string.create_plan_weekly_title),
                fontFamily = IranSansFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = PlanHeaderNavy,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "برنامه‌ریزی، مدیریت و پیشرفت هفتگی",
                fontFamily = IranSansFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 11.5.sp,
                color = PlanSubtitleGray,
                textAlign = TextAlign.Center,
            )
        }

        // Calendar Button on the left side of the screen (in RTL, CenterEnd maps to left)
        Surface(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(42.dp)
                .clip(CircleShape)
                .clickable { onCalendarClick() }
                .testTag("pixel_plan_calendar_button"),
            shape = CircleShape,
            color = Color.White,
            border = BorderStroke(1.dp, PlanCardBorderColor),
            shadowElevation = 1.5.dp,
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = "تقویم",
                    tint = PlanBrandPurple,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

/**
 * 2. Horizontal Week Days Selector
 * Shows 4 days visible in viewport with smooth horizontal scroll for the rest.
 * Positioned Right arrow on the right side and Left arrow on the left side.
 * Selected / Today day is highlighted with soft light purple (#EDE8FF) and brand purple border/text.
 */
@Composable
fun PixelPerfectWeekSelector(
    weekDays: List<WeekDayItem>,
    onDaySelected: (JalaliDate) -> Unit,
    onPreviousWeekClick: () -> Unit = {},
    onNextWeekClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(1.5.dp, RoundedCornerShape(16.dp), spotColor = Color(0x0A000000))
            .testTag("pixel_plan_week_row"),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, PlanCardBorderColor),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Right navigation chevron (pointing right, located at the right side of the container)
            IconButton(
                onClick = {
                    if (listState.firstVisibleItemIndex > 0) {
                        coroutineScope.launch {
                            listState.animateScrollToItem(maxOf(0, listState.firstVisibleItemIndex - 1))
                        }
                    } else {
                        onNextWeekClick()
                    }
                },
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "روز یا هفته بعد",
                    tint = PlanBrandPurple,
                    modifier = Modifier.size(22.dp),
                )
            }

            // 4 days visible in viewport with smooth horizontal scrolling for remaining days
            LazyRow(
                state = listState,
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(weekDays) { item ->
                    WeekDayItemView(
                        item = item,
                        onClick = { onDaySelected(item.date) },
                    )
                }
            }

            // Left navigation chevron (pointing left, located at the left side of the container)
            IconButton(
                onClick = {
                    if (listState.firstVisibleItemIndex < weekDays.size - 4) {
                        coroutineScope.launch {
                            listState.animateScrollToItem(minOf(weekDays.size - 1, listState.firstVisibleItemIndex + 1))
                        }
                    } else {
                        onPreviousWeekClick()
                    }
                },
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "روز یا هفته قبل",
                    tint = PlanBrandPurple,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}

@Composable
fun WeekDayItemView(
    item: WeekDayItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSelected = item.isSelected
    if (isSelected) {
        Surface(
            modifier = modifier
                .width(52.dp)
                .height(68.dp)
                .clip(RoundedCornerShape(14.dp))
                .clickable { onClick() }
                .testTag("week_day_${item.dayOfMonth}"),
            shape = RoundedCornerShape(14.dp),
            color = PlanLavenderTint,
            border = BorderStroke(1.5.dp, PlanBrandPurple),
            shadowElevation = 1.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = item.dayOfWeekName,
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = PlanBrandPurple,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
                Text(
                    text = item.dayOfMonth.toPersianNumber(),
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = PlanBrandPurple,
                    textAlign = TextAlign.Center,
                )
            }
        }
    } else {
        Column(
            modifier = modifier
                .width(44.dp)
                .height(68.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { onClick() }
                .padding(vertical = 8.dp)
                .testTag("week_day_${item.dayOfMonth}"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = item.dayOfWeekName,
                fontFamily = IranSansFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 10.5.sp,
                color = PlanSubtitleGray,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
            Text(
                text = item.dayOfMonth.toPersianNumber(),
                fontFamily = IranSansFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = PlanHeaderNavy,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/**
 * 3. Daily Summary Progress Card
 * Displays:
 *  - Right Half: Clock icon in lavender circle + "کل زمان مطالعه" + "۶:۳۰" + "ساعت"
 *  - Center: Subtle vertical divider
 *  - Left Half: List icon in lavender circle + "تعداد جلسات" + "۶" + "جلسه"
 */
@Composable
fun PixelPerfectDailySummaryCard(
    totalHoursText: String,
    totalSessionsCount: Int,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(1.5.dp, RoundedCornerShape(16.dp), spotColor = Color(0x0A000000))
            .testTag("pixel_plan_summary_card"),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, PlanCardBorderColor),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Right Metric (کل زمان مطالعه)
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEDE8FF)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = null,
                        tint = PlanBrandPurple,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.create_plan_total_study_time),
                        fontFamily = IranSansFontFamily,
                        fontSize = 10.5.sp,
                        color = PlanSubtitleGray,
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = totalHoursText.toPersianNumber(),
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = PlanHeaderNavy,
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = stringResource(R.string.create_plan_hour_unit),
                        fontFamily = IranSansFontFamily,
                        fontSize = 10.5.sp,
                        color = PlanSubtitleGray,
                    )
                }
            }

            // Center Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(40.dp)
                    .background(Color(0xFFF0F2F6))
            )

            // Left Metric (تعداد جلسات)
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEDE8FF)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FormatListBulleted,
                        contentDescription = null,
                        tint = PlanBrandPurple,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.create_plan_sessions_count),
                        fontFamily = IranSansFontFamily,
                        fontSize = 10.5.sp,
                        color = PlanSubtitleGray,
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = totalSessionsCount.toPersianNumber(),
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = PlanHeaderNavy,
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = stringResource(R.string.create_plan_session_unit),
                        fontFamily = IranSansFontFamily,
                        fontSize = 10.5.sp,
                        color = PlanSubtitleGray,
                    )
                }
            }
        }
    }
}

/**
 * 4. Study Session Item Card (Minimal & Compact)
 * Shows:
 *  - Subject Icon with colored background
 *  - Subject title & Chapter / Topic subtitle
 *  - Duration pill badge
 *  - Time indicator pill badge
 *  - Vertical divider
 *  - Minimal icon-only action buttons: Edit icon (lavender container) on top, Delete icon (red container) below
 */
@Composable
fun PixelPerfectSessionCard(
    session: StudySessionUiModel,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(1.dp, RoundedCornerShape(16.dp), spotColor = Color(0x06000000))
            .testTag("session_card_${session.id}"),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, PlanCardBorderColor),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Right Section (RTL): Subject Icon + Info + Badges
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Category Icon with tinted rounded container
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(session.category.containerBg)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(id = session.category.iconRes),
                        contentDescription = null,
                        tint = Color(session.category.iconTint),
                        modifier = Modifier.size(22.dp),
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    // Title
                    Text(
                        text = session.subjectTitle,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp,
                        color = PlanHeaderNavy,
                    )
                    // Subtitle / Chapter
                    Text(
                        text = session.chapterTopic,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = PlanSubtitleGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Badges row: Time badge + Duration badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        // Start Time pill
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = PlanBadgeBg,
                        ) {
                            Text(
                                text = session.startTime.toPersianNumber(),
                                fontFamily = IranSansFontFamily,
                                fontSize = 10.5.sp,
                                color = PlanSubtitleGray,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            )
                        }

                        // Duration pill
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = PlanBadgeBg,
                        ) {
                            Text(
                                text = stringResource(R.string.create_plan_minute_badge_unit, session.durationMinutes.toPersianNumber()),
                                fontFamily = IranSansFontFamily,
                                fontSize = 10.5.sp,
                                color = PlanSubtitleGray,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            )
                        }
                    }
                }
            }

            // Divider between Content & Action column
            Box(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .width(1.dp)
                    .height(44.dp)
                    .background(Color(0xFFF0EFF6))
            )

            // Left Section (RTL): Minimal Icon-Only Action Column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                // Edit Icon Button (clean lavender container without text)
                Surface(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onEditClick() }
                        .testTag("edit_session_${session.id}"),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF3F0FD),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = stringResource(R.string.create_plan_edit_session),
                            tint = PlanBrandPurple,
                            modifier = Modifier.size(17.dp),
                        )
                    }
                }

                // Delete Icon Button (clean subtle container without text)
                Surface(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onDeleteClick() }
                        .testTag("delete_session_${session.id}"),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFFF1F2),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = stringResource(R.string.create_plan_delete_session),
                            tint = PlanDeleteRed,
                            modifier = Modifier.size(17.dp),
                        )
                    }
                }
            }
        }
    }
}

/**
 * 5. Dashed Add Session Button («افزودن جلسه جدید»)
 * Full-width white card with fine purple dashed border, smaller text and circular lavender plus icon.
 */
@Composable
fun AddSessionDashedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .drawBehind {
                    val stroke = Stroke(
                        width = 1.0.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
                    )
                    drawRoundRect(
                        color = Color(0xFF8B5CF6),
                        style = stroke,
                        cornerRadius = CornerRadius(16.dp.toPx())
                    )
                }
                .clickable { onClick() }
                .testTag("add_session_action_button"),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "افزودن جلسه جدید",
                    color = PlanBrandPurple,
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEDE8FF)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = PlanBrandPurple,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

/**
 * 5. Pixel-Accurate Add Study Session Modal (Replacing legacy bottom sheet)
 * Strictly adhering to the reference design:
 * - Header with Back Button (circular), Centered Title ("افزودن جلسه") & Subtitle, Book Icon (circular)
 * - Main White Rounded Card (32.dp rounded, border, shadow):
 *   - Section 1: "درس و کتاب ۱" + Grade selector dropdown ("پایه دوازدهم")
 *   - Section 2: Subject Chips row ("اقتصاد", "تاریخ", "جامعه شناسی", "جغرافیا")
 *   - Section 3: Chapter Box ("فصل و مباحث ۱" + Clipboard icon + dropdown + selectable topic radio items)
 *   - Section 4: "+ اضافه کردن فصل" button
 *   - Divider
 *   - Section 5: "زمان‌بندی و دوره‌های مطالعه" with Clock icon + Stepper ("۳ دوره")
 *   - Section 6: Sliders row (Rest time slider "۱۵ د" with coffee icon + Study time slider "۴۵ د" with book icon)
 *   - Section 7: Info box ("برنامه شما به طور خودکار در تقویم تحصیلی شما به‌روزرسانی خواهد شد.")
 *   - Section 8: Action buttons ("ثبت جلسه" in Purple + "انصراف" in White/Outline)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStudySessionModal(
    editingSession: StudySessionUiModel? = null,
    onDismiss: () -> Unit,
    onConfirmAdd: (title: String, topic: String, startTime: String, duration: Int, category: SubjectCategory) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Grade dropdown state
    val availableGrades = remember {
        listOf(
            "grade_12" to "پایه دوازدهم",
            "grade_11" to "پایه یازدهم",
            "grade_10" to "پایه دهم",
            "konkur" to "کنکور",
        )
    }
    var selectedGrade by remember { mutableStateOf("grade_12") }
    var selectedGradeName by remember { mutableStateOf("پایه دوازدهم") }
    var isGradeMenuExpanded by remember { mutableStateOf(false) }

    // Subjects list matching the reference image: اقتصاد، تاریخ، جامعه شناسی، جغرافیا
    data class ModalSubject(val id: String, val name: String, val category: SubjectCategory)
    val defaultSubjects = remember {
        listOf(
            ModalSubject("econ", "اقتصاد", SubjectCategory.GENERAL),
            ModalSubject("hist", "تاریخ", SubjectCategory.LITERATURE),
            ModalSubject("soc", "جامعه شناسی", SubjectCategory.GENERAL),
            ModalSubject("geo", "جغرافیا", SubjectCategory.GENERAL),
        )
    }
    var selectedSubjectId by remember { mutableStateOf(defaultSubjects.first().id) }

    // Chapter & Topics state matching the reference:
    // Chapter 1: "۱: کسب‌وکار و کارآفرینی"
    // Topics: 1. "موفقیت و شکست کسب‌وکارها" (Selected) 2. "کارآفرینی و نقش"
    data class ModalTopic(val id: String, val title: String)
    data class ModalChapterBlock(
        val blockId: String,
        val chapterTitle: String,
        val topics: List<ModalTopic>,
        val selectedTopicId: String?,
    )

    val sampleChaptersList = remember {
        listOf(
            "۱: کسب‌وکار و کارآفرینی",
            "۲: تولید و توزیع در اقتصاد",
            "۳: بودجه و حسابداری",
            "۴: تجارت بین‌الملل و ارز",
        )
    }

    var chapterBlocks by remember {
        mutableStateOf(
            listOf(
                ModalChapterBlock(
                    blockId = "chap_block_1",
                    chapterTitle = "۱: کسب‌وکار و کارآفرینی",
                    topics = listOf(
                        ModalTopic("top_1", "موفقیت و شکست کسب‌وکارها"),
                        ModalTopic("top_2", "کارآفرینی و نقش"),
                    ),
                    selectedTopicId = "top_1",
                )
            )
        )
    }

    // Schedule: Cycle counter, Study time, Rest time
    var periodCount by remember { mutableStateOf(3) }
    var studyDurationMinutes by remember { mutableStateOf(45) }
    var restDurationMinutes by remember { mutableStateOf(15) }

    val isEditMode = editingSession != null

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFFFAF9FD),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = null,
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // --- 1. Header Row ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    // Right: Circular Book Icon
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, Color(0xFFEFEBF8), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF1ECFC)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Assignment,
                                contentDescription = null,
                                tint = Color(0xFF6B4EE8),
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }

                    // Center: Title & Subtitle
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.add_study_session_title),
                            fontFamily = IranSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF1E1548),
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = stringResource(R.string.add_study_session_subtitle),
                            fontFamily = IranSansFontFamily,
                            fontSize = 12.sp,
                            color = Color(0xFF78788D),
                            textAlign = TextAlign.Center,
                        )
                    }

                    // Left: Circular Close / Back Button
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, Color(0xFFEFEBF8), CircleShape)
                            .clickable { onDismiss() }
                            .testTag("add_session_close_button"),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بستن",
                            tint = Color(0xFF1E1548),
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }

                // --- 2. Main White Container Card ---
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(28.dp), spotColor = Color(0x126B4EE8))
                        .testTag("add_session_main_card"),
                    shape = RoundedCornerShape(28.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFECE8F6)),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        // Header Inside Card: "درس و کتاب ۱" + Grade Dropdown
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(RoundedCornerShape(9.dp))
                                        .background(Color(0xFFEDE9FE)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                                        contentDescription = null,
                                        tint = Color(0xFF6B4EE8),
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                                Text(
                                    text = stringResource(R.string.add_study_session_section_book),
                                    fontFamily = IranSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF1E1548),
                                )
                            }

                            // Grade selector pill dropdown
                            Box {
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { isGradeMenuExpanded = true }
                                        .testTag("add_session_grade_dropdown"),
                                    color = Color(0xFFF7F5FC),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color(0xFFE4DEF3)),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    ) {
                                        Text(
                                            text = selectedGradeName,
                                            fontFamily = IranSansFontFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 11.5.sp,
                                            color = Color(0xFF1E1548),
                                        )
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = null,
                                            tint = Color(0xFF6B4EE8),
                                            modifier = Modifier.size(15.dp),
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = isGradeMenuExpanded,
                                    onDismissRequest = { isGradeMenuExpanded = false },
                                    modifier = Modifier
                                        .background(Color.White, RoundedCornerShape(14.dp))
                                        .border(1.dp, Color(0xFFECE8F6), RoundedCornerShape(14.dp)),
                                ) {
                                    availableGrades.forEach { (key, name) ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = name,
                                                    fontFamily = IranSansFontFamily,
                                                    fontWeight = if (key == selectedGrade) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (key == selectedGrade) Color(0xFF6B4EE8) else Color(0xFF1E1548),
                                                    fontSize = 12.sp,
                                                )
                                            },
                                            onClick = {
                                                selectedGrade = key
                                                selectedGradeName = name
                                                isGradeMenuExpanded = false
                                            },
                                        )
                                    }
                                }
                            }
                        }

                        // Subject Selection Chips: اقتصاد، تاریخ، جامعه شناسی، جغرافیا
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_session_subject_chips_row"),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(defaultSubjects, key = { it.id }) { subj ->
                                val isSelected = subj.id == selectedSubjectId
                                Surface(
                                    modifier = Modifier
                                        .height(38.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { selectedSubjectId = subj.id }
                                        .testTag("add_session_subject_${subj.id}"),
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) Color(0xFFF7F5FC) else Color.White,
                                    border = BorderStroke(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) Color(0xFF6B4EE8) else Color(0xFFE5E2EE),
                                    ),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    ) {
                                        if (isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFF6B4EE8)),
                                                contentAlignment = Alignment.Center,
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(10.dp),
                                                )
                                            }
                                        }
                                        Text(
                                            text = subj.name,
                                            fontFamily = IranSansFontFamily,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 12.sp,
                                            color = if (isSelected) Color(0xFF6B4EE8) else Color(0xFF1E1548),
                                        )
                                    }
                                }
                            }
                        }

                        // --- 3. Chapter and Topics Section Box ---
                        chapterBlocks.forEachIndexed { chIdx, block ->
                            var isChapterDropdownOpen by remember { mutableStateOf(false) }

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .testTag("add_session_chapter_box_${block.blockId}"),
                                shape = RoundedCornerShape(18.dp),
                                color = Color(0xFFF9F8FD),
                                border = BorderStroke(1.dp, Color(0xFFECE9F7)),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    // Chapter Box Header: Clipboard Icon + "فصل و مباحث ۱"
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Outlined.Assignment,
                                                contentDescription = null,
                                                tint = Color(0xFF6B4EE8),
                                                modifier = Modifier.size(16.dp),
                                            )
                                            Text(
                                                text = "فصل و مباحث ${(chIdx + 1).toPersianNumber()}",
                                                fontFamily = IranSansFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = Color(0xFF1E1548),
                                            )
                                        }

                                        if (chapterBlocks.size > 1) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFFFEE2E2))
                                                    .clickable {
                                                        chapterBlocks = chapterBlocks.filterNot { it.blockId == block.blockId }
                                                    },
                                                contentAlignment = Alignment.Center,
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "حذف",
                                                    tint = Color(0xFFEF4444),
                                                    modifier = Modifier.size(13.dp),
                                                )
                                            }
                                        }
                                    }

                                    // Chapter Dropdown Selector
                                    Box(modifier = Modifier.fillMaxWidth()) {
                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(42.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .clickable { isChapterDropdownOpen = true }
                                                .testTag("add_session_chapter_selector_${block.blockId}"),
                                            shape = RoundedCornerShape(12.dp),
                                            color = Color.White,
                                            border = BorderStroke(1.dp, Color(0xFFE2DEF2)),
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                            ) {
                                                Text(
                                                    text = block.chapterTitle,
                                                    fontFamily = IranSansFontFamily,
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 12.sp,
                                                    color = Color(0xFF1E1548),
                                                )
                                                Icon(
                                                    imageVector = Icons.Default.KeyboardArrowDown,
                                                    contentDescription = null,
                                                    tint = Color(0xFF6B4EE8),
                                                    modifier = Modifier.size(16.dp),
                                                )
                                            }
                                        }

                                        DropdownMenu(
                                            expanded = isChapterDropdownOpen,
                                            onDismissRequest = { isChapterDropdownOpen = false },
                                            modifier = Modifier
                                                .fillMaxWidth(0.85f)
                                                .background(Color.White, RoundedCornerShape(12.dp))
                                                .border(1.dp, Color(0xFFECE8F6), RoundedCornerShape(12.dp)),
                                        ) {
                                            sampleChaptersList.forEach { chTitle ->
                                                DropdownMenuItem(
                                                    text = {
                                                        Text(
                                                            text = chTitle,
                                                            fontFamily = IranSansFontFamily,
                                                            fontSize = 12.sp,
                                                            color = if (chTitle == block.chapterTitle) Color(0xFF6B4EE8) else Color(0xFF1E1548),
                                                            fontWeight = if (chTitle == block.chapterTitle) FontWeight.Bold else FontWeight.Normal,
                                                        )
                                                    },
                                                    onClick = {
                                                        chapterBlocks = chapterBlocks.map { b ->
                                                            if (b.blockId == block.blockId) b.copy(chapterTitle = chTitle) else b
                                                        }
                                                        isChapterDropdownOpen = false
                                                    },
                                                )
                                            }
                                        }
                                    }

                                    // Selectable Topic Radio Items:
                                    // 1. "موفقیت و شکست کسب‌وکارها"
                                    // 2. "کارآفرینی و نقش"
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        block.topics.forEach { topic ->
                                            val isTopicSelected = topic.id == block.selectedTopicId

                                            Surface(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(42.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .clickable {
                                                        chapterBlocks = chapterBlocks.map { b ->
                                                            if (b.blockId == block.blockId) {
                                                                b.copy(selectedTopicId = topic.id)
                                                            } else b
                                                        }
                                                    }
                                                    .testTag("add_session_topic_${topic.id}"),
                                                shape = RoundedCornerShape(12.dp),
                                                color = Color.White,
                                                border = BorderStroke(
                                                    width = 1.dp,
                                                    color = if (isTopicSelected) Color(0xFF6B4EE8) else Color(0xFFE8E5F3),
                                                ),
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(horizontal = 12.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                ) {
                                                    Text(
                                                        text = topic.title,
                                                        fontFamily = IranSansFontFamily,
                                                        fontWeight = if (isTopicSelected) FontWeight.Bold else FontWeight.Normal,
                                                        fontSize = 12.sp,
                                                        color = Color(0xFF1E1548),
                                                    )

                                                    // Radio circle
                                                    if (isTopicSelected) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(18.dp)
                                                                .clip(CircleShape)
                                                                .background(Color(0xFF6B4EE8)),
                                                            contentAlignment = Alignment.Center,
                                                        ) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(7.dp)
                                                                    .clip(CircleShape)
                                                                    .background(Color.White),
                                                            )
                                                        }
                                                    } else {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(18.dp)
                                                                .clip(CircleShape)
                                                                .border(1.5.dp, Color(0xFFCBD5E1), CircleShape),
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Button: "+ اضافه کردن فصل"
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    val nextIndex = chapterBlocks.size + 1
                                    val newBlock = ModalChapterBlock(
                                        blockId = "chap_block_$nextIndex",
                                        chapterTitle = sampleChaptersList.getOrElse(nextIndex - 1) { "فصل $nextIndex" },
                                        topics = listOf(
                                            ModalTopic("top_${nextIndex}_1", "مبحث اول"),
                                            ModalTopic("top_${nextIndex}_2", "مبحث دوم"),
                                        ),
                                        selectedTopicId = "top_${nextIndex}_1",
                                    )
                                    chapterBlocks = chapterBlocks + newBlock
                                }
                                .testTag("add_session_add_chapter_btn"),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF7F5FC),
                            border = BorderStroke(1.dp, Color(0xFFE4DBFB)),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color(0xFF6B4EE8),
                                    modifier = Modifier.size(16.dp),
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = stringResource(R.string.add_study_session_add_chapter_action),
                                    fontFamily = IranSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF6B4EE8),
                                )
                            }
                        }

                        HorizontalDivider(color = Color(0xFFF1EEF8), thickness = 1.dp)

                        // --- 4. Schedule & Cycles Section ---
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEDE9FE)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.AccessTime,
                                        contentDescription = null,
                                        tint = Color(0xFF6B4EE8),
                                        modifier = Modifier.size(14.dp),
                                    )
                                }
                                Text(
                                    text = stringResource(R.string.add_study_session_timing_title),
                                    fontFamily = IranSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF1E1548),
                                )
                            }

                            // Stepper: "-" "۳ دوره" "+"
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF3F1FA),
                                border = BorderStroke(1.dp, Color(0xFFE5E0F2)),
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                            .clickable { if (periodCount > 1) periodCount-- }
                                            .testTag("add_session_decrement_cycle"),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Remove,
                                            contentDescription = "کاهش",
                                            tint = Color(0xFF6B4EE8),
                                            modifier = Modifier.size(12.dp),
                                        )
                                    }

                                    Text(
                                        text = "${periodCount.toPersianNumber()} ${stringResource(R.string.add_study_session_cycle_unit)}",
                                        fontFamily = IranSansFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF1E1548),
                                    )

                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                            .clickable { if (periodCount < 10) periodCount++ }
                                            .testTag("add_session_increment_cycle"),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "افزایش",
                                            tint = Color(0xFF6B4EE8),
                                            modifier = Modifier.size(12.dp),
                                        )
                                    }
                                }
                            }
                        }

                        // Sliders: Study Time (۴۵ د) & Rest Time (۱۵ د)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            // 1. Study Time Column (Right in RTL)
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                                            contentDescription = null,
                                            tint = Color(0xFF78788D),
                                            modifier = Modifier.size(14.dp),
                                        )
                                        Text(
                                            text = stringResource(R.string.add_study_session_study_time_label),
                                            fontFamily = IranSansFontFamily,
                                            fontSize = 11.sp,
                                            color = Color(0xFF78788D),
                                        )
                                    }

                                    Text(
                                        text = "${studyDurationMinutes.toPersianNumber()} ${stringResource(R.string.add_study_session_minute_unit)}",
                                        fontFamily = IranSansFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF6B4EE8),
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                val studySteps = listOf(15, 30, 45, 60, 90)
                                val studyIndex = when {
                                    studyDurationMinutes <= 15 -> 0
                                    studyDurationMinutes <= 30 -> 1
                                    studyDurationMinutes <= 45 -> 2
                                    studyDurationMinutes <= 60 -> 3
                                    else -> 4
                                }

                                ModalDonutStepSlider(
                                    stepCount = 5,
                                    currentStepIndex = studyIndex,
                                    onStepSelected = { idx -> studyDurationMinutes = studySteps[idx] },
                                    stepLabels = listOf("۱۵", "۳۰", "۴۵", "۶۰", "۹۰"),
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }

                            // Vertical Divider between sliders
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(65.dp)
                                    .background(Color(0xFFECE8F6))
                            )

                            // 2. Rest Time Column (Left in RTL)
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_coffee_cup),
                                            contentDescription = null,
                                            tint = Color(0xFF78788D),
                                            modifier = Modifier.size(14.dp),
                                        )
                                        Text(
                                            text = stringResource(R.string.add_study_session_rest_time_label),
                                            fontFamily = IranSansFontFamily,
                                            fontSize = 11.sp,
                                            color = Color(0xFF78788D),
                                        )
                                    }

                                    Text(
                                        text = "${restDurationMinutes.toPersianNumber()} ${stringResource(R.string.add_study_session_minute_unit)}",
                                        fontFamily = IranSansFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF6B4EE8),
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                val restSteps = listOf(5, 10, 15, 20, 30)
                                val restIndex = when {
                                    restDurationMinutes <= 5 -> 0
                                    restDurationMinutes <= 10 -> 1
                                    restDurationMinutes <= 15 -> 2
                                    restDurationMinutes <= 20 -> 3
                                    else -> 4
                                }

                                ModalDonutStepSlider(
                                    stepCount = 5,
                                    currentStepIndex = restIndex,
                                    onStepSelected = { idx -> restDurationMinutes = restSteps[idx] },
                                    stepLabels = listOf("۵", "۱۰", "۱۵", "۲۰", "۳۰"),
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }

                        // --- 5. Information Notice Box ---
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp)),
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFF5F2FF),
                            border = BorderStroke(1.dp, Color(0xFFE4DBFB)),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF6B4EE8)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "!",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        fontFamily = IranSansFontFamily,
                                    )
                                }
                                Text(
                                    text = stringResource(R.string.add_study_session_info_note),
                                    fontFamily = IranSansFontFamily,
                                    fontSize = 11.sp,
                                    color = Color(0xFF5E43E2),
                                    lineHeight = 16.sp,
                                )
                            }
                        }

                        // --- 6. Bottom Action Buttons: انصراف (Cancel) + ثبت جلسه (Submit) ---
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            // Primary Submit Button
                            Surface(
                                modifier = Modifier
                                    .weight(0.65f)
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable {
                                        val selectedSubj = defaultSubjects.firstOrNull { it.id == selectedSubjectId }
                                        val title = selectedSubj?.name ?: "مطالعه"
                                        val topic = chapterBlocks.firstOrNull()?.topics?.firstOrNull { it.id == chapterBlocks.firstOrNull()?.selectedTopicId }?.title
                                            ?: chapterBlocks.firstOrNull()?.chapterTitle
                                            ?: "مطالعه و حل تمرین"
                                        val category = selectedSubj?.category ?: SubjectCategory.GENERAL

                                        onConfirmAdd(title, topic, "۰۸:۳۰", studyDurationMinutes, category)
                                    }
                                    .testTag("add_session_submit_action_button"),
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFF6B4EE8),
                                shadowElevation = 2.dp,
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp),
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = stringResource(R.string.add_study_session_submit_action),
                                        fontFamily = IranSansFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp,
                                        color = Color.White,
                                    )
                                }
                            }

                            // Secondary Cancel Button
                            Surface(
                                modifier = Modifier
                                    .weight(0.35f)
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { onDismiss() }
                                    .testTag("add_session_cancel_action_button"),
                                shape = RoundedCornerShape(14.dp),
                                color = Color.White,
                                border = BorderStroke(1.2.dp, Color(0xFFE2DEF2)),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                ) {
                                    Text(
                                        text = stringResource(R.string.add_study_session_cancel_action),
                                        fontFamily = IranSansFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF78788D),
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun ModalDonutStepSlider(
    stepCount: Int,
    currentStepIndex: Int,
    onStepSelected: (Int) -> Unit,
    stepLabels: List<String>,
    modifier: Modifier = Modifier,
) {
    val activeColor = Color(0xFF6B4EE8)
    val inactiveTrackColor = Color(0xFFE5E5EB)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .clickable {
                        val nextStep = (currentStepIndex + 1) % stepCount
                        onStepSelected(nextStep)
                    },
                contentAlignment = Alignment.CenterStart,
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val thumbRadius = 7.dp.toPx()
                    val thumbBorderWidth = 2.5.dp.toPx()
                    val trackHeight = 3.dp.toPx()
                    val trackY = canvasHeight / 2f
                    val startX = thumbRadius
                    val endX = canvasWidth - thumbRadius
                    val availableWidth = endX - startX
                    val stepFraction = if (stepCount > 1) currentStepIndex.toFloat() / (stepCount - 1) else 0f
                    val thumbCenterX = startX + (availableWidth * stepFraction)

                    // Inactive Track
                    drawLine(
                        color = inactiveTrackColor,
                        start = androidx.compose.ui.geometry.Offset(startX, trackY),
                        end = androidx.compose.ui.geometry.Offset(endX, trackY),
                        strokeWidth = trackHeight,
                        cap = androidx.compose.ui.graphics.StrokeCap.Round,
                    )

                    // Active Track
                    if (currentStepIndex > 0) {
                        drawLine(
                            color = activeColor,
                            start = androidx.compose.ui.geometry.Offset(startX, trackY),
                            end = androidx.compose.ui.geometry.Offset(thumbCenterX, trackY),
                            strokeWidth = trackHeight,
                            cap = androidx.compose.ui.graphics.StrokeCap.Round,
                        )
                    }

                    // Ticks
                    for (i in 0 until stepCount) {
                        val stepX = if (stepCount > 1) startX + (availableWidth * (i.toFloat() / (stepCount - 1))) else canvasWidth / 2f
                        val tickColor = if (i <= currentStepIndex) activeColor else inactiveTrackColor
                        drawLine(
                            color = tickColor,
                            start = androidx.compose.ui.geometry.Offset(stepX, trackY + 3.dp.toPx()),
                            end = androidx.compose.ui.geometry.Offset(stepX, trackY + 7.dp.toPx()),
                            strokeWidth = 1.2.dp.toPx(),
                            cap = androidx.compose.ui.graphics.StrokeCap.Round,
                        )
                    }

                    // Donut Thumb
                    drawCircle(
                        color = Color.White,
                        radius = thumbRadius,
                        center = androidx.compose.ui.geometry.Offset(thumbCenterX, trackY),
                    )
                    drawCircle(
                        color = activeColor,
                        radius = thumbRadius - (thumbBorderWidth / 2f),
                        center = androidx.compose.ui.geometry.Offset(thumbCenterX, trackY),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = thumbBorderWidth),
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                stepLabels.forEachIndexed { idx, label ->
                    Text(
                        text = label,
                        color = if (idx == currentStepIndex) activeColor else Color(0xFF94A3B8),
                        fontFamily = IranSansFontFamily,
                        fontSize = 10.sp,
                        fontWeight = if (idx == currentStepIndex) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.clickable { onStepSelected(idx) },
                    )
                }
            }
        }
    }
}
