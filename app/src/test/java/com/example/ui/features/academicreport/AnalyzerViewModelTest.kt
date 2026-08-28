package com.example.ui.features.academicreport

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AnalyzerViewModelTest {

    private lateinit var viewModel: AnalyzerViewModel

    @Before
    fun setUp() {
        val application = ApplicationProvider.getApplicationContext<Application>()
        viewModel = AnalyzerViewModel(application)
    }

    @Test
    fun initialState_hasDefaultLastWeekTimeframeAndMetrics() {
        val state = viewModel.uiState.value
        assertEquals(AnalyzerTimeframe.LAST_WEEK, state.selectedTimeframe)
        assertEquals(4, state.metrics.size)
        assertEquals(486, state.metrics.first { it.title == "تعداد تست" }.value)
        assertEquals(158, state.metrics.first { it.title == "تست غلط" }.value)
        assertEquals(328, state.metrics.first { it.title == "تست صحیح" }.value)
        assertEquals(6, state.metrics.first { it.title == "تعداد آزمون" }.value)
        assertEquals(3, state.weaknesses.size)
        assertEquals(3, state.strengths.size)
        assertEquals(6, state.studyDistribution.size)
    }

    @Test
    fun selectTimeframe_updatesStateToMonthAnd3Months() {
        viewModel.selectTimeframe(AnalyzerTimeframe.LAST_MONTH)
        var state = viewModel.uiState.value
        assertEquals(AnalyzerTimeframe.LAST_MONTH, state.selectedTimeframe)
        assertEquals(1960, state.metrics.first { it.title == "تعداد تست" }.value)
        assertTrue(state.aiInsightParagraphs.isNotEmpty())

        viewModel.selectTimeframe(AnalyzerTimeframe.LAST_3_MONTHS)
        state = viewModel.uiState.value
        assertEquals(AnalyzerTimeframe.LAST_3_MONTHS, state.selectedTimeframe)
        assertEquals(6100, state.metrics.first { it.title == "تعداد تست" }.value)
    }

    @Test
    fun studyDistribution_hasPeakSlot() {
        val state = viewModel.uiState.value
        val peakSlot = state.studyDistribution.find { it.isPeak }
        assertNotNull(peakSlot)
        assertEquals("۱۲-۱۶", peakSlot?.timeSlot)
        assertEquals(3.0f, peakSlot?.hours)
    }
}
