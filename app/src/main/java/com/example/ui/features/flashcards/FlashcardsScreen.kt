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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsScreen(
    navController: NavController,
    viewModel: FlashcardsViewModel = viewModel()
) {
    val colors = LocalShetabColors.current
    val dueFlashcards by viewModel.dueFlashcards.collectAsState()
    val allFlashcards by viewModel.allFlashcards.collectAsState()
    val categories by viewModel.categories.collectAsState()
    
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var reviewMode by remember { mutableStateOf(false) }
    var customStudyMode by remember { mutableStateOf(false) } // Extra practice mode
    var showAddDialog by remember { mutableStateOf(false) }

    // Navigation and Edge-to-Edge Safe areas
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgMain)
    ) {
        AppBackground()

        // 1. ACTIVE REVIEW MODE (Today's Scheduled Reviews)
        if (reviewMode && dueFlashcards.isNotEmpty()) {
            val cardsToReview = if (selectedCategory != null) {
                dueFlashcards.filter { it.category == selectedCategory }
            } else {
                dueFlashcards
            }
            if (cardsToReview.isNotEmpty()) {
                FlashcardReviewScreen(
                    flashcards = cardsToReview,
                    titleText = "مرور کارت‌های امروز",
                    onAgain = { card -> viewModel.markAsAgain(card) },
                    onHard = { card -> viewModel.markAsHard(card) },
                    onGood = { card -> viewModel.markAsGood(card) },
                    onEasy = { card -> viewModel.markAsEasy(card) },
                    onClose = { reviewMode = false }
                )
            } else {
                reviewMode = false
            }
        } 
        // 2. CUSTOM STUDY MODE (Review of All Cards in the category for practice)
        else if (customStudyMode && allFlashcards.isNotEmpty()) {
            val cardsToReview = if (selectedCategory != null) {
                allFlashcards.filter { it.category == selectedCategory }
            } else {
                allFlashcards
            }
            if (cardsToReview.isNotEmpty()) {
                FlashcardReviewScreen(
                    flashcards = cardsToReview.shuffled(), // Shuffle for natural practice
                    titleText = "مرور اختیاری (تمرین)",
                    onAgain = { card -> viewModel.markAsAgain(card) },
                    onHard = { card -> viewModel.markAsHard(card) },
                    onGood = { card -> viewModel.markAsGood(card) },
                    onEasy = { card -> viewModel.markAsEasy(card) },
                    onClose = { customStudyMode = false }
                )
            } else {
                customStudyMode = false
            }
        } 
        // 3. MAIN DASHBOARD OVERVIEW
        else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .background(colors.cardBg.copy(alpha = 0.5f), CircleShape)
                                .size(44.dp)
                                .border(1.dp, colors.primaryText.copy(alpha = 0.1f), CircleShape)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack, 
                                contentDescription = "بازگشت", 
                                tint = colors.primaryText
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "جعبه لایتنر هوشمند",
                            color = colors.primaryText,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    // Add Custom Card FAB equivalent
                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = colors.accentMain),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("کارت جدید", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Today's Status Header Card
                    item {
                        TodayOverviewBanner(
                            dueCount = dueFlashcards.size,
                            totalCount = allFlashcards.size,
                            onStartReview = { reviewMode = true },
                            onStartCustomStudy = { customStudyMode = true },
                            accentColor = colors.accentMain,
                            cardBgColor = colors.cardBg,
                            primaryTextColor = colors.primaryText,
                            secondaryTextColor = colors.secondaryText
                        )
                    }

                    // Statistics Grid / Progress report
                    item {
                        StatisticsSection(
                            allFlashcards = allFlashcards,
                            colors = colors
                        )
                    }

                    // Filter / Deck Selection Header
                    item {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Text(
                                "کتاب‌ها و دسته‌بندی‌های من",
                                color = colors.primaryText,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "برای محدود کردن مرور یا اضافه کردن یادگیری، یکی را انتخاب کنید.",
                                color = colors.secondaryText,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Deck Categories horizontal scroll
                    item {
                        DcksSelectionRow(
                            categories = categories,
                            selectedCategory = selectedCategory,
                            allFlashcards = allFlashcards,
                            dueFlashcards = dueFlashcards,
                            colors = colors,
                            onSelectCategory = { selectedCategory = it }
                        )
                    }

                    // Selected Category Status / Due details
                    item {
                        val activeCards = if (selectedCategory != null) {
                            dueFlashcards.filter { it.category == selectedCategory }
                        } else {
                            dueFlashcards
                        }
                        
                        CategoryDueOverview(
                            categoryName = selectedCategory ?: "همه دسته‌بندی‌ها",
                            dueCount = activeCards.size,
                            totalCount = if (selectedCategory != null) allFlashcards.count { it.category == selectedCategory } else allFlashcards.size,
                            onStartReview = {
                                if (selectedCategory != null) {
                                    reviewMode = true
                                } else {
                                    reviewMode = true
                                }
                            },
                            colors = colors
                        )
                    }

                    // Card List (preview of cards in selected deck with option to delete)
                    val listToShow = if (selectedCategory != null) {
                        allFlashcards.filter { it.category == selectedCategory }
                    } else {
                        allFlashcards
                    }

                    if (listToShow.isNotEmpty()) {
                        item {
                            Text(
                                text = "لیست تمام کارت‌های این دسته (${listToShow.size})".toPersianNumber(),
                                color = colors.primaryText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }

                        items(listToShow, key = { it.id }) { card ->
                            CardPreviewItem(
                                card = card,
                                colors = colors,
                                onDelete = { viewModel.deleteCard(card.id) }
                            )
                        }
                    } else if (allFlashcards.isEmpty()) {
                        item {
                            EmptyStateInitial(
                                colors = colors,
                                onCreateClick = { showAddDialog = true }
                            )
                        }
                    }
                }
            }
        }
    }

    // Manual Creation Dialog BottomSheet/Dialog UI
    if (showAddDialog) {
        AddFlashcardDialog(
            categories = categories,
            initialCategory = selectedCategory ?: "",
            onDismiss = { showAddDialog = false },
            onSave = { category, question, explanation ->
                val newCard = FlashcardEntity(
                    question = question,
                    optionsJson = "",
                    answer = -1,
                    explanation = explanation,
                    category = category.trim().ifEmpty { "عمومی" },
                    boxNumber = 1,
                    nextReviewDate = System.currentTimeMillis()
                )
                viewModel.saveCard(newCard) {
                    showAddDialog = false
                }
            },
            colors = colors
        )
    }
}











// =========================================================================
// GORGEOUS FLUID 2D REVIEW ENGNE (ANKI STYLE, ULTRA-RELIABLE, ZERO CRASHES)
// =========================================================================

