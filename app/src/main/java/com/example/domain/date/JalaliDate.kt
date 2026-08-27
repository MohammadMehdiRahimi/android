package com.example.domain.date

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Immutable data class representing a Persian (Jalali / Solar Hijri) date.
 */
data class JalaliDate(
    val year: Int,
    val month: Int,
    val day: Int,
) : Comparable<JalaliDate> {

    val monthName: String
        get() = when (month) {
            1 -> "فروردین"
            2 -> "اردیبهشت"
            3 -> "خرداد"
            4 -> "تیر"
            5 -> "مرداد"
            6 -> "شهریور"
            7 -> "مهر"
            8 -> "آبان"
            9 -> "آذر"
            10 -> "دی"
            11 -> "بهمن"
            12 -> "اسفند"
            else -> ""
        }

    val isLeapYear: Boolean
        get() = isJalaliLeapYear(year)

    val daysInMonth: Int
        get() = when {
            month in 1..6 -> 31
            month in 7..11 -> 30
            month == 12 -> if (isLeapYear) 30 else 29
            else -> 30
        }

    fun toGregorian(): LocalDate {
        return jalaliToGregorian(year, month, day)
    }

    fun formatPersianNumeric(): String {
        return "%04d/%02d/%02d".format(year, month, day)
    }

    fun formatPersianText(): String {
        return "$day $monthName $year"
    }

    fun plusDays(days: Long): JalaliDate {
        return fromGregorian(toGregorian().plusDays(days))
    }

    fun minusDays(days: Long): JalaliDate {
        return fromGregorian(toGregorian().minusDays(days))
    }

    override fun compareTo(other: JalaliDate): Int {
        if (year != other.year) return year.compareTo(other.year)
        if (month != other.month) return month.compareTo(other.month)
        return day.compareTo(other.day)
    }

    override fun toString(): String {
        return formatPersianNumeric()
    }

    companion object {
        fun now(): JalaliDate {
            return fromGregorian(LocalDate.now())
        }

        fun fromGregorian(localDate: LocalDate): JalaliDate {
            return gregorianToJalali(localDate.year, localDate.monthValue, localDate.dayOfMonth)
        }

        fun fromIso(isoDate: String): JalaliDate {
            val parsed = LocalDate.parse(isoDate.trim(), DateTimeFormatter.ISO_LOCAL_DATE)
            return fromGregorian(parsed)
        }

        fun isJalaliLeapYear(jYear: Int): Boolean {
            val r = (jYear - (if (jYear > 0) 474 else 473)) % 2820 + 474 + 38
            return ((r * 682) % 2816) < 682
        }

        private val GREGORIAN_DAYS_IN_MONTH = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

        fun gregorianToJalali(gYear: Int, gMonth: Int, gDay: Int): JalaliDate {
            val gy = gYear - 1600
            val gm = gMonth - 1
            val gd = gDay - 1

            var gDayNo = 365L * gy + (gy + 3) / 4 - (gy + 99) / 100 + (gy + 399) / 400

            for (i in 0 until gm) {
                gDayNo += GREGORIAN_DAYS_IN_MONTH[i]
            }
            if (gm > 1 && ((gYear % 4 == 0 && gYear % 100 != 0) || (gYear % 400 == 0))) {
                gDayNo++
            }
            gDayNo += gd

            var jDayNo = gDayNo - 79
            val jNp = jDayNo / 12053
            jDayNo %= 12053

            var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
            jDayNo %= 1461

            if (jDayNo >= 366) {
                jy += (jDayNo - 1) / 365
                jDayNo = (jDayNo - 1) % 365
            }

            var jm = 0
            var jd = 0
            if (jDayNo < 186) {
                jm = 1 + (jDayNo / 31).toInt()
                jd = 1 + (jDayNo % 31).toInt()
            } else {
                val rem = jDayNo - 186
                jm = 7 + (rem / 30).toInt()
                jd = 1 + (rem % 30).toInt()
            }

            return JalaliDate(jy.toInt(), jm, jd)
        }

        fun jalaliToGregorian(jYear: Int, jMonth: Int, jDay: Int): LocalDate {
            val jy = jYear - 979
            val jm = jMonth - 1
            val jd = jDay - 1

            var jDayNo = 365L * jy + (jy / 33) * 8 + ((jy % 33) + 3) / 4
            for (i in 0 until jm) {
                jDayNo += if (i < 6) 31 else 30
            }
            jDayNo += jd

            var gDayNo = jDayNo + 79

            var gy = 1600 + 400 * (gDayNo / 146097)
            gDayNo %= 146097

            var leap = true
            if (gDayNo >= 36525) {
                gDayNo--
                gy += 100 * (gDayNo / 36524)
                gDayNo %= 36524

                if (gDayNo >= 365) {
                    gDayNo++
                } else {
                    leap = false
                }
            }

            gy += 4 * (gDayNo / 1461)
            gDayNo %= 1461

            if (gDayNo >= 366) {
                leap = false
                gDayNo--
                gy += gDayNo / 365
                gDayNo %= 365
            }

            val leap4 = gy % 4L == 0L
            val leap100 = gy % 100L != 0L
            val leap400 = gy % 400L == 0L
            val isLeapYear = (leap4 && leap100) || leap400

            val gDaysInMonthWithLeap = intArrayOf(
                31,
                if (leap && isLeapYear) 29 else 28,
                31, 30, 31, 30, 31, 31, 30, 31, 30, 31
            )

            var gm = 0
            while (gm < 12 && gDayNo >= gDaysInMonthWithLeap[gm]) {
                gDayNo -= gDaysInMonthWithLeap[gm]
                gm++
            }

            val gd = gDayNo.toInt() + 1
            return LocalDate.of(gy.toInt(), gm + 1, gd)
        }
    }
}
