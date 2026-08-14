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

// High-fidelity User Data Model representing student ranking details
data class LeaderboardUser(
    val rank: Int,
    val name: String,
    val points: Int,
    val isMe: Boolean = false,
    val rankChange: Int, // Positive = up, negative = down, 0 = neutral
    val streakDays: Int, // Streak flag
    val favoriteSubject: String,
    val accuracyPercentage: Int,
    val level: Int,
    val title: String,
    val hexColor: String = "#FFB300"
)

// Achievement / Medal representation
data class AcademicMedal(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val unlocked: Boolean,
    val progress: Float, // 0.0 to 1.0f
    val milestoneText: String,
    val rarity: String, // "افسانه‌ای", "کمیاب", "عمومی"
    val rarityColor: Color,
    val globalUsagePercent: Int
)

// Gamified Reward Road-Map Milestone
data class MilestoneReward(
    val xpRequired: Int,
    val title: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean,
    val claimable: Boolean
)

@Composable
fun PodiumColumn(
    user: LeaderboardUser,
    height: Dp,
    avatarColor: Color,
    crownIcon: String,
    onClick: () -> Unit
) {
    val colors = LocalShetabColors.current
    var isHovered by remember { mutableStateOf(false) }
    val scaleAnim by animateFloatAsState(
        targetValue = if (isHovered) 1.08f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "podium_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .padding(bottom = 4.dp)
            .scale(scaleAnim)
            .clickable {
                isHovered = true
                onClick()
            }
    ) {
        LaunchedEffect(isHovered) {
            if (isHovered) {
                delay(300)
                isHovered = false
            }
        }

        Text(text = crownIcon, fontSize = 28.sp)
        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .size(if (user.rank == 1) 64.dp else 52.dp)
                .clip(CircleShape)
                .background(colors.cardBg)
                .border(3.dp, avatarColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(avatarColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = colors.accentMain,
                    modifier = Modifier.size(if (user.rank == 1) 32.dp else 24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = user.name,
            color = colors.primaryText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(84.dp)
        )
        Text(
            text = "${user.points.toString().toPersianNumber()} امتیاز",
            color = colors.secondaryText,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .width(84.dp)
                .height(height),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = colors.accentMain.copy(alpha = if (user.rank == 1) 0.15f else 0.06f)
            ),
            border = if (user.rank == 1) {
                androidx.compose.foundation.BorderStroke(2.dp, colors.accentMain.copy(alpha = 0.4f))
            } else {
                androidx.compose.foundation.BorderStroke(1.dp, colors.primaryText.copy(alpha = 0.08f))
            }
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${user.rank}".toPersianNumber(),
                    fontSize = if (user.rank == 1) 28.sp else 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = colors.accentMain.copy(alpha = 0.85f)
                )
            }
        }
    }
}

@Composable
fun LeaderboardSection() {
    val colors = LocalShetabColors.current
    var timeLeft by remember { mutableLongStateOf(2 * 24 * 3600L + 12 * 3600L + 20 * 60L) }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
    }

    val days = timeLeft / (24 * 3600)
    val hours = (timeLeft % (24 * 3600)) / 3600
    val minutes = (timeLeft % 3600) / 60
    val seconds = timeLeft % 60
    val timeString = "$days روز و $hours:$minutes:$seconds".toPersianNumber()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.enb_sina),
                contentDescription = "Ibn Sina Medal",
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(colors.cardBg)
                    .border(1.dp, colors.primaryText.copy(alpha = 0.08f), CircleShape)
                    .padding(2.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "لیگ برتر ابن‌سینا",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = colors.primaryText
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .background(colors.accentMain.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = timeString,
                            color = colors.accentMain,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_feature_clock),
                        contentDescription = "Time left",
                        modifier = Modifier.size(11.dp),
                        tint = colors.secondaryText
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "زمان باقیمانده تا محاسبه نهایی رتبه‌ها",
                        fontSize = 10.sp,
                        color = colors.secondaryText
                    )
                }
            }
        }

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = colors.cardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, colors.primaryText.copy(alpha = 0.05f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🏆 ۲ تن از قهرمانان این هفته",
                    color = colors.primaryText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val userSilver = LeaderboardUser(
                        rank = 2, name = "غزل موسوی", points = 1720, rankChange = 1,
                        streakDays = 5, favoriteSubject = "ریاضیات", accuracyPercentage = 84, level = 12, title = "پژوهشگر جوان"
                    )
                    val userGold = LeaderboardUser(
                        rank = 1, name = "رضا محسنی", points = 1850, isMe = false, rankChange = 2,
                        streakDays = 14, favoriteSubject = "فیزیک نوین", accuracyPercentage = 95, level = 18, title = "کاشف برتر"
                    )
                    val userBronze = LeaderboardUser(
                        rank = 3, name = "پارسا اکبری", points = 1680, rankChange = -1,
                        streakDays = 3, favoriteSubject = "شیمی فیزیک", accuracyPercentage = 79, level = 9, title = "مبتدی شتاب"
                    )

                    PodiumColumn(
                        user = userSilver,
                        height = 55.dp,
                        avatarColor = Color(0xFFC0C0C0),
                        crownIcon = "🥈",
                        onClick = {}
                    )
                    PodiumColumn(
                        user = userGold,
                        height = 80.dp,
                        avatarColor = Color(0xFFFFD700),
                        crownIcon = "👑",
                        onClick = {}
                    )
                    PodiumColumn(
                        user = userBronze,
                        height = 45.dp,
                        avatarColor = Color(0xFFCD7F32),
                        crownIcon = "🥉",
                        onClick = {}
                    )
                }
            }
        }

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = colors.cardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                LeaderboardItem(
                    user = LeaderboardUser(12, "علی احمدی", 1250, false, 2, 7, "زیست", 82, 10, "کاوشگر"),
                    onClick = {}
                )
                HorizontalDivider(color = colors.bgMain)
                LeaderboardItem(
                    user = LeaderboardUser(13, "سارا رضایی", 1200, false, -1, 4, "شیمی عمومی", 78, 8, "ستاره"),
                    onClick = {}
                )
                HorizontalDivider(color = colors.bgMain)
                LeaderboardItem(
                    user = LeaderboardUser(14, "شما (رتبه شما)", 1150, true, 1, 9, "دین و زندگی", 91, 15, "دانشمند زمان"),
                    onClick = {}
                )
                HorizontalDivider(color = colors.bgMain)
                LeaderboardItem(
                    user = LeaderboardUser(15, "محمد کریمی", 1100, false, 0, 0, "ادبیات", 65, 6, "علاقه‌مند"),
                    onClick = {}
                )
            }
        }
    }
}

@Composable
fun LeaderboardItem(
    user: LeaderboardUser,
    onClick: () -> Unit
) {
    val colors = LocalShetabColors.current
    val bgColor = if (user.isMe) colors.accentMain.copy(alpha = 0.12f) else Color.Transparent
    val textColor = if (user.isMe) colors.accentMain else colors.primaryText
    val fontWeight = if (user.isMe) FontWeight.ExtraBold else FontWeight.Bold

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(64.dp)
        ) {
            Box(
                modifier = Modifier.width(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${user.rank}".toPersianNumber(),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = colors.secondaryText
                )
            }

            Row(
                modifier = Modifier.width(32.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (user.rankChange > 0) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_feature_up),
                        contentDescription = "Up",
                        modifier = Modifier.size(10.dp),
                        tint = Color.Unspecified
                    )
                    Text(
                        text = user.rankChange.toString().toPersianNumber(),
                        fontSize = 9.sp,
                        color = Color(0xFF12EF33),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                } else if (user.rankChange < 0) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_feature_down),
                        contentDescription = "Down",
                        modifier = Modifier.size(10.dp),
                        tint = Color.Unspecified
                    )
                    Text(
                        text = (-user.rankChange).toString().toPersianNumber(),
                        fontSize = 9.sp,
                        color = Color(0xFFF4410A),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                } else {
                    Text(
                        text = "–",
                        fontSize = 11.sp,
                        color = colors.secondaryText.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.weight(1f)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (user.isMe) colors.accentMain.copy(alpha = 0.15f) else colors.cardIconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = if (user.isMe) colors.accentMain else colors.secondaryText,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = user.name,
                        fontWeight = fontWeight,
                        fontSize = 13.sp,
                        color = textColor,
                        textAlign = TextAlign.Start
                    )
                    if (user.streakDays > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🔥", fontSize = 10.sp)
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${user.streakDays} روز متوالی".toPersianNumber(),
                                fontSize = 9.sp,
                                color = colors.accentMain,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Text(
                text = "${user.points.toString().toPersianNumber()} امتیاز",
                fontSize = 12.sp,
                color = colors.secondaryText,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// Full-Page Beautiful Gamified Leaderboard Screen (For selectedTab = 1)

// Auxiliary Divider
@Composable
fun VerticalDividerHex() {
    val colors = LocalShetabColors.current
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(30.dp)
            .background(colors.primaryText.copy(alpha = 0.1f))
    )
}

// ----------------------------------------------------
// Medals Screen UI (For tab Index 2)
// ----------------------------------------------------
