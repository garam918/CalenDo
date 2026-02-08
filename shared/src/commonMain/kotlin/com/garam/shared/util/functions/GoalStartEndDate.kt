package com.garam.shared.util.functions

import kotlinx.datetime.*

fun getWeekStartEnd(dateStr: String): Pair<String, String> {
    // 1. 문자열을 LocalDate 객체로 파싱합니다.
    // "yyyy-MM-dd"는 kotlinx-datetime의 기본 ISO 형식이므로 추가 포매터가 필요 없습니다.
    val date = LocalDate.parse(dateStr)

    // 2. 해당 주의 월요일을 찾습니다.
    // DayOfWeek.isoDayNumber는 월요일(1)부터 일요일(7)까지의 숫자를 반환합니다.
    // (오늘 요일 숫자 - 월요일 숫자)만큼 날짜를 빼면 월요일이 됩니다.
    val daysToSubtract = date.dayOfWeek.isoDayNumber - DayOfWeek.MONDAY.isoDayNumber
    val startOfWeek = date.minus(daysToSubtract, DateTimeUnit.DAY)

    // 3. 해당 주의 일요일을 찾습니다. (월요일로부터 6일 뒤)
    val endOfWeek = startOfWeek.plus(6, DateTimeUnit.DAY)

    // 4. LocalDate.toString()은 기본적으로 "yyyy-MM-dd" 형식을 반환합니다.
    return Pair(startOfWeek.toString(), endOfWeek.toString())
}

/**
 * 주어진 날짜 문자열(yyyy-MM-dd)을 기준으로
 * 해당 월의 시작일(1일)과 종료일(마지막 날)을 반환합니다.
 */
fun getMonthStartAndEnd(selectedDate: String): Pair<String, String> {
    // 1. 문자열을 LocalDate 객체로 파싱합니다.
    val date = LocalDate.parse(selectedDate)

    // 2. 해당 월의 시작일(1일)을 생성합니다.
    val startOfMonth = LocalDate(date.year, date.month, 1)

    // 3. 해당 월의 종료일을 계산합니다.
    // (다음 달 1일에서 하루를 빼는 방식으로 계산합니다)
    val startOfNextMonth = startOfMonth.plus(1, DateTimeUnit.MONTH)
    val endOfMonth = startOfNextMonth.minus(1, DateTimeUnit.DAY)

    // 4. "yyyy-MM-dd" 형식의 문자열로 반환합니다.
    return startOfMonth.toString() to endOfMonth.toString()
}