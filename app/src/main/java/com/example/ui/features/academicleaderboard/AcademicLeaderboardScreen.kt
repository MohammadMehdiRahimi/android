package com.example.ui.features.academicleaderboard

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.navigation.NavController
import com.example.ui.core.components.AppBackground
import com.example.ui.core.toPersianNumber
import com.example.ui.features.leaderboard.StudyGroupsSection
import com.example.ui.theme.LocalShetabColors

data class LeaderboardUser(
    val rank: Int,
    val name: String,
    val studyHours: Float,
    val avatarEmoji: String,
    val isCurrentUser: Boolean = false,
    val tier: String = "الماس",
    val badge: String = "🔥 7 روز"
)

data class SubjectStudyInfo(
    val name: String,
    var hours: Float,
    val targetHours: Float,
    val color: Color,
    val icon: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademicLeaderboardScreen(navController: NavController) {
    val colors = LocalShetabColors.current
    var selectedViewMode by remember { mutableIntStateOf(0) } // 0: All Dashboard, 1: Academic Report, 2: Leaderboard
    var selectedScopeFilter by remember { mutableStateOf("کشوری") } // "کشوری", "پایه دهم", "گروه مطالعه"
    var showAddLogSheet by remember { mutableStateOf(false) }

    // Subjects study state
    var subjectsList by remember {
        mutableStateOf(
            listOf(
                SubjectStudyInfo("زیست‌شناسی", 12f, 14f, Color(0xFF10B981), "🧬"),
                SubjectStudyInfo("فیزیک", 8f, 10f, Color(0xFF3B82F6), "⚛️"),
                SubjectStudyInfo("شیمی", 7f, 8f, Color(0xFFF59E0B), "🧪"),
                SubjectStudyInfo("ریاضیات", 7f, 8f, Color(0xFFEC4899), "📐"),
                SubjectStudyInfo("دروس عمومی", 4f, 6f, Color(0xFF8B5CF6), "📖")
            )
        )
    }

    val totalHours = subjectsList.sumOf { it.hours.toDouble() }.toFloat()
    val totalTarget = 40f
    val progress = (totalHours / totalTarget).coerceIn(0f, 1f)

    // Leaderboard list
    val topStudents = listOf(
        LeaderboardUser(1, "امیرحسین زارع", 46.5f, "👨‍🎓", tier = "افسانه‌ای", badge = "👑 صدرنشین"),
        LeaderboardUser(2, "مریم صفری", 42.0f, "👩‍🎓", tier = "الماس", badge = "🔥 12 روز"),
        LeaderboardUser(3, "علی رضاپور", 40.5f, "👨‍💻", tier = "الماس", badge = "🎯 98% دقت"),
        LeaderboardUser(4, "پارسا (شما)", totalHours, "🚀", isCurrentUser = true, tier = "الماس", badge = "⚡ پیشتاز"),
        LeaderboardUser(5, "سارا امیری", 36.0f, "👩‍🔬", tier = "طلا", badge = "📚 منظم"),
        LeaderboardUser(6, "محمد حسینی", 34.5f, "👨‍🎓", tier = "طلا", badge = "🔥 5 روز"),
        LeaderboardUser(7, "زهرا کاظمی", 32.0f, "👩‍🏫", tier = "نقره", badge = "💪 پرتلاش")
    )

    Box(modifier = Modifier.fillMaxSize()) {
        AppBackground()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // A. Header Title & Level Badge
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "تحلیل تحصیلی و رتبه‌بندی 📊",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = colors.primaryText
                            )
                            Text(
                                text = "پایه دهم تجربی • هفته جاری",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = colors.secondaryText
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = colors.accentMain.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, colors.accentMain.copy(alpha = 0.25f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("💎", fontSize = 12.sp)
                                Text(
                                    text = "سطح: الماس",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.accentMain
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // B. Top Tab Switcher Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colors.cardBg.copy(alpha = 0.8f), RoundedCornerShape(100.dp))
                            .border(1.dp, colors.primaryText.copy(alpha = 0.08f), RoundedCornerShape(100.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val tabs = listOf("داشبورد 🌟", "گزارش 📊", "رتبه‌بندی 🏆", "گروه‌ها 👥")
                        tabs.forEachIndexed { index, label ->
                            val selected = selectedViewMode == index
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(if (selected) colors.accentMain else Color.Transparent)
                                    .clickable { selectedViewMode = index }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (selected) FontWeight.Black else FontWeight.Bold,
                                    color = if (selected) Color.White else colors.secondaryText,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // Tab 0: Dashboard - Hero Score & Rank Card
            if (selectedViewMode == 0) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp)
                    ) {
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = colors.cardBg.copy(alpha = 0.9f)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, colors.accentMain.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "تراز تخمینی کنکور",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = colors.secondaryText
                                        )
                                        Text(
                                            text = "۷,۲۵۰".toPersianNumber(),
                                            fontSize = 26.sp,
                                            fontWeight = FontWeight.Black,
                                            color = colors.accentMain
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = Color(0xFF10B981).copy(alpha = 0.12f)
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = "موقعیت شما",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF10B981)
                                            )
                                            Text(
                                                text = "رتبه ۴ کشوری".toPersianNumber(),
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFF10B981)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Hours Progress Bar
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "پیشرفت هدف هفتگی (${(progress * 100).toInt().toString().toPersianNumber()}٪)",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.primaryText
                                    )
                                    Text(
                                        text = "${totalHours.toString().toPersianNumber()} از ${totalTarget.toInt().toString().toPersianNumber()} ساعت",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.accentMain
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                ClipProgress(progress = progress, accentColor = colors.accentMain)

                                Spacer(modifier = Modifier.height(14.dp))

                                // Metrics Badges Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    MetricBadgeItem(icon = "🔥", label = "استمرار", value = "۷ روز", colors = colors)
                                    MetricBadgeItem(icon = "📈", label = "تراز هفته قبل", value = "+۳۵۰", colors = colors)
                                    MetricBadgeItem(icon = "🎯", label = "دقت مطالعه", value = "۹۴٪", colors = colors)
                                }
                            }
                        }
                    }
                }
            }

            // Tab 3: Study Groups Section
            if (selectedViewMode == 3) {
                item {
                    StudyGroupsSection()
                }
            }

            // Tab 2: Top 3 Podium & Full Leaderboard (Shown only in Leaderboard tab)
            if (selectedViewMode == 2) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "👑 سکوی افتخار برترین‌های کشوری",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = colors.primaryText,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            // Rank 2 (Silver)
                            PodiumCard(
                                user = topStudents[1],
                                crown = "🥈",
                                heightDp = 130,
                                accentColor = Color(0xFF94A3B8),
                                modifier = Modifier.weight(1f),
                                colors = colors
                            )

                            // Rank 1 (Gold)
                            PodiumCard(
                                user = topStudents[0],
                                crown = "🥇",
                                heightDp = 155,
                                accentColor = Color(0xFFF59E0B),
                                modifier = Modifier.weight(1.1f),
                                colors = colors
                            )

                            // Rank 3 (Bronze)
                            PodiumCard(
                                user = topStudents[2],
                                crown = "🥉",
                                heightDp = 115,
                                accentColor = Color(0xFFD97706),
                                modifier = Modifier.weight(1f),
                                colors = colors
                            )
                        }
                    }
                }
            }

            // Tab 1: Academic Report & Subject Distribution (Shown only in Report tab)
            if (selectedViewMode == 1) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📚 تفکیک ساعات مطالعه دروس",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = colors.primaryText
                            )

                            Button(
                                onClick = { showAddLogSheet = true },
                                colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(100.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ثبت مطالعه", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = colors.cardBg.copy(alpha = 0.85f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                subjectsList.forEach { subject ->
                                    val subjectProgress = (subject.hours / subject.targetHours).coerceIn(0f, 1f)
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Text(subject.icon, fontSize = 14.sp)
                                                Text(
                                                    text = subject.name,
                                                    fontSize = 12.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = colors.primaryText
                                                )
                                            }

                                            Text(
                                                text = "${subject.hours.toString().toPersianNumber()} از ${subject.targetHours.toInt().toString().toPersianNumber()} ساعت",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = colors.secondaryText
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        ClipProgress(progress = subjectProgress, accentColor = subject.color, heightDp = 6)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Tab 2: Full Leaderboard Table
            if (selectedViewMode == 2) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🏆 جدول کل رتبه‌بندی",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = colors.primaryText
                            )

                            // Scope Filter Pills
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf("کشوری", "پایه دهم").forEach { filter ->
                                    val isSelected = filter == selectedScopeFilter
                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        color = if (isSelected) colors.accentMain.copy(alpha = 0.15f) else Color.Transparent,
                                        border = BorderStroke(1.dp, if (isSelected) colors.accentMain else colors.primaryText.copy(alpha = 0.1f)),
                                        modifier = Modifier.clickable { selectedScopeFilter = filter }
                                    ) {
                                        Text(
                                            text = filter,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) colors.accentMain else colors.secondaryText,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = colors.cardBg.copy(alpha = 0.85f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                topStudents.forEach { student ->
                                    LeaderboardRowItem(student = student, colors = colors)
                                }
                            }
                        }
                    }
                }
            }

            // Tab 0: Badges & Achievements in Dashboard
            if (selectedViewMode == 0) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "🎖️ مدال‌ها و افتخارات هفته",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = colors.primaryText,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            items(
                                listOf(
                                    Triple("🔥", "استمرار طلایی", "۷ روز ثبت متوالی"),
                                    Triple("🧬", "غول زیست", "۱۲ ساعت مطالعه"),
                                    Triple("⚡", "تندخوان برتر", "بازدهی ۹۵٪"),
                                    Triple("🌙", "شب‌زنده‌دار", "مطالعه قبل نیمه‌شب")
                                )
                            ) { (emoji, title, desc) ->
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = colors.cardBg.copy(alpha = 0.7f),
                                    border = BorderStroke(1.dp, colors.accentMain.copy(alpha = 0.15f)),
                                    modifier = Modifier.width(130.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(emoji, fontSize = 24.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(title, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = colors.primaryText)
                                        Text(desc, fontSize = 9.5.sp, color = colors.secondaryText, textAlign = TextAlign.Center)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Sheet for Logging Study Hours
    if (showAddLogSheet) {
        var selectedSubjectName by remember { mutableStateOf("زیست‌شناسی") }
        var inputHoursText by remember { mutableStateOf("1.5") }

        ModalBottomSheet(
            onDismissRequest = { showAddLogSheet = false },
            containerColor = colors.cardBg,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "⏱️ ثبت ساعت مطالعه جدید",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = colors.primaryText
                )

                Text(
                    text = "درس مورد نظر را انتخاب کرده و میزان مطالعه امروز را ثبت کنید:",
                    fontSize = 12.sp,
                    color = colors.secondaryText
                )

                // Select subject chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(subjectsList) { subj ->
                        val selected = subj.name == selectedSubjectName
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = if (selected) colors.accentMain else colors.cardBg,
                            border = BorderStroke(1.dp, if (selected) colors.accentMain else colors.primaryText.copy(alpha = 0.15f)),
                            modifier = Modifier.clickable { selectedSubjectName = subj.name }
                        ) {
                            Text(
                                text = "${subj.icon} ${subj.name}",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) Color.White else colors.primaryText,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = inputHoursText,
                    onValueChange = { inputHoursText = it },
                    label = { Text("میزان مطالعه (ساعت)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )

                Button(
                    onClick = {
                        val addedHours = inputHoursText.toFloatOrNull() ?: 1f
                        subjectsList = subjectsList.map { subj ->
                            if (subj.name == selectedSubjectName) {
                                subj.copy(hours = subj.hours + addedHours)
                            } else subj
                        }
                        showAddLogSheet = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("ذخیره و به روز رسانی تراز", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun MetricBadgeItem(icon: String, label: String, value: String, colors: com.example.ui.theme.ShetabColorPalette) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(icon, fontSize = 12.sp)
            Text(label, fontSize = 10.sp, color = colors.secondaryText, fontWeight = FontWeight.Medium)
        }
        Text(value.toPersianNumber(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colors.primaryText)
    }
}

@Composable
fun PodiumCard(
    user: LeaderboardUser,
    crown: String,
    heightDp: Int,
    accentColor: Color,
    modifier: Modifier = Modifier,
    colors: com.example.ui.theme.ShetabColorPalette
) {
    Card(
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 12.dp, bottomEnd = 12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBg.copy(alpha = 0.9f)),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f)),
        modifier = modifier.height(heightDp.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(crown, fontSize = 20.sp)

            Text(
                text = user.avatarEmoji,
                fontSize = 22.sp
            )

            Text(
                text = user.name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = colors.primaryText,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Surface(
                shape = RoundedCornerShape(100.dp),
                color = accentColor.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "${user.studyHours.toString().toPersianNumber()} ساعت",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Black,
                    color = accentColor,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun LeaderboardRowItem(student: LeaderboardUser, colors: com.example.ui.theme.ShetabColorPalette) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (student.isCurrentUser) colors.accentMain.copy(alpha = 0.12f) else Color.Transparent)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            // Rank Number
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        when (student.rank) {
                            1 -> Color(0xFFF59E0B)
                            2 -> Color(0xFF94A3B8)
                            3 -> Color(0xFFD97706)
                            else -> colors.primaryText.copy(alpha = 0.08f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = student.rank.toString().toPersianNumber(),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Black,
                    color = if (student.rank <= 3) Color.White else colors.primaryText
                )
            }

            Text(student.avatarEmoji, fontSize = 18.sp)

            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = student.name,
                        fontSize = 12.5.sp,
                        fontWeight = if (student.isCurrentUser) FontWeight.Black else FontWeight.Bold,
                        color = colors.primaryText
                    )
                    if (student.isCurrentUser) {
                        Surface(shape = RoundedCornerShape(100.dp), color = colors.accentMain) {
                            Text("شما", fontSize = 9.sp, color = Color.White, modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp))
                        }
                    }
                }

                Text(
                    text = "${student.badge} • ${student.tier}",
                    fontSize = 10.sp,
                    color = colors.secondaryText
                )
            }
        }

        Text(
            text = "${student.studyHours.toString().toPersianNumber()} ساعت",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = colors.accentMain
        )
    }
}

@Composable
fun ClipProgress(progress: Float, accentColor: Color, heightDp: Int = 8) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(heightDp.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(accentColor.copy(alpha = 0.15f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction = progress.coerceIn(0.02f, 1f))
                .clip(RoundedCornerShape(100.dp))
                .background(accentColor)
        )
    }
}
