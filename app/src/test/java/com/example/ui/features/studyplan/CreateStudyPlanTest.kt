package com.example.ui.features.studyplan

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.network.ApiClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CreateStudyPlanTest {

    @Before
    fun setUp() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        ApiClient.init(app)
    }

    @Test
    fun `test state default timing calculations`() {
        val state = CreateStudyPlanUiState(
            periodCount = 3,
            isManualTiming = false,
            studyDurationMinutes = 45,
            breakDurationMinutes = 15,
        )

        // Total = 3 * (45 + 15) = 180 mins = 3 hours 0 mins
        assertEquals(180, state.totalEstimatedMinutes)
        assertEquals(3, state.totalHours)
        assertEquals(0, state.remainingMinutes)
    }

    @Test
    fun `test manual timing toggle and state update`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = CreateStudyPlanViewModel(app)

        assertFalse(vm.state.value.isManualTiming)

        vm.setManualTiming(true)
        assertTrue(vm.state.value.isManualTiming)

        vm.setStudyDuration(60)
        assertEquals(60, vm.state.value.studyDurationMinutes)

        vm.setBreakDuration(30)
        assertEquals(30, vm.state.value.breakDurationMinutes)
    }

    @Test
    fun `test multi chapter block addition and removal`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = CreateStudyPlanViewModel(app)

        val initialBlocks = vm.state.value.chapterBlocks
        assertEquals(1, initialBlocks.size)
        assertEquals("", initialBlocks.first().selectedChapterId)

        // Select a chapter for the first block
        val currentSubject = vm.state.value.selectedSubject
        val firstChapterId = currentSubject?.chapters?.firstOrNull()?.id
        if (firstChapterId != null) {
            vm.selectChapterForBlock(initialBlocks.first().blockId, firstChapterId)
            assertEquals(firstChapterId, vm.state.value.chapterBlocks.first().selectedChapterId)
        }

        // Add a second chapter block
        vm.addChapterBlock()
        val twoBlocks = vm.state.value.chapterBlocks
        assertEquals(2, twoBlocks.size)
        assertEquals("", twoBlocks[1].selectedChapterId)

        // Select chapter and toggle topic for second block
        val secondBlock = twoBlocks[1]
        val secondChapterId = currentSubject?.chapters?.getOrNull(1)?.id ?: firstChapterId
        if (secondChapterId != null) {
            vm.selectChapterForBlock(secondBlock.blockId, secondChapterId)
            val updatedSecondBlock = vm.state.value.chapterBlocks.find { it.blockId == secondBlock.blockId }
            val firstTopicId = updatedSecondBlock?.selectedTopicIds?.firstOrNull()
            if (firstTopicId != null) {
                vm.toggleTopicForBlock(secondBlock.blockId, firstTopicId)
                assertFalse(vm.state.value.chapterBlocks.find { it.blockId == secondBlock.blockId }?.selectedTopicIds?.contains(firstTopicId) == true)
            }
        }

        // Remove second block
        vm.removeChapterBlock(secondBlock.blockId)
        assertEquals(1, vm.state.value.chapterBlocks.size)
    }

    @Test
    fun `test period increment and decrement limits`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = CreateStudyPlanViewModel(app)

        assertEquals(3, vm.state.value.periodCount)

        vm.incrementPeriod()
        assertEquals(4, vm.state.value.periodCount)

        vm.decrementPeriod()
        vm.decrementPeriod()
        assertEquals(2, vm.state.value.periodCount)
    }

    @Test
    fun `test grade selection and subject minimal names`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = CreateStudyPlanViewModel(app)

        vm.selectGrade("GRADE_11", "پایه یازدهم")
        assertEquals("GRADE_11", vm.state.value.selectedGrade)
        assertEquals("پایه یازدهم", vm.state.value.selectedGradeName)

        val subjects = vm.state.value.subjects
        assertTrue(subjects.isNotEmpty())
        subjects.forEach { subject ->
            assertNotNull(subject.minimalName)
            assertFalse(subject.minimalName.contains(" ۱"))
            assertFalse(subject.minimalName.contains(" ۲"))
            assertFalse(subject.minimalName.contains(" ۳"))
        }
    }
}
