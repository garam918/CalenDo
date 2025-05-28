package com.garam.todolist.util.functions

import com.garam.todolist.data.TodoMonthlyRptDayClass
import java.time.YearMonth

fun monthToDateList(month: YearMonth, selectedList : MutableList<Int>) : MutableList<TodoMonthlyRptDayClass> =
    (1..month.lengthOfMonth()).map { day ->
    TodoMonthlyRptDayClass(day.toString(), selectedList.contains(day))
}.toMutableList()