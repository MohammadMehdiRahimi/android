package com.example.ui.core.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalShetabColors

fun Modifier.shimmerEffect(
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(16.dp)
): Modifier = composed {
    val colors = LocalShetabColors.current
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val baseColor = colors.primaryText.copy(alpha = 0.06f)
    val highlightColor = colors.primaryText.copy(alpha = 0.16f)

    val brush = Brush.linearGradient(
        colors = listOf(
            baseColor,
            highlightColor,
            baseColor
        ),
        start = Offset(translateAnim - 200f, translateAnim - 200f),
        end = Offset(translateAnim, translateAnim)
    )

    this
        .clip(shape)
        .background(brush)
}

@Composable
fun SkeletonCard(
    height: Dp = 100.dp,
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(20.dp)
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .shimmerEffect(shape)
    )
}

@Composable
fun HomeSkeletonLoading() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Skeleton (Profile Avatar + User Name/Title & Bell Button)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .shimmerEffect(CircleShape)
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .width(90.dp)
                            .height(16.dp)
                            .shimmerEffect(RoundedCornerShape(6.dp))
                    )
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(12.dp)
                            .shimmerEffect(RoundedCornerShape(4.dp))
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .shimmerEffect(RoundedCornerShape(12.dp))
            )
        }

        // 2. Top 4 Stats Row Skeleton (4 small stat cards side by side)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(4) {
                SkeletonCard(
                    height = 76.dp,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }

        // 3. Overall Performance Chart Skeleton
        SkeletonCard(
            height = 220.dp,
            shape = RoundedCornerShape(24.dp)
        )

        // 4. Pro Banner Card Skeleton
        SkeletonCard(
            height = 105.dp,
            shape = RoundedCornerShape(22.dp)
        )

        // 5. Feature Grid Hub Skeleton (2x2 Grid)
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SkeletonCard(
                    height = 95.dp,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp)
                )
                SkeletonCard(
                    height = 95.dp,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SkeletonCard(
                    height = 95.dp,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp)
                )
                SkeletonCard(
                    height = 95.dp,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }
    }
}

@Composable
fun StudyPlanSkeletonLoading() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.width(140.dp).height(24.dp).shimmerEffect(RoundedCornerShape(8.dp)))
            Box(modifier = Modifier.size(36.dp).shimmerEffect(CircleShape))
        }

        SkeletonCard(height = 44.dp, shape = RoundedCornerShape(100.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(5) {
                SkeletonCard(height = 70.dp, modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp))
            }
        }

        SkeletonCard(height = 150.dp, shape = RoundedCornerShape(24.dp))
        SkeletonCard(height = 80.dp, shape = RoundedCornerShape(18.dp))
        SkeletonCard(height = 80.dp, shape = RoundedCornerShape(18.dp))
    }
}

@Composable
fun AcademicReportSkeletonLoading() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.width(150.dp).height(24.dp).shimmerEffect(RoundedCornerShape(8.dp)))
            Box(modifier = Modifier.size(36.dp).shimmerEffect(CircleShape))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SkeletonCard(height = 90.dp, modifier = Modifier.weight(1f), shape = RoundedCornerShape(20.dp))
            SkeletonCard(height = 90.dp, modifier = Modifier.weight(1f), shape = RoundedCornerShape(20.dp))
        }

        SkeletonCard(height = 210.dp, shape = RoundedCornerShape(24.dp))
        SkeletonCard(height = 76.dp, shape = RoundedCornerShape(18.dp))
        SkeletonCard(height = 76.dp, shape = RoundedCornerShape(18.dp))
    }
}

@Composable
fun ExamsSkeletonLoading() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.width(130.dp).height(24.dp).shimmerEffect(RoundedCornerShape(8.dp)))
            Box(modifier = Modifier.size(36.dp).shimmerEffect(CircleShape))
        }

        SkeletonCard(height = 120.dp, shape = RoundedCornerShape(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(4) {
                SkeletonCard(height = 36.dp, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
            }
        }

        SkeletonCard(height = 110.dp, shape = RoundedCornerShape(20.dp))
        SkeletonCard(height = 110.dp, shape = RoundedCornerShape(20.dp))
    }
}

@Composable
fun ProfileSkeletonLoading() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(modifier = Modifier.size(64.dp).shimmerEffect(CircleShape))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.width(140.dp).height(20.dp).shimmerEffect(RoundedCornerShape(6.dp)))
                Box(modifier = Modifier.width(100.dp).height(14.dp).shimmerEffect(RoundedCornerShape(4.dp)))
            }
        }

        SkeletonCard(height = 110.dp, shape = RoundedCornerShape(24.dp))
        SkeletonCard(height = 60.dp, shape = RoundedCornerShape(16.dp))
        SkeletonCard(height = 60.dp, shape = RoundedCornerShape(16.dp))
        SkeletonCard(height = 60.dp, shape = RoundedCornerShape(16.dp))
    }
}
