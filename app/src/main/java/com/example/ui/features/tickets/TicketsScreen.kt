package com.example.ui.features.tickets

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.TicketEntity
import com.example.ui.core.components.AppBackground
import com.example.ui.core.toPersianNumber
import com.example.ui.theme.LocalShetabColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TicketsViewModel(application: Application) : AndroidViewModel(application) {
    private val ticketDao = AppDatabase.getDatabase(application).ticketDao()
    
    private val _tickets = MutableStateFlow<List<TicketEntity>>(emptyList())
    val tickets: StateFlow<List<TicketEntity>> = _tickets.asStateFlow()

    init {
        // Collect tickets and auto-seed if empty
        viewModelScope.launch {
            ticketDao.getAllTickets().collect { ticketList ->
                if (ticketList.isEmpty()) {
                    seedMockTickets()
                } else {
                    _tickets.value = ticketList
                }
            }
        }
    }

    private suspend fun seedMockTickets() {
        val mock1 = TicketEntity(
            title = "اشکال در رندر فرمول شیمی آزمون جامع شماره ۳",
            category = "آموزشی",
            dateStr = "۱ تیر ۱۴۰۵",
            status = "پاسخ داده شده",
            conversation = "user:سلام وقت بخیر. در سوال شماره ۴ آزمون شیمی، فرمول ساختاری بنزن درست رندر نمیشه و عرض کادر کمه.<split>support:سلام کاربر عزیز شتاب! بابت این موضوع پوزش می‌خواهیم. تیم فنی بلافاصله کدهای مربوط به نمایش فرمول‌ها را اصلاح کرد و اکنون تمامی بخش‌ها با قالب‌بندی صحیح نمایش داده می‌شوند. از صبوری شما سپاسگزاریم."
        )
        val mock2 = TicketEntity(
            title = "درخواست کد تخفیف تمدید دوره طلایی",
            category = "مالی",
            dateStr = "۲۸ خرداد ۱۴۰۵",
            status = "بسته شده",
            conversation = "user:سلام. آیا برای تمدید دوره طلایی تخفیف ویژه‌ای برای کاربران قدیمی دارید؟ من در دوره قبلی با رتبه عالی فارغ‌التحصیل شدم.<split>support:سلام دوست قهرمان و سخت‌کوش شتابی ما! فوق‌العاده است که در حال تلاش مضاعف هستی. یک کد تخفیف ۳۰ درصدی اختصاصی ویژه تمدید در حساب شما فعال شد: SHETAB30. می‌تونی هر زمان که مایل بودی دوره رو تمدید کنی."
        )
        ticketDao.insertTicket(mock1)
        ticketDao.insertTicket(mock2)
    }

    fun submitNewTicket(title: String, category: String, messageText: String) {
        viewModelScope.launch {
            val newTicket = TicketEntity(
                title = title,
                category = category,
                dateStr = "امروز",
                status = "در حال بررسی",
                conversation = "user:$messageText"
            )
            ticketDao.insertTicket(newTicket)
        }
    }

    fun sendReply(ticketId: Int, replyText: String) {
        viewModelScope.launch {
            ticketDao.getTicketById(ticketId).collect { t ->
                if (t != null) {
                    val updatedConversation = "${t.conversation}<split>user:$replyText"
                    val updatedTicket = t.copy(
                        conversation = updatedConversation,
                        status = "در حال بررسی"
                    )
                    ticketDao.updateTicket(updatedTicket)
                    
                    // Trigger a professional support delayed response for high simulation quality!
                    viewModelScope.launch {
                        delay(2000)
                        simulateSupportReply(ticketId, replyText)
                    }
                }
            }
        }
    }

    private suspend fun simulateSupportReply(ticketId: Int, userLastMessage: String) {
        // Fetch current snapshot
        val flow = ticketDao.getTicketById(ticketId)
        // Simple manual single-collect helper
        var currentTicket: TicketEntity? = null
        val job = viewModelScope.launch {
            flow.collect {
                currentTicket = it
            }
        }
        delay(100) // Ensure collect starts
        job.cancel() // Cancel tracking

        currentTicket?.let { t ->
            val simulatedReply = when {
                userLastMessage.contains("سلام") -> "سلام دوست عزیزم! پیام شما مستقیماً به پشتیبانی تخصصی شتاب ارسال شد. در حال بررسی دقیق هستیم و به زودی با پاسخ کامل همراهت هستیم."
                userLastMessage.contains("خرید") || userLastMessage.contains("تخفیف") || userLastMessage.contains("پول") -> "کاربر گرامی شتاب، امور مالی شتاب درخواست مربوطه به خرید یا پرداخت شما را دریافت کرد. فاکتورهای شما تا ساعاتی دیگر به ایمیلتان فرستاده خواهد شد."
                else -> "متشکریم که جزئیات را ارسال کردید. کارشناسان شتاب تا ۲ ساعت آینده بررسی عمیق را به پایان رسانده و در همین بخش به شما پاسخ نهایی را خواهند داد."
            }
            val updatedConv = "${t.conversation}<split>support:$simulatedReply"
            val responseTicket = t.copy(
                conversation = updatedConv,
                status = "پاسخ داده شده"
            )
            ticketDao.updateTicket(responseTicket)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketsScreen(
    onBack: (() -> Unit)? = null,
    viewModel: TicketsViewModel = viewModel()
) {
    val colors = LocalShetabColors.current
    val tickets by viewModel.tickets.collectAsStateWithLifecycle()
    
    var openTicket: TicketEntity? by remember { mutableStateOf(null) }
    var showCreateDialog by remember { mutableStateOf(false) }

    // Sync live updates for opened ticket detail
    LaunchedEffect(tickets, openTicket) {
        openTicket?.let { currentOpen ->
            val updated = tickets.find { it.id == currentOpen.id }
            if (updated != null) {
                openTicket = updated
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AppBackground()
        
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Surface(
                color = colors.bgMain.copy(alpha = 0.95f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (openTicket != null) {
                        IconButton(
                            onClick = { openTicket = null },
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "برگشت",
                                tint = colors.primaryText,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    } else if (onBack != null) {
                        IconButton(
                            onClick = { onBack() },
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "برگشت",
                                tint = colors.primaryText,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Text(
                        text = if (openTicket != null) "گفتگو با پشتیبانی" else "پشتیبانی و تیکت‌ها",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = colors.primaryText,
                        modifier = Modifier.testTag("ticket_screen_title")
                    )
                }
            }

            if (openTicket != null) {
                // Ticket Detail Chat Screen
                TicketChatContent(
                    ticket = openTicket!!,
                    onSendReply = { text ->
                        viewModel.sendReply(openTicket!!.id, text)
                    }
                )
            } else {
                // Ticket list screen
                Box(modifier = Modifier
                    .fillGridOrColumn()
                    .weight(1f)
                    .padding(16.dp)
                ) {
                    if (tickets.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = null,
                                tint = colors.secondaryText.copy(alpha = 0.4f),
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "هیچ تیکتی ثبت نشده است",
                                color = colors.secondaryText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "برای ارسال سوالات، مشکلات یا پیشنهادات خود دکمه زیر را لمس کنید",
                                color = colors.secondaryText.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Text(
                                    text = "مکالمات فعال با شتاب",
                                    color = colors.primaryText,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }
                            
                            items(tickets) { ticket ->
                                TicketListItem(
                                    ticket = ticket,
                                    onClick = { openTicket = ticket }
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(80.dp)) // Avoid list content being cut at bottom
                            }
                        }
                    }

                    // Floating Create Ticket Button
                    Button(
                        onClick = { showCreateDialog = true },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                            .fillMaxWidth(0.9f)
                            .height(50.dp)
                            .testTag("create_ticket_fab"),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                        shape = RoundedCornerShape(25.dp),
                        elevation = ButtonDefaults.buttonElevation(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ایجاد تیکت جدید پشتیبانی",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Animated Dialog to create ticket
        if (showCreateDialog) {
            CreateTicketDialog(
                onDismiss = { showCreateDialog = false },
                onSubmit = { title, cat, msg ->
                    viewModel.submitNewTicket(title, cat, msg)
                    showCreateDialog = false
                }
            )
        }
    }
}

@Composable
fun TicketListItem(
    ticket: TicketEntity,
    onClick: () -> Unit
) {
    val colors = LocalShetabColors.current
    
    val badgeBg = when (ticket.status) {
        "پاسخ داده شده" -> Color(0xFF1B5E20)
        "در حال بررسی" -> Color(0xFFE65100)
        else -> Color(0xFF37474F)
    }
    val badgeText = when (ticket.status) {
        "پاسخ داده شده" -> "پاسخ داده شده"
        "در حال بررسی" -> "در حال بررسی"
        else -> "بسته شده"
    }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ticket_item_${ticket.id}"),
        colors = CardDefaults.cardColors(containerColor = colors.cardBg),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(colors.accentMain.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = ticket.category,
                            color = colors.accentMain,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Text(
                        text = ticket.dateStr.toPersianNumber(),
                        color = colors.secondaryText.copy(alpha = 0.6f),
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = ticket.title,
                    color = colors.primaryText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )

                // Split messages to preview last message
                val msgs = ticket.conversation.split("<split>")
                val lastMsgText = msgs.lastOrNull()?.substringAfter(":") ?: ""
                Text(
                    text = lastMsgText,
                    color = colors.secondaryText,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .background(badgeBg, RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onClick) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = null,
                        tint = colors.secondaryText.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTicketDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String, String) -> Unit
) {
    val colors = LocalShetabColors.current
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("فنی") }
    var firstMessage by remember { mutableStateOf("") }
    
    val categories = listOf("فنی", "مالی", "آموزشی", "پیشنهاد یا انتقاد")

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && firstMessage.isNotBlank()) {
                        onSubmit(title, category, firstMessage)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                enabled = title.isNotBlank() && firstMessage.isNotBlank(),
                modifier = Modifier.testTag("dialog_submit_ticket")
            ) {
                Text("ارسال تیکت", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = colors.secondaryText)
            ) {
                Text("انصراف")
            }
        },
        title = {
            Text(
                text = "ارسال تیکت جدید به شتاب",
                color = colors.primaryText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Right
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Subject TextField
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("موضوع") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.accentMain,
                        unfocusedBorderColor = colors.secondaryText.copy(alpha = 0.3f),
                        focusedLabelColor = colors.accentMain,
                        unfocusedLabelColor = colors.secondaryText
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("ticket_subject_input")
                )

                // Category row selection
                Column {
                    Text(
                        text = "دسته‌بندی موضوعی:",
                        color = colors.primaryText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        categories.forEach { cat ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        color = if (category == cat) colors.accentMain else colors.cardBg,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (category == cat) colors.accentMain else colors.secondaryText.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { category = cat }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cat,
                                    color = if (category == cat) Color.White else colors.secondaryText,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                // Message Text Area
                OutlinedTextField(
                    value = firstMessage,
                    onValueChange = { firstMessage = it },
                    label = { Text("متن درخواست شما...") },
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.accentMain,
                        unfocusedBorderColor = colors.secondaryText.copy(alpha = 0.3f),
                        focusedLabelColor = colors.accentMain,
                        unfocusedLabelColor = colors.secondaryText
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("ticket_message_input")
                )
            }
        },
        containerColor = colors.bgMain,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun TicketChatContent(
    ticket: TicketEntity,
    onSendReply: (String) -> Unit
) {
    val colors = LocalShetabColors.current
    var replyText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val messages = remember(ticket.conversation) {
        ticket.conversation.split("<split>").map { msgString ->
            val sender = msgString.substringBefore(":")
            val text = msgString.substringAfter(":")
            sender to text
        }
    }

    // Auto scroll to bottom when message arrives
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Conversation overview header card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            colors = CardDefaults.cardColors(containerColor = colors.cardBg),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(colors.accentMain.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = colors.accentMain, modifier = Modifier.size(18.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = ticket.title, color = colors.primaryText, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text(text = "دسته‌بندی: ${ticket.category} | وضعیت: ${ticket.status}", color = colors.secondaryText, fontSize = 10.sp)
                }
            }
        }

        // Messages Box
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { (sender, text) ->
                val isUser = sender == "user"
                val alignment = if (isUser) Alignment.End else Alignment.Start
                val bubbleBg = if (isUser) colors.accentMain else colors.cardBg
                val itemTextColor = if (isUser) Color.White else colors.primaryText
                val roundedShape = if (isUser) {
                    RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp)
                } else {
                    RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp)
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = alignment
                ) {
                    Text(
                        text = if (isUser) "شما" else "پشتیبان شتاب",
                        color = colors.secondaryText.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(bottom = 2.dp, start = 8.dp, end = 8.dp)
                    )
                    Box(
                        modifier = Modifier
                            .background(bubbleBg, roundedShape)
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .widthIn(max = 280.dp)
                    ) {
                        Text(
                            text = text,
                            color = itemTextColor,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            textAlign = TextAlign.Right
                        )
                    }
                }
            }
        }

        // Send Input Area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .background(colors.cardBg, RoundedCornerShape(26.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = replyText,
                onValueChange = { replyText = it },
                placeholder = { Text("پاسخ خود را بنویسید...", color = colors.secondaryText.copy(alpha = 0.5f), fontSize = 13.sp) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = colors.primaryText,
                    unfocusedTextColor = colors.primaryText
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("ticket_reply_input")
            )

            IconButton(
                onClick = {
                    if (replyText.isNotBlank()) {
                        onSendReply(replyText)
                        replyText = ""
                    }
                },
                modifier = Modifier
                    .size(40.dp)
                    .background(colors.accentMain, CircleShape)
                    .testTag("ticket_reply_send")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "ارسال با شتاب",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// Helper to make view fluidly responsive on tablet sizes
@Composable
fun Modifier.fillGridOrColumn(): Modifier {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    return if (configuration.screenWidthDp > 600) {
        this.fillMaxWidth(0.85f).wrapContentWidth(Alignment.CenterHorizontally)
    } else {
        this.fillMaxSize()
    }
}
