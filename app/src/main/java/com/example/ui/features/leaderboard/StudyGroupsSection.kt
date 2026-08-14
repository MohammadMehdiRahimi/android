package com.example.ui.features.leaderboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.core.toPersianNumber
import com.example.ui.theme.LocalShetabColors
import com.example.ui.theme.ShetabColorPalette

private fun Int.toPersianNumber(): String = this.toString().toPersianNumber()
private fun Float.toPersianNumber(): String = if (this % 1f == 0f) this.toInt().toString().toPersianNumber() else String.format("%.1f", this).toPersianNumber()

// Models for Study Groups
data class StudyGroupMember(
    val id: String,
    val name: String,
    val isMe: Boolean = false,
    val todayHours: Float,
    val todayTests: Int,
    val weekHours: Float,
    val weekTests: Int,
    val monthHours: Float,
    val monthTests: Int,
    val status: String, // "در حال مطالعه ⏱️", "استراحت ☕", "آفلاین"
    val role: String, // "مدیر گروه 👑", "عضو"
    val avatarBgColor: Color = Color(0xFF0D52FF)
)

data class StudyGroup(
    val id: String,
    val name: String,
    val description: String,
    val category: String, // "تجربی کنکور ۱۴۰۴", "ریاضی و فیزیک", "عمومی"
    val inviteCode: String,
    val memberCount: Int,
    val targetWeeklyHours: Int,
    val isJoined: Boolean,
    val members: List<StudyGroupMember>
) {
    val inviteUrl: String get() = "https://shetab.app/group/$inviteCode"
}

enum class GroupTimeRange(val title: String) {
    TODAY("امروز"),
    THIS_WEEK("این هفته"),
    THIS_MONTH("این ماه")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyGroupsSection() {
    val colors = LocalShetabColors.current
    val context = LocalContext.current

    // Initial mock data for groups
    var groupsList by remember {
        mutableStateOf(
            listOf(
                StudyGroup(
                    id = "g1",
                    name = "سلاطین زیست تجربی 🧬",
                    description = "رقابت روزانه ساعت مطالعه و تست‌زنی زیست‌شناسی کنکور ۱۴۰۴",
                    category = "تجربی",
                    inviteCode = "BIO-1404",
                    memberCount = 5,
                    targetWeeklyHours = 250,
                    isJoined = true,
                    members = listOf(
                        StudyGroupMember("m1", "شما (پویا)", isMe = true, todayHours = 7.5f, todayTests = 160, weekHours = 46.0f, weekTests = 980, monthHours = 180.0f, monthTests = 3600, status = "در حال مطالعه ⏱️", role = "مدیر گروه 👑", avatarBgColor = Color(0xFF0D52FF)),
                        StudyGroupMember("m2", "رضا محسنی", isMe = false, todayHours = 8.2f, todayTests = 180, weekHours = 51.5f, weekTests = 1120, monthHours = 195.0f, monthTests = 4100, status = "در حال مطالعه ⏱️", role = "عضو", avatarBgColor = Color(0xFFFFB300)),
                        StudyGroupMember("m3", "غزل موسوی", isMe = false, todayHours = 6.0f, todayTests = 120, weekHours = 41.0f, weekTests = 850, monthHours = 165.0f, monthTests = 3200, status = "استراحت ☕", role = "عضو", avatarBgColor = Color(0xFF4CAF50)),
                        StudyGroupMember("m4", "امیر محمدی", isMe = false, todayHours = 5.5f, todayTests = 90, weekHours = 38.0f, weekTests = 720, monthHours = 150.0f, monthTests = 2800, status = "آفلاین", role = "عضو", avatarBgColor = Color(0xFFE91E63)),
                        StudyGroupMember("m5", "سایه نجاتی", isMe = false, todayHours = 4.0f, todayTests = 75, weekHours = 32.0f, weekTests = 600, monthHours = 135.0f, monthTests = 2400, status = "آفلاین", role = "عضو", avatarBgColor = Color(0xFF9C27B0))
                    )
                ),
                StudyGroup(
                    id = "g2",
                    name = "فیزیکدانان ۱۰۰٪ ⚡",
                    description = "تیم تخصصی حل تست فیزیک و تحلیل آزمون‌های قلمچی و گاج",
                    category = "ریاضی و فیزیک",
                    inviteCode = "PHYS-100",
                    memberCount = 4,
                    targetWeeklyHours = 200,
                    isJoined = true,
                    members = listOf(
                        StudyGroupMember("m10", "رضا محسنی", isMe = false, todayHours = 9.0f, todayTests = 210, weekHours = 55.0f, weekTests = 1300, monthHours = 210.0f, monthTests = 4800, status = "در حال مطالعه ⏱️", role = "مدیر گروه 👑", avatarBgColor = Color(0xFFFFB300)),
                        StudyGroupMember("m1", "شما (پویا)", isMe = true, todayHours = 7.5f, todayTests = 160, weekHours = 46.0f, weekTests = 980, monthHours = 180.0f, monthTests = 3600, status = "در حال مطالعه ⏱️", role = "عضو", avatarBgColor = Color(0xFF0D52FF)),
                        StudyGroupMember("m11", "بردیا زارع", isMe = false, todayHours = 6.8f, todayTests = 140, weekHours = 42.0f, weekTests = 900, monthHours = 170.0f, monthTests = 3400, status = "استراحت ☕", role = "عضو", avatarBgColor = Color(0xFF00BCD4)),
                        StudyGroupMember("m12", "پارسا اکبری", isMe = false, todayHours = 5.0f, todayTests = 100, weekHours = 35.0f, weekTests = 750, monthHours = 140.0f, monthTests = 2900, status = "آفلاین", role = "عضو", avatarBgColor = Color(0xFFFF5722))
                    )
                ),
                StudyGroup(
                    id = "g3",
                    name = "کمپ مطالعه سحرخیزان 🌅",
                    description = "مطالعه از ساعت ۶ صبح هر روز + رقابت در تست‌زنی مواد عمومی و اختصاصی",
                    category = "عمومی",
                    inviteCode = "SAHAR-6AM",
                    memberCount = 8,
                    targetWeeklyHours = 300,
                    isJoined = false,
                    members = emptyList()
                ),
                StudyGroup(
                    id = "g4",
                    name = "رتبه‌های زیر ۱۰۰ کنکور 🏆",
                    description = "گروه ویژه برنامه‌ریزی فشرده و افزایش درصد دروس اختصاصی",
                    category = "تجربی",
                    inviteCode = "TOP-100",
                    memberCount = 6,
                    targetWeeklyHours = 350,
                    isJoined = false,
                    members = emptyList()
                )
            )
        )
    }

    // State
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("همه") }
    val categories = listOf("همه", "گروه‌های من", "تجربی", "ریاضی و فیزیک", "عمومی")

    var selectedGroup by remember { mutableStateOf<StudyGroup?>(groupsList.firstOrNull { it.isJoined }) }
    var showCreateGroupDialog by remember { mutableStateOf(false) }
    var showJoinByCodeDialog by remember { mutableStateOf(false) }
    var selectedMemberForDetail by remember { mutableStateOf<StudyGroupMember?>(null) }

    // Filtered group list based on search & category
    val filteredGroups = remember(groupsList, searchQuery, selectedCategoryFilter) {
        groupsList.filter { group ->
            val matchesCategory = when (selectedCategoryFilter) {
                "همه" -> true
                "گروه‌های من" -> group.isJoined
                else -> group.category == selectedCategoryFilter
            }
            val matchesSearch = searchQuery.isBlank() ||
                    group.name.contains(searchQuery, ignoreCase = true) ||
                    group.description.contains(searchQuery, ignoreCase = true) ||
                    group.category.contains(searchQuery, ignoreCase = true) ||
                    group.inviteCode.contains(searchQuery, ignoreCase = true)

            matchesCategory && matchesSearch
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Quick Actions & Invite Code Join Banner
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = colors.cardBg),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, colors.accentMain.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "گروه‌های مطالعه و رقابت 👥",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = colors.primaryText
                        )
                        Text(
                            text = "با دوستانت گروه بساز یا عضو شو و رقابت کن!",
                            fontSize = 11.5.sp,
                            color = colors.secondaryText
                        )
                    }

                    Button(
                        onClick = { showCreateGroupDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ایجاد گروه", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Search & Join by Code Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("جستجوی نام، دسته یا کد گروه...", fontSize = 12.sp, color = colors.secondaryText) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = colors.accentMain) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = colors.secondaryText)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.accentMain,
                            unfocusedBorderColor = colors.secondaryText.copy(alpha = 0.2f),
                            focusedContainerColor = colors.bgMain.copy(alpha = 0.5f),
                            unfocusedContainerColor = colors.bgMain.copy(alpha = 0.5f),
                            focusedTextColor = colors.primaryText,
                            unfocusedTextColor = colors.primaryText
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedButton(
                        onClick = { showJoinByCodeDialog = true },
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, colors.accentMain.copy(alpha = 0.4f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.accentMain),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 14.dp)
                    ) {
                        Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ورود با کد", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Categories Horizontal Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(categories) { cat ->
                        val isSelected = cat == selectedCategoryFilter
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategoryFilter = cat },
                            label = {
                                Text(
                                    text = cat,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            shape = RoundedCornerShape(100.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = colors.accentMain,
                                selectedLabelColor = Color.White,
                                containerColor = colors.bgMain,
                                labelColor = colors.primaryText
                            )
                        )
                    }
                }
            }
        }

        // Active Selected Group Dashboard (if user joined and selected a group)
        if (selectedGroup != null && selectedGroup!!.isJoined) {
            GroupDashboardCard(
                group = selectedGroup!!,
                onCopyInviteLink = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Invite Link", selectedGroup!!.inviteUrl)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "لینک دعوت گروه کپی شد! 📋", Toast.LENGTH_SHORT).show()
                },
                onMemberClick = { member -> selectedMemberForDetail = member }
            )
        }

        // Section Title: Search / Group list
        Text(
            text = if (searchQuery.isNotBlank()) "نتایج جستجو (${filteredGroups.size.toPersianNumber()} گروه)" else "همه گروه‌های پیشنهادی",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = colors.primaryText,
            modifier = Modifier.padding(top = 8.dp)
        )

        if (filteredGroups.isEmpty()) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBg),
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🔍", fontSize = 32.sp)
                    Text(
                        text = "گروهی با این مشخصات یافت نشد!",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primaryText
                    )
                    Text(
                        text = "می‌توانی خودت یک گروه جدید بسازی و دوستانت را دعوت کنی.",
                        fontSize = 11.5.sp,
                        color = colors.secondaryText,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = { showCreateGroupDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("ساخت گروه جدید", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            filteredGroups.forEach { group ->
                GroupListItemCard(
                    group = group,
                    isSelected = selectedGroup?.id == group.id,
                    onSelect = {
                        if (group.isJoined) {
                            selectedGroup = group
                        }
                    },
                    onJoin = {
                        // Toggle join state
                        groupsList = groupsList.map {
                            if (it.id == group.id) {
                                val updatedMembers = if (it.members.none { m -> m.isMe }) {
                                    it.members + StudyGroupMember(
                                        id = "m1",
                                        name = "شما (پویا)",
                                        isMe = true,
                                        todayHours = 7.5f,
                                        todayTests = 160,
                                        weekHours = 46.0f,
                                        weekTests = 980,
                                        monthHours = 180.0f,
                                        monthTests = 3600,
                                        status = "در حال مطالعه ⏱️",
                                        role = "عضو",
                                        avatarBgColor = Color(0xFF0D52FF)
                                    )
                                } else it.members

                                it.copy(
                                    isJoined = true,
                                    memberCount = it.memberCount + 1,
                                    members = updatedMembers
                                )
                            } else it
                        }
                        val newJoinedGroup = groupsList.find { it.id == group.id }
                        selectedGroup = newJoinedGroup
                        Toast.makeText(context, "با موفقیت به گروه ${group.name} پیوستی! 🎉", Toast.LENGTH_SHORT).show()
                    },
                    onCopyInvite = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Invite Link", group.inviteUrl)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "لینک دعوت کپی شد: ${group.inviteUrl}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    // Dialog: Create New Group
    if (showCreateGroupDialog) {
        CreateGroupDialog(
            onDismiss = { showCreateGroupDialog = false },
            onCreate = { name, desc, cat, goal ->
                val newCode = "JOIN-${(1000..9999).random()}"
                val newGroup = StudyGroup(
                    id = "g_${System.currentTimeMillis()}",
                    name = name,
                    description = desc,
                    category = cat,
                    inviteCode = newCode,
                    memberCount = 1,
                    targetWeeklyHours = goal,
                    isJoined = true,
                    members = listOf(
                        StudyGroupMember(
                            id = "m1",
                            name = "شما (پویا)",
                            isMe = true,
                            todayHours = 7.5f,
                            todayTests = 160,
                            weekHours = 46.0f,
                            weekTests = 980,
                            monthHours = 180.0f,
                            monthTests = 3600,
                            status = "در حال مطالعه ⏱️",
                            role = "مدیر گروه 👑",
                            avatarBgColor = Color(0xFF0D52FF)
                        )
                    )
                )
                groupsList = listOf(newGroup) + groupsList
                selectedGroup = newGroup
                showCreateGroupDialog = false
                Toast.makeText(context, "گروه «$name» با موفقیت ساخته شد! 🎉", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Dialog: Join by Invite Code / Link
    if (showJoinByCodeDialog) {
        JoinByCodeDialog(
            onDismiss = { showJoinByCodeDialog = false },
            onJoinWithCode = { codeInput ->
                val cleanCode = codeInput.trim().uppercase().removePrefix("HTTPS://SHETAB.APP/GROUP/")
                val foundGroup = groupsList.find { it.inviteCode.equals(cleanCode, ignoreCase = true) }
                if (foundGroup != null) {
                    groupsList = groupsList.map {
                        if (it.id == foundGroup.id) {
                            it.copy(isJoined = true)
                        } else it
                    }
                    selectedGroup = groupsList.find { it.id == foundGroup.id }
                    showJoinByCodeDialog = false
                    Toast.makeText(context, "با موفقیت به گروه ${foundGroup.name} ملحق شدی!", Toast.LENGTH_SHORT).show()
                } else {
                    // Create dynamic group if code didn't match pre-defined list
                    val dynamicGroup = StudyGroup(
                        id = "g_dyn_${System.currentTimeMillis()}",
                        name = "گروه دعوت‌شده ($cleanCode) 👥",
                        description = "گروه مطالعه اختصاصی پیوسته با لینک دعوت",
                        category = "عمومی",
                        inviteCode = cleanCode,
                        memberCount = 2,
                        targetWeeklyHours = 200,
                        isJoined = true,
                        members = listOf(
                            StudyGroupMember("m1", "شما (پویا)", isMe = true, todayHours = 7.5f, todayTests = 160, weekHours = 46.0f, weekTests = 980, monthHours = 180.0f, monthTests = 3600, status = "در حال مطالعه ⏱️", role = "عضو", avatarBgColor = Color(0xFF0D52FF)),
                            StudyGroupMember("m_host", "دعوت کننده", isMe = false, todayHours = 8.0f, todayTests = 190, weekHours = 52.0f, weekTests = 1100, monthHours = 190.0f, monthTests = 3900, status = "آفلاین", role = "مدیر گروه 👑", avatarBgColor = Color(0xFF4CAF50))
                        )
                    )
                    groupsList = listOf(dynamicGroup) + groupsList
                    selectedGroup = dynamicGroup
                    showJoinByCodeDialog = false
                    Toast.makeText(context, "با موفقیت وارد گروه شدی! 🎉", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    // Member Detail Popup Dialog
    if (selectedMemberForDetail != null) {
        MemberDetailDialog(
            member = selectedMemberForDetail!!,
            onDismiss = { selectedMemberForDetail = null }
        )
    }
}

// Group Dashboard Card showing leaderboard & member stats
@Composable
fun GroupDashboardCard(
    group: StudyGroup,
    onCopyInviteLink: () -> Unit,
    onMemberClick: (StudyGroupMember) -> Unit
) {
    val colors = LocalShetabColors.current
    var timeRange by remember { mutableStateOf(GroupTimeRange.TODAY) }

    // Sorted members based on selected time range
    val sortedMembers = remember(group.members, timeRange) {
        group.members.sortedByDescending { member ->
            when (timeRange) {
                GroupTimeRange.TODAY -> member.todayHours
                GroupTimeRange.THIS_WEEK -> member.weekHours
                GroupTimeRange.THIS_MONTH -> member.monthHours
            }
        }
    }

    // Stats calculations
    val totalHours = sortedMembers.sumOf {
        when (timeRange) {
            GroupTimeRange.TODAY -> it.todayHours.toDouble()
            GroupTimeRange.THIS_WEEK -> it.weekHours.toDouble()
            GroupTimeRange.THIS_MONTH -> it.monthHours.toDouble()
        }
    }.toFloat()

    val totalTests = sortedMembers.sumOf {
        when (timeRange) {
            GroupTimeRange.TODAY -> it.todayTests
            GroupTimeRange.THIS_WEEK -> it.weekTests
            GroupTimeRange.THIS_MONTH -> it.monthTests
        }
    }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBg),
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, colors.accentMain.copy(alpha = 0.3f), RoundedCornerShape(22.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Group Header Banner
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = group.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = colors.primaryText
                        )
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = colors.accentMain.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = group.category,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.accentMain,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = group.description,
                        fontSize = 11.sp,
                        color = colors.secondaryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Copy Link Button
                OutlinedButton(
                    onClick = onCopyInviteLink,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, colors.accentMain.copy(alpha = 0.3f)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp), tint = colors.accentMain)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("لینک دعوت", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colors.accentMain)
                }
            }

            // Summary Info Badges: Total Hours & Total Tests
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = colors.bgMain
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("⏱️", fontSize = 18.sp)
                        Column {
                            Text("کل مطالعه (${timeRange.title})", fontSize = 10.sp, color = colors.secondaryText)
                            Text(
                                text = "${totalHours.toPersianNumber()} ساعت",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = colors.accentMain
                            )
                        }
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = colors.bgMain
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("📝", fontSize = 18.sp)
                        Column {
                            Text("کل تست‌ها (${timeRange.title})", fontSize = 10.sp, color = colors.secondaryText)
                            Text(
                                text = "${totalTests.toPersianNumber()} تست",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = colors.accentMain
                            )
                        }
                    }
                }
            }

            // Time Range Toggle Switcher (امروز / این هفته / این ماه)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.bgMain, RoundedCornerShape(100.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                GroupTimeRange.values().forEach { range ->
                    val isSelected = range == timeRange
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(100.dp))
                            .background(if (isSelected) colors.accentMain else Color.Transparent)
                            .clickable { timeRange = range }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = range.title,
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else colors.secondaryText
                        )
                    }
                }
            }

            // Member Ranking List in Group
            Text(
                text = "جدول رتبه‌بندی اعضای گروه",
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = colors.primaryText
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                sortedMembers.forEachIndexed { index, member ->
                    val rank = index + 1
                    val rankBadge = when (rank) {
                        1 -> "🥇"
                        2 -> "🥈"
                        3 -> "🥉"
                        else -> "${rank.toPersianNumber()}"
                    }

                    val hours = when (timeRange) {
                        GroupTimeRange.TODAY -> member.todayHours
                        GroupTimeRange.THIS_WEEK -> member.weekHours
                        GroupTimeRange.THIS_MONTH -> member.monthHours
                    }

                    val tests = when (timeRange) {
                        GroupTimeRange.TODAY -> member.todayTests
                        GroupTimeRange.THIS_WEEK -> member.weekTests
                        GroupTimeRange.THIS_MONTH -> member.monthTests
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (member.isMe) colors.accentMain.copy(alpha = 0.12f) else colors.bgMain,
                        border = if (member.isMe) BorderStroke(1.dp, colors.accentMain.copy(alpha = 0.4f)) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMemberClick(member) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = rankBadge,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = colors.primaryText,
                                    modifier = Modifier.width(22.dp)
                                )

                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(member.avatarBgColor, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = member.name.take(1),
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = member.name,
                                            fontSize = 12.5.sp,
                                            fontWeight = if (member.isMe) FontWeight.Black else FontWeight.Bold,
                                            color = colors.primaryText
                                        )
                                        if (member.role.contains("مدیر")) {
                                            Text(text = "👑", fontSize = 11.sp)
                                        }
                                    }
                                    Text(
                                        text = member.status,
                                        fontSize = 10.sp,
                                        color = colors.secondaryText
                                    )
                                }
                            }

                            // Stats Pill: Hours + Tests
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${hours.toPersianNumber()} ساعت",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = colors.accentMain
                                    )
                                    Text(
                                        text = "${tests.toPersianNumber()} تست",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = colors.secondaryText
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

// Group Item Card in the list
@Composable
fun GroupListItemCard(
    group: StudyGroup,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onJoin: () -> Unit,
    onCopyInvite: () -> Unit
) {
    val colors = LocalShetabColors.current

    // Calculate current studying members count and total today study hours
    val studyingCount = group.members.count { it.status.contains("مطالعه") }
    val totalHoursToday = group.members.sumOf { it.todayHours.toDouble() }.toFloat()

    val categoryIcon = when {
        group.category.contains("تجربی") -> "🧬"
        group.category.contains("ریاضی") -> "📐"
        group.category.contains("انسانی") -> "📚"
        group.category.contains("هنر") -> "🎨"
        else -> "🎓"
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) colors.accentMain.copy(alpha = 0.08f) else colors.cardBg
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) colors.accentMain else colors.primaryText.copy(alpha = 0.08f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onSelect() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Icon + Title + Category Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Category/Group Avatar Box
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(
                                color = colors.accentMain.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .border(1.dp, colors.accentMain.copy(alpha = 0.2f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = categoryIcon, fontSize = 22.sp)
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = group.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = colors.primaryText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = group.description,
                            fontSize = 11.5.sp,
                            color = colors.secondaryText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = colors.accentMain.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = group.category,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.accentMain,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Live Activity & Stats Bar
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = colors.bgMain.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, colors.primaryText.copy(alpha = 0.05f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Members Count
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(14.dp), tint = colors.secondaryText)
                        Text(
                            text = "${group.memberCount.toPersianNumber()} عضو",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primaryText
                        )
                    }

                    // Live Studying Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(if (studyingCount > 0) Color(0xFF10B981) else colors.secondaryText, CircleShape)
                        )
                        Text(
                            text = if (studyingCount > 0) "${studyingCount.toPersianNumber()} نفر در حال مطالعه" else "آفلاین",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (studyingCount > 0) Color(0xFF10B981) else colors.secondaryText
                        )
                    }

                    // Total Today Hours
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "🔥", fontSize = 12.sp)
                        Text(
                            text = "${totalHoursToday.toPersianNumber()}h امروز",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.accentMain
                        )
                    }
                }
            }

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "کد دعوت:",
                        fontSize = 11.sp,
                        color = colors.secondaryText
                    )
                    Text(
                        text = group.inviteCode,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = colors.accentMain
                    )
                    IconButton(
                        onClick = onCopyInvite,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "کپی کد", tint = colors.secondaryText, modifier = Modifier.size(14.dp))
                    }
                }

                if (group.isJoined) {
                    Button(
                        onClick = onSelect,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) colors.accentMain else colors.accentMain.copy(alpha = 0.15f),
                            contentColor = if (isSelected) Color.White else colors.accentMain
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isSelected) "داشبورد فعال ✓" else "مشاهده گروه 📊",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Button(
                        onClick = onJoin,
                        colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("عضویت در گروه", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Dialog to create new group
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, desc: String, category: String, weeklyHoursGoal: Int) -> Unit
) {
    val colors = LocalShetabColors.current
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("تجربی") }
    var goalHours by remember { mutableIntStateOf(200) }

    val categories = listOf("تجربی", "ریاضی و فیزیک", "علوم انسانی", "عمومی", "هنر و زبان")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = colors.cardBg,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "ساخت گروه مطالعه جدید 👥",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = colors.primaryText
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("نام گروه (مثال: قهرمانان زیست)") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("توضیحات و قوانین گروه") },
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("رشته / گروه تحصیلی:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colors.primaryText)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = cat == category,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("هدف ساعت مطالعه هفتگی گروه:", fontSize = 12.sp, color = colors.primaryText)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (goalHours > 50) goalHours -= 50 }) {
                            Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = colors.accentMain)
                        }
                        Text("${goalHours.toPersianNumber()} ساعت", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { goalHours += 50 }) {
                            Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.accentMain)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("انصراف", color = colors.secondaryText)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onCreate(name, desc.ifBlank { "گروه مطالعه و رقابت تخصصی" }, category, goalHours)
                            }
                        },
                        enabled = name.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("ایجاد و ساخت گروه", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Dialog to join group with code or link
@Composable
fun JoinByCodeDialog(
    onDismiss: () -> Unit,
    onJoinWithCode: (String) -> Unit
) {
    val colors = LocalShetabColors.current
    var codeInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = colors.cardBg,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "ورود با کد یا لینک دعوت 🔗",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = colors.primaryText
                )

                Text(
                    text = "کد دعوت اختصاصی گروه یا لینک دعوت را در کادر زیر وارد کن:",
                    fontSize = 11.5.sp,
                    color = colors.secondaryText
                )

                OutlinedTextField(
                    value = codeInput,
                    onValueChange = { codeInput = it },
                    placeholder = { Text("مثلاً BIO-1404 یا لینک کامل") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("لغو", color = colors.secondaryText)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (codeInput.isNotBlank()) {
                                onJoinWithCode(codeInput)
                            }
                        },
                        enabled = codeInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("ورود به گروه", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Dialog to show member study detail
@Composable
fun MemberDetailDialog(
    member: StudyGroupMember,
    onDismiss: () -> Unit
) {
    val colors = LocalShetabColors.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = colors.cardBg,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(member.avatarBgColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = member.name.take(1),
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = member.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = colors.primaryText
                )

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = colors.accentMain.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "${member.role} • ${member.status}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.accentMain,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Divider(color = colors.secondaryText.copy(alpha = 0.15f))

                // Detailed stats comparison
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatBox("امروز", "${member.todayHours.toPersianNumber()} ساعت", "${member.todayTests.toPersianNumber()} تست", colors)
                    StatBox("این هفته", "${member.weekHours.toPersianNumber()} ساعت", "${member.weekTests.toPersianNumber()} تست", colors)
                    StatBox("این ماه", "${member.monthHours.toPersianNumber()} ساعت", "${member.monthTests.toPersianNumber()} تست", colors)
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("بستن", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StatBox(
    title: String,
    hoursText: String,
    testsText: String,
    colors: ShetabColorPalette
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = colors.bgMain,
        modifier = Modifier.padding(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 11.sp, color = colors.secondaryText)
            Text(hoursText, fontSize = 12.sp, fontWeight = FontWeight.Black, color = colors.accentMain)
            Text(testsText, fontSize = 10.sp, color = colors.primaryText)
        }
    }
}
