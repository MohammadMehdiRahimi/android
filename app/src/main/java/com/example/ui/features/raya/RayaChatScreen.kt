package com.example.ui.features.raya

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.R
import com.example.ui.core.components.AppBackground
import com.example.ui.theme.LocalShetabColors
import kotlinx.coroutines.launch

data class ChatMessage(val text: String, val isUser: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RayaChatScreen(navController: NavController) {
    val colors = LocalShetabColors.current
    var inputText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        AppBackground()
        
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Surface(
                color = colors.bgTopHeader.copy(alpha = 0.9f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(48.dp)
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colors.primaryText
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.raya),
                            contentDescription = "Raya",
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.White, CircleShape)
                                .padding(2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "هوش مصنوعی رایا",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primaryText
                        )
                    }
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }

            // Chat Content
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Welcome Message Header
                if (messages.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.raya_pan),
                                contentDescription = "Welcome Raya",
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .heightIn(max = 200.dp)
                                    .clip(RoundedCornerShape(24.dp)),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "سلام! من رایا هستم، دستیار هوشمند شما.\nچطور می‌تونم امروز کمکتون کنم؟",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = colors.primaryText,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier
                                    .background(colors.cardBg, RoundedCornerShape(16.dp))
                                    .padding(16.dp)
                            )
                        }
                    }
                }

                items(messages) { message ->
                    ChatBubble(message)
                }
            }

            // Bottom Input
            var showMenu by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp)
                    .background(colors.cardBg, RoundedCornerShape(32.dp))
                    .border(1.dp, colors.primaryText.copy(alpha = 0.1f), RoundedCornerShape(32.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add attachment",
                            tint = colors.secondaryText
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("ارسال عکس", color = colors.primaryText) },
                            onClick = { showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("انتخاب سوال از بانک آزمون", color = colors.primaryText) },
                            onClick = { showMenu = false }
                        )
                    }
                }

                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("پیام خود را بنویسید...", color = colors.secondaryText, fontSize = 14.sp) },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = colors.primaryText,
                        unfocusedTextColor = colors.primaryText
                    )
                )

                if (inputText.isNotBlank()) {
                    IconButton(
                        onClick = {
                            messages.add(ChatMessage(inputText, true))
                            inputText = ""
                            coroutineScope.launch {
                                kotlinx.coroutines.delay(100)
                                listState.animateScrollToItem(messages.size)
                                // Fake response
                                kotlinx.coroutines.delay(1000)
                                messages.add(ChatMessage("این یک قابلیت آزمایشی است.", false))
                                listState.animateScrollToItem(messages.size)
                            }
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(colors.accentMain, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp).padding(start = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val colors = LocalShetabColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.Start else Arrangement.End
    ) {
        if (!message.isUser) {
            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .background(
                        color = colors.cardBg,
                        shape = RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 20.dp,
                            bottomStart = 20.dp,
                            bottomEnd = 4.dp
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = message.text,
                    color = colors.primaryText,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(id = R.drawable.raya),
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(4.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .background(
                        color = colors.accentMain,
                        shape = RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 20.dp,
                            bottomStart = 4.dp,
                            bottomEnd = 20.dp
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = message.text,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}
