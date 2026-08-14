package com.example.ui.features.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.MockStoriesData
import com.example.data.ShetabStory
import com.example.ui.theme.LocalShetabColors
import kotlinx.coroutines.delay

fun parseHexColor(hex: String, defaultColor: Color = Color.Gray): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        defaultColor
    }
}

@Composable
fun StoriesSection() {
    val colors = LocalShetabColors.current
    var activeStoryIndex by remember { mutableStateOf<Int?>(null) }
    var showNotifications by remember { mutableStateOf(false) }
    var notifications by remember { mutableStateOf(listOf("ماموریت‌های امروز رو تکمیل نکردی!", "یادآوری: آزمون جامع فردا ساعت ۹ صبح")) }
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 8.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp), clip = false)
            .background(colors.cardBg, RoundedCornerShape(20.dp))
            .border(1.dp, colors.primaryText.copy(alpha = 0.04f), RoundedCornerShape(20.dp))
            .clickable { isExpanded = !isExpanded }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "📢 استوری‌ها و اخبار جدید شتاب",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primaryText
                )
                if (notifications.isNotEmpty() && !isExpanded) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Color.Red, CircleShape)
                    )
                }
            }
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = colors.secondaryText,
                modifier = Modifier.size(20.dp)
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(colors.cardIconBg)
                            .clickable { showNotifications = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = colors.accentMain,
                            modifier = Modifier.size(18.dp)
                        )
                        if (notifications.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(Color.Red, CircleShape)
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-2).dp, y = 2.dp)
                            )
                        }
                    }

                    LazyRow(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        reverseLayout = true
                    ) {
                        itemsIndexed(MockStoriesData.stories) { index, story ->
                            val startColor = parseHexColor(story.gradientStart, colors.accentMain)
                            val endColor = parseHexColor(story.gradientEnd, colors.accentSecondary)
                            val borderBrush = Brush.linearGradient(listOf(startColor, endColor))
                            val isRead = story.isRead

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .width(50.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = {
                                            story.isRead = true
                                            activeStoryIndex = index
                                        }
                                    )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .then(
                                            if (!isRead) {
                                                Modifier.border(2.dp, borderBrush, CircleShape)
                                            } else {
                                                Modifier.border(1.dp, colors.primaryText.copy(alpha = 0.15f), CircleShape)
                                            }
                                        )
                                        .padding(2.5.dp)
                                        .background(colors.cardBg, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .background(
                                                brush = Brush.linearGradient(
                                                    colors = listOf(startColor.copy(alpha = 0.12f), endColor.copy(alpha = 0.12f))
                                                ),
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = story.emoji,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = story.title,
                                    color = if (isRead) colors.secondaryText else colors.primaryText,
                                    fontSize = 8.5.sp,
                                    fontWeight = if (isRead) FontWeight.Medium else FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Active Story Viewer
    activeStoryIndex?.let { index ->
        val currentStory = MockStoriesData.stories.getOrNull(index)
        if (currentStory != null) {
            StoryViewerDialog(
                story = currentStory,
                onClose = { activeStoryIndex = null },
                onNext = {
                    if (index + 1 < MockStoriesData.stories.size) {
                        MockStoriesData.stories[index + 1].isRead = true
                        activeStoryIndex = index + 1
                    } else {
                        activeStoryIndex = null
                    }
                },
                onPrevious = {
                    if (index - 1 >= 0) {
                        MockStoriesData.stories[index - 1].isRead = true
                        activeStoryIndex = index - 1
                    }
                }
            )
        }
    }

    if (showNotifications) {
        @OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
        androidx.compose.material3.ModalBottomSheet(
            onDismissRequest = { showNotifications = false },
            containerColor = colors.cardBg,
            sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 32.dp).fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "پیام‌ها و اعلانات", fontWeight = FontWeight.Bold, color = colors.primaryText, fontSize = 18.sp)
                    if (notifications.isNotEmpty()) {
                        TextButton(onClick = { notifications = emptyList() }) {
                            Text("پاک کردن همه", color = Color(0xFFF44336))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (notifications.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                        Text("پیامی برای نمایش وجود ندارد.", color = colors.secondaryText, fontSize = 14.sp)
                    }
                } else {
                    notifications.forEach { msg ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(40.dp).background(colors.bgMain, CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Notifications, contentDescription = null, tint = colors.accentMain, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = msg, color = colors.primaryText, fontSize = 13.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Right)
                        }
                        HorizontalDivider(color = colors.primaryText.copy(alpha = 0.05f))
                    }
                }
            }
        }
    }
}

@Composable
fun AddAdminStoryDialog(onDismiss: () -> Unit) {
    AddStoryDialog(
        onDismiss = onDismiss,
        onStoryCreated = { newStory ->
            MockStoriesData.stories.add(0, newStory)
        }
    )
}

@Composable
fun StoryViewerDialog(
    story: ShetabStory,
    onClose: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    val colors = LocalShetabColors.current
    val startColor = parseHexColor(story.gradientStart, colors.accentMain)
    val endColor = parseHexColor(story.gradientEnd, colors.accentSecondary)

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        var progress by remember(story.id) { mutableStateOf(0f) }

        // Automatically increment progress to simulate Instagram Story auto-forward
        LaunchedEffect(story.id) {
            progress = 0f
            val totalDurationMs = 5000f // 5 seconds
            val stepMs = 50L
            val stepValue = stepMs / totalDurationMs
            while (progress < 1f) {
                delay(stepMs)
                progress += stepValue
            }
            onNext()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(startColor, endColor)
                    )
                )
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Main content layout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Progress Indicators & Header
                Column {
                    // Instagram-style segmented progress bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .weight(1f)
                                .height(3.dp)
                                .clip(RoundedCornerShape(50)),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.3f),
                        )
                    }

                    // Header Row with close button and info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.Black.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = story.title,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = story.date,
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 10.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color.White.copy(alpha = 0.25f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(story.emoji, fontSize = 18.sp)
                            }
                        }
                    }
                }

                // Middle Content Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 8.dp)
                    ) {
                        // Glowing large emoji
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .background(Color.White.copy(alpha = 0.15f), CircleShape)
                                .border(2.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = story.emoji,
                                fontSize = 56.sp,
                                modifier = Modifier.scale(1.1f)
                            )
                        }

                        // Full display Title
                        Text(
                            text = story.fullTitle,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            lineHeight = 28.sp
                        )

                        // Rich content description
                        Text(
                            text = story.content,
                            color = Color.White.copy(alpha = 0.95f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp,
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        )
                    }

                    // Invisible touch areas for Next/Previous story (left/right tap detection)
                    // Placed inside the middle Box on top of other content, so that they handle clicks perfectly
                    // only within this vertical area without ever blocking the top close or bottom CTA buttons!
                    Row(modifier = Modifier.fillMaxSize()) {
                        // Tapping left half goes to PREVIOUS story
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = onPrevious
                                )
                        )
                        // Tapping right half goes to NEXT story
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = onNext
                                )
                        )
                    }
                }

                // Bottom CTA / Actions
                Button(
                    onClick = onClose,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = startColor
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(
                        text = "متوجه شدم و تایید",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStoryDialog(
    onDismiss: () -> Unit,
    onStoryCreated: (ShetabStory) -> Unit
) {
    val colors = LocalShetabColors.current
    var title by remember { mutableStateOf("") }
    var fullTitle by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf("📣") }
    var selectedColorSchemeIndex by remember { mutableIntStateOf(0) }

    val emojis = listOf("📣", "🚨", "🤖", "🔥", "✨", "📝", "🎁", "🎉", "📚", "⚠️")
    
    // Gradient definitions (start, end)
    val colorSchemes = listOf(
        Pair("#FF416C", "#FF4B2B"), // Red-Orange (Urgent)
        Pair("#2196F3", "#00BCD4"), // Blue-Cyan (Info)
        Pair("#9C27B0", "#E91E63"), // Purple-Pink (Special)
        Pair("#4CAF50", "#009688"), // Green-Teal (Success/Gift)
        Pair("#FF9800", "#F44336")  // Orange-Red (Motivation)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && fullTitle.isNotBlank() && content.isNotBlank()) {
                        val scheme = colorSchemes[selectedColorSchemeIndex]
                        val newStory = ShetabStory(
                            id = "story_${System.currentTimeMillis()}",
                            title = title.take(15),
                            fullTitle = fullTitle,
                            content = content,
                            emoji = selectedEmoji,
                            gradientStart = scheme.first,
                            gradientEnd = scheme.second,
                            date = "هم‌اکنون",
                            isRead = false
                        )
                        onStoryCreated(newStory)
                    }
                },
                enabled = title.isNotBlank() && fullTitle.isNotBlank() && content.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("ارسال استوری به کاربران", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("انصراف", color = colors.secondaryText, fontWeight = FontWeight.Bold)
            }
        },
        title = {
            Text(
                text = "ارسال استوری جدید 🚀",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = colors.primaryText,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Right
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "خبر یا نوتیفیکیشنی که مایلید به صورت استوری برای کاربران شتاب دهم نمایش داده شود را وارد کنید:",
                    color = colors.secondaryText,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Right,
                    lineHeight = 16.sp
                )

                // Title (circle label)
                OutlinedTextField(
                    value = title,
                    onValueChange = { if (it.length <= 12) title = it },
                    label = { Text("عنوان کوتاه روی دایره (حداکثر ۱۲ حرف)") },
                    placeholder = { Text("مثال: هدیه شتاب") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.accentMain,
                        unfocusedBorderColor = colors.primaryText.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Full Title (story heading)
                OutlinedTextField(
                    value = fullTitle,
                    onValueChange = { fullTitle = it },
                    label = { Text("عنوان کامل داخل استوری") },
                    placeholder = { Text("مثال: 🎁 فعال‌سازی آزمون جامع آزمایشی رایگان!") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.accentMain,
                        unfocusedBorderColor = colors.primaryText.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Content (story body)
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("متن و پیام اصلی استوری") },
                    placeholder = { Text("پیام کامل خود را درباره خبر، زمان یا نحوه دسترسی بنویسید...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.accentMain,
                        unfocusedBorderColor = colors.primaryText.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Emoji Picker Row
                Column(horizontalAlignment = Alignment.End) {
                    Text("انتخاب ایموجی / آیکون خبر:", color = colors.primaryText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        reverseLayout = true
                    ) {
                        items(emojis.size) { idx ->
                            val emoji = emojis[idx]
                            val isSelected = emoji == selectedEmoji
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(
                                        color = if (isSelected) colors.accentMain.copy(alpha = 0.15f) else colors.bgMain,
                                        shape = CircleShape
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) colors.accentMain else colors.primaryText.copy(alpha = 0.1f),
                                        shape = CircleShape
                                    )
                                    .clickable { selectedEmoji = emoji },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = emoji, fontSize = 18.sp)
                            }
                        }
                    }
                }

                // Gradient Theme Picker Row
                Column(horizontalAlignment = Alignment.End) {
                    Text("رنگ‌بندی قالب استوری:", color = colors.primaryText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        reverseLayout = true
                    ) {
                        items(colorSchemes.size) { idx ->
                            val scheme = colorSchemes[idx]
                            val isSelected = idx == selectedColorSchemeIndex
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                parseHexColor(scheme.first),
                                                parseHexColor(scheme.second)
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                                    .border(
                                        width = if (isSelected) 3.dp else 0.dp,
                                        color = if (isSelected) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedColorSchemeIndex = idx },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(Color.White, CircleShape)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        containerColor = colors.cardBg,
        shape = RoundedCornerShape(20.dp)
    )
}
