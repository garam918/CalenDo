package com.garam.todolist.data

import com.garam.todolist.data.source.local.LocalCategory
import com.garam.todolist.data.source.local.LocalGoal
import com.garam.todolist.data.source.local.LocalTodo
import com.garam.todolist.data.source.local.LocalUserData
import com.garam.todolist.data.source.network.FirebaseUserData
import com.garam.todolist.data.source.network.NetworkCategory
import com.garam.todolist.data.source.network.NetworkGoal
import com.garam.todolist.data.source.network.NetworkTodo
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date

fun Category.toLocal(uid: String) = LocalCategory(
    categoryId = categoryId,
    title = title,
    index = index,
    icon = icon,
    color = color,
    userId = uid
)

fun LocalCategory.toExternal() = Category(
    categoryId = categoryId,
    title = title,
    index = index,
    icon = icon,
    color = color
)

fun Category.toNetwork() = NetworkCategory(
    categoryId = categoryId,
    title = title,
    index = index,
    icon = icon.name,
    color = color
)

fun NetworkCategory.toLocal(uid: String) = LocalCategory(
    categoryId = categoryId,
    title = title,
    index = index,
    icon = icon.let { CategoryIconType.valueOf(it) },
    color = color,
    userId = uid
)

@JvmName("localToExternal")
fun List<LocalCategory>.toExternal() = map(LocalCategory::toExternal)

@JvmName("localToExternalFlow")
fun Flow<List<LocalCategory>>.toExternal(): Flow<List<Category>> = map { list -> list.toExternal() }


fun Todo.toLocal(uid: String) = LocalTodo(
    id = id,
    categoryId = categoryId,
    title = title,
    startDate = startDate,
    endDate = endDate,
    repeatRule = repeatRule,
    status = status,
    priority = priority,
    memo = memo,
    icon = icon,
    color = color,
    startTime = startTime,
    index = index,
    userId = uid,
    savedTime = savedTime.toDate().time
)

fun Todo.toNetwork() = NetworkTodo(
    id = id,
    categoryId = categoryId,
    title = title,
    startDate = startDate,
    endDate = endDate,
    repeatRule = repeatRule,
    status = status?.mapValues { it.value.name },
    priority = priority,
    memo = memo,
    icon = icon?.name,
    color = color,
    startTime = startTime,
    index = index,
    savedTime = savedTime
)


fun LocalTodo.toExternal() = Todo(
    id = id,
    categoryId = categoryId,
    title = title,
    startDate = startDate,
    endDate = endDate,
    repeatRule = repeatRule,
    status = status,
    priority = priority,
    memo = memo,
    icon = icon,
    color = color,
    startTime = startTime,
    index = index,
    savedTime = Timestamp(Date(savedTime))
)

fun NetworkTodo.toLocal() = Todo(
    id = id,
    categoryId = categoryId,
    title = title,
    startDate = startDate,
    endDate = endDate,
    repeatRule = repeatRule,
    status = status?.mapValues { TodoStatus.valueOf(it.value) }?.toMutableMap(),
    priority = priority,
    memo = memo,
    icon = icon?.let { CategoryIconType.valueOf(it) },
    color = color,
    startTime = startTime,
    index = index,
    savedTime = savedTime
)


@JvmName("todoToExternal")
fun List<LocalTodo>.toExternal() = map(LocalTodo::toExternal)

@JvmName("todoToExternalFlow")
fun Flow<List<LocalTodo>>.toExternal(): Flow<List<Todo>> = map { list -> list.toExternal() }



fun Goal.toLocal(uid: String) = LocalGoal(
    goalId = goalId,
    title = title,
    startDate = startDate,
    endDate = endDate,
    type = type,
    userId = uid
)

fun Goal.toNetwork() = NetworkGoal(
    goalId = goalId,
    title = title,
    startDate = startDate,
    endDate = endDate,
    type = type.name
)

fun LocalGoal.toExternal() = Goal(
    goalId = goalId,
    title = title,
    startDate = startDate,
    endDate = endDate,
    type = type
)

fun NetworkGoal.toLocal(uid: String) = LocalGoal(
    goalId = goalId,
    title = title,
    startDate = startDate,
    endDate = endDate,
    type = type.let { GoalType.valueOf(it) },
    userId = uid
)

@JvmName("goalToExternal")
fun List<LocalGoal>.toExternal() = map(LocalGoal::toExternal)

@JvmName("goalToExternalFlow")
fun Flow<List<LocalGoal>>.toExternal(): Flow<List<Goal>> = map { list -> list.toExternal() }

fun UserData.toLocal() = LocalUserData(
    uid = uid,
    email = email ?: "",
    loginType = loginType
)

fun LocalUserData.toExternal() = UserData(
    uid = uid,
    email = email ?: "",
    loginType = loginType
)

fun FirebaseUserData.toExternal() = UserData(
    uid = uid,
    email = email ?: "",
    loginType = loginType
)