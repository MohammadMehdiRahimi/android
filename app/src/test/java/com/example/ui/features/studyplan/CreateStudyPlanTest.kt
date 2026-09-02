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
    fun `test state multi-book timing calculations`() {
        val book1 = BookPlanBlock(
            bookBlockId = "b1",
            periodCount = 3,
            studyDurationMinutes = 45,
            breakDurationMinutes = 15,
        )
        val book2 = BookPlanBlock(
            bookBlockId = "b2",
            periodCount = 2,
            studyDurationMinutes = 30,
            breakDurationMinutes = 10,
        )
        val state = CreateStudyPlanUiState(
            bookBlocks = listOf(book1, book2),
        )

        // Book 1: 3 * (45 + 15) = 180 min
        // Book 2: 2 * (30 + 10) = 80 min
        // Total = 260 min = 4 hours 20 mins
        assertEquals(260, state.totalEstimatedMinutes)
        assertEquals(4, state.totalHours)
        assertEquals(20, state.remainingMinutes)
    }

    @Test
    fun `test multi-book addition and removal`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = CreateStudyPlanViewModel(app)

        assertEquals(1, vm.state.value.bookBlocks.size)

        vm.addBookBlock()
        assertEquals(2, vm.state.value.bookBlocks.size)

        val secondBookId = vm.state.value.bookBlocks[1].bookBlockId
        vm.removeBookBlock(secondBookId)
        assertEquals(1, vm.state.value.bookBlocks.size)
    }

    @Test
    fun `test multi-chapter inside book addition and removal`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = CreateStudyPlanViewModel(app)

        val firstBook = vm.state.value.bookBlocks.first()
        assertEquals(1, firstBook.chapterBlocks.size)

        vm.addChapterBlockToBook(firstBook.bookBlockId)
        val updatedBook = vm.state.value.bookBlocks.first { it.bookBlockId == firstBook.bookBlockId }
        assertEquals(2, updatedBook.chapterBlocks.size)

        val secondChapterBlockId = updatedBook.chapterBlocks[1].blockId
        vm.removeChapterBlockFromBook(firstBook.bookBlockId, secondChapterBlockId)
        val finalBook = vm.state.value.bookBlocks.first { it.bookBlockId == firstBook.bookBlockId }
        assertEquals(1, finalBook.chapterBlocks.size)
    }

    @Test
    fun `test direct timing adjustment for individual book block`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = CreateStudyPlanViewModel(app)

        val bookId = vm.state.value.bookBlocks.first().bookBlockId
        assertEquals(3, vm.state.value.bookBlocks.first().periodCount)

        vm.incrementPeriodForBook(bookId)
        assertEquals(4, vm.state.value.bookBlocks.first().periodCount)

        vm.decrementPeriodForBook(bookId)
        vm.decrementPeriodForBook(bookId)
        assertEquals(2, vm.state.value.bookBlocks.first().periodCount)

        vm.setStudyDurationForBook(bookId, 60)
        assertEquals(60, vm.state.value.bookBlocks.first().studyDurationMinutes)

        vm.setBreakDurationForBook(bookId, 20)
        assertEquals(20, vm.state.value.bookBlocks.first().breakDurationMinutes)
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
    fun `test plan summary modal display and topic selection`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = CreateStudyPlanViewModel(app)

        assertFalse(vm.state.value.isSummaryModalVisible)

        // Without selecting any topic, summary modal is not opened
        vm.requestPlanSummary()
        assertFalse(vm.state.value.isSummaryModalVisible)

        val firstBook = vm.state.value.bookBlocks.first()
        val subjects = vm.state.value.getSubjectsForGrade(firstBook.selectedGrade)
        val firstSub = subjects.firstOrNull()
        val firstChapter = firstSub?.chapters?.firstOrNull()

        if (firstChapter != null) {
            val chBlockId = firstBook.chapterBlocks.first().blockId
            vm.selectChapterForBookBlock(firstBook.bookBlockId, chBlockId, firstChapter.id)

            val firstTopic = firstChapter.topics.firstOrNull()
            if (firstTopic != null) {
                vm.toggleTopicForBookBlock(firstBook.bookBlockId, chBlockId, firstTopic.id)
                vm.requestPlanSummary()
                assertTrue(vm.state.value.isSummaryModalVisible)

                vm.hideSummaryModal()
                assertFalse(vm.state.value.isSummaryModalVisible)
            }
        }
    }

    @Test
    fun `test add study session helper methods and state interactions`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = CreateStudyPlanViewModel(app)

        val firstBook = vm.state.value.bookBlocks.first()
        val initialPeriod = firstBook.periodCount

        vm.incrementCycleCount()
        assertEquals(initialPeriod + 1, vm.state.value.bookBlocks.first().periodCount)

        vm.decrementCycleCount()
        assertEquals(initialPeriod, vm.state.value.bookBlocks.first().periodCount)

        vm.updateStudyDuration(90)
        assertEquals(90, vm.state.value.bookBlocks.first().studyDurationMinutes)

        vm.updateRestDuration(30)
        assertEquals(30, vm.state.value.bookBlocks.first().breakDurationMinutes)

        val initialChapterCount = vm.state.value.bookBlocks.first().chapterBlocks.size
        vm.addChapterSection()
        assertEquals(initialChapterCount + 1, vm.state.value.bookBlocks.first().chapterBlocks.size)

        val secondChapterId = vm.state.value.bookBlocks.first().chapterBlocks[1].blockId
        vm.removeChapterSection(secondChapterId)
        assertEquals(initialChapterCount, vm.state.value.bookBlocks.first().chapterBlocks.size)
    }
}
