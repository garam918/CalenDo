package com.garam.shared.data.source.network

import kotlinx.serialization.Serializable

@Serializable
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
    val savedTime : Long = 0L
)
