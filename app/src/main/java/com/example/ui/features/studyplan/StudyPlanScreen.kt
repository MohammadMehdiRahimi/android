package com.example.ui.features.studyplan

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.R
import com.example.ui.theme.IranSansFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyPlanScreen(
    navController: NavController,
    onBackClick: (() -> Unit)? = null,
    viewModel: StudyPlanViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.mutationMessage) {
        state.mutationMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            containerColor = PlanBackground,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(PlanBackground),
            ) {
                if (state.loading && state.day == null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding(),
                    ) {
                        StudyPlanTopHeader(
                            onNotificationClick = { navController.navigate("notifications") },
                            unreadNotification = true,
                        )
                        StudyPlanSkeletonLoading()
                    }
                } else if (state.error != null && state.day == null) {
                    // Error Screen State
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = null,
                            tint = PlanOrange,
                            modifier = Modifier.size(56.dp),
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = state.error ?: "خطا در دریافت برنامه مطالعه",
                            color = PlanNavy,
                            fontFamily = IranSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { viewModel.retry() },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PlanPurple),
                            modifier = Modifier.testTag("study_plan_retry_button"),
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(id = R.string.study_plan_error_retry),
                                color = Color.White,
                                fontFamily = IranSansFontFamily,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                } else {
                    // Main Success / Loaded Content
                    val allItems = state.day?.items ?: emptyList()
                    val remainingItems = state.remainingItems
                    val completedItems = state.completedItems
                    val isFilterActive = state.selectedFilter != StudyTaskFilter.ALL
                    val displayedItems = state.filteredItems

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .testTag("study_plan_lazy_column"),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        // 1. Top Header
                        item(key = "header") {
                            StudyPlanTopHeader(
                                onNotificationClick = { navController.navigate("notifications") },
                                unreadNotification = true,
                            )
                        }

                        // 2. Summary Matrix Card
                        item(key = "summary_card") {
                            StudyPlanSummaryCard(
                                totalDurationMinutes = state.totalStudyMinutes,
                                remainingCount = state.remainingTasks,
                                completedCount = state.completedTasks,
                                totalCount = state.totalTasks,
                                progressFraction = state.progressFraction,
                            )
                        }

                        // 3. Filter Row
                        item(key = "filter_row") {
                            StudyPlanFilterRow(
                                selectedFilter = state.selectedFilter,
                                onFilterSelected = { viewModel.setFilter(it) },
                            )
                        }

                        if (allItems.isEmpty()) {
                            // Empty State
                            item(key = "empty_state") {
                                StudyPlanEmptyState(
                                    onAddTaskClick = { navController.navigate("create_study_plan") },
                                )
                            }
                        } else if (isFilterActive) {
                            // Filtered View
                            if (displayedItems.isEmpty()) {
                                item(key = "filter_empty") {
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 20.dp, vertical = 20.dp),
                                        shape = RoundedCornerShape(20.dp),
                                        color = Color.White,
                                    ) {
                                        Text(
                                            text = "تسکی در این وضعیت وجود ندارد.",
                                            color = PlanMuted,
                                            fontFamily = IranSansFontFamily,
                                            fontSize = 13.sp,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(24.dp),
                                        )
                                    }
                                }
                            } else {
                                items(displayedItems, key = { "filtered_${it.id}" }) { task ->
                                    if (task.isCompleted) {
                                        CompletedTaskItemCard(task = task)
                                    } else {
                                        StudyTaskItemCard(
                                            task = task,
                                            isBookmarked = state.bookmarkedIds.contains(task.id),
                                            onBookmarkToggle = { viewModel.toggleBookmark(task.id) },
                                            onStartClick = { viewModel.startTask(task) },
                                            onContinueClick = { viewModel.startTask(task) },
                                            isBusy = state.busyTaskId == task.id,
                                        )
                                    }
                                }
                            }
                        } else {
                            // Default View: Remaining Tasks Section + Completed Tasks Section
                            if (remainingItems.isNotEmpty()) {
                                item(key = "remaining_header") {
                                    Box {
                                        RemainingTasksSectionHeader(
                                            count = remainingItems.size,
                                            onSortClick = { viewModel.toggleSortMenu() },
                                        )

                                        DropdownMenu(
                                            expanded = state.showSortMenu,
                                            onDismissRequest = { viewModel.toggleSortMenu() },
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text("پیش‌فرض برنامه", fontFamily = IranSansFontFamily) },
                                                onClick = { viewModel.setSortOrder(StudyTaskSortOrder.DEFAULT) },
                                            )
                                            DropdownMenuItem(
                                                text = { Text("مدت زمان مطالعه", fontFamily = IranSansFontFamily) },
                                                onClick = { viewModel.setSortOrder(StudyTaskSortOrder.DURATION) },
                                            )
                                            DropdownMenuItem(
                                                text = { Text("اولویت دوره", fontFamily = IranSansFontFamily) },
                                                onClick = { viewModel.setSortOrder(StudyTaskSortOrder.PRIORITY) },
                                            )
                                        }
                                    }
                                }

                                items(remainingItems, key = { "remaining_${it.id}" }) { task ->
                                    StudyTaskItemCard(
                                        task = task,
                                        isBookmarked = state.bookmarkedIds.contains(task.id),
                                        onBookmarkToggle = { viewModel.toggleBookmark(task.id) },
                                        onStartClick = { viewModel.startTask(task) },
                                        onContinueClick = { viewModel.startTask(task) },
                                        isBusy = state.busyTaskId == task.id,
                                    )
                                }
                            }

                            if (completedItems.isNotEmpty()) {
                                item(key = "completed_header") {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    CompletedTasksSectionHeader(
                                        count = completedItems.size,
                                        onSeeAllClick = { viewModel.setFilter(StudyTaskFilter.COMPLETED) },
                                    )
                                }

                                items(completedItems, key = { "completed_${it.id}" }) { task ->
                                    CompletedTaskItemCard(task = task)
                                }
                            }
                        }
                    }
                }

                // Floating Action Button (FAB) strictly at Bottom-Left (In RTL, End is Left)
                AddTaskFloatingActionButton(
                    onClick = { navController.navigate("create_study_plan") },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 22.dp, bottom = 85.dp),
                )

                // Dialog for creating a new task manually
                if (state.showAddDialog) {
                    CreateTaskDialog(
                        books = state.catalog?.books ?: emptyList(),
                        isCreating = state.creating,
                        onDismiss = { viewModel.closeAddDialog() },
                        onConfirm = { topicId, periodCount, minutesPerPeriod ->
                            viewModel.createManualTask(
                                topicId = topicId,
                                periodCount = periodCount,
                                minutesPerPeriod = minutesPerPeriod,
                                onCreated = { viewModel.closeAddDialog() },
                            )
                        },
                    )
                }
            }
        }
    }
}
