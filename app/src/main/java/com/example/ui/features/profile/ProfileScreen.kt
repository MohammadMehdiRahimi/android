package com.example.ui.features.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.network.ApiClient
import com.example.network.NetworkResult
import com.example.network.TokenManager
import com.example.network.UserDto
import com.example.network.safeApiCall
import com.example.ui.theme.AppTheme
import com.example.ui.theme.LocalShetabColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun ProfileScreen(
    selectedTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
    onUpgradeClick: () -> Unit = {},
    onLoggedOut: () -> Unit = {},
) {
    val context = LocalContext.current
    val colors = LocalShetabColors.current
    val scope = rememberCoroutineScope()
    val tokenManager = remember(context) { ApiClient.getTokenManager() ?: TokenManager(context) }
    var profile by remember { mutableStateOf<UserDto?>(null) }
    var loading by remember { mutableStateOf(true) }
    var uploading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var selectedImage by remember { mutableStateOf<Uri?>(null) }

    fun persist(value: UserDto) {
        tokenManager.saveUserData(value.id, value.phone, value.role, value.fullName)
        tokenManager.saveProfileData(
            value.fullName,
            value.progression?.title?.nameFa,
            value.profileImageUrl,
            value.progression?.points,
        )
        tokenManager.updateSessionExpiry(value.sessionExpiresAt)
    }

    fun load() {
        scope.launch {
            loading = true
            when (val result = safeApiCall { ApiClient.apiService.getMe() }) {
                is NetworkResult.Success -> {
                    profile = result.data.body
                    result.data.body?.let(::persist)
                    error = null
                }
                is NetworkResult.Error -> error = result.message ?: "دریافت پروفایل انجام نشد"
                is NetworkResult.Exception -> error = "ارتباط با سرور برقرار نشد"
            }
            loading = false
        }
    }

    LaunchedEffect(Unit) { load() }

    val gallery = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImage = uri
    }

    if (selectedImage != null) {
        AvatarCropDialog(
            uri = selectedImage!!,
            busy = uploading,
            onDismiss = { if (!uploading) selectedImage = null },
            onConfirm = { zoom, offsetX, offsetY ->
                scope.launch {
                    uploading = true
                    val bytes = runCatching {
                        createCroppedAvatar(context, selectedImage!!, zoom, offsetX, offsetY)
                    }.getOrNull()
                    if (bytes == null) {
                        error = "پردازش عکس انجام نشد"
                    } else {
                        val part = MultipartBody.Part.createFormData(
                            "file",
                            "profile.jpg",
                            bytes.toRequestBody("image/jpeg".toMediaType()),
                        )
                        when (val result = safeApiCall { ApiClient.apiService.uploadMyProfileImage(part) }) {
                            is NetworkResult.Success -> {
                                profile = result.data.body
                                result.data.body?.let(::persist)
                                selectedImage = null
                                error = null
                            }
                            is NetworkResult.Error -> error = result.message ?: "بارگذاری عکس انجام نشد"
                            is NetworkResult.Exception -> error = "ارتباط با سرور برقرار نشد"
                        }
                    }
                    uploading = false
                }
            },
        )
    }

    if (loading && profile == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = colors.accentMain)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().shadow(1.dp, RoundedCornerShape(22.dp)),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(colors.cardBg),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(106.dp)
                        .clip(CircleShape)
                        .background(colors.cardIconBg)
                        .clickable { gallery.launch("image/*") },
                    contentAlignment = Alignment.Center,
                ) {
                    val avatar = ApiClient.resolveUrl(profile?.profileImageUrl)
                    if (avatar != null) {
                        AsyncImage(
                            model = avatar,
                            contentDescription = "عکس پروفایل",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Icon(Icons.Default.Person, null, Modifier.size(55.dp), tint = colors.accentMain)
                    }
                    Box(
                        modifier = Modifier.align(Alignment.BottomEnd).size(34.dp).background(colors.accentMain, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Default.CameraAlt, "تغییر عکس", Modifier.size(18.dp), tint = Color.White)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    profile?.fullName ?: tokenManager.getUserFullName() ?: "کاربر شتاب",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = colors.primaryText,
                )
                Text(
                    profile?.progression?.title?.nameFa ?: tokenManager.getUserTitle() ?: "تازه‌نفس",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.accentMain,
                )
                profile?.bio?.takeIf { it.isNotBlank() }?.let {
                    Text(it, Modifier.padding(top = 8.dp), fontSize = 11.sp, color = colors.secondaryText, textAlign = TextAlign.Center)
                }
                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = colors.primaryText.copy(alpha = .06f))
                Spacer(Modifier.height(14.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    ProfileMetric("امتیاز سراسری", profile?.progression?.points?.toString() ?: "۰", colors.accentMain)
                    ProfileMetric("پیوستگی", "${profile?.progression?.streak ?: 0} روز", Color(0xFFFF9D2E))
                    ProfileMetric("لیگ", profile?.progression?.league?.nameFa ?: "برنز", Color(0xFF21A878))
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onUpgradeClick),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(colors.cardBg),
        ) {
            Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.WorkspacePremium, null, tint = colors.accentMain)
                Spacer(Modifier.size(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("شتاب پرو", fontWeight = FontWeight.ExtraBold, color = colors.primaryText)
                    Text("امکانات پیشرفتهٔ برنامه‌ریزی و تحلیل", fontSize = 10.sp, color = colors.secondaryText)
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(colors.cardBg),
        ) {
            Column(Modifier.padding(18.dp)) {
                Text("ظاهر برنامه", fontWeight = FontWeight.ExtraBold, color = colors.primaryText)
                Spacer(Modifier.height(12.dp))
                AppTheme.values().toList().chunked(3).forEach { themes ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        themes.forEach { theme ->
                            val active = theme == selectedTheme
                            OutlinedButton(
                                onClick = { onThemeSelected(theme) },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (active) colors.accentMain.copy(alpha = .12f) else Color.Transparent,
                                ),
                            ) { Text(themeLabel(theme), fontSize = 10.sp, color = colors.primaryText) }
                        }
                    }
                }
            }
        }

        error?.let { Text(it, Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center) }

        OutlinedButton(
            onClick = {
                scope.launch {
                    // Firebase push is temporarily disabled.
                    // com.example.notifications.PushTokenRegistrar.unregister(context)
                    safeApiCall { ApiClient.apiService.logout() }
                    ApiClient.clearSession()
                    context.getSharedPreferences("shetab_onboarding_prefs", Context.MODE_PRIVATE).edit().clear().apply()
                    onLoggedOut()
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Icon(Icons.Default.ExitToApp, null, tint = MaterialTheme.colorScheme.error)
            Spacer(Modifier.size(8.dp))
            Text("خروج از حساب", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun ProfileMetric(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.ExtraBold, color = color, fontSize = 15.sp)
        Text(label, color = Color.Gray, fontSize = 9.sp)
    }
}

@Composable
private fun AvatarCropDialog(
    uri: Uri,
    busy: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Float, Float, Float) -> Unit,
) {
    var zoom by remember { mutableFloatStateOf(1f) }
    var horizontal by remember { mutableFloatStateOf(0f) }
    var vertical by remember { mutableFloatStateOf(0f) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("برش و تنظیم عکس") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier.size(220.dp).clip(CircleShape).background(Color(0xFFECEEF4)),
                    contentAlignment = Alignment.Center,
                ) {
                    AsyncImage(
                        model = uri,
                        contentDescription = "پیش‌نمایش برش",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().graphicsLayer {
                            scaleX = zoom
                            scaleY = zoom
                            translationX = horizontal * 70.dp.toPx()
                            translationY = vertical * 70.dp.toPx()
                        },
                    )
                }
                Text("بزرگ‌نمایی", Modifier.fillMaxWidth().padding(top = 12.dp), fontSize = 11.sp)
                Slider(zoom, { zoom = it }, valueRange = 1f..3f)
                Text("جابه‌جایی افقی", Modifier.fillMaxWidth(), fontSize = 11.sp)
                Slider(horizontal, { horizontal = it }, valueRange = -1f..1f)
                Text("جابه‌جایی عمودی", Modifier.fillMaxWidth(), fontSize = 11.sp)
                Slider(vertical, { vertical = it }, valueRange = -1f..1f)
            }
        },
        confirmButton = {
            Button(enabled = !busy, onClick = { onConfirm(zoom, horizontal, vertical) }) {
                if (busy) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp) else Text("ذخیره عکس")
            }
        },
        dismissButton = { OutlinedButton(enabled = !busy, onClick = onDismiss) { Text("انصراف") } },
    )
}

private suspend fun createCroppedAvatar(
    context: Context,
    uri: Uri,
    zoom: Float,
    offsetX: Float,
    offsetY: Float,
): ByteArray = withContext(Dispatchers.IO) {
    val source = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri)) { decoder, _, _ ->
            decoder.isMutableRequired = true
        }
    } else {
        @Suppress("DEPRECATION")
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            ?: context.contentResolver.openInputStream(uri)?.use(BitmapFactory::decodeStream)
    } ?: error("IMAGE_DECODE_FAILED")
    val cropSize = (min(source.width, source.height) / zoom).roundToInt().coerceAtLeast(1)
    val maxX = (source.width - cropSize).coerceAtLeast(0)
    val maxY = (source.height - cropSize).coerceAtLeast(0)
    val left = ((maxX / 2f) + offsetX.coerceIn(-1f, 1f) * maxX / 2f).roundToInt().coerceIn(0, maxX)
    val top = ((maxY / 2f) + offsetY.coerceIn(-1f, 1f) * maxY / 2f).roundToInt().coerceIn(0, maxY)
    val cropped = Bitmap.createBitmap(source, left, top, cropSize, cropSize)
    val output = Bitmap.createScaledBitmap(cropped, 1024, 1024, true)
    ByteArrayOutputStream().use { stream ->
        check(output.compress(Bitmap.CompressFormat.JPEG, 90, stream))
        stream.toByteArray()
    }
}

private fun themeLabel(theme: AppTheme): String = when (theme) {
    AppTheme.PESARANE -> "پسرانه"
    AppTheme.DOKHTARONE -> "دخترانه"
    AppTheme.BAHAR -> "بهار"
    AppTheme.TABESTAN -> "تابستان"
    AppTheme.PAEEZ -> "پاییز"
    AppTheme.ZEMESTAN -> "زمستان"
}
