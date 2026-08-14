package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

val LocalShetabColors = staticCompositionLocalOf<ShetabColorPalette> {
    error("No ShetabColorPalette provided")
}

@Composable
fun MyApplicationTheme(
    appTheme: AppTheme = AppTheme.PESARANE,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorPalette = when (appTheme) {
        AppTheme.PESARANE -> ThemeBoys
        AppTheme.DOKHTARONE -> ThemeGirls
        AppTheme.BAHAR -> ThemeSpring
        AppTheme.TABESTAN -> ThemeSummer
        AppTheme.PAEEZ -> ThemeAutumn
        AppTheme.ZEMESTAN -> ThemeWinter
    }

    CompositionLocalProvider(
        LocalShetabColors provides colorPalette,
        LocalLayoutDirection provides LayoutDirection.Rtl
    ) {
        MaterialTheme(
            typography = Typography,
            content = content
        )
    }
}
