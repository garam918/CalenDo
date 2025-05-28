package com.garam.todolist.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Insert
    suspend fun saveTodo(todo : LocalTodo)

    @Query("SELECT * FROM todo WHERE userId = :uid") // 함수명 변경 해야함
    fun getAllTodoList(uid: String) : Flow<List<LocalTodo>>

    @Query("SELECT * FROM todo WHERE categoryId = :categoryId AND userId = :uid")
    fun getTodoListByCategory(categoryId : String, uid: String) : Flow<List<LocalTodo>>

    @Query("SELECT * FROM todo WHERE categoryId = :goalId AND userId = :uid")
    fun getTodoByGoal(goalId: String, uid: String) : Flow<List<LocalTodo>>

    @Query("DELETE FROM todo WHERE id = :id AND userId = :uid")
    suspend fun deleteTodo(id : String, uid: String)

    @Query("SELECT * FROM todo WHERE categoryId is null AND userId = :uid") // 함수명 변경 해야 함
    fun getTodoList(uid: String) : Flow<List<LocalTodo>>

    @Upsert
    suspend fun upsertTodo(todo : LocalTodo)

    @Query("SELECT * FROM todo WHERE startDate = :selectedDate AND userId = :uid")
    suspend fun getPlanList(selectedDate : String, uid: String) : List<LocalTodo>


    @Upsert
    suspend fun upsertTodoList(todoList : List<LocalTodo>)

    @Upsert
    suspend fun upsertCategoryList(categoryList : List<LocalCategory>)

    @Upsert
    suspend fun upsertGoalList(goalList : List<LocalGoal>)

}