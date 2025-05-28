package com.garam.todolist.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert
    suspend fun insertCategory(category: LocalCategory)

    @Query("SELECT * FROM category WHERE userId = :uid")
    fun getAllCategory(uid: String) : Flow<List<LocalCategory>>

    @Query("DELETE FROM category WHERE categoryId = :categoryId")
    suspend fun deleteCategory(categoryId : String)

    @Upsert
    suspend fun upsertCategory(category: LocalCategory)
}