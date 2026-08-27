package com.example.domain.date

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class JalaliDateTest {

    @Test
    fun `test gregorian to jalali conversion for known dates`() {
        // 2026-03-21 -> 1405-01-01 (Nowruz)
        val nowruz = JalaliDate.fromGregorian(LocalDate.of(2026, 3, 21))
        assertEquals(1405, nowruz.year)
        assertEquals(1, nowruz.month)
        assertEquals(1, nowruz.day)
        assertEquals("فروردین", nowruz.monthName)

        // 2026-08-25 -> 1405-06-03
        val date1 = JalaliDate.fromGregorian(LocalDate.of(2026, 8, 25))
        assertEquals(1405, date1.year)
        assertEquals(6, date1.month)
        assertEquals(3, date1.day)
        assertEquals("شهریور", date1.monthName)
    }

    @Test
    fun `test jalali to gregorian bidirectional conversion`() {
        val originalGregorian = LocalDate.of(2026, 8, 25)
        val jalali = JalaliDate.fromGregorian(originalGregorian)
        val convertedBack = jalali.toGregorian()

        assertEquals(originalGregorian, convertedBack)
    }

    @Test
    fun `test plus and minus days`() {
        val base = JalaliDate(1405, 6, 3)
        val nextDay = base.plusDays(1)
        assertEquals(1405, nextDay.year)
        assertEquals(6, nextDay.month)
        assertEquals(4, nextDay.day)

        val prevDay = base.minusDays(1)
        assertEquals(1405, nextDay.year)
        assertEquals(6, prevDay.month)
        assertEquals(2, prevDay.day)
    }

    @Test
    fun `test jalali date formatting`() {
        val date = JalaliDate(1405, 6, 3)
        assertEquals("1405/06/03", date.formatPersianNumeric())
        assertEquals("3 شهریور 1405", date.formatPersianText())
    }

    @Test
    fun `test date transformer week calendar generator`() {
        val base = JalaliDate(1405, 6, 3)
        val weekDays = DateTransformer.generateWeekCalendarDays(base, rangeDaysBefore = 3, rangeDaysAfter = 3)

        assertEquals(7, weekDays.size)
        val selectedItem = weekDays.first { it.isSelected }
        assertEquals(base, selectedItem.jalaliDate)
    }
}
