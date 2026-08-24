package com.example.ui.features.studyplan

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import com.example.network.TokenManager
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.network.StudyTaskBookDto
import com.example.network.StudyTaskChapterDto
import com.example.network.StudyTaskDto
import com.example.network.StudyTaskTopicDto
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
val PlanCardBorder = Color(0xFFF0F2F7)

data class SubjectVisualConfig(
    val title: String,
    val gradeOrField: String,
    val icon: ImageVector,
    val iconTint: Color,
    val containerBg: Color,
)

fun getSubjectVisualConfig(subjectName: String, bookName: String? = null): SubjectVisualConfig {
    val text = (subjectName + " " + (bookName ?: "")).lowercase()
    return when {
        text.contains("ریاضی") || text.contains("هندسه") || text.contains("حسابان") || text.contains("آمار") -> SubjectVisualConfig(
            title = "ریاضی",
            gradeOrField = "رشته تجربی",
            icon = Icons.Outlined.MenuBook,
            iconTint = Color(0xFF7656F5),
            containerBg = Color(0xFFF5F3FF),
        )
        text.contains("فیزیک") -> SubjectVisualConfig(
            title = "فیزیک",
            gradeOrField = "رشته تجربی",
            icon = Icons.Outlined.Science,
            iconTint = Color(0xFF2563EB),
            containerBg = Color(0xFFEFF6FF),
        )
        text.contains("شیمی") -> SubjectVisualConfig(
            title = "شیمی",
            gradeOrField = "رشته تجربی",
            icon = Icons.Outlined.Science,
            iconTint = Color(0xFFEA580C),
            containerBg = Color(0xFFFFF7ED),
        )
        text.contains("زیست") -> SubjectVisualConfig(
            title = "زیست‌شناسی",
            gradeOrField = "رشته تجربی",
            icon = Icons.Outlined.Spa,
            iconTint = Color(0xFF16A34A),
            containerBg = Color(0xFFF0FDF4),
        )
        else -> SubjectVisualConfig(
            title = subjectName.ifBlank { "عمومی" },
            gradeOrField = "رشته تجربی",
            icon = Icons.Outlined.MenuBook,
            iconTint = Color(0xFF7656F5),
            containerBg = Color(0xFFF5F3FF),
        )
    }
}

@Composable
fun StudyPlanTopHeader(
    onNotificationClick: () -> Unit = {},
    unreadNotification: Boolean = true,
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val userAvatarUrl = remember { tokenManager.getProfileImageUrl() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Right side in RTL: User Avatar with online status
        Box(
            modifier = Modifier.size(50.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEDE9FE))
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                if (!userAvatarUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = userAvatarUrl,
                        contentDescription = "آواتار کاربر",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "آواتار کاربر",
                        tint = PlanPurple,
                        modifier = Modifier.size(26.dp),
                    )
                }
            }

            // Green online badge at bottom corner
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(15.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF22C55E))
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(9.dp),
                )
            }
        }

        // Center: Title and Subtitle
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = stringResource(id = R.string.study_plan_title),
                color = PlanNavy,
                fontFamily = IranSansFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(id = R.string.study_plan_subtitle),
                color = PlanMuted,
                fontFamily = IranSansFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
            )
        }

        // Left side in RTL: Circular Notification Bell Button
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, PlanCardBorder, CircleShape)
                .clickable { onNotificationClick() }
                .testTag("notification_button"),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "اعلان‌ها",
                tint = PlanNavy,
                modifier = Modifier.size(22.dp),
            )

            if (unreadNotification) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 10.dp, end = 11.dp)
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(PlanPurple),
                )
            }
        }
    }
}

@Composable
fun StudyPlanSummaryCard(
    totalDurationMinutes: Int,
    remainingCount: Int,
    completedCount: Int,
    totalCount: Int,
    progressFraction: Float,
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
            // 4 Matrix Columns Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Column 1 (Rightmost in RTL): Study Duration
                SummaryStatItem(
                    icon = Icons.Outlined.AccessTime,
                    iconTint = PlanPurple,
                    title = stringResource(id = R.string.study_plan_stat_duration),
                    value = "${totalDurationMinutes.toPersianNumber()} دقیقه",
                    modifier = Modifier.weight(1.3f),
                )

                VerticalSummaryDivider()

                // Column 2: Remaining
                SummaryStatItem(
                    icon = Icons.Outlined.AccessTime,
                    iconTint = PlanOrange,
                    title = stringResource(id = R.string.study_plan_stat_remaining),
                    value = remainingCount.toPersianNumber(),
                    modifier = Modifier.weight(0.9f),
                )

                VerticalSummaryDivider()

                // Column 3: Completed
                SummaryStatItem(
                    icon = Icons.Outlined.CheckCircle,
                    iconTint = PlanGreen,
                    title = stringResource(id = R.string.study_plan_stat_completed),
                    value = completedCount.toPersianNumber(),
                    modifier = Modifier.weight(0.9f),
                )

                VerticalSummaryDivider()

                // Column 4: Total Tasks
                SummaryStatItem(
                    icon = Icons.Outlined.BookmarkBorder,
                    iconTint = PlanPurple,
                    title = stringResource(id = R.string.study_plan_stat_total),
                    value = totalCount.toPersianNumber(),
                    modifier = Modifier.weight(1.0f),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar and Label
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "از ${totalCount.toPersianNumber()} تسک انجام شده",
                    color = PlanMuted,
                    fontFamily = IranSansFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                )

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(7.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFECEFF7)),
                ) {
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

@Composable
private fun SummaryStatItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    value: String,
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
        // "همه"
        item(key = "filter_all") {
            FilterChipPill(
                text = stringResource(id = R.string.study_plan_filter_all),
                isSelected = selectedFilter == StudyTaskFilter.ALL,
                onClick = { onFilterSelected(StudyTaskFilter.ALL) },
                testTag = "filter_all",
            )
        }

        // "در حال انجام"
        item(key = "filter_in_progress") {
            FilterChipPill(
                text = stringResource(id = R.string.study_plan_filter_in_progress),
                icon = Icons.Outlined.PlayCircleOutline,
                isSelected = selectedFilter == StudyTaskFilter.IN_PROGRESS,
                onClick = { onFilterSelected(StudyTaskFilter.IN_PROGRESS) },
                testTag = "filter_in_progress",
            )
        }

        // "انجام نشده"
        item(key = "filter_pending") {
            FilterChipPill(
                text = stringResource(id = R.string.study_plan_filter_pending),
                icon = Icons.Outlined.AccessTime,
                isSelected = selectedFilter == StudyTaskFilter.PENDING,
                onClick = { onFilterSelected(StudyTaskFilter.PENDING) },
                testTag = "filter_pending",
            )
        }

        // "انجام شده"
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
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Right: Title + Count Badge
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

        // Left: Sort dropdown button
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onSortClick() }
                .padding(horizontal = 6.dp, vertical = 4.dp)
                .testTag("sort_button"),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = PlanMuted,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = stringResource(id = R.string.study_plan_sort_button),
                color = PlanMuted,
                fontFamily = IranSansFontFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            )
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
        // Right: Title + Green Badge
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

        // Left: See all link
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
    isBookmarked: Boolean,
    onBookmarkToggle: () -> Unit,
    onStartClick: () -> Unit,
    onContinueClick: () -> Unit,
    isBusy: Boolean = false,
) {
    val subjectConfig = getSubjectVisualConfig(
        subjectName = task.book.name,
        bookName = task.title,
    )

    val isInProgress = task.isInProgress

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
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Right side in RTL: Subject Icon & Name Container Box
                SubjectBadgeBox(
                    config = subjectConfig,
                    modifier = Modifier.size(width = 68.dp, height = 72.dp),
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Middle Column: Title, Subtitle, Meta Row
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Text(
                        text = task.title,
                        color = PlanNavy,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Text(
                        text = "${task.chapter.name}: ${task.topic.name}",
                        color = PlanMuted,
                        fontFamily = IranSansFontFamily,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Meta Row: Time, Cycle, Priority Indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        // Duration Text
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

                        // Cycle Text
                        val cycleNumber = task.periodCount.coerceAtLeast(1)
                        Text(
                            text = "دور ${cycleNumber.toPersianNumber()}/۳",
                            color = PlanMuted,
                            fontFamily = IranSansFontFamily,
                            fontSize = 10.5.sp,
                        )

                        // Priority Signal Bars
                        PrioritySignalBars(level = (cycleNumber % 4) + 1)
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Left Column in RTL: Bookmark icon & Action Button
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.height(72.dp),
                ) {
                    // Bookmark icon
                    IconButton(
                        onClick = onBookmarkToggle,
                        modifier = Modifier.size(24.dp),
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "نشان کردن",
                            tint = if (isBookmarked) PlanPurple else Color(0xFF9CA3AF),
                            modifier = Modifier.size(20.dp),
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Action Button (شروع / ادامه)
                    if (isInProgress) {
                        Surface(
                            modifier = Modifier
                                .height(32.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable(enabled = !isBusy) { onContinueClick() }
                                .testTag("continue_button_${task.id}"),
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            border = BorderStroke(1.2.dp, PlanPurple),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Text(
                                    text = stringResource(id = R.string.study_plan_action_continue),
                                    color = PlanPurple,
                                    fontFamily = IranSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp,
                                )
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = null,
                                    tint = PlanPurple,
                                    modifier = Modifier.size(15.dp),
                                )
                            }
                        }
                    } else {
                        Surface(
                            modifier = Modifier
                                .height(32.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable(enabled = !isBusy) { onStartClick() }
                                .testTag("start_button_${task.id}"),
                            shape = RoundedCornerShape(12.dp),
                            color = PlanPurple,
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Text(
                                    text = stringResource(id = R.string.study_plan_action_start),
                                    color = Color.White,
                                    fontFamily = IranSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp,
                                )
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(15.dp),
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Progress Line if In Progress
            if (isInProgress) {
                val fraction = if (task.plannedMinutes > 0) {
                    (task.elapsedMinutes.toFloat() / task.plannedMinutes.toFloat()).coerceIn(0.1f, 1f)
                } else 0.5f

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 2.dp)
                        .height(3.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFECEFF7)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction)
                            .clip(CircleShape)
                            .background(PlanPurple),
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
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
            .shadow(1.dp, RoundedCornerShape(22.dp), spotColor = Color(0x0D000000))
            .testTag("completed_task_card_${task.id}"),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = BorderStroke(1.dp, PlanCardBorder),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Right: Subject Badge
            SubjectBadgeBox(
                config = subjectConfig,
                modifier = Modifier.size(width = 68.dp, height = 72.dp),
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Middle: Title, Chapter, Duration
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text = task.title,
                    color = PlanNavy,
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = null,
                        tint = PlanMuted,
                        modifier = Modifier.size(12.dp),
                    )
                    Text(
                        text = "${task.plannedMinutes.toPersianNumber()} دقیقه",
                        color = PlanMuted,
                        fontFamily = IranSansFontFamily,
                        fontSize = 10.5.sp,
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Left: Green Checkmark Badge "انجام شد"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.study_plan_action_done),
                    color = PlanGreen,
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                )
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = PlanGreen,
                    modifier = Modifier.size(20.dp),
                )
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
                modifier = Modifier.size(22.dp),
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = config.title,
                color = config.iconTint,
                fontFamily = IranSansFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp,
                maxLines = 1,
            )
            Text(
                text = config.gradeOrField,
                color = PlanMuted,
                fontFamily = IranSansFontFamily,
                fontSize = 8.5.sp,
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
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Summary Skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(130.dp)
                .shimmerEffect(RoundedCornerShape(24.dp)),
        )

        // Filter Pills Skeleton
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            repeat(4) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .shimmerEffect(RoundedCornerShape(19.dp)),
                )
            }
        }

        // Task Cards Skeleton
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
                        text = "کتابی برای رشته شما تعریف نشده است.",
                        color = PlanMuted,
                        fontFamily = IranSansFontFamily,
                        fontSize = 12.sp,
                    )
                } else {
                    // Book selector
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

                    // Chapter / Topic
                    if (topics.isNotEmpty()) {
                        Text(text = "مبحث:", color = PlanNavy, fontFamily = IranSansFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${currentChapter?.name ?: ""} - ${currentTopic?.name ?: ""}",
                            color = PlanPurple,
                            fontFamily = IranSansFontFamily,
                            fontSize = 12.sp,
                        )
                    }

                    // Duration selector
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
