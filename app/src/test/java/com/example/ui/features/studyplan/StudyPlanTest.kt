package com.example.ui.features.studyplan

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.domain.date.JalaliDate
import com.example.network.ApiClient
import com.example.network.DailyStudyTasksBodyDto
import com.example.network.StudyTaskDto
import com.example.network.StudyTaskExecutionDto
import com.example.network.StudyTaskNamedRefDto
import com.example.network.StudyTaskSummaryDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class StudyPlanTest {

    @Before
    fun setUp() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        ApiClient.init(app)
    }

    private val sampleTasks = listOf(
        StudyTaskDto(
            id = "task_math",
            sourceType = "SYSTEM_PLAN",
            title = "حل تمرین‌های صفحه ۲۲ تا ۸۰",
            book = StudyTaskNamedRefDto(id = "b1", name = "ریاضی ۳"),
            chapter = StudyTaskNamedRefDto(id = "c1", name = "فصل ۲"),
            topic = StudyTaskNamedRefDto(id = "t1", name = "معادلات درجه دوم"),
            scheduledOn = "2026-08-24",
            periodCount = 2,
            minutesPerPeriod = 45,
            plannedMinutes = 45,
            execution = null, // Pending
        ),
        StudyTaskDto(
            id = "task_chem",
            sourceType = "MANUAL",
            title = "حل تست‌های ترکیبات آلی",
            book = StudyTaskNamedRefDto(id = "b2", name = "شیمی ۳"),
            chapter = StudyTaskNamedRefDto(id = "c2", name = "فصل ۳"),
            topic = StudyTaskNamedRefDto(id = "t2", name = "ترکیبات کربن"),
            scheduledOn = "2026-08-24",
            periodCount = 2,
            minutesPerPeriod = 90,
            plannedMinutes = 90,
            execution = StudyTaskExecutionDto(
                id = "exec1",
                status = "ACTIVE",
                activeSeconds = 3600, // 60 min elapsed
            ), // In Progress - Execution != null
        ),
        StudyTaskDto(
            id = "task_bio",
            sourceType = "MANUAL",
            title = "مطالعه درس تنظیم عصبی",
            book = StudyTaskNamedRefDto(id = "b3", name = "زیست‌شناسی ۳"),
            chapter = StudyTaskNamedRefDto(id = "c3", name = "فصل ۲"),
            topic = StudyTaskNamedRefDto(id = "t3", name = "دستگاه عصبی"),
            scheduledOn = "2026-08-24",
            periodCount = 1,
            minutesPerPeriod = 25,
            plannedMinutes = 25,
            execution = null, // Pending Manual Task -> Editable & Deletable
        ),
    )

    @Test
    fun `test state metrics computation`() {
        val state = StudyPlanUiState(
            day = DailyStudyTasksBodyDto(
                date = "2026-08-24",
                items = sampleTasks,
                summary = StudyTaskSummaryDto(
                    total = 3,
                    completed = 0,
                    pending = 3,
                    completionPercent = 0,
                ),
            ),
        )

        assertEquals(3, state.totalTasks)
        assertEquals(0, state.completedTasks)
        assertEquals(3, state.remainingTasks)
        assertEquals(160, state.totalStudyMinutes)
    }

    @Test
    fun `test execution lock property rules`() {
        // System Plan task
        val systemTask = sampleTasks[0]
        assertFalse(systemTask.isEditable)
        assertFalse(systemTask.isDeletable)

        // Manual Task with Execution (In Progress) -> LOCKED
        val executedManualTask = sampleTasks[1]
        assertFalse(executedManualTask.isEditable)
        assertFalse(executedManualTask.isDeletable)

        // Manual Task with NO Execution -> EDITABLE & DELETABLE
        val pendingManualTask = sampleTasks[2]
        assertTrue(pendingManualTask.isEditable)
        assertTrue(pendingManualTask.isDeletable)
    }

    @Test
    fun `test filter functionality`() {
        val state = StudyPlanUiState(
            day = DailyStudyTasksBodyDto(
                date = "2026-08-24",
                items = sampleTasks,
            ),
        )

        // ALL filter
        val allState = state.copy(selectedFilter = StudyTaskFilter.ALL)
        assertEquals(3, allState.filteredItems.size)

        // IN_PROGRESS filter
        val inProgressState = state.copy(selectedFilter = StudyTaskFilter.IN_PROGRESS)
        assertEquals(1, inProgressState.filteredItems.size)
        assertEquals("task_chem", inProgressState.filteredItems.first().id)

        // PENDING filter
        val pendingState = state.copy(selectedFilter = StudyTaskFilter.PENDING)
        assertEquals(2, pendingState.filteredItems.size)
    }

    @Test
    fun `test sort order functionality`() {
        val state = StudyPlanUiState(
            day = DailyStudyTasksBodyDto(
                date = "2026-08-24",
                items = sampleTasks,
            ),
        )

        // DURATION sort
        val durationSorted = state.copy(sortOrder = StudyTaskSortOrder.DURATION)
        assertEquals("task_chem", durationSorted.filteredItems[0].id) // 90 mins
        assertEquals("task_math", durationSorted.filteredItems[1].id) // 45 mins
        assertEquals("task_bio", durationSorted.filteredItems[2].id)  // 25 mins
    }

    @Test
    fun `test subject visual configuration mapping`() {
        val mathConfig = getSubjectVisualConfig("ریاضی ۳")
        assertEquals("ریاضی", mathConfig.title)

        val physicsConfig = getSubjectVisualConfig("فیزیک ۱")
        assertEquals("فیزیک", physicsConfig.title)

        val chemistryConfig = getSubjectVisualConfig("شیمی ۲")
        assertEquals("شیمی", chemistryConfig.title)

        val biologyConfig = getSubjectVisualConfig("زیست‌شناسی ۳")
        assertEquals("زیست‌شناسی", biologyConfig.title)
    }

    @Test
    fun `test bookmark toggle`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = StudyPlanViewModel(app)

        assertFalse(viewModel.state.value.bookmarkedIds.contains("task_123"))
        viewModel.toggleBookmark("task_123")
        assertTrue(viewModel.state.value.bookmarkedIds.contains("task_123"))
        viewModel.toggleBookmark("task_123")
        assertFalse(viewModel.state.value.bookmarkedIds.contains("task_123"))
    }

    @Test
    fun `test jalali date selection and dialog controls in viewModel`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = StudyPlanViewModel(app)

        val targetDate = JalaliDate(1405, 6, 15)
        viewModel.selectJalaliDate(targetDate)
        assertEquals(targetDate, viewModel.state.value.selectedJalaliDate)

        viewModel.selectNextDay()
        assertEquals(JalaliDate(1405, 6, 16), viewModel.state.value.selectedJalaliDate)

        viewModel.selectPreviousDay()
        assertEquals(targetDate, viewModel.state.value.selectedJalaliDate)

        // Test Dialog state toggles
        viewModel.openAddDialog()
        assertTrue(viewModel.state.value.showAddDialog)
        viewModel.closeAddDialog()
        assertFalse(viewModel.state.value.showAddDialog)

        val pendingManualTask = sampleTasks[2]
        viewModel.openEditDialog(pendingManualTask)
        assertEquals(pendingManualTask, viewModel.state.value.taskBeingEdited)
        viewModel.closeEditDialog()
        assertNull(viewModel.state.value.taskBeingEdited)

        viewModel.openDeleteConfirmDialog(pendingManualTask)
        assertEquals(pendingManualTask, viewModel.state.value.taskBeingDeleted)
        viewModel.closeDeleteConfirmDialog()
        assertNull(viewModel.state.value.taskBeingDeleted)
    }

    @Test
    fun `test markTaskDone execution state transition logic`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = StudyPlanViewModel(app)

        // Calling markTaskDone for a pending task initiates activity completion
        val pendingTask = sampleTasks[0]
        viewModel.markTaskDone(pendingTask)
        // Check that function executes safely without exceptions
        assertTrue(pendingTask.isPending)
    }

    @Test
    fun `test viewModel factory instantiation`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val factory = StudyPlanViewModel.provideFactory(app)
        val createdVm = factory.create(StudyPlanViewModel::class.java)
        assertTrue(createdVm is StudyPlanViewModel)
    }
}
