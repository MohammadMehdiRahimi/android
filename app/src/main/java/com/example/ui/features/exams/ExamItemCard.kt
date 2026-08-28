package com.example.ui.features.exams

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.core.toPersianNumber
import com.example.ui.theme.IranSansFontFamily
import com.example.ui.theme.LocalShetabColors

@Composable
fun ExamItemCard(
    item: ExamListItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalShetabColors.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                color = colors.primaryText.copy(alpha = 0.06f),
                shape = RoundedCornerShape(20.dp)
            )
            .testTag("exam_item_${item.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // === Top Section ===
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Right side in RTL: Subject Icon + Title & Type badge + Topic
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    // Subject Theme Icon Box
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(item.themeColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_clipboard_check),
                            contentDescription = null,
                            tint = item.themeColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = item.subject,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.primaryText,
                                fontFamily = IranSansFontFamily
                            )

                            // Exam Type Badge
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (item.examType == ExamType.MULTIPLE_CHOICE) {
                                    Color(0xFFF3E8FF)
                                } else {
                                    Color(0xFFE6F7ED)
                                }
                            ) {
                                Text(
                                    text = item.examType.title,
                                    color = if (item.examType == ExamType.MULTIPLE_CHOICE) {
                                        Color(0xFF7C3AED)
                                    } else {
                                        Color(0xFF10B981)
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = IranSansFontFamily,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.5.dp)
                                )
                            }
                        }

                        Text(
                            text = item.topic,
                            fontSize = 11.5.sp,
                            color = colors.secondaryText.copy(alpha = 0.85f),
                            fontFamily = IranSansFontFamily,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Left side in RTL: Date + Day of week + Chevron
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = item.date.toPersianNumber(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primaryText,
                            fontFamily = IranSansFontFamily
                        )
                        Text(
                            text = item.dayOfWeek,
                            fontSize = 10.5.sp,
                            color = colors.secondaryText.copy(alpha = 0.7f),
                            fontFamily = IranSansFontFamily
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = null,
                        tint = colors.secondaryText.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(
                color = colors.primaryText.copy(alpha = 0.05f),
                thickness = 0.8.dp
            )
            Spacer(modifier = Modifier.height(10.dp))

            // === Bottom Metrics (3 Columns) ===
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Column 1: Score (نمره)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "نمره",
                        fontSize = 10.5.sp,
                        color = colors.secondaryText.copy(alpha = 0.7f),
                        fontFamily = IranSansFontFamily
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.BarChart,
                            contentDescription = null,
                            tint = Color(0xFF7C3AED),
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = item.score.toPersianNumber(),
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7C3AED),
                            fontFamily = IranSansFontFamily
                        )
                    }
                }

                // Vertical Divider
                Box(
                    modifier = Modifier
                        .height(26.dp)
                        .width(0.8.dp)
                        .background(colors.primaryText.copy(alpha = 0.06f))
                )

                // Column 2: Duration (زمان)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "زمان",
                        fontSize = 10.5.sp,
                        color = colors.secondaryText.copy(alpha = 0.7f),
                        fontFamily = IranSansFontFamily
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = null,
                            tint = colors.secondaryText.copy(alpha = 0.6f),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${item.durationMinutes.toString().toPersianNumber()} دقیقه",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.primaryText,
                            fontFamily = IranSansFontFamily
                        )
                    }
                }

                // Vertical Divider
                Box(
                    modifier = Modifier
                        .height(26.dp)
                        .width(0.8.dp)
                        .background(colors.primaryText.copy(alpha = 0.06f))
                )

                // Column 3: Question Count (تعداد تست)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "تعداد تست",
                        fontSize = 10.5.sp,
                        color = colors.secondaryText.copy(alpha = 0.7f),
                        fontFamily = IranSansFontFamily
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FormatListBulleted,
                            contentDescription = null,
                            tint = colors.secondaryText.copy(alpha = 0.6f),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = item.questionCount.toString().toPersianNumber(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.primaryText,
                            fontFamily = IranSansFontFamily
                        )
                    }
                }
            }
        }
    }
}
