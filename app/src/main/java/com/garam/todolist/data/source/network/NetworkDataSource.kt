package com.garam.todolist.data.source.network

import com.garam.todolist.data.UserData

interface NetworkDataSource {

    suspend fun setUserData(userData : UserData)

    suspend fun saveTodo(todo: NetworkTodo)
    suspend fun getTodoList() : List<NetworkTodo>
    suspend fun deleteTodo(todoId : String)
    suspend fun updateTodo(todo: NetworkTodo)

    suspend fun saveGoal(goal: NetworkGoal)

    suspend fun saveCategory(category: NetworkCategory)
    suspend fun updateCategory(category: NetworkCategory)

    suspend fun deleteCategory(categoryId : String)
}