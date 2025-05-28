package com.garam.todolist.data.source.repository

import com.garam.todolist.data.Category
import com.garam.todolist.data.Goal
import com.garam.todolist.data.Todo
import com.garam.todolist.data.UserIdProvider
import com.garam.todolist.data.source.local.CategoryDao
import com.garam.todolist.data.source.local.GoalDao
import com.garam.todolist.data.source.local.TodoDao
import com.garam.todolist.data.source.network.NetworkDataSource
import com.garam.todolist.data.source.network.NetworkMonitor
import com.garam.todolist.data.toExternal
import com.garam.todolist.data.toLocal
import com.garam.todolist.data.toNetwork
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton
import javax.inject.Inject

@Singleton
class DefaultTodoRepository @Inject constructor(
    private val localTodoDataSource : TodoDao,
    private val localCategoryDataSource : CategoryDao,
    private val localGoalDataSource : GoalDao,

    private val userIdProvider: UserIdProvider,
    private val networkDataSource: NetworkDataSource,
    private val networkMonitor: NetworkMonitor
): TodoRepository {

    override suspend fun deleteTodo(todoId : String) {
        localTodoDataSource.deleteTodo(todoId, userIdProvider.currentUserId)
        networkDataSource.deleteTodo(todoId)
    }

    override suspend fun saveTodo(todo: Todo) {
        localTodoDataSource.saveTodo(todo.toLocal(userIdProvider.currentUserId))
        if (networkMonitor.isConnected()) networkDataSource.saveTodo(todo.toNetwork())
    }

    override suspend fun updateTodo(todo: Todo) {
        localTodoDataSource.upsertTodo(todo.toLocal(userIdProvider.currentUserId))
        if (networkMonitor.isConnected()) networkDataSource.updateTodo(todo.toNetwork())
    }

    override suspend fun getTodoByCategory(categoryId: String): Flow<List<Todo>> = localTodoDataSource.getTodoListByCategory(categoryId, userIdProvider.currentUserId).toExternal()

    override suspend fun getTodoByGoal(goalId: String): Flow<List<Todo>> = localTodoDataSource.getTodoByGoal(goalId, userIdProvider.currentUserId).toExternal()

    override suspend fun getTodoList() : Flow<List<Todo>> = localTodoDataSource.getAllTodoList(userIdProvider.currentUserId).toExternal()


    override suspend fun getCategoryList() : Flow<List<Category>> = localCategoryDataSource.getAllCategory(userIdProvider.currentUserId).toExternal()


    override suspend fun saveCategory(categroy: Category) {
        localCategoryDataSource.insertCategory(categroy.toLocal(userIdProvider.currentUserId))
        if (networkMonitor.isConnected()) networkDataSource.saveCategory(categroy.toNetwork())
    }

    override suspend fun updateCategory(category: Category) {
        localCategoryDataSource.upsertCategory(category.toLocal(userIdProvider.currentUserId))
        if (networkMonitor.isConnected()) networkDataSource.updateCategory(category.toNetwork())
    }

    override suspend fun deleteCategory(categoryId: String) {
        localCategoryDataSource.deleteCategory(categoryId)
        if (networkMonitor.isConnected()) networkDataSource.deleteCategory(categoryId)
    }


    override suspend fun getPlanList(): Flow<List<Todo>> = localTodoDataSource.getTodoList(userIdProvider.currentUserId).toExternal()

    override suspend fun saveGoal(goal: Goal) {
        localGoalDataSource.saveGoal(goal.toLocal(userIdProvider.currentUserId))
        networkDataSource.saveGoal(goal.toNetwork())

    }

    override suspend fun getGoal(
        startDate: String,
        endDate: String
    ): Goal? = localGoalDataSource.getGoal(startDate, endDate, userIdProvider.currentUserId)?.toExternal()


    override suspend fun getGoalList(): Flow<List<Goal>> = localGoalDataSource.getAllGoal().toExternal()

}