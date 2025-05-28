package com.garam.todolist.util.functions

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun getWeekStartEnd(dateStr: String): Pair<String, String> {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val calendar = Calendar.getInstance()

    // 선택한 날짜 설정
    calendar.time = sdf.parse(dateStr) ?: Date()

    // 해당 주의 시작일 (월요일로 설정)
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    val startOfWeek = sdf.format(calendar.time)

    // 해당 주의 종료일 (일요일로 설정)
    calendar.add(Calendar.DATE, 6)
    val endOfWeek = sdf.format(calendar.time)

    return Pair(startOfWeek, endOfWeek)
}


fun getMonthStartAndEnd(selectedDate: String): Pair<String, String> {
    val yearMonth = YearMonth.from(LocalDate.parse(selectedDate))
    val startOfMonth = yearMonth.atDay(1)  // 월 시작 (1일)
    val endOfMonth = yearMonth.atEndOfMonth() // 월 종료 (마지막 날)
    return startOfMonth.toString() to endOfMonth.toString()
}