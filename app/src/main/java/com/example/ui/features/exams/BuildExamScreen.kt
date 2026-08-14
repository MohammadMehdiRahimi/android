package com.example.ui.features.exams

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.navigation.NavController
import com.example.ui.core.components.AppBackground
import com.example.ui.theme.LocalShetabColors
import com.example.ui.theme.IranSansFontFamily
import com.example.ui.core.toPersianNumber
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.LocalIndication
import kotlinx.coroutines.delay

data class ExamWrapper(
    val id: Int,
    var grade: String = "دهم",
    var field: String = "علوم انسانی",
    var book: String = "زبان و ادبیات فارسی",
    var chapters: List<String> = emptyList(),
    var topics: List<String> = emptyList(),
    var questionType: String = "تستی",
    var examSource: String = "همه",
    var easyCount: Int = 0,
    var mediumCount: Int = 0,
    var hardCount: Int = 0,
    var veryHardCount: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildExamScreen(navController: NavController) {
    val colors = LocalShetabColors.current
    var wrappers by remember { mutableStateOf(listOf(ExamWrapper(id = 0))) }
    var nextWrapperId by remember { mutableIntStateOf(1) }
    var currentStep by remember { mutableIntStateOf(1) }
    var globalExamType by remember { mutableStateOf(com.example.data.MockExamData.globalExamType) }
    
    // Exam Settings
    var examQuestionCount by remember { mutableStateOf("20") }
    var examName by remember { mutableStateOf("") }
    var examTimeMinutes by remember { mutableStateOf("45") }
    var hasNegativeScore by remember { mutableStateOf(false) }
    var hasTimeLimit by remember { mutableStateOf(false) }
    
    var repetitionStatus by remember { mutableStateOf(listOf("همه سوالات")) }
    
    val totalEasyAvailable = 20
    val totalMediumAvailable = 20
    val totalHardAvailable = 20
    val totalVeryHardAvailable = 20

    Box(modifier = Modifier.fillMaxSize()) {
        AppBackground()
        
        Column(modifier = Modifier.fillMaxSize()) {
            // Modern Elegant Colorless / Transparent Top Bar (Cohesive with other screens)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Transparent,
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(72.dp)
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(colors.cardBg)
                            .border(1.dp, colors.primaryText.copy(alpha = 0.08f), CircleShape)
                            .clickable { 
                                if (currentStep == 2) {
                                    currentStep = 1
                                } else {
                                    navController.popBackStack() 
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "بازگشت",
                            tint = colors.primaryText,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = when (currentStep) {
                            1 -> "ساخت آزمون"
                            else -> "تنظیمات آزمون"
                        },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primaryText,
                        letterSpacing = (-0.5).sp,
                        fontFamily = IranSansFontFamily
                    )

                    // Symmetric placeholder to keep title perfectly centered
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.Transparent)
                    )
                }
            }

            if (currentStep == 1) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 140.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = colors.cardBg),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                                    Text(
                                        text = "نوع آزمون طراحی‌شده",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.primaryText,
                                        fontFamily = IranSansFontFamily
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(colors.bgMain, RoundedCornerShape(8.dp))
                                            .padding(3.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        val examTypes = listOf("تستی", "تشریحی")
                                        examTypes.forEach { type ->
                                            val isSelected = type == globalExamType
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(if (isSelected) colors.accentMain else Color.Transparent)
                                                    .clickable { 
                                                        globalExamType = type 
                                                        com.example.data.MockExamData.globalExamType = type
                                                        // Sync existing sections' question types
                                                        wrappers = wrappers.map { it.copy(questionType = type) }
                                                    }
                                                    .padding(vertical = 8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = type,
                                                    color = if (isSelected) Color.White else colors.secondaryText,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    fontFamily = IranSansFontFamily
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        itemsIndexed(wrappers) { index, wrapper ->
                            WrapperCard(
                                wrapper = wrapper,
                                index = index,
                                canDelete = wrappers.size > 1,
                                onDelete = {
                                    wrappers = wrappers.filter { it.id != wrapper.id }
                                },
                                onUpdate = { updated ->
                                    wrappers = wrappers.map { if (it.id == updated.id) updated else it }
                                }
                            )
                        }
                        
                        item {
                            Button(
                                onClick = { 
                                    wrappers = wrappers + ExamWrapper(id = nextWrapperId++)
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.cardIconBg,
                                    contentColor = colors.accentMain
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add Wrapper")
                                Spacer(Modifier.width(8.dp))
                                Text("افزودن بخش جدید", fontWeight = FontWeight.Bold, fontFamily = IranSansFontFamily)
                            }
                        }
                    }

                    // Fixed Bottom Bar to prevent button from sliding under system controls
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, colors.bgMain.copy(alpha = 0.95f), colors.bgMain)
                                )
                            )
                            .navigationBarsPadding()
                            .padding(16.dp)
                    ) {
                        Button(
                            onClick = { currentStep = 2 },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("مرحله بعدی", fontWeight = FontWeight.Bold, fontSize = 15.sp, fontFamily = IranSansFontFamily)
                        }
                    }
                }
            } else if (currentStep == 2) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = colors.cardBg),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    MultiSelectDropdown(
                                        modifier = Modifier.weight(1f),
                                        label = "سوالات مجاز",
                                        selected = repetitionStatus,
                                        options = listOf("همه سوالات", "سوالاتی که قبلا اشتباه زدم", "سوالاتی که بقیه بیشترین اشتباه رو داشتن", "سوالاتی که تا حالا پاسخ ندادم")
                                    ) { newSelection ->
                                        val addedItem = newSelection.find { it !in repetitionStatus }
                                        if (addedItem == "همه سوالات") {
                                            repetitionStatus = listOf("همه سوالات")
                                        } else {
                                            val filtered = newSelection.filter { it != "همه سوالات" }
                                            repetitionStatus = filtered.ifEmpty { listOf("همه سوالات") }
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("نمره منفی", fontWeight = FontWeight.Bold, color = colors.primaryText, fontSize = 12.sp, fontFamily = IranSansFontFamily)
                                        CustomSwitch(
                                            checked = hasNegativeScore,
                                            onCheckedChange = { hasNegativeScore = it }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(color = colors.bgMain)
                                Spacer(modifier = Modifier.height(16.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    wrappers.forEachIndexed { index, wrapper ->
                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Text(
                                                text = "${wrapper.book} : بخش ${(index + 1).toString().toPersianNumber()}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = colors.accentMain,
                                                fontFamily = IranSansFontFamily
                                            )
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                                LevelCounter(modifier = Modifier.weight(1f), label = "آسان", count = wrapper.easyCount, total = totalEasyAvailable, onCountChange = { newCount -> 
                                                    wrappers = wrappers.map { w -> if (w.id == wrapper.id) w.copy(easyCount = newCount) else w }
                                                })
                                                LevelCounter(modifier = Modifier.weight(1f), label = "متوسط", count = wrapper.mediumCount, total = totalMediumAvailable, onCountChange = { newCount -> 
                                                    wrappers = wrappers.map { w -> if (w.id == wrapper.id) w.copy(mediumCount = newCount) else w }
                                                })
                                            }
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                                LevelCounter(modifier = Modifier.weight(1f), label = "دشوار", count = wrapper.hardCount, total = totalHardAvailable, onCountChange = { newCount -> 
                                                    wrappers = wrappers.map { w -> if (w.id == wrapper.id) w.copy(hardCount = newCount) else w }
                                                })
                                                LevelCounter(modifier = Modifier.weight(1f), label = "خیلی دشوار", count = wrapper.veryHardCount, total = totalVeryHardAvailable, onCountChange = { newCount -> 
                                                    wrappers = wrappers.map { w -> if (w.id == wrapper.id) w.copy(veryHardCount = newCount) else w }
                                                })
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = colors.cardBg),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("نام آزمون", fontWeight = FontWeight.Bold, color = colors.primaryText, fontFamily = IranSansFontFamily)
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = examName,
                                    onValueChange = { examName = it },
                                    modifier = Modifier.fillMaxWidth().height(52.dp),
                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, fontFamily = IranSansFontFamily),
                                    placeholder = { Text("مثلاً: آزمون جامع ادبیات", color = colors.secondaryText, fontSize = 12.sp, fontFamily = IranSansFontFamily) },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = colors.accentMain,
                                        unfocusedBorderColor = colors.primaryText.copy(alpha = 0.1f),
                                        focusedContainerColor = colors.bgMain,
                                        unfocusedContainerColor = colors.bgMain,
                                        focusedTextColor = colors.primaryText,
                                        unfocusedTextColor = colors.primaryText
                                    )
                                )
                                
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("آزمون زماندار", fontWeight = FontWeight.Bold, color = colors.primaryText, fontFamily = IranSansFontFamily)
                                    CustomSwitch(
                                        checked = hasTimeLimit,
                                        onCheckedChange = { hasTimeLimit = it }
                                    )
                                }
                                
                                if (hasTimeLimit) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text("زمان آزمون (دقیقه)", fontWeight = FontWeight.Bold, color = colors.primaryText, fontFamily = IranSansFontFamily)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = examTimeMinutes,
                                        onValueChange = { examTimeMinutes = it },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth().height(52.dp),
                                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, fontFamily = IranSansFontFamily),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = colors.accentMain,
                                            unfocusedBorderColor = colors.primaryText.copy(alpha = 0.1f),
                                            focusedContainerColor = colors.bgMain,
                                            unfocusedContainerColor = colors.bgMain,
                                            focusedTextColor = colors.primaryText,
                                            unfocusedTextColor = colors.primaryText
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Fixed Bottom Bar to prevent button from sliding under system controls
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, colors.bgMain.copy(alpha = 0.95f), colors.bgMain)
                                )
                            )
                            .navigationBarsPadding()
                            .padding(16.dp)
                    ) {
                        Button(
                            onClick = { navController.navigate("exam_taking") },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("شروع آزمون", fontWeight = FontWeight.Bold, fontSize = 15.sp, fontFamily = IranSansFontFamily, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WrapperCard(
    wrapper: ExamWrapper,
    index: Int,
    canDelete: Boolean,
    onDelete: () -> Unit,
    onUpdate: (ExamWrapper) -> Unit
) {
    val colors = LocalShetabColors.current
    
    val grades = listOf("دهم", "یازدهم", "دوازدهم")
    val fields = listOf("علوم انسانی", "ریاضی و فیزیک", "علوم تجربی")
    val books = listOf("زبان و ادبیات فارسی", "دین و زندگی", "عربی")
    val questionTypes = listOf("تستی", "تشریحی")
    val examSources = listOf("همه", "کنکور", "تالیفی")
    
    val allChapters = listOf("درس یکم", "درس دوم", "درس سوم", "درس چهارم")
    val allTopics = listOf("قرابت معنایی", "لغت و املا", "آرایه های ادبی", "دستور زبان")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "بخش ${index + 1}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = colors.accentMain,
                    fontFamily = IranSansFontFamily
                )
                if (canDelete) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFF44336),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            // Single Selects
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SingleSelectDropdown(modifier = Modifier.weight(1f), label = "پایه", selected = wrapper.grade, options = grades) { 
                    onUpdate(wrapper.copy(grade = it)) 
                }
                SingleSelectDropdown(modifier = Modifier.weight(1f), label = "رشته", selected = wrapper.field, options = fields) { 
                    onUpdate(wrapper.copy(field = it)) 
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            SingleSelectDropdown(label = "کتاب", selected = wrapper.book, options = books) { 
                onUpdate(wrapper.copy(book = it)) 
            }
            Spacer(modifier = Modifier.height(6.dp))
            
            // Multi Selects
            MultiSelectDropdown(label = "فصل (ها)", selected = wrapper.chapters, options = allChapters) { 
                onUpdate(wrapper.copy(chapters = it)) 
            }
            Spacer(modifier = Modifier.height(6.dp))
            MultiSelectDropdown(label = "موضوع (ها)", selected = wrapper.topics, options = allTopics) { 
                onUpdate(wrapper.copy(topics = it)) 
            }
            Spacer(modifier = Modifier.height(6.dp))
            
            SingleSelectDropdown(label = "منبع سوالات", selected = wrapper.examSource, options = examSources) { 
                onUpdate(wrapper.copy(examSource = it)) 
            }
        }
    }
}

@Composable
fun SingleSelectDropdown(
    modifier: Modifier = Modifier,
    label: String,
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val colors = LocalShetabColors.current
    
    Column(modifier = modifier) {
        Text(text = label, fontSize = 11.sp, color = colors.secondaryText, fontFamily = IranSansFontFamily, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.bgMain, RoundedCornerShape(6.dp))
                .clickable { expanded = true }
                .padding(vertical = 8.dp, horizontal = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(selected, fontSize = 13.sp, color = colors.primaryText, fontFamily = IranSansFontFamily)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = colors.secondaryText, modifier = Modifier.size(18.dp))
            }
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(colors.cardBg)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = colors.primaryText, fontFamily = IranSansFontFamily, fontSize = 13.sp) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun MultiSelectDropdown(
    modifier: Modifier = Modifier,
    label: String,
    selected: List<String>,
    options: List<String>,
    onSelect: (List<String>) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val colors = LocalShetabColors.current
    
    val displayString = if (selected.isEmpty()) "انتخاب کنید" else selected.joinToString(", ")
    
    Column(modifier = modifier) {
        if (label.isNotEmpty()) {
            Text(text = label, fontSize = 11.sp, color = colors.primaryText, fontWeight = FontWeight.Bold, fontFamily = IranSansFontFamily)
            Spacer(modifier = Modifier.height(3.dp))
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.bgMain, RoundedCornerShape(6.dp))
                .clickable { showDialog = true }
                .padding(vertical = 8.dp, horizontal = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = displayString, 
                    fontSize = 13.sp, 
                    color = if(selected.isEmpty()) colors.secondaryText else colors.primaryText,
                    maxLines = 1,
                    fontFamily = IranSansFontFamily
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = colors.secondaryText, modifier = Modifier.size(18.dp))
            }
        }
        
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("انتخاب $label", color = colors.primaryText, fontWeight = FontWeight.Bold, fontFamily = IranSansFontFamily, fontSize = 15.sp) },
                text = {
                    LazyColumn {
                        itemsIndexed(options) { _, option ->
                            val isSelected = selected.contains(option)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val newSelected = if (isSelected) selected - option else selected + option
                                        onSelect(newSelected)
                                    }
                                    .padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = null,
                                    colors = CheckboxDefaults.colors(checkedColor = colors.accentMain)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = option, color = colors.primaryText, fontFamily = IranSansFontFamily, fontSize = 13.sp)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("تایید", color = colors.accentMain, fontWeight = FontWeight.Bold, fontFamily = IranSansFontFamily, fontSize = 14.sp)
                    }
                },
                containerColor = colors.cardBg
            )
        }
    }
}

@Composable
fun RepeatingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val currentClickListener by rememberUpdatedState(onClick)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    LaunchedEffect(isPressed) {
        if (isPressed) {
            currentClickListener()
            delay(400)
            while (isPressed) {
                currentClickListener()
                delay(70)
            }
        }
    }
    
    Box(
        modifier = modifier.clickable(
            interactionSource = interactionSource,
            indication = LocalIndication.current,
            onClick = {}
        ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun CustomSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = LocalShetabColors.current
    val trackColor by animateColorAsState(targetValue = if (checked) colors.accentMain else colors.primaryText.copy(alpha = 0.2f))
    val thumbOffset by animateDpAsState(targetValue = if (checked) 18.dp else 2.dp)

    Box(
        modifier = Modifier
            .width(40.dp)
            .height(22.dp)
            .background(trackColor, RoundedCornerShape(11.dp))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onCheckedChange(!checked) },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(18.dp)
                .background(Color.White, CircleShape)
        )
    }
}

@Composable
fun LevelCounter(
    modifier: Modifier = Modifier,
    label: String,
    count: Int,
    total: Int,
    onCountChange: (Int) -> Unit
) {
    val colors = LocalShetabColors.current
    
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("${total.toString().toPersianNumber()} سوال $label", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colors.primaryText, fontFamily = IranSansFontFamily)
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.background(colors.bgMain, RoundedCornerShape(8.dp))
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { if (count > 0) onCountChange(count - 1) },
                contentAlignment = Alignment.Center
            ) {
                Text("-", fontWeight = FontWeight.Bold, color = colors.primaryText, fontSize = 16.sp, fontFamily = IranSansFontFamily)
            }
            Text(
                text = count.toString().toPersianNumber(),
                fontSize = 14.sp,
                color = colors.primaryText,
                modifier = Modifier.width(24.dp),
                textAlign = TextAlign.Center,
                fontFamily = IranSansFontFamily
            )
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { if (count < total) onCountChange(count + 1) },
                contentAlignment = Alignment.Center
            ) {
                Text("+", fontWeight = FontWeight.Bold, color = colors.primaryText, fontSize = 16.sp, fontFamily = IranSansFontFamily)
            }
        }
    }
}


