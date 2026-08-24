package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.R
import com.example.ui.theme.LocalShetabColors
import kotlinx.coroutines.delay
import androidx.compose.animation.AnimatedContent
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast

/**
 * Normalizes Persian and Arabic keypad digits back to standard English digits
 */
fun convertPersianToEnglishDigits(input: String): String {
    val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    var result = input
    for (i in 0..9) {
        result = result.replace(persianDigits[i], ('0'.toInt() + i).toChar())
        result = result.replace(arabicDigits[i], ('0'.toInt() + i).toChar())
    }
    return result
}

// Global state to track and edit the phone number across screens
var globalUserPhoneNumber by mutableStateOf("")

// Helper to convert English digits to Persian
fun String.toPersianDigits(): String {
    val englishDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
    val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    var result = this
    for (i in 0..9) {
        result = result.replace(englishDigits[i], persianDigits[i])
    }
    return result
}

val OrganicBlobShape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val w = size.width
            val h = size.height
            moveTo(w * 0.25f, h * 0.2f)
            cubicTo(w * 0.1f, h * 0.5f, w * 0.3f, h * 0.95f, w * 0.65f, h * 0.9f)
            cubicTo(w * 0.95f, h * 0.85f, w * 1.1f, h * 0.4f, w * 0.9f, h * 0.15f)
            cubicTo(w * 0.75f, h * -0.05f, w * 0.4f, h * -0.05f, w * 0.25f, h * 0.2f)
            close()
        }
        return Outline.Generic(path)
    }
}

val BottomRightBlobShape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val w = size.width
            val h = size.height
            moveTo(0f, h)
            cubicTo(w * 0.1f, h * 0.4f, w * 0.4f, h * 0.1f, w, 0f)
            lineTo(w, h)
            close()
        }
        return Outline.Generic(path)
    }
}

val TopSemiCircleShape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val w = size.width
            val h = size.height
            moveTo(0f, h)
            arcTo(
                rect = Rect(0f, 0f, w, h * 2f),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 180f,
                forceMoveTo = false
            )
            close()
        }
        return Outline.Generic(path)
    }
}

val BottomRightSemiCircleShape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val w = size.width
            val h = size.height
            moveTo(0f, h)
            quadraticTo(0f, 0f, w, 0f)
            lineTo(w, h)
            close()
        }
        return Outline.Generic(path)
    }
}

val BottomLeftBlobShape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val w = size.width
            val h = size.height
            moveTo(w, h)
            cubicTo(w * 0.9f, h * 0.4f, w * 0.6f, h * 0.1f, 0f, 0f)
            lineTo(0f, h)
            close()
        }
        return Outline.Generic(path)
    }
}

val CosmicWaveShape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val w = size.width
            val h = size.height
            moveTo(0f, 0f)
            lineTo(w, 0f)
            lineTo(w, h * 0.78f)
            quadraticTo(w * 0.5f, h * 1.05f, 0f, h * 0.78f)
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
fun CosmicHeader(
    rayaImageRes: Int,
    heightDp: Int = 230
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(heightDp.dp)
            .clip(CosmicWaveShape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D0E29),
                        Color(0xFF151845),
                        Color(0xFF1B1D54)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = drawContext.size.width
            val canvasHeight = drawContext.size.height
            val densityScale = density

            val random = java.util.Random(101)
            for (i in 0 until 18) {
                val x = random.nextFloat() * canvasWidth
                val y = random.nextFloat() * canvasHeight * 0.8f
                val radius = 1f + random.nextFloat() * 2f
                val alpha = 0.3f + random.nextFloat() * 0.7f
                drawCircle(
                    color = Color.White.copy(alpha = alpha),
                    radius = radius * densityScale,
                    center = androidx.compose.ui.geometry.Offset(x, y)
                )
            }
            
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFBA68C8).copy(alpha = 0.12f), Color.Transparent),
                    radius = 100f * densityScale
                ),
                radius = 100f * densityScale,
                center = androidx.compose.ui.geometry.Offset(canvasWidth * 0.8f, canvasHeight * 0.3f)
            )
            drawCircle(
                color = Color(0xFFBA68C8).copy(alpha = 0.25f),
                radius = 16f * densityScale,
                center = androidx.compose.ui.geometry.Offset(canvasWidth * 0.8f, canvasHeight * 0.3f)
            )
        }
        
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-10).dp)
                .size(100.dp)
                .background(Color.White.copy(alpha = 0.08f), CircleShape)
                .border(2.dp, Color(0xFF1E88E5).copy(alpha = 0.3f), CircleShape)
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = rayaImageRes),
                contentDescription = "Raya",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun AuthHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            
            // Exactly 3 overlapping giant curved circles matching OnboardingHeader style
            // 1. Largest outermost pink/magenta circle segment
            drawCircle(
                color = Color(0xFFFF52C5),
                radius = w * 1.35f,
                center = androidx.compose.ui.geometry.Offset(-w * 0.15f, -h * 0.15f)
            )

            // 2. Middle layer: Vibrant Royal Blue circle layer
            drawCircle(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF2979FF), Color(0xFF3D5AFE))
                ),
                radius = w * 1.05f,
                center = androidx.compose.ui.geometry.Offset(-w * 0.22f, -h * 0.2f)
            )

            // 3. Innermost layer: Deep dark indigo/navy
            drawCircle(
                color = Color(0xFF102A6B),
                radius = w * 0.78f,
                center = androidx.compose.ui.geometry.Offset(-w * 0.28f, -h * 0.25f)
            )
        }
    }
}

enum class AuthSheetStep {
    PHONE,    // Step 1: Mobile Phone Number
    OTP,      // Step 2: 4-digit OTP code
    VERIFY,   // Step 3: Verified Checkmark
    PROFILE   // Step 4: Name, Grade, Major
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AuthBottomSheetScreen(
    navController: NavController,
    initialStep: AuthSheetStep = AuthSheetStep.PHONE
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var currentStep by remember { mutableStateOf(initialStep) }
    
    // State for Step 1: Phone
    var rawPhoneInput by remember { mutableStateOf(globalUserPhoneNumber) }
    var phoneError by remember { mutableStateOf("") }
    var isNetworkLoading by remember { mutableStateOf(false) }
    var registrationToken by remember { mutableStateOf<String?>(null) }
    var existingUserNeedsOnboarding by remember { mutableStateOf(false) }
    var shouldShowProfile by remember { mutableStateOf(false) }
    
    // State for Step 2: OTP (up to 6 digits)
    var rawOtpInput by remember { mutableStateOf("") }
    var otpError by remember { mutableStateOf("") }
    var timeLeft by remember { mutableStateOf(120) }
    val focusRequester = remember { FocusRequester() }
    var isOtpFocused by remember { mutableStateOf(false) }

    // State for Step 4: Profile Details
    var nameInput by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }
    var onboardingOptions by remember {
        mutableStateOf(com.example.network.OnboardingOptionsBody())
    }
    var selectedGradeCode by remember { mutableStateOf("") }
    val selectedGrade = onboardingOptions.grades.firstOrNull { it.code == selectedGradeCode }?.label.orEmpty()
    val gradeOptions = onboardingOptions.grades
    
    var selectedMajorCode by remember { mutableStateOf("") }
    val majorOptions = onboardingOptions.fieldsOfStudy

    val isHighSchool = onboardingOptions.grades
        .firstOrNull { it.code == selectedGradeCode }
        ?.requiresFieldOfStudy == true

    LaunchedEffect(Unit) {
        when (val result = com.example.network.safeApiCall {
            com.example.network.ApiClient.apiService.getOnboardingOptions()
        }) {
            is com.example.network.NetworkResult.Success -> {
                result.data.body?.let { options ->
                    if (options.grades.isNotEmpty()) onboardingOptions = options
                    selectedGradeCode = options.grades.firstOrNull { it.code == "TENTH" }?.code
                        ?: options.grades.firstOrNull()?.code.orEmpty()
                    selectedMajorCode = options.fieldsOfStudy.firstOrNull()?.code.orEmpty()
                }
            }
            else -> Unit
        }
    }

    // OTP Timer
    LaunchedEffect(currentStep, timeLeft) {
        if (currentStep == AuthSheetStep.OTP && timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
    }

    // Helper: Request OTP from backend
    val requestOtpCode: () -> Unit = {
        val normalized = convertPersianToEnglishDigits(rawPhoneInput).filter { it.isDigit() }
        if (normalized.length < 10) {
            phoneError = "لطفاً شماره موبایل معتبر ۱۰ یا ۱۱ رقمی وارد کنید"
        } else {
            phoneError = ""
            val phoneToSend = if (normalized.startsWith("9")) "0$normalized" else normalized
            globalUserPhoneNumber = phoneToSend
            isNetworkLoading = true
            coroutineScope.launch {
                val result = com.example.network.safeApiCall {
                    com.example.network.ApiClient.apiService.requestOtp(
                        com.example.network.OtpRequestDto(phone = phoneToSend)
                    )
                }
                isNetworkLoading = false
                when (result) {
                    is com.example.network.NetworkResult.Success -> {
                        val responseData = result.data.body
                        val expiresIn = responseData?.expiresIn ?: 120
                        timeLeft = expiresIn
                        rawOtpInput = ""
                        otpError = ""
                        currentStep = AuthSheetStep.OTP
                    }
                    is com.example.network.NetworkResult.Error -> {
                        phoneError = result.message ?: "خطا در ارسال کد تایید"
                    }
                    is com.example.network.NetworkResult.Exception -> {
                        phoneError = "خطا در ارتباط با سرور. لطفاً اتصال اینترنت خود را بررسی کنید"
                    }
                }
            }
        }
    }

    // Helper: Verify OTP with backend
    val verifyOtpCode: (String) -> Unit = { code ->
        if (code.length < 4) {
            otpError = "لطفاً کد تایید را کامل وارد کنید"
        } else {
            otpError = ""
            isNetworkLoading = true
            coroutineScope.launch {
                val result = com.example.network.safeApiCall {
                    com.example.network.ApiClient.apiService.verifyOtp(
                        com.example.network.OtpVerifyDto(
                            phone = globalUserPhoneNumber,
                            otp = code,
                            deviceType = "ANDROID"
                        )
                    )
                }
                isNetworkLoading = false
                when (result) {
                    is com.example.network.NetworkResult.Success -> {
                        val authData = result.data.body
                        if (authData?.isNew == true && !authData.registrationToken.isNullOrBlank()) {
                            registrationToken = authData.registrationToken
                            existingUserNeedsOnboarding = false
                            shouldShowProfile = true
                            currentStep = AuthSheetStep.VERIFY
                        } else if (authData != null && !authData.accessToken.isNullOrBlank()) {
                            val tokenManager = com.example.network.ApiClient.getTokenManager()
                                ?: com.example.network.TokenManager(context)
                            tokenManager.saveSession(
                                authData.accessToken,
                                authData.accessExpiresAt,
                                authData.refreshExpiresAt,
                            )
                            tokenManager.saveUserData(
                                id = authData.user?.id,
                                phone = authData.user?.phone,
                                role = authData.user?.role,
                                fullName = authData.user?.fullName,
                            )
                            val sharedPrefs = context.getSharedPreferences("shetab_onboarding_prefs", android.content.Context.MODE_PRIVATE)
                            sharedPrefs.edit().apply {
                                putBoolean("is_logged_in", true)
                                authData.user?.fullName
                                    ?.takeIf { it.isNotBlank() }
                                    ?.let { putString("user_name", it.trim()) }
                            }.apply()
                            existingUserNeedsOnboarding = authData.onboarding?.required == true
                            shouldShowProfile = existingUserNeedsOnboarding
                            currentStep = AuthSheetStep.VERIFY
                        } else {
                            otpError = "پاسخ نامعتبر از سرور"
                        }
                    }
                    is com.example.network.NetworkResult.Error -> {
                        otpError = result.message ?: "کد وارد شده اشتباه یا منقضی شده است"
                    }
                    is com.example.network.NetworkResult.Exception -> {
                        otpError = "خطا در ارتباط با سرور. لطفاً اتصال اینترنت خود را بررسی کنید"
                    }
                }
            }
        }
    }

    // Auto verify when 6 digits typed in OTP
    val normalizedOtp = remember(rawOtpInput) { convertPersianToEnglishDigits(rawOtpInput).filter { it.isDigit() } }
    LaunchedEffect(normalizedOtp) {
        if (currentStep == AuthSheetStep.OTP && normalizedOtp.length == 6 && !isNetworkLoading) {
            verifyOtpCode(normalizedOtp)
        }
    }

    // Auto advance from VERIFY to PROFILE after short delay
    LaunchedEffect(currentStep) {
        if (currentStep == AuthSheetStep.VERIFY) {
            delay(800L)
            if (shouldShowProfile) {
                currentStep = AuthSheetStep.PROFILE
            } else {
                navController.navigate("dashboard") {
                    popUpTo("login_phone") { inclusive = true }
                }
            }
        }
    }

    val primaryPurple = Color(0xFF8B5CF6)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Draw only the guest dashboard as the backdrop. Rendering ShetabApp
        // here would start a second authentication/navigation owner and could
        // continuously add login_phone destinations to the back stack.
        com.example.ui.main.HomeScreenContent(
            navController = navController,
            isGuest = true,
            isLoading = false,
            onLoginClick = {},
        )

        // 2. Semi-transparent black modal overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .clickable {
                    navController.navigate("dashboard") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
        )

        // 3. Bottom Sheet Surface occupying bottom portion
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .wrapContentHeight()
                .heightIn(min = 380.dp, max = 580.dp)
                .clickable(enabled = false) { /* prevent click through to backdrop */ },
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = Color.White,
            shadowElevation = 20.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header: Drag Handle & Navigation Buttons
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Drag Handle Pill
                    Box(
                        modifier = Modifier
                            .width(42.dp)
                            .height(5.dp)
                            .background(Color(0xFFCBD5E1), CircleShape)
                    )

                    // Left / Back Action Button
                    if (currentStep == AuthSheetStep.OTP) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .size(32.dp)
                                .background(Color(0xFFF1F5F9), CircleShape)
                                .clickable { currentStep = AuthSheetStep.PHONE },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Dismiss Close Button
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(32.dp)
                            .background(Color(0xFFF1F5F9), CircleShape)
                            .clickable {
                                navController.navigate("dashboard") {
                                    popUpTo("dashboard") { inclusive = true }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Step Content
                AnimatedContent(
                    targetState = currentStep,
                    label = "AuthStepTransition"
                ) { step ->
                    when (step) {
                        AuthSheetStep.PHONE -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "ورود / ثبت‌نام در شتاب ⚡",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF1E1B4B),
                                    fontFamily = com.example.ui.theme.IranSansFontFamily,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "لطفاً شماره موبایل خود را وارد کنید",
                                    fontSize = 13.sp,
                                    color = Color(0xFF64748B),
                                    fontFamily = com.example.ui.theme.IranSansFontFamily,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                // Phone Input Field
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .shadow(3.dp, RoundedCornerShape(18.dp), spotColor = Color(0x208B5CF6)),
                                    color = Color.White,
                                    shape = RoundedCornerShape(18.dp),
                                    border = BorderStroke(1.dp, Color(0xFFF3E8FF))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                            BasicTextField(
                                                value = rawPhoneInput.toPersianDigits(),
                                                onValueChange = { input ->
                                                    val cleaned = convertPersianToEnglishDigits(input).filter { it.isDigit() }
                                                    if (cleaned.length <= 11) {
                                                        rawPhoneInput = cleaned
                                                        phoneError = ""
                                                    }
                                                },
                                                modifier = Modifier.weight(1f),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                                singleLine = true,
                                                textStyle = TextStyle(
                                                    color = Color(0xFF1E1B4B),
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = com.example.ui.theme.IranSansFontFamily,
                                                    textAlign = TextAlign.Left
                                                ),
                                                cursorBrush = SolidColor(primaryPurple),
                                                decorationBox = { innerTextField ->
                                                    Box(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        contentAlignment = Alignment.CenterStart
                                                    ) {
                                                        if (rawPhoneInput.isEmpty()) {
                                                            Text(
                                                                text = "شماره همراه (مثال: ۰۹۱۲۳۴۵۶۷۸۹)",
                                                                color = Color(0xFF94A3B8),
                                                                fontSize = 13.sp,
                                                                fontFamily = com.example.ui.theme.IranSansFontFamily,
                                                                textAlign = TextAlign.Left
                                                            )
                                                        }
                                                        innerTextField()
                                                    }
                                                }
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        // Country Chip in LTR to ensure +98 stays on one horizontal line
                                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                modifier = Modifier.wrapContentWidth()
                                            ) {
                                                Text(
                                                    text = "🇮🇷",
                                                    fontSize = 16.sp
                                                )
                                                Text(
                                                    text = "+۹۸",
                                                    color = Color(0xFF1E1B4B),
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = com.example.ui.theme.IranSansFontFamily,
                                                    maxLines = 1,
                                                    softWrap = false
                                                )
                                            }
                                        }
                                    }
                                }

                                if (phoneError.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = phoneError,
                                        color = Color(0xFFEF4444),
                                        fontSize = 12.sp,
                                        fontFamily = com.example.ui.theme.IranSansFontFamily
                                    )
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                // Button: دریافت کد تایید
                                Button(
                                    onClick = {
                                        if (!isNetworkLoading) {
                                            requestOtpCode()
                                        }
                                    },
                                    enabled = !isNetworkLoading,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = Color(0x408B5CF6)),
                                    colors = ButtonDefaults.buttonColors(containerColor = primaryPurple),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    if (isNetworkLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = Color.White,
                                            strokeWidth = 2.5.dp
                                        )
                                    } else {
                                        Text(
                                            text = "دریافت کد تایید",
                                            color = Color.White,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = com.example.ui.theme.IranSansFontFamily
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Security Note
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Lock",
                                        tint = primaryPurple,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "اطلاعات شما نزد شتاب کاملاً محفوظ است",
                                        fontSize = 11.5.sp,
                                        color = Color(0xFF94A3B8),
                                        fontFamily = com.example.ui.theme.IranSansFontFamily
                                    )
                                }
                            }
                        }

                        AuthSheetStep.OTP -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "کد تایید را وارد کنید 🔑",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF1E1B4B),
                                    fontFamily = com.example.ui.theme.IranSansFontFamily,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                val formattedPhone = globalUserPhoneNumber.toPersianDigits()
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "کد تایید به شماره $formattedPhone ارسال شد",
                                        fontSize = 12.5.sp,
                                        color = Color(0xFF64748B),
                                        fontFamily = com.example.ui.theme.IranSansFontFamily
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "ویرایش",
                                        color = primaryPurple,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = com.example.ui.theme.IranSansFontFamily,
                                        modifier = Modifier.clickable { currentStep = AuthSheetStep.PHONE }
                                    )
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                // Clean 6-slot OTP Input
                                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        BasicTextField(
                                            value = rawOtpInput,
                                            onValueChange = { text ->
                                                val clean = convertPersianToEnglishDigits(text).filter { it.isDigit() }
                                                if (clean.length <= 6) {
                                                    rawOtpInput = clean
                                                }
                                            },
                                            modifier = Modifier
                                                .size(1.dp)
                                                .alpha(0f)
                                                .focusRequester(focusRequester)
                                                .onFocusChanged { isOtpFocused = it.isFocused },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            cursorBrush = SolidColor(Color.Transparent)
                                        )

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 8.dp)
                                                .clickable { focusRequester.requestFocus() },
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            repeat(6) { index ->
                                                val char = when {
                                                    index >= normalizedOtp.length -> ""
                                                    else -> normalizedOtp[index].toString().toPersianDigits()
                                                }
                                                val isSlotActive = isOtpFocused && (normalizedOtp.length == index || (index == 5 && normalizedOtp.length == 6))

                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(52.dp)
                                                        .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                                                        .border(
                                                            width = if (isSlotActive) 2.dp else 1.dp,
                                                            color = if (isSlotActive) primaryPurple else Color(0xFFE2E8F0),
                                                            shape = RoundedCornerShape(12.dp)
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = char,
                                                        fontSize = 20.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF1E1B4B),
                                                        fontFamily = com.example.ui.theme.IranSansFontFamily
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                if (otpError.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = otpError,
                                        color = Color(0xFFEF4444),
                                        fontSize = 12.sp,
                                        fontFamily = com.example.ui.theme.IranSansFontFamily
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Countdown Timer / Resend Button
                                if (timeLeft > 0) {
                                    val minutes = timeLeft / 60
                                    val seconds = timeLeft % 60
                                    val timeStr = String.format("%02d:%02d", minutes, seconds).toPersianDigits()
                                    Text(
                                        text = "ارسال مجدد کد تا $timeStr",
                                        color = primaryPurple,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = com.example.ui.theme.IranSansFontFamily
                                    )
                                } else {
                                    Text(
                                        text = "ارسال مجدد کد تایید",
                                        color = primaryPurple,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = com.example.ui.theme.IranSansFontFamily,
                                        modifier = Modifier.clickable {
                                            if (!isNetworkLoading) {
                                                requestOtpCode()
                                            }
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                // Primary Button: تایید و ادامه
                                Button(
                                    onClick = {
                                        if (!isNetworkLoading) {
                                            verifyOtpCode(normalizedOtp)
                                        }
                                    },
                                    enabled = !isNetworkLoading,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = Color(0x408B5CF6)),
                                    colors = ButtonDefaults.buttonColors(containerColor = primaryPurple),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    if (isNetworkLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = Color.White,
                                            strokeWidth = 2.5.dp
                                        )
                                    } else {
                                        Text(
                                            text = "تایید و ادامه",
                                            color = Color.White,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = com.example.ui.theme.IranSansFontFamily
                                        )
                                    }
                                }
                            }
                        }

                        AuthSheetStep.VERIFY -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .background(Color(0xFF10B981).copy(alpha = 0.12f), CircleShape)
                                        .border(2.dp, Color(0xFF10B981), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Success",
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "شماره همراه تایید شد ✨",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981),
                                    fontFamily = com.example.ui.theme.IranSansFontFamily
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "در حال انتقال به مرحلۀ بعد...",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B),
                                    fontFamily = com.example.ui.theme.IranSansFontFamily
                                )
                            }
                        }

                        AuthSheetStep.PROFILE -> {
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(
                                        text = "تکمیل مشخصات تحصیلی ✍️",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF1E1B4B),
                                        fontFamily = com.example.ui.theme.IranSansFontFamily,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (isHighSchool) "نام، پایه و رشته تحصیلی خود را مشخص کنید" else "نام و پایه تحصیلی خود را مشخص کنید",
                                        fontSize = 12.5.sp,
                                        color = Color(0xFF64748B),
                                        fontFamily = com.example.ui.theme.IranSansFontFamily,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(18.dp))

                                    // Field 1: Name Input
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        Text(
                                            text = "نام و نام خانوادگی",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF334155),
                                            fontFamily = com.example.ui.theme.IranSansFontFamily
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(50.dp),
                                            color = Color(0xFFF8FAFC),
                                            shape = RoundedCornerShape(14.dp),
                                            border = BorderStroke(
                                                width = 1.dp,
                                                color = if (nameError) Color(0xFFEF4444) else Color(0xFFE2E8F0)
                                            )
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(horizontal = 14.dp),
                                                contentAlignment = Alignment.CenterStart
                                            ) {
                                                BasicTextField(
                                                    value = nameInput,
                                                    onValueChange = {
                                                        nameInput = it
                                                        nameError = false
                                                    },
                                                    modifier = Modifier.fillMaxWidth(),
                                                    singleLine = true,
                                                    textStyle = TextStyle(
                                                        color = Color(0xFF1E1B4B),
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        fontFamily = com.example.ui.theme.IranSansFontFamily,
                                                        textAlign = TextAlign.Right
                                                    ),
                                                    cursorBrush = SolidColor(primaryPurple),
                                                    decorationBox = { innerTextField ->
                                                        Box(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            contentAlignment = Alignment.CenterStart
                                                        ) {
                                                            if (nameInput.isEmpty()) {
                                                                Text(
                                                                    text = "مثلاً: علی محمدی",
                                                                    color = Color(0xFF94A3B8),
                                                                    fontSize = 13.sp,
                                                                    fontFamily = com.example.ui.theme.IranSansFontFamily,
                                                                    textAlign = TextAlign.Right
                                                                )
                                                            }
                                                            innerTextField()
                                                        }
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Field 2: Grade Selection (پایه تحصیلی)
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        Text(
                                            text = "پایه تحصیلی",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF334155),
                                            fontFamily = com.example.ui.theme.IranSansFontFamily
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            gradeOptions.forEach { grade ->
                                                val isSelected = selectedGradeCode == grade.code
                                                Surface(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(38.dp)
                                                        .clickable { selectedGradeCode = grade.code },
                                                    shape = RoundedCornerShape(12.dp),
                                                    color = if (isSelected) primaryPurple else Color(0xFFF1F5F9),
                                                    border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFE2E8F0))
                                                ) {
                                                    Box(
                                                        contentAlignment = Alignment.Center,
                                                        modifier = Modifier.fillMaxSize()
                                                    ) {
                                                        Text(
                                                            text = grade.label,
                                                            color = if (isSelected) Color.White else Color(0xFF475569),
                                                            fontSize = 12.sp,
                                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                            fontFamily = com.example.ui.theme.IranSansFontFamily
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // Field 3: Major Selection (رشته تحصیلی) - ONLY shown for Grades 10, 11, 12 (دهم، یازدهم، دوازدهم)
                                    if (isHighSchool) {
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalAlignment = Alignment.Start
                                        ) {
                                            Text(
                                                text = "رشته تحصیلی",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF334155),
                                                fontFamily = com.example.ui.theme.IranSansFontFamily
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                majorOptions.forEach { major ->
                                                    val emoji = when (major.code) {
                                                        "EXPERIMENTAL_SCIENCES" -> "🧬"
                                                        "MATHEMATICS_PHYSICS" -> "📐"
                                                        else -> "📚"
                                                    }
                                                    val isSelected = selectedMajorCode == major.code
                                                    Surface(
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .height(42.dp)
                                                            .clickable { selectedMajorCode = major.code },
                                                        shape = RoundedCornerShape(12.dp),
                                                        color = if (isSelected) primaryPurple else Color(0xFFF1F5F9),
                                                        border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFE2E8F0))
                                                    ) {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.Center,
                                                            modifier = Modifier.fillMaxSize()
                                                        ) {
                                                            Text(text = emoji, fontSize = 12.sp)
                                                            Spacer(modifier = Modifier.width(3.dp))
                                                            Text(
                                                                text = major.label,
                                                                color = if (isSelected) Color.White else Color(0xFF475569),
                                                                fontSize = 11.sp,
                                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                                fontFamily = com.example.ui.theme.IranSansFontFamily
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(22.dp))

                                    // Save & Complete Login Button
                                    Button(
                                        onClick = {
                                            if (nameInput.trim().isEmpty()) {
                                                nameError = true
                                            } else {
                                                nameError = false
                                                isNetworkLoading = true
                                                coroutineScope.launch {
                                                    val fieldCode = if (isHighSchool) selectedMajorCode else null
                                                    val currentRegToken = registrationToken
                                                    val result = if (currentRegToken != null) {
                                                        val phone = com.example.network.ApiClient.getTokenManager()?.getRegistrationPhone()
                                                            ?: com.example.network.ApiClient.getTokenManager()?.getUserPhone()
                                                            ?: globalUserPhoneNumber
                                                        com.example.network.safeApiCall {
                                                            com.example.network.ApiClient.apiService.register(
                                                                com.example.network.RegisterRequest(
                                                                    phone = phone,
                                                                    registrationToken = currentRegToken,
                                                                    deviceType = "ANDROID",
                                                                    fullName = nameInput.trim(),
                                                                    grade = selectedGradeCode,
                                                                    fieldOfStudy = fieldCode,
                                                                )
                                                            )
                                                        }
                                                    } else {
                                                        com.example.network.safeApiCall {
                                                            com.example.network.ApiClient.apiService.completeOnboarding(
                                                                com.example.network.CompleteOnboardingDto(
                                                                    fullName = nameInput.trim(),
                                                                    grade = selectedGradeCode,
                                                                    fieldOfStudy = fieldCode,
                                                                )
                                                            )
                                                        }
                                                    }
                                                    isNetworkLoading = false
                                                    when (result) {
                                                        is com.example.network.NetworkResult.Success -> {
                                                            if (registrationToken != null) {
                                                                val auth = (result.data as? com.example.network.AuthResponseDto)?.body
                                                                if (auth?.accessToken.isNullOrBlank()) {
                                                                    Toast.makeText(context, "پاسخ ثبت‌نام نامعتبر است", Toast.LENGTH_LONG).show()
                                                                    return@launch
                                                                }
                                                                val tokenManager = com.example.network.ApiClient.getTokenManager()
                                                                    ?: com.example.network.TokenManager(context)
                                                                tokenManager.saveSession(
                                                                    auth!!.accessToken!!,
                                                                    auth.accessExpiresAt,
                                                                    auth.refreshExpiresAt,
                                                                )
                                                                tokenManager.saveUserData(
                                                                    auth.user?.id,
                                                                    globalUserPhoneNumber,
                                                                    auth.user?.role,
                                                                    nameInput.trim(),
                                                                )
                                                            }
                                                            val finalMajor = onboardingOptions.fieldsOfStudy
                                                                .firstOrNull { it.code == fieldCode }?.label.orEmpty()
                                                            context.getSharedPreferences("shetab_onboarding_prefs", android.content.Context.MODE_PRIVATE)
                                                                .edit()
                                                                .putString("user_name", nameInput.trim())
                                                                .putString("user_grade", selectedGrade)
                                                                .putString("user_major", finalMajor)
                                                                .putBoolean("is_logged_in", true)
                                                                .apply()
                                                            navController.navigate("dashboard") {
                                                                popUpTo("login_phone") { inclusive = true }
                                                            }
                                                        }
                                                        is com.example.network.NetworkResult.Error -> Toast.makeText(
                                                            context,
                                                            result.message ?: "ثبت مشخصات انجام نشد",
                                                            Toast.LENGTH_LONG,
                                                        ).show()
                                                        is com.example.network.NetworkResult.Exception -> Toast.makeText(
                                                            context,
                                                            "ارتباط با سرور برقرار نشد",
                                                            Toast.LENGTH_LONG,
                                                        ).show()
                                                    }
                                                }
                                            }
                                        },
                                        enabled = !isNetworkLoading &&
                                            selectedGradeCode.isNotBlank() &&
                                            (!isHighSchool || selectedMajorCode.isNotBlank()),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp)
                                            .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = Color(0x408B5CF6)),
                                        colors = ButtonDefaults.buttonColors(containerColor = primaryPurple),
                                        shape = RoundedCornerShape(20.dp)
                                    ) {
                                        Text(
                                            text = "ثبت مشخصات و ورود به شتاب 🚀",
                                            color = Color.White,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = com.example.ui.theme.IranSansFontFamily
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
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LoginPhoneScreen(navController: NavController) {
    AuthBottomSheetScreen(navController = navController, initialStep = AuthSheetStep.PHONE)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LoginOtpScreen(navController: NavController) {
    AuthBottomSheetScreen(navController = navController, initialStep = AuthSheetStep.OTP)
}
