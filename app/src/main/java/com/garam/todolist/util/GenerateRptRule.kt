package com.garam.todolist.util.functions

fun generateRepeatRule(
    frequency: String,  // "DAILY", "WEEKLY", "MONTHLY_BY_WEEK", "MONTHLY_BY_DAY", "LAST_DAY_OF_MONTH"
    interval: Int = 1, // 기본적으로 1단위 반복
    daysOfWeek: List<String>? = null,  // ["MO", "WE", "FR"] 등
    weekNumbers: List<Int>? = null, // 특정 주차 (예: 2주차, 4주차)
    monthDays: List<Int>? = null, // 특정 날짜 (예: 5일, 15일)
    months: List<Int>? = null, // 연간 반복 (1~12월)
    count: Int? = null, // 반복 횟수 (옵션)
    until: String? = null // 종료일 (YYYYMMDD 형식, 옵션)
): String {
    val rule = mutableListOf("FREQ=$frequency", "INTERVAL=$interval")

    if (daysOfWeek != null && frequency == "WEEKLY") {
        rule.add("BYDAY=${daysOfWeek.joinToString(",")}")
    }

    if (weekNumbers != null && daysOfWeek != null && frequency == "MONTHLY") {
        rule.add("BYWEEKNO=${weekNumbers.joinToString(",")}")
        rule.add("BYDAY=${daysOfWeek.joinToString(",")}")
    }

    if (monthDays != null && frequency == "MONTHLY") {
        rule.add("BYMONTHDAY=${monthDays.joinToString(",")}")
    }

    if (frequency == "MONTHLY" && monthDays == null && weekNumbers == null) {
        rule.add("BYMONTHDAY=-1") // 마지막 날
    }

    if (months != null && frequency == "YEARLY") {
        rule.add("BYMONTH=${months.joinToString(",")}")
        if (monthDays != null) {
            rule.add("BYMONTHDAY=${monthDays.joinToString(",")}")
        }
    }

    if (count != null) {
        rule.add("COUNT=$count")
    }

    if (until != null) {
        rule.add("UNTIL=$until")
    }

    return rule.joinToString(";")
}

fun addUntilToRRule(rule: String, untilDate: String): String {
    val components = rule.split(";").filter { !it.startsWith("UNTIL=") }.toMutableList()
    components.add("UNTIL=${untilDate}")
    return components.joinToString(";")
}