package com.garam.todolist.util.functions

import com.garam.todolist.data.Todo
import java.time.DayOfWeek
import java.time.LocalDate

fun parseRRule(rule: String): Map<String, String> = rule.split(";").associate {
    val (key, value) = it.split("=")
    key to value
}

fun isDateMatchingRule(date: LocalDate, rule: String): Boolean {
    val parsedRule = parseRRule(rule)
    val frequency = parsedRule["FREQ"] ?: return false
    val interval = parsedRule["INTERVAL"]?.toInt() ?: 1

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
                if (byMonthDay.contains(-1)) {
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
            return date.monthValue in byMonth && date.dayOfMonth in byMonthDay
        }
    }
    return false
}

fun filterTodosByDate(todos: List<Todo>, targetDate: LocalDate): List<Todo> = todos.filter { todo ->
//    if (todo.repeatRule == null || todo.repeatRule == "") todo.startDate == targetDate.toString()
//    else todo.repeatRule.let { isDateMatchingRule(targetDate, it) }

    val startDate = LocalDate.parse(todo.startDate)
    val endDate = LocalDate.parse(todo.endDate)
    if (targetDate.isBefore(startDate)) return@filter false
//    if (targetDate.isAfter(endDate)) return@filter false

    if (todo.repeatRule.isNullOrBlank()) {
        return@filter startDate == targetDate
    }

    if (endDate.isAfter(startDate) && targetDate.isAfter(endDate)) {
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