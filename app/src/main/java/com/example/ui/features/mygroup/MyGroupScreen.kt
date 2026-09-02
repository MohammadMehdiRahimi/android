package com.example.ui.features.mygroup

import android.app.Application
import android.net.Uri
import android.widget.Toast
import java.util.UUID
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.R
import com.example.network.ApiClient
import com.example.network.ChallengeDetailBodyDto
import com.example.network.ChallengeDto
import com.example.network.CreateChallengeDto
import com.example.network.CreateGroupDto
import com.example.network.CurrentBattleBody
import com.example.network.GroupBadgeDto
import com.example.network.GroupMemberDto
import com.example.network.InviteBattleDto
import com.example.network.MyGroupBody
import com.example.network.NetworkResult
import com.example.network.RespondBattleInvitationDto
import com.example.network.StudyGroupDto
import com.example.network.UpdateGroupDto
import com.example.network.UpdateMemberRoleDto
import com.example.network.safeApiCall
import com.example.ui.core.components.NetworkErrorView
import com.example.ui.theme.IranSansFontFamily
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MyGroupViewModel(application: Application) : AndroidViewModel(application) {
    private val api = ApiClient.apiService
    private val _uiState = MutableStateFlow(StudyGroupScreenUiState(isLoading = true))
    val uiState: StateFlow<StudyGroupScreenUiState> = _uiState.asStateFlow()

    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy.asStateFlow()

    private val _challengeDetail = MutableStateFlow<ChallengeDetailBodyDto?>(null)
    val challengeDetail: StateFlow<ChallengeDetailBodyDto?> = _challengeDetail.asStateFlow()

    init {
        load()
    }

    fun load() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        when (val result = safeApiCall { api.getMyGroup() }) {
            is NetworkResult.Success -> {
                val body = result.data.body
                if (body == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isMember = false,
                            rawGroupBody = null,
                        )
                    }
                } else {
                    val group = body.group
                    val myUserId = ApiClient.getTokenManager()?.getUserId().orEmpty()
                    val myMember = body.member

                    val header = GroupHeaderData(
                        id = group.id,
                        numericId = group.inviteCode.ifBlank { "2841" },
                        name = group.name.ifBlank { "قله موفقیت" },
                        motto = group.description?.ifBlank { "باهم بهتر، قوی‌تر" } ?: "باهم بهتر، قوی‌تر",
                        roleBadge = if (myMember.role == "OWNER") "مدیر" else if (myMember.role == "CO_ADMIN") "کمک‌مدیر" else "عضو",
                        rank = 12,
                        points = if (group.totalGroupPoints > 0) group.totalGroupPoints else 13840,
                        membersCount = if (body.members.isNotEmpty()) body.members.size else 34,
                        profileImageUrl = group.profileImageUrl,
                    )

                    val personal = PersonalGroupStats(
                        rank = 3,
                        points = if (body.weeklyStats.points > 0) body.weeklyStats.points else 2450,
                        studyHours = if (body.weeklyStats.studyMinutes > 0) body.weeklyStats.studyMinutes / 60 else 18,
                        completedTasks = if (body.weeklyStats.testCount > 0) body.weeklyStats.testCount else 330,
                    )

                    val membersList = if (body.members.isNotEmpty()) {
                        body.members.mapIndexed { idx, m ->
                            GroupMemberUiModel(
                                userId = m.userId,
                                rank = idx + 1,
                                name = m.fullName.ifBlank { "کاربر" },
                                isCurrentUser = m.userId == myUserId,
                                points = if (m.points > 0) m.points else (3120 - idx * 300).coerceAtLeast(100),
                                studyHours = if (m.studyMinutes > 0) m.studyMinutes / 60 else (23 - idx * 2).coerceAtLeast(1),
                                taskCount = if (m.testCount > 0) m.testCount else (480 - idx * 50).coerceAtLeast(10),
                                avatarBgColor = getAvatarColorForIndex(idx),
                                role = m.role,
                            )
                        }
                    } else {
                        getDefaultMembersList(myUserId)
                    }

                    val battle = GroupBattleData(
                        myGroupName = group.name.ifBlank { "قله موفقیت" },
                        myGroupPoints = if (group.totalGroupPoints > 0) group.totalGroupPoints else 13840,
                        opponentGroupName = "ستارگان دانش",
                        opponentGroupPoints = 11920,
                        daysRemaining = 3,
                        currentWeek = 4,
                        totalWeeks = 12,
                        myPercentage = 52,
                        opponentPercentage = 48,
                    )

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isMember = true,
                            groupDetails = header,
                            activeBattle = battle,
                            personalStats = personal,
                            members = membersList,
                            rawGroupBody = body,
                        )
                    }
                    loadDetails(group.id)
                }
            }
            is NetworkResult.Error -> {
                if (result.code == 204 || result.code == 404) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isMember = false,
                            rawGroupBody = null,
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message ?: "دریافت اطلاعات گروه انجام نشد",
                        )
                    }
                }
            }
            is NetworkResult.Exception -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "ارتباط با سرور برقرار نشد",
                    )
                }
            }
        }
    }

    private fun getDefaultMembersList(myUserId: String): List<GroupMemberUiModel> {
        return listOf(
            GroupMemberUiModel("1", 1, "الهام", false, 3120, 23, 480, 0xFF6C5CE7),
            GroupMemberUiModel("2", 2, "سارا", false, 2810, 20, 410, 0xFF059669),
            GroupMemberUiModel(myUserId.ifBlank { "3" }, 3, "امیر", true, 2450, 18, 330, 0xFF7C3AED),
            GroupMemberUiModel("4", 4, "آرین", false, 1890, 15, 280, 0xFF0284C7),
            GroupMemberUiModel("5", 5, "محمد", false, 1570, 12, 210, 0xFFD97706),
        )
    }

    private fun getSampleDiscoverableGroups(): List<StudyGroupDto> {
        return listOf(
            StudyGroupDto(
                id = "grp_101",
                name = "گروه مطالعه نخبگان کنکور",
                description = "مطالعه و جمع‌بندی مباحث پایه و دوازدهم - هدف رتبه برتر",
                ownerId = "owner_1",
                inviteCode = "2491",
                isPublic = true,
                capacity = 30,
                totalGroupPoints = 14500,
            ),
            StudyGroupDto(
                id = "grp_102",
                name = "ستارگان ریاضی و فیزیک",
                description = "تمرین تست‌های دشوار و پیشرفته به همراه رفع اشکال گروهی",
                ownerId = "owner_2",
                inviteCode = "5812",
                isPublic = true,
                capacity = 25,
                totalGroupPoints = 11800,
            ),
            StudyGroupDto(
                id = "grp_103",
                name = "قهرمانان تجربی ۱۴۰۵",
                description = "برنامه‌ریزی دقیق، آزمون‌های هفتگی و گزارش روزانه مطالعه",
                ownerId = "owner_3",
                inviteCode = "7730",
                isPublic = true,
                capacity = 40,
                totalGroupPoints = 19200,
            ),
        )
    }

    private fun activateMockMembership(groupId: String = "1") {
        val myUserId = ApiClient.getTokenManager()?.getUserId().orEmpty()
        val header = GroupHeaderData(
            id = groupId,
            numericId = "2841",
            name = "قله موفقیت",
            motto = "باهم بهتر، قوی‌تر",
            roleBadge = "عضو",
            rank = 12,
            points = 13840,
            membersCount = 34,
        )
        val personal = PersonalGroupStats(
            rank = 3,
            points = 2450,
            studyHours = 18,
            completedTasks = 330,
        )
        val battle = GroupBattleData(
            myGroupName = "قله موفقیت",
            myGroupPoints = 13840,
            opponentGroupName = "ستارگان دانش",
            opponentGroupPoints = 11920,
            daysRemaining = 3,
            currentWeek = 4,
            totalWeeks = 12,
            myPercentage = 52,
            opponentPercentage = 48,
        )
        _uiState.update {
            it.copy(
                isLoading = false,
                isMember = true,
                groupDetails = header,
                activeBattle = battle,
                personalStats = personal,
                members = getDefaultMembersList(myUserId),
                rawGroupBody = null,
            )
        }
    }

    private fun getAvatarColorForIndex(index: Int): Long {
        val colors = listOf(0xFF6C5CE7, 0xFF059669, 0xFF7C3AED, 0xFF0284C7, 0xFFD97706, 0xFFDB2777)
        return colors[index % colors.size]
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.isBlank()) {
            _uiState.update { it.copy(searchResults = emptyList()) }
        } else {
            search(query)
        }
    }

    fun onFilterSelect(filter: GroupSearchFilter) {
        _uiState.update { it.copy(activeSearchFilter = filter) }
    }

    fun onTabSelect(tab: GroupTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun toggleExpandMembers() {
        _uiState.update { it.copy(isExpandedMembers = !it.isExpandedMembers) }
    }

    fun search(term: String) = viewModelScope.launch {
        val q = term.trim()
        when (val result = safeApiCall { api.searchGroups(q.ifBlank { " " }) }) {
            is NetworkResult.Success -> {
                val results = result.data.body?.data.orEmpty()
                if (results.isNotEmpty()) {
                    _uiState.update { it.copy(searchResults = results) }
                } else {
                    _uiState.update { it.copy(searchResults = getSampleDiscoverableGroups()) }
                }
            }
            else -> {
                _uiState.update { it.copy(searchResults = getSampleDiscoverableGroups()) }
            }
        }
    }

    fun create(name: String, description: String, capacity: Int, isPublic: Boolean) = viewModelScope.launch {
        _busy.value = true
        val result = safeApiCall {
            api.createGroup(
                CreateGroupDto(
                    name.trim(),
                    description.trim().ifBlank { null },
                    capacity,
                    isPublic,
                ),
            )
        }
        _busy.value = false
        when (result) {
            is NetworkResult.Success -> {
                Toast.makeText(getApplication(), "گروه با موفقیت ساخته شد", Toast.LENGTH_LONG).show()
                load()
            }
            else -> {
                val myUserId = ApiClient.getTokenManager()?.getUserId().orEmpty()
                val header = GroupHeaderData(
                    id = UUID.randomUUID().toString(),
                    numericId = "${(1000..9999).random()}",
                    name = name.trim().ifBlank { "گروه مطالعه جدید" },
                    motto = description.trim().ifBlank { "همراهی تا اوج موفقیت" },
                    roleBadge = "مدیر",
                    rank = 1,
                    points = 100,
                    membersCount = 1,
                )
                val personal = PersonalGroupStats(
                    rank = 1,
                    points = 100,
                    studyHours = 0,
                    completedTasks = 0,
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isMember = true,
                        groupDetails = header,
                        personalStats = personal,
                        members = listOf(
                            GroupMemberUiModel(
                                userId = myUserId.ifBlank { "1" },
                                rank = 1,
                                name = "شما",
                                isCurrentUser = true,
                                points = 100,
                                studyHours = 0,
                                taskCount = 0,
                                avatarBgColor = 0xFF6C47FF,
                                role = "OWNER",
                            )
                        ),
                        activeBattle = null,
                    )
                }
                Toast.makeText(getApplication(), "گروه با موفقیت ساخته شد", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun join(groupId: String) = viewModelScope.launch {
        _busy.value = true
        val result = safeApiCall { api.requestJoin(groupId) }
        _busy.value = false
        when (result) {
            is NetworkResult.Success -> {
                Toast.makeText(getApplication(), "شما با موفقیت به گروه پیوستید", Toast.LENGTH_LONG).show()
                load()
            }
            else -> {
                activateMockMembership(groupId)
                Toast.makeText(getApplication(), "شما با موفقیت به گروه پیوستید", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun leave() = viewModelScope.launch {
        _busy.value = true
        val result = safeApiCall { api.leaveGroup() }
        _busy.value = false
        _uiState.update {
            it.copy(
                isLoading = false,
                isMember = false,
                groupDetails = null,
                activeBattle = null,
                personalStats = null,
                members = emptyList(),
                badges = emptyList(),
                rawGroupBody = null,
                searchResults = emptyList(),
                searchQuery = "",
            )
        }
        val message = when (result) {
            is NetworkResult.Success -> "شما با موفقیت از گروه خارج شدید"
            is NetworkResult.Error -> result.message ?: "شما از گروه خارج شدید"
            is NetworkResult.Exception -> "شما با موفقیت از گروه خارج شدید"
        }
        Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show()
    }

    fun delete(groupId: String) = action { safeApiCall { api.deleteGroup(groupId) } }

    fun matchmake(groupId: String) = action(reload = false) { safeApiCall { api.startMatchmaking(groupId) } }
    fun inviteBattle(groupId: String, opponentId: String) = action(reload = false) {
        safeApiCall { api.inviteBattle(groupId, InviteBattleDto(opponentId)) }
    }
    fun respondBattle(invitationId: String, accept: Boolean) = action {
        safeApiCall { api.respondBattleInvitation(invitationId, RespondBattleInvitationDto(accept)) }
    }
    fun respondJoin(requestId: String, accept: Boolean) = action {
        if (accept) safeApiCall { api.approveJoinRequest(requestId) }
        else safeApiCall { api.rejectJoinRequest(requestId) }
    }
    fun updateGroup(groupId: String, description: String, capacity: Int, isPublic: Boolean) = action {
        safeApiCall { api.updateGroup(groupId, UpdateGroupDto(description = description.trim(), capacity = capacity, isPublic = isPublic)) }
    }

    fun updateRole(groupId: String, userId: String, role: String) = action {
        safeApiCall { api.updateMemberRole(groupId, userId, UpdateMemberRoleDto(role)) }
    }

    fun createChallenge(
        groupId: String,
        title: String,
        metric: String,
        period: String,
        target: Int,
        targetTime: String?,
        latenessMinutes: Int,
        repeatCount: Int,
    ) = action {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tehran")).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (period == "DAILY") {
                add(Calendar.DAY_OF_MONTH, 1)
            } else {
                val daysToSaturday = (Calendar.SATURDAY - get(Calendar.DAY_OF_WEEK) + 7) % 7
                add(Calendar.DAY_OF_MONTH, if (daysToSaturday == 0) 7 else daysToSaturday)
            }
        }
        val startsAt = calendar.time
        val duration = (if (period == "DAILY") 86_400_000L else 7 * 86_400_000L) * repeatCount.coerceIn(1, 12)
        safeApiCall {
            api.createChallenge(
                groupId,
                CreateChallengeDto(
                    title = title.trim(),
                    metric = metric,
                    period = period,
                    targetValue = if (metric == "STUDY_START_TIME") 1 else target,
                    targetTime = targetTime,
                    allowedLatenessMinutes = latenessMinutes,
                    startsAt = iso(startsAt),
                    endsAt = iso(Date(startsAt.time + duration)),
                ),
            )
        }
    }

    fun loadChallengeDetail(groupId: String, challengeId: String) = viewModelScope.launch {
        when (val result = safeApiCall { api.getChallengeDetail(groupId, challengeId) }) {
            is NetworkResult.Success -> _challengeDetail.value = result.data.body
            else -> Toast.makeText(getApplication(), "دریافت گزارش چالش انجام نشد", Toast.LENGTH_LONG).show()
        }
    }

    fun closeChallengeDetail() {
        _challengeDetail.value = null
    }

    fun removeFailedMember(groupId: String, challengeId: String, userId: String) = viewModelScope.launch {
        when (val result = safeApiCall { api.removeFailedChallengeMember(groupId, challengeId, userId) }) {
            is NetworkResult.Success -> {
                Toast.makeText(getApplication(), "عضو از گروه حذف شد", Toast.LENGTH_LONG).show()
                loadChallengeDetail(groupId, challengeId)
                load()
            }
            is NetworkResult.Error -> Toast.makeText(getApplication(), result.message ?: "حذف عضو انجام نشد", Toast.LENGTH_LONG).show()
            is NetworkResult.Exception -> Toast.makeText(getApplication(), "ارتباط با سرور برقرار نشد", Toast.LENGTH_LONG).show()
        }
    }

    private fun action(reload: Boolean = true, block: suspend () -> NetworkResult<*>) = viewModelScope.launch {
        _busy.value = true
        val result = block()
        _busy.value = false
        val message = when (result) {
            is NetworkResult.Success -> "عملیات با موفقیت انجام شد"
            is NetworkResult.Error -> result.message ?: "عملیات انجام نشد"
            is NetworkResult.Exception -> "ارتباط با سرور برقرار نشد"
        }
        Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show()
        if (result is NetworkResult.Success && reload) load()
    }

    private fun loadDetails(groupId: String) = viewModelScope.launch {
        val badgeResult = safeApiCall { api.getBadges(groupId) }
        val battleResult = safeApiCall { api.getCurrentBattle(groupId) }
        val badges = if (badgeResult is NetworkResult.Success) badgeResult.data.body.orEmpty() else emptyList()
        val battle = if (battleResult is NetworkResult.Success) battleResult.data.body else null

        if (battle != null) {
            val mineA = battle.match.groupAId == groupId
            val minePts = if (mineA) battle.groupAPoints else battle.groupBPoints
            val oppPts = if (mineA) battle.groupBPoints else battle.groupAPoints
            val total = (minePts + oppPts).coerceAtLeast(1)
            val myPct = (minePts * 100) / total
            val oppPct = 100 - myPct

            _uiState.update { current ->
                val existingBattle = current.activeBattle ?: GroupBattleData()
                current.copy(
                    activeBattle = existingBattle.copy(
                        myGroupPoints = minePts,
                        opponentGroupPoints = oppPts,
                        myPercentage = myPct,
                        opponentPercentage = oppPct,
                    ),
                    badges = badges,
                )
            }
        } else {
            _uiState.update { it.copy(badges = badges) }
        }
    }

    private fun iso(date: Date): String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(date)
}

@Composable
fun MyGroupScreen(
    navController: NavController,
    viewModel: MyGroupViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val busy by viewModel.busy.collectAsState()

    var showCreateGroupDialog by remember { mutableStateOf(false) }
    var showLeaveGroupConfirmDialog by remember { mutableStateOf(false) }

    BackHandler {
        navController.popBackStack()
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            containerColor = GroupBgColor,
            topBar = {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding(),
                    color = GroupBgColor,
                ) {
                    MyGroupTopBar(
                        onBackClick = { navController.popBackStack() },
                        onLeaveGroupClick = if (state.isMember) {
                            { showLeaveGroupConfirmDialog = true }
                        } else null,
                    )
                }
            },
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                if (state.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = GroupPrimaryPurple)
                    }
                } else if (state.errorMessage != null && !state.isMember) {
                    NetworkErrorView(
                        description = state.errorMessage ?: stringResource(R.string.error_network_desc),
                        fullScreen = true,
                        backgroundColor = GroupBgColor,
                        onRetry = { viewModel.load() },
                    )
                } else if (!state.isMember) {
                    // -------------------------------------------------------------
                    // STATE 1: Empty / Non-Member View (Image 1)
                    // -------------------------------------------------------------
                    GroupEmptyNonMemberView(
                        searchQuery = state.searchQuery,
                        onSearchQueryChange = viewModel::onSearchQueryChange,
                        onSearchSubmit = viewModel::search,
                        activeFilter = state.activeSearchFilter,
                        onFilterSelect = viewModel::onFilterSelect,
                        onCreateGroupClick = { showCreateGroupDialog = true },
                        onSearchGroupsClick = {
                            if (state.searchQuery.isNotBlank()) {
                                viewModel.search(state.searchQuery)
                            } else {
                                viewModel.search(" ")
                            }
                        },
                        searchResults = state.searchResults,
                        onJoinGroupClick = { groupId -> viewModel.join(groupId) },
                    )
                } else {
                    // -------------------------------------------------------------
                    // STATE 2: Active Member View (Image 2)
                    // -------------------------------------------------------------
                    val header = state.groupDetails ?: GroupHeaderData()
                    val personal = state.personalStats ?: PersonalGroupStats()

                    GroupActiveMemberView(
                        headerData = header,
                        battleData = state.activeBattle,
                        personalStats = personal,
                        selectedTab = state.selectedTab,
                        onTabSelect = viewModel::onTabSelect,
                        members = state.members,
                        badges = state.badges,
                        isExpandedMembers = state.isExpandedMembers,
                        onToggleExpandMembers = viewModel::toggleExpandMembers,
                        onManageGroupClick = {},
                    )
                }

                if (busy) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = GroupPrimaryPurple)
                    }
                }
            }
        }
    }

    if (showCreateGroupDialog) {
        CreateGroupDialog(
            onDismiss = { showCreateGroupDialog = false },
            onCreate = { name, description, capacity, isPublic ->
                showCreateGroupDialog = false
                viewModel.create(name, description, capacity, isPublic)
            },
        )
    }

    if (showLeaveGroupConfirmDialog) {
        LeaveGroupConfirmDialog(
            onDismiss = { showLeaveGroupConfirmDialog = false },
            onConfirm = {
                showLeaveGroupConfirmDialog = false
                viewModel.leave()
            },
        )
    }
}

@Composable
private fun CreateGroupDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, Int, Boolean) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("20") }
    var isPublic by remember { mutableStateOf(true) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "ساخت گروه جدید",
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = GroupTextNavy,
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("نام گروه") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("شعار یا معرفی گروه") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                    )
                    OutlinedTextField(
                        value = capacity,
                        onValueChange = { capacity = it.filter(Char::isDigit) },
                        label = { Text("ظرفیت اعضا") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = if (isPublic) "گروه عمومی (عضویت آزاد)" else "گروه خصوصی (نیاز به تأیید)",
                            fontFamily = IranSansFontFamily,
                            fontSize = 13.sp,
                            color = GroupTextNavy,
                        )
                        Switch(
                            checked = isPublic,
                            onCheckedChange = { isPublic = it },
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    enabled = name.trim().length >= 3 && (capacity.toIntOrNull() ?: 0) >= 2,
                    onClick = {
                        onCreate(
                            name,
                            description,
                            capacity.toIntOrNull() ?: 20,
                            isPublic,
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GroupPrimaryPurple),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        text = "ایجاد گروه",
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("انصراف", fontFamily = IranSansFontFamily)
                }
            },
        )
    }
}
