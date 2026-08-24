package com.example.ui.features.auth.otp

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.R
import com.example.ui.screens.toPersianDigits
import com.example.ui.theme.VazirmatnFontFamily

private val PrimaryPurple = Color(0xFF6851FF)
private val BrandPurpleDark = Color(0xFF5E3CEE)
private val TextDark = Color(0xFF191B2D)
private val TextGray = Color(0xFF64748B)
private val BorderStrokeColor = Color(0xFFE2E8F0)
private val ActiveBoxBorder = Color(0xFF6851FF)
private val ActiveBoxBackground = Color(0xFFFAF9FF)
private val PillBackground = Color(0xFFF1EFFF)
private val BackgroundTop = Color(0xFFFCFBFF)
private val BackgroundBottom = Color(0xFFF6F4FE)

@Composable
fun VerifyOtpScreen(
    navController: NavController,
    phoneNumber: String = "",
    viewModel: VerifyOtpViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return VerifyOtpViewModel(phoneNumber) as T
            }
        }
    ),
    onVerificationSuccess: (isNewUser: Boolean, registrationToken: String?, onboardingRequired: Boolean) -> Unit = { isNewUser, token, onboardingRequired ->
        if (isNewUser && !token.isNullOrBlank()) {
            navController.navigate("register_route") {
                popUpTo("login_phone") { inclusive = true }
            }
        } else if (onboardingRequired) {
            // Ideally navigate to onboarding profile completion, but for now we route to register if it's the same or dashboard if different
            navController.navigate("dashboard") {
                popUpTo("login_phone") { inclusive = true }
            }
        } else {
            navController.navigate("dashboard") {
                popUpTo("login_phone") { inclusive = true }
            }
        }
    }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val scrollState = rememberScrollState()
    var isOtpFocused by remember { mutableStateOf(false) }

    LaunchedEffect(phoneNumber) {
        if (phoneNumber.isNotBlank() && phoneNumber != uiState.rawPhoneNumber) {
            viewModel.setPhoneNumber(phoneNumber)
        }
    }

    LaunchedEffect(Unit) {
        // Automatically request focus for smooth OTP entry
        focusRequester.requestFocus()
    }

    BackHandler(enabled = true) {
        navController.popBackStack()
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(BackgroundTop, BackgroundBottom)
                    )
                )
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .testTag("verify_otp_root")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Content Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top App Bar / Brand Header
                    TopHeaderBar(
                        onBackClick = {
                            focusManager.clearFocus()
                            navController.popBackStack()
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Hero 3D Vector Graphic
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 260.dp)
                            .aspectRatio(1.1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.verify_otp_vector),
                            contentDescription = "Verify OTP Illustration",
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("otp_hero_image"),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Title: کد تأیید را وارد کنید
                    Text(
                        text = stringResource(id = R.string.otp_title),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDark,
                        fontFamily = VazirmatnFontFamily,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("otp_title")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Subtitle: کد ۶ رقمی ارسال شده به شماره ۰۹۱۲ ۳۴۵ ۶۷۸۹ را وارد کنید.
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("otp_subtitle"),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.otp_subtitle_prefix),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = TextGray,
                            fontFamily = VazirmatnFontFamily,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.otp_subtitle_suffix),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = TextGray,
                                fontFamily = VazirmatnFontFamily
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                Text(
                                    text = uiState.formattedPhoneNumber,
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark,
                                    fontFamily = VazirmatnFontFamily,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(26.dp))

                    // 6-Cell Interactive OTP Input Box Row
                    OtpInputCells(
                        code = uiState.otpCode,
                        isFocused = isOtpFocused,
                        hasError = uiState.errorMessage != null,
                        focusRequester = focusRequester,
                        onCodeChange = { newCode ->
                            viewModel.onOtpCodeChanged(newCode)
                            if (newCode.length == 6) {
                                focusManager.clearFocus()
                            }
                        },
                        onFocusChanged = { isOtpFocused = it },
                        onSubmit = {
                            if (uiState.isCodeComplete && !uiState.isLoading) {
                                focusManager.clearFocus()
                                viewModel.verifyCode(context) { isNew, token, onboardingRequired ->
                                    onVerificationSuccess(isNew, token, onboardingRequired)
                                }
                            }
                        }
                    )

                    // Error Message Display
                    if (uiState.errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.errorMessage ?: "",
                            color = Color(0xFFEF4444),
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = VazirmatnFontFamily,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("otp_error_text")
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Countdown Timer Row: "زمان باقی‌مانده: ۰۱:۵۹" with Clock icon
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("otp_timer_row"),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.formattedTime,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.isTimerActive) BrandPurpleDark else TextGray,
                            fontFamily = VazirmatnFontFamily
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(id = R.string.otp_timer_label),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = TextGray,
                            fontFamily = VazirmatnFontFamily
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = "Timer",
                            tint = if (uiState.isTimerActive) PrimaryPurple else TextGray,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Resend OTP Pill Button
                    ResendOtpPillButton(
                        text = uiState.resendButtonText,
                        isClickable = !uiState.isTimerActive && !uiState.isResending,
                        isResending = uiState.isResending,
                        onClick = {
                            viewModel.resendOtp {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.otp_resend_success),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )
                }

                // Bottom Action Button Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 28.dp, bottom = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.verifyCode(context) { isNew, token, onboardingRequired ->
                                onVerificationSuccess(isNew, token, onboardingRequired)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(
                                elevation = if (uiState.isCodeComplete) 8.dp else 2.dp,
                                shape = RoundedCornerShape(18.dp),
                                spotColor = Color(0x386851FF),
                                ambientColor = Color(0x10000000)
                            )
                            .testTag("otp_submit_button"),
                        enabled = !uiState.isLoading,
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryPurple,
                            disabledContainerColor = PrimaryPurple.copy(alpha = 0.5f)
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 2.dp
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = stringResource(id = R.string.otp_submit_button),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontFamily = VazirmatnFontFamily
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Forward",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopHeaderBar(
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        // Back Button on start (RTL right, LTR left)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .testTag("otp_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = BrandPurpleDark,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Center Brand Logo: "⚡ شتاب"
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "شتاب",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = BrandPurpleDark,
                fontFamily = VazirmatnFontFamily
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "⚡",
                fontSize = 20.sp,
                color = PrimaryPurple
            )
        }
    }
}

@Composable
private fun OtpInputCells(
    code: String,
    isFocused: Boolean,
    hasError: Boolean,
    focusRequester: FocusRequester,
    onCodeChange: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    onSubmit: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cursorBlink")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursorAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                focusRequester.requestFocus()
            }
            .testTag("otp_input_container"),
        contentAlignment = Alignment.Center
    ) {
        // Hidden BasicTextField capturing all keyboard inputs
        BasicTextField(
            value = code,
            onValueChange = onCodeChange,
            modifier = Modifier
                .size(1.dp)
                .alpha(0f)
                .focusRequester(focusRequester)
                .onFocusChanged { onFocusChanged(it.isFocused) }
                .testTag("otp_hidden_text_field"),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onSubmit() }
            ),
            singleLine = true,
            cursorBrush = SolidColor(Color.Transparent)
        )

        // 6 Discrete Display Cells rendered in LTR order so digits map 0 -> 5 from left to right
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(6) { index ->
                    val char = code.getOrNull(index)?.toString()?.toPersianDigits().orEmpty()
                    val isCurrentActiveSlot = isFocused && (code.length == index || (index == 5 && code.length == 6))
                    val isFilled = index < code.length

                    val borderColor = when {
                        hasError -> Color(0xFFEF4444)
                        isCurrentActiveSlot -> ActiveBoxBorder
                        isFilled -> PrimaryPurple.copy(alpha = 0.5f)
                        else -> BorderStrokeColor
                    }

                    val backgroundColor = when {
                        isCurrentActiveSlot -> ActiveBoxBackground
                        else -> Color.White
                    }

                    Surface(
                        modifier = Modifier
                            .width(48.dp)
                            .height(56.dp)
                            .shadow(
                                elevation = if (isCurrentActiveSlot) 4.dp else 1.dp,
                                shape = RoundedCornerShape(16.dp),
                                spotColor = if (isCurrentActiveSlot) Color(0x286851FF) else Color(0x0A000000)
                            )
                            .testTag("otp_box_$index"),
                        shape = RoundedCornerShape(16.dp),
                        color = backgroundColor,
                        border = BorderStroke(
                            width = if (isCurrentActiveSlot) 1.8.dp else 1.dp,
                            color = borderColor
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (char.isNotEmpty()) {
                                Text(
                                    text = char,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark,
                                    fontFamily = VazirmatnFontFamily,
                                    textAlign = TextAlign.Center
                                )
                            } else if (isCurrentActiveSlot) {
                                // Animated Blinking Cursor Bar
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(24.dp)
                                        .alpha(cursorAlpha)
                                        .background(PrimaryPurple, RoundedCornerShape(1.dp))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResendOtpPillButton(
    text: String,
    isClickable: Boolean,
    isResending: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                enabled = isClickable,
                onClick = onClick
            )
            .testTag("otp_resend_button"),
        shape = RoundedCornerShape(20.dp),
        color = PillBackground,
        border = BorderStroke(
            width = 1.dp,
            color = if (isClickable) PrimaryPurple.copy(alpha = 0.3f) else Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isResending) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = PrimaryPurple,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            } else {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Resend",
                    tint = if (isClickable) PrimaryPurple else PrimaryPurple.copy(alpha = 0.7f),
                    modifier = Modifier.size(17.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = text,
                fontSize = 13.5.sp,
                fontWeight = if (isClickable) FontWeight.Bold else FontWeight.Medium,
                color = if (isClickable) BrandPurpleDark else PrimaryPurple.copy(alpha = 0.85f),
                fontFamily = VazirmatnFontFamily
            )
        }
    }
}
