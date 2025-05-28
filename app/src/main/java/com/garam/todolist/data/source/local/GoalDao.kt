package com.garam.todolist.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Upsert
    suspend fun saveGoal(goal: LocalGoal)

    @Query("SELECT * FROM goal")
    fun getAllGoal() : Flow<List<LocalGoal>>

    @Query("""
        SELECT * FROM goal
        WHERE userId = :uid AND (startDate = :startDate AND endDate = :endDate)
    """)
    fun getGoal(startDate : String, endDate : String, uid: String) : LocalGoal?

}