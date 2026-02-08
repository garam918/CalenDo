package com.garam.shared.data.source.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.garam.shared.data.GoalType
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Upsert
    suspend fun saveGoal(goal: LocalGoal)

    @Query("SELECT * FROM goal")
    fun getAllGoal() : Flow<List<LocalGoal>>

    @Query("""
        SELECT * FROM goal
        WHERE userId = :uid AND (startDate = :startDate AND endDate = :endDate AND type = :type)
    """)
    fun getGoal(startDate : String, endDate : String, type: GoalType , uid: String) : Flow<LocalGoal?>

}