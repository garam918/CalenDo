package com.garam.todolist.util.functions

import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

fun dateToString(date: LocalDate, repeatType: String): String = when (repeatType) {
    "WEEKLY" -> {
        val dayName = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
        "매주 $dayName"
    }
    "MONTHLY" -> "매월 ${date.dayOfMonth}일"
    "YEARLY" -> "매년 ${date.monthValue}월 ${date.dayOfMonth}일"
    else -> {
        val dayName = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
        "매주 $dayName"
    }
}
