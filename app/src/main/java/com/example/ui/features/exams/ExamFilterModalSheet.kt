package com.example.ui.features.exams

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.core.toPersianNumber
import com.example.ui.theme.IranSansFontFamily
import com.example.ui.theme.LocalShetabColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamFilterModalSheet(
    filterType: FilterType,
    availableDates: List<String>,
    availableSubjects: List<String>,
    availableTopics: List<String>,
    selectedDate: String?,
    selectedSubject: String?,
    selectedTopic: String?,
    onSelectDate: (String?) -> Unit,
    onSelectSubject: (String?) -> Unit,
    onSelectTopic: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalShetabColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val title = when (filterType) {
        FilterType.DATE -> "فیلتر بر اساس تاریخ"
        FilterType.SUBJECT -> "فیلتر بر اساس درس"
        FilterType.TOPIC -> "فیلتر بر اساس مبحث"
    }

    val items = when (filterType) {
        FilterType.DATE -> availableDates
        FilterType.SUBJECT -> availableSubjects
        FilterType.TOPIC -> availableTopics
    }

    val selectedValue = when (filterType) {
        FilterType.DATE -> selectedDate
        FilterType.SUBJECT -> selectedSubject
        FilterType.TOPIC -> selectedTopic
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.cardBg,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .background(colors.primaryText.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
            )
        },
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primaryText,
                    fontFamily = IranSansFontFamily
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "بستن",
                        tint = colors.secondaryText
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // "All" option (همه)
                item {
                    FilterOptionItem(
                        text = when (filterType) {
                            FilterType.DATE -> "همه تاریخ‌ها"
                            FilterType.SUBJECT -> "همه درس‌ها"
                            FilterType.TOPIC -> "همه مباحث"
                        },
                        isSelected = selectedValue == null,
                        onClick = {
                            when (filterType) {
                                FilterType.DATE -> onSelectDate(null)
                                FilterType.SUBJECT -> onSelectSubject(null)
                                FilterType.TOPIC -> onSelectTopic(null)
                            }
                        }
                    )
                }

                items(items) { option ->
                    val isSelected = option == selectedValue
                    FilterOptionItem(
                        text = if (filterType == FilterType.DATE) option.toPersianNumber() else option,
                        isSelected = isSelected,
                        onClick = {
                            when (filterType) {
                                FilterType.DATE -> onSelectDate(option)
                                FilterType.SUBJECT -> onSelectSubject(option)
                                FilterType.TOPIC -> onSelectTopic(option)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterOptionItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = LocalShetabColors.current
    val bgColor = if (isSelected) Color(0xFFF3E8FF) else colors.bgMain.copy(alpha = 0.5f)
    val borderColor = if (isSelected) Color(0xFF7C3AED) else colors.primaryText.copy(alpha = 0.06f)
    val textColor = if (isSelected) Color(0xFF7C3AED) else colors.primaryText

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 13.dp)
            .testTag("filter_option_$text")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = textColor,
                fontFamily = IranSansFontFamily
            )

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFF7C3AED),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
