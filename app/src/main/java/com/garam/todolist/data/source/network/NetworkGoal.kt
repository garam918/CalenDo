package com.garam.todolist.data.source.network

import com.garam.todolist.data.GoalType

data class NetworkGoal(
    val goalId: String = "",
    val title: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val type: String = ""
)
