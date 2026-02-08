package com.garam.shared.data.source

import com.garam.shared.data.Category
import com.garam.shared.data.Goal
import com.garam.shared.data.GoalType
import com.garam.shared.data.Todo
import com.garam.shared.data.source.local.CategoryDao
import com.garam.shared.data.source.local.GoalDao
import com.garam.shared.data.source.local.LocalGoal
import com.garam.shared.data.source.local.LocalTodo
import com.garam.shared.data.source.local.TodoDao
import com.garam.shared.data.source.network.NetworkDataSource
import com.garam.shared.data.toExternal
import com.garam.shared.data.toLocal
import com.garam.shared.data.toNetwork
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class DefaultTodoRepository(
    private val todoDao : TodoDao,
    private val categoryDao: CategoryDao,
    private val goalDao: GoalDao,
    private val networkDataSource: NetworkDataSource

) : TodoRepository {

    override suspend fun getTodoList(uid: String): Flow<List<Todo>> = todoDao.getAllTodoList(uid).toExternal()

    override suspend fun getTodoByCategory(categoryId: String, uid: String): Flow<List<Todo>> = todoDao.getTodoListByCategory(
        categoryId = categoryId,
        uid = uid
    ).toExternal()

    override suspend fun getTodoByGoal(goalId: String, uid: String): List<Todo> = todoDao.getTodoByGoal(
        goalId = goalId,
        uid = uid
    ).toExternal()

    override suspend fun saveTodo(todo: Todo, uid: String) {
        todoDao.upsertTodo(todo.toLocal(uid))
        networkDataSource.updateTodo(todo.toNetwork(), uid)
    }

    override suspend fun deleteTodo(todoId: String, uid : String) {
        todoDao.deleteTodo(id = todoId, uid = uid)
        networkDataSource.deleteTodo(todoId)
    }

    override suspend fun updateTodo(todo: Todo) {
        TODO("Not yet implemented")
    }

    override suspend fun getPlanList(uid: String): Flow<List<Todo>> = todoDao.getPlanList(uid).toExternal()

    override suspend fun getCategoryList(uid: String): Flow<List<Category>> = categoryDao.getAllCategory(uid).toExternal()


    override suspend fun saveCategory(category: Category, uid: String) {
        categoryDao.insertCategory(category.toLocal(uid))
        networkDataSource.saveCategory(category.toNetwork(), uid)
    }

    override suspend fun updateCategory(category: Category) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCategory(categoryId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun saveGoal(goal: Goal, uid: String) {
        goalDao.saveGoal(goal.toLocal(uid))
        networkDataSource.saveGoal(goal.toNetwork(), uid)
    }

    override suspend fun getGoal(
        startDate: String,
        endDate: String,
        type : GoalType,
        uid: String
    ): Goal? = goalDao.getGoal(startDate, endDate, type ,uid).first()?.toExternal()

}