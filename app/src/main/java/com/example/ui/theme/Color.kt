package com.example.ui.theme

import androidx.compose.ui.graphics.Color

enum class AppTheme {
    PESARANE,     // Boys (Lapis Midnight Blue) - Beautiful dark-mode-like rich feel
    DOKHTARONE,    // Girls (Rose Cherry Pink) - Soft pastel beauty
    BAHAR,         // Spring (Fresh Blossom Green & Peach Glow) - Vibrant new beginnings
    TABESTAN,      // Summer (Golden Yellow / Summer Orange / Deep Ocean Blue) - Bright energizing rays
    PAEEZ,         // Autumn (Amber Copper / Golden Auburn / Pumpkin Spice) - Warm cozy vibes
    ZEMESTAN       // Winter (Glacier Frost / Ice Blue / Snowy Silver) - Cool crisp atmosphere
}

data class ShetabColorPalette(
    val bgMain: Color,
    val bgTopHeader: Color,
    val primaryText: Color,
    val secondaryText: Color,
    val accentMain: Color,
    val accentSecondary: Color,
    val cardBg: Color,
    val cardIconBg: Color,
    val bottomNavBg: Color,
    val bottomNavUnselected: Color,
    val chartFill: Color,
    val chartLine: Color,
    val tooltipBg: Color,
    val tooltipText: Color,
    val sunColor: Color,
    val mountainFront: Color,
    val mountainBack: Color,
    val mountainBase: Color
)

// === 1. PESARANE THEME (The user's favorite Lapis/Lajevardi Blue theme) ===
val ThemeBoys = ShetabColorPalette(
    bgMain = Color.White,
    bgTopHeader = Color(0xFFC5DBF0),
    primaryText = Color(0xFF230462),
    secondaryText = Color(0xFF203590).copy(alpha = 0.7f),
    accentMain = Color(0xFF203590),
    accentSecondary = Color(0xFF6083C5),
    cardBg = Color.White,
    cardIconBg = Color(0xFF96B8DB).copy(alpha = 0.3f),
    bottomNavBg = Color.White,
    bottomNavUnselected = Color(0xFF230462).copy(alpha = 0.6f),
    chartFill = Color(0xFF96B8DB),
    chartLine = Color(0xFF203590),
    tooltipBg = Color(0xFF203590),
    tooltipText = Color.White,
    sunColor = Color.White.copy(alpha = 0.8f),
    mountainFront = Color(0xFFF0F5FA),
    mountainBack = Color(0xFF96B8DB).copy(alpha=0.4f),
    mountainBase = Color(0xFFC5DBF0)
)

// === 2. DOKHTARONE THEME (Girly - Soft Elegant Pink & Rose Orchid) ===
val ThemeGirls = ShetabColorPalette(
    bgMain = Color(0xFFFFF0F5),
    bgTopHeader = Color(0xFFFFD1DC),
    primaryText = Color(0xFF6B1A30),
    secondaryText = Color(0xFFB56576),
    accentMain = Color(0xFFD62246),
    accentSecondary = Color(0xFFE5A4B4),
    cardBg = Color.White,
    cardIconBg = Color(0xFFFEE1EC),
    bottomNavBg = Color.White,
    bottomNavUnselected = Color(0xFFB56576).copy(alpha = 0.6f),
    chartFill = Color(0xFFFFC0CB),
    chartLine = Color(0xFFD62246),
    tooltipBg = Color(0xFFD62246),
    tooltipText = Color.White,
    sunColor = Color(0xFFFFB7B2),
    mountainFront = Color(0xFFFFF0F5),
    mountainBack = Color(0xFFFBC4D4),
    mountainBase = Color(0xFFFFD1DC)
)

// === 3. BAHAR THEME (Spring - Fresh Blossom Green & Mint Glow) ===
val ThemeSpring = ShetabColorPalette(
    bgMain = Color(0xFFF4FBF4),
    bgTopHeader = Color(0xFFFFE5D9),
    primaryText = Color(0xFF1B4332),
    secondaryText = Color(0xFF52B788),
    accentMain = Color(0xFF2D6A4F),
    accentSecondary = Color(0xFFFFCAD4),
    cardBg = Color.White,
    cardIconBg = Color(0xFFD8F3DC),
    bottomNavBg = Color.White,
    bottomNavUnselected = Color(0xFF52B788).copy(alpha = 0.6f),
    chartFill = Color(0xFFB7E4C7),
    chartLine = Color(0xFF2D6A4F),
    tooltipBg = Color(0xFF2D6A4F),
    tooltipText = Color.White,
    sunColor = Color(0xFFFFCAD4),
    mountainFront = Color(0xFFF4FBF4),
    mountainBack = Color(0xFF95D5B2),
    mountainBase = Color(0xFFFFE5D9)
)

// === 4. TABESTAN THEME (Summer - Sunny Gold Orange & Surf Navy) ===
val ThemeSummer = ShetabColorPalette(
    bgMain = Color(0xFFFFFDF0),
    bgTopHeader = Color(0xFFFFECA1),
    primaryText = Color(0xFF0F2C59),
    secondaryText = Color(0xFFFF8C00),
    accentMain = Color(0xFFFF6B35),
    accentSecondary = Color(0xFFFFD166),
    cardBg = Color.White,
    cardIconBg = Color(0xFFE0F4FF),
    bottomNavBg = Color.White,
    bottomNavUnselected = Color(0xFF008DDA).copy(alpha = 0.6f),
    chartFill = Color(0xFFFFD166),
    chartLine = Color(0xFFFF6B35),
    tooltipBg = Color(0xFFFF6B35),
    tooltipText = Color.White,
    sunColor = Color(0xFFFFF07C),
    mountainFront = Color(0xFFFFFDF0),
    mountainBack = Color(0xFFFFD166).copy(alpha = 0.6f),
    mountainBase = Color(0xFFE0F4FF)
)

// === 5. PAEEZ THEME (Autumn - Copper Terracotta & Warm Mahogany) ===
val ThemeAutumn = ShetabColorPalette(
    bgMain = Color(0xFFFFFBF5),
    bgTopHeader = Color(0xFFFCDDBC),
    primaryText = Color(0xFF431B04),
    secondaryText = Color(0xFFB55D14),
    accentMain = Color(0xFFC84B31),
    accentSecondary = Color(0xFFE3A857),
    cardBg = Color.White,
    cardIconBg = Color(0xFFF7E1D7),
    bottomNavBg = Color.White,
    bottomNavUnselected = Color(0xFFB55D11).copy(alpha = 0.6f),
    chartFill = Color(0xFFE4C59E),
    chartLine = Color(0xFFC84B31),
    tooltipBg = Color(0xFFC84B31),
    tooltipText = Color.White,
    sunColor = Color(0xFFFFA23A),
    mountainFront = Color(0xFFFFFBF5),
    mountainBack = Color(0xFFE3A857).copy(alpha = 0.5f),
    mountainBase = Color(0xFFFCDDBC)
)

// === 6. ZEMESTAN THEME (Winter - Glacier Frost Ice Silver Blue) ===
val ThemeWinter = ShetabColorPalette(
    bgMain = Color(0xFFF2F8FD),
    bgTopHeader = Color(0xFFDCEAF5),
    primaryText = Color(0xFF1A365D),
    secondaryText = Color(0xFF6B8DB0),
    accentMain = Color(0xFF3182CE),
    accentSecondary = Color(0xFFBEE3F8),
    cardBg = Color.White,
    cardIconBg = Color(0xFFEBF8FF),
    bottomNavBg = Color.White,
    bottomNavUnselected = Color(0xFF6B8DB0).copy(alpha = 0.6f),
    chartFill = Color(0xFFBEE3F8),
    chartLine = Color(0xFF3182CE),
    tooltipBg = Color(0xFF3182CE),
    tooltipText = Color.White,
    sunColor = Color(0xFFE2E8F0),
    mountainFront = Color(0xFFF2F8FD),
    mountainBack = Color(0xFFBDD7EE),
    mountainBase = Color(0xFFDCEAF5)
)
