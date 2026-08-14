package com.example.ui.features.exams

import com.example.ui.theme.IranSansFontFamily
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.draw.clip
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.core.components.LatexSkeletonType
import com.example.ui.core.components.LatexText
import com.example.ui.core.toPersianNumber
import com.example.ui.theme.LocalShetabColors

@Composable
fun ExamQuestionCard(
    question: com.example.data.ExamQuestion,
    questionIndex: Int,
    selectedOption: Int?,
    onOptionSelected: (Int) -> Unit,
    isDescriptive: Boolean = false,
    typedAnswer: String = "",
    onTypedAnswerChange: (String) -> Unit = {},
    onUploadImage: () -> Unit = {},
    imageUploaded: Boolean = false,
    isViewingAnswerSheet: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = LocalShetabColors.current
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            var showReportDialog by remember { mutableStateOf(false) }
            var showRayaChatDialog by remember { mutableStateOf(false) }
            var showFlashcardDialog by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                LatexText(
                    latexString = "<b>سوال ${(questionIndex + 1).toString().toPersianNumber()}.</b> <b>${question.question}</b>",
                    textColor = colors.primaryText,
                    modifier = Modifier.weight(1f),
                    skeletonType = LatexSkeletonType.QUESTION,
                    isBold = true
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (isViewingAnswerSheet) {
                        // Raya Chat Icon
                        IconButton(
                            onClick = { showRayaChatDialog = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Image(
                                painter = painterResource(id = com.example.R.drawable.raya),
                                contentDescription = "گفتگو با رایا",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        
                        // Flashcard Icon
                        IconButton(
                            onClick = { showFlashcardDialog = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = "ایجاد فلش کارت",
                                tint = colors.accentSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = { showReportDialog = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "گزارش خطا",
                            tint = Color(0xFFEF5350),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            if (showReportDialog) {
                QuestionReportDialog(
                    onDismissRequest = { showReportDialog = false },
                    onSubmitReport = { reason ->
                        // Report submitted
                    }
                )
            }

            if (showRayaChatDialog) {
                RayaQuestionChatDialog(
                    questionText = question.question,
                    explanationText = question.explanationObj?.stepByStepSolution ?: question.explanation,
                    onDismissRequest = { showRayaChatDialog = false },
                    colors = colors
                )
            }

            if (showFlashcardDialog) {
                CreateQuestionFlashcardDialog(
                    questionText = question.flashCard?.front ?: question.question,
                    explanationText = question.flashCard?.back ?: (question.explanationObj?.stepByStepSolution ?: question.explanation),
                    onDismissRequest = { showFlashcardDialog = false },
                    colors = colors
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            if (isDescriptive) {
                OutlinedTextField(
                    value = typedAnswer,
                    onValueChange = { if (!isViewingAnswerSheet) onTypedAnswerChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    enabled = !isViewingAnswerSheet,
                    placeholder = { Text("پاسخ تشریحی خود را در این قسمت بنویسید...", color = colors.secondaryText.copy(alpha = 0.6f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colors.primaryText.copy(alpha = 0.02f),
                        unfocusedContainerColor = colors.primaryText.copy(alpha = 0.02f),
                        disabledContainerColor = colors.primaryText.copy(alpha = 0.05f),
                        focusedBorderColor = colors.accentMain,
                        unfocusedBorderColor = colors.primaryText.copy(alpha = 0.1f),
                    ),
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Right),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onUploadImage,
                        enabled = !isViewingAnswerSheet,
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.accentSecondary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("آپلود تصویر راه حل", fontWeight = FontWeight.Bold, fontFamily = IranSansFontFamily)
                    }
                    if (imageUploaded) {
                        Text(
                            text = "تصویر آپلود شد ✓",
                            color = Color(0xFF2E7D32),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = IranSansFontFamily
                        )
                    }
                }
            } else {
                if (question.images != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(3f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            question.options.forEachIndexed { optIndex, optionStr ->
                                val isSelected = selectedOption == optIndex
                                val isCorrectOption = question.answer == optIndex + 1
                                
                                val bgColor = when {
                                    !isViewingAnswerSheet -> {
                                        if (isSelected) colors.accentMain.copy(alpha = 0.15f) else colors.cardBg
                                    }
                                    isCorrectOption -> Color(0xFFE8F5E9)
                                    isSelected -> Color(0xFFFFEBEE)
                                    else -> colors.cardBg
                                }
                                
                                val borderColor = when {
                                    !isViewingAnswerSheet -> {
                                        if (isSelected) colors.accentMain else colors.primaryText.copy(alpha = 0.1f)
                                    }
                                    isCorrectOption -> Color(0xFF2E7D32)
                                    isSelected -> Color(0xFFC62828)
                                    else -> colors.primaryText.copy(alpha = 0.08f)
                                }
                                
                                val contentTextColor = when {
                                    !isViewingAnswerSheet -> {
                                        if (isSelected) colors.accentMain else colors.primaryText
                                    }
                                    isCorrectOption -> Color(0xFF2E7D32)
                                    isSelected -> Color(0xFFC62828)
                                    else -> colors.primaryText
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(bgColor, RoundedCornerShape(12.dp))
                                        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                        .clickable(
                                            enabled = !isViewingAnswerSheet,
                                            indication = null,
                                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                                        ) { onOptionSelected(optIndex) }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .background(
                                                color = when {
                                                    !isViewingAnswerSheet -> {
                                                        if (isSelected) colors.accentMain else Color.Transparent
                                                    }
                                                    isCorrectOption -> Color(0xFF2E7D32)
                                                    isSelected -> Color(0xFFC62828)
                                                    else -> Color.Transparent
                                                },
                                                shape = CircleShape
                                            )
                                            .border(
                                                width = 2.dp,
                                                color = when {
                                                    !isViewingAnswerSheet -> {
                                                        if (isSelected) colors.accentMain else colors.secondaryText
                                                    }
                                                    isCorrectOption -> Color(0xFF2E7D32)
                                                    isSelected -> Color(0xFFC62828)
                                                    else -> colors.secondaryText.copy(alpha = 0.4f)
                                                },
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        when {
                                            isViewingAnswerSheet && isCorrectOption -> {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Correct",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            isViewingAnswerSheet && isSelected -> {
                                                Text(
                                                    text = "✗",
                                                    color = Color.White,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            isSelected -> {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            else -> {
                                                Text(
                                                    text = (optIndex + 1).toString().toPersianNumber(),
                                                    color = colors.secondaryText,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.width(12.dp))
                                    
                                    LatexText(
                                        latexString = optionStr,
                                        textColor = contentTextColor,
                                        modifier = Modifier.weight(1f),
                                        skeletonType = LatexSkeletonType.OPTION
                                    )
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(2f)
                                .height(220.dp)
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            SvgWebView(
                                assetPath = question.images?.q ?: "vision.svg",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                } else {
                    question.options.forEachIndexed { optIndex, optionStr ->
                        val isSelected = selectedOption == optIndex
                        val isCorrectOption = question.answer == optIndex + 1
                        
                        val bgColor = when {
                            !isViewingAnswerSheet -> {
                                if (isSelected) colors.accentMain.copy(alpha = 0.15f) else colors.cardBg
                            }
                            isCorrectOption -> Color(0xFFE8F5E9)
                            isSelected -> Color(0xFFFFEBEE)
                            else -> colors.cardBg
                        }
                        
                        val borderColor = when {
                            !isViewingAnswerSheet -> {
                                if (isSelected) colors.accentMain else colors.primaryText.copy(alpha = 0.1f)
                            }
                            isCorrectOption -> Color(0xFF2E7D32)
                            isSelected -> Color(0xFFC62828)
                            else -> colors.primaryText.copy(alpha = 0.08f)
                        }
                        
                        val contentTextColor = when {
                            !isViewingAnswerSheet -> {
                                if (isSelected) colors.accentMain else colors.primaryText
                            }
                            isCorrectOption -> Color(0xFF2E7D32)
                            isSelected -> Color(0xFFC62828)
                            else -> colors.primaryText
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .background(bgColor, RoundedCornerShape(12.dp))
                                .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                .clickable(
                                    enabled = !isViewingAnswerSheet,
                                    indication = null,
                                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                                ) { onOptionSelected(optIndex) }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        color = when {
                                            !isViewingAnswerSheet -> {
                                                if (isSelected) colors.accentMain else Color.Transparent
                                            }
                                            isCorrectOption -> Color(0xFF2E7D32)
                                            isSelected -> Color(0xFFC62828)
                                            else -> Color.Transparent
                                        },
                                        shape = CircleShape
                                    )
                                    .border(
                                        width = 2.dp,
                                        color = when {
                                            !isViewingAnswerSheet -> {
                                                if (isSelected) colors.accentMain else colors.secondaryText
                                            }
                                            isCorrectOption -> Color(0xFF2E7D32)
                                            isSelected -> Color(0xFFC62828)
                                            else -> colors.secondaryText.copy(alpha = 0.4f)
                                        },
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                when {
                                    isViewingAnswerSheet && isCorrectOption -> {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Correct",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    isViewingAnswerSheet && isSelected -> {
                                        Text(
                                            text = "✗",
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    isSelected -> {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    else -> {
                                        Text(
                                            text = (optIndex + 1).toString().toPersianNumber(),
                                            color = colors.secondaryText,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            LatexText(
                                latexString = optionStr,
                                textColor = contentTextColor,
                                modifier = Modifier.weight(1f),
                                skeletonType = LatexSkeletonType.OPTION
                            )
                        }
                    }
                }
            }

            // Answer Sheet details goes here (can be placed below)
            if (isViewingAnswerSheet) {
                AnswerExplanationSection(
                    question = question,
                    colors = colors
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RayaQuestionChatDialog(
    questionText: String,
    explanationText: String,
    onDismissRequest: () -> Unit,
    colors: com.example.ui.theme.ShetabColorPalette
) {
    var inputText by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            RayaDialogMessage(
                text = "سلام دوست خوبم! من تحلیل تشریحی این سوال رو بررسی کردم. چجوری می‌تونم توی فهم بهترش کمکت کنم؟ می‌تونی ازم بخوای بیشتر توضیحش بدم یا بخش خاصی رو باز کنم.",
                isUser = false
            )
        )
    }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var isSending by remember { mutableStateOf(false) }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismissRequest,
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = colors.cardBg
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header (Thin & Elegant)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.accentMain)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Image(
                            painter = painterResource(id = com.example.R.drawable.raya),
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .padding(2.dp)
                        )
                        Text(
                            text = "رفع اشکال هوشمند با رایا",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            fontFamily = IranSansFontFamily
                        )
                    }
                    
                    IconButton(onClick = onDismissRequest, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بستن",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Chat History
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(messages.size) { index ->
                        val msg = messages[index]
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (msg.isUser) Arrangement.Start else Arrangement.End
                        ) {
                            if (!msg.isUser) {
                                Box(
                                    modifier = Modifier
                                        .widthIn(max = 280.dp)
                                        .background(
                                            color = colors.bgMain.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(
                                                topStart = 16.dp,
                                                topEnd = 16.dp,
                                                bottomStart = 16.dp,
                                                bottomEnd = 4.dp
                                            )
                                        )
                                        .border(1.dp, colors.primaryText.copy(alpha = 0.05f), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp))
                                        .padding(12.dp)
                                ) {
                                    LatexText(
                                        latexString = msg.text,
                                        textColor = colors.primaryText,
                                        skeletonType = LatexSkeletonType.EXPLANATION,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .widthIn(max = 280.dp)
                                        .background(
                                            color = colors.accentMain,
                                            shape = RoundedCornerShape(
                                                topStart = 16.dp,
                                                topEnd = 16.dp,
                                                bottomStart = 4.dp,
                                                bottomEnd = 16.dp
                                            )
                                        )
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = msg.text,
                                        color = Color.White,
                                        fontSize = 12.5.sp,
                                        fontFamily = IranSansFontFamily
                                    )
                                }
                            }
                        }
                    }

                    if (isSending) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "رایا در حال پاسخ دادن... ✍️",
                                    color = colors.secondaryText,
                                    fontSize = 11.sp,
                                    fontFamily = IranSansFontFamily,
                                    modifier = Modifier.padding(8.dp)
                                )
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = colors.accentMain
                                )
                            }
                        }
                    }
                }

                // Quick suggestions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val suggestions = listOf("این سوال رو بیشتر برام باز کن", "نکته اصلی حل این سوال چی بود؟", "یک سوال مشابه بهم بده")
                    suggestions.forEach { text ->
                        Box(
                            modifier = Modifier
                                .background(colors.accentSecondary.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                .clickable(enabled = !isSending) {
                                    inputText = text
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = text,
                                color = colors.accentSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = IranSansFontFamily
                            )
                        }
                    }
                }

                // Input (Compact)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .background(colors.bgMain.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .border(1.dp, colors.primaryText.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.foundation.text.BasicTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        enabled = !isSending,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = colors.primaryText,
                            fontSize = 12.sp,
                            fontFamily = IranSansFontFamily
                        ),
                        cursorBrush = androidx.compose.ui.graphics.SolidColor(colors.accentMain),
                        decorationBox = { innerTextField ->
                            Box(contentAlignment = Alignment.CenterStart) {
                                if (inputText.isEmpty()) {
                                    Text(
                                        text = "پیام خود را بنویسید...",
                                        color = colors.secondaryText,
                                        fontSize = 11.5.sp,
                                        fontFamily = IranSansFontFamily
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )

                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank() && !isSending) {
                                val userText = inputText
                                messages.add(RayaDialogMessage(userText, true))
                                inputText = ""
                                isSending = true
                                
                                coroutineScope.launch {
                                    kotlinx.coroutines.delay(100)
                                    listState.animateScrollToItem(messages.size)
                                    
                                    // Fetch actual Gemini AI response!
                                    val aiResponse = com.example.data.GeminiService.getRayaResponse(
                                        userPrompt = userText,
                                        questionText = questionText,
                                        answerExplanation = explanationText
                                    )
                                    
                                    messages.add(RayaDialogMessage(aiResponse, false))
                                    isSending = false
                                    kotlinx.coroutines.delay(100)
                                    listState.animateScrollToItem(messages.size)
                                }
                            }
                        },
                        enabled = inputText.isNotBlank() && !isSending,
                        modifier = Modifier
                            .size(30.dp)
                            .background(if (inputText.isNotBlank() && !isSending) colors.accentMain else colors.secondaryText.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "ارسال",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

data class RayaDialogMessage(val text: String, val isUser: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateQuestionFlashcardDialog(
    questionText: String,
    explanationText: String,
    onDismissRequest: () -> Unit,
    colors: com.example.ui.theme.ShetabColorPalette
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    val category = "تحلیل آزمون"
    var frontText by remember { mutableStateOf(questionText) }
    var backText by remember { mutableStateOf(explanationText) }
    var isSaving by remember { mutableStateOf(false) }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismissRequest,
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = colors.cardBg
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header (Thin & Elegant, exactly like Raya)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.accentSecondary)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "ایجاد فلش کارت جدید",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            fontFamily = IranSansFontFamily
                        )
                    }
                    
                    IconButton(onClick = onDismissRequest, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بستن",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Content Area
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Front of card (Question)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "روی کارت (سوال)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.secondaryText,
                            fontFamily = IranSansFontFamily
                        )
                        OutlinedTextField(
                            value = frontText,
                            onValueChange = { frontText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 100.dp, max = 160.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colors.accentSecondary,
                                unfocusedBorderColor = colors.primaryText.copy(alpha = 0.12f),
                                focusedTextColor = colors.primaryText,
                                unfocusedTextColor = colors.primaryText,
                                focusedContainerColor = colors.bgMain.copy(alpha = 0.2f),
                                unfocusedContainerColor = colors.bgMain.copy(alpha = 0.2f)
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontFamily = IranSansFontFamily,
                                fontSize = 12.5.sp,
                                color = colors.primaryText
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Back of card (Explanation)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "پشت کارت (پاسخ تشریحی)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.secondaryText,
                            fontFamily = IranSansFontFamily
                        )
                        OutlinedTextField(
                            value = backText,
                            onValueChange = { backText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 150.dp, max = 250.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colors.accentSecondary,
                                unfocusedBorderColor = colors.primaryText.copy(alpha = 0.12f),
                                focusedTextColor = colors.primaryText,
                                unfocusedTextColor = colors.primaryText,
                                focusedContainerColor = colors.bgMain.copy(alpha = 0.2f),
                                unfocusedContainerColor = colors.bgMain.copy(alpha = 0.2f)
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontFamily = IranSansFontFamily,
                                fontSize = 12.5.sp,
                                color = colors.primaryText
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                // Action (Save Button - Compact & Styled like Raya's style/layout)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Button(
                        onClick = {
                            if (frontText.isNotBlank() && backText.isNotBlank() && !isSaving) {
                                isSaving = true
                                coroutineScope.launch {
                                    val newCard = com.example.data.local.entity.FlashcardEntity(
                                        question = frontText,
                                        optionsJson = "",
                                        answer = -1,
                                        explanation = backText,
                                        category = category,
                                        boxNumber = 1,
                                        nextReviewDate = System.currentTimeMillis()
                                    )
                                    com.example.data.local.DatabaseBuilder.getInstance(context).flashcardDao().insertFlashcard(newCard)
                                    android.widget.Toast.makeText(context, "فلش کارت با موفقیت به مجموعه اضافه شد! 🎉", android.widget.Toast.LENGTH_SHORT).show()
                                    isSaving = false
                                    onDismissRequest()
                                }
                            }
                        },
                        enabled = frontText.isNotBlank() && backText.isNotBlank() && !isSaving,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.accentSecondary,
                            disabledContainerColor = colors.secondaryText.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text(
                                text = "ذخیره فلش کارت جدید",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White,
                                fontFamily = IranSansFontFamily
                            )
                        }
                    }
                }
            }
        }
    }
}
