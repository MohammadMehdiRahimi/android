package com.example.ui.features.studyplan

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.network.ApiClient
import com.example.network.DailyStudyTasksBodyDto
import com.example.network.StudyTaskDto
import com.example.network.StudyTaskExecutionDto
import com.example.network.StudyTaskNamedRefDto
import com.example.network.StudyTaskSummaryDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
            sourceType = "SYSTEM_PLAN",
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
            ), // In Progress
        ),
        StudyTaskDto(
            id = "task_bio",
            sourceType = "SYSTEM_PLAN",
            title = "مطالعه درس تنظیم عصبی",
            book = StudyTaskNamedRefDto(id = "b3", name = "زیست‌شناسی ۳"),
            chapter = StudyTaskNamedRefDto(id = "c3", name = "فصل ۲"),
            topic = StudyTaskNamedRefDto(id = "t3", name = "دستگاه عصبی"),
            scheduledOn = "2026-08-24",
            periodCount = 1,
            minutesPerPeriod = 25,
            plannedMinutes = 25,
            execution = StudyTaskExecutionDto(
                id = "exec2",
                status = "COMPLETED",
                completionPercent = 100,
            ), // Completed
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
                    completed = 1,
                    pending = 2,
                    completionPercent = 33,
                ),
            ),
        )

        assertEquals(3, state.totalTasks)
        assertEquals(1, state.completedTasks)
        assertEquals(2, state.remainingTasks)
        assertEquals(160, state.totalStudyMinutes)
        assertEquals(1f / 3f, state.progressFraction, 0.01f)
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
        assertEquals(1, pendingState.filteredItems.size)
        assertEquals("task_math", pendingState.filteredItems.first().id)

        // COMPLETED filter
        val completedState = state.copy(selectedFilter = StudyTaskFilter.COMPLETED)
        assertEquals(1, completedState.filteredItems.size)
        assertEquals("task_bio", completedState.filteredItems.first().id)
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
}
