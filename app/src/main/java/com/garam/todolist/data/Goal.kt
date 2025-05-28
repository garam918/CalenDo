package com.garam.todolist.data

data class Goal(
    val goalId: String,
    val title: String,
    val startDate: String,
    val endDate: String,
    val type: GoalType
)

enum class GoalType {
    WEEKLY,  // 이번 주 목표
    MONTHLY  // 이번 달 목표
}