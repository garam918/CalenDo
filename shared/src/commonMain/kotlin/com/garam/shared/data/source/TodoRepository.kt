package com.garam.shared.data.source

import com.garam.shared.data.Category
import com.garam.shared.data.Goal
import com.garam.shared.data.GoalType
import com.garam.shared.data.Todo
import kotlinx.coroutines.flow.Flow

interface TodoRepository {

    suspend fun getTodoList(uid: String) : Flow<List<Todo>>

    suspend fun getTodoByCategory(categoryId : String, uid: String) : Flow<List<Todo>>
    suspend fun getTodoByGoal(goalId : String, uid: String) : List<Todo>


    suspend fun saveTodo(todo: Todo, uid: String)
    suspend fun deleteTodo(todoId : String, uid: String)
    suspend fun updateTodo(todo: Todo)


    suspend fun getPlanList(uid: String) : Flow<List<Todo>>

    suspend fun getCategoryList(uid: String) : Flow<List<Category>>
    suspend fun saveCategory(category: Category, uid: String)
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategory(categoryId : String)

    suspend fun saveGoal(goal: Goal, uid: String)
    suspend fun getGoal(startDate : String, endDate : String, type: GoalType, uid: String) : Goal?

}