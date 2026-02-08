package com.garam.shared.util.functions

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

fun isDateInCurrentWeek(
    selectedDate: LocalDate,
    weekStartDate: LocalDate,
    startDayOfWeek: DayOfWeek = DayOfWeek.MONDAY
): Boolean {
    val startOfWeek = getStartOfWeek(weekStartDate, startDayOfWeek)
    val endOfWeek = startOfWeek.plus(DatePeriod(days = 6))
    return selectedDate >= startOfWeek && selectedDate <= endOfWeek
}

/**
 * 주 시작일 계산 (요일 기준으로)
 */
fun getStartOfWeek(date: LocalDate, startDayOfWeek: DayOfWeek): LocalDate {
    val currentDayOfWeek = date.dayOfWeek
    val diff = (currentDayOfWeek.ordinal - startDayOfWeek.ordinal + 7) % 7
    return date.minus(DatePeriod(days = diff))
}