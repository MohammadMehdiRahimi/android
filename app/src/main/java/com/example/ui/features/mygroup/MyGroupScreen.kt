package com.example.ui.features.mygroup

import android.app.Application
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.R
import com.example.network.ApiClient
import com.example.network.BadgeListResponseDto
import com.example.network.BattleHistoryResponseDto
import com.example.network.BattleInvitationDto
import com.example.network.ChallengeDto
import com.example.network.ChallengeDetailBodyDto
import com.example.network.CreateChallengeDto
import com.example.network.CreateGroupDto
import com.example.network.CurrentBattleBody
import com.example.network.GroupBadgeDto
import com.example.network.GroupMemberDto
import com.example.network.InviteBattleDto
import com.example.network.JoinRequestDto
import com.example.network.MyGroupBody
import com.example.network.NetworkResult
import com.example.network.StudyGroupDto
import com.example.network.RespondBattleInvitationDto
import com.example.network.UpdateGroupDto
import com.example.network.UpdateMemberRoleDto
import com.example.network.safeApiCall
import com.example.ui.core.components.NetworkErrorView
import com.example.ui.theme.IranSansFontFamily
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

private val Purple = Color(0xFF8358E8)
private val Navy = Color(0xFF162052)
private val PageBackground = Color(0xFFF7F7FB)

sealed interface MyGroupUiState {
    data object Loading : MyGroupUiState
    data object Empty : MyGroupUiState
    data class Ready(
        val data: MyGroupBody,
        val challenges: List<ChallengeDto> = emptyList(),
        val badges: List<GroupBadgeDto> = emptyList(),
        val currentBattle: CurrentBattleBody? = null,
        val battleHistoryCount: Int = 0,
        val battleInvitations: List<BattleInvitationDto> = emptyList(),
        val joinRequests: List<JoinRequestDto> = emptyList(),
    ) : MyGroupUiState
    data class Error(val message: String) : MyGroupUiState
}

class MyGroupViewModel(application: Application) : AndroidViewModel(application) {
    private val api = ApiClient.apiService
    private val _state = MutableStateFlow<MyGroupUiState>(MyGroupUiState.Loading)
    val state: StateFlow<MyGroupUiState> = _state.asStateFlow()
    private val _searchResults = MutableStateFlow<List<StudyGroupDto>>(emptyList())
    val searchResults: StateFlow<List<StudyGroupDto>> = _searchResults.asStateFlow()
    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy.asStateFlow()
    private val _challengeDetail = MutableStateFlow<ChallengeDetailBodyDto?>(null)
    val challengeDetail: StateFlow<ChallengeDetailBodyDto?> = _challengeDetail.asStateFlow()

    init { load() }

    fun load() = viewModelScope.launch {
        _state.value = MyGroupUiState.Loading
        when (val result = safeApiCall { api.getMyGroup() }) {
            is NetworkResult.Success -> {
                val body = result.data.body
                if (body == null) _state.value = MyGroupUiState.Empty
                else {
                    _state.value = MyGroupUiState.Ready(body)
                    loadDetails(body.group.id)
                }
            }
            is NetworkResult.Error -> {
                _state.value = if (result.code == 204 || result.code == 404) {
                    MyGroupUiState.Empty
                } else {
                    MyGroupUiState.Error(result.message ?: "دریافت گروه انجام نشد")
                }
            }
            is NetworkResult.Exception -> _state.value = MyGroupUiState.Error("ارتباط با سرور برقرار نشد")
        }
    }

    fun search(term: String) = viewModelScope.launch {
        if (term.isBlank()) { _searchResults.value = emptyList(); return@launch }
        when (val result = safeApiCall { api.searchGroups(term.trim()) }) {
            is NetworkResult.Success -> _searchResults.value = result.data.body?.data.orEmpty()
            else -> _searchResults.value = emptyList()
        }
    }

    fun create(name: String, description: String, capacity: Int, isPublic: Boolean) = action {
        safeApiCall { api.createGroup(CreateGroupDto(name.trim(), description.trim().ifBlank { null }, capacity, isPublic)) }
    }

    fun join(groupId: String) = action { safeApiCall { api.requestJoin(groupId) } }
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

    fun uploadProfileImage(groupId: String, uri: Uri) = viewModelScope.launch {
        val resolver = getApplication<Application>().contentResolver
        val contentType = resolver.getType(uri)?.lowercase()
        if (
            contentType == null ||
            contentType !in setOf("image/png", "image/jpeg", "image/webp")
        ) {
            Toast.makeText(getApplication(), "فرمت عکس باید PNG، JPG یا WebP باشد", Toast.LENGTH_LONG).show()
            return@launch
        }
        val bytes = resolver.openInputStream(uri)?.use { it.readBytes() }
        if (bytes == null || bytes.isEmpty() || bytes.size > 10 * 1024 * 1024) {
            Toast.makeText(getApplication(), "حجم عکس باید کمتر از ۱۰ مگابایت باشد", Toast.LENGTH_LONG).show()
            return@launch
        }
        val extension = when (contentType) {
            "image/png" -> "png"
            "image/webp" -> "webp"
            else -> "jpg"
        }
        val part = MultipartBody.Part.createFormData(
            "file",
            "group-profile.$extension",
            bytes.toRequestBody(contentType.toMediaType()),
        )
        action { safeApiCall { api.uploadGroupProfileImage(groupId, part) } }.join()
    }
    fun leave() = action { safeApiCall { api.leaveGroup() } }
    fun delete(groupId: String) = action { safeApiCall { api.deleteGroup(groupId) } }
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
                    title = title.trim(), metric = metric, period = period,
                    targetValue = if (metric == "STUDY_START_TIME") 1 else target,
                    targetTime = targetTime,
                    allowedLatenessMinutes = latenessMinutes,
                    startsAt = iso(startsAt), endsAt = iso(Date(startsAt.time + duration)),
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

    fun closeChallengeDetail() { _challengeDetail.value = null }

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
        val challengeResult = safeApiCall { api.getChallenges(groupId) }
        val badgeResult = safeApiCall { api.getBadges(groupId) }
        val battleResult = safeApiCall { api.getCurrentBattle(groupId) }
        val historyResult = safeApiCall { api.getBattleHistory(groupId) }
        val invitationResult = safeApiCall { api.getBattleInvitations(groupId) }
        val joinRequestResult = safeApiCall { api.getJoinRequests(groupId) }
        val challenges = if (challengeResult is NetworkResult.Success) challengeResult.data.body.orEmpty() else emptyList()
        val badges = if (badgeResult is NetworkResult.Success) badgeResult.data.body.orEmpty() else emptyList()
        val battle = if (battleResult is NetworkResult.Success) battleResult.data.body else null
        val history = if (historyResult is NetworkResult.Success) historyResult.data.body?.total ?: 0 else 0
        val invitations = if (invitationResult is NetworkResult.Success) invitationResult.data.body.orEmpty() else emptyList()
        val joinRequests = if (joinRequestResult is NetworkResult.Success) joinRequestResult.data.body.orEmpty() else emptyList()
        val current = _state.value as? MyGroupUiState.Ready ?: return@launch
        _state.value = current.copy(
            challenges = challenges,
            badges = badges,
            currentBattle = battle,
            battleHistoryCount = history,
            battleInvitations = invitations,
            joinRequests = joinRequests,
        )
    }

    private fun iso(date: Date): String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(date)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyGroupScreen(navController: NavController, viewModel: MyGroupViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    val results by viewModel.searchResults.collectAsState()
    val busy by viewModel.busy.collectAsState()
    Scaffold(
        containerColor = PageBackground,
        topBar = {
            TopAppBar(
                title = { Text("گروه من", fontFamily = IranSansFontFamily, fontWeight = FontWeight.ExtraBold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
                actions = { IconButton(onClick = viewModel::load) { Icon(Icons.Default.Refresh, "بازخوانی") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (val current = state) {
                MyGroupUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center), color = Purple)
                MyGroupUiState.Empty -> EmptyGroupContent(results, viewModel)
                is MyGroupUiState.Ready -> GroupContent(current, viewModel)
                is MyGroupUiState.Error -> ErrorContent(current.message, viewModel::load)
            }
            if (busy) Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = .15f)), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Purple)
            }
        }
    }
}

@Composable
private fun EmptyGroupContent(results: List<StudyGroupDto>, vm: MyGroupViewModel) {
    var search by remember { mutableStateOf("") }
    var showCreate by remember { mutableStateOf(false) }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Spacer(Modifier.height(14.dp))
            Image(
                painter = painterResource(R.drawable.study_group),
                contentDescription = "ساخت یا پیدا کردن گروه مطالعه",
                modifier = Modifier.fillMaxWidth().height(260.dp),
                contentScale = ContentScale.Fit,
            )
            Text("هنوز عضو هیچ گروهی نیستی!", fontFamily = IranSansFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 19.sp, color = Navy)
            Text("یک گروه بساز یا با نام، شناسه و مدال گروه جست‌وجو کن.", textAlign = TextAlign.Center, fontFamily = IranSansFontFamily, color = Color.Gray)
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("نام، شناسه یا مدال گروه") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = { vm.search(search) }, colors = ButtonDefaults.buttonColors(containerColor = Purple)) { Text("جست‌وجو") }
            }
        }
        item {
            Button(
                onClick = { showCreate = true },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Navy),
                shape = RoundedCornerShape(18.dp),
            ) { Icon(Icons.Default.Add, null); Spacer(Modifier.width(8.dp)); Text("ساخت گروه جدید", fontFamily = IranSansFontFamily) }
        }
        items(results, key = { it.id }) { group -> GroupSearchCard(group) { vm.join(group.id) } }
        item { Spacer(Modifier.height(30.dp)) }
    }
    if (showCreate) CreateGroupDialog(onDismiss = { showCreate = false }) { name, description, capacity, isPublic ->
        showCreate = false
        vm.create(name, description, capacity, isPublic)
    }
}

@Composable
private fun GroupSearchCard(group: StudyGroupDto, onJoin: () -> Unit) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            GroupProfileImage(group, 52)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(group.name, fontFamily = IranSansFontFamily, fontWeight = FontWeight.Bold, color = Navy)
                Text("شناسه: ${group.inviteCode} • ${if (group.isPublic) "عمومی" else "خصوصی"}", fontFamily = IranSansFontFamily, fontSize = 11.sp, color = Color.Gray)
            }
            OutlinedButton(onClick = onJoin, shape = RoundedCornerShape(14.dp)) { Text(if (group.isPublic) "عضویت" else "درخواست") }
        }
    }
}

@Composable
private fun GroupContent(state: MyGroupUiState.Ready, vm: MyGroupViewModel) {
    var tab by remember { mutableStateOf(0) }
    var showChallenge by remember { mutableStateOf(false) }
    var showBattle by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    val challengeDetail by vm.challengeDetail.collectAsState()
    val group = state.data.group
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { vm.uploadProfileImage(group.id, it) }
    }
    val canManage = group.ownerId == ApiClient.getTokenManager()?.getUserId() || state.data.member.role == "CO_ADMIN"
    LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { GroupHero(state) }
        state.currentBattle?.let { battle -> item { BattleCard(group.id, battle) } }
        item { StatsRow(state.data) }
        if (canManage) item {
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (state.currentBattle == null) {
                    Button(onClick = { showBattle = true }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(Purple), shape = RoundedCornerShape(16.dp)) {
                        Icon(Icons.Default.Bolt, null); Text(" نبرد گروهی")
                    }
                }
                OutlinedButton(onClick = { showChallenge = true }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp)) { Text("تعریف چالش") }
            }
        }
        item {
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (group.ownerId == ApiClient.getTokenManager()?.getUserId()) {
                    OutlinedButton(onClick = { showSettings = true }, modifier = Modifier.weight(1f)) { Text("تنظیمات گروه") }
                    OutlinedButton(onClick = { vm.delete(group.id) }, modifier = Modifier.weight(1f)) { Text("حذف گروه", color = Color(0xFFC43A3A)) }
                } else {
                    OutlinedButton(onClick = vm::leave, modifier = Modifier.fillMaxWidth()) { Text("خروج از گروه", color = Color(0xFFC43A3A)) }
                }
            }
        }
        if (canManage) items(state.battleInvitations, key = { "battle-${it.id}" }) { invitation ->
            ApprovalCard(
                title = "دعوت به نبرد از گروه ${invitation.challengerGroupId.take(8)}",
                accept = { vm.respondBattle(invitation.id, true) },
                reject = { vm.respondBattle(invitation.id, false) },
            )
        }
        if (canManage) items(state.joinRequests, key = { "join-${it.id}" }) { request ->
            ApprovalCard(
                title = "درخواست عضویت ${request.userId.take(8)}",
                accept = { vm.respondJoin(request.id, true) },
                reject = { vm.respondJoin(request.id, false) },
            )
        }
        item {
            val tabs = listOf("اعضا", "چالش‌ها", "مدال‌ها", "نبردها")
            TabRow(selectedTabIndex = tab, containerColor = Color.White, contentColor = Purple) {
                tabs.forEachIndexed { index, title -> Tab(tab == index, { tab = index }, text = { Text(title, fontFamily = IranSansFontFamily, fontSize = 11.sp) }) }
            }
        }
        when (tab) {
            0 -> items(state.data.members, key = { it.userId }) { member ->
                MemberCard(
                    member,
                    group.ownerId,
                    canChangeRole = group.ownerId == ApiClient.getTokenManager()?.getUserId(),
                    onRoleChange = {
                        vm.updateRole(group.id, member.userId, if (member.role == "CO_ADMIN") "MEMBER" else "CO_ADMIN")
                    },
                )
            }
            1 -> items(state.challenges, key = { it.id }) { challenge ->
                ChallengeCard(challenge) { vm.loadChallengeDetail(group.id, challenge.id) }
            }
            2 -> items(state.badges, key = { it.id }) { BadgeCard(it) }
            else -> item { SimpleInfoCard("تاریخچه ${state.battleHistoryCount} نبرد ثبت شده است.") }
        }
        item { Spacer(Modifier.height(30.dp)) }
    }
    if (showChallenge) ChallengeDialog(onDismiss = { showChallenge = false }) { title, metric, period, target, targetTime, lateness, repeats ->
        showChallenge = false
        vm.createChallenge(group.id, title, metric, period, target, targetTime, lateness, repeats)
    }
    challengeDetail?.let { detail ->
        ChallengeProgressDialog(
            detail = detail,
            onDismiss = vm::closeChallengeDetail,
            onRemove = { userId -> vm.removeFailedMember(group.id, detail.challenge.id, userId) },
        )
    }
    if (showBattle) BattleDialog(group, vm, onDismiss = { showBattle = false })
    if (showSettings) {
        GroupSettingsDialog(
            group = group,
            onDismiss = { showSettings = false },
            onPickImage = { imagePicker.launch("image/*") },
        ) { description, capacity, isPublic ->
            showSettings = false
            vm.updateGroup(group.id, description, capacity, isPublic)
        }
    }
}

@Composable
private fun GroupHero(state: MyGroupUiState.Ready) {
    val group = state.data.group
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(Color.Transparent),
    ) {
        Column(Modifier.background(Brush.linearGradient(listOf(Navy, Color(0xFF4A2B82)))).padding(22.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                GroupProfileImage(group, 62)
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(group.name, color = Color.White, fontFamily = IranSansFontFamily, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
                    Text("#${group.inviteCode} • ${state.data.members.size}/${group.capacity} عضو", color = Color.White.copy(alpha = .75f), fontSize = 12.sp)
                }
                Icon(if (group.isPublic) Icons.Default.Public else Icons.Default.Lock, null, tint = Color.White)
            }
            Spacer(Modifier.height(18.dp))
            Text(group.description ?: "با هم می‌خوانیم، پیشرفت می‌کنیم و می‌جنگیم.", color = Color.White.copy(alpha = .9f), fontFamily = IranSansFontFamily)
            Spacer(Modifier.height(12.dp))
            Text("امتیاز دائمی گروه: ${group.totalGroupPoints}", color = Color(0xFFFFD66B), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun StatsRow(data: MyGroupBody) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        StatCard("امتیاز هفته", data.weeklyStats.points.toString(), Modifier.weight(1f))
        StatCard("مطالعه", formatMinutes(data.weeklyStats.studyMinutes), Modifier.weight(1f))
        StatCard("تست", data.weeklyStats.testCount.toString(), Modifier.weight(1f))
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier) {
    Card(modifier, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(Color.White)) {
        Column(Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = Purple, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
            Text(label, color = Color.Gray, fontFamily = IranSansFontFamily, fontSize = 10.sp)
        }
    }
}

@Composable
private fun MemberCard(member: GroupMemberDto, ownerId: String, canChangeRole: Boolean, onRoleChange: () -> Unit) {
    Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(Color.White)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).background(Purple.copy(alpha = .12f), CircleShape), contentAlignment = Alignment.Center) { Text(member.fullName.take(1), color = Purple, fontWeight = FontWeight.Bold) }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(member.fullName, fontFamily = IranSansFontFamily, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(if (member.userId == ownerId) "مدیر گروه" else if (member.role == "CO_ADMIN") "کمک‌مدیر" else "عضو", color = Color.Gray, fontSize = 11.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${member.points} امتیاز", color = Purple, fontWeight = FontWeight.Bold)
                Text("${formatMinutes(member.studyMinutes)} • ${member.testCount} تست", fontSize = 10.sp, color = Color.Gray)
                if (canChangeRole && member.userId != ownerId) {
                    Text(
                        if (member.role == "CO_ADMIN") "حذف کمک‌مدیریت" else "ارتقا به کمک‌مدیر",
                        modifier = Modifier.clickable(onClick = onRoleChange).padding(top = 4.dp),
                        fontSize = 9.sp,
                        color = Purple,
                    )
                }
            }
        }
    }
}

@Composable
private fun ChallengeCard(challenge: ChallengeDto, onClick: () -> Unit) {
    val progress = challenge.progress
    Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp).clickable(onClick = onClick), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(Color.White)) {
        Column(Modifier.padding(16.dp)) {
            Row { Text(challenge.title, Modifier.weight(1f), fontFamily = IranSansFontFamily, fontWeight = FontWeight.Bold); Text(statusFa(challenge.status), color = if (challenge.status == "SUCCEEDED") Color(0xFF19A66A) else Purple, fontSize = 11.sp) }
            Spacer(Modifier.height(6.dp))
            val targetText = when (challenge.metric) {
                "TEST_COUNT" -> "${challenge.targetValue} تست"
                "STUDY_START_TIME" -> "شروع در ${challenge.targetTime ?: "زمان تعیین‌شده"} با ${challenge.allowedLatenessMinutes} دقیقه مهلت"
                else -> "${challenge.targetValue} دقیقه مطالعه"
            }
            Text("هدف هر عضو: $targetText • انجام برای همه اجباری است", color = Color.Gray, fontSize = 11.sp)
            progress?.let { Text("${it.completedMemberCount} از ${it.requiredMemberCount} عضو انجام داده‌اند", fontSize = 11.sp, color = Navy) }
            Text("مشاهده گزارش اعضا", color = Purple, fontSize = 10.sp, modifier = Modifier.padding(top = 6.dp))
        }
    }
}

@Composable
private fun BadgeCard(badge: GroupBadgeDto) {
    Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(Color.White)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).background(Color(0xFFFFE7A6), CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Default.Star, null, tint = Color(0xFFF5A623)) }
            Spacer(Modifier.width(12.dp)); Column { Text(badge.badge.name, fontFamily = IranSansFontFamily, fontWeight = FontWeight.Bold); Text(badge.badge.description ?: "این مدال برای همیشه ثبت شده است.", color = Color.Gray, fontSize = 11.sp) }
        }
    }
}

@Composable
private fun BattleCard(groupId: String, battle: CurrentBattleBody) {
    val mineA = battle.match.groupAId == groupId
    val mine = if (mineA) battle.groupAPoints else battle.groupBPoints
    val rival = if (mineA) battle.groupBPoints else battle.groupAPoints
    Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color(0xFFFFF4E9))) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(if (battle.match.status == "SCHEDULED") "نبرد برنامه‌ریزی‌شده" else "نبرد گروهی فعال", color = Color(0xFFD56B1A), fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp)); Text("$mine  ⚡  $rival", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Navy)
        }
    }
}

@Composable
private fun ApprovalCard(title: String, accept: () -> Unit, reject: () -> Unit) {
    Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp), colors = CardDefaults.cardColors(Color.White), shape = RoundedCornerShape(18.dp)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(title, Modifier.weight(1f), fontFamily = IranSansFontFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            OutlinedButton(onClick = reject) { Text("رد") }
            Spacer(Modifier.width(6.dp))
            Button(onClick = accept, colors = ButtonDefaults.buttonColors(Purple)) { Text("قبول") }
        }
    }
}

@Composable
private fun CreateGroupDialog(onDismiss: () -> Unit, onCreate: (String, String, Int, Boolean) -> Unit) {
    var name by remember { mutableStateOf("") }; var description by remember { mutableStateOf("") }; var capacity by remember { mutableStateOf("20") }; var isPublic by remember { mutableStateOf(true) }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("ساخت گروه", fontFamily = IranSansFontFamily) }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(name, { name = it }, label = { Text("نام گروه") }, singleLine = true)
            OutlinedTextField(description, { description = it }, label = { Text("بیو گروه") })
            OutlinedTextField(capacity, { capacity = it.filter(Char::isDigit) }, label = { Text("ظرفیت") }, singleLine = true)
            Row(verticalAlignment = Alignment.CenterVertically) { Text(if (isPublic) "گروه عمومی" else "گروه خصوصی", Modifier.weight(1f)); Switch(isPublic, { isPublic = it }) }
        }
    }, confirmButton = { Button(enabled = name.trim().length >= 3 && (capacity.toIntOrNull() ?: 0) >= 2, onClick = { onCreate(name, description, capacity.toIntOrNull() ?: 20, isPublic) }) { Text("ساخت") } }, dismissButton = { OutlinedButton(onClick = onDismiss) { Text("انصراف") } })
}

@Composable
private fun GroupSettingsDialog(
    group: StudyGroupDto,
    onDismiss: () -> Unit,
    onPickImage: () -> Unit,
    onSave: (String, Int, Boolean) -> Unit,
) {
    var description by remember { mutableStateOf(group.description.orEmpty()) }
    var capacity by remember { mutableStateOf(group.capacity.toString()) }
    var isPublic by remember { mutableStateOf(group.isPublic) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تنظیمات گروه") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GroupProfileImage(group, 58)
                    Spacer(Modifier.width(10.dp))
                    OutlinedButton(onClick = onPickImage) { Text("انتخاب عکس پروفایل") }
                }
                OutlinedTextField(description, { description = it }, label = { Text("بیو گروه") })
                OutlinedTextField(capacity, { capacity = it.filter(Char::isDigit) }, label = { Text("ظرفیت") })
                Row(verticalAlignment = Alignment.CenterVertically) { Text(if (isPublic) "عمومی" else "خصوصی", Modifier.weight(1f)); Switch(isPublic, { isPublic = it }) }
            }
        },
        confirmButton = { Button(onClick = { onSave(description, capacity.toIntOrNull() ?: group.capacity, isPublic) }) { Text("ذخیره") } },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("انصراف") } },
    )
}

@Composable
private fun GroupProfileImage(group: StudyGroupDto, size: Int) {
    val imageUrl = ApiClient.resolveUrl(group.profileImageUrl)
    if (imageUrl != null) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "عکس پروفایل ${group.name}",
            modifier = Modifier.size(size.dp).clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
    } else {
        Box(
            Modifier
                .size(size.dp)
                .background(
                    Brush.linearGradient(listOf(Purple, Color(0xFFB76BE8))),
                    CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Default.Groups,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size((size * 0.55f).dp),
            )
        }
    }
}

@Composable
private fun ChallengeDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, String, Int, String?, Int, Int) -> Unit,
) {
    val metrics = listOf("TEST_COUNT", "STUDY_MINUTES", "STUDY_START_TIME")
    val labels = listOf("تعداد تست", "زمان مطالعه", "شروع سر ساعت")
    var title by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("100") }
    var metricIndex by remember { mutableStateOf(0) }
    var weekly by remember { mutableStateOf(false) }
    var targetTime by remember { mutableStateOf("07:00") }
    var lateness by remember { mutableStateOf("0") }
    var repeats by remember { mutableStateOf("1") }
    val metric = metrics[metricIndex]
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("چالش اجباری جدید") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(title, { title = it }, label = { Text("عنوان") })
                Row {
                    OutlinedButton({ metricIndex = (metricIndex + 1) % metrics.size }) { Text(labels[metricIndex]) }
                    Spacer(Modifier.width(8.dp))
                    OutlinedButton({ weekly = !weekly }) { Text(if (weekly) "هفتگی" else "روزانه") }
                }
                if (metric == "STUDY_START_TIME") {
                    OutlinedTextField(targetTime, { targetTime = it.take(5) }, label = { Text("ساعت شروع، مثل 07:00") })
                    OutlinedTextField(lateness, { lateness = it.filter(Char::isDigit) }, label = { Text("مهلت تأخیر به دقیقه") })
                } else {
                    OutlinedTextField(target, { target = it.filter(Char::isDigit) }, label = { Text(if (metric == "TEST_COUNT") "تعداد تست" else "دقایق مطالعه") })
                }
                OutlinedTextField(
                    repeats,
                    { repeats = it.filter(Char::isDigit) },
                    label = { Text(if (weekly) "تعداد هفته‌های تکرار" else "تعداد روزهای تکرار") },
                )
                Text("تمام اعضایی که در زمان شروع عضو گروه باشند باید هر تکرار را انجام دهند.", fontSize = 10.sp, color = Color.Gray)
            }
        },
        confirmButton = {
            Button(
                enabled = title.trim().length >= 2 && (metric != "STUDY_START_TIME" || Regex("^([01]\\d|2[0-3]):[0-5]\\d$").matches(targetTime)),
                onClick = {
                    onCreate(
                        title,
                        metric,
                        if (weekly) "WEEKLY" else "DAILY",
                        target.toIntOrNull()?.coerceAtLeast(1) ?: 1,
                        targetTime.takeIf { metric == "STUDY_START_TIME" },
                        lateness.toIntOrNull()?.coerceIn(0, 1440) ?: 0,
                        repeats.toIntOrNull()?.coerceIn(1, 12) ?: 1,
                    )
                },
            ) { Text("ثبت") }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("انصراف") } },
    )
}

@Composable
private fun ChallengeProgressDialog(
    detail: ChallengeDetailBodyDto,
    onDismiss: () -> Unit,
    onRemove: (String) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("گزارش ${detail.challenge.title}") },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().height(420.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    Text("انجام‌شده، انجام‌نشده و میزان پیشرفت هر عضو", fontSize = 11.sp, color = Color.Gray)
                }
                items(detail.members, key = { it.userId }) { member ->
                    Card(colors = CardDefaults.cardColors(Color(0xFFF7F7FB)), shape = RoundedCornerShape(12.dp)) {
                        Row(Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(member.fullName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(
                                    "${member.actualValue} از ${member.targetValue} • ${challengeMemberStatusFa(member.status)}",
                                    color = if (member.status == "FAILED") Color(0xFFC43A3A) else Color.Gray,
                                    fontSize = 10.sp,
                                )
                            }
                            if (member.canRemove) {
                                OutlinedButton(onClick = { onRemove(member.userId) }) {
                                    Text("اخراج", color = Color(0xFFC43A3A), fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { Button(onClick = onDismiss) { Text("بستن") } },
    )
}

@Composable
private fun BattleDialog(group: StudyGroupDto, vm: MyGroupViewModel, onDismiss: () -> Unit) {
    val results by vm.searchResults.collectAsState(); var search by remember { mutableStateOf("") }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("نبرد گروهی") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { vm.matchmake(group.id); onDismiss() }, modifier = Modifier.fillMaxWidth()) { Text("حریف‌یابی خودکار") }
            OutlinedTextField(search, { search = it }, label = { Text("جست‌وجوی گروه حریف") }, trailingIcon = { Icon(Icons.Default.Search, null, Modifier.clickable { vm.search(search) }) })
            results.filter { it.id != group.id }.take(4).forEach { rival ->
                Row(Modifier.fillMaxWidth().clickable { vm.inviteBattle(group.id, rival.id); onDismiss() }.padding(8.dp)) { Text(rival.name, Modifier.weight(1f)); Text("دعوت", color = Purple) }
            }
        }
    }, confirmButton = {}, dismissButton = { OutlinedButton(onClick = onDismiss) { Text("بستن") } })
}

@Composable private fun SimpleInfoCard(text: String) { Card(Modifier.fillMaxWidth().padding(16.dp), colors = CardDefaults.cardColors(Color.White)) { Text(text, Modifier.padding(20.dp), fontFamily = IranSansFontFamily) } }
@Composable private fun ErrorContent(message: String, retry: () -> Unit) {
    NetworkErrorView(
        description = message,
        fullScreen = true,
        backgroundColor = PageBackground,
        onRetry = retry
    )
}
private fun formatMinutes(value: Int) = if (value < 60) "$value دقیقه" else "${value / 60}:${(value % 60).toString().padStart(2, '0')} ساعت"
private fun statusFa(status: String) = when (status) { "ACTIVE" -> "فعال"; "SUCCEEDED" -> "موفق"; "FAILED" -> "ناموفق"; else -> status }
private fun challengeMemberStatusFa(status: String) = when (status) {
    "SUCCEEDED" -> "انجام‌شده"
    "FAILED" -> "انجام‌نشده"
    "IN_PROGRESS" -> "در حال انجام"
    else -> "شروع‌نشده"
}
