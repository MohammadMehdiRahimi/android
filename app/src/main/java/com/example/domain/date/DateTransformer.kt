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
            DayOfWeek.THURSDAY -> "پنج‌شنبه"
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
