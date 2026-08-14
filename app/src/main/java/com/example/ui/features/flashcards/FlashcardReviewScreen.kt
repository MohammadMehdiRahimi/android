package com.example.ui.features.flashcards

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.data.local.entity.FlashcardEntity
import com.example.ui.core.components.AppBackground
import com.example.ui.core.components.LatexText
import com.example.ui.core.components.LatexSkeletonType
import com.example.ui.core.toPersianNumber
import com.example.ui.theme.LocalShetabColors

@Composable
fun FlashcardReviewScreen(
    flashcards: List<FlashcardEntity>,
    titleText: String,
    onAgain: (FlashcardEntity) -> Unit,
    onHard: (FlashcardEntity) -> Unit,
    onGood: (FlashcardEntity) -> Unit,
    onEasy: (FlashcardEntity) -> Unit,
    onClose: () -> Unit
) {
    val colors = LocalShetabColors.current
    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

    if (currentIndex >= flashcards.size) {
        LaunchedEffect(Unit) {
            onClose()
        }
        return
    }

    val currentCard = flashcards[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .background(colors.cardBg.copy(alpha = 0.5f), CircleShape)
                    .size(44.dp)
                    .border(1.dp, colors.primaryText.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "خروج", tint = colors.primaryText)
            }

            Text(
                text = titleText,
                color = colors.primaryText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${currentIndex + 1} از ${flashcards.size}".toPersianNumber(),
                color = colors.secondaryText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Sleek Minimalist Progress Indicator
        val progress = (currentIndex + 1).toFloat() / flashcards.size.toFloat()
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .height(6.dp)
                .clip(CircleShape),
            color = colors.accentMain,
            trackColor = colors.accentMain.copy(alpha = 0.15f),
        )

        Spacer(modifier = Modifier.weight(1f))

        // ULTRA-POLISHED SCALE / EASE INTERACTION TO SWITCH FRONT AND BACK GORGEOUSLY
        val scaleTransition by animateFloatAsState(
            targetValue = if (isFlipped) 1.02f else 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "scaleCard"
        )

        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.68f)
                .graphicsLayer {
                    scaleX = scaleTransition
                    scaleY = scaleTransition
                }
                .clickable { isFlipped = !isFlipped }
                .shadow(
                    elevation = if (isFlipped) 20.dp else 10.dp,
                    shape = RoundedCornerShape(26.dp),
                    clip = false
                ),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(
                containerColor = colors.cardBg
            ),
            border = BorderStroke(1.2.dp, colors.primaryText.copy(alpha = 0.08f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                // Animated content transition for absolute smoothness
                AnimatedContent(
                    targetState = isFlipped,
                    transitionSpec = {
                        slideInHorizontally(animationSpec = tween(250)) { width -> -width } + fadeIn(animationSpec = tween(250)) togetherWith
                        slideOutHorizontally(animationSpec = tween(250)) { width -> width } + fadeOut(animationSpec = tween(250))
                    },
                    label = "flashcardFlipAnimation"
                ) { flipped ->
                    if (!flipped) {
                        // FRONT STATE
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "سوال",
                                color = colors.accentMain,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier
                                    .background(colors.accentMain.copy(alpha = 0.1f), CircleShape)
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "کتاب: " + currentCard.category,
                                color = colors.secondaryText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                LatexText(
                                    latexString = currentCard.question,
                                    textColor = colors.primaryText,
                                    modifier = Modifier.fillMaxWidth(),
                                    skeletonType = LatexSkeletonType.FLASHCARD
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    Icons.Default.TouchApp,
                                    contentDescription = null,
                                    tint = colors.secondaryText.copy(alpha = 0.4f),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "برای نمایش پاسخ ضربه بزنید",
                                    color = colors.secondaryText.copy(alpha = 0.5f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    } else {
                        // BACK STATE (Both Question and Answer elegantly visible!)
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Top small question preview
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "سوال اصلی",
                                    color = colors.secondaryText.copy(alpha = 0.7f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "سطح ${currentCard.boxNumber}".toPersianNumber(),
                                    color = Color(0xFFFF9800),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Compact latex Question for reference
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.2f),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                LatexText(
                                    latexString = currentCard.question,
                                    textColor = colors.primaryText.copy(alpha = 0.6f),
                                    modifier = Modifier.fillMaxWidth(),
                                    skeletonType = LatexSkeletonType.FLASHCARD
                                )
                            }

                            Divider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = colors.primaryText.copy(alpha = 0.08f)
                            )

                            // Answer Section
                            Text(
                                text = "راهنما و پاسخ",
                                color = Color(0xFF43A047),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier
                                    .background(Color(0xFF43A047).copy(alpha = 0.1f), CircleShape)
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                LatexText(
                                    latexString = currentCard.explanation,
                                    textColor = colors.primaryText,
                                    modifier = Modifier.fillMaxWidth(),
                                    skeletonType = LatexSkeletonType.FLASHCARD
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "امتیاز را در پایین ثبت کنید",
                                color = colors.secondaryText.copy(alpha = 0.4f),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // FOUR COMPREHENSIVE ANKI CHOICES WITH INTEGRATED FEEDBACK TIMES
        if (isFlipped) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // 1. Again (دوباره)
                Button(
                    onClick = {
                        onAgain(currentCard)
                        isFlipped = false
                        currentIndex++
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)), // Red
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("دوباره", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Text("< ۱ دقیقه".toPersianNumber(), fontSize = 9.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }
                
                // 2. Hard (سخت)
                Button(
                    onClick = {
                        onHard(currentCard)
                        isFlipped = false
                        currentIndex++
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF757575)), // Grey/Dark
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.HourglassEmpty, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("سخت", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Text("عادی", fontSize = 9.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }

                // 3. Good (خوب)
                Button(
                    onClick = {
                        onGood(currentCard)
                        isFlipped = false
                        currentIndex++
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)), // Green
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("خوب", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Text("آسان", fontSize = 9.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }

                // 4. Easy (آسان)
                Button(
                    onClick = {
                        onEasy(currentCard)
                        isFlipped = false
                        currentIndex++
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)), // Blue
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("آسان", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Text("خیلی آسان", fontSize = 9.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }
        } else {
            // Friendly instruction box to prompt actions
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(vertical = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.accentMain.copy(alpha = 0.05f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = colors.accentMain, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "روی کارت بزنید تا پاسخ را بررسی و به خود امتیاز دهید.",
                        color = colors.secondaryText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
