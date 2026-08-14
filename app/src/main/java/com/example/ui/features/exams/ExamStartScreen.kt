package com.example.ui.features.exams

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ShetabColorPalette
import com.example.ui.core.toPersianNumber

@Composable
fun ExamStartScreenContent(
    colors: ShetabColorPalette,
    examTitle: String = "آزمون شبیه‌ساز سریع",
    grade: String = "یازدهم",
    field: String = "علوم تجربی",
    book: String = "زیست‌شناسی",
    questionCount: Int = 10,
    durationMinutes: Int = 15,
    isDescriptive: Boolean = false,
    hasNegativeScore: Boolean = false,
    isPlacementWizardIntro: Boolean = false,
    onStartClick: () -> Unit
) {
    if (isPlacementWizardIntro) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(colors.accentMain.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🤖", fontSize = 54.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "تعیین سطح علمی با رایا",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = colors.primaryText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "برای مشخص شدن سطحت، یه آزمون ساده می‌گیرم ازت تا بتونم برنامه‌ای دقیق و کاملاً متناسب با نیازهات برات آماده کنم.",
                fontSize = 16.sp,
                color = colors.secondaryText,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("زمان پاسخ‌گویی", fontSize = 11.sp, color = colors.secondaryText)
                        Text("۳ دقیقه", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = colors.primaryText)
                    }
                    Box(modifier = Modifier.size(1.dp, 30.dp).background(colors.primaryText.copy(alpha = 0.1f)))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("تعداد سوالات", fontSize = 11.sp, color = colors.secondaryText)
                        Text("۳ سوال تستی", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = colors.primaryText)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(colors.accentMain.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📝", fontSize = 44.sp)
            }

            Text(
                text = examTitle,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = colors.primaryText,
                textAlign = TextAlign.Center
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(colors.accentMain.copy(alpha = 0.08f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Book,
                                contentDescription = null,
                                tint = colors.accentMain,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(text = "درس و مبحث علمی", fontSize = 11.sp, color = colors.secondaryText)
                            Text(text = book, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.primaryText)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(colors.accentMain.copy(alpha = 0.08f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = colors.accentMain,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(text = "پایه و رشته تحصیلی", fontSize = 11.sp, color = colors.secondaryText)
                            Text(text = "$grade $field", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.primaryText)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(colors.accentMain.copy(alpha = 0.08f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ListAlt,
                                contentDescription = null,
                                tint = colors.accentMain,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(text = "تعداد سوالات و نوع سنجش", fontSize = 11.sp, color = colors.secondaryText)
                            val examTypeStr = if (isDescriptive) "تشریحی" else "تستی چهارگزینه‌ای"
                            Text(
                                text = "${questionCount.toString().toPersianNumber()} سوال ($examTypeStr)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.primaryText
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(colors.accentMain.copy(alpha = 0.08f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = colors.accentMain,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(text = "زمان پاسخ‌گویی", fontSize = 11.sp, color = colors.secondaryText)
                            Text(
                                text = "${durationMinutes.toString().toPersianNumber()} دقیقه",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.primaryText
                            )
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.accentMain.copy(alpha = 0.06f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = colors.accentMain,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "راهنمای شرکت در آزمون",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primaryText
                        )
                    }

                    Text(
                        text = "• نمره منفی برای آزمون‌های تستی ${if (hasNegativeScore) "محاسبه می‌شود" else "محاسبه نخواهد شد"}.",
                        fontSize = 12.sp,
                        color = colors.secondaryText,
                        lineHeight = 20.sp
                    )
                    Text(
                        text = "• برای خروج قطعی از دکمه بازگشت در بالای صفحه استفاده کنید.",
                        fontSize = 12.sp,
                        color = colors.secondaryText,
                        lineHeight = 20.sp
                    )
                    Text(
                        text = "• با پایان یافتن زمان آزمون، پاسخ‌های شما به صورت خودکار ذخیره و تصحیح خواهند شد.",
                        fontSize = 12.sp,
                        color = colors.secondaryText,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onStartClick,
                colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "شروع آزمون سنجش",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
