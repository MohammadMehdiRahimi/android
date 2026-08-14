package com.example.ui.core.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.graphics.Color
import com.example.R
import com.example.ui.theme.LocalShetabColors

@Composable
fun AppBackground(showPattern: Boolean = false, customBgColor: Color? = null) {
    val bgColor = customBgColor ?: Color.White
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
    }
}
