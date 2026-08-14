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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(showHeader: Boolean = true) {
    val colors = LocalShetabColors.current
    val coroutineScope = rememberCoroutineScope()

    // 1. Theme and Filter configuration
    var selectedFilter by remember { mutableStateOf("لیگ ابن‌سینا") }
    val filters = listOf("لیگ ابن‌سینا", "جام فیزیک دهم", "مسابقه زیست", "کوهستان طلایی")

    // Dynamic mock list updating depending on the selected filter (UX high response)
    var playersList by remember {
        mutableStateOf(
            listOf(
                LeaderboardUser(1, "رضا محسنی", 1850, false, 2, 14, "فیزیک نوین", 95, 18, "کاشف برتر"),
                LeaderboardUser(2, "غزل موسوی", 1720, false, 1, 5, "ریاضیات", 84, 12, "پژوهشگر جوان"),
                LeaderboardUser(3, "پارسا اکبری", 1680, false, -1, 3, "شیمی فیزیک", 79, 9, "مبتدی شتاب"),
                LeaderboardUser(4, "امیر محمدی", 1540, false, 3, 10, "عربی اختصاصی", 91, 14, "ادیب خلاق"),
                LeaderboardUser(5, "سایه نجاتی", 1490, false, -2, 12, "فلسفه دوازدهم", 88, 11, "فیلسوف نوپا"),
                LeaderboardUser(6, "آرش یوسفی", 1380, false, 0, 7, "زبان عمومی", 89, 13, "سخن‌ور لایت"),
                LeaderboardUser(7, "شما (پویا)", 1350, true, 4, 8, "شیمی آلی", 92, 15, "دانشمند شتاب"),
                LeaderboardUser(8, "رویا همتی", 1290, false, -1, 2, "ادبیات فارسی", 74, 8, "ستاره ادبی"),
                LeaderboardUser(9, "بردیا زارع", 1180, false, 1, 0, "جغرافیا کنکور", 63, 6, "جهانگرد نوپا"),
                LeaderboardUser(10, "نازنین بیاتی", 1020, false, -3, 5, "زیست جانوری", 70, 7, "زیست‌پژوه")
            )
        )
    }

    // Interactive States for Details Overlay Modal
    var selectedUserForDetails by remember { mutableStateOf<LeaderboardUser?>(null) }
    var userXPClaimState by remember { mutableStateOf(1350) } // Interactive update metric
    var activeQuestSolved by remember { mutableStateOf(false) }
    
    var currentLeaderboardTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(selectedFilter) {
        // Change player numbers mock points slightly to demonstrate live reactive sorting UX
        val randomOffset = when (selectedFilter) {
            "جام فیزیک دهم" -> 150
            "مسابقه زیست" -> -200
            "کوهستان طلایی" -> 50
            else -> 0
        }
        playersList = playersList.map {
            it.copy(points = (it.points + randomOffset).coerceAtLeast(100))
        }.sortedByDescending { it.points }.mapIndexed { index, user ->
            user.copy(rank = index + 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(if (showHeader) Modifier.statusBarsPadding() else Modifier)
    ) {
        TabRow(
            selectedTabIndex = currentLeaderboardTab,
            containerColor = Color.Transparent,
            contentColor = colors.accentMain,
            divider = {}
        ) {
            Tab(
                selected = currentLeaderboardTab == 0,
                onClick = { currentLeaderboardTab = 0 },
                text = { Text("برترین‌ها", fontWeight = if (currentLeaderboardTab == 0) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = currentLeaderboardTab == 1,
                onClick = { currentLeaderboardTab = 1 },
                text = { Text("گروه‌های مطالعه 👥", fontWeight = if (currentLeaderboardTab == 1) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = currentLeaderboardTab == 2,
                onClick = { currentLeaderboardTab = 2 },
                text = { Text("مدال‌های من", fontWeight = if (currentLeaderboardTab == 2) FontWeight.Bold else FontWeight.Normal) }
            )
        }
        
        when (currentLeaderboardTab) {
            1 -> StudyGroupsSection()
            2 -> MedalsScreen()
            else -> {
                Box(modifier = Modifier.fillMaxSize().weight(1f)) {
                LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Screen Header Title
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "رتبه‌بندی قهرمانان شتاب",
                            color = colors.primaryText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                        Box(
                            modifier = Modifier
                                .background(colors.accentMain.copy(alpha = 0.1f), CircleShape)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "⚡ سطح ۱۵".toPersianNumber(),
                                    color = colors.accentMain,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Text(
                        text = "با پاسخدهی درست به آزمون‌ها و مرور کارت‌ها، رتبه خود را در لیگ بکشید بالا!",
                        color = colors.secondaryText,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                    )
                }
            }

            // Gamified User Profile Progress Metrics and simulated Quick Quest Complete
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.cardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "👑", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "لیگ طلایی ابن‌سینا",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = colors.primaryText
                                    )
                                    Text(
                                        text = "تراز فعلی شما: ۶,۸۵۰".toPersianNumber(),
                                        fontSize = 11.sp,
                                        color = colors.secondaryText
                                    )
                                }
                            }
                            Text(
                                text = "رتبه ۷".toPersianNumber(),
                                color = colors.accentMain,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .background(colors.accentMain.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Custom Progress bar to next Diamond League
                        Text(
                            text = "پیشرفت تراز شما تا ارتقای جدید (لیگ الماس)".toPersianNumber(),
                            fontSize = 10.sp,
                            color = colors.secondaryText
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(colors.primaryText.copy(alpha = 0.05f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.68f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(colors.accentMain, colors.accentSecondary)
                                        )
                                    )
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "۶,۸۵۰ تراز".toPersianNumber(),
                                fontSize = 9.sp,
                                color = colors.accentMain,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "حد نصاب بعدی: ۸,۰۰۰".toPersianNumber(),
                                fontSize = 9.sp,
                                color = colors.secondaryText
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = colors.primaryText.copy(alpha = 0.04f))
                        Spacer(modifier = Modifier.height(14.dp))

                        // Interactive Simulated Daily Quest (UX gamification engine)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (activeQuestSolved) Color(0xFFE8F5E9) else Color(
                                                0xFFFFF3E0
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = if (activeQuestSolved) "✅" else "🎯", fontSize = 16.sp)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (activeQuestSolved) "کارت حافظه با موفقیت بررسی شد!" else "ماموریت روزانه: تایید مرور روزانه کارت",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = colors.primaryText
                                    )
                                    Text(
                                        text = if (activeQuestSolved) "امتیاز با موفقیت به پروفایلت اضافه شد" else "جایزه تایید: ۱۰۰+ امتیاز فوری به رتبه شما",
                                        fontSize = 9.sp,
                                        color = colors.secondaryText
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    if (!activeQuestSolved) {
                                        activeQuestSolved = true
                                        userXPClaimState += 100
                                        // Update personal model score immediately inside sorting list!
                                        playersList = playersList.map {
                                            if (it.isMe) it.copy(points = it.points + 100) else it
                                        }.sortedByDescending { it.points }.mapIndexed { index, u ->
                                            u.copy(rank = index + 1)
                                        }
                                    }
                                },
                                enabled = !activeQuestSolved,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.accentMain,
                                    disabledContainerColor = Color(0xFF4CAF50).copy(alpha = 0.15f)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text(
                                    text = if (activeQuestSolved) "صد درصد" else "شروع ضربتی",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (activeQuestSolved) Color(0xFF4CAF50) else Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Hot League Filter Chips
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filters.size) { index ->
                        val item = filters[index]
                        val isSelected = item == selectedFilter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) colors.accentMain else colors.cardBg)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) colors.accentMain else colors.primaryText.copy(
                                        alpha = 0.1f
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedFilter = item }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = item,
                                color = if (isSelected) Color.White else colors.secondaryText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Top 3 Podium Cards
            item {
                val topThree = playersList.take(3)
                if (topThree.size >= 3) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.cardBg),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            colors.primaryText.copy(alpha = 0.06f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                PodiumColumn(
                                    user = topThree[1],
                                    height = 55.dp,
                                    avatarColor = Color(0xFFC0C0C0),
                                    crownIcon = "🥈",
                                    onClick = { selectedUserForDetails = topThree[1] }
                                )
                                PodiumColumn(
                                    user = topThree[0],
                                    height = 78.dp,
                                    avatarColor = Color(0xFFFFD700),
                                    crownIcon = "👑",
                                    onClick = { selectedUserForDetails = topThree[0] }
                                )
                                PodiumColumn(
                                    user = topThree[2],
                                    height = 45.dp,
                                    avatarColor = Color(0xFFCD7F32),
                                    crownIcon = "🥉",
                                    onClick = { selectedUserForDetails = topThree[2] }
                                )
                            }
                        }
                    }
                }
            }

            // Milestone rewards roadmap slider (Horizontally scrollable gamification progress)
            item {
                Text(
                    text = "🎁 جاده جوایز و مدال‌های فصلی",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = colors.primaryText,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))

                val rewardsList = listOf(
                    MilestoneReward(1000, "دفترچه تحلیل کنکور دهم", "دسترسی مستقیم پی‌دی‌اف", "📚", true, false),
                    MilestoneReward(1200, "مشاوره صوتی با هوش تجاری", "۱۰ دقیقه مکالمه آنلاین با رایا شتاب", "🎙️", true, false),
                    MilestoneReward(1400, "بسته‌ هدیه اینترنت دانش‌آموزی", "شارژ گیگابایتی اینترنت شاد", "⚡", false, true),
                    MilestoneReward(1600, "کارت طلایی ورود به اتاق نخبگان", "ورودی مسابقات مگا شتاب کشوری", "🎟️", false, false)
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(rewardsList) { r ->
                        Card(
                            modifier = Modifier
                                .width(220.dp)
                                .border(
                                    width = 1.dp,
                                    color = if (r.claimable) colors.accentMain else Color.Transparent,
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (r.isUnlocked) Color(0xFFE8F5E9) else if (r.claimable) colors.cardBg else colors.cardBg.copy(
                                    alpha = 0.85f
                                )
                            )
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = r.icon, fontSize = 22.sp)
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (r.isUnlocked) Color(0xFF2E7D32) else if (r.claimable) colors.accentMain else Color.Gray.copy(
                                                    alpha = 0.4f
                                                ),
                                                RoundedCornerShape(6.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (r.isUnlocked) "دریافت شده" else if (r.claimable) "آماده دریافت" else "قفل شده",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = r.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = colors.primaryText
                                )
                                Text(
                                    text = r.description,
                                    fontSize = 9.sp,
                                    color = colors.secondaryText
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "حداقل تراز: ${r.xpRequired.toString().toPersianNumber()} امتیاز",
                                    fontSize = 8.sp,
                                    color = colors.accentMain,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Dynamic User Listings (excluding top 3 for cleaner layout and separation)
            item {
                Text(
                    text = "رده‌بندی کل رقابت هفتگی",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = colors.primaryText,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            itemsIndexed(playersList.drop(0)) { index, user ->
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.cardBg),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (user.isMe) colors.accentMain.copy(alpha = 0.3f) else colors.primaryText.copy(
                                alpha = 0.06f
                            )
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        LeaderboardItem(
                            user = user,
                            onClick = { selectedUserForDetails = user }
                        )
                    }
                }
            }
        }

        // Expanded Interactive Custom Dialog Modal Details (Premium UI/UX)
        selectedUserForDetails?.let { user ->
            Dialog(onDismissRequest = { selectedUserForDetails = null }) {
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
                        // User Avatar Crown representation
                        Box(contentAlignment = Alignment.BottomCenter) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(colors.accentMain.copy(alpha = 0.1f))
                                    .border(3.dp, colors.accentMain, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = colors.accentMain,
                                    modifier = Modifier.size(44.dp)
                                )
                            }
                            Text(
                                text = "Lvl ${user.level}".toPersianNumber(),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .background(colors.accentMain, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Username and Subtitle/Title
                        Text(
                            text = user.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = colors.primaryText
                        )
                        Text(
                            text = user.title,
                            fontSize = 12.sp,
                            color = colors.secondaryText,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = colors.primaryText.copy(alpha = 0.05f))
                        Spacer(modifier = Modifier.height(16.dp))

                        // High analytics panel mock detailing actual achievements
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "🔥 ${user.streakDays.toString().toPersianNumber()} روز", fontWeight = FontWeight.Black, color = colors.accentMain, fontSize = 14.sp)
                                Text(text = "روز تداوم مطالعه", color = colors.secondaryText, fontSize = 10.sp)
                            }
                            VerticalDividerHex()
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "🎯 ${user.accuracyPercentage.toString().toPersianNumber()}%", fontWeight = FontWeight.Black, color = Color(0xFF4CAF50), fontSize = 14.sp)
                                Text(text = "پاسخ صحیح تجمعی", color = colors.secondaryText, fontSize = 10.sp)
                            }
                            VerticalDividerHex()
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "🎖️ ${user.favoriteSubject}", fontWeight = FontWeight.Black, color = colors.primaryText, fontSize = 14.sp)
                                Text(text = "موضوع موردعلاقه‌", color = colors.secondaryText, fontSize = 10.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Motivational interactive reactive emoji trigger bar
                        Text(
                            text = "براش یه واکنش بفرست تا شتاب بگیره:",
                            fontSize = 11.sp,
                            color = colors.secondaryText,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val cheerOptions = listOf("تلاش عالی! 👏👏", "ماشالله سرعت! ⚡", "سلطان زیست! 🦕", "پرچمت بالا 💪")
                        var sentReactionText by remember { mutableStateOf<String?>(null) }

                        if (sentReactionText == null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                cheerOptions.forEach { opt ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(colors.primaryText.copy(alpha = 0.04f))
                                            .clickable {
                                                sentReactionText = opt
                                            }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = opt,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = colors.primaryText,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp))
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🎉 واکنش شما ارسال شد: ", fontSize = 11.sp, color = Color(0xFF2E7D32))
                                Text(text = sentReactionText!!, fontSize = 11.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { selectedUserForDetails = null },
                            colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("بستن جزئیات رتبه", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
}
}
}
