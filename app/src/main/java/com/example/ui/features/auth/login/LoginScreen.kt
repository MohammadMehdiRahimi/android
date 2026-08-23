package com.example.ui.features.auth.login

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.R
import com.example.ui.theme.VazirmatnFontFamily

private val PrimaryPurple = Color(0xFF6851FF)
private val BrandPurpleDark = Color(0xFF5E3CEE)
private val TextDark = Color(0xFF191B2D)
private val TextGray = Color(0xFF64748B)
private val TextPlaceholder = Color(0xFF94A3B8)
private val BorderStrokeColor = Color(0xFFE2E8F0)
private val BackgroundTop = Color(0xFFFCFBFF)
private val BackgroundBottom = Color(0xFFF6F4FE)

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = viewModel(),
    onNavigateToOtp: (phoneNumber: String) -> Unit = { phone ->
        if (phone.isNotBlank()) {
            navController.navigate("verify_otp/${android.net.Uri.encode(phone)}")
        } else {
            navController.navigate("login_otp")
        }
    }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val scrollState = rememberScrollState()

    var lastBackPressTime by remember { mutableStateOf(0L) }
    BackHandler(enabled = true) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBackPressTime < 2000) {
            (context as? Activity)?.finish()
        } else {
            lastBackPressTime = currentTime
            Toast.makeText(
                context,
                "برای خروج از برنامه، یک‌بار دیگر ضربه بزنید",
                Toast.LENGTH_SHORT
            ).show()
        }
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
                    Spacer(modifier = Modifier.height(24.dp))

                    // 1. Hero Vector Illustration
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp)
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.send_otp_vector),
                            contentDescription = "OTP Illustration",
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("login_hero_image"),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // 2. Title: شماره موبایل خود را وارد کنید
                    Text(
                        text = stringResource(id = R.string.login_title),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDark,
                        fontFamily = VazirmatnFontFamily,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_title")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 3. Subtitle: کد تأیید برای شما ارسال خواهد شد.
                    Text(
                        text = stringResource(id = R.string.login_subtitle),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextGray,
                        fontFamily = VazirmatnFontFamily,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_subtitle")
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // 4. Mobile Input Card Field with +98 on the left and phone number in LTR
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .shadow(
                                elevation = 3.dp,
                                shape = RoundedCornerShape(18.dp),
                                spotColor = Color(0x186851FF),
                                ambientColor = Color(0x08000000)
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                focusRequester.requestFocus()
                            }
                            .testTag("login_phone_card"),
                        shape = RoundedCornerShape(18.dp),
                        color = Color.White,
                        border = BorderStroke(
                            width = 1.2.dp,
                            color = if (uiState.errorMessage != null) Color(0xFFEF4444) else BorderStrokeColor
                        )
                    ) {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Left Section: Flag + +98 + Chevron + Divider
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.wrapContentWidth()
                                ) {
                                    // Iran Flag Image
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_flag_iran),
                                        contentDescription = "Iran Flag",
                                        modifier = Modifier
                                            .size(width = 26.dp, height = 18.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        contentScale = ContentScale.FillBounds
                                    )

                                    // +98 Code
                                    Text(
                                        text = "+98",
                                        color = TextDark,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = VazirmatnFontFamily
                                    )

                                    // Down Arrow Icon
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Country Code Dropdown",
                                        tint = TextDark,
                                        modifier = Modifier.size(18.dp)
                                    )

                                    Spacer(modifier = Modifier.width(6.dp))

                                    // Vertical Divider
                                    Box(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .height(24.dp)
                                            .background(BorderStrokeColor)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // Right Section: Phone Input Field in LTR
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    BasicTextField(
                                        value = uiState.displayPhoneNumber,
                                        onValueChange = { input ->
                                            viewModel.onPhoneNumberChanged(input)
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .focusRequester(focusRequester)
                                            .testTag("login_phone_input"),
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Phone,
                                            imeAction = ImeAction.Done
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onDone = {
                                                focusManager.clearFocus()
                                                if (uiState.isValid && !uiState.isLoading) {
                                                    viewModel.requestOtp { phone ->
                                                        onNavigateToOtp(phone)
                                                    }
                                                }
                                            }
                                        ),
                                        singleLine = true,
                                        textStyle = TextStyle(
                                            color = TextDark,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            fontFamily = VazirmatnFontFamily,
                                            textAlign = TextAlign.Start
                                        ),
                                        cursorBrush = SolidColor(PrimaryPurple),
                                        decorationBox = { innerTextField ->
                                            Box(
                                                modifier = Modifier.fillMaxWidth(),
                                                contentAlignment = Alignment.CenterStart
                                            ) {
                                                if (uiState.rawPhoneNumber.isEmpty()) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            text = stringResource(id = R.string.login_phone_placeholder),
                                                            color = TextPlaceholder,
                                                            fontSize = 14.sp,
                                                            fontFamily = VazirmatnFontFamily,
                                                            textAlign = TextAlign.Start
                                                        )
                                                        Text(
                                                            text = stringResource(id = R.string.login_phone_label),
                                                            color = TextPlaceholder,
                                                            fontSize = 14.sp,
                                                            fontFamily = VazirmatnFontFamily,
                                                            textAlign = TextAlign.End
                                                        )
                                                    }
                                                }
                                                innerTextField()
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Error Message
                    if (uiState.errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.errorMessage ?: "",
                            color = Color(0xFFEF4444),
                            fontSize = 12.5.sp,
                            fontFamily = VazirmatnFontFamily,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_error_text")
                        )
                    }
                }

                // Bottom Footer Section: Submit Button + Terms & Conditions
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Action Button: ورود و دریافت کد تأیید (placed at bottom)
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.requestOtp { phone ->
                                onNavigateToOtp(phone)
                            }
                        },
                        enabled = !uiState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .shadow(
                                elevation = 6.dp,
                                shape = RoundedCornerShape(18.dp),
                                spotColor = Color(0x336851FF),
                                ambientColor = Color(0x10000000)
                            )
                            .testTag("login_submit_button"),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryPurple,
                            disabledContainerColor = PrimaryPurple.copy(alpha = 0.6f)
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 2.dp,
                            pressedElevation = 6.dp
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
                                // Arrow Icon pointing left
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = stringResource(id = R.string.login_submit_button),
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = VazirmatnFontFamily
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    val termsAnnotatedString = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = TextGray,
                                fontSize = 13.sp,
                                fontFamily = VazirmatnFontFamily,
                                fontWeight = FontWeight.Normal
                            )
                        ) {
                            append(stringResource(id = R.string.login_terms_prefix))
                        }
                        withStyle(
                            SpanStyle(
                                color = PrimaryPurple,
                                fontSize = 13.sp,
                                fontFamily = VazirmatnFontFamily,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(stringResource(id = R.string.login_terms_link))
                        }
                        withStyle(
                            SpanStyle(
                                color = TextGray,
                                fontSize = 13.sp,
                                fontFamily = VazirmatnFontFamily,
                                fontWeight = FontWeight.Normal
                            )
                        ) {
                            append(stringResource(id = R.string.login_terms_suffix))
                        }
                    }

                    Text(
                        text = termsAnnotatedString,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                Toast.makeText(
                                    context,
                                    "قوانین و مقررات شتاب",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .testTag("login_terms_text")
                    )
                }
            }
        }
    }
}
