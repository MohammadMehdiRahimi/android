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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.network.ApiClient
import com.example.network.NetworkResult
import com.example.network.TokenManager
import com.example.network.UserDto
import com.example.network.safeApiCall
import com.example.ui.theme.AppTheme
import com.example.ui.theme.IranSansFontFamily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import kotlin.math.min
import kotlin.math.roundToInt

private val ProfilePurple = Color(0xFF7543EA)
private val ProfilePurpleLight = Color(0xFFF6F3FE)
private val ProfilePurpleIconBg = Color(0xFFECE6FE)
private val ProfileDarkText = Color(0xFF1E293B)
private val ProfileGrayText = Color(0xFF64748B)
private val ProfileLightGrayText = Color(0xFF94A3B8)
private val ProfileCardBg = Color.White
private val ProfileRed = Color(0xFFEF4444)
private val ProfileRedLight = Color(0xFFFEE2E2)

@Composable
fun ProfileScreen(
    selectedTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
    onUpgradeClick: () -> Unit = {},
    onLoggedOut: () -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val tokenManager = remember(context) { ApiClient.getTokenManager() ?: TokenManager(context) }
    var profile by remember { mutableStateOf<UserDto?>(null) }
    var loading by remember { mutableStateOf(true) }
    var uploading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var selectedImage by remember { mutableStateOf<Uri?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showSupportDialog by remember { mutableStateOf(false) }

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

    if (showLogoutDialog) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = {
                    Text(
                        text = "خروج از حساب کاربری",
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                },
                text = {
                    Text(
                        text = "آیا مطمئن هستید که می‌خواهید از حساب کاربری خود خارج شوید؟",
                        fontFamily = IranSansFontFamily,
                        fontSize = 13.sp,
                        color = ProfileGrayText
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showLogoutDialog = false
                            scope.launch {
                                safeApiCall { ApiClient.apiService.logout() }
                                ApiClient.clearSession()
                                context.getSharedPreferences("shetab_onboarding_prefs", Context.MODE_PRIVATE).edit().clear().apply()
                                onLoggedOut()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ProfileRed)
                    ) {
                        Text("خروج", fontFamily = IranSansFontFamily, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("انصراف", fontFamily = IranSansFontFamily, color = ProfileGrayText)
                    }
                },
                shape = RoundedCornerShape(20.dp),
                containerColor = Color.White
            )
        }
    }

    if (showAboutDialog) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            AlertDialog(
                onDismissRequest = { showAboutDialog = false },
                title = {
                    Text("درباره برنامه شتاب", fontFamily = IranSansFontFamily, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("سامانه هوشمند برنامه‌ریزی، آموزش و آزمون‌های تحصیلی شتاب", fontFamily = IranSansFontFamily, fontSize = 13.sp)
                        Text("نسخه: ۱.۴.۰", fontFamily = IranSansFontFamily, fontSize = 12.sp, color = ProfilePurple)
                    }
                },
                confirmButton = {
                    Button(onClick = { showAboutDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = ProfilePurple)) {
                        Text("بستن", fontFamily = IranSansFontFamily)
                    }
                },
                shape = RoundedCornerShape(20.dp),
                containerColor = Color.White
            )
        }
    }

    if (showSupportDialog) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            AlertDialog(
                onDismissRequest = { showSupportDialog = false },
                title = {
                    Text("پشتیبانی شتاب", fontFamily = IranSansFontFamily, fontWeight = FontWeight.Bold)
                },
                text = {
                    Text("تیم پشتیبانی شتاب همواره آماده پاسخگویی به سوالات و مشکلات شماست. به زودی سیستم تیکتینگ آنلاین متصل خواهد شد.", fontFamily = IranSansFontFamily, fontSize = 13.sp)
                },
                confirmButton = {
                    Button(onClick = { showSupportDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = ProfilePurple)) {
                        Text("متوجه شدم", fontFamily = IranSansFontFamily)
                    }
                },
                shape = RoundedCornerShape(20.dp),
                containerColor = Color.White
            )
        }
    }

    if (loading && profile == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ProfilePurple)
        }
        return
    }

    val displayName = profile?.fullName ?: tokenManager.getUserFullName() ?: "علی محمدی"
    val avatarUrl = ApiClient.resolveUrl(profile?.profileImageUrl)

    // Strict RTL Layout Enforcement
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFAFAFC))
                .verticalScroll(rememberScrollState())
                .padding(start = 18.dp, end = 18.dp, top = 20.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // 1. Hero Profile Card (Avatar on Right, Details on Left in RTL)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(26.dp))
                    .background(ProfilePurpleLight)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Right in RTL (Start): Large Avatar with Camera Badge
                    Box(
                        modifier = Modifier.size(96.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(92.dp)
                                .clip(CircleShape)
                                .border(3.dp, Color.White, CircleShape)
                                .background(ProfilePurpleIconBg)
                                .clickable { gallery.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (avatarUrl != null) {
                                AsyncImage(
                                    model = avatarUrl,
                                    contentDescription = "عکس کاربر",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(50.dp),
                                    tint = ProfilePurple
                                )
                            }
                        }

                        // Camera Icon Button at bottom-end of the avatar
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = 2.dp, y = 2.dp)
                                .size(30.dp)
                                .shadow(2.dp, CircleShape)
                                .background(Color.White, CircleShape)
                                .clickable { gallery.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "تغییر عکس",
                                tint = ProfilePurple,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    // Left in RTL (End): User Details (Name + Pencil, Grade, Field)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Name + Edit Pencil Icon
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = displayName,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = IranSansFontFamily,
                                color = ProfileDarkText
                            )
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(ProfilePurple.copy(alpha = 0.12f))
                                    .clickable { /* Edit name action */ },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "ویرایش نام",
                                    tint = ProfilePurple,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(2.dp))

                        // Academic Grade (پایه دوازدهم)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.School,
                                contentDescription = null,
                                tint = ProfileGrayText,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "پایه دوازدهم",
                                fontSize = 12.5.sp,
                                fontFamily = IranSansFontFamily,
                                color = ProfileGrayText,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Field of Study (رشته تجربی)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = ProfileGrayText,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "رشته تجربی",
                                fontSize = 12.5.sp,
                                fontFamily = IranSansFontFamily,
                                color = ProfileGrayText,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // 2. Personal Information Section ("اطلاعات شخصی")
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Section Header (Icon on Right, Title next to it in RTL)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Icon(
                        Icons.Outlined.Person,
                        contentDescription = null,
                        tint = ProfilePurple,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "اطلاعات شخصی",
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = IranSansFontFamily,
                        color = ProfileDarkText
                    )
                }

                // Unified Information Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ProfileCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Row 1: Full Name
                        PersonalInfoRow(
                            title = "نام و نام خانوادگی",
                            value = displayName,
                            onClick = { /* edit name */ }
                        )
                        HorizontalDivider(
                            color = Color(0xFFF1F5F9),
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        // Row 2: Academic Grade
                        PersonalInfoRow(
                            title = "پایه تحصیلی",
                            value = "دوازدهم",
                            onClick = { /* edit grade */ }
                        )
                        HorizontalDivider(
                            color = Color(0xFFF1F5F9),
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        // Row 3: Field of Study
                        PersonalInfoRow(
                            title = "رشته تحصیلی",
                            value = "تجربی",
                            onClick = { /* edit field */ }
                        )
                    }
                }

                // Explanatory Note below card
                Text(
                    text = "برای ویرایش اطلاعات روی هر مورد کلیک کنید.",
                    fontSize = 11.sp,
                    fontFamily = IranSansFontFamily,
                    color = ProfileLightGrayText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp)
                )
            }

            // 3. User Account Section ("حساب کاربری")
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Section Header (Icon on Right, Title next to it in RTL)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Icon(
                        Icons.Outlined.ManageAccounts,
                        contentDescription = null,
                        tint = ProfilePurple,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "حساب کاربری",
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = IranSansFontFamily,
                        color = ProfileDarkText
                    )
                }

                // Action Card 1: Upgrade to Pro (ارتقاء به اکانت پرو)
                AccountActionCard(
                    title = "ارتقاء به اکانت پرو",
                    subtitle = "از تمام امکانات ویژه استفاده کنید",
                    icon = Icons.Default.WorkspacePremium,
                    iconBg = ProfilePurpleIconBg,
                    iconTint = ProfilePurple,
                    onClick = onUpgradeClick
                )

                // Action Card 2: Support Ticket (تیکت پشتیبانی)
                AccountActionCard(
                    title = "تیکت پشتیبانی",
                    subtitle = "سوال یا مشکلی دارید؟ با ما در ارتباط باشید",
                    icon = Icons.Outlined.HeadsetMic,
                    iconBg = ProfilePurpleIconBg,
                    iconTint = ProfilePurple,
                    onClick = { showSupportDialog = true }
                )

                // Action Card 3: About App (درباره برنامه)
                AccountActionCard(
                    title = "درباره برنامه",
                    subtitle = "نسخه برنامه و اطلاعات بیشتر",
                    icon = Icons.Outlined.Info,
                    iconBg = ProfilePurpleIconBg,
                    iconTint = ProfilePurple,
                    onClick = { showAboutDialog = true }
                )

                // Action Card 4: Logout (خروج از حساب کاربری)
                AccountActionCard(
                    title = "خروج از حساب کاربری",
                    subtitle = "از حساب کاربری خود خارج شوید",
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    iconBg = ProfileRedLight,
                    iconTint = ProfileRed,
                    titleColor = ProfileRed,
                    onClick = { showLogoutDialog = true }
                )
            }

            // Error message if any
            error?.let {
                Text(
                    it,
                    Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    fontFamily = IranSansFontFamily,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun PersonalInfoRow(
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Title on Right (Start in RTL)
        Text(
            text = title,
            fontSize = 13.sp,
            fontFamily = IranSansFontFamily,
            fontWeight = FontWeight.Bold,
            color = ProfileDarkText
        )

        // Value and Arrow on Left (End in RTL)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = value,
                fontSize = 13.sp,
                fontFamily = IranSansFontFamily,
                fontWeight = FontWeight.Medium,
                color = ProfileGrayText
            )
            Icon(
                Icons.Default.KeyboardArrowLeft,
                contentDescription = null,
                tint = ProfilePurple,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun AccountActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    titleColor: Color = ProfileDarkText,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ProfileCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Right in RTL (Start): Square Icon Box + Text Column
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Square Icon Box
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Text Column
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = title,
                        fontSize = 13.5.sp,
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        color = titleColor
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = subtitle,
                        fontSize = 10.5.sp,
                        fontFamily = IranSansFontFamily,
                        color = ProfileGrayText
                    )
                }
            }

            // Left in RTL (End): Chevron Arrow
            Icon(
                Icons.Default.KeyboardArrowLeft,
                contentDescription = null,
                tint = ProfilePurple,
                modifier = Modifier.size(20.dp)
            )
        }
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
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("برش و تنظیم عکس", fontFamily = IranSansFontFamily, fontWeight = FontWeight.Bold) },
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
                    Text("بزرگ‌نمایی", Modifier.fillMaxWidth().padding(top = 12.dp), fontSize = 11.sp, fontFamily = IranSansFontFamily)
                    Slider(zoom, { zoom = it }, valueRange = 1f..3f)
                    Text("جابه‌جایی افقی", Modifier.fillMaxWidth(), fontSize = 11.sp, fontFamily = IranSansFontFamily)
                    Slider(horizontal, { horizontal = it }, valueRange = -1f..1f)
                    Text("جابه‌جایی عمودی", Modifier.fillMaxWidth(), fontSize = 11.sp, fontFamily = IranSansFontFamily)
                    Slider(vertical, { vertical = it }, valueRange = -1f..1f)
                }
            },
            confirmButton = {
                Button(
                    enabled = !busy,
                    onClick = { onConfirm(zoom, horizontal, vertical) },
                    colors = ButtonDefaults.buttonColors(containerColor = ProfilePurple)
                ) {
                    if (busy) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = Color.White) else Text("ذخیره عکس", fontFamily = IranSansFontFamily)
                }
            },
            dismissButton = { OutlinedButton(enabled = !busy, onClick = onDismiss) { Text("انصراف", fontFamily = IranSansFontFamily) } },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }
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
