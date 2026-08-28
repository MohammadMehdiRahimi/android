package com.example.ui.features.exams

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExamsViewModelTest {

    private lateinit var viewModel: ExamsViewModel

    @Before
    fun setUp() {
        viewModel = ExamsViewModel()
    }

    @Test
    fun initialState_loadsAll12ExamsAndAvailableFilters() {
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(12, state.allExams.size)
        assertEquals(12, state.filteredExams.size)
        assertTrue(state.availableDates.isNotEmpty())
        assertTrue(state.availableSubjects.isNotEmpty())
        assertTrue(state.availableTopics.isNotEmpty())
        assertNull(state.selectedDate)
        assertNull(state.selectedSubject)
        assertNull(state.selectedTopic)
        assertNull(state.activeFilterType)
    }

    @Test
    fun selectSubject_filtersExamsCorrectly() {
        viewModel.selectSubject("ریاضی دهم")
        val state = viewModel.uiState.value
        assertEquals("ریاضی دهم", state.selectedSubject)
        assertEquals(2, state.filteredExams.size)
        assertTrue(state.filteredExams.all { it.subject == "ریاضی دهم" })
    }

    @Test
    fun selectDate_filtersExamsCorrectly() {
        viewModel.selectDate("۱۴۰۳/۰۳/۲۵")
        val state = viewModel.uiState.value
        assertEquals("۱۴۰۳/۰۳/۲۵", state.selectedDate)
        assertEquals(1, state.filteredExams.size)
        assertEquals("ریاضی دهم", state.filteredExams.first().subject)
        assertEquals("معادله و نامعادله", state.filteredExams.first().topic)
    }

    @Test
    fun selectTopic_filtersExamsCorrectly() {
        viewModel.selectTopic("ساختار اتم")
        val state = viewModel.uiState.value
        assertEquals("ساختار اتم", state.selectedTopic)
        assertEquals(1, state.filteredExams.size)
        assertEquals("شیمی دهم", state.filteredExams.first().subject)
    }

    @Test
    fun clearAllFilters_resetsFilteredExamsToAll() {
        viewModel.selectSubject("زیست دهم")
        assertEquals(2, viewModel.uiState.value.filteredExams.size)

        viewModel.clearAllFilters()
        val state = viewModel.uiState.value
        assertNull(state.selectedSubject)
        assertNull(state.selectedDate)
        assertNull(state.selectedTopic)
        assertEquals(12, state.filteredExams.size)
    }

    @Test
    fun openAndDismissFilterSheet_updatesActiveFilterType() {
        viewModel.openFilterSheet(FilterType.SUBJECT)
        assertEquals(FilterType.SUBJECT, viewModel.uiState.value.activeFilterType)

        viewModel.dismissFilterSheet()
        assertNull(viewModel.uiState.value.activeFilterType)
    }
}
