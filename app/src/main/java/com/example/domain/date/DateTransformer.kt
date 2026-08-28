package com.example.domain.date

import com.example.ui.core.toPersianNumber
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId

data class CalendarDayItem(
    val jalaliDate: JalaliDate,
    val gregorianDate: LocalDate,
    val isoDateString: String,
    val dayOfWeekName: String,
    val dayOfWeekShort: String,
    val dayNumberPersian: String,
    val monthName: String,
    val isToday: Boolean,
    val isSelected: Boolean,
)

data class CalendarMonthGridItem(
    val jalaliDate: JalaliDate,
    val dayNumberPersian: String,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val isSelected: Boolean,
)

object DateTransformer {

    private val TEHRAN_ZONE = ZoneId.of("Asia/Tehran")

    fun getTodayJalali(): JalaliDate = JalaliDate.fromGregorian(LocalDate.now(TEHRAN_ZONE))

    fun getTodayGregorian(): LocalDate = LocalDate.now(TEHRAN_ZONE)

    fun toGregorianIso(jalaliDate: JalaliDate): String {
        return jalaliDate.toGregorian().toString()
    }

    fun fromGregorianIsoToJalali(isoDate: String): JalaliDate {
        return JalaliDate.fromIso(isoDate)
    }

    fun getPersianDayOfWeekName(localDate: LocalDate): String {
        return when (localDate.dayOfWeek) {
            DayOfWeek.SATURDAY -> "شنبه"
            DayOfWeek.SUNDAY -> "یکشنبه"
            DayOfWeek.MONDAY -> "دوشنبه"
            DayOfWeek.TUESDAY -> "سه‌شنبه"
            DayOfWeek.WEDNESDAY -> "چهارشنبه"
            DayOfWeek.THURSDAY -> "پنجشنبه"
            DayOfWeek.FRIDAY -> "جمعه"
            null -> ""
        }
    }

    fun getPersianDayOfWeekShort(localDate: LocalDate): String {
        return when (localDate.dayOfWeek) {
            DayOfWeek.SATURDAY -> "ش"
            DayOfWeek.SUNDAY -> "ی"
            DayOfWeek.MONDAY -> "د"
            DayOfWeek.TUESDAY -> "س"
            DayOfWeek.WEDNESDAY -> "چ"
            DayOfWeek.THURSDAY -> "پ"
            DayOfWeek.FRIDAY -> "ج"
            null -> ""
        }
    }

    fun getJalaliWeekdayIndex(localDate: LocalDate): Int {
        return when (localDate.dayOfWeek) {
            DayOfWeek.SATURDAY -> 0
            DayOfWeek.SUNDAY -> 1
            DayOfWeek.MONDAY -> 2
            DayOfWeek.TUESDAY -> 3
            DayOfWeek.WEDNESDAY -> 4
            DayOfWeek.THURSDAY -> 5
            DayOfWeek.FRIDAY -> 6
            null -> 0
        }
    }

    fun formatHeaderTitle(jalaliDate: JalaliDate, today: JalaliDate = getTodayJalali()): String {
        val diffDays = java.time.temporal.ChronoUnit.DAYS.between(
            today.toGregorian(),
            jalaliDate.toGregorian()
        )
        val relativePrefix = when (diffDays) {
            0L -> "امروز، "
            -1L -> "دیروز، "
            1L -> "فردا، "
            else -> ""
        }
        val dayOfWeek = getPersianDayOfWeekName(jalaliDate.toGregorian())
        return "$relativePrefix$dayOfWeek ${jalaliDate.day.toPersianNumber()} ${jalaliDate.monthName}"
    }

    fun formatFullPersianDate(date: JalaliDate): String {
        val dayOfWeek = getPersianDayOfWeekName(date.toGregorian())
        return "$dayOfWeek ${date.day.toPersianNumber()} ${date.monthName} ${date.year.toPersianNumber()}"
    }

    fun generateMonthCalendarDays(
        year: Int,
        month: Int,
        selectedDate: JalaliDate,
    ): List<CalendarMonthGridItem> {
        val today = getTodayJalali()
        val firstDay = JalaliDate(year, month, 1)
        val firstDayWeekday = getJalaliWeekdayIndex(firstDay.toGregorian())
        val daysInCurrent = firstDay.daysInMonth

        val prevMonth = if (month == 1) 12 else month - 1
        val prevYear = if (month == 1) year - 1 else year
        val prevMonthDays = JalaliDate(prevYear, prevMonth, 1).daysInMonth

        val nextMonth = if (month == 12) 1 else month + 1
        val nextYear = if (month == 12) year + 1 else year

        val items = mutableListOf<CalendarMonthGridItem>()

        // 1. Leading days from previous month
        for (i in 0 until firstDayWeekday) {
            val dayNum = prevMonthDays - firstDayWeekday + 1 + i
            val date = JalaliDate(prevYear, prevMonth, dayNum)
            items.add(
                CalendarMonthGridItem(
                    jalaliDate = date,
                    dayNumberPersian = dayNum.toPersianNumber(),
                    isCurrentMonth = false,
                    isToday = date == today,
                    isSelected = date == selectedDate,
                )
            )
        }

        // 2. Days of current month
        for (d in 1..daysInCurrent) {
            val date = JalaliDate(year, month, d)
            items.add(
                CalendarMonthGridItem(
                    jalaliDate = date,
                    dayNumberPersian = d.toPersianNumber(),
                    isCurrentMonth = true,
                    isToday = date == today,
                    isSelected = date == selectedDate,
                )
            )
        }

        // 3. Trailing days from next month to complete the week rows (either 35 or 42 cells)
        val totalCells = ((items.size + 6) / 7) * 7
        val trailingCount = totalCells - items.size
        for (d in 1..trailingCount) {
            val date = JalaliDate(nextYear, nextMonth, d)
            items.add(
                CalendarMonthGridItem(
                    jalaliDate = date,
                    dayNumberPersian = d.toPersianNumber(),
                    isCurrentMonth = false,
                    isToday = date == today,
                    isSelected = date == selectedDate,
                )
            )
        }

        return items
    }

    fun generateWeekCalendarDays(
        selectedJalali: JalaliDate,
        rangeDaysBefore: Int = 3,
        rangeDaysAfter: Int = 3,
    ): List<CalendarDayItem> {
        val today = getTodayJalali()
        val items = mutableListOf<CalendarDayItem>()
        for (offset in -rangeDaysBefore..rangeDaysAfter) {
            val date = selectedJalali.plusDays(offset.toLong())
            val gDate = date.toGregorian()
            items.add(
                CalendarDayItem(
                    jalaliDate = date,
                    gregorianDate = gDate,
                    isoDateString = gDate.toString(),
                    dayOfWeekName = getPersianDayOfWeekName(gDate),
                    dayOfWeekShort = getPersianDayOfWeekShort(gDate),
                    dayNumberPersian = date.day.toPersianNumber(),
                    monthName = date.monthName,
                    isToday = date == today,
                    isSelected = date == selectedJalali,
                )
            )
        }
        return items
    }
}
