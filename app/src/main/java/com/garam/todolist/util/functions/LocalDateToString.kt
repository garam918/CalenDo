package com.garam.todolist.util.functions

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

fun localDateToString(date: LocalDate) = date.format(DateTimeFormatter.ofPattern("yyyy년 M월"))

fun monthToString(month: YearMonth) = month.format(DateTimeFormatter.ofPattern("yyyy년 M월"))

fun localDateToDateString(date: LocalDate) = date.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 EEE", Locale.KOREAN))

fun localDateToDateStringForDialog(date: LocalDate) = date.format(DateTimeFormatter.ofPattern("M월 d일", Locale.KOREAN))