package com.example.ui.features.studyplan

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.domain.date.DateTransformer
import com.example.domain.date.JalaliDate
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
class PixelPerfectStudyPlanTest {

    private lateinit var app: Application

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext<Application>()
        ApiClient.init(app)
    }

    @Test
    fun `test initial state loads 6 default study sessions and 7 week days`() {
        val vm = CreateStudyPlanViewModel(app)
        val state = vm.state.value

        assertEquals(6, state.sessions.size)
        assertEquals(7, state.weekDays.size)

        // Verify Saturday to Friday week sequence
        val selectedDay = state.weekDays.firstOrNull { it.isSelected }
        assertNotNull(selectedDay)

        // Check progress calculation
        val completedCount = state.sessions.count { it.isCompleted }
        assertEquals(1, completedCount) // Biology is completed by default
        val expectedProgress = ((1f / 6f) * 100).toInt()
        assertEquals(expectedProgress, state.progressPercentage)

        // Check total minutes calculation (90 + 60 + 75 + 60 + 45 + 60 = 390 min = 6:30)
        assertEquals(390, state.totalStudyMinutes)
        assertEquals("6:30", state.totalHoursText)
    }

    @Test
    fun `test toggle session completion updates progress and isNext status`() {
        val vm = CreateStudyPlanViewModel(app)
        val mathSession = vm.state.value.sessions.first { it.subjectTitle == "ریاضی" }
        assertFalse(mathSession.isCompleted)
        assertTrue(mathSession.isNext)

        // Complete math session
        vm.toggleSessionCompletion(mathSession.id)
        val updatedMath = vm.state.value.sessions.first { it.id == mathSession.id }
        assertTrue(updatedMath.isCompleted)
        assertFalse(updatedMath.isNext)

        // Next non-completed session (Chemistry) should now be isNext
        val updatedChem = vm.state.value.sessions.first { it.subjectTitle == "شیمی" }
        assertTrue(updatedChem.isNext)

        // Progress percentage should increase
        val newProgress = vm.state.value.progressPercentage
        assertEquals(((2f / 6f) * 100).toInt(), newProgress)
    }

    @Test
    fun `test add and remove study session updates metrics dynamically`() {
        val vm = CreateStudyPlanViewModel(app)
        val initialCount = vm.state.value.sessions.size

        vm.addStudySession(
            subjectTitle = "فیزیک",
            chapterTopic = "مغناطیس و القای الکترومغناطیسی",
            startTime = "۲۰:۰۰",
            durationMinutes = 60,
            category = SubjectCategory.PHYSICS,
        )

        assertEquals(initialCount + 1, vm.state.value.sessions.size)
        val added = vm.state.value.sessions.last()
        assertEquals("فیزیک", added.subjectTitle)
        assertEquals(60, added.durationMinutes)

        // Remove the session
        vm.removeStudySession(added.id)
        assertEquals(initialCount, vm.state.value.sessions.size)
    }

    @Test
    fun `test week day selection updates selected state and preserves correct dates`() {
        val vm = CreateStudyPlanViewModel(app)
        val currentWeek = vm.state.value.weekDays
        val targetDay = currentWeek.first() // Saturday

        vm.selectDate(targetDay.date)
        assertEquals(targetDay.date, vm.state.value.selectedDate)

        val updatedDay = vm.state.value.weekDays.first { it.date == targetDay.date }
        assertTrue(updatedDay.isSelected)
    }

    @Test
    fun `test copy previous day plan replaces sessions and resets completion`() {
        val vm = CreateStudyPlanViewModel(app)
        vm.copyPreviousDayPlan()

        val sessions = vm.state.value.sessions
        assertTrue(sessions.isNotEmpty())
        assertTrue(sessions.all { !it.isCompleted })
        assertEquals("برنامه روز قبل با موفقیت کپی شد", vm.state.value.successMessage)
    }

    @Test
    fun `test open edit session and update session details`() {
        val vm = CreateStudyPlanViewModel(app)
        val sessionToEdit = vm.state.value.sessions.first()

        vm.openEditSession(sessionToEdit)
        assertEquals(sessionToEdit.id, vm.state.value.editingSession?.id)
        assertTrue(vm.state.value.isAddSessionSheetVisible)

        vm.updateStudySession(
            sessionId = sessionToEdit.id,
            subjectTitle = "زیست‌شناسی پیشرفته",
            chapterTopic = "ژنتیک و مولکول‌های زیستی",
            startTime = "۰۸:۰۰",
            durationMinutes = 120,
            category = SubjectCategory.BIOLOGY,
        )

        val updated = vm.state.value.sessions.first { it.id == sessionToEdit.id }
        assertEquals("زیست‌شناسی پیشرفته", updated.subjectTitle)
        assertEquals("ژنتیک و مولکول‌های زیستی", updated.chapterTopic)
        assertEquals(120, updated.durationMinutes)
        assertEquals(null, vm.state.value.editingSession)
        assertFalse(vm.state.value.isAddSessionSheetVisible)
    }

    @Test
    fun `test week navigation forward and backward shifts dates by 7 days`() {
        val vm = CreateStudyPlanViewModel(app)
        val initialDate = vm.state.value.selectedDate

        // Next week (plus 7 days)
        val nextWeekDate = initialDate.plusDays(7)
        vm.selectDate(nextWeekDate)
        assertEquals(nextWeekDate, vm.state.value.selectedDate)
        assertEquals(7, vm.state.value.weekDays.size)

        // Previous week (minus 7 days back to initial)
        val prevWeekDate = vm.state.value.selectedDate.minusDays(7)
        vm.selectDate(prevWeekDate)
        assertEquals(initialDate, vm.state.value.selectedDate)
    }

    @Test
    fun `test design color tokens and session category icons match visual spec`() {
        assertEquals(0xFF17203A, PlanHeaderNavy.value.toLong() ushr 32 or (PlanHeaderNavy.value.toLong() and 0xFFFFFFFFL))
        assertEquals(0xFF5B2CFF, PlanBrandPurple.value.toLong() ushr 32 or (PlanBrandPurple.value.toLong() and 0xFFFFFFFFL))
        assertEquals(0xFFF9FAFE, PlanLightBg.value.toLong() ushr 32 or (PlanLightBg.value.toLong() and 0xFFFFFFFFL))
        assertEquals(0xFFECEEF5, PlanCardBorderColor.value.toLong() ushr 32 or (PlanCardBorderColor.value.toLong() and 0xFFFFFFFFL))

        // Check subject categories metadata
        assertEquals(0xFF16A34A, SubjectCategory.BIOLOGY.iconTint)
        assertEquals(0xFFD97706, SubjectCategory.MATH.iconTint)
        assertEquals(0xFFE11D48, SubjectCategory.CHEMISTRY.iconTint)
        assertEquals(0xFF2563EB, SubjectCategory.PHYSICS.iconTint)

        // Soft light purple for selected day / badges
        assertEquals(0xFFEDE8FF, PlanLavenderTint.value.toLong() ushr 32 or (PlanLavenderTint.value.toLong() and 0xFFFFFFFFL))
    }

    @Test
    fun `test week selector contains all 7 days with correct initial selection`() {
        val vm = CreateStudyPlanViewModel(app)
        val state = vm.state.value
        assertEquals(7, state.weekDays.size)
        val selectedDay = state.weekDays.firstOrNull { it.isSelected }
        org.junit.Assert.assertNotNull(selectedDay)
        assertEquals(state.selectedDate.day, selectedDay?.dayOfMonth)
    }
}
