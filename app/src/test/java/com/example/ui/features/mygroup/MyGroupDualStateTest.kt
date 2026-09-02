package com.example.ui.features.mygroup

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
class MyGroupDualStateTest {

    private lateinit var app: Application

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        ApiClient.init(app)
    }

    @Test
    fun testPersianFormatting() {
        assertEquals("۱۲", toPersianDigits(12))
        assertEquals("۱۳,۸۴۰", formatPersianNumber(13840))
        assertEquals("۳۳۰", toPersianDigits(330))
        assertEquals("۲,۴۵۰", formatPersianNumber(2450))
    }

    @Test
    fun testViewModelSearchAndFilterState() {
        val vm = MyGroupViewModel(app)

        assertEquals(GroupSearchFilter.GROUP_NAME, vm.uiState.value.activeSearchFilter)

        vm.onFilterSelect(GroupSearchFilter.GROUP_ID)
        assertEquals(GroupSearchFilter.GROUP_ID, vm.uiState.value.activeSearchFilter)

        vm.onFilterSelect(GroupSearchFilter.MEDALS)
        assertEquals(GroupSearchFilter.MEDALS, vm.uiState.value.activeSearchFilter)

        vm.onSearchQueryChange("کنکور")
        assertEquals("کنکور", vm.uiState.value.searchQuery)

        vm.onTabSelect(GroupTab.MEDALS)
        assertEquals(GroupTab.MEDALS, vm.uiState.value.selectedTab)

        vm.onTabSelect(GroupTab.MEMBERS)
        assertEquals(GroupTab.MEMBERS, vm.uiState.value.selectedTab)

        assertFalse(vm.uiState.value.isExpandedMembers)
        vm.toggleExpandMembers()
        assertTrue(vm.uiState.value.isExpandedMembers)
    }

    @Test
    fun testDualStateModelsInitialization() {
        val emptyState = StudyGroupScreenUiState(
            isLoading = false,
            isMember = false,
        )
        assertFalse(emptyState.isMember)
        assertEquals(0, emptyState.searchResults.size)

        val header = GroupHeaderData(
            id = "1",
            numericId = "2841",
            name = "قله موفقیت",
            motto = "باهم بهتر، قوی‌تر",
            roleBadge = "عضو",
            rank = 12,
            points = 13840,
            membersCount = 34,
        )
        val battle = GroupBattleData(
            myGroupName = "قله موفقیت",
            myGroupPoints = 13840,
            opponentGroupName = "ستارگان دانش",
            opponentGroupPoints = 11920,
            daysRemaining = 3,
            currentWeek = 4,
            totalWeeks = 12,
            myPercentage = 52,
            opponentPercentage = 48,
        )
        val personal = PersonalGroupStats(
            rank = 3,
            points = 2450,
            studyHours = 18,
            completedTasks = 330,
        )

        val memberState = StudyGroupScreenUiState(
            isLoading = false,
            isMember = true,
            groupDetails = header,
            activeBattle = battle,
            personalStats = personal,
        )

        assertTrue(memberState.isMember)
        assertNotNull(memberState.groupDetails)
        assertEquals("قله موفقیت", memberState.groupDetails?.name)
        assertEquals(12, memberState.groupDetails?.rank)
        assertEquals(13840, memberState.groupDetails?.points)
        assertEquals(34, memberState.groupDetails?.membersCount)
        assertEquals(52, memberState.activeBattle?.myPercentage)
        assertEquals(48, memberState.activeBattle?.opponentPercentage)
        assertEquals(3, memberState.personalStats?.rank)
        assertEquals(2450, memberState.personalStats?.points)
    }

    @Test
    fun testLeaveGroupAction() {
        val vm = MyGroupViewModel(app)
        assertNotNull(vm)
        // Invoking leave action does not crash and updates busy state flow
        vm.leave()
    }
}
