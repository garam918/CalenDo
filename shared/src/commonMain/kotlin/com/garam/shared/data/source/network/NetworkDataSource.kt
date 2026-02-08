package com.garam.shared.data.source.network

import com.garam.shared.data.source.local.LocalCategory
import com.garam.shared.data.source.local.LocalGoal
import com.garam.shared.data.source.local.LocalTodo
import com.garam.shared.data.source.local.LocalUserData


interface NetworkDataSource {

    suspend fun setUserData(userData: LocalUserData)

    suspend fun saveTodo(todo: NetworkTodo, uid: String)
    suspend fun getTodoList(): List<NetworkTodo>
    suspend fun deleteTodo(todoId: String)
    suspend fun updateTodo(todo: NetworkTodo, uid: String)

    suspend fun saveGoal(goal: NetworkGoal, uid: String)

    suspend fun saveCategory(category: NetworkCategory, uid: String)
    suspend fun updateCategory(category: NetworkCategory)

    suspend fun deleteCategory(categoryId: String, uid: String)

    suspend fun saveTodoList(uid: String)
    suspend fun saveCategoryList(uid: String)
    suspend fun saveGoalList(uid: String)
}
