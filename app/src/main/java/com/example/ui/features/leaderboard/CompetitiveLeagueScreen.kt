package com.example.ui.features.leaderboard

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.network.ActivityWindowDto
import com.example.network.ApiClient
import com.example.network.FeedbackOptionDto
import com.example.network.LeagueLeaderboardBodyDto
import com.example.network.LeagueMemberDto
import com.example.network.MemberActivityBodyDto
import com.example.network.NetworkResult
import com.example.network.SendPeerFeedbackDto
import com.example.network.safeApiCall
import com.example.ui.theme.IranSansFontFamily

private val LeaguePurple = Color(0xFF7357F5)
private val LeagueNavy = Color(0xFF172554)

@Composable
fun CompetitiveLeagueScreen(navController: NavController) {
    var leaderboard by remember { mutableStateOf<LeagueLeaderboardBodyDto?>(null) }
    var selectedMember by remember { mutableStateOf<LeagueMemberDto?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var reload by remember { mutableStateOf(0) }
    var page by remember { mutableStateOf(1) }
    var loadingMore by remember { mutableStateOf(false) }

    LaunchedEffect(reload, page) {
        error = null
        if (page > 1) loadingMore = true
        when (val result = safeApiCall { ApiClient.apiService.getCurrentLeagueLeaderboard(page = page) }) {
            is NetworkResult.Success -> result.data.body?.let { body ->
                leaderboard = if (page == 1 || leaderboard == null) body else body.copy(
                    data = leaderboard!!.data + body.data,
                )
            }
            is NetworkResult.Error -> error = result.message
            is NetworkResult.Exception -> error = "ارتباط با سرور برقرار نشد"
        }
        loadingMore = false
    }

    Column(Modifier.fillMaxSize().background(Color(0xFFF7F8FC))) {
        Row(
            Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "بازگشت", tint = LeagueNavy)
            }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("لیگ‌های رقابتی", fontFamily = IranSansFontFamily, fontWeight = FontWeight.ExtraBold, color = LeagueNavy, fontSize = 18.sp)
                leaderboard?.league?.nameFa?.let { Text(it, fontFamily = IranSansFontFamily, color = LeaguePurple, fontSize = 11.sp) }
            }
            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = LeaguePurple, modifier = Modifier.size(32.dp))
        }
        when {
            leaderboard == null && error == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = LeaguePurple) }
            error != null -> Box(Modifier.fillMaxSize().clickable { reload++ }, contentAlignment = Alignment.Center) {
                Text("$error\nبرای تلاش دوباره لمس کنید", fontFamily = IranSansFontFamily, color = Color(0xFFC43E3E))
            }
            else -> LeagueList(
                data = leaderboard!!,
                loadingMore = loadingMore,
                onLoadMore = { page++ },
                onMemberClick = { selectedMember = it },
            )
        }
    }

    selectedMember?.let { member ->
        MemberDetailDialog(member = member, onDismiss = { selectedMember = null })
    }
}

@Composable
private fun LeagueList(
    data: LeagueLeaderboardBodyDto,
    loadingMore: Boolean,
    onLoadMore: () -> Unit,
    onMemberClick: (LeagueMemberDto) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            data.me?.let { me ->
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE8FF)), shape = RoundedCornerShape(20.dp)) {
                    Column(Modifier.fillMaxWidth().padding(16.dp)) {
                        Text("جایگاه شما در لیگ", fontFamily = IranSansFontFamily, color = LeagueNavy, fontWeight = FontWeight.Bold)
                        Text("رتبه ${me.rank} از ${data.total} • ${me.points} امتیاز", fontFamily = IranSansFontFamily, color = LeaguePurple, fontWeight = FontWeight.ExtraBold)
                    }
                }
                Spacer(Modifier.height(7.dp))
            }
        }
        items(data.data, key = { it.userId }) { member ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onMemberClick(member) },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = if (member.isMe) Color(0xFFF1EEFF) else Color.White),
            ) {
                Row(Modifier.fillMaxWidth().padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(member.rank.toString(), color = LeaguePurple, fontWeight = FontWeight.Black, fontSize = 17.sp, modifier = Modifier.padding(end = 10.dp))
                    AsyncImage(
                        model = ApiClient.resolveUrl(member.profileImageUrl),
                        contentDescription = member.fullName,
                        modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFE9E5FB)),
                    )
                    Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                        Text(if (member.isMe) "${member.fullName} (شما)" else member.fullName, fontFamily = IranSansFontFamily, color = LeagueNavy, fontWeight = FontWeight.Bold)
                        Text("${studyText(member.totalStudySeconds)} • ${member.totalTestCount} تست", fontFamily = IranSansFontFamily, color = Color.Gray, fontSize = 10.sp)
                    }
                    Text("${member.points}\nامتیاز", fontFamily = IranSansFontFamily, color = LeaguePurple, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
        if (data.data.size < data.total) {
            item {
                Button(
                    enabled = !loadingMore,
                    onClick = onLoadMore,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = LeaguePurple),
                ) {
                    Text(if (loadingMore) "در حال دریافت…" else "نمایش اعضای بیشتر", fontFamily = IranSansFontFamily)
                }
            }
        }
    }
}

@Composable
private fun MemberDetailDialog(member: LeagueMemberDto, onDismiss: () -> Unit) {
    var activity by remember { mutableStateOf<MemberActivityBodyDto?>(null) }
    var options by remember { mutableStateOf<List<FeedbackOptionDto>>(emptyList()) }
    var message by remember { mutableStateOf<String?>(null) }
    var sending by remember { mutableStateOf(false) }
    var selectedFeedbackCode by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(member.userId) {
        when (val result = safeApiCall { ApiClient.apiService.getLeagueMemberActivity(member.userId) }) {
            is NetworkResult.Success -> activity = result.data.body
            else -> Unit
        }
        if (!member.isMe) {
            when (val result = safeApiCall { ApiClient.apiService.getFeedbackOptions() }) {
                is NetworkResult.Success -> options = result.data.body.orEmpty()
                else -> Unit
            }
        }
    }

    LaunchedEffect(selectedFeedbackCode) {
        val code = selectedFeedbackCode ?: return@LaunchedEffect
        when (val result = safeApiCall { ApiClient.apiService.sendPeerFeedback(member.userId, SendPeerFeedbackDto(code)) }) {
            is NetworkResult.Success -> message = "بازخورد ارسال شد"
            is NetworkResult.Error -> message = if (result.code == 409) "در هر ۲۴ ساعت فقط یک بازخورد می‌توانید بفرستید" else result.message
            is NetworkResult.Exception -> message = "ارسال انجام نشد"
        }
        sending = false
        selectedFeedbackCode = null
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                AsyncImage(model = ApiClient.resolveUrl(member.profileImageUrl), contentDescription = null, modifier = Modifier.size(72.dp).clip(CircleShape).background(Color(0xFFE9E5FB)))
                Spacer(Modifier.height(8.dp))
                Text(member.fullName, fontFamily = IranSansFontFamily, fontWeight = FontWeight.ExtraBold, color = LeagueNavy, fontSize = 17.sp)
                Text("رتبه ${member.rank} • ${member.points} امتیاز", fontFamily = IranSansFontFamily, color = LeaguePurple, fontSize = 11.sp)
                Spacer(Modifier.height(12.dp))
                activity?.let {
                    ActivityRow("امروز", it.today)
                    ActivityRow("۷ روز اخیر", it.last7Days)
                    ActivityRow("۳۰ روز اخیر", it.last30Days)
                    ActivityRow("کل دوره", it.allTime)
                } ?: CircularProgressIndicator(color = LeaguePurple, modifier = Modifier.size(24.dp))
                if (!member.isMe) {
                    Spacer(Modifier.height(12.dp))
                    Text("یک بازخورد آماده بفرست", fontFamily = IranSansFontFamily, color = LeagueNavy, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    options.forEach { option ->
                        Button(
                            enabled = !sending,
                            onClick = {
                                sending = true
                                message = null
                                selectedFeedbackCode = option.code
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0ECFF), contentColor = LeaguePurple),
                            modifier = Modifier.fillMaxWidth(),
                        ) { Text(option.labelFa, fontFamily = IranSansFontFamily, fontSize = 10.sp) }
                    }
                    message?.let { Text(it, fontFamily = IranSansFontFamily, color = if (it.contains("ارسال شد")) Color(0xFF16865B) else Color(0xFFC43E3E), fontSize = 10.sp) }
                }
            }
        }
    }
}

@Composable
private fun ActivityRow(label: String, value: ActivityWindowDto) {
    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontFamily = IranSansFontFamily, color = LeagueNavy, fontSize = 11.sp)
        Text("${studyText(value.studySeconds)} • ${value.testCount} تست", fontFamily = IranSansFontFamily, color = Color.Gray, fontSize = 10.sp)
    }
}

private fun studyText(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return if (hours > 0) "$hours ساعت و $minutes دقیقه" else "$minutes دقیقه"
}
