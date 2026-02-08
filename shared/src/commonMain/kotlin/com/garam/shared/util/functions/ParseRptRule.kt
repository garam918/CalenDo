package com.garam.shared.util.functions

import com.garam.shared.data.Todo
import com.kizitonwose.calendar.core.plusDays
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number


fun parseRRule(rule: String): Map<String, String> = rule.split(";").associate {
    val (key, value) = it.split("=")
    key to value
}

fun isDateMatchingRule(date: LocalDate, rule: String): Boolean {
    val parsedRule = parseRRule(rule)
    val frequency = parsedRule["FREQ"] ?: return false
    val interval = parsedRule["INTERVAL"]?.toInt() ?: 1
    val until = parsedRule["UNTIL"] ?: ""

    if(until != "") {
        if(date >(LocalDate.parse(until))) return false
    }



    when (frequency) {
        "DAILY" -> {
            return true
        }

        "WEEKLY" -> {
            val byDay = parsedRule["BYDAY"]?.split(",") ?: return false
            val dayOfWeek = date.dayOfWeek.toICalDay()
            return byDay.contains(dayOfWeek)
        }

        "MONTHLY" -> {
//            if (parsedRule.containsKey("BYMONTHDAY")) {
//                val byMonthDay = parsedRule["BYMONTHDAY"]!!.split(",").map { it.toInt() }
//                return byMonthDay.contains(date.dayOfMonth)
//            }
//            if (parsedRule["BYMONTHDAY"] == "-1") {
//                return date.plusDays(1).month != date.month // 마지막 날인지 확인
//            }
            val byMonthDay = parsedRule["BYMONTHDAY"]?.split(",")?.map { it.toInt() }

            if (byMonthDay != null) {
                // 마지막 날인지 확인
                if (byMonthDay.contains(32)) {
                    val isLastDay = date.plusDays(1).month != date.month
                    if (isLastDay) return true
                }

                return byMonthDay.contains(date.dayOfMonth)
            }
        }

        "YEARLY" -> {
            val byMonth = parsedRule["BYMONTH"]?.split(",")?.map { it.toInt() } ?: return false
            val byMonthDay =
                parsedRule["BYMONTHDAY"]?.split(",")?.map { it.toInt() } ?: return false
            return date.month.number in byMonth && date.dayOfMonth in byMonthDay
        }
    }
    return false
}

fun filterTodosByDate(todos: List<Todo>, targetDate: LocalDate): List<Todo> = todos.filter { todo ->
//    if (todo.repeatRule == null || todo.repeatRule == "") todo.startDate == targetDate.toString()
//    else todo.repeatRule.let { isDateMatchingRule(targetDate, it) }

    val startDate = LocalDate.parse(todo.startDate)
    val endDate = LocalDate.parse(todo.endDate)
    if (targetDate < startDate) return@filter false
//    if (targetDate.isAfter(endDate)) return@filter false

    if (todo.repeatRule.isNullOrBlank()) {
        return@filter startDate == targetDate
    }

    if (endDate > (startDate) && targetDate > (endDate)) {
        return@filter false
    }

    isDateMatchingRule(targetDate, todo.repeatRule)
}


fun DayOfWeek.toICalDay() = when (this) {
    DayOfWeek.MONDAY -> "MO"
    DayOfWeek.TUESDAY -> "TU"
    DayOfWeek.WEDNESDAY -> "WE"
    DayOfWeek.THURSDAY -> "TH"
    DayOfWeek.FRIDAY -> "FR"
    DayOfWeek.SATURDAY -> "SA"
    DayOfWeek.SUNDAY -> "SU"
}

fun DayOfWeek.toKorICalDay() = when (this) {
    DayOfWeek.MONDAY -> "월"
    DayOfWeek.TUESDAY -> "화"
    DayOfWeek.WEDNESDAY -> "수"
    DayOfWeek.THURSDAY -> "목"
    DayOfWeek.FRIDAY -> "금"
    DayOfWeek.SATURDAY -> "토"
    DayOfWeek.SUNDAY -> "일"
}

fun dayOfWeekKorToICalDay(dayOfWeek: String) = when(dayOfWeek) {
    "월" -> "MO"
    "화" -> "TU"
    "수" -> "WE"
    "목" -> "TH"
    "금" -> "FR"
    "토" -> "SA"
    "일" -> "SU"
    else -> "MO"
}