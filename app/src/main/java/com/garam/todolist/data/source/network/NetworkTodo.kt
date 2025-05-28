package com.garam.todolist.data.source.network

import com.google.firebase.Timestamp

data class NetworkTodo(
    val id : String = "",
    val categoryId : String? = null,
    val title : String = "",
    val startDate : String = "",
    val endDate : String = "",
    val repeatRule : String? = null,
    val status : Map<String,String>? = null,
    val priority : Boolean = false,
    val memo : String = "",
    val icon : String? = null,
    val color : String? = null,
    val startTime : String? = null,
    val index : Int? = null,
    val savedTime : Timestamp = Timestamp.now()
)