package com.example.ui.features.exams

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.core.toPersianNumber
import com.example.ui.theme.IranSansFontFamily
import com.example.ui.theme.LocalShetabColors

@Composable
fun ExamFilterChipsRow(
    selectedDate: String?,
    selectedSubject: String?,
    selectedTopic: String?,
    onOpenFilter: (FilterType) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalShetabColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Date Filter Chip (Rightmost in RTL)
        ExamFilterChipItem(
            title = selectedDate?.toPersianNumber() ?: "همه تاریخ‌ها",
            icon = Icons.Outlined.DateRange,
            isActive = selectedDate != null,
            onClick = { onOpenFilter(FilterType.DATE) },
            modifier = Modifier
                .weight(1.1f)
                .testTag("filter_chip_date")
        )

        // Subject Filter Chip (Middle in RTL)
        ExamFilterChipItem(
            title = selectedSubject ?: "همه درس‌ها",
            icon = Icons.Outlined.MenuBook,
            isActive = selectedSubject != null,
            onClick = { onOpenFilter(FilterType.SUBJECT) },
            modifier = Modifier
                .weight(1f)
                .testTag("filter_chip_subject")
        )

        // Topic Filter Chip (Leftmost in RTL)
        ExamFilterChipItem(
            title = selectedTopic ?: "همه مباحث",
            icon = Icons.Outlined.Layers,
            isActive = selectedTopic != null,
            onClick = { onOpenFilter(FilterType.TOPIC) },
            modifier = Modifier
                .weight(1f)
                .testTag("filter_chip_topic")
        )
    }
}

@Composable
fun ExamFilterChipItem(
    title: String,
    icon: ImageVector,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalShetabColors.current
    val borderColor = if (isActive) Color(0xFF7C3AED) else colors.primaryText.copy(alpha = 0.08f)
    val contentColor = if (isActive) Color(0xFF7C3AED) else colors.primaryText.copy(alpha = 0.85f)
    val bgColor = if (isActive) Color(0xFFF3E8FF) else Color.White

    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = contentColor.copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp)
            )

            Text(
                text = title,
                fontSize = 11.5.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                color = contentColor,
                fontFamily = IranSansFontFamily,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(horizontal = 4.dp)
            )

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor.copy(alpha = 0.75f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
