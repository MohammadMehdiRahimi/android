package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val VazirmatnFontFamily = FontFamily(
    Font(R.font.vazirmatn_thin, FontWeight.Thin),
    Font(R.font.vazirmatn_extra_light, FontWeight.ExtraLight),
    Font(R.font.vazirmatn_light, FontWeight.Light),
    Font(R.font.vazirmatn_regular, FontWeight.Normal),
    Font(R.font.vazirmatn_medium, FontWeight.Medium),
    Font(R.font.vazirmatn_semi_bold, FontWeight.SemiBold),
    Font(R.font.vazirmatn_bold, FontWeight.Bold),
    Font(R.font.vazirmatn_extra_bold, FontWeight.ExtraBold),
    Font(R.font.vazirmatn_black, FontWeight.Black)
)

// Keep the old names as aliases so existing screens use Vazirmatn without a
// large, error-prone rename across the whole UI.
val IranSansXFontFamily = VazirmatnFontFamily

val IranSansFontFamily = IranSansXFontFamily

val PeydaFontFamily = VazirmatnFontFamily

val defaultTypography = Typography()

// Set of Material typography styles to start with
val Typography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = VazirmatnFontFamily, fontSize = 48.sp),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = VazirmatnFontFamily, fontSize = 36.sp),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = VazirmatnFontFamily, fontSize = 30.sp),
    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = VazirmatnFontFamily, fontSize = 28.sp),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = VazirmatnFontFamily, fontSize = 24.sp),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = VazirmatnFontFamily, fontSize = 20.sp),
    titleLarge = defaultTypography.titleLarge.copy(fontFamily = VazirmatnFontFamily, fontSize = 18.sp),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = VazirmatnFontFamily, fontSize = 14.sp),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = VazirmatnFontFamily, fontSize = 12.sp),
    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = VazirmatnFontFamily, fontSize = 14.sp, lineHeight = 22.sp, letterSpacing = 0.5.sp),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = VazirmatnFontFamily, fontSize = 12.sp),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = VazirmatnFontFamily, fontSize = 10.sp),
    labelLarge = defaultTypography.labelLarge.copy(fontFamily = VazirmatnFontFamily, fontSize = 12.sp),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = VazirmatnFontFamily, fontSize = 10.sp),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = VazirmatnFontFamily, fontSize = 9.sp)
)

