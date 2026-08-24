package com.example.ui.features.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.R
import com.example.ui.theme.VazirmatnFontFamily
import kotlinx.coroutines.launch

private val PurplePrimary = Color(0xFF5E3CEE)
private val PurpleLight = Color(0xFF6851FF)
private val DarkTitleColor = Color(0xFF191B2D)
private val SubtitleColor = Color(0xFF555974)
private val DotInactiveColor = Color(0xFFDCDCFA)
private val BackgroundTop = Color(0xFFFAF9FE)
private val BackgroundBottom = Color(0xFFF3F2FD)

@Composable
fun OnboardingScreen(navController: NavController) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

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
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Horizontal pager containing the 2 onboarding pages
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) { page ->
                    if (page == 0) {
                        OnboardingPageOne()
                    } else {
                        OnboardingPageTwo()
                    }
                }

                // Page indicators (2 dots)
                Row(
                    modifier = Modifier
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(2) { index ->
                        val isSelected = pagerState.currentPage == index
                        val width by animateDpAsState(
                            targetValue = if (isSelected) 10.dp else 10.dp,
                            animationSpec = tween(300),
                            label = "dot_width"
                        )
                        Box(
                            modifier = Modifier
                                .size(width = width, height = 10.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) PurpleLight else DotInactiveColor
                                )
                        )
                    }
                }

                // Bottom Action Button
                Button(
                    onClick = {
                        if (pagerState.currentPage < 1) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        } else {
                            navController.navigate("login_phone") {
                                popUpTo("onboarding") { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 24.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PurpleLight
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 6.dp
                    )
                ) {
                    Text(
                        text = if (pagerState.currentPage == 0) "بعدی" else "شروع شتاب",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = VazirmatnFontFamily
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingPageOne() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Vector Illustration
        Image(
            painter = painterResource(id = R.drawable.onboarding_page_1_vector),
            contentDescription = "برنامه هوشمند شتاب",
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 380.dp)
                .aspectRatio(0.92f),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Title: شتاب، برنامه فرداتو می‌سازه
        val titleText = buildAnnotatedString {
            withStyle(
                SpanStyle(
                    color = PurpleLight,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = VazirmatnFontFamily,
                    fontSize = 24.sp
                )
            ) {
                append("شتاب،")
            }
            withStyle(
                SpanStyle(
                    color = DarkTitleColor,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = VazirmatnFontFamily,
                    fontSize = 24.sp
                )
            ) {
                append(" برنامه فرداتو می‌سازه")
            }
        }

        Text(
            text = titleText,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Subtitle / Description text
        val subtitleText = buildAnnotatedString {
            withStyle(
                SpanStyle(
                    color = SubtitleColor,
                    fontWeight = FontWeight.Normal,
                    fontFamily = VazirmatnFontFamily,
                    fontSize = 15.sp
                )
            ) {
                append("بر اساس عملکرد امروزت، شتاب بهترین برنامه\nمطالعه فردا رو بهت پیشنهاد می‌ده.\n")
            }
            withStyle(
                SpanStyle(
                    color = PurpleLight,
                    fontWeight = FontWeight.Bold,
                    fontFamily = VazirmatnFontFamily,
                    fontSize = 15.sp
                )
            ) {
                append("هوشمند،")
            }
            withStyle(
                SpanStyle(
                    color = SubtitleColor,
                    fontWeight = FontWeight.Normal,
                    fontFamily = VazirmatnFontFamily,
                    fontSize = 15.sp
                )
            ) {
                append(" منظم و کاملاً شخصی‌سازی شده!")
            }
        }

        Text(
            text = subtitleText,
            textAlign = TextAlign.Center,
            lineHeight = 25.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun OnboardingPageTwo() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Vector Illustration for page 2 - compact height to bring text up
        Image(
            painter = painterResource(id = R.drawable.onboarding_page_2_vector),
            contentDescription = "آزمون و رقابت در شتاب",
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .heightIn(max = 220.dp)
                .aspectRatio(1.15f),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Headlines:
        // رقابت کن، آزمون بده و با لذت درس بخون
        // هوش مصنوعی هم تحلیلت می‌کنه
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "رقابت کن، آزمون بده و با لذت درس بخون",
                color = DarkTitleColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = VazirmatnFontFamily,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "هوش مصنوعی هم تحلیلت می‌کنه",
                color = PurpleLight,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = VazirmatnFontFamily,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Subtitle:
        Text(
            text = "شتاب لیگ‌های رقابتی، آزمون‌ساز حرفه‌ای و تحلیل هوشمند رو\nکنار هم آورده تا یادگیری برات جذاب‌تر و مؤثرتر بشه.",
            color = SubtitleColor,
            fontSize = 13.5.sp,
            lineHeight = 22.sp,
            fontFamily = VazirmatnFontFamily,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 3 Feature Cards in a row:
        // RTL order: [لیگ رقابتی] [آزمون‌ساز] [تحلیل هوشمند]
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FeaturePillCard(
                title = "لیگ رقابتی",
                icon = Icons.Outlined.EmojiEvents,
                modifier = Modifier.weight(1f)
            )
            FeaturePillCard(
                title = "آزمون‌ساز",
                icon = Icons.AutoMirrored.Outlined.Assignment,
                modifier = Modifier.weight(1f)
            )
            FeaturePillCard(
                title = "تحلیل هوشمند",
                icon = Icons.Outlined.Psychology,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun FeaturePillCard(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color(0x1A6851FF),
                ambientColor = Color(0x0D000000)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, Color(0xFFF1EFFE))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                color = DarkTitleColor,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = VazirmatnFontFamily,
                maxLines = 1
            )
            Spacer(modifier = Modifier.width(5.dp))
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = PurpleLight,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
