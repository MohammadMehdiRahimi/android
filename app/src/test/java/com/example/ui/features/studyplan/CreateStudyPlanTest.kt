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
    fun `test selecting chapter initializes with zero pre-selected topics`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = CreateStudyPlanViewModel(app)

        val blockId = vm.state.value.chapterBlocks.first().blockId
        val currentSubject = vm.state.value.selectedSubject
        val firstChapter = currentSubject?.chapters?.firstOrNull()

        if (firstChapter != null) {
            vm.selectChapterForBlock(blockId, firstChapter.id)
            val updatedBlock = vm.state.value.chapterBlocks.first { it.blockId == blockId }
            assertEquals(firstChapter.id, updatedBlock.selectedChapterId)
            assertTrue("Topics must be empty by default on chapter selection", updatedBlock.selectedTopicIds.isEmpty())

            // Now toggle a topic explicitly
            val firstTopic = firstChapter.topics.firstOrNull()
            if (firstTopic != null) {
                vm.toggleTopicForBlock(blockId, firstTopic.id)
                val blockWithTopic = vm.state.value.chapterBlocks.first { it.blockId == blockId }
                assertTrue(blockWithTopic.selectedTopicIds.contains(firstTopic.id))
                assertEquals(1, blockWithTopic.selectedTopicIds.size)
            }
        }
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

    @Test
    fun `test loading catalog state lifecycle`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        // Uncached: isLoadingCatalog is true while loading in background
        StudyPlanCatalogCache.clear()
        val vmUncached = CreateStudyPlanViewModel(app)
        assertTrue(vmUncached.state.value.isLoadingCatalog)

        // Cached: isLoadingCatalog is false immediately
        StudyPlanCatalogCache.put(
            "GRADE_12",
            "EXPERIMENTAL",
            listOf(
                SubjectVisualItem(
                    id = "sub_1",
                    name = "ریاضی ۱",
                    minimalName = "ریاضی",
                    drawableRes = com.example.R.drawable.ic_launcher_foreground,
                    tintHex = 0xFF4F46E5,
                    chapters = emptyList(),
                ),
            ),
        )
        val vmCached = CreateStudyPlanViewModel(app)
        assertFalse(vmCached.state.value.isLoadingCatalog)
    }

    @Test
    fun `test formatMinimalChapterName outputs numbers without fasl prefix`() {
        val formatted1 = formatMinimalChapterName(0, "فصل اول: تابع و معادله")
        assertEquals("۱: تابع و معادله", formatted1)

        val formatted2 = formatMinimalChapterName(1, "فصل 2")
        assertEquals("۲", formatted2)

        val formatted3 = formatMinimalChapterName(2, "فصل ۳: مثلثات")
        assertEquals("۳: مثلثات", formatted3)
    }

    @Test
    fun `test catalog caching stores grade catalog`() {
        val cache = StudyPlanCatalogCache
        val testGrade = "GRADE_TEST"
        val testMajor = "EXPERIMENTAL"
        assertTrue(cache.get(testGrade, testMajor) == null)

        val dummySubjects = listOf(
            SubjectVisualItem(
                id = "sub_1",
                name = "ریاضی ۱",
                minimalName = "ریاضی",
                drawableRes = com.example.R.drawable.ic_launcher_foreground,
                tintHex = 0xFF4F46E5,
                chapters = emptyList(),
            ),
        )
        cache.put(testGrade, testMajor, dummySubjects)

        assertEquals(dummySubjects, cache.get(testGrade, testMajor))
    }

    @Test
    fun `test plan summary modal display and hide`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = CreateStudyPlanViewModel(app)

        assertFalse(vm.state.value.isSummaryModalVisible)

        // When topics are empty, requestPlanSummary should trigger ShowError
        vm.requestPlanSummary()
        assertFalse(vm.state.value.isSummaryModalVisible)

        // Select chapter and topic
        val blockId = vm.state.value.chapterBlocks.first().blockId
        val currentSubject = vm.state.value.selectedSubject
        val firstChapter = currentSubject?.chapters?.firstOrNull()
        if (firstChapter != null) {
            vm.selectChapterForBlock(blockId, firstChapter.id)
            val firstTopic = firstChapter.topics.firstOrNull()
            if (firstTopic != null) {
                vm.toggleTopicForBlock(blockId, firstTopic.id)
                vm.requestPlanSummary()
                assertTrue(vm.state.value.isSummaryModalVisible)

                vm.hideSummaryModal()
                assertFalse(vm.state.value.isSummaryModalVisible)
            }
        }
    }
}
