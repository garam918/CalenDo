package com.garam.todolist.data.source.repository

import com.garam.todolist.data.Category
import com.garam.todolist.data.Goal
import com.garam.todolist.data.Todo
import kotlinx.coroutines.flow.Flow

interface TodoRepository {

    suspend fun getTodoList() : Flow<List<Todo>>
    suspend fun getTodoByCategory(categoryId : String) : Flow<List<Todo>>
    suspend fun getTodoByGoal(goalId : String) : Flow<List<Todo>>


    suspend fun saveTodo(todo: Todo)
    suspend fun deleteTodo(todoId : String)
    suspend fun updateTodo(todo: Todo)


    suspend fun getPlanList() : Flow<List<Todo>>

    suspend fun getCategoryList() : Flow<List<Category>>
    suspend fun saveCategory(category: Category)
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategory(categoryId : String)

    suspend fun saveGoal(goal: Goal)
    suspend fun getGoal(startDate : String, endDate : String) : Goal?
    suspend fun getGoalList() : Flow<List<Goal>>
}