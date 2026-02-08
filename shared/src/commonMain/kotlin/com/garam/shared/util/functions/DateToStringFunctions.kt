package com.garam.shared.util.functions

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.format
import kotlinx.datetime.number

fun localDateToString(date: LocalDate) = "${date.year}년 ${date.month}월"

fun monthToString(month: YearMonth) = "${month.year}년 ${month.month.number}월"

fun localDateToDateString(date: LocalDate): String {
    val dayOfWeek = date.dayOfWeek.toKoreanShortName()
    return "${date.year}년 ${date.month.number}월 ${date.day}일 $dayOfWeek"
}

fun localDateToDateStringForDialog(date: LocalDate): String {
    return "${date.month.number}월 ${date.day}일"
}

fun localDateToWidgetDateString(date: LocalDate): String {
    val dayOfWeek = date.dayOfWeek.toKoreanShortName()
    return "${date.month.number}월 ${date.day}일 $dayOfWeek"
}

fun dateToString(date: LocalDate, repeatType: String): String = when (repeatType) {
    "WEEKLY" -> {
        val dayName = date.dayOfWeek.toKoreanShortName()
        "매주 $dayName"
    }
    "MONTHLY" -> "매월 ${date.day}일"
    "YEARLY" -> "매년 ${date.month.number}월 ${date.day}일"
    else -> {
        val dayName = date.dayOfWeek.toKoreanShortName()
        "매주 $dayName"
    }
}

private fun DayOfWeek.toKoreanShortName(): String = when (this) {
    DayOfWeek.MONDAY -> "월"
    DayOfWeek.TUESDAY -> "화"
    DayOfWeek.WEDNESDAY -> "수"
    DayOfWeek.THURSDAY -> "목"
    DayOfWeek.FRIDAY -> "금"
    DayOfWeek.SATURDAY -> "토"
    DayOfWeek.SUNDAY -> "일"
}