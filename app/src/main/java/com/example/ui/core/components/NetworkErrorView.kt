package com.example.ui.core.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

/**
 * A standard, high-polish network error and offline state component
 * providing clear Persian visual feedback and a retry action.
 */
@Composable
fun NetworkErrorView(
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.error_network_title),
    description: String = stringResource(R.string.error_network_desc),
    retryButtonText: String = stringResource(R.string.action_retry),
    icon: ImageVector = Icons.Outlined.WifiOff,
    isRetrying: Boolean = false,
    fullScreen: Boolean = true,
    backgroundColor: Color = Color(0xFFF8F9FD),
    onRetry: () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        if (fullScreen) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(24.dp)
                    .testTag("network_error_view"),
                contentAlignment = Alignment.Center
            ) {
                NetworkErrorCardContent(
                    title = title,
                    description = description,
                    retryButtonText = retryButtonText,
                    icon = icon,
                    isRetrying = isRetrying,
                    onRetry = onRetry,
                    cardElevation = 0.dp
                )
            }
        } else {
            Surface(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("network_error_view"),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Box(
                    modifier = Modifier.padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    NetworkErrorCardContent(
                        title = title,
                        description = description,
                        retryButtonText = retryButtonText,
                        icon = icon,
                        isRetrying = isRetrying,
                        onRetry = onRetry,
                        cardElevation = 0.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun NetworkErrorCardContent(
    title: String,
    description: String,
    retryButtonText: String,
    icon: ImageVector,
    isRetrying: Boolean,
    onRetry: () -> Unit,
    cardElevation: Dp = 0.dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Icon with dual concentric soft circles
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFFEDE9FE).copy(alpha = 0.6f))
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFDDD6FE))
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color(0xFF6D28D9),
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp,
                lineHeight = 28.sp
            ),
            color = Color(0xFF1E1B4B),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Description
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                lineHeight = 22.sp
            ),
            color = Color(0xFF6B7280),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Retry Action Button
        Button(
            onClick = { if (!isRetrying) onRetry() },
            enabled = !isRetrying,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF7C3AED),
                contentColor = Color.White,
                disabledContainerColor = Color(0xFF7C3AED).copy(alpha = 0.6f),
                disabledContentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth(0.65f)
                .height(48.dp)
                .testTag("network_retry_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (isRetrying) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "در حال اتصال...",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = retryButtonText,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = retryButtonText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
