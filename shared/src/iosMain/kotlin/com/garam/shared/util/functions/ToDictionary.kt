package com.garam.shared.util.functions

import com.garam.shared.data.source.network.NetworkCategory
import com.garam.shared.data.source.network.NetworkGoal
import com.garam.shared.data.source.network.NetworkTodo
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.serialization.json.Json
import platform.Foundation.NSDictionary
import platform.Foundation.NSJSONReadingMutableContainers
import platform.Foundation.NSJSONSerialization
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.dataUsingEncoding

@OptIn(ExperimentalForeignApi::class)
fun Any.toNSDictionary(): NSDictionary? {
    return try {
        val json = Json.encodeToString(this)
        val data = (json as NSString).dataUsingEncoding(NSUTF8StringEncoding)
        val nsDict = NSJSONSerialization.JSONObjectWithData(
            data!!,
            NSJSONReadingMutableContainers,
            null
        ) as? NSDictionary
        nsDict
    } catch (e: Exception) {
        println("❌ JSON → NSDictionary 변환 실패: ${e.message}")
        null
    }
}

fun NetworkTodo.toMap(): Map<Any?, *> = mapOf(
    "id" to id,
    "title" to title,
    "categoryId" to categoryId,
    "startDate" to startDate,
    "endDate" to endDate,
    "repeatRule" to repeatRule,
    "status" to status,
    "priority" to priority,
    "memo" to memo,
    "icon" to icon,
    "color" to color,
    "startTime" to startTime,
    "index" to index,
    "savedTime" to savedTime
)

fun NetworkCategory.toMap() : Map<Any?, *> = mapOf(
    "categoryId" to categoryId,
    "title" to title,
    "index" to index,
    "icon" to icon,
    "color" to color
)

fun NetworkGoal.toMap() : Map<Any?, *> = mapOf(
    "goalId" to goalId,
    "title" to title,
    "startDate" to startDate,
    "endDate" to endDate,
    "type" to type
)

fun Any?.safeToString(): String = this?.toString() ?: ""
fun Any?.safeToInt(): Int? = (this as? Number)?.toInt()
fun Any?.safeToLong(): Long? = (this as? Number)?.toLong()
fun Any?.safeToBoolean(): Boolean = (this as? Boolean) ?: false
fun Any?.safeToMapStringString(): Map<String, String>? {
    @Suppress("UNCHECKED_CAST")
    return this as? Map<String, String>
}