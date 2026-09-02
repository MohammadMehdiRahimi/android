package com.example.ui.features.mygroup

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QueryBuilder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.MilitaryTech
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.network.ApiClient
import com.example.network.GroupBadgeDto
import com.example.network.StudyGroupDto
import com.example.ui.theme.IranSansFontFamily
import java.text.NumberFormat
import java.util.Locale

// Minimalist Design System Palette
val GroupPrimaryPurple = Color(0xFF7C3AED)
val GroupLightPurpleBg = Color(0xFFF5F3FF)
val GroupBorderPurple = Color(0xFFDDD6FE)
val GroupTextNavy = Color(0xFF1E293B)
val GroupTextMuted = Color(0xFF64748B)
val GroupTextLight = Color(0xFF94A3B8)
val GroupCardBorder = Color(0xFFE2E8F0)
val GroupBgColor = Color(0xFFF8F9FD)
val GroupGoldAccent = Color(0xFFF59E0B)
val GroupGreenStatusBg = Color(0xFFD1FAE5)
val GroupGreenStatusText = Color(0xFF059669)

fun toPersianDigits(text: Any): String {
    val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    return text.toString().map { ch ->
        if (ch in '0'..'9') persianDigits[ch - '0'] else ch
    }.joinToString("")
}

fun formatPersianNumber(number: Number): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    return toPersianDigits(formatter.format(number))
}

/**
 * Sleek Minimalist Top App Bar (RTL back button & compact typography)
 */
@Composable
fun MyGroupTopBar(
    title: String = stringResource(R.string.my_groups_title),
    onBackClick: () -> Unit,
    onLeaveGroupClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Top Right/Start Back Button
        Surface(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .clickable(onClick = onBackClick)
                .testTag("group_back_button"),
            color = Color.White,
            shape = CircleShape,
            border = androidx.compose.foundation.BorderStroke(1.dp, GroupCardBorder),
            shadowElevation = 1.dp,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "بازگشت",
                    tint = GroupTextNavy,
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        // Title (Clean 16sp font size)
        Text(
            text = title,
            fontFamily = IranSansFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = GroupTextNavy,
            modifier = Modifier.testTag("group_top_title"),
        )

        // Top Left/End Leave Group Button or Balanced Spacer
        if (onLeaveGroupClick != null) {
            Surface(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onLeaveGroupClick)
                    .testTag("group_leave_button"),
                color = Color(0xFFFEF2F2),
                shape = CircleShape,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA)),
                shadowElevation = 1.dp,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = stringResource(R.string.group_leave_title),
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.size(38.dp))
        }
    }
}

/**
 * Confirmation dialog for leaving a study group
 */
@Composable
fun LeaveGroupConfirmDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AlertDialog(
            onDismissRequest = onDismiss,
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            icon = {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEE2E2)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(24.dp),
                    )
                }
            },
            title = {
                Text(
                    text = stringResource(R.string.group_leave_title),
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = GroupTextNavy,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.group_leave_confirm_message),
                    fontFamily = IranSansFontFamily,
                    fontSize = 12.5.sp,
                    color = GroupTextMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("leave_group_confirm_button"),
                ) {
                    Text(
                        text = stringResource(R.string.group_leave_action),
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White,
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GroupCardBorder),
                    modifier = Modifier.testTag("leave_group_cancel_button"),
                ) {
                    Text(
                        text = stringResource(R.string.group_leave_cancel),
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = GroupTextNavy,
                    )
                }
            },
        )
    }
}

/**
 * STATE 1: Empty / Non-Member View (Minimalist & RTL)
 */
@Composable
fun GroupEmptyNonMemberView(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchSubmit: (String) -> Unit,
    activeFilter: GroupSearchFilter,
    onFilterSelect: (GroupSearchFilter) -> Unit,
    onCreateGroupClick: () -> Unit,
    onSearchGroupsClick: () -> Unit,
    searchResults: List<StudyGroupDto>,
    onJoinGroupClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 4.dp, bottom = 24.dp),
    ) {
        // 1. Search Box
        item {
            GroupSearchBox(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                onSearch = { onSearchSubmit(searchQuery) },
            )
        }

        // 2. Filter Chips Row
        item {
            GroupFilterChipsRow(
                activeFilter = activeFilter,
                onFilterSelect = onFilterSelect,
            )
        }

        // 3. Central Empty State Card
        item {
            GroupEmptyHeroCard(
                onCreateGroupClick = onCreateGroupClick,
                onSearchGroupsClick = onSearchGroupsClick,
            )
        }

        // 4. Search Results if available
        if (searchResults.isNotEmpty()) {
            item {
                Text(
                    text = "نتایج جستجو (${toPersianDigits(searchResults.size)})",
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = GroupTextNavy,
                    modifier = Modifier.padding(top = 4.dp, bottom = 2.dp),
                )
            }
            items(searchResults, key = { it.id }) { group ->
                GroupSearchResultCard(
                    group = group,
                    onJoinClick = { onJoinGroupClick(group.id) },
                )
            }
        }
    }
}

/**
 * Search Input Field (RTL aligned, sleek 48dp height)
 */
@Composable
fun GroupSearchBox(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .shadow(1.dp, shape = RoundedCornerShape(25.dp), spotColor = Color(0x08000000)),
        shape = RoundedCornerShape(25.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = {
                    Text(
                        text = stringResource(R.string.group_search_placeholder),
                        fontFamily = IranSansFontFamily,
                        fontSize = 13.5.sp,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Start,
                    )
                },
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = IranSansFontFamily,
                    fontSize = 13.5.sp,
                    color = GroupTextNavy,
                    textAlign = TextAlign.Start,
                ),
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("group_search_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = GroupPrimaryPurple,
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearch() }),
            )

            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "پاک کردن",
                        tint = GroupTextLight,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }

            IconButton(
                onClick = onSearch,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("group_search_action_button"),
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "جستجو",
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}

/**
 * Filter Chips Row: "نام گروه", "# آیدی گروه", "مدال‌ها" (RTL layout)
 */
@Composable
fun GroupFilterChipsRow(
    activeFilter: GroupSearchFilter,
    onFilterSelect: (GroupSearchFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Group Name Chip
        GroupFilterChipItem(
            title = stringResource(R.string.group_filter_name),
            icon = Icons.Default.Assignment,
            isSelected = activeFilter == GroupSearchFilter.GROUP_NAME,
            onClick = { onFilterSelect(GroupSearchFilter.GROUP_NAME) },
            modifier = Modifier.weight(1.1f),
        )

        // Group ID Chip
        GroupFilterChipItem(
            title = stringResource(R.string.group_filter_id),
            icon = Icons.Default.Numbers,
            isSelected = activeFilter == GroupSearchFilter.GROUP_ID,
            onClick = { onFilterSelect(GroupSearchFilter.GROUP_ID) },
            modifier = Modifier.weight(1.1f),
        )

        // Medals Chip
        GroupFilterChipItem(
            title = stringResource(R.string.group_filter_medals),
            icon = Icons.Outlined.MilitaryTech,
            isSelected = activeFilter == GroupSearchFilter.MEDALS,
            onClick = { onFilterSelect(GroupSearchFilter.MEDALS) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
fun GroupFilterChipItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(19.dp))
            .clickable(onClick = onClick)
            .testTag("filter_chip_${title}"),
        shape = RoundedCornerShape(19.dp),
        color = if (isSelected) Color(0xFFF3F0FF) else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.2.dp,
            if (isSelected) GroupPrimaryPurple else Color(0xFFE2E8F0),
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) GroupPrimaryPurple else Color(0xFF64748B),
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = title,
                fontFamily = IranSansFontFamily,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 12.sp,
                color = if (isSelected) GroupPrimaryPurple else GroupTextNavy,
                maxLines = 1,
            )
        }
    }
}

/**
 * Empty Hero Card with no_group_vector illustration matching screenshot
 */
@Composable
fun GroupEmptyHeroCard(
    onCreateGroupClick: () -> Unit,
    onSearchGroupsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, shape = RoundedCornerShape(28.dp), spotColor = Color(0x10000000)),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // 3D Vector Illustration
            Image(
                painter = painterResource(R.drawable.no_group_vector),
                contentDescription = "هنوز عضو هیچ گروهی نیستی",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                contentScale = ContentScale.Fit,
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Headline
            Text(
                text = stringResource(R.string.group_empty_title),
                fontFamily = IranSansFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.5.sp,
                color = Color(0xFF1E1B4B),
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("group_empty_headline"),
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = stringResource(R.string.group_empty_subtitle),
                fontFamily = IranSansFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 1. Primary CTA: "ساخت گروه جدید"
            Button(
                onClick = onCreateGroupClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("create_new_group_button"),
                colors = ButtonDefaults.buttonColors(containerColor = GroupPrimaryPurple),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = stringResource(R.string.group_create_new),
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = GroupPrimaryPurple,
                            modifier = Modifier.size(15.dp),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Secondary CTA: "جستجوی گروه‌ها"
            OutlinedButton(
                onClick = onSearchGroupsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("search_groups_cta_button"),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, GroupPrimaryPurple),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = GroupPrimaryPurple,
                ),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = stringResource(R.string.group_search_action),
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = GroupPrimaryPurple,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = GroupPrimaryPurple,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

/**
 * Search Result Item Card
 */
@Composable
fun GroupSearchResultCard(
    group: StudyGroupDto,
    onJoinClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(1.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, GroupCardBorder),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val imgUrl = ApiClient.resolveUrl(group.profileImageUrl)
            if (imgUrl != null) {
                AsyncImage(
                    model = imgUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(GroupLightPurpleBg),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        tint = GroupPrimaryPurple,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = group.name,
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    color = GroupTextNavy,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "کد: ${toPersianDigits(group.inviteCode)} • ${if (group.isPublic) "عمومی" else "خصوصی"}",
                    fontFamily = IranSansFontFamily,
                    fontSize = 11.sp,
                    color = GroupTextMuted,
                )
            }

            Button(
                onClick = onJoinClick,
                colors = ButtonDefaults.buttonColors(containerColor = GroupPrimaryPurple),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.height(34.dp),
            ) {
                Text(
                    text = if (group.isPublic) "عضویت" else "درخواست",
                    fontFamily = IranSansFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
        }
    }
}

// -------------------------------------------------------------
// STATE 2: Active Member View (Minimalist & RTL Refined)
// -------------------------------------------------------------

@Composable
fun GroupActiveMemberView(
    headerData: GroupHeaderData,
    battleData: GroupBattleData?,
    personalStats: PersonalGroupStats,
    selectedTab: GroupTab,
    onTabSelect: (GroupTab) -> Unit,
    members: List<GroupMemberUiModel>,
    badges: List<GroupBadgeDto>,
    isExpandedMembers: Boolean,
    onToggleExpandMembers: () -> Unit,
    onManageGroupClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(top = 2.dp, bottom = 24.dp),
    ) {
        // 1. Group Identity Card
        item {
            GroupIdentityCard(headerData = headerData)
        }

        // 2. Active Battle Card (نبرد فعال)
        if (battleData != null) {
            item {
                GroupActiveBattleCard(battle = battleData)
            }
        }

        // 3. 3-item Personal Stats Grid (Task count removed)
        item {
            GroupPersonalStatsGrid(stats = personalStats)
        }

        // 4. Members & Medals Tabbed Leaderboard Card
        item {
            GroupLeaderboardCard(
                selectedTab = selectedTab,
                onTabSelect = onTabSelect,
                members = members,
                badges = badges,
                isExpanded = isExpandedMembers,
                onToggleExpand = onToggleExpandMembers,
            )
        }
    }
}

/**
 * Card 1: Group Identity Card (Compact & Minimalist)
 */
@Composable
fun GroupIdentityCard(
    headerData: GroupHeaderData,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(2.dp, shape = RoundedCornerShape(20.dp), spotColor = Color(0x0A000000)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, GroupCardBorder),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
        ) {
            // Header Top Row: Mountain Graphic + Title + Motto + Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                // Mountain Badge Illustration
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFFEDE9FE), Color(0xFFDDD6FE)),
                            ),
                        )
                        .border(1.5.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(R.drawable.mountain),
                        contentDescription = "نشان گروه",
                        modifier = Modifier.size(46.dp),
                        contentScale = ContentScale.Fit,
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Name + Motto + Badges Row
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = headerData.name,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = GroupTextNavy,
                        modifier = Modifier.testTag("group_detail_name"),
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = headerData.motto,
                        fontFamily = IranSansFontFamily,
                        fontSize = 12.sp,
                        color = GroupTextMuted,
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Chips Row: "عضو" + "ID: 2841"
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        // Status: عضو
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GroupGreenStatusBg,
                        ) {
                            Text(
                                text = headerData.roleBadge,
                                fontFamily = IranSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = GroupGreenStatusText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            )
                        }

                        // ID Chip
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF1F5F9),
                        ) {
                            Text(
                                text = "کد: ${toPersianDigits(headerData.numericId)}",
                                fontFamily = IranSansFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp,
                                color = Color(0xFF475569),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = GroupCardBorder.copy(alpha = 0.6f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // 3-Metric Summary Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Metric 1: رتبه گروه
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = GroupGoldAccent,
                            modifier = Modifier.size(13.dp),
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = stringResource(R.string.group_stat_rank),
                            fontFamily = IranSansFontFamily,
                            fontSize = 11.sp,
                            color = GroupTextMuted,
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = toPersianDigits(headerData.rank),
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = GroupTextNavy,
                    )
                }

                Divider(
                    modifier = Modifier
                        .height(26.dp)
                        .width(1.dp),
                    color = GroupCardBorder,
                )

                // Metric 2: امتیاز گروه
                Column(
                    modifier = Modifier.weight(1.2f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = GroupGoldAccent,
                            modifier = Modifier.size(13.dp),
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = stringResource(R.string.group_stat_points),
                            fontFamily = IranSansFontFamily,
                            fontSize = 11.sp,
                            color = GroupTextMuted,
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formatPersianNumber(headerData.points),
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = GroupTextNavy,
                    )
                }

                Divider(
                    modifier = Modifier
                        .height(26.dp)
                        .width(1.dp),
                    color = GroupCardBorder,
                )

                // Metric 3: اعضا
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = null,
                            tint = GroupPrimaryPurple,
                            modifier = Modifier.size(13.dp),
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = stringResource(R.string.group_stat_members),
                            fontFamily = IranSansFontFamily,
                            fontSize = 11.sp,
                            color = GroupTextMuted,
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = toPersianDigits(headerData.membersCount),
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = GroupTextNavy,
                    )
                }
            }
        }
    }
}

/**
 * Card 2: Active Battle Card ("نبرد فعال") - Compact & Sleek
 */
@Composable
fun GroupActiveBattleCard(
    battle: GroupBattleData,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(2.dp, shape = RoundedCornerShape(18.dp), spotColor = Color(0x0A000000)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, GroupCardBorder),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
        ) {
            // Top Battle Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                // Right: "نبرد فعال" + Bolt icon
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = GroupPrimaryPurple,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.group_battle_title),
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = GroupPrimaryPurple,
                    )
                }

                // Left Meta: روز باقی‌مانده / هفته
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.QueryBuilder,
                        contentDescription = null,
                        tint = GroupTextMuted,
                        modifier = Modifier.size(13.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${stringResource(R.string.group_battle_time_remaining, toPersianDigits(battle.daysRemaining))} | ${stringResource(R.string.group_battle_week, toPersianDigits(battle.currentWeek), toPersianDigits(battle.totalWeeks))}",
                        fontFamily = IranSansFontFamily,
                        fontSize = 10.5.sp,
                        color = GroupTextMuted,
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Teams Duel Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                // My Group (Right in RTL)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = battle.myGroupName,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = GroupTextNavy,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formatPersianNumber(battle.myGroupPoints),
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = GroupPrimaryPurple,
                    )
                }

                // "✕" Matchup Cross
                Text(
                    text = "✕",
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = GroupTextLight,
                )

                // Team Opponent (Left in RTL)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = battle.opponentGroupName,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = GroupTextNavy,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formatPersianNumber(battle.opponentGroupPoints),
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = GroupPrimaryPurple,
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Comparison Segmented Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${toPersianDigits(battle.myPercentage)}٪",
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = GroupPrimaryPurple,
                )

                Spacer(modifier = Modifier.width(6.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFFDDD6FE)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(battle.myPercentage / 100f)
                            .clip(RoundedCornerShape(3.dp))
                            .background(GroupPrimaryPurple),
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "${toPersianDigits(battle.opponentPercentage)}٪",
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = GroupPrimaryPurple,
                )
            }
        }
    }
}

/**
 * 3-Card Personal Stats Row (Task count removed for a clean minimal look)
 */
@Composable
fun GroupPersonalStatsGrid(
    stats: PersonalGroupStats,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // 1. رتبه من
        GroupPersonalStatMiniCard(
            title = stringResource(R.string.group_my_rank),
            value = toPersianDigits(stats.rank),
            icon = Icons.Default.EmojiEvents,
            iconTint = GroupGoldAccent,
            backgroundColor = Color(0xFFFFFBEB),
            borderColor = Color(0xFFFDE68A),
            modifier = Modifier.weight(1f),
        )

        // 2. امتیاز من
        GroupPersonalStatMiniCard(
            title = stringResource(R.string.group_my_points),
            value = formatPersianNumber(stats.points),
            icon = Icons.Default.Star,
            iconTint = GroupGoldAccent,
            modifier = Modifier.weight(1.1f),
        )

        // 3. ساعت مطالعه
        GroupPersonalStatMiniCard(
            title = stringResource(R.string.group_my_study_hours),
            value = "${toPersianDigits(stats.studyHours)} ${stringResource(R.string.group_hour_unit)}",
            icon = Icons.Default.QueryBuilder,
            iconTint = Color(0xFF0284C7),
            modifier = Modifier.weight(1.1f),
        )
    }
}

@Composable
fun GroupPersonalStatMiniCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: Color,
    backgroundColor: Color = Color.White,
    borderColor: Color = GroupCardBorder,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .shadow(1.dp, shape = RoundedCornerShape(14.dp), spotColor = Color(0x08000000)),
        shape = RoundedCornerShape(14.dp),
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(12.dp),
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = title,
                    fontFamily = IranSansFontFamily,
                    fontSize = 10.5.sp,
                    color = GroupTextMuted,
                    maxLines = 1,
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = value,
                fontFamily = IranSansFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.5.sp,
                color = GroupTextNavy,
            )
        }
    }
}

/**
 * Leaderboard & Medals Tabbed Table Card (RTL table, Task column removed)
 */
@Composable
fun GroupLeaderboardCard(
    selectedTab: GroupTab,
    onTabSelect: (GroupTab) -> Unit,
    members: List<GroupMemberUiModel>,
    badges: List<GroupBadgeDto>,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(2.dp, shape = RoundedCornerShape(20.dp), spotColor = Color(0x0A000000)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, GroupCardBorder),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
        ) {
            // Tabs Row
            TabRow(
                selectedTabIndex = if (selectedTab == GroupTab.MEMBERS) 0 else 1,
                containerColor = Color.White,
                contentColor = GroupPrimaryPurple,
                divider = {
                    Divider(color = GroupCardBorder, thickness = 1.dp)
                },
                indicator = { tabPositions ->
                    val index = if (selectedTab == GroupTab.MEMBERS) 0 else 1
                    if (index < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[index]),
                            color = GroupPrimaryPurple,
                            height = 2.5.dp,
                        )
                    }
                },
                modifier = Modifier.padding(horizontal = 14.dp),
            ) {
                // Tab 1: اعضا
                Tab(
                    selected = selectedTab == GroupTab.MEMBERS,
                    onClick = { onTabSelect(GroupTab.MEMBERS) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Groups,
                                contentDescription = null,
                                tint = if (selectedTab == GroupTab.MEMBERS) GroupPrimaryPurple else GroupTextMuted,
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.group_tab_members),
                                fontFamily = IranSansFontFamily,
                                fontWeight = if (selectedTab == GroupTab.MEMBERS) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp,
                                color = if (selectedTab == GroupTab.MEMBERS) GroupPrimaryPurple else GroupTextMuted,
                            )
                        }
                    },
                )

                // Tab 2: مدال‌ها
                Tab(
                    selected = selectedTab == GroupTab.MEDALS,
                    onClick = { onTabSelect(GroupTab.MEDALS) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.MilitaryTech,
                                contentDescription = null,
                                tint = if (selectedTab == GroupTab.MEDALS) GroupPrimaryPurple else GroupTextMuted,
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.group_tab_medals),
                                fontFamily = IranSansFontFamily,
                                fontWeight = if (selectedTab == GroupTab.MEDALS) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp,
                                color = if (selectedTab == GroupTab.MEDALS) GroupPrimaryPurple else GroupTextMuted,
                            )
                        }
                    },
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            when (selectedTab) {
                GroupTab.MEMBERS -> {
                    // RTL Table Header: رتبه | عضو | ساعت مطالعه | امتیاز
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // 1. رتبه
                        Text(
                            text = stringResource(R.string.group_column_rank),
                            fontFamily = IranSansFontFamily,
                            fontSize = 11.sp,
                            color = GroupTextLight,
                            modifier = Modifier.width(36.dp),
                            textAlign = TextAlign.Center,
                        )

                        // 2. عضو
                        Text(
                            text = stringResource(R.string.group_column_member),
                            fontFamily = IranSansFontFamily,
                            fontSize = 11.sp,
                            color = GroupTextLight,
                            modifier = Modifier.weight(1.8f),
                            textAlign = TextAlign.Start,
                        )

                        // 3. ساعت مطالعه
                        Text(
                            text = stringResource(R.string.group_column_study_time),
                            fontFamily = IranSansFontFamily,
                            fontSize = 11.sp,
                            color = GroupTextLight,
                            modifier = Modifier.weight(1.1f),
                            textAlign = TextAlign.Center,
                        )

                        // 4. امتیاز
                        Text(
                            text = stringResource(R.string.group_column_points),
                            fontFamily = IranSansFontFamily,
                            fontSize = 11.sp,
                            color = GroupTextLight,
                            modifier = Modifier.weight(1.1f),
                            textAlign = TextAlign.End,
                        )
                    }

                    Divider(
                        color = GroupCardBorder.copy(alpha = 0.5f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                    )

                    // Member Rows
                    val displayedMembers = if (isExpanded) members else members.take(5)
                    displayedMembers.forEach { member ->
                        GroupMemberTableRow(member = member)
                    }

                    // "مشاهده همه" Footer button
                    if (members.size > 5) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onToggleExpand)
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = if (isExpanded) "بستن لیست" else stringResource(R.string.group_view_all),
                                fontFamily = IranSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = GroupPrimaryPurple,
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = null,
                                tint = GroupPrimaryPurple,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                }

                GroupTab.MEDALS -> {
                    if (badges.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = GroupGoldAccent.copy(alpha = 0.4f),
                                modifier = Modifier.size(36.dp),
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "هنوز مدالی برای این گروه ثبت نشده است.",
                                fontFamily = IranSansFontFamily,
                                fontSize = 12.sp,
                                color = GroupTextMuted,
                            )
                        }
                    } else {
                        badges.forEach { badgeItem ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFEF3C7)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = GroupGoldAccent,
                                        modifier = Modifier.size(18.dp),
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = badgeItem.badge.name,
                                        fontFamily = IranSansFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = GroupTextNavy,
                                    )
                                    Text(
                                        text = badgeItem.badge.description ?: "مدال افتخار گروه",
                                        fontFamily = IranSansFontFamily,
                                        fontSize = 11.sp,
                                        color = GroupTextMuted,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Single Row for Member Table in Leaderboard (Strictly RTL: رتبه -> عضو -> ساعت مطالعه -> امتیاز)
 */
@Composable
fun GroupMemberTableRow(
    member: GroupMemberUiModel,
    modifier: Modifier = Modifier,
) {
    val isMe = member.isCurrentUser
    val rowBackground = if (isMe) GroupLightPurpleBg else Color.Transparent

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 2.dp),
        shape = RoundedCornerShape(12.dp),
        color = rowBackground,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 1. Rank Badge (RTL First: Leftmost/Rightmost according to RTL)
            Box(
                modifier = Modifier.width(36.dp),
                contentAlignment = Alignment.Center,
            ) {
                when (member.rank) {
                    1 -> RankCircleBadge(rank = 1, bgColor = Color(0xFFFEF3C7), textColor = Color(0xFFB45309))
                    2 -> RankCircleBadge(rank = 2, bgColor = Color(0xFFD1FAE5), textColor = Color(0xFF047857))
                    3 -> RankCircleBadge(rank = 3, bgColor = Color(0xFFFFEDD5), textColor = Color(0xFFC2410C))
                    else -> RankCircleBadge(rank = member.rank, bgColor = Color(0xFFF1F5F9), textColor = Color(0xFF64748B))
                }
            }

            // 2. Member Avatar & Name (+ "شما" chip)
            Row(
                modifier = Modifier.weight(1.8f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
            ) {
                MemberMiniAvatar(member = member)

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = member.name,
                    fontFamily = IranSansFontFamily,
                    fontWeight = if (isMe) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 12.5.sp,
                    color = GroupTextNavy,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                if (isMe) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFEDE9FE),
                    ) {
                        Text(
                            text = stringResource(R.string.group_you_badge),
                            fontFamily = IranSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            color = GroupPrimaryPurple,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                        )
                    }
                }
            }

            // 3. Study Time (e.g. "۲۳ ساعت")
            Text(
                text = "${toPersianDigits(member.studyHours)} " + stringResource(R.string.group_hour_unit),
                fontFamily = IranSansFontFamily,
                fontWeight = if (isMe) FontWeight.Bold else FontWeight.Normal,
                fontSize = 11.5.sp,
                color = if (isMe) GroupPrimaryPurple else GroupTextNavy,
                modifier = Modifier.weight(1.1f),
                textAlign = TextAlign.Center,
            )

            // 4. Points (e.g. "۳,۱۲۰")
            Text(
                text = formatPersianNumber(member.points),
                fontFamily = IranSansFontFamily,
                fontWeight = if (isMe) FontWeight.Bold else FontWeight.SemiBold,
                fontSize = 12.5.sp,
                color = if (isMe) GroupPrimaryPurple else GroupTextNavy,
                modifier = Modifier.weight(1.1f),
                textAlign = TextAlign.End,
            )
        }
    }
}

@Composable
fun RankCircleBadge(
    rank: Int,
    bgColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = toPersianDigits(rank),
            fontFamily = IranSansFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = textColor,
        )
    }
}

@Composable
fun MemberMiniAvatar(
    member: GroupMemberUiModel,
    modifier: Modifier = Modifier,
) {
    val bgColor = Color(member.avatarBgColor)
    Box(
        modifier = modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(bgColor.copy(alpha = 0.2f))
            .border(1.dp, bgColor.copy(alpha = 0.35f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = member.name.take(1),
            fontFamily = IranSansFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 11.5.sp,
            color = bgColor,
        )
    }
}
