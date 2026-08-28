package com.example.ui.features.leaderboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CompetitiveLeagueTest {

    @Test
    fun testCurrentUserLeagueInfo() {
        val userInfo = LeagueSampleData.currentUser
        assertEquals("محمد امین", userInfo.name)
        assertEquals("لیگ طلایی", userInfo.leagueName)
        assertEquals("فصل شهریور", userInfo.seasonName)
        assertEquals(12, userInfo.rank)
        assertEquals(6420, userInfo.score)
        assertEquals(180, userInfo.scoreToNextRank)
        assertEquals(6600, userInfo.requiredPointsForNextTier)
    }

    @Test
    fun testTop3PodiumRanks() {
        val top3 = LeagueSampleData.top3Members
        assertEquals(3, top3.size)
        assertEquals(1, top3[0].rank)
        assertEquals("سینا قربانی", top3[0].name)
        assertEquals(8930, top3[0].score)
        assertEquals(LeagueBadgeType.GOLD_SHIELD, top3[0].badgeType)

        assertEquals(2, top3[1].rank)
        assertEquals("نگار رحیمی", top3[1].name)
        assertEquals(8210, top3[1].score)
        assertEquals(LeagueBadgeType.SILVER_SHIELD, top3[1].badgeType)

        assertEquals(3, top3[2].rank)
        assertEquals("پارسا احمدی", top3[2].name)
        assertEquals(7280, top3[2].score)
        assertEquals(LeagueBadgeType.BRONZE_SHIELD, top3[2].badgeType)
    }

    @Test
    fun testAllMembersListContainsCurrentUser() {
        val allMembers = LeagueSampleData.allMembers
        assertTrue(allMembers.isNotEmpty())
        val currentUser = allMembers.find { it.isCurrentUser }
        assertNotNull(currentUser)
        assertEquals(12, currentUser?.rank)
        assertEquals("محمد امین", currentUser?.name)
        assertEquals(6420, currentUser?.score)
    }

    @Test
    fun testPersianConversion() {
        assertEquals("۱۲", 12.toPersianString())
        assertEquals("۶۴۲۰", 6420.toPersianString())
        assertEquals("۸۹۳۰", 8930.toPersianString())
    }
}
