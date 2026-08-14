package com.example.ui.features.leaderboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.ui.core.toPersianNumber
import com.example.ui.theme.LocalShetabColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MedalsScreen() {
    val colors = LocalShetabColors.current

    val academicMedalsList = listOf(
        AcademicMedal("1", "حکیم ریاضی", "ثبت ۳ آزمون پی‌در‌پی بالای ۸۵ درصد درس ریاضی", "📐", true, 1.0f, "تکمیل شده", "افسانه‌ای", Color(0xFFFFB300), 5),
        AcademicMedal("2", "سرعت نور", "حل آزمون جامع فیزیک دهم در کمتر از نصف زمان مجاز", "⚡", true, 1.0f, "تکمیل شده", "کمیاب", Color(0xFFE040FB), 12),
        AcademicMedal("3", "شب مانی", "مرور ۲۰ کارت حافظه بعد از نیمه شب به مدت ۵ روز", "🦉", false, 0.8f, "۴ روز از ۵ روز", "عمومی", Color(0xFF2196F3), 40),
        AcademicMedal("4", "دیکتاتور تست", "پاسخ صحیح به بیش از ۵۰۰ تست چندگزینه‌ای در شتاب", "📚", true, 1.0f, "تکمیل شده", "افسانه‌ای", Color(0xFFFFB300), 3),
        AcademicMedal("5", "ذهن آرام", "اتمام مستمر ۱۰ دوره تمرکز ذهن و تنفس رایا", "🧘‍♂️", false, 0.0f, "۰ روز از ۱۰ روز", "عمومی", Color(0xFF2196F3), 60),
        AcademicMedal("6", "سروش شتاب", "دریافت تشویق و واکنش روحیه بخش از ۱۰ دوست هم کلاسی", "📣", true, 1.0f, "تکمیل شده", "کمیاب", Color(0xFFE040FB), 18)
    )

    var selectedMedalForLore by remember { mutableStateOf<AcademicMedal?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Achievement summary Hero box
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "جوایز و افتخارات شتاب",
                    color = colors.primaryText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "با کسب دستاوردهای ارزشمند علمی، تندیس حکیم‌های بزرگ شتاب بگیرید!",
                    color = colors.secondaryText,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.accentMain.copy(alpha = 0.12f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🎖️", fontSize = 44.sp)
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "سطح مجموع مدال‌های شما: ۴ از ۶".toPersianNumber(),
                                fontWeight = FontWeight.Bold,
                                color = colors.accentMain,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "شما جزو ۸ درصد نخبگان با عملکرد برتر شتاب هستید!",
                                color = colors.primaryText.copy(alpha = 0.82f),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Title Section
        item {
            Text(
                text = "کمد مدال‌ها و نشان‌های شما",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = colors.primaryText,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        // Two columns grid items utilizing itemsIndexed
        itemsIndexed(academicMedalsList.windowed(2, 2, true)) { _, rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (medal in rowItems) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedMedalForLore = medal },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (medal.unlocked) colors.cardBg else colors.cardBg.copy(alpha = 0.5f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (medal.unlocked) medal.rarityColor.copy(alpha = 0.25f) else colors.primaryText.copy(alpha = 0.03f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (medal.unlocked) medal.rarityColor.copy(alpha = 0.12f) else Color.Gray.copy(alpha = 0.1f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = medal.icon,
                                    fontSize = 28.sp,
                                    modifier = Modifier.alpha(if (medal.unlocked) 1.0f else 0.45f)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = medal.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (medal.unlocked) colors.primaryText else colors.secondaryText
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Box(
                                modifier = Modifier
                                    .background(medal.rarityColor.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = medal.rarity,
                                    color = medal.rarityColor,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            LinearProgressIndicator(
                                progress = medal.progress,
                                color = medal.rarityColor,
                                trackColor = colors.primaryText.copy(alpha = 0.05f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = medal.milestoneText.toPersianNumber(),
                                fontSize = 8.sp,
                                color = colors.secondaryText,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                if (rowItems.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }

    // Medal lore and requirements description Dialog (Ultra precise responsive design)
    selectedMedalForLore?.let { m ->
        Dialog(onDismissRequest = { selectedMedalForLore = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(16.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBg)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = m.icon, fontSize = 54.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = m.title,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = colors.primaryText
                    )
                    Text(
                        text = "دسته مدال برتر: ${m.rarity}",
                        color = m.rarityColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = colors.primaryText.copy(alpha = 0.05f))
                    Spacer(modifier = Modifier.height(14.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = colors.primaryText.copy(alpha = 0.03f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "شرط باز شدن قفل:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.accentMain
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = m.description,
                                fontSize = 11.sp,
                                color = colors.primaryText,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "رواج جهانی مدال بین کل کاربران:", fontSize = 10.sp, color = colors.secondaryText)
                        Text(text = "${m.globalUsagePercent.toString().toPersianNumber()}% کل شرکت کنندگان", fontSize = 10.sp, color = colors.primaryText, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { selectedMedalForLore = null },
                        colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("متوجه شدم", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
