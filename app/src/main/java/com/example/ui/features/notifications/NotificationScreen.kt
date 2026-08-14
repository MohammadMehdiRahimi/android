package com.example.ui.features.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.network.ApiClient
import com.example.network.NetworkResult
import com.example.network.StudentNotificationDto
import com.example.network.safeApiCall
import com.example.ui.theme.IranSansFontFamily
import kotlinx.coroutines.launch

@Composable
fun NotificationScreen(navController: NavController) {
    var notifications by remember { mutableStateOf<List<StudentNotificationDto>?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var reload by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(reload) {
        when (val result = safeApiCall { ApiClient.apiService.getProgressionNotifications() }) {
            is NetworkResult.Success -> {
                notifications = result.data.body?.data.orEmpty()
                error = null
            }
            is NetworkResult.Error -> error = result.message
            is NetworkResult.Exception -> error = "ارتباط با سرور برقرار نشد"
        }
    }

    Column(Modifier.fillMaxSize().background(Color(0xFFF7F8FC))) {
        Row(
            Modifier.fillMaxWidth().background(Color.White).padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, contentDescription = "بازگشت") }
            Text("اعلان‌ها", fontFamily = IranSansFontFamily, fontWeight = FontWeight.ExtraBold, color = Color(0xFF172554), fontSize = 18.sp, modifier = Modifier.weight(1f))
            Text(
                "خواندن همه",
                fontFamily = IranSansFontFamily,
                color = Color(0xFF7357F5),
                fontSize = 10.sp,
                modifier = Modifier.clickable {
                    notifications = notifications?.map { it.copy(readAt = it.readAt ?: "now") }
                    scope.launch { safeApiCall { ApiClient.apiService.markAllNotificationsRead() } }
                },
            )
        }
        when {
            notifications == null && error == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color(0xFF7357F5)) }
            error != null -> Box(Modifier.fillMaxSize().clickable { reload++ }, contentAlignment = Alignment.Center) { Text("$error\nبرای تلاش دوباره لمس کنید", fontFamily = IranSansFontFamily, color = Color(0xFFC43E3E)) }
            notifications.isNullOrEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.NotificationsNone, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
                    Text("هنوز اعلانی ندارید", fontFamily = IranSansFontFamily, color = Color.Gray)
                }
            }
            else -> LazyColumn(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(notifications!!, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable {
                            if (item.readAt == null) {
                                notifications = notifications?.map { if (it.id == item.id) it.copy(readAt = "now") else it }
                                scope.launch { safeApiCall { ApiClient.apiService.markNotificationRead(item.id) } }
                            }
                        },
                        colors = CardDefaults.cardColors(containerColor = if (item.readAt == null) Color(0xFFF0ECFF) else Color.White),
                        shape = RoundedCornerShape(18.dp),
                    ) {
                        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = ApiClient.resolveUrl(item.actorProfileImageUrl),
                                contentDescription = item.actorName,
                                modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFE4DEF9)),
                            )
                            Column(Modifier.weight(1f).padding(horizontal = 11.dp)) {
                                Text(item.titleFa, fontFamily = IranSansFontFamily, color = Color(0xFF172554), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(item.bodyFa, fontFamily = IranSansFontFamily, color = Color.Gray, fontSize = 10.sp)
                            }
                            if (item.readAt == null) Box(Modifier.size(9.dp).background(Color(0xFF7357F5), CircleShape))
                        }
                    }
                }
            }
        }
    }
}
