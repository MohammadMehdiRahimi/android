package com.example.ui.features.leaderboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.zIndex
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.core.toPersianNumber
import com.example.ui.theme.IranSansFontFamily

val LeaguePrimaryPurple = Color(0xFF6366F1)
val LeagueDarkNavy = Color(0xFF1E1B4B)
val LeagueSecondaryText = Color(0xFF64748B)
val LeagueCardBorder = Color(0xFFEEF2F6)
val LeagueLightPurpleBg = Color(0xFFF5F3FF)

// Extension to convert Int to Persian string
fun Int.toPersianString(): String = this.toString().toPersianNumber()

// -------------------------------------------------------------
// 1. TOP HEADER BAR
// -------------------------------------------------------------
@Composable
fun LeagueTopHeader(
    onBackClick: () -> Unit,
    onGiftClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Right button in RTL: Back Button
            Surface(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .clickable { onBackClick() },
                shape = CircleShape,
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shadowElevation = 1.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "بازگشت",
                        tint = LeagueDarkNavy,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Center: Title only
            Text(
                text = "لیگ",
                fontFamily = IranSansFontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = LeagueDarkNavy
            )

            // Left button in RTL: Gift Button
            Surface(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .clickable { onGiftClick() },
                shape = CircleShape,
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFEDE9FE)),
                shadowElevation = 1.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.CardGiftcard,
                        contentDescription = "جوایز",
                        tint = LeaguePrimaryPurple,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. HERO LEAGUE CARD (CURRENT USER & GOLD LEAGUE STATUS)
// -------------------------------------------------------------
@Composable
fun HeroLeagueCard(
    userLeagueInfo: CurrentUserLeagueInfo,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp)),
            shape = RoundedCornerShape(26.dp),
            color = Color(0xFFF8FAFC), // Light gray background for whole card
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Top Row: League Info on Right, User Avatar on Left (in RTL)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(2f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Right in RTL: League Title & Season & Golden Laurel Shield
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Golden Laurel Shield Emblem
                        GoldenShieldLaurelBadge(size = 56.dp)

                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = userLeagueInfo.leagueName,
                                fontFamily = IranSansFontFamily,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = LeagueDarkNavy
                            )

                            // Season Badge
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.CalendarMonth,
                                        contentDescription = null,
                                        tint = LeaguePrimaryPurple,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = userLeagueInfo.seasonName,
                                        fontFamily = IranSansFontFamily,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF475569)
                                    )
                                }
                            }
                        }
                    }

                    // Left in RTL: User Avatar with Crown Badge (Larger + White Border + Overlap onto stats)
                    Box(
                        modifier = Modifier
                            .offset(y = 12.dp)
                            .size(90.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Surface(
                            shape = CircleShape,
                            shadowElevation = 3.dp,
                            color = Color.White,
                            modifier = Modifier.size(86.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                val avatarModel = if (userLeagueInfo.avatarUrl.isNotBlank()) {
                                    userLeagueInfo.avatarUrl
                                } else {
                                    "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=300&q=80"
                                }

                                AsyncImage(
                                    model = avatarModel,
                                    contentDescription = userLeagueInfo.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(86.dp)
                                        .clip(CircleShape)
                                        .border(3.5.dp, Color.White, CircleShape)
                                )
                            }
                        }

                        // Crown Badge in circle
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            border = BorderStroke(1.5.dp, Color(0xFFC7D2FE)),
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .size(28.dp)
                                .offset(x = 2.dp, y = 2.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.EmojiEvents,
                                    contentDescription = null,
                                    tint = LeaguePrimaryPurple,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // Middle: 3 Stats Columns (Clean White card on light gray surface)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shadowElevation = 0.5.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. Your Rank (Right in RTL)
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "رتبه شما",
                                fontFamily = IranSansFontFamily,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = LeagueSecondaryText
                            )
                            Text(
                                text = userLeagueInfo.rank.toPersianString(),
                                fontFamily = IranSansFontFamily,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = LeaguePrimaryPurple
                            )
                        }

                        // Divider
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(22.dp)
                                .background(Color(0xFFF1F5F9))
                        )

                        // 2. Your Score (Center)
                        Column(
                            modifier = Modifier.weight(1.1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "امتیاز شما",
                                fontFamily = IranSansFontFamily,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = LeagueSecondaryText
                            )
                            Text(
                                text = userLeagueInfo.score.toPersianString(),
                                fontFamily = IranSansFontFamily,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = LeagueDarkNavy
                            )
                        }

                        // Divider
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(22.dp)
                                .background(Color(0xFFF1F5F9))
                        )

                        // 3. To Next Rank (Left in RTL)
                        Column(
                            modifier = Modifier.weight(1.2f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "تا رتبه بعدی",
                                fontFamily = IranSansFontFamily,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = LeagueSecondaryText
                            )
                            Text(
                                text = "${userLeagueInfo.scoreToNextRank.toPersianString()} امتیاز",
                                fontFamily = IranSansFontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = LeagueDarkNavy
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Progress Bar: Path to Diamond League (Progress fills LTR, smaller height + darker color)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Right in RTL: Diamond League title
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = "مسیر تا ${userLeagueInfo.targetLeagueName}",
                                fontFamily = IranSansFontFamily,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF475569)
                            )
                            DiamondVectorIcon(size = 15.dp, tint = Color(0xFF4338CA))
                        }

                        // Left in RTL: Current / Target Score
                        Text(
                            text = "${userLeagueInfo.currentPointsInTier.toPersianString()} / ${userLeagueInfo.requiredPointsForNextTier.toPersianString()}",
                            fontFamily = IranSansFontFamily,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4338CA)
                        )
                    }

                    // Progress Bar explicitly in LTR (thinner height 5.5.dp, darker rich purple)
                    val progress = (userLeagueInfo.currentPointsInTier.toFloat() / userLeagueInfo.requiredPointsForNextTier.toFloat()).coerceIn(0f, 1f)
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.5.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE2E8F0)),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(progress)
                                    .fillMaxHeight()
                                    .clip(CircleShape)
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                Color(0xFF4338CA),
                                                Color(0xFF3730A3)
                                            )
                                        )
                                    )
                            )
                        }
                    }
                }

                // Promotion Info Pill Banner (Light Gray background, smaller font)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CardGiftcard,
                            contentDescription = null,
                            tint = Color(0xFF4338CA),
                            modifier = Modifier.size(17.dp)
                        )

                        Text(
                            text = userLeagueInfo.promotionNotice,
                            fontFamily = IranSansFontFamily,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF64748B),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp),
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. TOP 3 PODIUM CARD
// -------------------------------------------------------------
@Composable
fun Top3PodiumCard(
    top3Members: List<LeagueMember>,
    modifier: Modifier = Modifier
) {
    if (top3Members.size < 3) return

    val rank1 = top3Members.find { it.rank == 1 } ?: top3Members[0]
    val rank2 = top3Members.find { it.rank == 2 } ?: top3Members[1]
    val rank3 = top3Members.find { it.rank == 3 } ?: top3Members[2]

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp)),
            shape = RoundedCornerShape(26.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
            shadowElevation = 1.5.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 18.dp, start = 12.dp, end = 12.dp)
            ) {
                // Background Confetti Canvas
                ConfettiCanvas(modifier = Modifier.matchParentSize())

                // 3 Podiums in RTL:
                // First in Row (Right): Rank 2 (نگار رحیمی)
                // Second in Row (Center): Rank 1 (سینا قربانی)
                // Third in Row (Left): Rank 3 (پارسا احمدی)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Right in RTL: Rank 2 (نگار رحیمی) - Soft Silver/Slate Light BG
                    PodiumPillar(
                        member = rank2,
                        medalRank = 2,
                        pillarHeight = 64.dp,
                        pillarBg = Color(0xFFF1F5F9), // Light Silver/Slate
                        fallbackAvatar = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=300&q=80",
                        modifier = Modifier.weight(1f)
                    )

                    // Center: Rank 1 (سینا قربانی) - Soft Gold/Yellow Light BG (Taller)
                    PodiumPillar(
                        member = rank1,
                        medalRank = 1,
                        pillarHeight = 88.dp,
                        pillarBg = Color(0xFFFEF9C3), // Light Gold/Yellow
                        fallbackAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&q=80",
                        modifier = Modifier.weight(1.15f)
                    )

                    // Left in RTL: Rank 3 (پارسا احمدی) - Soft Bronze/Peach Light BG
                    PodiumPillar(
                        member = rank3,
                        medalRank = 3,
                        pillarHeight = 64.dp,
                        pillarBg = Color(0xFFFFEDD5), // Light Bronze/Peach
                        fallbackAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300&q=80",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PodiumPillar(
    member: LeagueMember,
    medalRank: Int,
    pillarHeight: Dp,
    pillarBg: Color,
    fallbackAvatar: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Medal at top of Avatar
        Box(
            modifier = Modifier.size(28.dp),
            contentAlignment = Alignment.Center
        ) {
            PodiumMedalBadge(rank = medalRank, size = 26.dp)
        }

        Spacer(modifier = Modifier.height(4.dp))

        // User Mock Avatar with Clean White border and shadow (No colored border)
        Surface(
            shape = CircleShape,
            shadowElevation = 2.dp,
            color = Color.White,
            modifier = Modifier.size(if (medalRank == 1) 58.dp else 50.dp)
        ) {
            val avatarUrl = if (member.avatarUrl.isNotBlank()) member.avatarUrl else fallbackAvatar
            AsyncImage(
                model = avatarUrl,
                contentDescription = member.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Pillar Box with Top-Left and Top-Right radius, no border, light matching background
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(pillarHeight),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = 4.dp,
                bottomEnd = 4.dp
            ),
            color = pillarBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = member.name,
                    fontFamily = IranSansFontFamily,
                    fontSize = if (medalRank == 1) 12.sp else 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = LeagueDarkNavy,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = member.score.toPersianString(),
                    fontFamily = IranSansFontFamily,
                    fontSize = if (medalRank == 1) 14.sp else 12.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF4338CA)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 4. FILTER TABS (CHIPS)
// -------------------------------------------------------------
@Composable
fun LeagueFilterTabs(
    selectedTab: LeagueTab,
    onTabSelected: (LeagueTab) -> Unit,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LeagueTab.values().forEach { tab ->
                val isSelected = selectedTab == tab
                Surface(
                    modifier = Modifier
                        .height(36.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onTabSelected(tab) },
                    shape = RoundedCornerShape(18.dp),
                    color = if (isSelected) Color(0xFF6D28D9) else Color.White,
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) Color(0xFF6D28D9) else Color(0xFFE2E8F0)
                    ),
                    shadowElevation = if (isSelected) 2.dp else 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        val icon: ImageVector = when (tab) {
                            LeagueTab.ALL -> Icons.Default.Groups
                            LeagueTab.FRIENDS -> Icons.Outlined.People
                            LeagueTab.AROUND_ME -> Icons.Outlined.LocationOn
                            LeagueTab.RULES -> Icons.Outlined.Info
                        }

                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) Color.White else Color(0xFF64748B),
                            modifier = Modifier.size(17.dp)
                        )

                        Text(
                            text = tab.title,
                            fontFamily = IranSansFontFamily,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else Color(0xFF475569)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 5. LEAGUE TABLE CARD (جدول لیگ)
// -------------------------------------------------------------
@Composable
fun LeagueTableCard(
    members: List<LeagueMember>,
    onMemberClick: (LeagueMember) -> Unit,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
            shadowElevation = 1.5.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Header of Table
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = LeaguePrimaryPurple,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "جدول لیگ",
                            fontFamily = IranSansFontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = LeagueDarkNavy
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Table Rows
                members.forEachIndexed { index, member ->
                    LeagueMemberRow(
                        member = member,
                        onClick = { onMemberClick(member) }
                    )

                    if (index < members.size - 1) {
                        HorizontalDivider(
                            color = Color(0xFFF8FAFC),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 1.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LeagueMemberRow(
    member: LeagueMember,
    onClick: () -> Unit
) {
    val isCurrent = member.isCurrentUser

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isCurrent) Color(0xFFF5F3FF) else Color.Transparent,
        border = if (isCurrent) BorderStroke(1.dp, Color(0xFFEDE9FE)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Right Section in RTL: Rank, Avatar, Name & Optional "You" Tag
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Rank Number
                Text(
                    text = member.rank.toPersianString(),
                    fontFamily = IranSansFontFamily,
                    fontSize = 12.5.sp,
                    fontWeight = if (isCurrent) FontWeight.Black else FontWeight.Bold,
                    color = if (isCurrent) LeaguePrimaryPurple else Color(0xFF64748B),
                    modifier = Modifier.width(20.dp),
                    textAlign = TextAlign.Center
                )

                // Avatar
                AsyncImage(
                    model = member.avatarUrl,
                    contentDescription = member.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .border(
                            width = if (isCurrent) 1.5.dp else 1.dp,
                            color = if (isCurrent) LeaguePrimaryPurple else Color(0xFFE2E8F0),
                            shape = CircleShape
                        )
                )

                // Name & "شما" Tag
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = member.name,
                        fontFamily = IranSansFontFamily,
                        fontSize = 12.sp,
                        fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.SemiBold,
                        color = if (isCurrent) Color(0xFF4338CA) else Color(0xFF1E293B)
                    )

                    if (isCurrent) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFEDE9FE)
                        ) {
                            Text(
                                text = "شما",
                                fontFamily = IranSansFontFamily,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = LeaguePrimaryPurple,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }

            // Left Section in RTL: Badge Emblem & Score
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Badge Emblem
                when (member.badgeType) {
                    LeagueBadgeType.GOLD_SHIELD -> ShieldBadgeIcon(color = Color(0xFFF59E0B), size = 15.dp)
                    LeagueBadgeType.SILVER_SHIELD -> ShieldBadgeIcon(color = Color(0xFF94A3B8), size = 15.dp)
                    LeagueBadgeType.BRONZE_SHIELD -> ShieldBadgeIcon(color = Color(0xFFD97706), size = 15.dp)
                    LeagueBadgeType.DIAMOND -> DiamondVectorIcon(size = 15.dp, tint = Color(0xFF818CF8))
                }

                // Score + "امتیاز"
                Text(
                    text = "${member.score.toPersianString()} امتیاز",
                    fontFamily = IranSansFontFamily,
                    fontSize = 11.5.sp,
                    fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.Bold,
                    color = if (isCurrent) LeaguePrimaryPurple else Color(0xFF334155)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 6. BOTTOM SEASON PRIZES BANNER
// -------------------------------------------------------------
@Composable
fun BottomSeasonPrizesBanner(
    onPrizesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp)),
            shape = RoundedCornerShape(22.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFEDE9FE)),
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Right in RTL: Trophy Icon & Prompt
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFF5F3FF),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.EmojiEvents,
                                contentDescription = null,
                                tint = LeaguePrimaryPurple,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Text(
                        text = "برای دیدن جوایز فصل و شرایط صعود کلیک کن",
                        fontFamily = IranSansFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF334155)
                    )
                }

                // Left in RTL: "جوایز لیگ" Button
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onPrizesClick() },
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFAF5FF),
                    border = BorderStroke(1.dp, Color(0xFFEDE9FE))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CardGiftcard,
                            contentDescription = null,
                            tint = LeaguePrimaryPurple,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "جوایز لیگ",
                            fontFamily = IranSansFontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = LeaguePrimaryPurple
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// GRAPHICAL VECTOR & CANVAS DRAWING UTILITIES
// -------------------------------------------------------------

@Composable
fun GoldenShieldLaurelBadge(size: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height

            // Outer Laurel leaves arcs
            val laurelColor = Color(0xFFFBBF24)
            val strokeW = 2.5f

            // Golden star background glow
            drawCircle(
                color = Color(0xFFFEF3C7),
                radius = w * 0.44f,
                center = center
            )

            // Shield path
            val shieldPath = Path().apply {
                moveTo(w * 0.5f, h * 0.18f)
                lineTo(w * 0.82f, h * 0.28f)
                lineTo(w * 0.82f, h * 0.58f)
                cubicTo(w * 0.82f, h * 0.78f, w * 0.5f, h * 0.90f, w * 0.5f, h * 0.90f)
                cubicTo(w * 0.5f, h * 0.90f, w * 0.18f, h * 0.78f, w * 0.18f, h * 0.58f)
                lineTo(w * 0.18f, h * 0.28f)
                close()
            }

            drawPath(
                path = shieldPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFBBF24),
                        Color(0xFFD97706)
                    )
                )
            )

            drawPath(
                path = shieldPath,
                color = Color(0xFFB45309),
                style = Stroke(width = 2.5f)
            )

            // Star inside shield
            val starPath = Path().apply {
                val cx = w * 0.5f
                val cy = h * 0.52f
                val outerR = w * 0.16f
                val innerR = w * 0.07f
                for (i in 0 until 5) {
                    val outerAngle = (i * 72 - 90) * (Math.PI / 180.0)
                    val innerAngle = ((i * 72 + 36) - 90) * (Math.PI / 180.0)
                    val x1 = cx + (outerR * Math.cos(outerAngle)).toFloat()
                    val y1 = cy + (outerR * Math.sin(outerAngle)).toFloat()
                    val x2 = cx + (innerR * Math.cos(innerAngle)).toFloat()
                    val y2 = cy + (innerR * Math.sin(innerAngle)).toFloat()
                    if (i == 0) moveTo(x1, y1) else lineTo(x1, y1)
                    lineTo(x2, y2)
                }
                close()
            }

            drawPath(
                path = starPath,
                color = Color.White
            )
        }
    }
}

@Composable
fun PodiumMedalBadge(rank: Int, size: Dp) {
    val medalColor = when (rank) {
        1 -> Color(0xFFF59E0B) // Gold
        2 -> Color(0xFF94A3B8) // Silver
        else -> Color(0xFFD97706) // Bronze
    }

    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height

            // Laurel Ring
            drawCircle(
                color = medalColor.copy(alpha = 0.25f),
                radius = w * 0.5f,
                center = center
            )

            // Inner Medal Circle
            drawCircle(
                color = medalColor,
                radius = w * 0.38f,
                center = center
            )
            drawCircle(
                color = Color.White,
                radius = w * 0.38f,
                center = center,
                style = Stroke(width = 2f)
            )
        }

        Text(
            text = rank.toPersianString(),
            fontFamily = IranSansFontFamily,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
    }
}

@Composable
fun ShieldBadgeIcon(color: Color, size: Dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        val path = Path().apply {
            moveTo(w * 0.5f, 0f)
            lineTo(w, h * 0.22f)
            lineTo(w, h * 0.62f)
            cubicTo(w, h * 0.88f, w * 0.5f, h, w * 0.5f, h)
            cubicTo(w * 0.5f, h, 0f, h * 0.88f, 0f, h * 0.62f)
            lineTo(0f, h * 0.22f)
            close()
        }

        drawPath(path = path, color = color.copy(alpha = 0.25f))
        drawPath(path = path, color = color, style = Stroke(width = 2f))

        // Center dot/star
        drawCircle(
            color = color,
            radius = w * 0.16f,
            center = Offset(w * 0.5f, h * 0.5f)
        )
    }
}

@Composable
fun DiamondVectorIcon(size: Dp, tint: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        val path = Path().apply {
            moveTo(w * 0.22f, h * 0.15f)
            lineTo(w * 0.78f, h * 0.15f)
            lineTo(w, h * 0.45f)
            lineTo(w * 0.5f, h * 0.95f)
            lineTo(0f, h * 0.45f)
            close()
        }

        drawPath(path = path, color = tint.copy(alpha = 0.25f))
        drawPath(path = path, color = tint, style = Stroke(width = 2.2f))

        // Inner facets
        drawLine(
            color = tint,
            start = Offset(w * 0.22f, h * 0.15f),
            end = Offset(w * 0.5f, h * 0.95f),
            strokeWidth = 1.2f
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.78f, h * 0.15f),
            end = Offset(w * 0.5f, h * 0.95f),
            strokeWidth = 1.2f
        )
        drawLine(
            color = tint,
            start = Offset(0f, h * 0.45f),
            end = Offset(w, h * 0.45f),
            strokeWidth = 1.2f
        )
    }
}

@Composable
fun ConfettiCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val colors = listOf(
            Color(0xFFFDE047),
            Color(0xFFC4B5FD),
            Color(0xFF93C5FD),
            Color(0xFFFCA5A5),
            Color(0xFFA7F3D0)
        )

        // Draw festive confetti particles
        drawRect(
            color = colors[0],
            topLeft = Offset(size.width * 0.12f, size.height * 0.22f),
            size = Size(6f, 6f)
        )
        drawRect(
            color = colors[1],
            topLeft = Offset(size.width * 0.28f, size.height * 0.15f),
            size = Size(8f, 5f)
        )
        drawCircle(
            color = colors[2],
            radius = 3.5f,
            center = Offset(size.width * 0.42f, size.height * 0.18f)
        )
        drawRect(
            color = colors[3],
            topLeft = Offset(size.width * 0.62f, size.height * 0.16f),
            size = Size(7f, 7f)
        )
        drawRect(
            color = colors[4],
            topLeft = Offset(size.width * 0.78f, size.height * 0.24f),
            size = Size(6f, 8f)
        )
        drawCircle(
            color = colors[1],
            radius = 4f,
            center = Offset(size.width * 0.88f, size.height * 0.18f)
        )
    }
}

// -------------------------------------------------------------
// DIALOGS: LEAGUE RULES & SEASON PRIZES
// -------------------------------------------------------------
@Composable
fun LeagueRulesDialog(
    onDismiss: () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = LeaguePrimaryPurple),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("متوجه شدم", fontFamily = IranSansFontFamily, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Outlined.Info, contentDescription = null, tint = LeaguePrimaryPurple)
                    Text("قوانین لیگ‌های رقابتی", fontFamily = IranSansFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "۱. امتیازها بر اساس زمان تمرکز، حل تست و آزمون‌های روزانه محاسبه می‌شوند.",
                        fontFamily = IranSansFontFamily,
                        fontSize = 13.sp,
                        color = Color(0xFF334155)
                    )
                    Text(
                        "۲. لیگ‌ها در پایان هر ماه بازنشانی شده و ۳ نفر برتر صعود خواهند کرد.",
                        fontFamily = IranSansFontFamily,
                        fontSize = 13.sp,
                        color = Color(0xFF334155)
                    )
                    Text(
                        "۳. حفظ استمرار و زنجیره مطالعه ضریب امتیاز شما را تا ۱.۵ برابر افزایش می‌دهد.",
                        fontFamily = IranSansFontFamily,
                        fontSize = 13.sp,
                        color = Color(0xFF334155)
                    )
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }
}

@Composable
fun SeasonPrizesDialog(
    onDismiss: () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = LeaguePrimaryPurple),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("بستن", fontFamily = IranSansFontFamily, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Outlined.EmojiEvents, contentDescription = null, tint = Color(0xFFF59E0B))
                    Text("جوایز فصل شهریور", fontFamily = IranSansFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFEF9C3),
                        border = BorderStroke(1.dp, Color(0xFFFEF08A))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🥇", fontSize = 18.sp)
                            Text("نفر اول: ۱ ماه اشتراک شتاب پلاس + نشان طلایی لیگ", fontFamily = IranSansFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF713F12))
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF1F5F9),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🥈", fontSize = 18.sp)
                            Text("نفر دوم: ۲ هفته اشتراک شتاب پلاس + صعود به الماس", fontFamily = IranSansFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFEDD5),
                        border = BorderStroke(1.dp, Color(0xFFFED7AA))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🥉", fontSize = 18.sp)
                            Text("نفر سوم: ۱ هفته اشتراک شتاب پلاس + ۵۰۰ سکه شتاب", fontFamily = IranSansFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7C2D12))
                        }
                    }
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }
}
