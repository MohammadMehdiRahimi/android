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
fun TodayOverviewBanner(
    dueCount: Int,
    totalCount: Int,
    onStartReview: () -> Unit,
    onStartCustomStudy: () -> Unit,
    accentColor: Color,
    cardBgColor: Color,
    primaryTextColor: Color,
    secondaryTextColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                clip = false
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBgColor
        ),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "برنامه آماده مرور امروز",
                        color = secondaryTextColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (dueCount > 0) "$dueCount کارت آماده" else "همه مرورها کامل شده",
                        color = primaryTextColor,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // High count badge
                Box(
                    modifier = Modifier
                        .background(
                            if (dueCount > 0) Color(0xFFFF5252).copy(alpha = 0.15f) else Color(0xFF4CAF50).copy(alpha = 0.15f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (dueCount > 0) "کارت فعال" else "سبز",
                        color = if (dueCount > 0) Color(0xFFFF5252) else Color(0xFF4CAF50),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (dueCount > 0) {
                // Start Scheduled deck reviews
                Button(
                    onClick = onStartReview,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.School, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("شروع مرور هوشمند امروز", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                }
            } else {
                // Celebrating Done! Allow Custom Study
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color(0xFF4CAF50).copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check, 
                            contentDescription = "کیلیپبرد کامل", 
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "آفرین! تمام کارت‌های نوبت امروز مرور شدند.",
                        color = secondaryTextColor,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (totalCount > 0) {
                        // Let them review cards for practice anytime
                        OutlinedButton(
                            onClick = onStartCustomStudy,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            border = BorderStroke(1.2.dp, accentColor),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = accentColor)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("مرور اختیاری و آزاد (آموزش مجدد)", fontWeight = FontWeight.Bold, color = accentColor)
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun StatisticsSection(
    allFlashcards: List<FlashcardEntity>,
    colors: com.example.ui.theme.ShetabColorPalette
) {
    val total = allFlashcards.size
    val box1 = allFlashcards.count { it.boxNumber == 1 }
    val box2 = allFlashcards.count { it.boxNumber == 2 }
    val box3 = allFlashcards.count { it.boxNumber == 3 }
    val box4 = allFlashcards.count { it.boxNumber == 4 }
    val box5 = allFlashcards.count { it.boxNumber == 5 }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBg.copy(alpha = 0.7f)),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, colors.primaryText.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "آمار لایتنر من",
                color = colors.primaryText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))

            // Basic metrics row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatCompactBox(
                    label = "کل فلش‌کارت‌ها",
                    value = total.toString(),
                    color = colors.accentMain,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                StatCompactBox(
                    label = "آرشیو (مسلط)",
                    value = box5.toString(),
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                StatCompactBox(
                    label = "در حال یادگیری",
                    value = (total - box5).toString(),
                    color = Color(0xFFFF9800),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Litener level distribution chart
            Text(
                "پراکندگی کارت‌ها در جعبه‌ها",
                color = colors.secondaryText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))

            val maxBoxCount = remember(box1, box2, box3, box4, box5) {
                listOf(box1, box2, box3, box4, box5).maxOrNull()?.coerceAtLeast(1) ?: 1
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                BoxLineItem(levelNum = 1, currentCount = box1, maxCount = maxBoxCount, barColor = Color(0xFFF44336), colors = colors)
                BoxLineItem(levelNum = 2, currentCount = box2, maxCount = maxBoxCount, barColor = Color(0xFFFF9800), colors = colors)
                BoxLineItem(levelNum = 3, currentCount = box3, maxCount = maxBoxCount, barColor = Color(0xFFFFEB3B), colors = colors)
                BoxLineItem(levelNum = 4, currentCount = box4, maxCount = maxBoxCount, barColor = Color(0xFF03A9F4), colors = colors)
                BoxLineItem(levelNum = 5, currentCount = box5, maxCount = maxBoxCount, barColor = Color(0xFF4CAF50), colors = colors)
            }
        }
    }
}
@Composable
fun StatCompactBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value.toPersianNumber(),
                color = color,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = Color.Gray,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
@Composable
fun BoxLineItem(
    levelNum: Int,
    currentCount: Int,
    maxCount: Int,
    barColor: Color,
    colors: com.example.ui.theme.ShetabColorPalette
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "جعبه $levelNum".toPersianNumber(),
            color = colors.primaryText,
            fontSize = 11.sp,
            modifier = Modifier.width(48.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))

        // Progress line
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(CircleShape)
                .background(colors.primaryText.copy(alpha = 0.05f))
        ) {
            val fraction = currentCount.toFloat() / maxCount.toFloat()
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction)
                    .clip(CircleShape)
                    .background(barColor)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "$currentCount کارت".toPersianNumber(),
            color = colors.secondaryText,
            fontSize = 11.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.width(44.dp)
        )
    }
}
@Composable
fun DcksSelectionRow(
    categories: List<String>,
    selectedCategory: String?,
    allFlashcards: List<FlashcardEntity>,
    dueFlashcards: List<FlashcardEntity>,
    colors: com.example.ui.theme.ShetabColorPalette,
    onSelectCategory: (String?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // "All Decks" Card
        DeckSelectCard(
            title = "همه کارت‌ها",
            dueCount = dueFlashcards.size,
            totalCount = allFlashcards.size,
            isSelected = selectedCategory == null,
            onClick = { onSelectCategory(null) },
            colors = colors
        )

        // Category Cards
        categories.forEach { cat ->
            val dueInCat = dueFlashcards.count { it.category == cat }
            val totalInCat = allFlashcards.count { it.category == cat }
            
            DeckSelectCard(
                title = cat,
                dueCount = dueInCat,
                totalCount = totalInCat,
                isSelected = selectedCategory == cat,
                onClick = { onSelectCategory(cat) },
                colors = colors
            )
        }
    }
}
@Composable
fun DeckSelectCard(
    title: String,
    dueCount: Int,
    totalCount: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    colors: com.example.ui.theme.ShetabColorPalette
) {
    val borderColor = if (isSelected) colors.accentMain else colors.primaryText.copy(alpha = 0.05f)
    val containerColor = if (isSelected) colors.accentMain.copy(alpha = 0.12f) else colors.cardBg

    Card(
        modifier = Modifier
            .width(150.dp)
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                color = colors.primaryText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "کل: $totalCount".toPersianNumber(),
                        color = colors.secondaryText,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "$dueCount آماده مرور".toPersianNumber(),
                        color = if (dueCount > 0) Color(0xFFFF5252) else colors.secondaryText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            if (isSelected) colors.accentMain else colors.primaryText.copy(alpha = 0.05f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isSelected) Icons.Default.Check else Icons.Default.MenuBook,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = if (isSelected) Color.White else colors.secondaryText
                    )
                }
            }
        }
    }
}
@Composable
fun CategoryDueOverview(
    categoryName: String,
    dueCount: Int,
    totalCount: Int,
    onStartReview: () -> Unit,
    colors: com.example.ui.theme.ShetabColorPalette
) {
    if (dueCount > 0) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = colors.accentMain.copy(alpha = 0.05f)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, colors.accentMain.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "دسته متمرکز: $categoryName",
                        color = colors.primaryText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "$dueCount کارت از کل $totalCount برای امروز معلق می باشد.".toPersianNumber(),
                        color = colors.secondaryText,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = onStartReview,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("مرور ویژه", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
@Composable
fun CardPreviewItem(
    card: FlashcardEntity,
    colors: com.example.ui.theme.ShetabColorPalette,
    onDelete: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(0.8.dp, colors.primaryText.copy(alpha = 0.06f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = card.category,
                        color = colors.accentMain,
                        fontSize = 10.sp,
                        modifier = Modifier
                            .background(colors.accentMain.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                    Text(
                        text = "جعبه ${card.boxNumber}".toPersianNumber(),
                        color = when (card.boxNumber) {
                            1 -> Color(0xFFE53935)
                            2 -> Color(0xFFEF6C00)
                            3 -> Color(0xFFFBC02D)
                            4 -> Color(0xFF0288D1)
                            else -> Color(0xFF43A047)
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(colors.bgMain.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Delete, 
                            contentDescription = "حذف کارت", 
                            tint = Color.Red.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Non-HTML presentation of Question
            val plainQuestion = remember(card.question) {
                card.question.replace(Regex("<[^>]*>"), "")
            }

            Text(
                text = plainQuestion,
                color = colors.primaryText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis
            )

            if (isExpanded) {
                Divider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = colors.primaryText.copy(alpha = 0.1f)
                )

                Text(
                    text = "پاسخ لایتنر:",
                    color = Color(0xFF4CAF50),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                val plainExplanation = remember(card.explanation) {
                    card.explanation.replace(Regex("<[^>]*>"), "")
                }

                Text(
                    text = plainExplanation,
                    color = colors.secondaryText,
                    fontSize = 12.sp
                )
            }
        }
    }
}
@Composable
fun EmptyStateInitial(
    colors: com.example.ui.theme.ShetabColorPalette,
    onCreateClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBg.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Outlined.Lightbulb,
                contentDescription = null,
                tint = colors.accentMain,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "کارت به این دسته اضافه نشده است",
                fontWeight = FontWeight.Bold,
                color = colors.primaryText,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "می‌توانید با ایجاد کارت دستی یا با جواب به تست‌ها، فلش‌کارت‌های جدید بسازید.",
                color = colors.secondaryText,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onCreateClick,
                colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain)
            ) {
                Text("ثبت اولین کارت دستی", fontSize = 11.sp)
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFlashcardDialog(
    categories: List<String>,
    initialCategory: String,
    onDismiss: () -> Unit,
    onSave: (category: String, question: String, explanation: String) -> Unit,
    colors: com.example.ui.theme.ShetabColorPalette
) {
    var categoryText by remember { mutableStateOf(initialCategory) }
    var questionText by remember { mutableStateOf("") }
    var explanationText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = colors.cardBg),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "افزودن فلش کارت دستی",
                    color = colors.primaryText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                // Category field
                OutlinedTextField(
                    value = categoryText,
                    onValueChange = { categoryText = it },
                    label = { Text("درس یا دسته‌بندی") },
                    placeholder = { Text("مثال: شیمی یا فیزیک") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.accentMain,
                        focusedLabelColor = colors.accentMain,
                        unfocusedBorderColor = colors.primaryText.copy(alpha = 0.1f)
                    )
                )

                // Question field
                OutlinedTextField(
                    value = questionText,
                    onValueChange = { questionText = it },
                    label = { Text("سوال روی کارت (پشتیبانی LaTeX)") },
                    placeholder = { Text("مثال: فرض کنید \$x^2 + \$y^2 = \$r^2\$") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.accentMain,
                        focusedLabelColor = colors.accentMain,
                        unfocusedBorderColor = colors.primaryText.copy(alpha = 0.1f)
                    )
                )

                // Answer/Explanation field
                OutlinedTextField(
                    value = explanationText,
                    onValueChange = { explanationText = it },
                    label = { Text("پاسخ پشت کارت") },
                    placeholder = { Text("توضیحات و پاسخ کامل فرمول یا پرسش") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.accentMain,
                        focusedLabelColor = colors.accentMain,
                        unfocusedBorderColor = colors.primaryText.copy(alpha = 0.1f)
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            if (questionText.isNotBlank() && explanationText.isNotBlank()) {
                                onSave(categoryText, questionText, explanationText)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain)
                    ) {
                        Text("ثبت کارت")
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("انصراف")
                    }
                }
            }
        }
    }
}
