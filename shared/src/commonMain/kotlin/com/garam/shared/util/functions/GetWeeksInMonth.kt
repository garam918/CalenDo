package com.garam.shared.util.functions

import com.kizitonwose.calendar.core.deprecated.atEndOfMonth
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.number

fun getWeeksInMonth(date: LocalDate, startFromSunday: Boolean): Int {
    // 1. 해당 월의 1일과 마지막 날 구하기
    val firstDayOfMonth = LocalDate(date.year, date.month, 1)

    val nextMonthYear = if (date.monthNumber == 12) date.year + 1 else date.year
    val nextMonth = if (date.monthNumber == 12) 1 else date.monthNumber + 1
    val lastDayOfMonth = LocalDate(nextMonthYear, nextMonth, 1).minus(1, DateTimeUnit.DAY)

    val totalDaysInMonth = lastDayOfMonth.day

    // 2. 1일의 요일 가져오기 (Monday=1, ..., Sunday=7)
    val firstDayDayOfWeek = firstDayOfMonth.dayOfWeek.isoDayNumber

    // 3. 시작 요일에 따른 앞쪽 빈칸(leadingDays) 계산
    val leadingDays = if (startFromSunday) {
        // 일요일 시작: 일(0), 월(1), 화(2), 수(3), 목(4), 금(5), 토(6)
        if (firstDayDayOfWeek == 7) 0 else firstDayDayOfWeek
    } else {
        // 월요일 시작: 월(0), 화(1), 수(2), 목(3), 금(4), 토(5), 일(6)
        firstDayDayOfWeek - 1
    }

    // 4. 전체 칸 수(빈칸 + 실제 날짜)를 7로 나누어 주차 계산
    return (leadingDays + totalDaysInMonth + 6) / 7
}